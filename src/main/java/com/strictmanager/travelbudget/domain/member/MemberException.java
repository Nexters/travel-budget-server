package com.strictmanager.travelbudget.domain.member;

import lombok.Getter;

@Getter
public class MemberException extends RuntimeException {

    private final String message = "Member error";


    public MemberException(String message) {
        super(message);
    }

}
