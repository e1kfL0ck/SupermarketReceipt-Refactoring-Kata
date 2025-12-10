package dojo.supermarket.model;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@Controller
public class ReceiptController {

    @GetMapping("/receipt")
    public String displayReceipt(ShoppingCart cart, Model model) {
        // make sure all pricing is done
        cart.handleAllOffers();
        cart.goToCheckout();

        List<ReceiptItem> items = cart.getReceiptItems();
        Receipt receipt = cart.getReceipt();
        List<Discount> discounts = receipt.getDiscounts(); // you’ll add this getter

        model.addAttribute("items", items);
        model.addAttribute("discounts", discounts);
        model.addAttribute("receipt", receipt);

        return "receipt"; // Thymeleaf template
    }
}
