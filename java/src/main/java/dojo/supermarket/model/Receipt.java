package dojo.supermarket.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Receipt {

    private List<Discount> discounts = new ArrayList<>();
    private double totalPrice = 0;
    private double totalDiscounts = 0;
    private double totalPriceAfterDiscount = 0;

    public void addDiscount(Discount discount) {
        discounts.add(discount);
    }

    public void pay(Collection<ReceiptItem> receiptItems) {
        for (ReceiptItem item : receiptItems) {
            totalPrice += item.getTotalPrice();
        }
        for (Discount discount : discounts) {
            totalDiscounts += discount.getDiscountAmount();
        }

        totalPriceAfterDiscount = totalPrice - totalDiscounts;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public double getTotalDiscounts() {
        return totalDiscounts;
    }

    public double getTotalPriceAfterDiscount() {
        return totalPriceAfterDiscount;
    }

    public List<Discount> getDiscounts() {
        return discounts;
    }
}
