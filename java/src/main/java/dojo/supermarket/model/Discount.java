package dojo.supermarket.model;

import java.util.List;

public class Discount {

    private final String description;
    private final double discountAmount;
    private List<Product> products;

    public Discount(List<Product> products, String description, double discountAmount) {
        this.products = products;
        this.description = description;
        this.discountAmount = discountAmount;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }
}
