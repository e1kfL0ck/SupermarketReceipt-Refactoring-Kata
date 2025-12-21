package dojo.supermarket.model;

import java.util.*;

public class ShoppingCart {

    private Map<Product, ReceiptItem> items = new LinkedHashMap<>(); // to preserve insertion order

    ShoppingCart() {}

    /**
     * Adds an item to the shopping cart. If the item already exists, it updates the quantity.
     *
     * @param product  The product to add.
     * @param quantity The quantity of the product.
     */
    void addItemToCart(Product product, double quantity) {
        items.merge(
                product,
                new ReceiptItem(product, quantity),
                (existing, added) -> new ReceiptItem(product, existing.getQuantity() + added.getQuantity())
        );
    }

    public ArrayList<ReceiptItem> items() {
        return new ArrayList<>(items.values());
    }

    ReceiptItem get(Product product) {
        return items.get(product);
    }

    boolean contains(Product product) {
        return items.containsKey(product);
    }

}

