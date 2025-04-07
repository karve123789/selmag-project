package ag.selm.feedback.listener;

import ag.selm.ProductFavouritedEvent; // <-- Импорт Kafka DTO
import ag.selm.feedback.entity.FavouriteProduct;
import ag.selm.feedback.event.FavouriteProductAddedEvent; // <-- Импорт Spring Event
import ag.selm.feedback.kafka.ProductFavouritedEventProducer; // <-- Импорт Kafka Producer
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FavouriteProductKafkaNotifier {

    private final ProductFavouritedEventProducer productFavouritedEventProducer; // <-- Внедряем Producer

    @EventListener // <-- Аннотация для прослушивания
    public void handleFavouriteProductAdded(FavouriteProductAddedEvent event) {
        FavouriteProduct favouriteProduct = event.getFavouriteProduct();
        log.info("Received FavouriteProductAddedEvent for product ID: {}, user ID: {}. Sending to Kafka.",
                favouriteProduct.getProductId(), favouriteProduct.getUserId());

        // Создаем Kafka DTO из данных Spring события
        ProductFavouritedEvent kafkaEvent = new ProductFavouritedEvent(
                favouriteProduct.getProductId(),
                favouriteProduct.getUserId()
        );

        // Отправляем в Kafka
        productFavouritedEventProducer.sendProductFavouritedEvent(favouriteProduct.getUserId(), kafkaEvent);
    }
}