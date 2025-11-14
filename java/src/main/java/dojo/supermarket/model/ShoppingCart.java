package dojo.supermarket.model;

import java.util.*;

public class ShoppingCart {

    private List<DefaultOffer> offerCatalog;
    private Receipt receipt = new Receipt();
    private List<ReceiptItem> receiptItems = new ArrayList<>();

    ShoppingCart(List<DefaultOffer> offerCatalog) {
        this.offerCatalog = offerCatalog;
    }

    void addItemInCart(Product product, double quantity) {
        receiptItems.add(new ReceiptItem(product, quantity));
    }

    void goToCheckout() {
        //TODO: where should the reciptItems should be ?
        receipt.pay(receiptItems);
        //receipt.show(receiptItems);
    }

    void handleAllOffers() {
        for (DefaultOffer offer : offerCatalog) {
            if (offer.getOfferType() != SpecialOfferType.BUNDLE) {
                handleSingleOffers(offer);
            } else {
                handleBundles(offer);
            }
        }
    }

    //TODO: ca ne va pas gerer si un produit est ajouté en 2 fois pour le moment
    void handleSingleOffers(DefaultOffer offer) {
        ReceiptItem item = findByProduct(offer.getProducts().get(0));
        if(item != null) {
            Discount discount = null;
            int quantityAsInt = (int) item.getQuantity();
            double discountAmount = 0;
            int numberOfPromotionUsage = 0;

            switch (offer.getOfferType()) {
                case THREE_FOR_TWO:
                    if (quantityAsInt >= 3) {
                        numberOfPromotionUsage = quantityAsInt / 3;
                        discountAmount = item.getTotalPrice() - (numberOfPromotionUsage * 2 * item.getPrice() + quantityAsInt % 3 * item.getPrice());
                        discount = new Discount(item.getProduct(), "3 for 2", discountAmount);
                    }
                    break;
                case TEN_PERCENT_DISCOUNT:
                    discountAmount = item.getQuantity() * item.getPrice() * offer.getDiscountAmount() / 100.0;
                    discount = new Discount(item.getProduct(), offer.getDiscountAmount() + "% off", discountAmount);
                    break;
                case TWO_FOR_AMOUNT:
                    if (quantityAsInt >= 2) {
                        numberOfPromotionUsage = quantityAsInt / 2;
                        discountAmount = numberOfPromotionUsage*(item.getPrice()*2-offer.getDiscountAmount());
                        discount = new Discount(item.getProduct(), "2 for " + offer.getDiscountAmount(), discountAmount);
                    }
                    break;
                case FIVE_FOR_AMOUNT:
                    if (quantityAsInt >= 5) {
                        numberOfPromotionUsage = quantityAsInt / 5;
                        discountAmount = numberOfPromotionUsage*(item.getPrice()*5-offer.getDiscountAmount());
                        discount = new Discount(item.getProduct(), "5 for " + offer.getDiscountAmount(), discountAmount);
                    }
                    break;
                default:
                    break;
            }

            if (discount != null) receipt.addDiscount(discount);
        }
    }

    void handleBundles(DefaultOffer offer) {

        if (findProducts(offer.getProducts())) {

            int numberOfPromotionUsage = minQuantity(offer.getProducts());
            double totalPriceBundle = calculateTotalPriceBundle(offer.getProducts());
            double discountAmount = totalPriceBundle*offer.getDiscountAmount()/100*numberOfPromotionUsage;
            Discount discount = new Discount(offer.getProducts(), "10% off bundle", discountAmount);
            receipt.addDiscount(discount);
        }
    }

    boolean findProducts(List<Product> bundleProducts) {
        Set<Product> remaining = new HashSet<>(bundleProducts);
        if (remaining.isEmpty()) return true;
        for (ReceiptItem item : receiptItems) {
            remaining.remove(item.getProduct());
            if (remaining.isEmpty()) return true;
        }
        return false;
    }

    ReceiptItem findByProduct(Product product) {
        for (ReceiptItem i : receiptItems) {
            if(i.getProduct().equals(product)) return i;
        }
        return null;
    }

    int minQuantity(List<Product> products) {

        Set<Product> filter = new HashSet<>(products);
        double min = Double.POSITIVE_INFINITY;

        for (ReceiptItem item : receiptItems) {
            if (filter.contains(item.getProduct())) {
                double q = item.getQuantity();
                if (q < min) {
                    min = q;
                }
            }
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

    public List<ReceiptItem> getReceiptItems() {
        return receiptItems;
    }
}

