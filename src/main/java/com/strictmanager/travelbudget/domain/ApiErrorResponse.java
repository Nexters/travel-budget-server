package com.strictmanager.travelbudget.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public class ApiErrorResponse {


    private final LocalDateTime timeStamp;
    private final int code;
    private final String message;
    private final String type;
    private final String path;

    @Builder
    public ApiErrorResponse(HttpStatus status, String message, String type, String path) {
        this.type = type;
        this.timeStamp = LocalDateTime.now();
        this.code = status.value();
        this.message = message;
        this.path = path;
    }
}
