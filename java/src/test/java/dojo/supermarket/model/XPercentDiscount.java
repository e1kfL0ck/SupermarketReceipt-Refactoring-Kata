package dojo.supermarket.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class XPercentDiscount extends BaseSupermarketTest{

    @Test
    void tenPercentDiscountOnKilo() {
        ShoppingCart cart = new ShoppingCart();
        CheckoutCounter counter = new CheckoutCounter(offers, cart);

        cart.addItemInCart(product("grapes"), 3.2);
        counter.checkout();

        double totalBeforeDiscount = 3.2 * 3;
        double discount = totalBeforeDiscount * 0.1;
        double totalAfterDiscount = totalBeforeDiscount - discount;

        assertEquals(totalBeforeDiscount, counter.getReceipt().getTotalPrice(), 0.01);
        assertEquals(discount, counter.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(totalAfterDiscount, counter.getReceipt().getTotalPriceAfterDiscount(), 0);
    }

    @Test
    void tenPercentDiscountOnEach() {
        ShoppingCart cart = new ShoppingCart();
        CheckoutCounter counter = new CheckoutCounter(offers, cart);

        cart.addItemInCart(product("chocolate"), 3.0);
        counter.checkout();

        double totalBeforeDiscount = 3.2 * 3;
        double discount = totalBeforeDiscount * 0.1;
        double totalAfterDiscount = totalBeforeDiscount - discount;

        assertEquals(totalBeforeDiscount, counter.getReceipt().getTotalPrice(), 0.01);
        assertEquals(discount, counter.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(totalAfterDiscount, counter.getReceipt().getTotalPriceAfterDiscount(), 0);
    }
}
