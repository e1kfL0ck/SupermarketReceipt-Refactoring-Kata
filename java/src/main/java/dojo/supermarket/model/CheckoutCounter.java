package dojo.supermarket.model;

import java.util.Map;

//TODO: Revert pom.xml to original state before submission
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

    public Receipt checkout(Customer customer) {
        Receipt receipt = new Receipt(cart.items());
        engine.applyAll(cart, customer, receipt);

        double total = receipt.computeTotalPrice();

        int maxPointsNeeded = loyalty.moneyToPoints(total);
        int usedPoints = customer.useCreditPoints(maxPointsNeeded);
        double covered = loyalty.pointsToMoney(usedPoints);

        double paidByCash = total - covered;

        //receipt.pay(new PaymentResult(paidByCash, usedPoints));
        receipt.pay();

        // gain de points sur l'argent réellement dépensé (règle simple)
        int earned = loyalty.earnPoints(paidByCash);
        customer.addCreditPoints(earned);

        return receipt;
    }
}
