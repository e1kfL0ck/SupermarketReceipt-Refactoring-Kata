package dojo.supermarket.model;

import java.util.List;

public class DefaultOffer {
    private SpecialOfferType offerType;
    private double discountPercentageAmount;
    private List<Product> products;

    DefaultOffer(SpecialOfferType offerType, double discountPercentageAmount) {
        this.offerType = offerType;
        this.discountPercentageAmount = discountPercentageAmount;
    }

    DefaultOffer(SpecialOfferType offerType, double discountPercentageAmount, List<Product> products) {
        this.offerType = offerType;
        this.discountPercentageAmount = discountPercentageAmount;
        this.products = products;
    }

    DefaultOffer(SpecialOfferType offerType) {
        this.offerType = offerType;
    }

    public List<Product> getProducts() {
        return products;
    }

    public double getDiscountPercentageAmount() {
        return discountPercentageAmount;
    }

    public SpecialOfferType getOfferType() {
        return offerType;
    }
}
