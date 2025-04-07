package ag.selm.recommendationservice.kafka;

import ag.selm.ProductFavouritedEvent;
import ag.selm.ProductReviewedEvent;
import ag.selm.recommendationservice.service.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload; // Импорт Payload
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaListeners {

    private final RatingService ratingService; // Инжектируем наш сервис

    // --- Слушатель для событий ОТЗЫВОВ (ProductReviewedEvent) ---
    @KafkaListener(
            topics = "${app.kafka.topic.product-reviews}", // Берем имя топика из properties
            groupId = "${spring.kafka.consumer.group-id}", // Берем ID группы из properties
            containerFactory = "productReviewedKafkaListenerContainerFactory" // Указываем фабрику для этого типа
    )
    public void listenProductReviews(@Payload(required = false) ProductReviewedEvent event) { // required=false + проверка на null
        log.info("Received message from product-reviews topic");
        if (event == null) {
            log.warn("Received null message from product-reviews topic. Skipping.");
            return; // Игнорируем null сообщения (могут прийти из-за ошибок десериализации с ErrorHandlingDeserializer)
        }
        log.debug("Received ProductReviewedEvent: {}", event);
        try {
            if (event.getProductId() != null && event.getRating() != null) {
                // Вызываем метод сервиса для сохранения отзыва и обновления среднего
                ratingService.saveProductRatingRecord(event.getProductId(), event.getRating());
                // Лог об успехе теперь внутри сервиса
            } else {
                log.warn("Received incomplete ProductReviewedEvent (missing productId or rating): {}", event);
            }
        } catch (Exception e) {
            // Логируем ошибку, но не пробрасываем дальше, чтобы не остановить listener
            log.error("Error processing ProductReviewedEvent: {}. Error: {}", event, e.getMessage(), e);
            // Здесь можно реализовать отправку в Dead Letter Queue (DLQ), если нужно
        }
    }

    // --- Слушатель для событий ИЗБРАННОГО (ProductFavouritedEvent) ---
    @KafkaListener(
            topics = "${app.kafka.topic.favourite-products-events}", // Другой топик
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "productFavouritedKafkaListenerContainerFactory" // Другая фабрика
    )
    public void listenFavouriteProducts(@Payload(required = false) ProductFavouritedEvent event) {
        log.info("Received message from favourite-products-events topic");
        if (event == null) {
            log.warn("Received null message from favourite-products-events topic. Skipping.");
            return;
        }
        log.debug("Received ProductFavouritedEvent: {}", event);
        try {
            // Проверяем, что productId не null (userId в событии нам может быть не нужен для этой логики)
            if (event.getProductId() != null) {
                // Вызываем НОВЫЙ метод сервиса для увеличения счетчика избранного
                ratingService.incrementFavouriteCount(event.getProductId());
                // Лог об успехе теперь внутри сервиса
            } else {
                log.warn("Received incomplete ProductFavouritedEvent (missing productId): {}", event);
            }
        } catch (Exception e) {
            log.error("Error processing ProductFavouritedEvent: {}. Error: {}", event, e.getMessage(), e);
            // Рассмотрите DLQ
        }
    }
}

    // Старый слушатель для NewProductEvent удален
    /*
    @KafkaListener(topics = "${app.kafka.topic.new-product}", ...)
    public void newProductListener(@Payload NewProductEvent event) { ... }
    */


//package ag.selm.recommendationservice.kafka;
//
//import ag.selm.NewProductEvent;
//import ag.selm.recommendationservice.entity.ProductRating;
//import ag.selm.recommendationservice.entity.ProductRatingRecord;
//import ag.selm.recommendationservice.repository.ProductRatingRepository;
//import ag.selm.recommendationservice.service.RatingService;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional; // Важно добавить транзакционность
//
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class KafkaListeners {
//
//    private static final Logger logger = LoggerFactory.getLogger(KafkaListeners.class);
//
//    private final RatingService ratingService;
//
//    @KafkaListener(topics = "${app.kafka.topic.new-product}",
//            groupId = "${spring.kafka.consumer.group-id}", // Также хорошая практика
//            containerFactory = "kafkaListenerContainerFactory")
//    public void newProductListener(@Payload NewProductEvent event) {
//        try {
//            logger.info("Received new product event from topic '${app.kafka.topic.new-product}': {}", event); // Можно и так в логе
//            logger.info("Received Product ID: {}", event.getProductId());
//            ratingService.saveProductRatingRecord(event.getProductId(), 0);
//        } catch (Exception e) {
//            logger.error("Error processing new product event: {}", event, e);
//        }
//    }
//}