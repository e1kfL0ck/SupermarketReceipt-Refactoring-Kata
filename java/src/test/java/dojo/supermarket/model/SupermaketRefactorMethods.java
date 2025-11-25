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

        assertEquals(25, cart.getReceipt().getTotalPrice(), 0.01);
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

        assertEquals(expectedTotal, cart.getReceipt().getTotalPrice(), 0.01);
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

        assertEquals(totalBeforeDiscount, cart.getReceipt().getTotalPrice(), 0.01);
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

        String result = ReceiptDisplay.printReceipt(cart);
        System.out.println(result);
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

        assertEquals(totalBeforeDiscount, cart.getReceipt().getTotalPrice(), 0.01);
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

        assertEquals(totalBeforeDiscount, cart.getReceipt().getTotalPrice(), 0.01);
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

        assertEquals(totalBeforeDiscount, cart.getReceipt().getTotalPrice(), 0.01);
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

        assertEquals(totalBeforeDiscount, cart.getReceipt().getTotalPrice(), 0.01);
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

        assertEquals(totalBeforeDiscount, cart.getReceipt().getTotalPrice(), 0.01);
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

        assertEquals(totalBeforeDiscount, cart.getReceipt().getTotalPrice(), 0.01);
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

        assertEquals(totalBeforeDiscount, cart.getReceipt().getTotalPrice(), 0.01);
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

        assertEquals(totalBeforeDiscount, cart.getReceipt().getTotalPrice(), 0.01);
        assertEquals(discount, cart.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(totalAfterDiscount, cart.getReceipt().getTotalPriceAfterDiscount(), 0.01);

        assertEquals(1, cart.getReceiptItems().size());
        ReceiptItem receiptItem = cart.getReceiptItems().get(0);
        assertEquals(steak, receiptItem.getProduct());
        assertEquals(2.42, receiptItem.getPrice());
        assertEquals(totalBeforeDiscount, receiptItem.getTotalPrice(), 0.01);
        assertEquals(6, receiptItem.getQuantity(), 0.01);
    }

    @Test
    void complexScenario_multipleOffersKiloAndEach_case1() {
        Product apples = new Product("apples", ProductUnit.KILO, 2.0);
        Product rice = new Product("rice", ProductUnit.KILO, 1.5);
        Product milk = new Product("milk", ProductUnit.EACH, 1.2);
        Product chocolate = new Product("chocolate", ProductUnit.EACH, 2.0);

        // Offers:
        //  - 10% bundle discount on 1kg apples + 1kg rice (kilo + kilo)
        //  - 2-for-amount on milk (2 for 2.0)
        //  - 10% discount on chocolate
        List<DefaultOffer> offers = new ArrayList<>();
        offers.add(new DefaultOffer(
                SpecialOfferType.BUNDLE,
                10.0,
                new ArrayList<>(List.of(apples, rice))
        ));
        offers.add(new DefaultOffer(
                SpecialOfferType.TWO_FOR_AMOUNT,
                2.0,
                new ArrayList<>(List.of(milk))
        ));
        offers.add(new DefaultOffer(
                SpecialOfferType.TEN_PERCENT_DISCOUNT,
                10.0,
                new ArrayList<>(List.of(chocolate))
        ));

        ShoppingCart cart = new ShoppingCart(offers);

        // Quantities:
        //  apples 2.0kg, rice 1.0kg, milk 3, chocolate 4
        cart.addItemInCart(apples, 2.0);
        cart.addItemInCart(rice, 1.0);
        cart.addItemInCart(milk, 3);
        cart.addItemInCart(chocolate, 4);

        cart.handleAllOffers();
        cart.goToCheckout();

        double applesTotal = 2.0 * 2.0;  // 4.0
        double riceTotal = 1.0 * 1.5;    // 1.5
        double milkTotal = 3 * 1.2;      // 3.6
        double chocolateTotal = 4 * 2.0; // 8.0

        double totalBeforeDiscount = applesTotal + riceTotal + milkTotal + chocolateTotal; // 17.1

        // Bundle (apples + rice): 1 full bundle -> (2.0 + 1.5) * 10% = 0.35
        double bundleDiscount = (2.0 + 1.5) * 0.10; // 0.35

        // Milk: 2 for 2.0 -> normal 2 * 1.2 = 2.4, offer 2.0 => 0.4 discount
        // There are 3 milks -> 2 in offer, 1 full price
        double milkDiscount = milkTotal - (2.0 + 1.2); // 0.4

        // Chocolate: 10% discount on all
        double chocolateDiscount = chocolateTotal * 0.10; // 0.8

        double totalDiscount = bundleDiscount + milkDiscount + chocolateDiscount; // 1.55
        double totalAfterDiscount = totalBeforeDiscount - totalDiscount;          // 15.55

        assertEquals(totalBeforeDiscount, cart.getReceipt().getTotalPrice(), 0.01);
        assertEquals(totalDiscount, cart.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(totalAfterDiscount, cart.getReceipt().getTotalPriceAfterDiscount(), 0.01);

        assertEquals(4, cart.getReceiptItems().size());

        ReceiptItem item = cart.getReceiptItems().get(0);
        assertEquals(apples, item.getProduct());
        assertEquals(2.0, item.getPrice(), 0.01);
        assertEquals(applesTotal, item.getTotalPrice(), 0.01);
        assertEquals(2.0, item.getQuantity(), 0.01);

        item = cart.getReceiptItems().get(1);
        assertEquals(rice, item.getProduct());
        assertEquals(1.5, item.getPrice(), 0.01);
        assertEquals(riceTotal, item.getTotalPrice(), 0.01);
        assertEquals(1.0, item.getQuantity(), 0.01);

        item = cart.getReceiptItems().get(2);
        assertEquals(milk, item.getProduct());
        assertEquals(1.2, item.getPrice(), 0.01);
        assertEquals(milkTotal, item.getTotalPrice(), 0.01);
        assertEquals(3, item.getQuantity(), 0.01);

        item = cart.getReceiptItems().get(3);
        assertEquals(chocolate, item.getProduct());
        assertEquals(2.0, item.getPrice(), 0.01);
        assertEquals(chocolateTotal, item.getTotalPrice(), 0.01);
        assertEquals(4, item.getQuantity(), 0.01);
    }

    @Test
    void complexScenario_multipleOffersKiloAndEach_case2() {
        Product cheese = new Product("cheese", ProductUnit.KILO, 9.0);
        Product ham = new Product("ham", ProductUnit.EACH, 4.0);
        Product bread = new Product("bread", ProductUnit.EACH, 1.5);
        Product wine = new Product("wine", ProductUnit.EACH, 10.0);
        Product grapes = new Product("grapes", ProductUnit.KILO, 3.0);

        // Offers:
        //  - 10% bundle on (cheese kilo + ham each + bread each), applied per full set
        //  - 2-for-amount on wine (2 for 15)
        //  - 10% discount on grapes
        List<DefaultOffer> offers = new ArrayList<>();
        offers.add(new DefaultOffer(
                SpecialOfferType.BUNDLE,
                10.0,
                new ArrayList<>(List.of(cheese, ham, bread))
        ));
        offers.add(new DefaultOffer(
                SpecialOfferType.TWO_FOR_AMOUNT,
                15.0,
                new ArrayList<>(List.of(wine))
        ));
        offers.add(new DefaultOffer(
                SpecialOfferType.TEN_PERCENT_DISCOUNT,
                10.0,
                new ArrayList<>(List.of(grapes))
        ));

        ShoppingCart cart = new ShoppingCart(offers);

        // Quantities:
        //  cheese 2.0kg, ham 2, bread 5, wine 2, grapes 1.2kg
        cart.addItemInCart(cheese, 2.0);
        cart.addItemInCart(ham, 2);
        cart.addItemInCart(bread, 5);
        cart.addItemInCart(wine, 2);
        cart.addItemInCart(grapes, 1.2);

        cart.handleAllOffers();
        cart.goToCheckout();

        double cheeseTotal = 2.0 * 9.0; // 18.0
        double hamTotal = 2 * 4.0;      // 8.0
        double breadTotal = 5 * 1.5;    // 7.5
        double wineTotal = 2 * 10.0;    // 20.0
        double grapesTotal = 1.2 * 3.0; // 3.6

        double totalBeforeDiscount = cheeseTotal + hamTotal + breadTotal + wineTotal + grapesTotal; // 57.1

        // Bundle: min quantities among (cheese 2kg, ham 2, bread 5) => 2 full bundles
        // One bundle unit price: 9.0 + 4.0 + 1.5 = 14.5
        // 10% of 14.5 = 1.45 -> times 2 = 2.9
        double bundleDiscount = 2 * (9.0 + 4.0 + 1.5) * 0.10; // 2.9

        // Wine: 2 for 15 -> normal 20, offer 15 => 5 discount
        double wineDiscount = wineTotal - 15.0; // 5.0

        // Grapes: 10% on all
        double grapesDiscount = grapesTotal * 0.10; // 0.36

        double totalDiscount = bundleDiscount + wineDiscount + grapesDiscount; // 8.26
        double totalAfterDiscount = totalBeforeDiscount - totalDiscount;       // 48.84

        assertEquals(totalBeforeDiscount, cart.getReceipt().getTotalPrice(), 0.01);
        assertEquals(totalDiscount, cart.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(totalAfterDiscount, cart.getReceipt().getTotalPriceAfterDiscount(), 0.01);

        assertEquals(5, cart.getReceiptItems().size());

        ReceiptItem item = cart.getReceiptItems().get(0);
        assertEquals(cheese, item.getProduct());
        assertEquals(9.0, item.getPrice(), 0.01);
        assertEquals(cheeseTotal, item.getTotalPrice(), 0.01);
        assertEquals(2.0, item.getQuantity(), 0.01);

        item = cart.getReceiptItems().get(1);
        assertEquals(ham, item.getProduct());
        assertEquals(4.0, item.getPrice(), 0.01);
        assertEquals(hamTotal, item.getTotalPrice(), 0.01);
        assertEquals(2, item.getQuantity(), 0.01);

        item = cart.getReceiptItems().get(2);
        assertEquals(bread, item.getProduct());
        assertEquals(1.5, item.getPrice(), 0.01);
        assertEquals(breadTotal, item.getTotalPrice(), 0.01);
        assertEquals(5, item.getQuantity(), 0.01);

        item = cart.getReceiptItems().get(3);
        assertEquals(wine, item.getProduct());
        assertEquals(10.0, item.getPrice(), 0.01);
        assertEquals(wineTotal, item.getTotalPrice(), 0.01);
        assertEquals(2, item.getQuantity(), 0.01);

        item = cart.getReceiptItems().get(4);
        assertEquals(grapes, item.getProduct());
        assertEquals(3.0, item.getPrice(), 0.01);
        assertEquals(grapesTotal, item.getTotalPrice(), 0.01);
        assertEquals(1.2, item.getQuantity(), 0.01);
    }

    @Test
    void complexScenario_multipleOffersKiloAndEach_case3() {
        Product bananas = new Product("bananas", ProductUnit.KILO, 1.8);
        Product apples = new Product("apples", ProductUnit.KILO, 2.2);
        Product cereal = new Product("cereal", ProductUnit.EACH, 3.5);
        Product soda = new Product("soda", ProductUnit.EACH, 1.2);
        Product chips = new Product("chips", ProductUnit.EACH, 2.0);

        // Offers:
        //  - 10% bundle on (bananas kilo + apples kilo), fractional quantities
        //  - 10% discount on cereal
        //  - 3-for-2 on soda
        //  - 5-for-amount on chips (5 for 8.0)
        List<DefaultOffer> offers = new ArrayList<>();
        offers.add(new DefaultOffer(
                SpecialOfferType.BUNDLE,
                10.0,
                new ArrayList<>(List.of(bananas, apples))
        ));
        offers.add(new DefaultOffer(
                SpecialOfferType.TEN_PERCENT_DISCOUNT,
                10.0,
                new ArrayList<>(List.of(cereal))
        ));
        offers.add(new DefaultOffer(
                SpecialOfferType.THREE_FOR_TWO,
                0,
                new ArrayList<>(List.of(soda))
        ));
        offers.add(new DefaultOffer(
                SpecialOfferType.FIVE_FOR_AMOUNT,
                8.0,
                new ArrayList<>(List.of(chips))
        ));

        ShoppingCart cart = new ShoppingCart(offers);

        // Quantities:
        //  bananas 2.5kg, apples 1.0kg, cereal 2, soda 7, chips 6
        cart.addItemInCart(bananas, 2.5);
        cart.addItemInCart(apples, 1.0);
        cart.addItemInCart(cereal, 2);
        cart.addItemInCart(soda, 7);
        cart.addItemInCart(chips, 6);

        cart.handleAllOffers();
        cart.goToCheckout();

        double bananasTotal = 2.5 * 1.8; // 4.5
        double applesTotal = 1.0 * 2.2;  // 2.2
        double cerealTotal = 2 * 3.5;    // 7.0
        double sodaTotal = 7 * 1.2;      // 8.4
        double chipsTotal = 6 * 2.0;     // 12.0

        double totalBeforeDiscount = bananasTotal + applesTotal + cerealTotal + sodaTotal + chipsTotal; // 34.1

        // Bundle bananas+apples: min(2.5, 1.0) -> 1 full bundle
        // one bundle price: 1.8 + 2.2 = 4.0 -> 10% = 0.4
        double bundleDiscount = (1.8 + 2.2) * 0.10; // 0.4

        // Cereal: 10% on all
        double cerealDiscount = cerealTotal * 0.10; // 0.7

        // Soda: 3-for-2 on 7 units -> floor(7/3)=2 groups
        // Normal: 7 * 1.2 = 8.4
        // Offer: 2 groups * (2 * 1.2) + 1 * 1.2 = 4.8 + 1.2 = 6.0
        // Discount: 2.4
        double sodaDiscount = 2.4;

        // Chips: 5-for-8.0 with 6 units
        // Normal: 6 * 2.0 = 12
        // Offer: 5 for 8 + 1 * 2 = 10
        // Discount: 2
        double chipsDiscount = 2.0;

        double totalDiscount = bundleDiscount + cerealDiscount + sodaDiscount + chipsDiscount; // 5.5
        double totalAfterDiscount = totalBeforeDiscount - totalDiscount;                       // 28.6

        assertEquals(totalBeforeDiscount, cart.getReceipt().getTotalPrice(), 0.01);
        assertEquals(totalDiscount, cart.getReceipt().getTotalDiscounts(), 0.01);
        assertEquals(totalAfterDiscount, cart.getReceipt().getTotalPriceAfterDiscount(), 0.01);

        assertEquals(5, cart.getReceiptItems().size());

        ReceiptItem item = cart.getReceiptItems().get(0);
        assertEquals(bananas, item.getProduct());
        assertEquals(1.8, item.getPrice(), 0.01);
        assertEquals(bananasTotal, item.getTotalPrice(), 0.01);
        assertEquals(2.5, item.getQuantity(), 0.01);

        item = cart.getReceiptItems().get(1);
        assertEquals(apples, item.getProduct());
        assertEquals(2.2, item.getPrice(), 0.01);
        assertEquals(applesTotal, item.getTotalPrice(), 0.01);
        assertEquals(1.0, item.getQuantity(), 0.01);

        item = cart.getReceiptItems().get(2);
        assertEquals(cereal, item.getProduct());
        assertEquals(3.5, item.getPrice(), 0.01);
        assertEquals(cerealTotal, item.getTotalPrice(), 0.01);
        assertEquals(2, item.getQuantity(), 0.01);

        item = cart.getReceiptItems().get(3);
        assertEquals(soda, item.getProduct());
        assertEquals(1.2, item.getPrice(), 0.01);
        assertEquals(sodaTotal, item.getTotalPrice(), 0.01);
        assertEquals(7, item.getQuantity(), 0.01);

        item = cart.getReceiptItems().get(4);
        assertEquals(chips, item.getProduct());
        assertEquals(2.0, item.getPrice(), 0.01);
        assertEquals(chipsTotal, item.getTotalPrice(), 0.01);
        assertEquals(6, item.getQuantity(), 0.01);
    }

}
