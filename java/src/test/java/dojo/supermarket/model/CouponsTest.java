package dojo.supermarket.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CouponsTest extends BaseSupermarketTest{

    @Test
    public void testCoupons() {
        ShoppingCart cart = new ShoppingCart();
        CheckoutCounter counter = new CheckoutCounter(offersMap, cart);

        cart.addItemInCart(product("bread"), 3);
        counter.checkout(java.time.LocalDate.now(), customer );

        ReceiptItem item = cart.items().get(0);
        assertEquals(product("bread"), item.getProduct());
        assertEquals(1.5, item.getPrice(), 0.01);
        assertEquals(4.5, item.getTotalPrice(), 0.01);
        assertEquals(3.0, item.getQuantity(), 0.01);

        assertEquals(4.5, counter.getReceipt().getTotalPrice(), 0.01);
        assertEquals(0.75, counter.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(3.75, counter.getReceipt().getTotalPriceAfterDiscount(), 0.01);
    }
}
