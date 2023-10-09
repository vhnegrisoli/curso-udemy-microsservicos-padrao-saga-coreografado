package br.com.microservices.choreography.orderservice.core.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SagaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.topic.product-validation-success}")
    private String productValidationSuccessTopic;

    public void sendEvent(String payload) {
        try {
            log.info("Sending event to topic {} with data {}", productValidationSuccessTopic, payload);
            kafkaTemplate.send(productValidationSuccessTopic, payload);
        } catch (Exception ex) {
            log.error("Error trying to send data to topic {} with data {}", productValidationSuccessTopic, payload, ex);
        }
    }
}
