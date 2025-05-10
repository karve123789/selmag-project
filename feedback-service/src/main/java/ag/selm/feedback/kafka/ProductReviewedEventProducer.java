// Файл: ag/selm/feedback/kafka/ProductReviewedEventProducer.java
package ag.selm.feedback.kafka;

import ag.selm.ProductReviewedEvent;
import lombok.RequiredArgsConstructor; // Убедись, что импорт есть
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor // Используем Lombok для конструктора
public class ProductReviewedEventProducer {

    // Внедрение через final поле и конструктор (Lombok)
    private final KafkaTemplate<String, ProductReviewedEvent> kafkaTemplate;

    @Value("${app.kafka.topic.product-reviews}")
    private String topicName;

    public void sendProductReviewedEvent(String key, ProductReviewedEvent event) {
        log.info("Attempting to send event to Kafka topic '{}': {}", topicName, event);
        try {
            // Прямое использование kafkaTemplate
            CompletableFuture<SendResult<String, ProductReviewedEvent>> future =
                    kafkaTemplate.send(topicName, key, event);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Sent ProductReviewedEvent to Kafka topic='{}' partition={} offset={}: {}",
                            topicName,
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset(),
                            event);
                } else {
                    log.error("Failed to send ProductReviewedEvent to Kafka topic='{}': {}. Error: {}",
                            topicName, event, ex.getMessage(), ex);
                }
            });
        } catch (Exception e) {
            log.error("Unexpected error sending ProductReviewedEvent to Kafka topic='{}': {}. Error: {}",
                    topicName, event, e.getMessage(), e);
        }
    }
}