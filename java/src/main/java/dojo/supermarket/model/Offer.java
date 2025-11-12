package dojo.supermarket.model;

import java.util.ArrayList;

public class Offer {

    SpecialOfferType offerType;
    private Product product;
    double argument; //TODO: c quoi cette merde ?
    private ArrayList<Product> bundleProducts;

    public Offer(SpecialOfferType offerType, Product product, double argument) {
        this.offerType = offerType;
        this.argument = argument;
        this.product = product;
    }

    public Offer(SpecialOfferType offerType, Product product) {
        this.offerType = offerType;
        this.product = product;
    }

    public Offer(SpecialOfferType offerType, ArrayList<Product> bundleProducts) {
        this.offerType = offerType;
        this.bundleProducts =  bundleProducts;
    }

    Product getProduct() {
        return product;
    }
}
