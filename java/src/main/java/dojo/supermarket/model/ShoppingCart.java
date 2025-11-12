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
        for (Product product: productQuantities().keySet()) {
            if (productOfferMap.containsKey(product)) {

                Discount discount = null;
                Offer offer = productOfferMap.get(product);

                double unitPrice = catalog.getUnitPrice(product);
                double quantity = productQuantities.get(product);
                int quantityAsInt = (int) quantity;
                int numberOfElementForDiscount;

                switch (offer.offerType) {
                    case TWO_FOR_AMOUNT:
                        if (quantityAsInt >= 2) {
                            numberOfElementForDiscount = 2;
                            int numberOfPromotionUsage = quantityAsInt / numberOfElementForDiscount;
                            double discountAmount = unitPrice * quantity - (offer.argument * numberOfPromotionUsage + quantityAsInt % 2 * unitPrice); //total value of the discount
                            discount = new Discount(product, "2 for " + offer.argument, -discountAmount);
                        }
                        break;
                    case FIVE_FOR_AMOUNT:
                        if (quantityAsInt >= 5) {
                            numberOfElementForDiscount = 5;
                            int numberOfPromotionUsage = quantityAsInt / numberOfElementForDiscount;
                            double discountAmount = unitPrice * quantity - (offer.argument * numberOfPromotionUsage + quantityAsInt % 5 * unitPrice);
                            discount = new Discount(product, "5 for " + offer.argument, -discountAmount);
                        }
                        break;
                    case THREE_FOR_TWO:
                        if (quantityAsInt >=3 ) {
                            numberOfElementForDiscount=3;
                            int numberOfPromotionUsage = quantityAsInt / numberOfElementForDiscount;
                            double discountAmount = quantity * unitPrice - ((numberOfPromotionUsage  * 2 * unitPrice) + quantityAsInt % 3 * unitPrice);
                            discount = new Discount(product, "3 for 2", -discountAmount);
                        }
                        break;
                    case TEN_PERCENT_DISCOUNT:
                        double discountAmount = -quantity * unitPrice * offer.argument / 100.0;
                        discount = new Discount(product, offer.argument + "% off", discountAmount);
                }

                if (discount != null)
                    receipt.addDiscount(discount);
            }
        }
    }
}
