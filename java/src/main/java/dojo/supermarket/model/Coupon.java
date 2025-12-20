package dojo.supermarket.model;

import java.time.LocalDate;

public class Coupon {

    private final Product product;
    private final LocalDate validFrom;
    private final LocalDate validTo;
    private final int triggerQuantity;
    private final int discountedQuantity;
    private final double discountRate; // 0.5 = -50%
    private boolean used;

    public Coupon(Product product,
                  LocalDate validFrom,
                  LocalDate validTo,
                  int triggerQuantity,
                  int discountedQuantity,
                  double discountRate) {
        this.product = product;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.triggerQuantity = triggerQuantity;
        this.discountedQuantity = discountedQuantity;
        this.discountRate = discountRate;
        this.used = false;
    }

    public Coupon(Coupon other) {
        this.product = other.product;
        this.validFrom = other.validFrom;
        this.validTo = other.validTo;
        this.triggerQuantity = other.triggerQuantity;
        this.discountedQuantity = other.discountedQuantity;
        this.discountRate = other.discountRate;
        this.used = other.used;
    }

    public void markUsed() { this.used = true; }

    public boolean isValidOn(LocalDate date) {
        return (date.isEqual(validFrom) || date.isAfter(validFrom))
                && (date.isEqual(validTo) || date.isBefore(validTo));
    }

    public int getTriggerQuantity() {
        return triggerQuantity;
    }

    public int getDiscountedQuantity() {
        return discountedQuantity;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public boolean isUsed() {
        return used;
    }
}
