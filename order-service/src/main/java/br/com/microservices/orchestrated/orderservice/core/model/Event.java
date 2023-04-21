package br.com.microservices.orchestrated.orderservice.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.ObjectUtils.isEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notification")
public class Event {

    @Id
    private String id;
    private String transactionId;
    private String orderId;
    private Object payload;
    private String source;
    private String status;
    private String currentExecuted;
    private List<Object> eventHistory;
    private LocalDateTime createdAt;

    public void createHistory(String message) {
        addToHistory(History
            .builder()
            .source(this.getSource())
            .status(this.getStatus())
            .message(message)
            .createdAt(LocalDateTime.now())
            .build());
    }

    private void addToHistory(History history) {
        if (isEmpty(eventHistory)) {
            eventHistory = new ArrayList<>();
        }
        eventHistory.add(history);
    }
}
