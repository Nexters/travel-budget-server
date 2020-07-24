package com.strictmanager.travelbudget.infra.persistence.jpa;

import com.strictmanager.travelbudget.domain.plan.TripPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripPlanRepository extends JpaRepository<TripPlan, Long> {

}
