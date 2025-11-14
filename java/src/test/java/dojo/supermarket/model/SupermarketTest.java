package dojo.supermarket.model;

import org.approvaltests.Approvals;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SupermarketTest {

    // Todo: test all kinds of discounts are applied properly

    @Test
    void tenPercentDiscountNoDiscount() {
        SupermarketCatalog catalog = new FakeCatalog();
        Product toothbrush = new Product("toothbrush", ProductUnit.EACH, 0.99);
        Product apples = new Product("apples", ProductUnit.KILO, 1.99);

        Teller teller = new Teller(catalog);
        teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT, toothbrush, 10.0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(apples, 2.5);

        // ACT
        Receipt receipt = teller.checksOutArticlesFrom(cart);

        // ASSERT
        assertEquals(4.975, receipt.getTotalPrice(), 0.01);
        assertEquals(Collections.emptyList(), receipt.getDiscounts());
        assertEquals(1, receipt.getItems().size());
        ReceiptItem receiptItem = receipt.getItems().get(0);
        assertEquals(apples, receiptItem.getProduct());
        assertEquals(1.99, receiptItem.getPrice());
        assertEquals(2.5 * 1.99, receiptItem.getTotalPrice());
        assertEquals(2.5, receiptItem.getQuantity());

    }

    @Test
    void tenPercentDiscount() {
        SupermarketCatalog catalog = new FakeCatalog();
        Product toothbrush = new Product("toothbrush", ProductUnit.EACH, 0.99);
        Product apples = new Product("apples", ProductUnit.KILO, 1.99);

        Teller teller = new Teller(catalog);
        teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT, toothbrush, 10.0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(apples, 2.5);
        cart.addItemQuantity(toothbrush, 3);

        // ACT
        Receipt receipt = teller.checksOutArticlesFrom(cart);

        // ASSERT
        assertEquals(7.648, receipt.getTotalPrice(), 0.01);
        assertEquals(2, receipt.getItems().size());

        ReceiptItem receiptItem = receipt.getItems().get(0);
        assertEquals(apples, receiptItem.getProduct());
        assertEquals(1.99, receiptItem.getPrice());
        assertEquals(2.5 * 1.99, receiptItem.getTotalPrice());
        assertEquals(2.5, receiptItem.getQuantity());

        receiptItem = receipt.getItems().get(1);
        assertEquals(toothbrush, receiptItem.getProduct());
        assertEquals(0.99, receiptItem.getPrice());
        assertEquals(2.97, receiptItem.getTotalPrice(), 0.01);
        assertEquals(3 * 0.99, receiptItem.getQuantity(), 0.1);

    }

    @Test
    void threeForTwoDiscount() {
        SupermarketCatalog catalog = new FakeCatalog();
        Product ps5 = new Product("PS5", ProductUnit.EACH, (double) 200);

        Teller teller = new Teller(catalog);
        teller.addSpecialOffer(SpecialOfferType.THREE_FOR_TWO, ps5);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(ps5, 3);

        // ACT
        Receipt receipt = teller.checksOutArticlesFrom(cart);

        // ASSERT
        assertEquals(400, receipt.getTotalPrice(), 0.01);
        assertEquals(1, receipt.getItems().size());

        ReceiptItem receiptItem = receipt.getItems().get(0);
        assertEquals(ps5, receiptItem.getProduct());
        assertEquals(200, receiptItem.getPrice());
        assertEquals(600, receiptItem.getTotalPrice());
        assertEquals(3, receiptItem.getQuantity());
    }

    @Test
    void threeForTwoDiscountFourProducts() {
        SupermarketCatalog catalog = new FakeCatalog();
        Product ps5 = new Product("PS5", ProductUnit.EACH, (double) 200);

        Teller teller = new Teller(catalog);
        teller.addSpecialOffer(SpecialOfferType.THREE_FOR_TWO, ps5);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(ps5, 4);

        // ACT
        Receipt receipt = teller.checksOutArticlesFrom(cart);

        // ASSERT
        assertEquals(600, receipt.getTotalPrice());
        assertEquals(1, receipt.getItems().size());

        ReceiptItem receiptItem = receipt.getItems().get(0);
        assertEquals(ps5, receiptItem.getProduct());
        assertEquals(200, receiptItem.getPrice());
        assertEquals(800, receiptItem.getTotalPrice());
        assertEquals(4, receiptItem.getQuantity());
    }

    @Test
    void threeForTwoDiscountTimesTwo() {
        SupermarketCatalog catalog = new FakeCatalog();
        Product ps5 = new Product("PS5", ProductUnit.EACH, (double)200);

        Teller teller = new Teller(catalog);
        teller.addSpecialOffer(SpecialOfferType.THREE_FOR_TWO, ps5);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(ps5, 7);

        // ACT
        Receipt receipt = teller.checksOutArticlesFrom(cart);

        // ASSERT
        assertEquals(1000, receipt.getTotalPrice(), 0.01);
        assertEquals(1, receipt.getItems().size());

        ReceiptItem receiptItem = receipt.getItems().get(0);
        assertEquals(ps5, receiptItem.getProduct());
        assertEquals(200, receiptItem.getPrice());
        assertEquals(1400, receiptItem.getTotalPrice());
        assertEquals(7, receiptItem.getQuantity());
    }

    @Test
    void twoForAmount() {
        SupermarketCatalog catalog = new FakeCatalog();
        Product milk = new Product("Milk", ProductUnit.EACH, 1.37);

        Teller teller = new Teller(catalog);
        teller.addSpecialOffer(SpecialOfferType.TWO_FOR_AMOUNT, milk, 2.37);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(milk, 2);

        // ACT
        Receipt receipt = teller.checksOutArticlesFrom(cart);

        // ASSERT
        assertEquals(2.37, receipt.getTotalPrice());
        assertEquals(1, receipt.getItems().size());

        ReceiptItem receiptItem = receipt.getItems().get(0);
        assertEquals(milk, receiptItem.getProduct());
        assertEquals(1.37, receiptItem.getPrice());
        assertEquals(2.74, receiptItem.getTotalPrice());
        assertEquals(2, receiptItem.getQuantity());
    }

    @Test
    void twoForAmountThreeProduct() {
        SupermarketCatalog catalog = new FakeCatalog();
        Product milk = new Product("Milk", ProductUnit.EACH, 1.37);

        Teller teller = new Teller(catalog);
        teller.addSpecialOffer(SpecialOfferType.TWO_FOR_AMOUNT, milk, 2.37);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(milk, 3);

        // ACT
        Receipt receipt = teller.checksOutArticlesFrom(cart);

        // ASSERT
        assertEquals(3.74, receipt.getTotalPrice());
        assertEquals(1, receipt.getItems().size());

        ReceiptItem receiptItem = receipt.getItems().get(0);
        assertEquals(milk, receiptItem.getProduct());
        assertEquals(1.37, receiptItem.getPrice());
        assertEquals(4.11, receiptItem.getTotalPrice());
        assertEquals(3, receiptItem.getQuantity());
    }

    @Test
    void twoForAmountTwoTime() {
        SupermarketCatalog catalog = new FakeCatalog();
        Product milk = new Product("Milk", ProductUnit.EACH, 1.37);

        Teller teller = new Teller(catalog);
        teller.addSpecialOffer(SpecialOfferType.TWO_FOR_AMOUNT, milk, 2.37);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(milk, 4);

        // ACT
        Receipt receipt = teller.checksOutArticlesFrom(cart);

        // ASSERT
        assertEquals(4.74, receipt.getTotalPrice());
        assertEquals(1, receipt.getItems().size());

        ReceiptItem receiptItem = receipt.getItems().get(0);
        assertEquals(milk, receiptItem.getProduct());
        assertEquals(1.37, receiptItem.getPrice());
        assertEquals(5.48, receiptItem.getTotalPrice());
        assertEquals(4, receiptItem.getQuantity());
    }

    @Test
    void fiveForAmount() {
        SupermarketCatalog catalog = new FakeCatalog();
        Product steak = new Product("Steak", ProductUnit.EACH, 2.42);

        Teller teller = new Teller(catalog);
        teller.addSpecialOffer(SpecialOfferType.FIVE_FOR_AMOUNT, steak, 10);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(steak, 5);

        // ACT
        Receipt receipt = teller.checksOutArticlesFrom(cart);

        // ASSERT
        assertEquals(10, receipt.getTotalPrice());
        assertEquals(1, receipt.getItems().size());

        ReceiptItem receiptItem = receipt.getItems().get(0);
        assertEquals(steak, receiptItem.getProduct());
        assertEquals(2.42, receiptItem.getPrice());
        assertEquals(12.1, receiptItem.getTotalPrice());
        assertEquals(5, receiptItem.getQuantity());
    }

    @Test
    void fiveForAmountSixProduct() {
        SupermarketCatalog catalog = new FakeCatalog();
        Product steak = new Product("Steak", ProductUnit.EACH, 2.42);

        Teller teller = new Teller(catalog);
        teller.addSpecialOffer(SpecialOfferType.FIVE_FOR_AMOUNT, steak, 10);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(steak, 6);

        // ACT
        Receipt receipt = teller.checksOutArticlesFrom(cart);

        // ASSERT
        assertEquals(12.42, receipt.getTotalPrice());
        assertEquals(1, receipt.getItems().size());

        ReceiptItem receiptItem = receipt.getItems().get(0);
        assertEquals(steak, receiptItem.getProduct());
        assertEquals(2.42, receiptItem.getPrice());
        assertEquals(14.52, receiptItem.getTotalPrice());
        assertEquals(6, receiptItem.getQuantity());
    }
}