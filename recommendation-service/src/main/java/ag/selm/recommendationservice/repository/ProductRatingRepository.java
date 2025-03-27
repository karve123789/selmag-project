package ag.selm.recommendationservice.repository;

import ag.selm.recommendationservice.entity.ProductRating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRatingRepository extends JpaRepository<ProductRating, Integer> {

}
