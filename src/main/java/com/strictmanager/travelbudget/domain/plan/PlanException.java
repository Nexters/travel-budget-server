package com.strictmanager.travelbudget.domain.plan;

import lombok.Getter;

@Getter
public class PlanException extends RuntimeException {

    public PlanException(PlanMessage planMessage) {
        super(planMessage.getMsg());
    }

    @Getter
    public enum PlanMessage {
        INVALID_DATE("시작일을 종료일 이전으로 설정해주세요"),
        CAN_NOT_FIND_PLAN("해당 여행을 찾을 수 없어요");

        private final String msg;

        PlanMessage(String msg) {
            this.msg = msg;
        }
    }
}
