package ag.selm.feedback.service;

import ag.selm.feedback.entity.ProductReview;
import ag.selm.feedback.event.ProductReviewCreatedEvent; // <-- Импорт Spring Event
import ag.selm.feedback.repository.ProductReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher; // <-- Импорт Publisher
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultProductReviewsService implements ProductReviewsService {

    private final ProductReviewRepository productReviewRepository;
    private final ApplicationEventPublisher eventPublisher; // <-- Внедряем Publisher

    // УБИРАЕМ зависимость от ProductReviewedEventProducer

    @Override
    public Mono<ProductReview> createProductReview(int productId, int rating, String review, String userId) {
        ProductReview productReview = new ProductReview(UUID.randomUUID(), productId, rating, review, userId);

        return this.productReviewRepository.save(productReview)
                .doOnSuccess(savedReview -> {
                    log.info("Product review saved successfully. Publishing event: {}", savedReview);
                    // Публикуем Spring событие вместо прямого вызова Kafka
                    eventPublisher.publishEvent(new ProductReviewCreatedEvent(this, savedReview));
                })
                .doOnError(error -> log.error("Failed to save product review for product {}: {}", productId, error.getMessage(), error));
    }

    @Override
    public Flux<ProductReview> findProductReviewsByProduct(int productId) {
        return this.productReviewRepository.findAllByProductId(productId);
    }
}