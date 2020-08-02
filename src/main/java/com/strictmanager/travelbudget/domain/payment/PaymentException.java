package com.strictmanager.travelbudget.domain.payment;

import lombok.Getter;

@Getter
public class PaymentException extends RuntimeException {

    private final String message = "Payment error";
}