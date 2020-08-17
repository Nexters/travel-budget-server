package com.strictmanager.travelbudget.domain.plan;

import lombok.Getter;

@Getter
public class PlanException extends RuntimeException {

    public PlanException(PlanMessage planMessage) {
        super(planMessage.getMsg());
    }

    @Getter
    public enum PlanMessage {
        ALREADY_DELETE_PLAN("이미 삭제된 여행이에요"),
        DELETE_PLAN("삭제된 여행이에요"),
        INVALID_DATE("시작일을 종료일 이전으로 설정해주세요"),
        DELETE_MUST_BE_ALONE("모든 친구를 내보내야 삭제할 수 있어요"),
        NO_AUTHORITY("자신이 만든 여행만 삭제가 가능해요"),
        CAN_NOT_FIND_PLAN("해당 여행을 찾을 수 없어요");

        private final String msg;

        PlanMessage(String msg) {
            this.msg = msg;
        }
    }
}
