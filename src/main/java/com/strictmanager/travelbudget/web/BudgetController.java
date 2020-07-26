package com.strictmanager.travelbudget.web;

import javax.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@ApiController
@RequiredArgsConstructor
public class BudgetController {
    @PostMapping("/budgets")
    public ResponseEntity<BudgetResponse> createBudget(@RequestBody @Valid BudgetCreateRequest request) {
        return null;
    }

    @PutMapping("/budgets")
    public ResponseEntity<BudgetResponse> updateBudget(@RequestBody @Valid BudgetUpdateRequest request) {
        return null;
    }

    @Getter
    private static class BudgetCreateRequest {

    }

    @Getter
    private static class BudgetUpdateRequest {

    }

    @Getter
    private static class BudgetResponse {
        
    }
}
