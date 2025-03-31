package ag.selm.recommendationservice.kafka;

import ag.selm.NewProductEvent;
import ag.selm.recommendationservice.entity.ProductRating;
import ag.selm.recommendationservice.entity.ProductRatingRecord;
import ag.selm.recommendationservice.repository.ProductRatingRepository;
import ag.selm.recommendationservice.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Важно добавить транзакционность

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KafkaListeners {

    private static final Logger logger = LoggerFactory.getLogger(KafkaListeners.class);

    private final RatingService ratingService;

    @KafkaListener(topics = "new-products", groupId = "recommendation-service", containerFactory = "kafkaListenerContainerFactory")
    public void newProductListener(@Payload NewProductEvent event) {
        try {
            logger.info("Received new product event: {}", event);
            logger.info("Received Product ID: {}", event.getProductId());

            ratingService.saveProductRatingRecord(event.getProductId(), 0);

        } catch (Exception e) {
            logger.error("Error processing new product event: {}", event, e); // Используйте event, а не event.toString()
        }
    }
}