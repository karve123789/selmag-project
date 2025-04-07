package ag.selm.catalogue.config; // Или другой подходящий пакет

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    // Лучшая практика: вынести имя топика в application.yml
    @Value("${app.kafka.topic.new-product}")
    private String newProductTopicName;

    @Bean
    public NewTopic newProductTopic() {
        return TopicBuilder.name(newProductTopicName)
                .partitions(3)       // Пример: задаем 3 партиции для возможности масштабирования
                .replicas(1)         // У вас 1 брокер, поэтому репликация 1
                // .config(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(TimeUnit.DAYS.toMillis(7))) // Пример доп. опции: хранить сообщения неделю
                .build();
    }

    // Здесь можно добавить бины NewTopic для других топиков, если они понадобятся
}