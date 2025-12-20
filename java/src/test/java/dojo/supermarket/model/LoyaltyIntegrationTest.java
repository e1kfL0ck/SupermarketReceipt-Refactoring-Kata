package dojo.supermarket.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoyaltyIntegrationTest extends BaseSupermarketTest {

    @Test
    void checkCustomerPointIncrease() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItemToCart(product("apples"), 3.0);

        CheckoutCounter counter = new CheckoutCounter(offersMap, cart);

        int before = customer.getCreditPoints(); //0
        Receipt receipt = counter.checkout(customer);

        assertEquals(4.5, receipt.getTotalPriceBeforeDiscount(), 0.01);

        int after = customer.getCreditPoints(); //45

        assertEquals(before + 45, after);
    }

    @Test
    void checkCustomerPointDecrease() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItemToCart(product("apples"), 3.0);

        CheckoutCounter counter = new CheckoutCounter(offersMap, cart);

        int before = customer.getCreditPoints(); //0
        Receipt receipt = counter.checkout(customer);

        assertEquals(4.5, receipt.getTotalPriceBeforeDiscount(), 0.01);

        int after = customer.getCreditPoints(); //45

        assertEquals(before + 45, after);

        // now test point usage
        cart.addItemToCart(product("apples"), 3.0);
        Receipt receiptAfterUsage = counter.checkout(customer);

        double totalPrice = 9-(after/100.0); // 9 - 0.45 = 8.55
        double newPoints = Math.floor(totalPrice*10); // 8.55 -> 85 points

        assertEquals(totalPrice, receiptAfterUsage.getTotalPrice(), 0.01);
        assertEquals(newPoints, customer.getCreditPoints());
    }

    @Test
    void checkPointUsageExceedingAvailablePoints() {
        customer.addCreditPoints(2000); // 20.0€

        ShoppingCart cart = new ShoppingCart();
        cart.addItemToCart(product("apples"), 3.0); // 4.5€

        CheckoutCounter counter = new CheckoutCounter(offersMap, cart);

        int before = customer.getCreditPoints(); // 2000
        Receipt receipt = counter.checkout(customer);

        assertEquals(4.5, receipt.getTotalPriceBeforeDiscount(), 0.01);

        // Needed points to cover 4.5€ = 450 points (since 1 point = 0.01€)
        int expectedUsedPoints = 450;
        int newPointsEarned = 0; // cash paid = 0 -> earned points = floor(0) = 0
        int expectedAfter = before - expectedUsedPoints + newPointsEarned;

        assertEquals(expectedAfter, customer.getCreditPoints(), 0.01);

        // If basket fully paid by points -> cash paid = 0 -> earned points = floor(0) = 0
        assertEquals(expectedAfter, customer.getCreditPoints());
    }

    @Test
    void checkPointUsageAndDiscountsTogether() {
       // Give enough points to fully pay AFTER discount, but not enough to pay BEFORE discount
        // soda: 3 for 2 => total before = 3.6, discount = 1.2, after = 2.4
        customer.addCreditPoints(300); // 3.0€

        ShoppingCart cart = new ShoppingCart();
        cart.addItemToCart(product("soda"), 3.0);

        CheckoutCounter counter = new CheckoutCounter(offersMap, cart);

        int before = customer.getCreditPoints(); // 300
        Receipt receipt = counter.checkout(customer);

        // Discount must be applied
        assertEquals(3.6, receipt.getTotalPriceBeforeDiscount(), 0.01);
        assertEquals(1.2, receipt.getTotalDiscounts(), 0.01);
        assertEquals(0, receipt.getTotalPrice(), 0.01);

        // Points should be consumed on AFTER-discount amount (2.4€ => 240 points), not on 3.6€ (360 points)
        int expectedUsedPoints = 240;
        int expectedAfterUse = before - expectedUsedPoints; // 300 - 240 = 60

        // Cash paid = 0 (since points cover 2.4€) => earned points = 0
        assertEquals(expectedAfterUse, customer.getCreditPoints());
    }

    //This time the points won't be enough to cover the after-discount amount fully
    @Test
    void checkPointUsageAndDiscountsTogether2() {
        customer.addCreditPoints(60); // 0.60€

        ShoppingCart cart = new ShoppingCart();
        cart.addItemToCart(product("soda"), 3.0);

        CheckoutCounter counter = new CheckoutCounter(offersMap, cart);

        int before = customer.getCreditPoints(); // 60
        Receipt receipt = counter.checkout(customer);

        // Discount must be applied
        assertEquals(3.6, receipt.getTotalPriceBeforeDiscount(), 0.01);
        assertEquals(1.2, receipt.getTotalDiscounts(), 0.01);
        assertEquals(2.4-0.6, receipt.getTotalPrice(), 0.01);

        int expectedUsedPoints = 60;
        int newPointsEarned = 18; // cash paid = 1.8 -> earned points = floor(1.8*10) = 18
        int expectedAfterUse = before - expectedUsedPoints + newPointsEarned; //=0

        assertEquals(expectedAfterUse, customer.getCreditPoints());
    }

}
