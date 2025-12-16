package dojo.supermarket.model;

import java.util.*;

public class ShoppingCart {

    private List<Offer> offerCatalog;
    private Receipt receipt = new Receipt();
    private Map<Product, ReceiptItem> receiptItems = new LinkedHashMap<>();

    ShoppingCart(List<Offer> offerCatalog) {
        this.offerCatalog = offerCatalog;
    }

    void addItemInCart(Product product, double quantity) {
        receiptItems.merge(
                product,
                new ReceiptItem(product, quantity),
                (existing, added) -> new ReceiptItem(product, existing.getQuantity() + added.getQuantity())
        );
    }


    void goToCheckout() {
        //TODO: where should the reciptItems should be ?
        receipt.pay(receiptItems.values());
    }

    void handleAllOffers() {
        for (Offer offer : offerCatalog) {
            if (offer.getOfferType() != SpecialOfferType.BUNDLE) {
                handleSingleOffers(offer);
            } else {
                handleBundles(offer);
            }
        }
    }

    //TODO: ca ne va pas gerer si un produit est ajouté en 2 fois pour le moment
    void handleSingleOffers(Offer offer) {
        Product product = offer.getProducts().get(0);
        ReceiptItem item = receiptItems.get(product);
        if (item == null) return;

        double unitPrice = item.getPrice();
        double quantity = item.getQuantity();
        int quantityAsInt = (int) quantity; // attention : perte de la partie décimale
        Discount discount = null;

        switch (offer.getOfferType()) {
            case THREE_FOR_TWO:
                if (quantityAsInt >= 3) {
                    int uses = quantityAsInt / 3;
                    double normalPrice = quantityAsInt * unitPrice;
                    double promoPrice = uses * 2 * unitPrice + (quantityAsInt % 3) * unitPrice;
                    double discountAmount = normalPrice - promoPrice;
                    discount = new Discount(product, "3 for 2", discountAmount);
                }
                break;
            case TEN_PERCENT_DISCOUNT:
            {
                double discountAmount = quantity * unitPrice * offer.getDiscountAmount() / 100.0;
                discount = new Discount(product, offer.getDiscountAmount() + "% off", discountAmount);
            }
            break;
            case TWO_FOR_AMOUNT:
                if (quantityAsInt >= 2) {
                    int uses = quantityAsInt / 2;
                    double normalPrice = uses * 2 * unitPrice;
                    double discountAmount = normalPrice - uses * offer.getDiscountAmount();
                    discount = new Discount(product, "2 for " + offer.getDiscountAmount(), discountAmount);
                }
                break;
            case FIVE_FOR_AMOUNT:
                if (quantityAsInt >= 5) {
                    int uses = quantityAsInt / 5;
                    double normalPrice = uses * 5 * unitPrice;
                    double discountAmount = normalPrice - uses * offer.getDiscountAmount();
                    discount = new Discount(product, "5 for " + offer.getDiscountAmount(), discountAmount);
                }
                break;
            default:
                break;
        }

        if (discount != null) {
            receipt.addDiscount(discount);
        }
    }


    void handleBundles(Offer offer) {

        if (findProducts(offer.getProducts())) {

            int numberOfPromotionUsage = minQuantity(offer.getProducts());
            double totalPriceBundle = calculateTotalPriceBundle(offer.getProducts());
            double discountAmount = totalPriceBundle*offer.getDiscountAmount()/100*numberOfPromotionUsage;
            Discount discount = new Discount(offer.getProducts(), "10% off bundle", discountAmount);
            receipt.addDiscount(discount);
        }
    }

    boolean findProducts(List<Product> bundleProducts) {
        for (Product p : bundleProducts) {
            if (!receiptItems.containsKey(p)) {
                return false;
            }
        }
        return true;
    }

    int minQuantity(List<Product> products) {
        double min = Double.POSITIVE_INFINITY;

        for (Product p : products) {
            ReceiptItem item = receiptItems.get(p);
            if (item != null) {
                double q = item.getQuantity();
                if (q < min) {
                    min = q;
                }
            }
        }

        if (min == Double.POSITIVE_INFINITY) {
            return 0; // None of the products found
        }

        return (int) min;
    }

    double calculateTotalPriceBundle(List<Product> bundleProducts) {
        double bundleTotalPrice = 0;
        for (Product p : bundleProducts) {
            bundleTotalPrice += p.getPrice();
        }
        return bundleTotalPrice;
    }

    public Receipt getReceipt() {
        return receipt;
    }

    public Map<Product, ReceiptItem> getReceiptItems() {
        return receiptItems;
    }

    public List<Offer> getOfferCatalog() {
        return offerCatalog;
    }
}

