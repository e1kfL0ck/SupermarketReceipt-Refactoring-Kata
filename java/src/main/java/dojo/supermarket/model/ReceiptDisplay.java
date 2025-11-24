package dojo.supermarket.model;

public class ReceiptDisplay {

    ReceiptDisplay(){}

    public static String printReceipt(ShoppingCart cart) {
        StringBuilder result = new StringBuilder();

        result.append("Supermarket Receipt\n\n");
        result.append("Prodcuts\n\n");

        result.append("Item  Quantity * Price\n       Total Price\n\n");

        for (ReceiptItem item: cart.getReceiptItems()) {
            result.append(item.getProduct().getName() + "  "
                    + item.getQuantity()+ " * " +item.getPrice() + "\n"
                    + "       " + String.format("%.2f", item.getTotalPrice()) + "\n");
        }

        result.append("\nDiscounts\n\n");

        result.append("Offer Type\nProducts - Amount\n\n");

        for (DefaultOffer offer : cart.getOfferCatalog()) {
            result.append(offer.getOfferType().toString() + "\n"
                    + offer.getProducts().toString() + " - " + offer.getDiscountAmount());
        }

        result.append("\n\nTotal price :" + String.format("%.4f", cart.getReceipt().getTotalPrice()));
        result.append("\nTotal discounts :" + String.format("%.4f", cart.getReceipt().getTotalDiscounts()));
        result.append("\nTotal price after discounts :" + String.format("%.4f", cart.getReceipt().getTotalPriceAfterDiscount()));

        return result.toString();
    }
}
