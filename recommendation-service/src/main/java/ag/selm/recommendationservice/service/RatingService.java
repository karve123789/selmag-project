package ag.selm.recommendationservice.service;

import ag.selm.recommendationservice.entity.ProductRating;

import java.util.List;
import java.util.Optional;

public interface RatingService {
    Optional<ProductRating> getProductRating(Integer productId);

    List<ProductRating> getAllProductRatings();
}

