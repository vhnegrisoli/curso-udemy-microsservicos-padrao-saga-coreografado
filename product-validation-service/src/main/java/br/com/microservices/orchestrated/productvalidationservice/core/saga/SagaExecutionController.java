package br.com.microservices.orchestrated.productvalidationservice.core.saga;

import br.com.microservices.orchestrated.productvalidationservice.core.producer.KafkaProducer;
import br.com.microservices.orchestrated.productvalidationservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class SagaExecutionController {

    private final JsonUtil jsonUtil;
    private final KafkaProducer producer;

    public void handleSaga(SagaHandler handler) {
        try {
            var jsonEvent = jsonUtil.toJson(handler.getEvent());
            producer.sendEvent(jsonEvent, handler.getTopic());
        } catch (Exception ex) {
            log.error("Error trying to send to producer: {}", ex.getMessage());
        }
    }
}
