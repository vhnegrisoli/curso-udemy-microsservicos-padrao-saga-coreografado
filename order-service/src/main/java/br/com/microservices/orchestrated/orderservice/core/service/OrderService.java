package br.com.microservices.orchestrated.orderservice.core.service;

import br.com.microservices.orchestrated.orderservice.core.dto.OrderRequest;
import br.com.microservices.orchestrated.orderservice.core.enums.ECurrentService;
import br.com.microservices.orchestrated.orderservice.core.enums.ESagaExecution;
import br.com.microservices.orchestrated.orderservice.core.enums.ESagaStatus;
import br.com.microservices.orchestrated.orderservice.core.model.Event;
import br.com.microservices.orchestrated.orderservice.core.model.Order;
import br.com.microservices.orchestrated.orderservice.core.producer.SagaProducer;
import br.com.microservices.orchestrated.orderservice.core.repository.OrderRepository;
import br.com.microservices.orchestrated.orderservice.core.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private static final String TRANSACTION_ID_PATTERN = "%s_%s";

    private final SagaProducer producer;
    private final JsonUtil jsonUtil;
    private final OrderRepository repository;
    @Value("${spring.kafka.topic.product-validation-start}")
    private String productValidationStartTopic;

    public Order createOrder(OrderRequest orderRequest) {
        var order = new Order();
        order.setProducts(orderRequest.getProducts());
        order.setCreatedAt(LocalDateTime.now());
        order.setTransactionId(
            String.format(TRANSACTION_ID_PATTERN, Instant.now().toEpochMilli(), UUID.randomUUID())
        );
        repository.save(order);
        producer.sendEvent(jsonUtil.toJson(createPayload(order)), productValidationStartTopic);
        return order;
    }

    private Event createPayload(Order order) {
        var event = Event
            .builder()
            .orderId(order.getId())
            .transactionId(order.getTransactionId())
            .status(ESagaStatus.SUCCESS.name())
            .source(ECurrentService.ORDER_SERVICE.name())
            .currentExecuted(ESagaExecution.CURRENT_IS_SUCCESS.name())
            .payload(order)
            .createdAt(LocalDateTime.now())
            .build();
        event.createHistory("Saga started!");
        return event;
    }
}
