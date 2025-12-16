package dojo.supermarket.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class XForAmountTest extends BaseSupermarketTest {

    @Test
    void twoForAmount() {
        ShoppingCart cart = new ShoppingCart();
        CheckoutCounter counter = new CheckoutCounter(offers, cart);

        cart.addItemInCart(product("milk"), 2);
        counter.checkout();

        double totalBeforeDiscount = 2 * 1.37;
        double totalAfterDiscount = 2.37;
        double discount = totalBeforeDiscount - totalAfterDiscount;

        assertEquals(totalBeforeDiscount, counter.getReceipt().getTotalPrice(), 0.01);
        assertEquals(discount, counter.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(totalAfterDiscount, counter.getReceipt().getTotalPriceAfterDiscount(), 0.01);

        ReceiptItem receiptItem = cart.items().stream()
                .filter(i -> i.getProduct().equals(product("milk")))
                .findFirst()
                .orElseThrow();

        assertEquals(totalBeforeDiscount, receiptItem.getTotalPrice(), 0.01);
        assertEquals(2, receiptItem.getQuantity(), 0.01);
    }


    @Test
    void twoForAmountThreeProduct() {
        ShoppingCart cart = new ShoppingCart();
        CheckoutCounter counter = new CheckoutCounter(offers, cart);

        cart.addItemInCart(product("milk"), 3);
        counter.checkout();

        double totalBeforeDiscount = 3 * 1.37;
        double totalAfterDiscount = 3.74; // 2 for 2.37 + 1 * 1.37
        double discount = totalBeforeDiscount - totalAfterDiscount;

        assertEquals(totalBeforeDiscount, counter.getReceipt().getTotalPrice(), 0.01);
        assertEquals(discount, counter.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(totalAfterDiscount, counter.getReceipt().getTotalPriceAfterDiscount(), 0.01);

        ReceiptItem receiptItem = cart.items().stream()
                .filter(i -> i.getProduct().equals(product("milk")))
                .findFirst()
                .orElseThrow();

        assertEquals(totalBeforeDiscount, receiptItem.getTotalPrice(), 0.01);
        assertEquals(3, receiptItem.getQuantity(), 0.01);
    }

    @Test
    void twoForAmountTwoTime() {
        ShoppingCart cart = new ShoppingCart();
        CheckoutCounter counter = new CheckoutCounter(offers, cart);

        cart.addItemInCart(product("milk"), 5);
        counter.checkout();

        double totalBeforeDiscount = 5 * 1.37;
        double totalAfterDiscount = 4.74+1.37; // 2 * (2 for 2.37)
        double discount = totalBeforeDiscount - totalAfterDiscount;

        assertEquals(totalBeforeDiscount, counter.getReceipt().getTotalPrice(), 0.01);
        assertEquals(discount, counter.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(totalAfterDiscount, counter.getReceipt().getTotalPriceAfterDiscount(), 0.01);

        ReceiptItem receiptItem = cart.items().stream()
                .filter(i -> i.getProduct().equals(product("milk")))
                .findFirst()
                .orElseThrow();

        assertEquals(totalBeforeDiscount, receiptItem.getTotalPrice(), 0.01);
        assertEquals(5, receiptItem.getQuantity(), 0.01);
    }

    @Test
    void fiveForAmount() {
        ShoppingCart cart = new ShoppingCart();
        CheckoutCounter counter = new CheckoutCounter(offers, cart);

        cart.addItemInCart(product("steak"), 5);
        counter.checkout();

        double totalBeforeDiscount = 5 * 2.42;
        double totalAfterDiscount = 10.0;
        double discount = totalBeforeDiscount - totalAfterDiscount;

        assertEquals(totalBeforeDiscount, counter.getReceipt().getTotalPrice(), 0.01);
        assertEquals(discount, counter.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(totalAfterDiscount, counter.getReceipt().getTotalPriceAfterDiscount(), 0.01);

        assertEquals(1, cart.items().size());
        ReceiptItem item = cart.items().stream()
                .filter(i -> i.getProduct().equals(product("steak")))
                .findFirst()
                .orElseThrow();

        assertEquals(product("steak"), item.getProduct());
        assertEquals(2.42, item.getPrice(), 0.01);
        assertEquals(totalBeforeDiscount, item.getTotalPrice(), 0.01);
        assertEquals(5, item.getQuantity(), 0.01);
    }

    @Test
    void fiveForAmountSixProduct() {
        ShoppingCart cart = new ShoppingCart();
        CheckoutCounter counter = new CheckoutCounter(offers, cart);

        cart.addItemInCart(product("steak"), 6);
        counter.checkout();

        double totalBeforeDiscount = 6 * 2.42;
        double totalAfterDiscount = 12.42; // 5 for 10 + 1 * 2.42
        double discount = totalBeforeDiscount - totalAfterDiscount;

        assertEquals(totalBeforeDiscount, counter.getReceipt().getTotalPrice(), 0.01);
        assertEquals(discount, counter.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(totalAfterDiscount, counter.getReceipt().getTotalPriceAfterDiscount(), 0.01);

        assertEquals(1, cart.items().size());
        ReceiptItem item = cart.items().stream()
                .filter(i -> i.getProduct().equals(product("steak")))
                .findFirst()
                .orElseThrow();

        assertEquals(product("steak"), item.getProduct());
        assertEquals(2.42, item.getPrice(), 0.0001);
        assertEquals(totalBeforeDiscount, item.getTotalPrice(), 0.01);
        assertEquals(6, item.getQuantity(), 0.01);
    }

}
