package dojo.supermarket.model;

import java.util.Objects;

public class Product {

    private final String name;
    private final ProductUnit unit;
    private double price;

    public Product(String name, ProductUnit unit) {
        this.name = name;
        this.unit = unit;
    }

    public Product(String name, ProductUnit unit, Double price) {
        this.name = name;
        this.unit = unit;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public ProductUnit getUnit() {
        return unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        //TODO: c quoi cette merde ?
        Product product = (Product) o;
        return Objects.equals(name, product.name) &&
                unit == product.unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, unit);
    }

    public double getPrice() {
        return price;
    }
}
