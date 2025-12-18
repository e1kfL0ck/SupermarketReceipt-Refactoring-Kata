package dojo.supermarket.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class XPercentDiscount extends BaseSupermarketTest{

    @Test
    void tenPercentDiscountOnKilo() {
        ShoppingCart cart = new ShoppingCart();
        CheckoutCounter counter = new CheckoutCounter(offersMap, cart);

        cart.addItemInCart(product("grapes"), 3.2);
        counter.checkout(customer);

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
        CheckoutCounter counter = new CheckoutCounter(offersMap, cart);

        cart.addItemInCart(product("chocolate"), 3.0);
        counter.checkout(customer);

        double totalBeforeDiscount = 2.0 * 3;
        double discount = totalBeforeDiscount * 0.1;
        double totalAfterDiscount = totalBeforeDiscount - discount;

        ReceiptItem item = cart.items().get(0);
        assertEquals(2.0, item.getPrice(), 0.01);
        assertEquals(6.0, item.getTotalPrice(), 0.01);

        assertEquals(totalBeforeDiscount, counter.getReceipt().getTotalPrice(), 0.01);
        assertEquals(discount, counter.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(totalAfterDiscount, counter.getReceipt().getTotalPriceAfterDiscount(), 0);
    }
}
