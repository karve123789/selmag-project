package ag.selm.customer.controller;

import ag.selm.customer.client.RecommendationClient; // Импорт клиента
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/recommendations") // Базовый путь для этого контроллера
@RequiredArgsConstructor
@Slf4j
public class RecommendationController {

    private final RecommendationClient recommendationClient; // Инжектируем наш клиент

    @GetMapping("/list") // Обрабатываем GET запросы на /recommendations/list
    public Mono<String> getRecommendationsPage(Model model) {
        log.info("Request received for recommendations page");
        // Вызываем метод клиента, собираем результат в список и добавляем в модель
        return recommendationClient.findAllProductStats()
                .collectList() // Собираем Flux<ProductStats> в Mono<List<ProductStats>>
                .doOnSuccess(statsList -> {
                    log.info("Successfully fetched {} product stats records.", statsList.size());
                    model.addAttribute("productStatsList", statsList); // Добавляем список в модель
                    model.addAttribute("errorOccurred", false); // Флаг успеха
                })
                .doOnError(error -> {
                    log.error("Error fetching recommendations: {}", error.getMessage(), error);
                    model.addAttribute("errorOccurred", true); // Флаг ошибки
                    model.addAttribute("errorMessage", "Could not load recommendations."); // Сообщение об ошибке
                })
                .thenReturn("customer/products/recommendations"); // Имя Thymeleaf шаблона
    }
}