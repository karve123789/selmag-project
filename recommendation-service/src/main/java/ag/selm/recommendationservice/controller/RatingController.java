package ag.selm.recommendationservice.controller;

import ag.selm.recommendationservice.entity.ProductRating;
import ag.selm.recommendationservice.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/ratings")
public class RatingController {

    private static final Logger logger = LoggerFactory.getLogger(RatingController.class);
    private final RatingService ratingService;

    @GetMapping("/{productId}")
    @ResponseBody // Добавляем эту аннотацию для REST API
    public ResponseEntity<ProductRating> getRating(@PathVariable("productId") Integer productId) {
        logger.info("Getting product rating for product ID: {}", productId);
        Optional<ProductRating> rating = ratingService.getProductRating(productId);
        return rating.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public String getRecommendationsPage(Model model) {
        logger.info("getRecommendationsPage");

        try {
            List<ProductRating> productRatings = ratingService.getAllProductRatings();
            logger.info("Получено рейтингов {}", productRatings.size());
            model.addAttribute("productRatings", productRatings);
            model.addAttribute("ratingsLoaded", true); //  Флаг для отображения контента
        }
        catch (Exception exception){
            logger.error(exception.getMessage(), exception);
            model.addAttribute("errorMessage", "Ошибка при загрузке рейтингов."); // Сообщение об ошибке
            model.addAttribute("ratingsLoaded", false); //  Устанавливаем флаг в false при ошибке
        }

        return "recommendations/list"; // Путь к Thymeleaf шаблону
    }
}