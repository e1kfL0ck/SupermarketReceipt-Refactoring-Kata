package dojo.supermarket.model;

import java.util.ArrayList;
import java.util.List;

public class Receipt {

    private List<Discount> discounts = new ArrayList<>();
    private List<ReceiptItem> items = new ArrayList<>();
    private double totalPriceBeforeDiscount = 0;
    private double totalDiscounts = 0;
    private double totalPrice = 0;
    private double creditPointsUsed = 0;

    public Receipt(ArrayList<ReceiptItem> items) {
        this.items = items;
    }

    public void addDiscount(Discount discount) {
        discounts.add(discount);
    }

    public void pay() {
        if(totalPriceBeforeDiscount ==0) computeTotalPriceBeforeDiscount();
        for (Discount discount : discounts) {
            totalDiscounts += discount.getDiscountAmount();
        }

        totalPrice = totalPriceBeforeDiscount - totalDiscounts - this.creditPointsUsed;
    }

    public double computeTotalPriceBeforeDiscount() {
        for (ReceiptItem item : items) {
            totalPriceBeforeDiscount += item.getTotalPrice();
        }
        return totalPriceBeforeDiscount;
    }

    public double usePoints(double amountCoveredByPoints) {
        this.creditPointsUsed = amountCoveredByPoints;
        totalPrice -= this.creditPointsUsed;
        return this.totalPrice;
    }

    public double getTotalPriceBeforeDiscount() {
        return totalPriceBeforeDiscount;
    }

    public double getTotalDiscounts() {
        return totalDiscounts;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public List<Discount> getDiscounts() {
        return discounts;
    }
}
