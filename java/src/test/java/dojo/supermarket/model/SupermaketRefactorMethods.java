package dojo.supermarket.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SupermaketRefactorMethods {

    @Test
    void testsUsingDiscountCatalog() {

        Product apples = new Product("apples", ProductUnit.KILO, 1.5);
        Product toothbrush = new Product("toothbrush", ProductUnit.EACH, 0.5);

        List<DefaultOffer> offers = new ArrayList<>();

        ArrayList<Product> products = new ArrayList<>();
        products.add(apples);
        products.add(toothbrush);
        offers.add(new DefaultOffer(SpecialOfferType.BUNDLE, 10, products));

        ShoppingCart cart = new ShoppingCart(offers);
        cart.addItemInCart(apples, 2);
        cart.addItemInCart(toothbrush, 2);

        cart.handleAllOffers();
        cart.goToCheckout();

        assertEquals(3.6, cart.getReceipt().getTotalPriceAfterDiscount(), 0.01);
        assertEquals(2, cart.getReceiptItems().size());

        assertEquals(0.4, cart.getReceipt().getTotalDiscounts(), 0.01);

        ReceiptItem receiptItem = cart.getReceiptItems().get(0);
        assertEquals(apples, receiptItem.getProduct());
        assertEquals(1.5, receiptItem.getPrice());
        assertEquals(3, receiptItem.getTotalPrice());
        assertEquals(2, receiptItem.getQuantity());

        receiptItem = cart.getReceiptItems().get(1);
        assertEquals(toothbrush, receiptItem.getProduct());
        assertEquals(0.5, receiptItem.getPrice());
        assertEquals(1, receiptItem.getTotalPrice());
        assertEquals(2, receiptItem.getQuantity());

    }

    @Test
    void testForMultipleOffers() {

        Product apples = new Product("apples", ProductUnit.KILO, 1.5);
        Product toothbrush = new Product("toothbrush", ProductUnit.EACH, 0.5);
        Product computer = new Product("computer", ProductUnit.EACH, 7.0);

        List<DefaultOffer> offers = new ArrayList<>();

        ArrayList<Product> products = new ArrayList<>();
        products.add(apples);
        products.add(toothbrush);
        offers.add(new DefaultOffer(SpecialOfferType.BUNDLE, 10, products));
        offers.add(new DefaultOffer(SpecialOfferType.THREE_FOR_TWO, 0, new ArrayList<Product>(List.of(computer))));

        ShoppingCart cart = new ShoppingCart(offers);
        cart.addItemInCart(apples, 2);
        cart.addItemInCart(toothbrush, 2);
        cart.addItemInCart(computer, 3);

        cart.handleAllOffers();
        cart.goToCheckout();

        assertEquals(3, cart.getReceiptItems().size());

        ReceiptItem receiptItem = cart.getReceiptItems().get(0);
        assertEquals(apples, receiptItem.getProduct());
        assertEquals(1.5, receiptItem.getPrice());
        assertEquals(3, receiptItem.getTotalPrice());
        assertEquals(2, receiptItem.getQuantity());

        receiptItem = cart.getReceiptItems().get(1);
        assertEquals(toothbrush, receiptItem.getProduct());
        assertEquals(0.5, receiptItem.getPrice());
        assertEquals(1, receiptItem.getTotalPrice());
        assertEquals(2, receiptItem.getQuantity());

        receiptItem = cart.getReceiptItems().get(2);
        assertEquals(computer, receiptItem.getProduct());
        assertEquals(7, receiptItem.getPrice());
        assertEquals(21, receiptItem.getTotalPrice());
        assertEquals(3, receiptItem.getQuantity());

        assertEquals(25, cart.getReceipt().getTotalPrice2(), 0.01);
        assertEquals(7.4, cart.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(17.6, cart.getReceipt().getTotalPriceAfterDiscount(), 0.01);

    }

    @Test
    void testForBundleKilo() {
        Product tomatoes = new Product("tomatoes", ProductUnit.KILO, 1.5);
        Product carrots = new Product("carrots", ProductUnit.EACH, 2.20);

        List<DefaultOffer> offers = new ArrayList<>();

        ArrayList<Product> products = new ArrayList<>();
        products.add(tomatoes);
        products.add(carrots);
        offers.add(new DefaultOffer(SpecialOfferType.BUNDLE, 10, products));

        ShoppingCart cart = new ShoppingCart(offers);
        cart.addItemInCart(tomatoes, 1.5);
        cart.addItemInCart(carrots, 1.5);

        cart.handleAllOffers();
        cart.goToCheckout();

        assertEquals(2, cart.getReceiptItems().size());


        ReceiptItem receiptItem = cart.getReceiptItems().get(0);
        assertEquals(tomatoes, receiptItem.getProduct());
        assertEquals(1.5, receiptItem.getPrice());
        assertEquals(2.25, receiptItem.getTotalPrice());
        assertEquals(1.5, receiptItem.getQuantity());

        receiptItem = cart.getReceiptItems().get(1);
        assertEquals(carrots, receiptItem.getProduct());
        assertEquals(2.20, receiptItem.getPrice());
        assertEquals(3.30, receiptItem.getTotalPrice(), 0.01);
        assertEquals(1.5, receiptItem.getQuantity());

        assertEquals(0.37, cart.getReceipt().getTotalDiscounts());
        assertEquals(5.18, cart.getReceipt().getTotalPriceAfterDiscount(), 0.01);
    }

    // ========= Adapted tests from SupermarketTest =========

    @Test
    void tenPercentDiscountNoDiscount() {
        Product toothbrush = new Product("toothbrush", ProductUnit.EACH, 0.99);
        Product apples = new Product("apples", ProductUnit.KILO, 1.99);

        List<DefaultOffer> offers = new ArrayList<>();
        offers.add(new DefaultOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT, 10.0,
                new ArrayList<Product>(List.of(toothbrush))));

        ShoppingCart cart = new ShoppingCart(offers);
        cart.addItemInCart(apples, 2.5);

        cart.handleAllOffers();
        cart.goToCheckout();

        double expectedTotal = 2.5 * 1.99;

        assertEquals(expectedTotal, cart.getReceipt().getTotalPrice2(), 0.01);
        assertEquals(0.0, cart.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(expectedTotal, cart.getReceipt().getTotalPriceAfterDiscount(), 0.01);

        assertEquals(1, cart.getReceiptItems().size());
        ReceiptItem receiptItem = cart.getReceiptItems().get(0);
        assertEquals(apples, receiptItem.getProduct());
        assertEquals(1.99, receiptItem.getPrice());
        assertEquals(expectedTotal, receiptItem.getTotalPrice(), 0.01);
        assertEquals(2.5, receiptItem.getQuantity(), 0.01);
    }

    @Test
    void tenPercentDiscount() {
        Product toothbrush = new Product("toothbrush", ProductUnit.EACH, 0.99);
        Product apples = new Product("apples", ProductUnit.KILO, 1.99);

        List<DefaultOffer> offers = new ArrayList<>();
        offers.add(new DefaultOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT, 10.0,
                new ArrayList<Product>(List.of(toothbrush))));

        ShoppingCart cart = new ShoppingCart(offers);
        cart.addItemInCart(apples, 2.5);
        cart.addItemInCart(toothbrush, 3);

        cart.handleAllOffers();
        cart.goToCheckout();

        double applesTotal = 2.5 * 1.99;
        double toothbrushTotal = 3 * 0.99;
        double totalBeforeDiscount = applesTotal + toothbrushTotal;
        double discount = toothbrushTotal * 0.10;
        double totalAfterDiscount = totalBeforeDiscount - discount;

        assertEquals(totalBeforeDiscount, cart.getReceipt().getTotalPrice2(), 0.01);
        assertEquals(discount, cart.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(totalAfterDiscount, cart.getReceipt().getTotalPriceAfterDiscount(), 0.01);

        assertEquals(2, cart.getReceiptItems().size());

        ReceiptItem receiptItem = cart.getReceiptItems().get(0);
        assertEquals(apples, receiptItem.getProduct());
        assertEquals(1.99, receiptItem.getPrice());
        assertEquals(applesTotal, receiptItem.getTotalPrice(), 0.01);
        assertEquals(2.5, receiptItem.getQuantity(), 0.01);

        receiptItem = cart.getReceiptItems().get(1);
        assertEquals(toothbrush, receiptItem.getProduct());
        assertEquals(0.99, receiptItem.getPrice());
        assertEquals(toothbrushTotal, receiptItem.getTotalPrice(), 0.01);
        assertEquals(3, receiptItem.getQuantity(), 0.01);
    }

    @Test
    void threeForTwoDiscount() {
        Product ps5 = new Product("PS5", ProductUnit.EACH, 200.0);

        List<DefaultOffer> offers = new ArrayList<>();
        offers.add(new DefaultOffer(SpecialOfferType.THREE_FOR_TWO, 0,
                new ArrayList<Product>(List.of(ps5))));

        ShoppingCart cart = new ShoppingCart(offers);
        cart.addItemInCart(ps5, 3);

        cart.handleAllOffers();
        cart.goToCheckout();

        double totalBeforeDiscount = 3 * 200.0;
        double discount = 200.0;
        double totalAfterDiscount = totalBeforeDiscount - discount;

        assertEquals(totalBeforeDiscount, cart.getReceipt().getTotalPrice2(), 0.01);
        assertEquals(discount, cart.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(totalAfterDiscount, cart.getReceipt().getTotalPriceAfterDiscount(), 0.01);

        assertEquals(1, cart.getReceiptItems().size());
        ReceiptItem receiptItem = cart.getReceiptItems().get(0);
        assertEquals(ps5, receiptItem.getProduct());
        assertEquals(200.0, receiptItem.getPrice());
        assertEquals(totalBeforeDiscount, receiptItem.getTotalPrice(), 0.01);
        assertEquals(3, receiptItem.getQuantity(), 0.01);
    }

    @Test
    void threeForTwoDiscountFourProducts() {
        Product ps5 = new Product("PS5", ProductUnit.EACH, 200.0);

        List<DefaultOffer> offers = new ArrayList<>();
        offers.add(new DefaultOffer(SpecialOfferType.THREE_FOR_TWO, 0,
                new ArrayList<Product>(List.of(ps5))));

        ShoppingCart cart = new ShoppingCart(offers);
        cart.addItemInCart(ps5, 4);

        cart.handleAllOffers();
        cart.goToCheckout();

        double totalBeforeDiscount = 4 * 200.0;
        double discount = 200.0;
        double totalAfterDiscount = totalBeforeDiscount - discount;

        assertEquals(totalBeforeDiscount, cart.getReceipt().getTotalPrice2(), 0.01);
        assertEquals(discount, cart.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(totalAfterDiscount, cart.getReceipt().getTotalPriceAfterDiscount(), 0.01);

        assertEquals(1, cart.getReceiptItems().size());
        ReceiptItem receiptItem = cart.getReceiptItems().get(0);
        assertEquals(ps5, receiptItem.getProduct());
        assertEquals(200.0, receiptItem.getPrice());
        assertEquals(totalBeforeDiscount, receiptItem.getTotalPrice(), 0.01);
        assertEquals(4, receiptItem.getQuantity(), 0.01);
    }

    @Test
    void threeForTwoDiscountTimesTwo() {
        Product ps5 = new Product("PS5", ProductUnit.EACH, 200.0);

        List<DefaultOffer> offers = new ArrayList<>();
        offers.add(new DefaultOffer(SpecialOfferType.THREE_FOR_TWO, 0,
                new ArrayList<Product>(List.of(ps5))));

        ShoppingCart cart = new ShoppingCart(offers);
        cart.addItemInCart(ps5, 7);

        cart.handleAllOffers();
        cart.goToCheckout();

        double totalBeforeDiscount = 7 * 200.0;
        double discount = 2 * 200.0; // two full 3-for-2 groups
        double totalAfterDiscount = totalBeforeDiscount - discount;

        assertEquals(totalBeforeDiscount, cart.getReceipt().getTotalPrice2(), 0.01);
        assertEquals(discount, cart.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(totalAfterDiscount, cart.getReceipt().getTotalPriceAfterDiscount(), 0.01);

        assertEquals(1, cart.getReceiptItems().size());
        ReceiptItem receiptItem = cart.getReceiptItems().get(0);
        assertEquals(ps5, receiptItem.getProduct());
        assertEquals(200.0, receiptItem.getPrice());
        assertEquals(totalBeforeDiscount, receiptItem.getTotalPrice(), 0.01);
        assertEquals(7, receiptItem.getQuantity(), 0.01);
    }

    @Test
    void twoForAmount() {
        Product milk = new Product("Milk", ProductUnit.EACH, 1.37);

        List<DefaultOffer> offers = new ArrayList<>();
        offers.add(new DefaultOffer(SpecialOfferType.TWO_FOR_AMOUNT, 2.37,
                new ArrayList<Product>(List.of(milk))));

        ShoppingCart cart = new ShoppingCart(offers);
        cart.addItemInCart(milk, 2);

        cart.handleAllOffers();
        cart.goToCheckout();

        double totalBeforeDiscount = 2 * 1.37;
        double totalAfterDiscount = 2.37;
        double discount = totalBeforeDiscount - totalAfterDiscount;

        assertEquals(totalBeforeDiscount, cart.getReceipt().getTotalPrice2(), 0.01);
        assertEquals(discount, cart.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(totalAfterDiscount, cart.getReceipt().getTotalPriceAfterDiscount(), 0.01);

        assertEquals(1, cart.getReceiptItems().size());
        ReceiptItem receiptItem = cart.getReceiptItems().get(0);
        assertEquals(milk, receiptItem.getProduct());
        assertEquals(1.37, receiptItem.getPrice());
        assertEquals(totalBeforeDiscount, receiptItem.getTotalPrice(), 0.01);
        assertEquals(2, receiptItem.getQuantity(), 0.01);
    }

    @Test
    void twoForAmountThreeProduct() {
        Product milk = new Product("Milk", ProductUnit.EACH, 1.37);

        List<DefaultOffer> offers = new ArrayList<>();
        offers.add(new DefaultOffer(SpecialOfferType.TWO_FOR_AMOUNT, 2.37,
                new ArrayList<Product>(List.of(milk))));

        ShoppingCart cart = new ShoppingCart(offers);
        cart.addItemInCart(milk, 3);

        cart.handleAllOffers();
        cart.goToCheckout();

        double totalBeforeDiscount = 3 * 1.37;
        double totalAfterDiscount = 3.74; // 2 for 2.37 + 1 * 1.37
        double discount = totalBeforeDiscount - totalAfterDiscount;

        assertEquals(totalBeforeDiscount, cart.getReceipt().getTotalPrice2(), 0.01);
        assertEquals(discount, cart.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(totalAfterDiscount, cart.getReceipt().getTotalPriceAfterDiscount(), 0.01);

        assertEquals(1, cart.getReceiptItems().size());
        ReceiptItem receiptItem = cart.getReceiptItems().get(0);
        assertEquals(milk, receiptItem.getProduct());
        assertEquals(1.37, receiptItem.getPrice());
        assertEquals(totalBeforeDiscount, receiptItem.getTotalPrice(), 0.01);
        assertEquals(3, receiptItem.getQuantity(), 0.01);
    }

    @Test
    void twoForAmountTwoTime() {
        Product milk = new Product("Milk", ProductUnit.EACH, 1.37);

        List<DefaultOffer> offers = new ArrayList<>();
        offers.add(new DefaultOffer(SpecialOfferType.TWO_FOR_AMOUNT, 2.37,
                new ArrayList<Product>(List.of(milk))));

        ShoppingCart cart = new ShoppingCart(offers);
        cart.addItemInCart(milk, 4);

        cart.handleAllOffers();
        cart.goToCheckout();

        double totalBeforeDiscount = 4 * 1.37;
        double totalAfterDiscount = 4.74; // 2 * (2 for 2.37)
        double discount = totalBeforeDiscount - totalAfterDiscount;

        assertEquals(totalBeforeDiscount, cart.getReceipt().getTotalPrice2(), 0.01);
        assertEquals(discount, cart.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(totalAfterDiscount, cart.getReceipt().getTotalPriceAfterDiscount(), 0.01);

        assertEquals(1, cart.getReceiptItems().size());
        ReceiptItem receiptItem = cart.getReceiptItems().get(0);
        assertEquals(milk, receiptItem.getProduct());
        assertEquals(1.37, receiptItem.getPrice());
        assertEquals(totalBeforeDiscount, receiptItem.getTotalPrice(), 0.01);
        assertEquals(4, receiptItem.getQuantity(), 0.01);
    }

    @Test
    void fiveForAmount() {
        Product steak = new Product("Steak", ProductUnit.EACH, 2.42);

        List<DefaultOffer> offers = new ArrayList<>();
        offers.add(new DefaultOffer(SpecialOfferType.FIVE_FOR_AMOUNT, 10.0,
                new ArrayList<Product>(List.of(steak))));

        ShoppingCart cart = new ShoppingCart(offers);
        cart.addItemInCart(steak, 5);

        cart.handleAllOffers();
        cart.goToCheckout();

        double totalBeforeDiscount = 5 * 2.42;
        double totalAfterDiscount = 10.0;
        double discount = totalBeforeDiscount - totalAfterDiscount;

        assertEquals(totalBeforeDiscount, cart.getReceipt().getTotalPrice2(), 0.01);
        assertEquals(discount, cart.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(totalAfterDiscount, cart.getReceipt().getTotalPriceAfterDiscount(), 0.01);

        assertEquals(1, cart.getReceiptItems().size());
        ReceiptItem receiptItem = cart.getReceiptItems().get(0);
        assertEquals(steak, receiptItem.getProduct());
        assertEquals(2.42, receiptItem.getPrice());
        assertEquals(totalBeforeDiscount, receiptItem.getTotalPrice(), 0.01);
        assertEquals(5, receiptItem.getQuantity(), 0.01);
    }

    @Test
    void fiveForAmountSixProduct() {
        Product steak = new Product("Steak", ProductUnit.EACH, 2.42);

        List<DefaultOffer> offers = new ArrayList<>();
        offers.add(new DefaultOffer(SpecialOfferType.FIVE_FOR_AMOUNT, 10.0,
                new ArrayList<Product>(List.of(steak))));

        ShoppingCart cart = new ShoppingCart(offers);
        cart.addItemInCart(steak, 6);

        cart.handleAllOffers();
        cart.goToCheckout();

        double totalBeforeDiscount = 6 * 2.42;
        double totalAfterDiscount = 12.42; // 5 for 10 + 1 * 2.42
        double discount = totalBeforeDiscount - totalAfterDiscount;

        assertEquals(totalBeforeDiscount, cart.getReceipt().getTotalPrice2(), 0.01);
        assertEquals(discount, cart.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(totalAfterDiscount, cart.getReceipt().getTotalPriceAfterDiscount(), 0.01);

        assertEquals(1, cart.getReceiptItems().size());
        ReceiptItem receiptItem = cart.getReceiptItems().get(0);
        assertEquals(steak, receiptItem.getProduct());
        assertEquals(2.42, receiptItem.getPrice());
        assertEquals(totalBeforeDiscount, receiptItem.getTotalPrice(), 0.01);
        assertEquals(6, receiptItem.getQuantity(), 0.01);
    }
}
