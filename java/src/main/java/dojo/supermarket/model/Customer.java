package dojo.supermarket.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Customer {

    private int id;
    private Map<Product, Coupon> coupons;
    private int creditPoints;

    public Customer(int id, Map<Product, Coupon> coupons) {
        this.id = id;
        this.coupons = coupons;
    }

    public Customer(Customer other) {
        this.coupons = new HashMap<>();
        for (var e : other.coupons.entrySet()) {
            this.coupons.put(e.getKey(), new Coupon(e.getValue()));
        }
        // Copy the credit points value; primitives like int are always copied by value in Java
        this.creditPoints = other.creditPoints;
    }

    public Coupon getCouponValidity(Product product, LocalDate checkoutDate) {
        Coupon coupon = coupons.get(product);
        return coupon == null || !coupon.isValidOn(checkoutDate) || coupon.isUsed() ? null : coupon;
    }

    public int getUnusedCouponsCount() {
        return (int) coupons.values().stream().filter(coupon -> !coupon.isUsed()).count();
    }

    public int getCreditPoints() { return creditPoints; }

    /**
     * Adds credit points to the customer's account.
     * Used for tests.
     */
    public void addCreditPoints(int points) {
        if (points > 0) creditPoints += points;
    }

    public int useCreditPoints(int pointsRequested) {
        int used = Math.min(pointsRequested, creditPoints);
        creditPoints -= used;
        return used;
    }

}
