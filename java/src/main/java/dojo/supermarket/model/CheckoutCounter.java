package dojo.supermarket.model;

import java.util.Map;

public class CheckoutCounter {
    private final Map<Product, Offer> offersMap;
    private final ShoppingCart cart;
    private DiscountEngine engine;

    public CheckoutCounter(Map<Product, Offer> offersMap, ShoppingCart cart) {
        this.offersMap = offersMap != null ? offersMap : new java.util.HashMap<>();
        this.cart = cart;
        this.engine = new DiscountEngine(this.offersMap);
    }

    public Receipt checkout(Customer customer) {
        Receipt receipt = new Receipt(cart.items());// IMPORTANT: receipt exist before applying offers and coupons (so qty is the same)
        engine.applyAll(cart, customer, receipt);
        receipt.pay();
        return receipt;
    }
}
