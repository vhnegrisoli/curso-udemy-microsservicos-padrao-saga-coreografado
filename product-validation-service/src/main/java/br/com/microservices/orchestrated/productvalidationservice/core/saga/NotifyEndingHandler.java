package br.com.microservices.orchestrated.productvalidationservice.core.saga;

import br.com.microservices.orchestrated.productvalidationservice.config.kafka.TopicSingleton;
import br.com.microservices.orchestrated.productvalidationservice.core.dto.Event;

public class NotifyEndingHandler extends SagaHandler {

    public NotifyEndingHandler(Event event) {
        super(event);
    }

    @Override
    public Event getEvent() {
        return event;
    }

    @Override
    public String getTopic() {
        return TopicSingleton.getNotifyEndingTopic();
    }
}
