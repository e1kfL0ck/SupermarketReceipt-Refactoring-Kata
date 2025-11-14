package dojo.supermarket.model;

import java.util.List;

public class DefaultOffer {
    private SpecialOfferType offerType;
    private double discountAmount;
    private List<Product> products;

    DefaultOffer(SpecialOfferType offerType, double discountAmount, List<Product> products) {
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
}
