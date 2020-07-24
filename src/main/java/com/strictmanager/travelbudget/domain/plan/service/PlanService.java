package com.strictmanager.travelbudget.domain.plan.service;

import com.strictmanager.travelbudget.domain.plan.TripPlan;
import java.util.List;

public interface PlanService {

    List<TripPlan> getPlans(Long userId);
}
