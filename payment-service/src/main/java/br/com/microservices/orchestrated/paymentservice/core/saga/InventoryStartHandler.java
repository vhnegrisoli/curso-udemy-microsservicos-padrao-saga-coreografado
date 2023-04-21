package br.com.microservices.orchestrated.paymentservice.core.saga;

import br.com.microservices.orchestrated.paymentservice.config.kafka.TopicSingleton;
import br.com.microservices.orchestrated.paymentservice.core.dto.Event;

public class InventoryStartHandler extends SagaHandler {

    public InventoryStartHandler(Event event) {
        super(event);
    }

    @Override
    public Event getEvent() {
        return event;
    }

    @Override
    public String getTopic() {
        return TopicSingleton.getInventoryStartTopic();
    }
}
