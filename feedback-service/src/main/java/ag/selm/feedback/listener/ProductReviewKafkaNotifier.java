package ag.selm.feedback.listener;

import ag.selm.ProductReviewedEvent; // <-- Импорт Kafka DTO
import ag.selm.feedback.entity.ProductReview;
import ag.selm.feedback.event.ProductReviewCreatedEvent; // <-- Импорт Spring Event
import ag.selm.feedback.kafka.ProductReviewedEventProducer; // <-- Импорт Kafka Producer
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductReviewKafkaNotifier {

    private final ProductReviewedEventProducer productReviewedEventProducer; // <-- Внедряем Producer

    @EventListener // <-- Аннотация для прослушивания
    public void handleProductReviewCreated(ProductReviewCreatedEvent event) {
        ProductReview review = event.getProductReview();
        log.info("Received ProductReviewCreatedEvent for product ID: {}, user ID: {}, rating: {}. Sending to Kafka.",
                review.getProductId(), review.getUserId(), review.getRating());

        // Создаем Kafka DTO из данных Spring события
        ProductReviewedEvent kafkaEvent = new ProductReviewedEvent(
                review.getProductId(),
                review.getUserId(),
                review.getRating()
        );

        // Отправляем в Kafka (ключ - ID продукта)
        productReviewedEventProducer.sendProductReviewedEvent(String.valueOf(review.getProductId()), kafkaEvent);
    }
}