package com.strictmanager.travelbudget.domain.budget;

import lombok.Getter;

@Getter
public class BudgetException extends RuntimeException {

    private final String message = "Budget error";
}