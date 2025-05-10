package ag.selm.recommendationservice.service;

import ag.selm.recommendationservice.entity.ProductRating;
import ag.selm.recommendationservice.entity.ProductRatingRecord;
import ag.selm.recommendationservice.repository.ProductRatingRepository;
import ag.selm.recommendationservice.repository.ProductRatingRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Добавляем логирование
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Импорт @Transactional

import java.util.List;
import java.util.Optional;

@Slf4j // Используем Slf4j
@RequiredArgsConstructor
@Service
public class RatingServiceImpl implements RatingService {

    private final ProductRatingRepository productRatingRepository;
    private final ProductRatingRecordRepository productRatingRecordRepository;

    @Override
    public Optional<ProductRating> getProductRating(Integer productId) {
        log.debug("Fetching product rating for productId: {}", productId);
        return productRatingRepository.findById(productId);
    }

    @Override
    public List<ProductRating> getAllProductRatings() {
        log.debug("Fetching all product ratings.");
        return productRatingRepository.findAll();
    }

    @Override
    @Transactional // Добавляем транзакционность
    public void saveProductRatingRecord(Integer productId, Integer rating) {
        log.info("Processing rating record for productId: {}, rating: {}", productId, rating);
        ProductRatingRecord ratingRecord = new ProductRatingRecord();
        ratingRecord.setProductId(productId);
        ratingRecord.setRating(rating);
        productRatingRecordRepository.save(ratingRecord);
        log.debug("Rating record saved. Updating average rating for productId: {}", productId);
        // Обновляем средний рейтинг (этот метод теперь тоже обновим)
        updateAverageRating(productId);
        log.info("Finished processing rating record for productId: {}", productId);
    }

    // --- НОВЫЙ МЕТОД для обработки события избранного ---
    @Override
    @Transactional // Добавляем транзакционность
    public void incrementFavouriteCount(Integer productId) {
        log.info("Processing favourite event for productId: {}", productId);
        // Находим или создаем ProductRating
        ProductRating productRating = productRatingRepository.findById(productId)
                .orElseGet(() -> createInitialProductRating(productId)); // Используем хелпер

        // Увеличиваем счетчик (инициализируем, если он null)
        int currentCount = productRating.getFavouriteCount() != null ? productRating.getFavouriteCount() : 0;
        productRating.setFavouriteCount(currentCount + 1);

        // Сохраняем изменения
        productRatingRepository.save(productRating);
        log.info("Incremented favourite count for productId: {}. New count: {}", productId, productRating.getFavouriteCount());
    }
    // ---------------

    // Сделаем этот метод приватным или protected, т.к. он вызывается только внутри сервиса
    // Аннотация @Transactional здесь необязательна, если он ВСЕГДА вызывается из @Transactional методов (saveProductRatingRecord)
    // Но для надежности можно оставить.
    @Transactional
    protected void updateAverageRating(Integer productId) {
        log.debug("Updating average rating for productId: {}", productId);
        // Находим или создаем ProductRating
        ProductRating productRating = productRatingRepository.findById(productId)
                .orElseGet(() -> createInitialProductRating(productId)); // Используем хелпер

        // Считаем среднее по записям
        List<ProductRatingRecord> ratingRecords = productRatingRecordRepository.findAllByProductId(productId);
        log.debug("Found {} rating records for productId: {}", ratingRecords.size(), productId);

        if (!ratingRecords.isEmpty()) {
            double sum = ratingRecords.stream()
                    .mapToInt(ProductRatingRecord::getRating)
                    .sum();
            double averageRating = sum / ratingRecords.size();
            // Округляем до целого (или используйте double/BigDecimal, если нужна точность)
            productRating.setRatingAverage((int) Math.round(averageRating));
            productRating.setRatingCount(ratingRecords.size());
            log.debug("Calculated average rating: {}, count: {}", productRating.getRatingAverage(), productRating.getRatingCount());
        } else {
            // Если оценок нет, сбрасываем рейтинг (но НЕ счетчик избранного!)
            productRating.setRatingAverage(0);
            productRating.setRatingCount(0);
            log.debug("No rating records found, resetting average rating and count.");
        }

        // Сохраняем изменения (средний рейтинг, количество оценок, и возможно, начальный favouriteCount)
        productRatingRepository.save(productRating);
        log.debug("Saved updated product rating for productId: {}", productId);
    }

    // Вспомогательный метод для создания начального ProductRating
    private ProductRating createInitialProductRating(Integer productId) {
        log.debug("Creating initial ProductRating entry for productId: {}", productId);
        ProductRating productRating = new ProductRating();
        productRating.setProductId(productId);
        productRating.setRatingAverage(0); // Начальный рейтинг
        productRating.setRatingCount(0);   // Начальное количество оценок
        productRating.setFavouriteCount(0); // <-- Инициализируем новое поле
        // Сохранение произойдет в вызывающем методе (updateAverageRating или incrementFavouriteCount)
        // Не сохраняем здесь, чтобы избежать лишних транзакций, если findById сработает
        return productRating;
    }
}