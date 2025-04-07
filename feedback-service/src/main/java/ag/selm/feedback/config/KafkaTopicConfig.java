package ag.selm.feedback.config; // Или другой подходящий пакет

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
// import org.apache.kafka.common.config.TopicConfig; // Раскомментируйте, если нужны доп. опции
// import java.util.concurrent.TimeUnit;             // Раскомментируйте, если нужны доп. опции

@Configuration
public class KafkaTopicConfig {


    @Value("${app.kafka.topic.product-reviews}")
    private String productReviewsTopicName;

    @Value("${app.kafka.topic.favourite-products-events}")
    private String favouriteProductsEventsTopicName;


    @Value("${app.kafka.topic.default-partitions:3}") // Значение по умолчанию 3, если не задано
    private int defaultPartitions;

    @Value("${app.kafka.topic.default-replicas:1}")  // Значение по умолчанию 1 (подходит для одного брокера)
    private int defaultReplicas;

    // --- Бин для создания топика "product-reviews" ---
    @Bean
    public NewTopic productReviewsTopic() {
        return TopicBuilder.name(productReviewsTopicName)
                .partitions(defaultPartitions)
                .replicas(defaultReplicas)
                .build();
    }

    // --- Бин для создания топика "favourite-products-events" ---
    @Bean
    public NewTopic favouriteProductsEventsTopic() {
        return TopicBuilder.name(favouriteProductsEventsTopicName)
                .partitions(defaultPartitions)
                .replicas(defaultReplicas)
                .build();
    }
}