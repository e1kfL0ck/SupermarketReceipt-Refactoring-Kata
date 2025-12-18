package dojo.supermarket.model;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CouponsTest extends BaseSupermarketTest{

    @Test
    public void testCouponBasic() {
        ShoppingCart cart = new ShoppingCart();
        CheckoutCounter counter = new CheckoutCounter(offersMap, cart);
        Customer customerCopy = new Customer(customer);

        // Buy 2, get 1 for 50% off
        cart.addItemInCart(product("bread"), 3.0);
        Receipt r = counter.checkout(customerCopy);

        ReceiptItem item = cart.items().get(0);
        assertEquals(product("bread"), item.getProduct());
        assertEquals(1.5, item.getPrice(), 0.01);
        assertEquals(4.5, item.getTotalPrice(), 0.01);
        assertEquals(3.0, item.getQuantity(), 0.01);

        assertEquals(4.5, counter.getReceipt().getTotalPrice(), 0.01);
        assertEquals(0.75, counter.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(3.75, counter.getReceipt().getTotalPriceAfterDiscount(), 0.01);
    }

    @Test
    public void testCouponWinOverOffer() {
        ShoppingCart cart = new ShoppingCart();
        Map<Product, Offer> myOffersMap = new HashMap<>(offersMap);
        Customer customerCopy = new Customer(customer);

        //coupon gives 50% on 2nd soda

        // 1.20 € on soda, so 2 for 2€
        myOffersMap.put(product("soda"), new Offer(
                SpecialOfferType.TWO_FOR_AMOUNT,
                2.0,
                List.of(product("soda"))
        ));

        CheckoutCounter counter = new CheckoutCounter(myOffersMap, cart);

        cart.addItemInCart(product("soda"), 2.0);
        Receipt r = counter.checkout(customerCopy);

        ReceiptItem item = cart.items().get(0);
        assertEquals(product("soda"), item.getProduct());
        assertEquals(1.2, item.getPrice(), 0.01);
        assertEquals(2.4, item.getTotalPrice(), 0.01);
        assertEquals(2.0, item.getQuantity(), 0.01);

        assertEquals(2.4, counter.getReceipt().getTotalPrice(), 0.01);
        assertEquals(0.6, counter.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(1.8, counter.getReceipt().getTotalPriceAfterDiscount(), 0.01);
    }

    @Test
    public void testCouponLooseOverOffer() {
        ShoppingCart cart = new ShoppingCart();

        Map<Product, Offer> myOffersMap = new HashMap<>(offersMap);

        // 2 for 1€ on bread, so 3 for 1+1.5€
        myOffersMap.put(product("bread"), new Offer(
                SpecialOfferType.TWO_FOR_AMOUNT,
                1.0,
                List.of(product("bread"))
        ));

        CheckoutCounter counter = new CheckoutCounter(myOffersMap, cart);

        // Buy 2, get 1 for 50% off
        cart.addItemInCart(product("bread"), 3.0);
        Receipt r = counter.checkout(customer);

        ReceiptItem item = cart.items().get(0);
        assertEquals(product("bread"), item.getProduct());
        assertEquals(1.5, item.getPrice(), 0.01);
        assertEquals(4.5, item.getTotalPrice(), 0.01);
        assertEquals(3.0, item.getQuantity(), 0.01);

        assertEquals(4.5, counter.getReceipt().getTotalPrice(), 0.01);
        assertEquals(2.0, counter.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(2.5, counter.getReceipt().getTotalPriceAfterDiscount(), 0.01);
    }

    @Test
    public void testCouponEqualOffer() {
        ShoppingCart cart = new ShoppingCart();
        Map<Product, Offer> myOffersMap = new HashMap<>(offersMap);
        Customer customerCopy = new Customer(customer);

        // 2 for 2.25€ on bread, so 3 for 2.25+1.5=3.75, same as coupon€
        myOffersMap.put(product("bread"), new Offer(
                SpecialOfferType.TWO_FOR_AMOUNT,
                2.25,
                List.of(product("bread"))
        ));

        CheckoutCounter counter = new CheckoutCounter(myOffersMap, cart);

        // Buy 2, get 1 for 50% off
        cart.addItemInCart(product("bread"), 3.0);
        Receipt r = counter.checkout(customerCopy);

        ReceiptItem item = cart.items().get(0);
        assertEquals(product("bread"), item.getProduct());
        assertEquals(1.5, item.getPrice(), 0.01);
        assertEquals(4.5, item.getTotalPrice(), 0.01);
        assertEquals(3.0, item.getQuantity(), 0.01);

        assertEquals(4.5, counter.getReceipt().getTotalPrice(), 0.01);
        assertEquals(0.75, counter.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(3.75, counter.getReceipt().getTotalPriceAfterDiscount(), 0.01);
    }

    @Test
    public void testCouponApplyOnlyOnce() {
        ShoppingCart cart = new ShoppingCart();
        CheckoutCounter counter = new CheckoutCounter(offersMap, cart);
        Customer customerCopy = new Customer(customer);
        // Buy 2, get 1 for 50% off but only once
        cart.addItemInCart(product("bread"), 6.0);
        counter.checkout(customerCopy);

        ReceiptItem item = cart.items().get(0);
        assertEquals(product("bread"), item.getProduct());
        assertEquals(1.5, item.getPrice(), 0.01);
        assertEquals(9, item.getTotalPrice(), 0.01);
        assertEquals(6.0, item.getQuantity(), 0.01);

        assertEquals(9, counter.getReceipt().getTotalPrice(), 0.01);
        assertEquals(0.75, counter.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(8.25, counter.getReceipt().getTotalPriceAfterDiscount(), 0.01);
    }

    @Test
    /**
     * Test that a coupon is not applied if a part of the product quantity has already been used in a bundle offer.
     * Here milk is in a bundle offer with ham, and bread
     * Milk is also in TWO_FOR_AMOUNT for 2.37
     */
    public void testCouponUnusedAfterBundle() {
        ShoppingCart cart = new ShoppingCart();
        CheckoutCounter counter = new CheckoutCounter(offersMap, cart);
        Customer customerCopy = new Customer(customer);
        int couponBeforeCheckout = customerCopy.getUnusedCouponsCount();

        cart.addItemInCart(product("milk"), 2.0);
        cart.addItemInCart(product("ham"), 1.0);
        cart.addItemInCart(product("bread"), 1.0);
        Receipt r = counter.checkout(customerCopy);

        Double bundlePrice =  (1.37 + 4.00 + 1.50);
        Double bundleDiscount = bundlePrice*0.1;

        Double totalPriceBeforeDiscount = (1.37*2) + 4.00 + 1.50;
        Double totalPriceAfterDiscount = totalPriceBeforeDiscount-bundleDiscount;

        ReceiptItem item = cart.items().get(0);
        assertEquals(product("milk"), item.getProduct());
        assertEquals(1.37, item.getPrice(), 0.01);
        assertEquals(2.74, item.getTotalPrice(), 0.01);
        assertEquals(2.0, item.getQuantity(), 0.01);

        assertEquals(totalPriceBeforeDiscount, counter.getReceipt().getTotalPrice(), 0.01);
        assertEquals(bundleDiscount, counter.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(totalPriceAfterDiscount, counter.getReceipt().getTotalPriceAfterDiscount(), 0.01);

        assertEquals(couponBeforeCheckout, customerCopy.getUnusedCouponsCount());
    }

    @Test
    public void testCouponTooOldToBeUsed() {
        ShoppingCart cart = new ShoppingCart();
        CheckoutCounter counter = new CheckoutCounter(offersMap, cart);

        Map<Product, Coupon> coupons = new HashMap<>();
        coupons.put(
                product("bread"),
                new Coupon(
                        product("bread"),
                        java.time.LocalDate.now().minusDays(10),
                        java.time.LocalDate.now().minusDays(1),
                        2,
                        1,
                        0.5
                )
        );
        Customer expiredCustomer = new Customer(2, coupons);

        // Buy 3 bread, coupon is expired and should not be applied
        cart.addItemInCart(product("bread"), 3.0);
        counter.checkout(expiredCustomer);

        ReceiptItem item = cart.items().get(0);
        assertEquals(product("bread"), item.getProduct());
        assertEquals(1.5, item.getPrice(), 0.01);
        assertEquals(4.5, item.getTotalPrice(), 0.01);
        assertEquals(3.0, item.getQuantity(), 0.01);

        assertEquals(4.5, counter.getReceipt().getTotalPrice(), 0.01);
        assertEquals(0.0, counter.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(4.5, counter.getReceipt().getTotalPriceAfterDiscount(), 0.01);

        // coupon exists but is expired so still unused
        assertEquals(1, expiredCustomer.getUnusedCouponsCount());
    }

}
