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

    public static void generateReceiptHTML(ShoppingCart cart) {

        StringBuilder html = new StringBuilder();
        html.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"4\">");
        html.append("<thead><tr>");
        html.append("<th>Product</th><th>Qty</th><th>Unit</th><th>Unit Price</th><th>Total</th>");
        html.append("</tr></thead>");
        html.append("<tbody>");

        for (ReceiptItem item : cart.getReceiptItems()) {
            html.append("<tr>");
            html.append("<td>").append(escapeHtml(item.getProduct().getName())).append("</td>");
            html.append("<td>").append(presentQuantity(item)).append("</td>");
            html.append("<td>").append(escapeHtml(String.valueOf(item.getProduct().getUnit()))).append("</td>");
            html.append("<td>").append(presentPrice(item.getPrice())).append("</td>");
            html.append("<td>").append(presentPrice(item.getTotalPrice())).append("</td>");
            html.append("</tr>");
        }

        html.append("</tbody>");

        // discounts
        if(!cart.getReceipt().getDiscounts().isEmpty()) {
            html.append("<tfoot>");
            for (Discount d : cart.getReceipt().getDiscounts()) {
                html.append("<tr>");
                html.append("<td colspan=\"4\">").append(escapeHtml(d.getDescription()));
                // show involved products if present
                if (d.getProducts() != null && !d.getProducts().isEmpty()) {
                    html.append(" (");
                    boolean first = true;
                    for (Product p : d.getProducts()) {
                        if (!first) html.append(", ");
                        html.append(escapeHtml(p.getName()));
                        first = false;
                    }
                    html.append(")");
                } else if (d.getProduct() != null) {
                    html.append(" (").append(escapeHtml(d.getProduct().getName())).append(")");
                }
                html.append("</td>");
                html.append("<td>-").append(presentPrice(d.getDiscountAmount())).append("</td>");
                html.append("</tr>");
            }
            // total row
            html.append("<tr><td colspan=\"4\"><strong>Total</strong></td><td><strong>")
                    .append(presentPrice(cart.getReceipt().getTotalPrice()))
                    .append("</strong></td></tr>");
            html.append("</tfoot>");
        } else {
            html.append("<tfoot><tr><td colspan=\"4\"><strong>Total</strong></td><td><strong>")
                    .append(presentPrice(cart.getReceipt().getTotalPrice()))
                    .append("</strong></td></tr></tfoot>");
        }

        html.append("</table>");

        try {
            java.nio.file.Path path = java.nio.file.Paths.get("cart.html").toAbsolutePath();
            java.nio.file.Path parent = path.getParent();
            if (parent != null) java.nio.file.Files.createDirectories(parent);
            try (java.io.BufferedWriter writer = java.nio.file.Files.newBufferedWriter(path, java.nio.charset.StandardCharsets.UTF_8)) {
                writer.write(html.toString());
            }
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String presentPrice(double price) {
        return String.format(java.util.Locale.UK, "%.2f", price);
    }

    private static String presentQuantity(ReceiptItem item) {
        return ProductUnit.EACH.equals(item.getProduct().getUnit())
                ? String.format("%d", (int) item.getQuantity())
                : String.format(java.util.Locale.UK, "%.3f", item.getQuantity());
    }

    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

}
