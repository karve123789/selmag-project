package ag.selm.recommendationservice.repository;

import ag.selm.recommendationservice.entity.ProductRatingRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRatingRecordRepository extends JpaRepository<ProductRatingRecord, Long> {

    List<ProductRatingRecord> findAllByProductId(Integer productId);
}