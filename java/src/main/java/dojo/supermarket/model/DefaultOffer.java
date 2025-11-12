package dojo.supermarket.model;

public abstract class DefaultOffer {
    SpecialOfferType offerType;
    double discountPercentageAmount;

    DefaultOffer(SpecialOfferType offerType, double discountPercentageAmount) {
        this.offerType = offerType;
        this.discountPercentageAmount = discountPercentageAmount;
    }

    DefaultOffer(SpecialOfferType offerType) {
        this.offerType = offerType;
    }
}
