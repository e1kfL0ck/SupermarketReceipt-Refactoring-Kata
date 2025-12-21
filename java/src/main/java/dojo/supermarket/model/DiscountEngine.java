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

    /**
     * Applies all applicable discounts to the shopping cart for the given customer,
     * updating the receipt with the calculated discounts.
     * @param cart
     * @param customer
     * @param receipt
     */
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

    /**
     * Initializes the remaining quantities map for products sold by EACH unit.
     */
    private void initRemainingEach() {
        for (ReceiptItem item : cart.items()) {
            if (item.getProduct().getUnit() == ProductUnit.EACH) {
                remaining.put(item.getProduct(), item.getQuantityAsInt());
            }
        }
    }

    /**
     * Applies a bundle offer to the cart, consuming the products in the bundle
     * and adding the corresponding discount to the receipt.
     * @param offer The bundle offer to apply.
     */
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

    /**
     * Checks if the cart contains all products in the given list.
     * @param products The list of products to check.
     * @return true if all products are in the cart, false otherwise.
     */
    private boolean containsAll(List<Product> products) {
        return products.stream().allMatch(cart::contains);
    }

    /**
     * Calculates how many times a bundle can be used based on the remaining quantities.
     * @param bundle The list of products in the bundle.
     * @return The number of times the bundle can be used.
     */
    private int calculateBundleUses(List<Product> bundle) {
        if (bundle.stream().anyMatch(p -> p.getUnit() != ProductUnit.EACH)) return 0;

        int uses = Integer.MAX_VALUE;
        for (Product p : bundle) {
            uses = Math.min(uses, remaining.getOrDefault(p, 0));
        }
        return uses;
    }

    /**
     * Applies the best available single offer (either an offer or a coupon) for a product,
     * consuming the appropriate quantities and updating the receipt with the discount.
     * @param product The product to apply the offer/coupon to.
     * @param customer The customer for coupon validation.
     */
    private void applySingleOfferBestOf(Product product, Customer customer) {
        LocalDate checkoutDate = LocalDate.now();
        Offer offer = offersMap.get(product);
        if (offer == null) return;

        while (true) {
            int remainingUnits = remaining.getOrDefault(product, 0);
            if (remainingUnits <= 0) return;

            // OFFER
            Discount offerDiscount = computeOfferDiscount(offer, product, remainingUnits);
            double offerAmount = offerDiscount == null ? 0.0 : offerDiscount.getDiscountAmount();
            int offerConsumes = offerConsumesUnits(offer, remainingUnits);

            // if the offer consumes nothing (e.g., remaining < threshold), it is not applicable
            if (offerConsumes <= 0) {
                offerAmount = 0.0;
                offerDiscount = null;
            }

            // COUPON
            Coupon coupon = customer.getCouponValidity(product, checkoutDate);
            Discount couponDiscount = (coupon == null) ? null : computeCouponDiscountOnce(coupon, product, remainingUnits);
            double couponAmount = couponDiscount == null ? 0.0 : couponDiscount.getDiscountAmount();
            int couponConsumes = (coupon == null) ? 0 : couponConsumesUnitsOnce(remainingUnits, coupon);

            if (couponConsumes <= 0) {
                couponAmount = 0.0;
                couponDiscount = null;
            }

            if (offerAmount <= 0.0 && couponAmount <= 0.0) return;

            if (couponAmount > offerAmount) {
                receipt.addDiscount(couponDiscount);
                coupon.markUsed();
                consume(remaining, product, couponConsumes);
            } else {
                receipt.addDiscount(offerDiscount);
                consume(remaining, product, offerConsumes);
            }
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

    private int offerConsumesUnits(Offer offer, int remainingUnits) {
        if (offer == null || remainingUnits <= 0) return 0;

        return switch (offer.getOfferType()) {
            case THREE_FOR_TWO -> (remainingUnits / 3) * 3;
            case TWO_FOR_AMOUNT -> (remainingUnits / 2) * 2;
            case FIVE_FOR_AMOUNT -> (remainingUnits / 5) * 5;
            case TEN_PERCENT_DISCOUNT -> remainingUnits;

            default -> 0;
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

    /**
     * Computes the discount provided by a coupon for a product, applied only once.
     * @param coupon The coupon to apply.
     * @param product The product to which the coupon applies.
     * @param remainingUnits The number of remaining units of the product.
     * @return The computed discount, or null if the coupon is not applicable.
     */
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

