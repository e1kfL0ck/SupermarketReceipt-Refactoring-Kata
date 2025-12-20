package dojo.supermarket.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class XPercentDiscount extends BaseSupermarketTest{

    @Test
    void tenPercentDiscountOnKilo() {
        ShoppingCart cart = new ShoppingCart();
        CheckoutCounter counter = new CheckoutCounter(offersMap, cart);

        cart.addItemToCart(product("grapes"), 3.2);
        Receipt r = counter.checkout(customer);

        double totalBeforeDiscount = 3.2 * 3;
        double discount = totalBeforeDiscount * 0.1;
        double totalAfterDiscount = totalBeforeDiscount - discount;

        assertEquals(totalBeforeDiscount, r.getTotalPrice(), 0.01);
        assertEquals(discount, r.getTotalDiscounts(), 0.01);
        assertEquals(totalAfterDiscount, r.getTotalPriceAfterDiscount(), 0);
    }

    @Test
    void tenPercentDiscountOnEach() {
        ShoppingCart cart = new ShoppingCart();
        CheckoutCounter counter = new CheckoutCounter(offersMap, cart);

        cart.addItemToCart(product("chocolate"), 3.0);
        Receipt r = counter.checkout(customer);

        double totalBeforeDiscount = 2.0 * 3;
        double discount = totalBeforeDiscount * 0.1;
        double totalAfterDiscount = totalBeforeDiscount - discount;

        ReceiptItem item = cart.items().get(0);
        assertEquals(2.0, item.getPrice(), 0.01);
        assertEquals(6.0, item.getTotalPrice(), 0.01);

        assertEquals(totalBeforeDiscount, r.getTotalPrice(), 0.01);
        assertEquals(discount, r.getTotalDiscounts(), 0.01);
        assertEquals(totalAfterDiscount, r.getTotalPriceAfterDiscount(), 0);
    }
}
