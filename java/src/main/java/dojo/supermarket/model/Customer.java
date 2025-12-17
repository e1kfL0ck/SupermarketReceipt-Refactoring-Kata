package dojo.supermarket.model;

import java.util.ArrayList;

public class Customer {

    private int id;
    private ArrayList<Coupon> coupons;

    public Customer(int id, ArrayList<Coupon> coupons) {
        this.id = id;
        this.coupons = coupons;
    }

    public Coupon getCouponValidity(Product product, LocalDate checkoutDate) {
        Coupon coupon = coupons.get(product);
        return coupon == null || !coupon.isValidOn(checkoutDate) || !coupon.isUsed() ? null : coupon;
    }
}
