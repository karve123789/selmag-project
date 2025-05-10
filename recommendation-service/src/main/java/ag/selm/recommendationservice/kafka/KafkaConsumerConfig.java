package ag.selm.recommendationservice.kafka;

import ag.selm.ProductFavouritedEvent; // <-- Импорт второго типа события
import ag.selm.ProductReviewedEvent;   // <-- Импорт первого типа события
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler; // Обработчик ошибок по умолчанию
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer; // Важно!
import org.springframework.kafka.support.serializer.JsonDeserializer; // Важно!

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka // Включаем поддержку Kafka Listener'ов
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    // --- Общие настройки Consumer ---
    private Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        // Ключ и Значение Десериализаторы будут переопределены ниже специфичными
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class); // Используем ErrorHandling
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class); // Используем ErrorHandling
        // Настройки для ErrorHandlingDeserializer
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class); // Десериализатор ключа
        // Value deserializer будет указан в конкретных фабриках
        return props;
    }

    // --- Конфигурация для ProductReviewedEvent ---

    @Bean
    public ConsumerFactory<String, ProductReviewedEvent> productReviewedConsumerFactory() {
        Map<String, Object> config = consumerConfigs(); // Берем общие настройки
        // Настраиваем JsonDeserializer для ProductReviewedEvent
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName()); // Десериализатор значения
        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, ProductReviewedEvent.class.getName()); // Указываем класс
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "ag.selm"); // Доверяем пакету
//        config.put(JsonDeserializer.USE_TYPE_HEADERS, "false"); // Обычно false, если тип указан выше
//
        // Создаем фабрику с типизированными десериализаторами через ErrorHandlingDeserializer
        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean("productReviewedKafkaListenerContainerFactory") // Уникальное имя бина
    public ConcurrentKafkaListenerContainerFactory<String, ProductReviewedEvent> productReviewedKafkaListenerContainerFactory(
            ConsumerFactory<String, ProductReviewedEvent> productReviewedConsumerFactory // Инжектируем правильную фабрику
    ) {
        ConcurrentKafkaListenerContainerFactory<String, ProductReviewedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(productReviewedConsumerFactory);
        factory.setCommonErrorHandler(new DefaultErrorHandler()); // Стандартный обработчик ошибок
        return factory;
    }

    // --- Конфигурация для ProductFavouritedEvent ---

    @Bean
    public ConsumerFactory<String, ProductFavouritedEvent> productFavouritedConsumerFactory() {
        Map<String, Object> config = consumerConfigs(); // Берем общие настройки
        // Настраиваем JsonDeserializer для ProductFavouritedEvent
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, ProductFavouritedEvent.class.getName()); // Указываем другой класс
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "ag.selm"); // Тот же пакет
//        config.put(JsonDeserializer.USE_TYPE_HEADERS, "false");

        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean("productFavouritedKafkaListenerContainerFactory") // Другое уникальное имя бина
    public ConcurrentKafkaListenerContainerFactory<String, ProductFavouritedEvent> productFavouritedKafkaListenerContainerFactory(
            ConsumerFactory<String, ProductFavouritedEvent> productFavouritedConsumerFactory // Инжектируем фабрику для избранного
    ) {
        ConcurrentKafkaListenerContainerFactory<String, ProductFavouritedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(productFavouritedConsumerFactory);
        factory.setCommonErrorHandler(new DefaultErrorHandler());
        return factory;
    }
}
    // Старые бины для NewProductEvent можно удалить, если они больше не используются.
    /*
    @Bean
    public ConsumerFactory<String, NewProductEvent> consumerFactory() { ... }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NewProductEvent> kafkaListenerContainerFactory() { ... }
    */

//package ag.selm.recommendationservice.kafka;
//
//import ag.selm.NewProductEvent; // Import общего класса
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.annotation.EnableKafka;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//import org.springframework.kafka.listener.DefaultErrorHandler;
//import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
//import org.springframework.kafka.support.serializer.JsonDeserializer;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//@EnableKafka
//public class KafkaConsumerConfig {
//
//    @Value("${spring.kafka.bootstrap-servers}")
//    private String bootstrapServers;
//
//    @Value("${spring.kafka.consumer.group-id}")
//    private String groupId;
//
//    @Bean
//    public ConsumerFactory<String, NewProductEvent> consumerFactory() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
//
//        // Value Deserializer
//        JsonDeserializer<NewProductEvent> valueJsonDeserializer = new JsonDeserializer<>(NewProductEvent.class);
//        valueJsonDeserializer.addTrustedPackages("ag.selm"); // Добавляем только общий пакет
//        ErrorHandlingDeserializer<NewProductEvent> valueDeserializer = new ErrorHandlingDeserializer<>(valueJsonDeserializer);
//
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer); // Используем экземпляр deserializer напрямую
//
//
//        // Key Deserializer
//        ErrorHandlingDeserializer<String> keyDeserializer = new ErrorHandlingDeserializer<>(new StringDeserializer());
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer); // Используем экземпляр deserializer напрямую
//
//
//        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), valueDeserializer); // Передаем десериализаторы consumerFactory
//    }
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, NewProductEvent> kafkaListenerContainerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, NewProductEvent> factory =
//                new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(consumerFactory());
//        factory.setCommonErrorHandler(new DefaultErrorHandler());
//        return factory;
//    }
//}