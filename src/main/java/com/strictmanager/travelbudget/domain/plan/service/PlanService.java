package com.strictmanager.travelbudget.domain.plan.service;

import com.strictmanager.travelbudget.domain.budget.Budget;
import com.strictmanager.travelbudget.domain.plan.TripPlan;
import java.util.List;

public interface PlanService {

    List<TripPlan> getPlans(Long userId);

    Budget createBudget(Budget budget);

    TripPlan createPlan(TripPlan tripPlan);

    TripPlan createNewPlan(Long userId);
}
