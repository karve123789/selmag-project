// Файл: ag/selm/feedback/config/KafkaProducerConfiguration.java
package ag.selm.feedback.config;

import ag.selm.ProductFavouritedEvent;
import ag.selm.ProductReviewedEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// import org.springframework.context.annotation.DependsOn; // DependsOn больше не нужен
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    // Метод producerConfigs() УДАЛЕН

    // --- Фабрики ---

    @Bean
    // @DependsOn("producerConfigs") // УДАЛЕНО
    public ProducerFactory<String, ProductReviewedEvent> productReviewedEventProducerFactory() {
        // Создаем конфигурацию ПРЯМО ЗДЕСЬ
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class); // Общий JsonSerializer

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    // @DependsOn("producerConfigs") // УДАЛЕНО
    public ProducerFactory<String, ProductFavouritedEvent> productFavouritedEventProducerFactory() {
        // Создаем конфигурацию ПРЯМО ЗДЕСЬ (повторно)
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class); // Общий JsonSerializer

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    // --- Темплейты ---
    // Зависимости от фабрик остаются, как были

    @Bean
    public KafkaTemplate<String, ProductReviewedEvent> productReviewedEventKafkaTemplate(
            ProducerFactory<String, ProductReviewedEvent> productReviewedEventProducerFactory) {
        return new KafkaTemplate<>(productReviewedEventProducerFactory);
    }

    @Bean
    public KafkaTemplate<String, ProductFavouritedEvent> productFavouritedEventKafkaTemplate(
            ProducerFactory<String, ProductFavouritedEvent> productFavouritedEventProducerFactory) {
        return new KafkaTemplate<>(productFavouritedEventProducerFactory);
    }
}