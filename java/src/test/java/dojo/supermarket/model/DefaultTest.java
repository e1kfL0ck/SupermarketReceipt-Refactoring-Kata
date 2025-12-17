package dojo.supermarket.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DefaultTest extends BaseSupermarketTest{

    @Test
    void checkProductEachQuantity() {
        ShoppingCart cart = new ShoppingCart();
        assertThrows(IllegalArgumentException.class, () ->
                cart.addItemInCart(product("chocolate"), 3.1)
        );    }

}
