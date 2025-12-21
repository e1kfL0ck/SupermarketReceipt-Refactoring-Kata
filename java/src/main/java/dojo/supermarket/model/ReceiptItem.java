package dojo.supermarket.model;

public class ReceiptItem {

    private final Product product;
    private final double totalPrice;
    private final double quantity;

    ReceiptItem(Product p, double quantity) {
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

    public double getQuantity() {
        return quantity;
    }

    public int getQuantityAsInt() {
        return (int) quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}
