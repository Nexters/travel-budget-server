package com.strictmanager.travelbudget.web.handler;

import com.strictmanager.travelbudget.domain.user.UserException;
import com.strictmanager.travelbudget.web.ApiController;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice(annotations = ApiController.class)
public class ApiControllerExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(HttpServletRequest request, Exception exception) {
        log.debug("Exception occurred. {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<?> handleUserException(HttpServletRequest request, Exception exception) {
        log.debug("User exception occurred. {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
