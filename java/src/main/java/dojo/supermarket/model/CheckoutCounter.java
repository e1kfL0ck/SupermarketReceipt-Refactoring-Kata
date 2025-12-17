package dojo.supermarket.model;

import java.util.List;

public class CheckoutCounter {
    private final List<Offer> offerCatalog;
    private final ShoppingCart cart;
    private Receipt receipt;

    public CheckoutCounter(List<Offer> offerCatalog, ShoppingCart cart) {
        this.offerCatalog = offerCatalog;
        this.cart = cart;
    }

    /*
    TODO: One counter should be able to handle multiple carts ?
    public CheckoutCounter(List<Offer> offerCatalog) {
        this.offerCatalog = offerCatalog;
    }

    public void addCart(ShoppingCart cart) {
        this.cart = cart;
    }
    */

    public Receipt checkout() {
        receipt = new Receipt();

        applyOffers();

        // 1) base receipt lines (snapshot)
        receipt.pay(cart.items());

        return receipt;
    }

    private void applyOffers() {
        for (Offer offer : offerCatalog) {
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

        int q = (int) item.getQuantity();
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
    private Discount computeOfferDiscount(Offer offer, ReceiptItem item, int q) {
        double unitPrice = item.getPrice();

        return switch (offer.getOfferType()) {
            case THREE_FOR_TWO -> discountThreeForTwo(item, q, unitPrice);
            case TEN_PERCENT_DISCOUNT -> discountPercent(item, offer.getDiscountAmount());
            case TWO_FOR_AMOUNT -> discountNForAmount(item, q, unitPrice, 2, offer.getDiscountAmount());
            case FIVE_FOR_AMOUNT -> discountNForAmount(item, q, unitPrice, 5, offer.getDiscountAmount());
            default -> null;
        };
    }

    private Discount discountThreeForTwo(ReceiptItem item, int q, double unitPrice) {
        if (q < 3) return null;
        int uses = q / 3;
        double normal = q * unitPrice;
        double promo = uses * 2 * unitPrice + (q % 3) * unitPrice;
        double amount = normal - promo;
        return amount > 0 ? new Discount(List.of(item.getProduct()), "3 for 2", amount) : null;
    }

    private Discount discountPercent(ReceiptItem item, double percent) {
        double amount = item.getQuantity() * item.getPrice() * percent / 100.0;
        return amount > 0 ? new Discount(List.of(item.getProduct()), percent + "% off", amount) : null;
    }

    private Discount discountNForAmount(ReceiptItem item, int q, double unitPrice, int n, double amountForN) {
        if (q < n) return null;
        int uses = q / n;
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
        return min == Double.POSITIVE_INFINITY ? 0 : (int) min;
    }

    public Receipt getReceipt() {
        return receipt;
    }
}
