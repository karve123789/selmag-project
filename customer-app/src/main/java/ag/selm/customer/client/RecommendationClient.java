package ag.selm.customer.client;

import ag.selm.customer.entity.ProductStats;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface RecommendationClient {
    Flux<ProductStats> findAllProductStats();
}
