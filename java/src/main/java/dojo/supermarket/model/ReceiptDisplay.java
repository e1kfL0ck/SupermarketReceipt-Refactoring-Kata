package dojo.supermarket.model;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

public final class ReceiptDisplay {

    private ReceiptDisplay() {}

    /**
     * Produce a text receipt using the provided receipt (totals/discounts) and items list.
     * Use e.g. CheckoutCounter.getReceipt() and ShoppingCart.items() when calling.
     */
    public static String printReceipt(Receipt receipt) {
        List<ReceiptItem> items = receipt.getItems();

        StringBuilder result = new StringBuilder();
        String nl = System.lineSeparator();

        result.append("Supermarket Receipt").append(nl).append(nl);
        result.append("Products").append(nl).append(nl);

        result.append("Item  Quantity * Price").append(nl);
        result.append("       Total Price").append(nl).append(nl);

        if (items != null) {
            for (ReceiptItem item : items) {
                result.append(item.getProduct().getName())
                        .append("  ")
                        .append(presentQuantity(item))
                        .append(" * ")
                        .append(presentPrice(item.getPrice()))
                        .append(nl)
                        .append("       ")
                        .append(presentPrice(item.getTotalPrice()))
                        .append(nl);
            }
        }

        result.append(nl).append("Discounts").append(nl).append(nl);
        result.append("Description").append(nl).append(nl);

        if (receipt != null && receipt.getDiscounts() != null && !receipt.getDiscounts().isEmpty()) {
            for (Discount d : receipt.getDiscounts()) {
                result.append(d.getDescription());
                // Discounts refer to receipt items (not Product). Use the receipt items linked to the discount.
                List<Product> products = d.getProducts();
                if (products != null && !products.isEmpty()) {
                    String names = products.stream()
                            .map(Product::getName)
                            .collect(java.util.stream.Collectors.joining(", "));
                    result.append(" (").append(names).append(")");
                }
                result.append(" - ").append(presentPrice(d.getDiscountAmount())).append(nl);
            }
        } else {
            result.append("No discounts").append(nl);
        }

        if (receipt != null) {
            result.append(nl)
                    .append("Total price : ").append(presentPrice(receipt.getTotalPriceBeforeDiscount())).append(nl)
                    .append("Total discounts : ").append(presentPrice(receipt.getTotalDiscounts())).append(nl)
                    .append("Total price after discounts : ").append(presentPrice(receipt.getTotalPrice())).append(nl);
        }

        return result.toString();
    }

    /**
     * Generate an HTML receipt file `cart.html` using provided receipt and items.
     * Use e.g. CheckoutCounter.getReceipt() and ShoppingCart.items() when calling.
     */
    public static void generateReceiptHTML(Receipt receipt) {
        List<ReceiptItem> items = receipt.getItems();

        StringBuilder html = new StringBuilder();
        html.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"4\">");
        html.append("<thead><tr>");
        html.append("<th>Product</th><th>Qty</th><th>Unit</th><th>Unit Price</th><th>Total</th>");
        html.append("</tr></thead>");
        html.append("<tbody>");

        if (items != null) {
            for (ReceiptItem item : items) {
                html.append("<tr>");
                html.append("<td>").append(escapeHtml(item.getProduct().getName())).append("</td>");
                html.append("<td>").append(presentQuantity(item)).append("</td>");
                html.append("<td>").append(escapeHtml(String.valueOf(item.getProduct().getUnit()))).append("</td>");
                html.append("<td>").append(presentPrice(item.getPrice())).append("</td>");
                html.append("<td>").append(presentPrice(item.getTotalPrice())).append("</td>");
                html.append("</tr>");
            }
        }

        html.append("</tbody>");

        // discounts and totals come from the receipt
        if (receipt != null && receipt.getDiscounts() != null && !receipt.getDiscounts().isEmpty()) {
            html.append("<tfoot>");
            for (Discount d : receipt.getDiscounts()) {
                html.append("<tr>");
                html.append("<td colspan=\"4\">").append(escapeHtml(d.getDescription()));
                // Use receipt items referenced by the discount
                List<Product> products = d.getProducts();
                if (products != null && !products.isEmpty()) {
                    String names = products.stream()
                            .map(Product::getName)
                            .collect(java.util.stream.Collectors.joining(", "));
                    html.append(" (").append(names).append(")");
                }
                html.append("</td>");
                html.append("<td>-").append(presentPrice(d.getDiscountAmount())).append("</td>");
                html.append("</tr>");
            }
            // total row
            html.append("<tr><td colspan=\"4\"><strong>Total</strong></td><td><strong>")
                    .append(presentPrice(receipt.getTotalPrice()))
                    .append("</strong></td></tr>");
            html.append("</tfoot>");
        } else {
            // still print total row even if no discounts
            if (receipt != null) {
                html.append("<tfoot><tr><td colspan=\"4\"><strong>Total</strong></td><td><strong>")
                        .append(presentPrice(receipt.getTotalPrice()))
                        .append("</strong></td></tr></tfoot>");
            } else {
                html.append("<tfoot><tr><td colspan=\"5\"><strong>No receipt available</strong></td></tr></tfoot>");
            }
        }

        html.append("</table>");

        try {
            Path path = Paths.get("cart.html").toAbsolutePath();
            Path parent = path.getParent();
            if (parent != null) Files.createDirectories(parent);
            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                writer.write(html.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String presentPrice(double price) {
        return String.format(Locale.UK, "%.2f", price);
    }

    private static String presentQuantity(ReceiptItem item) {
        // item.getQuantity() is a Double object; use intValue() to avoid invalid cast
        if (ProductUnit.EACH.equals(item.getProduct().getUnit())) {
            Double q = item.getQuantity();
            return String.format("%d", q == null ? 0 : q.intValue());
        } else {
            Double q = item.getQuantity();
            return String.format(Locale.UK, "%.3f", q == null ? 0.0 : q);
        }
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
