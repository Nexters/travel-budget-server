package com.strictmanager.travelbudget.domain.payment;

import lombok.Getter;

@Getter
public class PaymentException extends RuntimeException {

    public PaymentException(PaymentMessage paymentMessage) {
        super(paymentMessage.getMsg());
    }

    @Getter
    public enum PaymentMessage {
        EDIT_ONLY_MINE("본인의 지출 내역만 수정할 수 있어요"),
        CAN_NOT_FIND_PAYMENT("해당 지출 내역을 찾을 수 없어요");

        private final String msg;

        PaymentMessage(String msg) {
            this.msg = msg;
        }
    }
}