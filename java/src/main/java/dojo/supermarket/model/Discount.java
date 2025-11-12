package dojo.supermarket.model;

import java.util.ArrayList;

public class Discount {

    private final String description;
    private final double discountAmount;
    private Product product;
    private ArrayList<Product> products;

    public Discount(Product product, String description, double discountAmount) {
        this.product = product;
        this.description = description;
        this.discountAmount = discountAmount;
    }

    public Discount(ArrayList<Product> products, String description, double discountAmount) {
        this.products = products;
        this.description = description;
        this.discountAmount = discountAmount;
    }

    public String getDescription() {
        return description;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public Product getProduct() {
        return product;
    }
}
