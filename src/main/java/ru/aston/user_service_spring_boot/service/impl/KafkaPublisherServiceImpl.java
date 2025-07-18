package ru.aston.user_service_spring_boot.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import ru.aston.user_service_spring_boot.model.UserKafkaEvents;
import ru.aston.user_service_spring_boot.service.KafkaPublisherService;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaPublisherServiceImpl implements KafkaPublisherService {

    private final Logger LOGGER = LoggerFactory.getLogger(KafkaPublisherServiceImpl.class);
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaPublisherServiceImpl(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAsyncKafkaMessage(UserKafkaEvents userKafkaEvents, String email){
        String kafkaMessage = userKafkaEvents + ":" + email;
        LOGGER.info("trying to send message to kafka {}", kafkaMessage);
        CompletableFuture<SendResult<String, String>> future =
                kafkaTemplate.send("user-create-delete-events-topic", null, kafkaMessage);
        future.whenComplete((result, exception) -> {
            if (exception != null) {
                LOGGER.error("Failed to send message to kafka {}", exception.getMessage());
            } else {
                LOGGER.info("Message sent to kafka successfully {}", result.getRecordMetadata());
            }
        });
    }

}
