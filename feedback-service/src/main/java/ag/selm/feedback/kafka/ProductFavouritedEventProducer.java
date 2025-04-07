// Файл: ag/selm/feedback/kafka/ProductFavouritedEventProducer.java
package ag.selm.feedback.kafka;

import ag.selm.ProductFavouritedEvent;
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
public class ProductFavouritedEventProducer {

    // Внедрение через final поле и конструктор (Lombok)
    private final KafkaTemplate<String, ProductFavouritedEvent> kafkaTemplate;

    @Value("${app.kafka.topic.favourite-products-events}")
    private String topicName;

    public void sendProductFavouritedEvent(String key, ProductFavouritedEvent event) {
        log.info("Attempting to send event to Kafka topic '{}': {}", topicName, event);
        try {
            // Прямое использование kafkaTemplate
            CompletableFuture<SendResult<String, ProductFavouritedEvent>> future =
                    kafkaTemplate.send(topicName, key, event);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Sent ProductFavouritedEvent to Kafka topic='{}' partition={} offset={}: {}",
                            topicName,
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset(),
                            event);
                } else {
                    log.error("Failed to send ProductFavouritedEvent to Kafka topic='{}': {}. Error: {}",
                            topicName, event, ex.getMessage(), ex);
                }
            });
        } catch (Exception e) {
            log.error("Unexpected error sending ProductFavouritedEvent to Kafka topic='{}': {}. Error: {}",
                    topicName, event, e.getMessage(), e);
        }
    }
}