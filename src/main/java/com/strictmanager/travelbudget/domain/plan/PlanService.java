package com.strictmanager.travelbudget.domain.plan;

import com.strictmanager.travelbudget.domain.YnFlag;
import com.strictmanager.travelbudget.domain.plan.PlanException.PlanMessage;
import com.strictmanager.travelbudget.domain.user.User;
import com.strictmanager.travelbudget.infra.persistence.jpa.TripMemberRepository;
import com.strictmanager.travelbudget.infra.persistence.jpa.TripPlanRepository;
import com.strictmanager.travelbudget.infra.persistence.predicate.TripMemberPredicate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final TripPlanRepository tripPlanRepository;
    private final TripMemberRepository tripMemberRepository;

    public List<TripPlan> getComingPlans(User user) {
        return StreamSupport.stream(
            tripMemberRepository.findAll(TripMemberPredicate
                .searchComingPlans(user)).spliterator(), false)
            .map(TripMember::getTripPlan)
            .sorted(Comparator.comparing(TripPlan::getStartDate))
            .collect(Collectors.toList());
    }

    public List<TripPlan> getDoingPlans(User user) {
        return StreamSupport.stream(
            tripMemberRepository.findAll(TripMemberPredicate
                .searchDoingPlans(user)).spliterator(), false)
            .map(TripMember::getTripPlan)
            .sorted(Comparator.comparing(TripPlan::getStartDate))
            .collect(Collectors.toList());
    }

    public Stream<TripPlan> getFinishPlans(User user) {
        return StreamSupport.stream(
            tripMemberRepository.findAll(TripMemberPredicate
                .searchFinishedPlans(user)).spliterator(), false)
            .map(TripMember::getTripPlan)
            .sorted(Comparator.comparing(TripPlan::getStartDate).reversed());
    }

    public TripPlan getPlan(Long planId) {
        TripPlan plan = tripPlanRepository.findById(planId).orElseThrow();

        if (plan.getIsDelete().equals(YnFlag.Y)) {
            throw new PlanException(PlanMessage.DELETE_PLAN);
        }

        return plan;
    }

    public TripPlan savePlan(TripPlan tripPlan) {
        return tripPlanRepository.save(tripPlan);
    }


}
