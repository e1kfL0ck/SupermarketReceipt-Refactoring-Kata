package dojo.supermarket.model;

import java.util.Objects;

public class ReceiptItem {

    private final Product product;
    private final double totalPrice;
    private final Double quantity;

    ReceiptItem(Product p, Double quantity) {
        product = p;
        if (p.getUnit() == ProductUnit.EACH && quantity % 1 != 0) {
            throw new IllegalArgumentException("Qty for unit EACH must be an integer value.");
        }
        this.quantity = quantity;
        totalPrice = p.getPrice() * quantity;
    }

    public double getPrice() {
        return product.getPrice();
    }

    public Product getProduct() {
        return product;
    }

    public Double getQuantity() {
        return quantity;
    }

    public Integer getQuantityAsInt() {
        return quantity.intValue();
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
