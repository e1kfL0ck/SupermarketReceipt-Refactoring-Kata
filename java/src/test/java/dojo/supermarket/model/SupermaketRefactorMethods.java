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
}
