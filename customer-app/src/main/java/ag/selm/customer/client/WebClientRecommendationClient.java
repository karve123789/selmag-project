package ag.selm.customer.client;

import ag.selm.customer.entity.ProductStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor // Для инжекции WebClient через конструктор
@Slf4j
// @Component // Если не создаем через @Bean в ClientConfig
public class WebClientRecommendationClient implements RecommendationClient {

    private final WebClient webClient; // Будет инжектирован настроенный WebClient

    @Override
    public Flux<ProductStats> findAllProductStats() {
        log.debug("Requesting all product stats from recommendation-service");
        return this.webClient
                .get()
                .uri("/ratings/api") // Путь к эндпоинту в recommendation-service
                .retrieve()
                .bodyToFlux(ProductStats.class) // Десериализуем в наш DTO
                .onErrorResume(WebClientResponseException.class, exception -> {
                    // Обработка ошибок (например, если сервис недоступен)
                    log.error("Error fetching product stats: {} (Status: {})",
                            exception.getMessage(), exception.getStatusCode(), exception);
                    return Flux.empty(); // Возвращаем пустой поток при ошибке
                })
                .doOnComplete(() -> log.debug("Successfully fetched all product stats"))
                .doOnError(error -> log.error("Error processing product stats stream", error)); // Лог на всякий случай
    }
}