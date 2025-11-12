package dojo.supermarket.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingCart {

    private final List<ProductQuantity> items = new ArrayList<>();
    private final Map<Product, Double> productQuantities = new HashMap<>();

    List<ProductQuantity> getItems() {
        return Collections.unmodifiableList(items);
    }

    void addItem(Product product) {
        addItemQuantity(product, 1.0);
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

    void handleOffers(Receipt receipt, Map<Product, Offer> productOfferMap, SupermarketCatalog catalog) {
        for (Product product : productQuantities().keySet()) {
            if (productOfferMap.containsKey(product)) {

                Discount discount = null;
                Offer offer = productOfferMap.get(product);

                double unitPrice = product.getPrice();
                double quantity = productQuantities.get(product);
                int quantityAsInt = (int) quantity;
                int numberOfElementForDiscount;

                switch (offer.offerType) {
                    case TWO_FOR_AMOUNT:
                        if (quantityAsInt >= 2) {
                            numberOfElementForDiscount = 2;
                            int numberOfPromotionUsage = quantityAsInt / numberOfElementForDiscount;
                            double discountAmount = unitPrice * quantity - (offer.discountPercentageAmount * numberOfPromotionUsage + quantityAsInt % 2 * unitPrice); //total value of the discount
                            discount = new Discount(product, "2 for " + offer.discountPercentageAmount, -discountAmount);
                        }
                        break;
                    case FIVE_FOR_AMOUNT:
                        if (quantityAsInt >= 5) {
                            numberOfElementForDiscount = 5;
                            int numberOfPromotionUsage = quantityAsInt / numberOfElementForDiscount;
                            double discountAmount = unitPrice * quantity - (offer.discountPercentageAmount * numberOfPromotionUsage + quantityAsInt % 5 * unitPrice);
                            discount = new Discount(product, "5 for " + offer.discountPercentageAmount, -discountAmount);
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
                        double discountAmount = -quantity * unitPrice * offer.discountPercentageAmount / 100.0;
                        discount = new Discount(product, offer.discountPercentageAmount + "% off", discountAmount);
                        break;
                }

                if (discount != null)
                    receipt.addDiscount(discount);
            }
        }
    }

    void handleBundles(Receipt receipt, ArrayList<BundleOffer> bundleOffers, SupermarketCatalog catalog) {

        ArrayList<Product> bundleProducts = null;

        for (BundleOffer bundleOffer : bundleOffers) {
            bundleProducts = bundleOffer.getProducts();
        }

        if (findProdcuts(bundleProducts)) {

            double numberOfDiscountUsage = findMin(bundleProducts);
            double totalPriceBundle = calculateTotalPriceBundle(bundleProducts);
            double totalPrice = calculateTotalPrice(bundleProducts);
            double discountAmount = totalPrice - totalPriceBundle*(10-numberOfDiscountUsage);
            Discount discount = new Discount(bundleProducts, "10% off bundle", discountAmount);
            receipt.addDiscount(discount);
        }

    }

    boolean findProdcuts(ArrayList<Product> bundleProducts) {
        int bundleSize = bundleProducts.size();
        int i = 0;
        for (Product p : bundleProducts) {
            if (p != null && productQuantities.containsKey(p)) {
                i++;
            }
        }

        if (i == bundleSize) {
            return true;
        } else {
            return false;
        }
    }

    double findMin(ArrayList<Product> bundleProducts) {
        double min = Double.MAX_VALUE;
        for (Product p : bundleProducts) {
            if (p.getPrice() < min) {
                min = p.getPrice();
            }
        }
        return min;
    }

    double calculateTotalPriceBundle(ArrayList<Product> bundleProducts) {
        double totalPrice = 0;
        for (Product p : bundleProducts) {
            totalPrice += p.getPrice();
        }
        return totalPrice;
    }

    double calculateTotalPrice(ArrayList<Product> bundleProducts) {
        double totalPrice = 0;
        for (Product product : bundleProducts) {
            totalPrice += productQuantities.get(product) * product.getPrice();
        }
        return totalPrice;
    }
}

