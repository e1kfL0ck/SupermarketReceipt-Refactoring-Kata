package dojo.supermarket.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ThreeForTwoTest extends BaseSupermarketTest {

    @Test
    void threeForTwoDiscount() {
        ShoppingCart cart = new ShoppingCart();
        CheckoutCounter counter = new CheckoutCounter(offers, cart);

        cart.addItemInCart(product("soda"), 3);
        counter.checkout();

        double totalBeforeDiscount = 3 * 1.20;
        double discount = 1.20;
        double totalAfterDiscount = totalBeforeDiscount - discount;

        assertEquals(totalBeforeDiscount, counter.getReceipt().getTotalPrice(), 0.01);
        assertEquals(discount, counter.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(totalAfterDiscount, counter.getReceipt().getTotalPriceAfterDiscount(), 0.01);

        ReceiptItem receiptItem = cart.items().stream()
                .filter(i -> i.getProduct().equals(product("soda")))
                .findFirst()
                .orElseThrow();

        assertEquals(totalBeforeDiscount, receiptItem.getTotalPrice(), 0.01);
        assertEquals(3, receiptItem.getQuantity(), 0.01);
    }

    @Test
    void threeForTwoDiscountFourProducts() {
        ShoppingCart cart = new ShoppingCart();
        CheckoutCounter counter = new CheckoutCounter(offers, cart);

        cart.addItemInCart(product("soda"), 4);
        counter.checkout();

        double totalBeforeDiscount = 4 * 1.20;
        double discount = 1.20;
        double totalAfterDiscount = totalBeforeDiscount - discount;

        assertEquals(totalBeforeDiscount, counter.getReceipt().getTotalPrice(), 0.01);
        assertEquals(discount, counter.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(totalAfterDiscount, counter.getReceipt().getTotalPriceAfterDiscount(), 0.01);

        ReceiptItem receiptItem = cart.items().stream()
                .filter(i -> i.getProduct().equals(product("soda")))
                .findFirst()
                .orElseThrow();

        assertEquals(totalBeforeDiscount, receiptItem.getTotalPrice(), 0.01);
        assertEquals(4, receiptItem.getQuantity(), 0.01);
    }

    @Test
    void threeForTwoDiscountTimesTwo() {
        ShoppingCart cart = new ShoppingCart();
        CheckoutCounter counter = new CheckoutCounter(offers, cart);

        cart.addItemInCart(product("soda"), 7);
        counter.checkout();

        double totalBeforeDiscount = 7 * 1.20;
        double discount = 2 * 1.20; // two full 3-for-2 groups
        double totalAfterDiscount = totalBeforeDiscount - discount;

        assertEquals(totalBeforeDiscount, counter.getReceipt().getTotalPrice(), 0.01);
        assertEquals(discount, counter.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(totalAfterDiscount, counter.getReceipt().getTotalPriceAfterDiscount(), 0.01);

        ReceiptItem receiptItem = cart.items().stream()
                .filter(i -> i.getProduct().equals(product("soda")))
                .findFirst()
                .orElseThrow();

        assertEquals(totalBeforeDiscount, receiptItem.getTotalPrice(), 0.01);
        assertEquals(7, receiptItem.getQuantity(), 0.01);
    }

}
