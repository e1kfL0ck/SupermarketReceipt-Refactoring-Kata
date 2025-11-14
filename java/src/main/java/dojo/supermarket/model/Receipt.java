package dojo.supermarket.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Receipt {

    private List<Discount> discounts = new ArrayList<>();
    private double totalPrice = 0;
    private double totalDiscounts = 0;
    private double totalPriceAfterDiscount = 0;

    public void addDiscount(Discount discount) {
        discounts.add(discount);
    }

    public void pay(List<ReceiptItem> receiptItems) {
        for (ReceiptItem item : receiptItems) {
            totalPrice += item.getTotalPrice();
        }
        for (Discount discount : discounts) {
            totalDiscounts += discount.getDiscountAmount();
        }

        totalPriceAfterDiscount = totalPrice - totalDiscounts;
    }

    public double getTotalPrice2() {
        return totalPrice;
    }

    public double getTotalDiscounts() {
        return totalDiscounts;
    }

    public double getTotalPriceAfterDiscount() {
        return totalPriceAfterDiscount;
    }
}
