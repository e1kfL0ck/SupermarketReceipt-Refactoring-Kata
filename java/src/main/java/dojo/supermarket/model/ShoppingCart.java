package dojo.supermarket.model;

import java.util.*;

public class ShoppingCart {

    private final List<ProductQuantity> items = new ArrayList<>();
    private final Map<Product, Double> productQuantities = new HashMap<>();

    private List<DefaultOffer> offerCatalog;
    private Receipt receipt = new Receipt();
    private List<ReceiptItem> receiptItems = new ArrayList<>();

    ShoppingCart() {}

    ShoppingCart(List<DefaultOffer> offerCatalog) {
        this.offerCatalog = offerCatalog;
    }

    List<ProductQuantity> getItems() {
        return Collections.unmodifiableList(items);
    }

    Map<Product, Double> productQuantities() {
        return Collections.unmodifiableMap(productQuantities);
    }

    public void addItemQuantity(Product product, double quantity) {
        items.add(new ProductQuantity(product, quantity));
        if (productQuantities.containsKey(product)) {
            productQuantities.put(product, productQuantities.get(product) + quantity);
        } else {
            productQuantities.put(product, quantity);
        }
    }

    void addItemInCart(Product product, double quantity) {
        receiptItems.add(new ReceiptItem(product, quantity));
    }

    void handleOffers(Receipt receipt, Map<Product, Offer> productOfferMap, SupermarketCatalog catalog) {
        for (Product product : productQuantities().keySet()) {
            if (productOfferMap.containsKey(product)) {

                Discount discount = null;
                Offer offer = productOfferMap.get(product);

                double unitPrice = product.getPrice();
                double quantity = productQuantities.get(product);
                int quantityAsInt = (int) quantity;
                int numberOfElementForDiscount;

                switch (offer.getOfferType()) {
                    case TWO_FOR_AMOUNT:
                        if (quantityAsInt >= 2) {
                            numberOfElementForDiscount = 2;
                            int numberOfPromotionUsage = quantityAsInt / numberOfElementForDiscount;
                            double discountAmount = unitPrice * quantity - (offer.getDiscountPercentageAmount() * numberOfPromotionUsage + quantityAsInt % 2 * unitPrice); //total value of the discount
                            discount = new Discount(product, "2 for " + offer.getDiscountPercentageAmount(), -discountAmount);
                        }
                        break;
                    case FIVE_FOR_AMOUNT:
                        if (quantityAsInt >= 5) {
                            numberOfElementForDiscount = 5;
                            int numberOfPromotionUsage = quantityAsInt / numberOfElementForDiscount;
                            double discountAmount = unitPrice * quantity - (offer.getDiscountPercentageAmount() * numberOfPromotionUsage + quantityAsInt % 5 * unitPrice);
                            discount = new Discount(product, "5 for " + offer.getDiscountPercentageAmount(), -discountAmount);
                        }
                        break;
                    case THREE_FOR_TWO:
                        if (quantityAsInt >= 3) {
                            numberOfElementForDiscount = 3;
                            int numberOfPromotionUsage = quantityAsInt / numberOfElementForDiscount;
                            double discountAmount = quantity * unitPrice - ((numberOfPromotionUsage * 2 * unitPrice) + quantityAsInt % 3 * unitPrice);
                            discount = new Discount(product, "3 for 2", -discountAmount);
                        }
                        break;
                    case TEN_PERCENT_DISCOUNT:
                        double discountAmount = -quantity * unitPrice * offer.getDiscountPercentageAmount() / 100.0;
                        discount = new Discount(product, offer.getDiscountPercentageAmount() + "% off", discountAmount);
                        break;
                }

                if (discount != null)
                    receipt.addDiscount(discount);
            }
        }
    }

//    void handleBundles(Receipt receipt, ArrayList<BundleOffer> bundleOffers) {
//
//        ArrayList<Product> bundleProducts = null;
//
//        for (BundleOffer bundleOffer : bundleOffers) {
//            bundleProducts = bundleOffer.getProducts();
//        }
//
//        if (findProdcuts(bundleProducts)) {
//
//            double numberOfDiscountUsage = findMinQuantity(bundleProducts);
//            double totalPriceBundle = calculateTotalPriceBundle(bundleProducts);
//            double totalPrice = calculateTotalPrice(bundleProducts);
//            double discountAmount = totalPrice - totalPriceBundle*0.9*numberOfDiscountUsage;
//            Discount discount = new Discount(bundleProducts, "10% off bundle", -discountAmount);
//            receipt.addDiscount(discount);
//        }
//
//    }

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
                handleBundles2(offer);
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
                        break;
                    }
                case TEN_PERCENT_DISCOUNT:
                    discountAmount = item.getQuantity() * item.getPrice() * offer.getDiscountPercentageAmount() / 100.0;
                    discount = new Discount(item.getProduct(), offer.getDiscountPercentageAmount() + "% off", discountAmount);
                    break;
            }

            if (discount != null) receipt.addDiscount(discount);
        }
    }

    void handleBundles2(DefaultOffer offer) {

        if (findProducts(offer.getProducts())) {

            int numberOfPromotionUsage = minQuantity(offer.getProducts());
            double totalPriceBundle = calculateTotalPriceBundle(offer.getProducts());
            double discountAmount = totalPriceBundle*offer.getDiscountPercentageAmount()/100*numberOfPromotionUsage;
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

    double calculateTotalPrice(List<Product> products) {
        double totalPrice = 0;
        Set<Product> filter = new HashSet<>(products);
        for (ReceiptItem item : receiptItems) {
            if (filter.contains(item.getProduct())) {
                totalPrice += item.getTotalPrice();
            }
        }
        return totalPrice;
    }

    public Receipt getReceipt() {
        return receipt;
    }

    public List<ReceiptItem> getReceiptItems() {
        return receiptItems;
    }
}

