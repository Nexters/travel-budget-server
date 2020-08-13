package com.strictmanager.travelbudget.web.handler;

import com.strictmanager.travelbudget.domain.budget.BudgetException;
import com.strictmanager.travelbudget.domain.member.MemberException;
import com.strictmanager.travelbudget.domain.payment.PaymentException;
import com.strictmanager.travelbudget.domain.plan.PlanException;
import com.strictmanager.travelbudget.web.ApiErrorResponse;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class DomainExceptionHandler {

    private final HttpServletRequest request;

    @ExceptionHandler({MemberException.class, PaymentException.class, BudgetException.class,
        PlanException.class})
    public ResponseEntity<ApiErrorResponse> memberExceptionHandler(RuntimeException ex) {

        String exClassName = ex.getClass().getName();

        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST)
            .message(ex.getMessage())
            .type(exClassName.substring(exClassName.lastIndexOf('.') + 1))
            .path(request.getRequestURI())
            .build();

        log.error(errorResponse.toString());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


}
