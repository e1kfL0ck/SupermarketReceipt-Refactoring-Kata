package dojo.supermarket.model;

import java.util.ArrayList;

public class Customer {

    private int id;
    private ArrayList<Coupon> coupons;

    public Customer(int id, ArrayList<Coupon> coupons) {
        this.id = id;
        this.coupons = coupons;
    }

}
