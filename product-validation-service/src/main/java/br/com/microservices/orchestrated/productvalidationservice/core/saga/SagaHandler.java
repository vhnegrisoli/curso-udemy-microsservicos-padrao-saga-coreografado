package br.com.microservices.orchestrated.productvalidationservice.core.saga;

import br.com.microservices.orchestrated.productvalidationservice.core.dto.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class SagaHandler {

    protected Event event;

    public abstract Event getEvent();
    public abstract String getTopic();
}
