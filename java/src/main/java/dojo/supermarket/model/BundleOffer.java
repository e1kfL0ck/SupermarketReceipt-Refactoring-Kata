package dojo.supermarket.model;

import java.util.ArrayList;

public class BundleOffer extends DefaultOffer{

    ArrayList<Product> products;

    public BundleOffer(SpecialOfferType offerType, ArrayList<Product> products) {
        super(offerType);
        this.products = products;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }
}
