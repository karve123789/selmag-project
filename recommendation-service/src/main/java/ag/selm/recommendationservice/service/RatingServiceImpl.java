package ag.selm.recommendationservice.service;

import ag.selm.recommendationservice.entity.ProductRating;
import ag.selm.recommendationservice.repository.ProductRatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RatingServiceImpl implements RatingService {

    private final ProductRatingRepository  productRatingRepository;

    @Override
    public List<ProductRating> getAllProductRatings() {
        return productRatingRepository.findAll();
    }


    @Override
    public Optional<ProductRating> getProductRating(Integer productId) {
        return productRatingRepository.findById(productId);
    }
}
