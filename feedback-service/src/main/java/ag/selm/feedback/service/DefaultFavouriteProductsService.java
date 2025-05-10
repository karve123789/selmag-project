package ag.selm.feedback.service;

import ag.selm.feedback.entity.FavouriteProduct;
import ag.selm.feedback.event.FavouriteProductAddedEvent; // <-- Импорт Spring Event
import ag.selm.feedback.repository.FavouriteProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher; // <-- Импорт Publisher
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultFavouriteProductsService implements FavouriteProductsService {

    private final FavouriteProductRepository favouriteProductRepository;
    private final ApplicationEventPublisher eventPublisher; // <-- Внедряем Publisher

    // УБИРАЕМ зависимость от ProductFavouritedEventProducer

    @Override
    public Mono<FavouriteProduct> addProductToFavourites(int productId, String userId) {
        FavouriteProduct favouriteProduct = new FavouriteProduct(UUID.randomUUID(), productId, userId);

        return this.favouriteProductRepository.save(favouriteProduct)
                .doOnSuccess(savedFavourite -> {
                    log.info("Product {} added to favourites for user {}. Publishing event.",
                            productId, userId);
                    // Публикуем Spring событие вместо прямого вызова Kafka
                    eventPublisher.publishEvent(new FavouriteProductAddedEvent(this, savedFavourite));
                })
                .doOnError(error -> log.error("Failed to add product {} to favourites for user {}: {}",
                        productId, userId, error.getMessage(), error));
    }

    @Override
    public Mono<Void> removeProductFromFavourites(int productId, String userId) {
        // Логика удаления остается прежней.
        // Если нужно отправлять событие об удалении, делаем аналогично добавлению.
        return this.favouriteProductRepository.deleteByProductIdAndUserId(productId, userId)
                .doOnSuccess(v -> log.info("Product {} removed from favourites for user {}", productId, userId))
                .doOnError(error -> log.error("Failed to remove product {} from favourites for user {}: {}",
                        productId, userId, error.getMessage()));
    }

    @Override
    public Mono<FavouriteProduct> findFavouriteProductByProduct(int productId, String userId) {
        return this.favouriteProductRepository.findByProductIdAndUserId(productId, userId);
    }

    @Override
    public Flux<FavouriteProduct> findFavouriteProducts(String userId) {
        return this.favouriteProductRepository.findAllByUserId(userId);
    }
}