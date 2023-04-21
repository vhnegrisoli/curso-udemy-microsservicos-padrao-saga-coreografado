package br.com.microservices.orchestrated.inventoryservice.core.saga;

import br.com.microservices.orchestrated.inventoryservice.config.kafka.TopicSingleton;
import br.com.microservices.orchestrated.inventoryservice.core.dto.Event;

public class PaymentFailHandler extends SagaHandler {

    public PaymentFailHandler(Event event) {
        super(event);
    }

    @Override
    public Event getEvent() {
        return event;
    }

    @Override
    public String getTopic() {
        return TopicSingleton.getPaymentFailTopic();
    }
}
