package dojo.supermarket.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DefaultTest extends BaseSupermarketTest{

    @Test
    void checkProductEachQuantity() {
        ShoppingCart cart = new ShoppingCart();
        assertThrows(IllegalArgumentException.class, () ->
                cart.addItemInCart(product("chocolate"), 3.1)
        );    }

    @Test
    void dumbCheckout() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItemInCart(product("chips"), 2.0);
        cart.addItemInCart(product("apples"), 1.2);

        CheckoutCounter counter = new CheckoutCounter(offersMap, cart);
        Receipt r = counter.checkout(customer);

        double total = 2*2 + 1.2*1.5;

        assertEquals(total, r.getTotalPrice(), 0.01);
        assertEquals(0.0, r.getTotalDiscounts(), 0.01);
        assertEquals(total, r.getTotalPriceAfterDiscount(), 0.01);
    }

}
