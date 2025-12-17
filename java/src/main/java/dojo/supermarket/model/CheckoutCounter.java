package dojo.supermarket.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckoutCounter {
    private final List<Offer> bundleOfferCatalog;
    private final Map<Product, Offer> offersMap;
    private final ShoppingCart cart;
    private Receipt receipt;

    public CheckoutCounter(List<Offer> bundleOfferCatalog, ShoppingCart cart) {
        this.bundleOfferCatalog = bundleOfferCatalog;
        this.cart = cart;
        this.offersMap = null;
    }

    public CheckoutCounter(Map<Product, Offer> offersMap, ShoppingCart cart) {
        this.offersMap = offersMap;
        this.cart = cart;
        this.bundleOfferCatalog = new ArrayList<>();

        offersMap.values().stream()
                .filter(o -> o.getOfferType() == SpecialOfferType.BUNDLE)
                .forEach(this.bundleOfferCatalog::add);

    }

    public Receipt checkout() {
        receipt = new Receipt(cart.items());
        this.applyOffers();
        receipt.pay();
        return receipt;
    }

    //TODO: the return receipt is not used, still usefull ?
    public Receipt checkout(Customer customer) {
        this.receipt = new Receipt(cart.items());                // IMPORTANT: receipt exist before applying offers and coupons (so qty is the same)
        Map<Product, Double> remaining = initRemainingEach();

        // Apply bundle first
        for (Offer offer : bundleOfferCatalog) {
            if (offer.getOfferType() == SpecialOfferType.BUNDLE) {
                applyBundleOfferConsuming(offer, remaining);
            }
        }

        //TODO: Unify use of OffersMap and OfferCatalog
        //TODO: Améliorer la méthode afin de limiter les entrées
        for(Product product: remaining.keySet()) {
            applySingleOfferBestOf(offersMap, product, remaining, customer);
        }

        receipt.pay();
        return receipt;
    }

    private void applyOffers() {
        for (Offer offer : bundleOfferCatalog) {
            if (offer.getOfferType() == SpecialOfferType.BUNDLE) {
                applyBundleOffer(offer);
            } else {
                applySingleOffer(offer);
            }
        }
    }

    private void applySingleOffer(Offer offer) {
        Product product = offer.getFirstProduct();
        ReceiptItem item = cart.get(product);
        if (item == null) return;

        Double q = item.getQuantity();
        double unitPrice = item.getPrice();

        Discount discount = switch (offer.getOfferType()) {
            case THREE_FOR_TWO -> discountThreeForTwo(item, q, unitPrice);
            case TEN_PERCENT_DISCOUNT -> discountPercent(item, offer.getDiscountAmount());
            case TWO_FOR_AMOUNT -> discountNForAmount(item, q, unitPrice, 2, offer.getDiscountAmount());
            case FIVE_FOR_AMOUNT -> discountNForAmount(item, q, unitPrice, 5, offer.getDiscountAmount());
            default -> null;
        };

        if (discount != null) receipt.addDiscount(discount);
    }

    //TODO: new method that will replace the one above
    private Discount computeOfferDiscount(Offer offer, ReceiptItem item, Double q) {
        double unitPrice = item.getPrice();

        return switch (offer.getOfferType()) {
            case THREE_FOR_TWO -> discountThreeForTwo(item, q, unitPrice);
            //TODO: TEN_PERCENT_DISCOUNT is the only one to work for weight products for now
            case TEN_PERCENT_DISCOUNT -> discountPercent(item, offer.getDiscountAmount());
            case TWO_FOR_AMOUNT -> discountNForAmount(item, q, unitPrice, 2, offer.getDiscountAmount());
            case FIVE_FOR_AMOUNT -> discountNForAmount(item, q, unitPrice, 5, offer.getDiscountAmount());
            default -> null;
        };
    }

    private Discount discountThreeForTwo(ReceiptItem item, Double q, double unitPrice) {
        if (q < 3) return null;
        double uses = q / 3;
        double normal = q * unitPrice;
        double promo = uses * 2 * unitPrice + (q % 3) * unitPrice;
        double amount = normal - promo;
        //condition ? valeurSiVrai : valeurSiFaux
        return amount > 0 ? new Discount(List.of(item.getProduct()), "3 for 2", amount) : null;
    }

    private Discount discountPercent(ReceiptItem item, double percent) {
        double amount = item.getQuantity() * item.getPrice() * percent / 100.0;
        return amount > 0 ? new Discount(List.of(item.getProduct()), percent + "% off", amount) : null;
    }

    private Discount discountNForAmount(ReceiptItem item, Double q, double unitPrice, int n, double amountForN) {
        if (q < n) return null;
        double uses = q / n;
        double normal = uses * n * unitPrice;
        double amount = normal - uses * amountForN;
        return amount > 0 ? new Discount(List.of(item.getProduct()), n + " for " + amountForN, amount) : null;
    }

    private void applyBundleOffer(Offer offer) {
        List<Product> bundle = offer.getProducts();

        if (!containsAll(bundle)) return;

        int uses = minQuantity(bundle);
        if (uses <= 0) return;

        double bundleUnitTotal = bundle.stream().mapToDouble(Product::getPrice).sum();
        double amount = bundleUnitTotal * offer.getDiscountAmount() / 100.0 * uses;

        if (amount > 0) {
            receipt.addDiscount(new Discount(bundle, offer.getDiscountAmount() + "% off bundle", amount));
        }
    }

    private boolean containsAll(List<Product> products) {
        for (Product p : products) {
            if (!cart.contains(p)) return false;
        }
        return true;
    }

    private int minQuantity(List<Product> products) {
        int min = Integer.MAX_VALUE;
        for (Product p : products) {
            ReceiptItem item = cart.get(p);
            if (item == null) return 0;
            min = (int) Math.min(min, item.getQuantity());
        }
        return min == Integer.MAX_VALUE ? 0 : min;
    }

    //TODO: ne fonctionne pas avec les produits au poids
    //TODO: vérifier quelles promos peuvent s'appliquer au poids
    private Map<Product, Double> initRemainingEach() {
        Map<Product, Double> remaining = new java.util.HashMap<>();
        for (ReceiptItem item : cart.items()) {
            remaining.put(item.getProduct(), item.getQuantity());
        }
        return remaining;
    }

    private void applyBundleOfferConsuming(Offer offer, Map<Product, Double> remaining) {
        List<Product> bundle = offer.getProducts();

        if (!containsAll(bundle)) return;

        int uses = Integer.MAX_VALUE;
        for (Product p : bundle) {
            uses = (int) Math.min(uses, remaining.get(p));
        }
        if (uses <= 0) return;

        double bundleUnitTotal = bundle.stream().mapToDouble(Product::getPrice).sum();
        /*
        Same as :
        double bundleUnitTotal = 0.0;
        for (Product p : bundle) {
            bundleUnitTotal += p.getPrice();
        }
         */
        double amount = bundleUnitTotal * offer.getDiscountAmount() / 100.0 * uses;
        if (amount <= 0) return;

        receipt.addDiscount(new Discount(bundle, offer.getDiscountAmount() + "% off bundle", amount));

        // consommation
        for (Product p : bundle) {
            double left = remaining.get(p) - uses;
            if (left == 0) remaining.remove(p);
            //TODO: check if this case can happen
            else if (left < 0) throw new IllegalStateException("Negative remaining quantity for product " + p.getName());
            else remaining.put(p, left);
        }
    }

    ///TODO: ne pas confondre q avec item.getQuantity()
    ///  Le premier est un int (unités entières restantes à traiter)
    ///  Le second est un double (quantité totale dans le panier)
    private void applySingleOfferBestOf(Map<Product, Offer> offersMap,
                                        Product product,
                                        Map<Product, Double> remaining,
                                        Customer customer) {

        LocalDate checkoutDate = java.time.LocalDate.now();

        Offer offer = offersMap.get(product);
        if (offer == null) return;
        Double q = remaining.getOrDefault(product, 0.0);
        if (q <= 0) return;

        ReceiptItem item = cart.get(product);
        if (item == null) return;

        // Offer discount sur q restant
        Discount offerDiscount = computeOfferDiscount(offer, item, q);
        double offerAmount = offerDiscount == null ? 0.0 : offerDiscount.getDiscountAmount();
        Double offerConsumes = q;

        // Coupon discount sur q restant (valide une seule fois)
        Coupon coupon = customer.getCouponValidity(product, checkoutDate);
        Discount couponDiscount = computeCouponDiscountOnce(coupon, item, q);
        double couponAmount = couponDiscount == null ? 0.0 : couponDiscount.getDiscountAmount();
        Double couponConsumes = couponConsumesUnitsOnce(q, coupon); // 0 si non applicable

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

    private void consume(Map<Product, Double> remaining, Product p, Double used) {
        if (used <= 0) return;
        double left = remaining.getOrDefault(p, 0.0) - used;
        if (left <= 0) remaining.remove(p); else remaining.put(p, left);
    }

    private Discount computeCouponDiscountOnce(Coupon coupon, ReceiptItem item, Double q) {
        if (coupon == null) return null;
        int triggerQty = coupon.getTriggerQuantity();
        int discountedQty = coupon.getDiscountedQuantity();
        double discountRate = coupon.getDiscountRate();

        if (q < triggerQty) return null;

        int applicableDiscountedQty = (int) Math.min(discountedQty, q - triggerQty);
        if (applicableDiscountedQty <= 0) return null;

        double amount = applicableDiscountedQty * item.getPrice() * discountRate;
        return amount > 0 ? new Discount(List.of(item.getProduct()), "Coupon " + (discountRate * 100) + "% off", amount) : null;
    }

    private Double couponConsumesUnitsOnce(Double qRemaining, Coupon coupon) {
        if (coupon == null) return 0.0;
        int trigger = coupon.getTriggerQuantity();
        int discounted = coupon.getDiscountedQuantity();
        return (double) ((qRemaining >= trigger + discounted) ? (trigger + discounted) : 0);
    }

    public Receipt getReceipt() {
        return receipt;
    }
}
