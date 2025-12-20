package dojo.supermarket.model;

import java.time.LocalDate;
import java.util.*;

/*
    The DiscountEngine applies special offers and coupons to a shopping cart,
    calculating discounts and updating the receipt accordingly.
*/
public class DiscountEngine {
    private final List<Offer> bundleOfferCatalog = new ArrayList<>();
    private final List<Offer> kiloOfferCatalog = new ArrayList<>();
    private final Map<Product, Offer> offersMap;

    private ShoppingCart cart;
    private Receipt receipt;
    private Map<Product, Integer> remaining = new HashMap<>();

    public DiscountEngine(Map<Product, Offer> offersMap) {
        this.offersMap = offersMap;
        this.offersMap.values().stream()
                .filter(o -> o.getOfferType() == SpecialOfferType.BUNDLE)
                .forEach(bundleOfferCatalog::add);

        this.offersMap.values().stream()
                .filter(o -> o.getFirstProduct().getUnit() == ProductUnit.KILO)
                .forEach(kiloOfferCatalog::add);
    }

    public void applyAll(ShoppingCart cart, Customer customer, Receipt receipt) {
        this.cart = cart;
        this.receipt = receipt;
        this.initRemainingEach();

        for (Offer offer : bundleOfferCatalog) {
            applyBundleOfferConsuming(offer);
        }

        for (Product p : new ArrayList<>(remaining.keySet())) {
            applySingleOfferBestOf(p, customer);
        }

        for (Offer offer : kiloOfferCatalog) {
            applyKiloOffer(offer);
        }
    }

    private void initRemainingEach() {
        for (ReceiptItem item : cart.items()) {
            if (item.getProduct().getUnit() == ProductUnit.EACH) {
                remaining.put(item.getProduct(), item.getQuantityAsInt());
            }
        }
    }

    private void applyBundleOfferConsuming(Offer offer) {
        List<Product> bundle = offer.getProducts();

        if (!containsAll(bundle)) return;

        int uses = calculateBundleUses(bundle);
        if (uses <= 0) return;

        double bundleUnitTotal = bundle.stream().mapToDouble(Product::getPrice).sum();
        double amount = bundleUnitTotal * offer.getDiscountAmount() / 100.0 * uses;
        if (amount <= 0) return;

        receipt.addDiscount(new Discount(bundle, offer.getDiscountAmount() + "% off bundle", amount));

        for (Product p : bundle) {
            Integer left = remaining.get(p) - uses;
            if (left == 0) remaining.remove(p);
            else if (left < 0) throw new IllegalStateException("Negative remaining quantity for product " + p.getName());
            else remaining.put(p, left);
        }
    }

    private boolean containsAll(List<Product> products) {
        return products.stream().allMatch(cart::contains);
    }

    private int calculateBundleUses(List<Product> bundle) {
        if (bundle.stream().anyMatch(p -> p.getUnit() != ProductUnit.EACH)) return 0;

        int uses = Integer.MAX_VALUE;
        for (Product p : bundle) {
            uses = Math.min(uses, remaining.getOrDefault(p, 0));
        }
        return uses;
    }

    // TODO: this method needs refactoring to reduce its complexity
    private void applySingleOfferBestOf(Product product,
                                        Customer customer) {

        LocalDate checkoutDate = java.time.LocalDate.now();

        Offer offer = offersMap.get(product);
        if (offer == null) return;
        Integer remainingUnits = remaining.getOrDefault(product, 0);
        if (remainingUnits <= 0) return;

        //TODO: séparer en offerCandidate et couponCandidate ?
        // Offer discount sur remainingUnits restant
        Discount offerDiscount = computeOfferDiscount(offer, product, remainingUnits);
        double offerAmount = offerDiscount == null ? 0.0 : offerDiscount.getDiscountAmount();
        Integer offerConsumes = remainingUnits;

        // Coupon discount sur remainingUnits restant
        Coupon coupon = customer.getCouponValidity(product, checkoutDate);
        Discount couponDiscount = computeCouponDiscountOnce(coupon, product, remainingUnits);
        double couponAmount = couponDiscount == null ? 0.0 : couponDiscount.getDiscountAmount();
        Integer couponConsumes = couponConsumesUnitsOnce(remainingUnits, coupon); // 0 si non applicable

        // Best-of
        if (couponAmount > offerAmount) {
            receipt.addDiscount(couponDiscount);
            coupon.markUsed();
            consume(remaining, product, couponConsumes);
        } else if (offerAmount > 0) {
            receipt.addDiscount(offerDiscount);
            consume(remaining, product, offerConsumes);
        }
    }

