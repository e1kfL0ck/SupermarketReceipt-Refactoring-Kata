package dojo.supermarket.model;

import java.time.LocalDate;
import java.util.Map;

public class Customer {

    private int id;
    private Map<Product, Coupon> coupons;

    public Customer(int id, Map<Product, Coupon> coupons) {
        this.id = id;
        this.coupons = coupons;
    }

    public Coupon getCouponValidity(Product product, LocalDate checkoutDate) {
        Coupon coupon = coupons.get(product);
        return coupon == null || !coupon.isValidOn(checkoutDate) || coupon.isUsed() ? null : coupon;
    }
}
