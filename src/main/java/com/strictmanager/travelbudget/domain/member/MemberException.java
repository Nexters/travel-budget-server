package com.strictmanager.travelbudget.domain.member;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MemberException extends RuntimeException {

    public MemberException(MemberMessage messageCase) {
        super(messageCase.getMsg());
    }

    @Getter
    public enum MemberMessage {
        INVITE_CODE_INVALID("유효하지 않은 초대 코드예요"),
        IS_JOINED_MEMBER("이미 여행을 함께 하고 있어요"),
        CAN_NOT_JOIN_PERSONAL_PLAN("참여할 수 없는 방이에요"),
        CAN_NOT_FIND_MEMBER("사용자를 찾을 수 없어요"),
        CAN_NOT_DELETE_OWNER("관리자는 삭제할 수 없어요"),
        NOT_HAVE_PERMISSION("관리자만 진행할 수 있어요");

        private final String msg;

        MemberMessage(String msg) {
            this.msg = msg;
        }
    }
}
