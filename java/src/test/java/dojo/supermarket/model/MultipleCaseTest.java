package dojo.supermarket.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MultipleCaseTest extends BaseSupermarketTest {
    @Test
    void oneOfEachOfferAppliedOnce() {
        ShoppingCart cart = new ShoppingCart();
        CheckoutCounter counter = new CheckoutCounter(offersMap, cart);

        cart.addItemToCart(product("grapes"), 2.2); //tenPercent
        cart.addItemToCart(product("soda"), 3.0); //threeForTwo
        cart.addItemToCart(product("milk"), 2.0); //twoForAmount
        cart.addItemToCart(product("steak"), 5.0); //fiveForAmount

        // Bundle
        cart.addItemToCart(product("toothbrush"), 1.0);
        cart.addItemToCart(product("toothpaste"), 1.0);

        Receipt r = counter.checkout(customer);

        double grapesTotal = 2.2 * 3.0;        // 6.6
        double sodaTotal = 3 * 1.2;            // 3.6
        double milkTotal = 2 * 1.37;           // 2.74
        double steakTotal = 5 * 2.42;          // 12.1
        double toothbrushTotal = 1 * 0.5;        // 0.5
        double toothpasteTotal = 1 * 1.5;       // 1.5

        double totalBeforeDiscount = grapesTotal + sodaTotal + milkTotal + steakTotal + toothbrushTotal + toothpasteTotal; // 26.04
        double grapesDiscount = grapesTotal * 0.10; // 0.66
        double sodaDiscount = 1.2; // 3-for-2 => 1 free
        double milkDiscount = (2 * 1.37) - 2.37; // qty=2 => uses=1 => 0.37
        double steakDiscount = steakTotal - 10.00; // 2.10
        double toothBundle = (1.5 + 0.5) * 0.10; // 0.20
        double totalDiscount = grapesDiscount + sodaDiscount + milkDiscount + steakDiscount + toothBundle; // 4.93
        double totalAfterDiscount = totalBeforeDiscount - totalDiscount;

        ReceiptItem item = cart.items().get(0);
        assertEquals(product("grapes"), item.getProduct());
        assertEquals(grapesTotal, item.getTotalPrice(), 0.01);

        item = cart.items().get(1);
        assertEquals(product("soda"), item.getProduct());
        assertEquals(sodaTotal, item.getTotalPrice(), 0.01);

        item = cart.items().get(2);
        assertEquals(product("milk"), item.getProduct());
        assertEquals(milkTotal, item.getTotalPrice(), 0.01);

        item = cart.items().get(3);
        assertEquals(product("steak"), item.getProduct());
        assertEquals(steakTotal, item.getTotalPrice(), 0.01);

        assertEquals(totalBeforeDiscount, r.getTotalPriceBeforeDiscount(), 0.01);
        assertEquals(totalDiscount, r.getTotalDiscounts(), 0.01);
        assertEquals(totalAfterDiscount, r.getTotalPrice(), 0.01);
    }
}
