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
        INVITE_CODE_INVALID("초대코드가 유효하지 않은걸요 :)"),
        IS_JOINED_MEMBER("이미 방에 존재하는 사용자에요 :)"),
        CAN_NOT_JOIN_PERSONAL_PLAN("개인 여행은 사용자를 초대할 수 없어요 :)"),
        CAN_NOT_FIND_MEMBER("사용자를 찾을 수 없어요"),
        NOT_HAVE_PERMISSION("방장만 진행할 수 있어요");

        private final String msg;

        MemberMessage(String msg) {
            this.msg = msg;
        }
    }
}
