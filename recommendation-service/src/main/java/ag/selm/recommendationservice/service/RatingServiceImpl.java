package ag.selm.recommendationservice.service;

import ag.selm.recommendationservice.entity.ProductRating;
import ag.selm.recommendationservice.entity.ProductRatingRecord;
import ag.selm.recommendationservice.repository.ProductRatingRepository;
import ag.selm.recommendationservice.repository.ProductRatingRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RatingServiceImpl implements RatingService {

    private final ProductRatingRepository productRatingRepository;
    private final ProductRatingRecordRepository productRatingRecordRepository;

    public Optional<ProductRating> getProductRating(Integer productId) {
        return productRatingRepository.findById(productId);
    }

    public List<ProductRating> getAllProductRatings() {
        return productRatingRepository.findAll();
    }

    @Override
    @Transactional
    public void saveProductRatingRecord(Integer productId, Integer rating) {
        ProductRatingRecord ratingRecord = new ProductRatingRecord();
        ratingRecord.setProductId(productId);
        ratingRecord.setRating(rating);
        productRatingRecordRepository.save(ratingRecord);

        updateAverageRating(productId);
    }

    @Transactional
    public void updateAverageRating(Integer productId) {
        // Сначала пытаемся найти ProductRating
        Optional<ProductRating> productRatingOptional = productRatingRepository.findById(productId);
        ProductRating productRating;

        // Если ProductRating не существует, создаем его
        if (productRatingOptional.isEmpty()) {
            productRating = new ProductRating();
            productRating.setProductId(productId);
            productRating.setRatingAverage(0); // Начальный рейтинг
            productRating.setRatingCount(0);   // Начальное количество оценок
        } else {
            productRating = productRatingOptional.get();
        }

        List<ProductRatingRecord> ratingRecords = productRatingRecordRepository.findAllByProductId(productId);
        if (ratingRecords.isEmpty()) {
            productRatingRepository.save(productRating);
            return; // Нет оценок для этого продукта, сохраняем с начальными значениями
        }

        int totalRating = 0;
        for (ProductRatingRecord record : ratingRecords) {
            totalRating += record.getRating();
        }

        double averageRating = (double) totalRating / ratingRecords.size();

        productRating.setRatingAverage((int) Math.round(averageRating));
        productRating.setRatingCount(ratingRecords.size());
        productRatingRepository.save(productRating);
    }
}