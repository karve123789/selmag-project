package ag.selm.catalogue.kafka;

import ag.selm.NewProductEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProductProducer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProductProducer.class);

    private final KafkaTemplate<String, NewProductEvent> kafkaTemplate;

    public void sendNewProductEvent(NewProductEvent event) {
        logger.info("Sending new product event: {}", event);
        kafkaTemplate.send("new-products", event);
    }
}