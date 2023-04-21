package br.com.microservices.orchestrated.productvalidationservice.core.service;

import br.com.microservices.orchestrated.productvalidationservice.config.exception.ValidationException;
import br.com.microservices.orchestrated.productvalidationservice.core.dto.Event;
import br.com.microservices.orchestrated.productvalidationservice.core.dto.History;
import br.com.microservices.orchestrated.productvalidationservice.core.dto.OrderProducts;
import br.com.microservices.orchestrated.productvalidationservice.core.enums.ESagaExecution;
import br.com.microservices.orchestrated.productvalidationservice.core.enums.ESagaStatus;
import br.com.microservices.orchestrated.productvalidationservice.core.model.Validation;
import br.com.microservices.orchestrated.productvalidationservice.core.repository.ProductRepository;
import br.com.microservices.orchestrated.productvalidationservice.core.repository.ValidationRepository;
import br.com.microservices.orchestrated.productvalidationservice.core.saga.NotifyEndingHandler;
import br.com.microservices.orchestrated.productvalidationservice.core.saga.PaymentStartHandler;
import br.com.microservices.orchestrated.productvalidationservice.core.saga.ProductValidationFailHandler;
import br.com.microservices.orchestrated.productvalidationservice.core.saga.SagaExecutionController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private static final String CURRENT_SOURCE = "PRODUCT_VALIDATION_SERVICE";

    private final SagaExecutionController sagaExecutionController;
    private final ProductRepository productRepository;
    private final ValidationRepository validationRepository;

    public void validateExistingProducts(Event event) {
        try {
            checkCurrentValidation(event.getPayload().getId(), event.getTransactionId());
            createValidation(event, true);
            validateProductsInformed(event);
            event.getPayload().getProducts().forEach(product -> {
                validateProductInformed(product);
                validateExistingProduct(product.getProduct().getCode());
            });
            handleSuccess(event);
            sagaExecutionController.handleSaga(new PaymentStartHandler(event));
        } catch (Exception ex) {
            log.error("Error trying to validate product: ", ex);
            handleFailCurrentNotExecuted(event, ex.getMessage());
        }
    }

    private void validateProductsInformed(Event event) {
        if (isEmpty(event.getPayload()) || isEmpty(event.getPayload().getProducts())) {
            throw new ValidationException("Product list is empty!");
        }
        if (isEmpty(event.getPayload().getId()) || isEmpty(event.getTransactionId())) {
            throw new ValidationException("OrderID and TransactionID must be informed!");
        }
    }

    private void validateProductInformed(OrderProducts product) {
        if (isEmpty(product.getProduct()) || isEmpty(product.getProduct().getCode())) {
            throw new ValidationException("Product must be informed!");
        }
    }

    private void validateExistingProduct(String code) {
        if (!productRepository.existsByCode(code)) {
            throw new ValidationException("Product does not exists in database!");
        }
    }

    private void checkCurrentValidation(String orderId, String transactionId) {
        if (validationRepository.existsByOrderIdAndTransactionId(orderId, transactionId)) {
            throw new ValidationException("There's another transactionId for this validation.");
        }
    }

    private void handleSuccess(Event event) {
        event.setStatus(ESagaStatus.SUCCESS);
        event.setSource(CURRENT_SOURCE);
        event.setCurrentExecuted(ESagaExecution.CURRENT_IS_SUCCESS);
        addHistory(event, "Products are validated successfully!");
    }

    private void handleFailCurrentNotExecuted(Event event, String message) {
        event.setStatus(ESagaStatus.FAIL);
        event.setSource(CURRENT_SOURCE);
        event.setCurrentExecuted(ESagaExecution.CURRENT_FAIL_PENDING_ROLLBACK);
        addHistory(event, "Fail to validate products: ".concat(message));
        sagaExecutionController.handleSaga(new ProductValidationFailHandler(event));
    }

    public void rollbackEvent(Event event) {
        changeValidationToFail(event.getPayload().getId(), event.getTransactionId());
        event.setStatus(ESagaStatus.FAIL);
        event.setSource(CURRENT_SOURCE);
        event.setCurrentExecuted(ESagaExecution.CURRENT_FAIL_EXECUTED_ROLLBACK);
        addHistory(event, "Rollback executed on product validation!");
        sagaExecutionController.handleSaga(new NotifyEndingHandler(event));
    }

    private void addHistory(Event event, String message) {
        var history = History
            .builder()
            .source(event.getSource())
            .status(event.getStatus())
            .message(message)
            .createdAt(LocalDateTime.now())
            .build();
        event.addToHistory(history);
    }

    private void createValidation(Event event, boolean success) {
        var validation = Validation
            .builder()
            .orderId(event.getPayload().getId())
            .transactionId(event.getTransactionId())
            .success(success)
            .build();
        validationRepository.save(validation);
    }

    private void changeValidationToFail(String orderId, String transactionId) {
        var validation = validationRepository
            .findByOrderIdAndTransactionId(orderId, transactionId)
            .orElseThrow(() -> new ValidationException("Validation does not exists for order and transaction."));
        validation.setSuccess(false);
        validationRepository.save(validation);
    }
}
