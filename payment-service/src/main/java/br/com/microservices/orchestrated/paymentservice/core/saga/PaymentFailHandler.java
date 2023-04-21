package br.com.microservices.orchestrated.paymentservice.core.saga;

import br.com.microservices.orchestrated.paymentservice.config.kafka.TopicSingleton;
import br.com.microservices.orchestrated.paymentservice.core.dto.Event;

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
