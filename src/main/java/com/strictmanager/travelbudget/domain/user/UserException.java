package com.strictmanager.travelbudget.domain.user;

import lombok.Getter;

@Getter
public class UserException extends RuntimeException {

    private final String message = "User error";
}
