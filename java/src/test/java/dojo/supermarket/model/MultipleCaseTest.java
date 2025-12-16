package dojo.supermarket.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MultipleCaseTest extends BaseSupermarketTest {

    @Test
    void complexScenario_multipleOffersKiloAndEach_case1() {
        // Offers:
        //  - 10% bundle discount on 1kg apples + 1kg tootbrush (kilo + kilo)
        //  - 2-for-amount on milk (2 for 2.0)
        //  - 10% discount on chocolate

        ShoppingCart cart = new ShoppingCart();
        CheckoutCounter counter = new CheckoutCounter(offers, cart);

        // Quantities:
        //  apples 2.0kg, rice 1.0kg, milk 3, chocolate 4
        cart.addItemInCart(product("apples"), 2.0);
        cart.addItemInCart(product("toothbrush"), 1.0);
        cart.addItemInCart(product("milk"), 3);
        cart.addItemInCart(product("chocolate"), 4);
        counter.checkout();

        double applesTotal = 2.0 * 1.5;  // 4.0
        double toothbrushTotal = 1.0 * 0.5;    // 0.5
        double milkTotal = 3 * 1.37;      // 3.6
        double chocolateTotal = 4 * 2.0; // 8.0

        double totalBeforeDiscount = applesTotal + toothbrushTotal + milkTotal + chocolateTotal; // 16.1

        // Bundle (apples + rice): 1 full bundle -> (2.0 + 0.5) * 10% = 0.25
        double bundleDiscount = (1.5 + 0.5) * 0.10; // 0.25

        // Milk: 2 for 2.0 -> normal 2 * 1.2 = 2.4, offer 2.0 => 0.4 discount
        // There are 3 milks -> 2 in offer, 1 full price
        double milkDiscount = milkTotal - (2.37 + 1.37); // 0.4

        // Chocolate: 10% discount on all
        double chocolateDiscount = chocolateTotal * 0.10; // 0.8

        double totalDiscount = bundleDiscount + milkDiscount + chocolateDiscount; // 1.45
        double totalAfterDiscount = totalBeforeDiscount - totalDiscount;

        ReceiptItem item = cart.items().get(0);
        assertEquals(product("apples"), item.getProduct());
        assertEquals(1.5, item.getPrice(), 0.01);
        assertEquals(applesTotal, item.getTotalPrice(), 0.01);
        assertEquals(2.0, item.getQuantity(), 0.01);

        item = cart.items().get(1);
        assertEquals(product("toothbrush"), item.getProduct());
        assertEquals(0.5, item.getPrice(), 0.01);
        assertEquals(toothbrushTotal, item.getTotalPrice(), 0.01);
        assertEquals(1.0, item.getQuantity(), 0.01);

        item = cart.items().get(2);
        assertEquals(product("milk"), item.getProduct());
        assertEquals(1.37, item.getPrice(), 0.01);
        assertEquals(milkTotal, item.getTotalPrice(), 0.01);
        assertEquals(3, item.getQuantity(), 0.01);

        item = cart.items().get(3);
        assertEquals(product("chocolate"), item.getProduct());
        assertEquals(2.0, item.getPrice(), 0.01);
        assertEquals(chocolateTotal, item.getTotalPrice(), 0.01);
        assertEquals(4, item.getQuantity(), 0.01);// 15.55

        assertEquals(totalBeforeDiscount, counter.getReceipt().getTotalPrice(), 0.01);
        assertEquals(totalDiscount, counter.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(totalAfterDiscount, counter.getReceipt().getTotalPriceAfterDiscount(), 0.01);
    }

    @Test
    void all_offer_types_are_applied_correctly_in_one_checkout_with_split_adds() {
        ShoppingCart cart = new ShoppingCart();
        CheckoutCounter counter = new CheckoutCounter(offers, cart);

        // BUNDLE (apples + toothbrush) -> 2 bundles (added multiple times)
        cart.addItemInCart(product("apples"), 1);
        cart.addItemInCart(product("apples"), 1);
        cart.addItemInCart(product("toothbrush"), 1);
        cart.addItemInCart(product("toothbrush"), 1);

        // BUNDLE (cheese + ham + bread) -> 1 bundle (added multiple times)
        cart.addItemInCart(product("cheese"), 0.4);
        cart.addItemInCart(product("cheese"), 0.6);
        cart.addItemInCart(product("ham"), 1);
        cart.addItemInCart(product("bread"), 1);

        // TEN_PERCENT_DISCOUNT (added multiple times)
        cart.addItemInCart(product("chocolate"), 2);
        cart.addItemInCart(product("chocolate"), 2);
        cart.addItemInCart(product("grapes"), 0.7);
        cart.addItemInCart(product("grapes"), 0.5);

        // THREE_FOR_TWO
        cart.addItemInCart(product("soda"), 1);
        cart.addItemInCart(product("soda"), 2);

        // TWO_FOR_AMOUNT
        cart.addItemInCart(product("milk"), 2);
        cart.addItemInCart(product("milk"), 3);

        // FIVE_FOR_AMOUNT
        cart.addItemInCart(product("steak"), 2);
        cart.addItemInCart(product("steak"), 3);
        cart.addItemInCart(product("chips"), 1);
        cart.addItemInCart(product("chips"), 4);

        counter.checkout();

        // Totals before discount
        double applesTotal = 2 * 1.50;          // 3.00
        double toothbrushTotal = 2 * 0.50;      // 1.00
        double cheeseTotal = 1.0 * 9.00;        // 9.00
        double hamTotal = 1 * 4.00;             // 4.00
        double breadTotal = 1 * 1.50;           // 1.50
        double chocolateTotal = 4 * 2.00;       // 8.00
        double grapesTotal = 1.2 * 3.00;        // 3.60
        double sodaTotal = 3 * 1.20;            // 3.60
        double milkTotal = 5 * 1.37;            // 6.85
        double steakTotal = 5 * 2.42;           // 12.10
        double chipsTotal = 5 * 2.00;           // 10.00

        double totalBeforeDiscount =
                applesTotal + toothbrushTotal +
                        cheeseTotal + hamTotal + breadTotal +
                        chocolateTotal + grapesTotal +
                        sodaTotal + milkTotal +
                        steakTotal + chipsTotal;

        // Discounts (one promo per product)
        double bundleApplesToothbrush = (1.50 + 0.50) * 0.10 * 2; // 0.40
        double bundleCheeseHamBread = (9.00 + 4.00 + 1.50) * 0.10 * 1; // 1.45
        double chocolateDiscount = chocolateTotal * 0.10; // 0.80
        double grapesDiscount = grapesTotal * 0.10; // 0.36
        double sodaDiscount = 1.20; // 3-for-2 => 1 free
        double milkDiscount = 2 * ((2 * 1.37) - 2.37); // qty=5 => uses=2 => 0.74
        double steakDiscount = steakTotal - 10.00; // 2.10
        double chipsDiscount = chipsTotal - 8.00;  // 2.00

        double totalDiscount =
                bundleApplesToothbrush +
                        bundleCheeseHamBread +
                        chocolateDiscount +
                        grapesDiscount +
                        sodaDiscount +
                        milkDiscount +
                        steakDiscount +
                        chipsDiscount;

        double totalAfterDiscount = totalBeforeDiscount - totalDiscount;

        assertEquals(totalBeforeDiscount, counter.getReceipt().getTotalPrice(), 0.01);
        assertEquals(totalDiscount, counter.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(totalAfterDiscount, counter.getReceipt().getTotalPriceAfterDiscount(), 0.01);
    }
}
