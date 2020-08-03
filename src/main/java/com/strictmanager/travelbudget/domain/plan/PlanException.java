package com.strictmanager.travelbudget.domain.plan;

import lombok.Getter;

@Getter
public class PlanException extends RuntimeException {
    private final String message = "Plan error";
}
