package com.shop.payments.messaging.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentEvent(
        UUID orderId,
        UUID userId,
        BigDecimal amount,
        EventType eventType
) {
    public enum EventType {
        PAYMENT_REQUESTED,
        PAYMENT_SUCCESS,
        PAYMENT_FAILED
    }
}