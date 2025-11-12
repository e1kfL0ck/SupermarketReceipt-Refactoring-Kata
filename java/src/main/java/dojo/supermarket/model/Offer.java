package dojo.supermarket.model;

public class Offer {

    SpecialOfferType offerType;
    private final Product product;
    double argument; //TODO: c quoi cette merde ?

    public Offer(SpecialOfferType offerType, Product product, double argument) {
        this.offerType = offerType;
        this.argument = argument;
        this.product = product;
    }

    public Offer(SpecialOfferType offerType, Product product) {
        this.offerType = offerType;
        this.product = product;
    }

    Product getProduct() {
        return product;
    }
}
