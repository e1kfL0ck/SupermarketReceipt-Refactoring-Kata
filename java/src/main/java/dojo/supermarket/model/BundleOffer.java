package dojo.supermarket.model;

import java.util.List;

public class BundleOffer extends DefaultOffer{

    List<Product> products;

    public BundleOffer(SpecialOfferType offerType, List<Product> products) {
        super(offerType);
        this.products = products;
    }

}
