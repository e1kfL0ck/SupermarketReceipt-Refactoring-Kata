package dojo.supermarket.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckoutCounter {
    private final List<Offer> bundleOfferCatalog;
    private final List<Offer> kiloOfferCatalog;
    private final Map<Product, Offer> offersMap;
    private final ShoppingCart cart;
    private Receipt receipt;

    public CheckoutCounter(Map<Product, Offer> offersMap, ShoppingCart cart) {
        this.offersMap = offersMap != null ? offersMap : new java.util.HashMap<>();
        this.cart = cart;
        this.bundleOfferCatalog = new ArrayList<>();
        this.offersMap.values().stream()
                .filter(o -> o.getOfferType() == SpecialOfferType.BUNDLE)
                .forEach(this.bundleOfferCatalog::add);

        this.kiloOfferCatalog = new ArrayList<>();
        this.offersMap.values().stream()
                .filter(o -> o.getFirstProduct().getUnit() == ProductUnit.KILO)
                .forEach(this.kiloOfferCatalog::add);

    }

    public Receipt checkout(Customer customer) {
        this.receipt = new Receipt(cart.items());                // IMPORTANT: receipt exist before applying offers and coupons (so qty is the same)
        Map<Product, Integer> remaining = initRemainingEach();

        // Apply bundle first
        for (Offer offer : bundleOfferCatalog) {
            if (offer.getOfferType() == SpecialOfferType.BUNDLE) {
                applyBundleOfferConsuming(offer, remaining);
            }
        }

        //TODO: Améliorer la méthode afin de limiter les entrées
        for(Product product: new ArrayList<>(remaining.keySet())) { //New array lsit to avoid concurrent modification exception
            applySingleOfferBestOf(offersMap, product, remaining, customer);
        }

        //Finally apply kilo offers
        for(Offer offer: kiloOfferCatalog) {
            applyKiloOffer(offer);
        }

        receipt.pay();
        return receipt;
    }

    private void applyKiloOffer(Offer offer) {
        Product product = offer.getFirstProduct();
        ReceiptItem item = cart.get(product);
        if (item == null) return;
        Discount discount = discountPercent(item, offer.getDiscountAmount());
        if (discount != null) receipt.addDiscount(discount);
    }

    private Discount computeOfferDiscount(Offer offer, ReceiptItem item, Integer remainingUnits) {
        double unitPrice = item.getPrice();

        return switch (offer.getOfferType()) {
            case THREE_FOR_TWO -> discountThreeForTwo(item, remainingUnits, unitPrice);
            case TEN_PERCENT_DISCOUNT -> discountPercent(item, offer.getDiscountAmount());
            case TWO_FOR_AMOUNT -> discountNForAmount(item, remainingUnits, unitPrice, 2, offer.getDiscountAmount());
            case FIVE_FOR_AMOUNT -> discountNForAmount(item, remainingUnits, unitPrice, 5, offer.getDiscountAmount());
            default -> null;
        };
    }

    private Discount discountThreeForTwo(ReceiptItem item, int remainingUnits, double unitPrice) {
        if (remainingUnits < 3) return null;
        double uses = remainingUnits / 3;
        double normal = remainingUnits * unitPrice;
        double promo = uses * 2 * unitPrice + (remainingUnits % 3) * unitPrice;
        double amount = normal - promo;
        return amount > 0 ? new Discount(List.of(item.getProduct()), "3 for 2", amount) : null;
    }

    private Discount discountPercent(ReceiptItem item, double percent) {
        double amount = item.getQuantity() * item.getPrice() * percent / 100.0;
        return amount > 0 ? new Discount(List.of(item.getProduct()), percent + "% off", amount) : null;
    }

    private Discount discountNForAmount(ReceiptItem item, int remainingUnits, double unitPrice, int n, double amountForN) {
        if (remainingUnits < n) return null;
        double uses = remainingUnits / n;
        double normal = uses * n * unitPrice;
        double amount = normal - uses * amountForN;
        return amount > 0 ? new Discount(List.of(item.getProduct()), n + " for " + amountForN, amount) : null;
    }


    private boolean containsAll(List<Product> products) {
        for (Product p : products) {
            if (!cart.contains(p)) return false;
        }
        return true;
    }

    private Map<Product, Integer> initRemainingEach() {
        Map<Product, Integer> remaining = new java.util.HashMap<>();
        for (ReceiptItem item : cart.items()) {
            if (item.getProduct().getUnit() == ProductUnit.EACH) {
                remaining.put(item.getProduct(), item.getQuantityAsInt());
            }
        }
        return remaining;
    }

    private void applyBundleOfferConsuming(Offer offer, Map<Product, Integer> remaining) {
        List<Product> bundle = offer.getProducts();

        if (!containsAll(bundle)) return;

        int uses = calculateBundleUses(bundle, remaining);
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

    private int calculateBundleUses(List<Product> bundle, Map<Product, Integer> remaining) {
        for (Product p : bundle) {
            if (p.getUnit() != ProductUnit.EACH) return 0;
        }
        int uses = Integer.MAX_VALUE;
        for (Product p : bundle) {
            uses = Math.min(uses, remaining.getOrDefault(p, 0));
        }
        return uses;
    }

    // TODO: ne pas confondre remainingUnits avec item.getQuantity()
    //  Le premier est un int (unités entières restantes à traiter)
    //  Le second est un double (quantité totale dans le panier)
    // TODO: this method needs refactoring to reduce its complexity
    private void applySingleOfferBestOf(Map<Product, Offer> offersMap,
                                        Product product,
                                        Map<Product, Integer> remaining,
                                        Customer customer) {

        LocalDate checkoutDate = java.time.LocalDate.now();

        Offer offer = offersMap.get(product);
        if (offer == null) return;
        Integer remainingUnits = remaining.getOrDefault(product, 0);
        if (remainingUnits <= 0) return;

        ReceiptItem item = cart.get(product);
        if (item == null) return;

        // Offer discount sur remainingUnits restant
        Discount offerDiscount = computeOfferDiscount(offer, item, remainingUnits);
        double offerAmount = offerDiscount == null ? 0.0 : offerDiscount.getDiscountAmount();
        Integer offerConsumes = remainingUnits;

        // Coupon discount sur remainingUnits restant (valide une seule fois)
        Coupon coupon = customer.getCouponValidity(product, checkoutDate);
        Discount couponDiscount = computeCouponDiscountOnce(coupon, item, remainingUnits);
        double couponAmount = couponDiscount == null ? 0.0 : couponDiscount.getDiscountAmount();
        Integer couponConsumes = couponConsumesUnitsOnce(remainingUnits, coupon); // 0 si non applicable

        // Best-of
        //TODO: move to another method
        if (couponAmount > offerAmount) {
            receipt.addDiscount(couponDiscount);
            coupon.markUsed();
            consume(remaining, product, couponConsumes);
        } else if (offerAmount > 0) {
            receipt.addDiscount(offerDiscount);
            consume(remaining, product, offerConsumes);
        }
    }

    private void consume(Map<Product, Integer> remaining, Product p, Integer used) {
        if (used <= 0) return;
        Integer left = remaining.getOrDefault(p, 0) - used;
        if (left <= 0) remaining.remove(p); else remaining.put(p, left);
    }

    private Discount computeCouponDiscountOnce(Coupon coupon, ReceiptItem item, Integer remainingUnits) {
        if (coupon == null) return null;
        int triggerQty = coupon.getTriggerQuantity();
        int discountedQty = coupon.getDiscountedQuantity();
        double discountRate = coupon.getDiscountRate();

        if (remainingUnits < triggerQty) return null;

        int applicableDiscountedQty = Math.min(discountedQty, remainingUnits - triggerQty);
        if (applicableDiscountedQty <= 0) return null;

        double amount = applicableDiscountedQty * item.getPrice() * discountRate;
        return amount > 0 ? new Discount(List.of(item.getProduct()), "Coupon " + (discountRate * 100) + "% off", amount) : null;
    }

    private Integer couponConsumesUnitsOnce(Integer remainingUnits, Coupon coupon) {
        if (coupon == null) return 0;
        int trigger = coupon.getTriggerQuantity();
        int discounted = coupon.getDiscountedQuantity();
        return ((remainingUnits >= trigger + discounted) ? (trigger + discounted) : 0);
    }

    public Receipt getReceipt() {
        return receipt;
    }
}
