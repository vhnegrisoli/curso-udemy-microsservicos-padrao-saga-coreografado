package br.com.microservices.orchestrated.inventoryservice.core.saga;

import br.com.microservices.orchestrated.inventoryservice.core.dto.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class SagaHandler {

    protected Event event;

    public abstract Event getEvent();
    public abstract String getTopic();
}
