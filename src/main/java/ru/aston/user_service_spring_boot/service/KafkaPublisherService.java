package ru.aston.user_service_spring_boot.service;

import ru.aston.user_service_spring_boot.model.UserKafkaEvents;

public interface KafkaPublisherService {
    void sendAsyncKafkaMessage(UserKafkaEvents userKafkaEvents, String email);
}
