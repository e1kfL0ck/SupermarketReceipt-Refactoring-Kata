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

    @Test
    void bundleMultiple() {
        ShoppingCart cart = new ShoppingCart();
        CheckoutCounter counter = new CheckoutCounter(offersMap, cart);
        //bundle 1 * 3
        cart.addItemToCart(product("toothpaste"), 4.0);
        cart.addItemToCart(product("toothbrush"), 3.0);

        //bundle 2 * 2
        cart.addItemToCart(product("milk"), 2.0);
        cart.addItemToCart(product("ham"), 3.0);
        cart.addItemToCart(product("bread"), 4.0);

        Receipt r = counter.checkout(customer);

        double bundle1Price = (1.50 + 0.50)*3; // 2 bundles of toothpaste + toothbrush
        double bundle2Price = (1.37 + 4.00 + 1.50)*2; // 2 bundles of milk + ham + bread

        double totalPrice = (bundle1Price + bundle2Price)*0.9 +
                // remaining items
                (1.50) + // toothpaste
                (4.00) + // ham
                (1.50)*2; // bread

        assertEquals(2, r.getDiscounts().size());
        assertEquals(bundle1Price*0.1 + bundle2Price*0.1, r.getTotalDiscounts(), 0.01);
        assertEquals(totalPrice, r.getTotalPrice(), 0.01);
    }
}