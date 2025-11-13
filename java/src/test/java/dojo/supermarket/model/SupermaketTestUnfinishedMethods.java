package dojo.supermarket.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SupermaketTestUnfinishedMethods {

    @Test
    void testBundle() {
        SupermarketCatalog catalog = new FakeCatalog();
        Product apples = new Product("apples", ProductUnit.KILO, 1.5);
        Product toothbrush = new Product("toothbrush", ProductUnit.EACH, 0.5);


        Teller teller = new Teller(catalog);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(apples, 2);
        cart.addItemQuantity(toothbrush, 2);

        //Add bundle offers
        ArrayList<BundleOffer> bundleOffers = new ArrayList<>();

        ArrayList<Product> products = new ArrayList<>();
        products.add(apples);
        products.add(toothbrush);
        bundleOffers.add(new BundleOffer(SpecialOfferType.BUNDLE, products));

        // ACT
        Receipt receipt = new Receipt();
        receipt.addProduct(apples, 2, apples.getPrice(), apples.getPrice()*2);
        receipt.addProduct(toothbrush, 2, toothbrush.getPrice(), toothbrush.getPrice()*2);

        //cart.handleBundles(receipt, bundleOffers);

        assertEquals(3.6, receipt.getTotalPrice(), 0.01);
        assertEquals(2, receipt.getItems().size());

        ReceiptItem receiptItem = receipt.getItems().get(0);
        assertEquals(apples, receiptItem.getProduct());
        assertEquals(1.5, receiptItem.getPrice());
        assertEquals(3, receiptItem.getTotalPrice());
        assertEquals(2, receiptItem.getQuantity());

        receiptItem = receipt.getItems().get(1);
        assertEquals(toothbrush, receiptItem.getProduct());
        assertEquals(0.5, receiptItem.getPrice());
        assertEquals(1, receiptItem.getTotalPrice());
        assertEquals(2, receiptItem.getQuantity());

    }

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

        assertEquals(3.6, cart.getReceipt().getTotalPrice(), 0.01);
        assertEquals(2, cart.getReceipt().getItems().size());

        ReceiptItem receiptItem = cart.getReceipt().getItems().get(0);
        assertEquals(apples, receiptItem.getProduct());
        assertEquals(1.5, receiptItem.getPrice());
        assertEquals(3, receiptItem.getTotalPrice());
        assertEquals(2, receiptItem.getQuantity());

        receiptItem = cart.getReceipt().getItems().get(1);
        assertEquals(toothbrush, receiptItem.getProduct());
        assertEquals(0.5, receiptItem.getPrice());
        assertEquals(1, receiptItem.getTotalPrice());
        assertEquals(2, receiptItem.getQuantity());

    }
}
