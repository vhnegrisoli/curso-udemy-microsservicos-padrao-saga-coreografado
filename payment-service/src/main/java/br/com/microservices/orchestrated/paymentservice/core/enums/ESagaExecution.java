package br.com.microservices.orchestrated.paymentservice.core.enums;

public enum ESagaExecution {

    CURRENT_FAIL_PENDING_ROLLBACK,
    CURRENT_FAIL_EXECUTED_ROLLBACK,
    CURRENT_IS_SUCCESS
}
