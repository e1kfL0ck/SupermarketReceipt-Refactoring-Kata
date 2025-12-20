package dojo.supermarket.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BundleOfferTest extends BaseSupermarketTest {

    @Test
    void bundleBasic() {
        ShoppingCart cart = new ShoppingCart();
        CheckoutCounter counter = new CheckoutCounter(offersMap, cart);
        cart.addItemToCart(product("toothpaste"), 2.0);
        cart.addItemToCart(product("toothbrush"), 2.0);

        Receipt r = counter.checkout(customer);

        assertEquals(0.4, r.getTotalDiscounts(), 0.01);
        assertEquals(3.6, r.getTotalPrice(), 0.01);
    }
}