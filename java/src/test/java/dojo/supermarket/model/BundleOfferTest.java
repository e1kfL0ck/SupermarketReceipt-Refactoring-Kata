package dojo.supermarket.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BundleOfferTest extends BaseSupermarketTest {

    @Test
    void bundleBasic() {
        ShoppingCart cart = new ShoppingCart();
        CheckoutCounter counter = new CheckoutCounter(offers, cart);
        cart.addItemInCart(product("apples"), 2.0);
        cart.addItemInCart(product("toothbrush"), 2.0);

        counter.checkout();

        assertEquals(0.4, counter.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(3.6, counter.getReceipt().getTotalPriceAfterDiscount(), 0.01);
    }
}