package ag.selm.recommendationservice.controller;

import ag.selm.recommendationservice.entity.ProductRating;
import ag.selm.recommendationservice.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger; // Используем SLF4J
import org.slf4j.LoggerFactory; // Используем SLF4J
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody; // Для REST
import org.springframework.http.ResponseEntity; // Для REST

import java.util.Collections; // Для пустого списка
import java.util.List;
import java.util.Optional;

@Controller // Оставляем Controller, т.к. есть метод для Thymeleaf
@RequiredArgsConstructor
@RequestMapping("/ratings") // Базовый путь для всех методов контроллера
public class RatingController {

    private static final Logger log = LoggerFactory.getLogger(RatingController.class); // Имя логгера log - стандарт
    private final RatingService ratingService;

    // REST эндпоинт для получения рейтинга одного продукта
    @GetMapping("/{productId}") // Путь относительно /ratings
    @ResponseBody // Указываем, что возвращаем тело ответа, а не имя шаблона
    public ResponseEntity<ProductRating> getRating(@PathVariable("productId") Integer productId) {
        log.info("Request received for product rating with productId: {}", productId);
        Optional<ProductRating> ratingOpt = ratingService.getProductRating(productId);

        // Возвращаем 200 OK с объектом, если найден, или 404 Not Found
        return ratingOpt.map(rating -> {
                    log.info("Product rating found for productId: {}", productId);
                    return ResponseEntity.ok(rating);
                })
                .orElseGet(() -> {
                    log.warn("Product rating not found for productId: {}", productId);
                    return ResponseEntity.notFound().build();
                });
    }

    // Метод для отображения страницы Thymeleaf со всеми рекомендациями/статистикой
    @GetMapping // Путь /ratings
    public String getRecommendationsPage(Model model) {
        log.info("Request received for recommendations page.");
        try {
            List<ProductRating> productRatings = ratingService.getAllProductRatings();
            if (productRatings == null) { // Доп. проверка на null
                productRatings = Collections.emptyList();
                log.warn("Rating service returned null list, treating as empty.");
            }
            log.info("Successfully fetched {} product ratings.", productRatings.size());
            model.addAttribute("productRatings", productRatings);
            model.addAttribute("ratingsLoaded", true); // Флаг успеха
            model.addAttribute("errorMessage", null); // Очищаем сообщение об ошибке
        } catch (Exception e) {
            log.error("Error fetching product ratings for recommendations page: {}", e.getMessage(), e);
            model.addAttribute("productRatings", Collections.emptyList()); // Пустой список при ошибке
            model.addAttribute("ratingsLoaded", false); // Флаг ошибки
            model.addAttribute("errorMessage", "Failed to load product ratings. Please try again later."); // Сообщение об ошибке для пользователя
        }
        return "recommendations/list"; // Имя Thymeleaf шаблона (src/main/resources/templates/recommendations/list.html)
    }
}