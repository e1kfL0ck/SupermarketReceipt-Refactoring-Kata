package dojo.supermarket.model;

public class Offer extends DefaultOffer{

    private Product product;

    public Offer(SpecialOfferType offerType, Product product, double discountPercentageAmount) {
        super(offerType, discountPercentageAmount);
        this.product = product;
    }

    public Offer(SpecialOfferType offerType, Product product) {
        super(offerType);
        this.product = product;
    }

    Product getProduct() {
        return product;
    }
}
