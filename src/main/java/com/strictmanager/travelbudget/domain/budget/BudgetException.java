package com.strictmanager.travelbudget.domain.budget;

import lombok.Getter;

@Getter
public class BudgetException extends RuntimeException {

    public BudgetException(BudgetMessage budgetMessage) {
        super(budgetMessage.getMsg());
    }

    @Getter
    public enum BudgetMessage {
        EDIT_ONLY_MINE("본인의 예산만 수정할 수 있어요"),
        CAN_NOT_FIND_BUDGET("예산 정보를 찾을 수 없어요");

        private final String msg;

        BudgetMessage(String msg) {
            this.msg = msg;
        }
    }
}