package ag.selm.customer.controller;

import ag.selm.customer.client.ProductsClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductsClient productsClient;

    @GetMapping("/")
    public String home(Model model,
                       @RequestParam(name = "filter", required = false) String filter) {
        model.addAttribute("products", productsClient.findAllProducts(filter));
        model.addAttribute("filter", filter);
        return "customer/products/list";
    }
}