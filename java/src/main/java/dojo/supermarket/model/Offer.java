package dojo.supermarket.model;

import java.util.List;

//TODO: merge discounts and offers ?
public class Offer {
    private SpecialOfferType offerType;
    private double discountAmount;
    private List<Product> products;

    Offer(SpecialOfferType offerType, double discountAmount, List<Product> products) {
        this.offerType = offerType;
        this.discountAmount = discountAmount;
        this.products = products;
    }

    public List<Product> getProducts() {
        return products;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public SpecialOfferType getOfferType() {
        return offerType;
    }

    public Product getFirstProduct() {
        return products.get(0);
    }

    @Override
    public String toString() {
        StringBuilder retour = new StringBuilder();
        retour.append(products.get(0).getName());

        if (offerType == SpecialOfferType.BUNDLE) {
            for(int i=0;i < products.size();i++) {
                retour.append(", "+products.get(i+1));
            }
        }

        return retour.toString();
    }
}
