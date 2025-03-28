package ag.selm.recommendationservice.kafka;

import ag.selm.NewProductEvent;
import ag.selm.recommendationservice.entity.ProductRating;
import ag.selm.recommendationservice.repository.ProductRatingRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaListeners {

    private static final Logger logger = LoggerFactory.getLogger(KafkaListeners.class);

    private final ProductRatingRepository productRatingRepository;

    @KafkaListener(topics = "new-products", groupId = "recommendation-service", containerFactory = "kafkaListenerContainerFactory")
    public void newProductListener(@Payload NewProductEvent event) {
        try {
            logger.info("Received new product event: {}", event);
            logger.info("Received Product ID: {}", event.getProductId()); // Дополнительная проверка

            ProductRating productRating = new ProductRating();
            productRating.setProductId(event.getProductId());
            productRating.setRatingAverage(0);
            productRating.setRatingCount(0);
            productRatingRepository.save(productRating);

        } catch (Exception e) {
            logger.error("Error processing new product event: {}", event.toString(), e); // toString()
        }
    }
}