    private Discount computeOfferDiscount(Offer offer, Product product, Integer remainingUnits) {
         return switch (offer.getOfferType()) {
            case THREE_FOR_TWO -> discountThreeForTwo(product, remainingUnits);
            case TEN_PERCENT_DISCOUNT -> discountPercent(product, remainingUnits, offer.getDiscountAmount());
            case TWO_FOR_AMOUNT -> discountNForAmount(product, remainingUnits, 2, offer.getDiscountAmount());
            case FIVE_FOR_AMOUNT -> discountNForAmount(product, remainingUnits,5, offer.getDiscountAmount());
            default -> null;
        };
    }

    private Discount discountThreeForTwo(Product product, int remainingUnits) {
        if (remainingUnits < 3) return null;
        double unitPrice = product.getPrice();
        double uses = remainingUnits / 3;
        double normal = remainingUnits * unitPrice;
        double promo = uses * 2 * unitPrice + (remainingUnits % 3) * unitPrice;
        double amount = normal - promo;
        return amount > 0 ? new Discount(List.of(product), "3 for 2", amount) : null;
    }

    private Discount discountPercent(Product product, int remainingUnits, double percent) {
        double amount = remainingUnits * product.getPrice() * percent / 100.0;
        return amount > 0 ? new Discount(List.of(product), percent + "% off", amount) : null;
    }

    private Discount discountNForAmount(Product product, int remainingUnits, int n, double amountForN) {
        if (remainingUnits < n) return null;
        double unitPrice = product.getPrice();
        double uses = remainingUnits / n;
        double normal = uses * n * unitPrice;
        double amount = normal - uses * amountForN;
        return amount > 0 ? new Discount(List.of(product), n + " for " + amountForN, amount) : null;
    }

    private void consume(Map<Product, Integer> remaining, Product p, Integer used) {
        if (used <= 0) return;
        Integer left = remaining.getOrDefault(p, 0) - used;
        if (left <= 0) remaining.remove(p); else remaining.put(p, left);
    }

    private Discount computeCouponDiscountOnce(Coupon coupon, Product product, Integer remainingUnits) {
        if (coupon == null) return null;
        int triggerQty = coupon.getTriggerQuantity();
        int discountedQty = coupon.getDiscountedQuantity();
        double discountRate = coupon.getDiscountRate();

        if (remainingUnits < triggerQty) return null;

        int applicableDiscountedQty = Math.min(discountedQty, remainingUnits - triggerQty);
        if (applicableDiscountedQty <= 0) return null;

        double amount = applicableDiscountedQty * product.getPrice() * discountRate;
        return amount > 0 ? new Discount(List.of(product), "Coupon " + (discountRate * 100) + "% off", amount) : null;
    }

    private Integer couponConsumesUnitsOnce(Integer remainingUnits, Coupon coupon) {
        if (coupon == null) return 0;
        int trigger = coupon.getTriggerQuantity();
        int discounted = coupon.getDiscountedQuantity();
        return ((remainingUnits >= trigger + discounted) ? (trigger + discounted) : 0);
    }

    private void applyKiloOffer(Offer offer) {
        Product product = offer.getFirstProduct();
        ReceiptItem item = cart.get(product);
        if (item == null) return;
        double amount = item.getQuantity() * item.getPrice() * offer.getDiscountAmount() / 100.0;
        Discount discount = amount > 0 ? new Discount(List.of(item.getProduct()), offer.getDiscountAmount() + "% off", amount) : null;
        if (discount != null) receipt.addDiscount(discount);
    }

}

