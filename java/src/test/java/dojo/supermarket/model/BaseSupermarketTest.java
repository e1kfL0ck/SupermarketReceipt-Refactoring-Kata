package dojo.supermarket.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseSupermarketTest {

    protected static final Map<String, Product> P = new HashMap<>();
    protected static final Map<Product, Offer> offersMap = new HashMap<>();
    protected static Customer customer;

    @BeforeAll
    static void setup() {
        P.clear();

        P.put("apples",     new Product("apples", ProductUnit.KILO, 1.50));
        P.put("toothbrush", new Product("toothbrush", ProductUnit.EACH, 0.50));
        P.put("toothpaste", new Product("toothpaste", ProductUnit.EACH, 1.50));
        P.put("steak",      new Product("steak", ProductUnit.EACH, 2.42));
        P.put("milk",       new Product("milk", ProductUnit.EACH, 1.37));
        P.put("soda",       new Product("soda", ProductUnit.EACH, 1.20));
        P.put("chips",      new Product("chips", ProductUnit.EACH, 2.00));
        P.put("cheese",     new Product("cheese", ProductUnit.KILO, 9.00));
        P.put("ham",        new Product("ham", ProductUnit.EACH, 4.00));
        P.put("bread",      new Product("bread", ProductUnit.EACH, 1.50));
        P.put("grapes",     new Product("grapes", ProductUnit.KILO, 3.00));
        P.put("chocolate",  new Product("chocolate", ProductUnit.EACH, 2.00));

        offersMap.put(
                product("toothpaste"),
                new Offer(
                        SpecialOfferType.BUNDLE,
                        10.0,
                        new ArrayList<>(List.of(product("toothpaste"), product("toothbrush")))
                ));

        offersMap.put(
                product("toothbrush"),
                new Offer(
                        SpecialOfferType.BUNDLE,
                        10.0,
                        new ArrayList<>(List.of(product("toothpaste"), product("toothbrush")))
                ));

        offersMap.put(
                product("milk"),
                new Offer(
                        SpecialOfferType.BUNDLE,
                        10.0,
                        new ArrayList<>(List.of(product("milk"), product("ham"), product("bread")))
                ));

        offersMap.put(
                product("ham"),
                new Offer(
                        SpecialOfferType.BUNDLE,
                        10.0,
                        new ArrayList<>(List.of(product("milk"), product("ham"), product("bread")))
                ));

        offersMap.put(
                product("bread"),
                new Offer(
                        SpecialOfferType.BUNDLE,
                        10.0,
                        new ArrayList<>(List.of(product("milk"), product("ham"), product("bread")))
                ));

        offersMap.put(
                product("chocolate"),
                new Offer(
                        SpecialOfferType.TEN_PERCENT_DISCOUNT,
                        10.0,
                        new ArrayList<>(List.of(product("chocolate")))
                ));

        offersMap.put(
                product("grapes"),
                new Offer(
                        SpecialOfferType.TEN_PERCENT_DISCOUNT,
                        10.0,
                        new ArrayList<>(List.of(product("grapes")))
                ));

        offersMap.put(
                product("soda"),
                new Offer(
                        SpecialOfferType.THREE_FOR_TWO,
                        0.0,
                        new ArrayList<>(List.of(product("soda")))
                ));

        offersMap.put(
                product("toothbrush"),
                new Offer(
                        SpecialOfferType.THREE_FOR_TWO,
                        0.0,
                        new ArrayList<>(List.of(product("toothbrush")))
                ));

        offersMap.put(
                product("milk"),
                new Offer(
                        SpecialOfferType.TWO_FOR_AMOUNT,
                        2.37,
                        new ArrayList<>(List.of(product("milk")))
                ));

        offersMap.put(
                product("steak"),
                new Offer(
                        SpecialOfferType.FIVE_FOR_AMOUNT,
                        10.00,
                        new ArrayList<>(List.of(product("steak")))
                ));

        offersMap.put(
                product("chips"),
                new Offer(
                        SpecialOfferType.FIVE_FOR_AMOUNT,
                        8.00,
                        new ArrayList<>(List.of(product("chips")))
                ));
    }

    protected static Product product(String key) {
        return P.get(key);
    }

    @BeforeEach
    void initCustomer() {
        Map<Product, Coupon> coupons = new HashMap<>();

        coupons.put(
                product("bread"),
                new Coupon(
                        product("bread"),
                        java.time.LocalDate.now().minusDays(1),
                        java.time.LocalDate.now().plusDays(10),
                        2,
                        1,
                        0.5
                )
        );

        coupons.put(
                product("soda"),
                new Coupon(
                        product("soda"),
                        java.time.LocalDate.now().minusDays(1),
                        java.time.LocalDate.now().plusDays(10),
                        1,
                        1,
                        0.5
                )
        );

        customer = new  Customer(1, coupons);
    }
}
