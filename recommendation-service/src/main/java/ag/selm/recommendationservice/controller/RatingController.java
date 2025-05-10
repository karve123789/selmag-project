package ag.selm.recommendationservice.controller;

import ag.selm.recommendationservice.entity.ProductRating;
import ag.selm.recommendationservice.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType; // <-- Импорт
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/ratings")
public class RatingController {

    private static final Logger log = LoggerFactory.getLogger(RatingController.class);
    private final RatingService ratingService;

    // REST эндпоинт для получения рейтинга ОДНОГО продукта (остается как есть)
    @GetMapping("/{productId}")
    @ResponseBody
    public ResponseEntity<ProductRating> getRating(@PathVariable("productId") Integer productId) {
        // ... (код без изменений) ...
        log.info("Request received for product rating with productId: {}", productId);
        Optional<ProductRating> ratingOpt = ratingService.getProductRating(productId);
        return ratingOpt.map(rating -> {
                    log.info("Product rating found for productId: {}", productId);
                    return ResponseEntity.ok(rating);
                })
                .orElseGet(() -> {
                    log.warn("Product rating not found for productId: {}", productId);
                    return ResponseEntity.notFound().build();
                });
    }

    // Метод для отображения страницы Thymeleaf (остается как есть)
    @GetMapping
    public String getRecommendationsPage(Model model) {
        // ... (код без изменений) ...
        log.info("Request received for recommendations page.");
        try {
            List<ProductRating> productRatings = ratingService.getAllProductRatings();
            if (productRatings == null) {
                productRatings = Collections.emptyList();
                log.warn("Rating service returned null list, treating as empty.");
            }
            log.info("Successfully fetched {} product ratings.", productRatings.size());
            model.addAttribute("productRatings", productRatings);
            model.addAttribute("ratingsLoaded", true);
            model.addAttribute("errorMessage", null);
        } catch (Exception e) {
            log.error("Error fetching product ratings for recommendations page: {}", e.getMessage(), e);
            model.addAttribute("productRatings", Collections.emptyList());
            model.addAttribute("ratingsLoaded", false);
            model.addAttribute("errorMessage", "Failed to load product ratings. Please try again later.");
        }
        return "recommendations/list"; // Имя Thymeleaf шаблона
    }

    // ========= НОВЫЙ МЕТОД ДЛЯ ВОЗВРАТА JSON СПИСКА =========
    @GetMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE) // Указываем путь /ratings/api и что возвращаем JSON
    @ResponseBody // Обязательно, чтобы вернуть тело ответа, а не имя шаблона
    public List<ProductRating> getAllRatingsApi() {
        log.info("Request received for all ratings API data.");
        try {
            List<ProductRating> ratings = ratingService.getAllProductRatings();
            return ratings != null ? ratings : Collections.emptyList();
        } catch (Exception e) {
            log.error("Error fetching all ratings for API: {}", e.getMessage(), e);
            // Можно вернуть пустой список или выбросить исключение,
            // которое будет обработано глобальным обработчиком ошибок, если он есть
            return Collections.emptyList();
        }
    }
    // ========================================================
}