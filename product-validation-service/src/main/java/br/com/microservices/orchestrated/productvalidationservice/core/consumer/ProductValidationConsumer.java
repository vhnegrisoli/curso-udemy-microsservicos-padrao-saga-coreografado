package br.com.microservices.orchestrated.productvalidationservice.core.consumer;

import br.com.microservices.orchestrated.productvalidationservice.core.service.ProductService;
import br.com.microservices.orchestrated.productvalidationservice.core.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductValidationConsumer {

    private final ProductService productService;
    private final JsonUtil jsonUtil;

    @KafkaListener(
        groupId = "${spring.kafka.consumer.group-id}",
        topics = "${spring.kafka.topic.product-validation-start}"
    )
    public void consumerSuccessTopic(String payload) {
        log.info("Recieving success event {} from product-validation-start topic", payload);
        var event = jsonUtil.toEvent(payload);
        productService.validateExistingProducts(event);
    }

    @KafkaListener(
        groupId = "${spring.kafka.consumer.group-id}",
        topics = "${spring.kafka.topic.product-validation-fail}"
    )
    public void consumerFailTopic(String payload) {
        log.info("Recieving rollback event {} from product-validation-fail topic", payload);
        var event = jsonUtil.toEvent(payload);
        productService.rollbackEvent(event);
    }
}
