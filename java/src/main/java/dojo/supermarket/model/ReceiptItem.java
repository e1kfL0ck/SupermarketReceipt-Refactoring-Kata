package dojo.supermarket.model;

import java.util.Objects;

public class ReceiptItem {

    private final Product product;
    private final double totalPrice;
    private final double quantity;

    ReceiptItem(Product p, double quantity) {
        product = p;
        this.quantity = quantity;
        totalPrice = p.getPrice() * quantity;
    }

    public double getPrice() {
        return product.getPrice();
    }

    public Product getProduct() {
        return product;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReceiptItem)) return false;
        ReceiptItem that = (ReceiptItem) o;
        return Double.compare(that.product.getPrice(), product.getPrice()) == 0 &&
                Double.compare(that.totalPrice, totalPrice) == 0 &&
                Double.compare(that.quantity, quantity) == 0 &&
                Objects.equals(product, that.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, product.getPrice(), totalPrice, quantity);
    }
}
