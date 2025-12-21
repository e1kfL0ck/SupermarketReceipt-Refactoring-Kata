package dojo.supermarket.model;

import java.util.Map;

/**
 * The CheckoutCounter class is responsible for processing the checkout of a shopping cart,
 * applying relevant offers and discounts, and managing customer loyalty points.
 */
public class CheckoutCounter {
    private final Map<Product, Offer> offersMap;
    private final ShoppingCart cart;
    private DiscountEngine engine;
    private LoyaltyProgram loyalty = new LoyaltyProgram();

    public CheckoutCounter(Map<Product, Offer> offersMap, ShoppingCart cart) {
        this.offersMap = offersMap != null ? offersMap : new java.util.HashMap<>();
        this.cart = cart;
        this.engine = new DiscountEngine(this.offersMap);
    }

    /**
     * Processes the checkout for a given customer, applying discounts and managing loyalty points.
     *
     * @param customer The customer checking out.
     * @return A Receipt object containing the details of the transaction.
     */
    public Receipt checkout(Customer customer) {
        Receipt receipt = new Receipt(cart.items());
        engine.applyAll(cart, customer, receipt);

        receipt.pay();

        int maxPointsNeeded = loyalty.eurosToPoints(receipt.getTotalPrice());
        int usedPoints = customer.useCreditPoints(maxPointsNeeded);
        double coveredByPoints = loyalty.pointsToEuros(usedPoints);

        if (coveredByPoints > 0) receipt.usePoints(coveredByPoints);

        // win points only on the amount actually paid
        int earned = loyalty.earnPoints(receipt.getTotalPrice());
        customer.addCreditPoints(earned);

        return receipt;
    }
}
