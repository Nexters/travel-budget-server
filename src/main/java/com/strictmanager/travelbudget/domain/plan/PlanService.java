package com.strictmanager.travelbudget.domain.plan;

import com.strictmanager.travelbudget.domain.plan.TripPlan.YnFlag;
import com.strictmanager.travelbudget.domain.user.User;
import com.strictmanager.travelbudget.infra.persistence.jpa.TripMemberRepository;
import com.strictmanager.travelbudget.infra.persistence.jpa.TripPlanRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final TripPlanRepository tripPlanRepository;
    private final TripMemberRepository tripMemberRepository;

     public List<TripPlan> getComingPlans(User user) {
        return tripMemberRepository.findByUserAndTripPlanEndDateAfter(user, LocalDate.now())
            .map(TripMember::getTripPlan)
            .filter(tripPlan -> tripPlan.getStartDate().compareTo(LocalDate.now()) > 0)
            .filter(tripPlan -> tripPlan.getIsDelete().equals(YnFlag.N))
            .sorted(Comparator.comparing(TripPlan::getStartDate))
            .collect(Collectors.toList());
    }

    public List<TripPlan> getDoingPlans(User user) {
        return tripMemberRepository.findByUserAndTripPlanStartDateBeforeAndTripPlanEndDateGreaterThanEqual(user, LocalDate.now(), LocalDate.now())
            .map(TripMember::getTripPlan)
            .filter(tripPlan -> tripPlan.getIsDelete().equals(YnFlag.N))
            .sorted(Comparator.comparing(TripPlan::getStartDate))
            .collect(Collectors.toList());
    }

    public Stream<TripPlan> getFinishPlans(User user) {
        return tripMemberRepository.findByUserAndTripPlanEndDateBefore(user, LocalDate.now())
            .map(TripMember::getTripPlan)
            .filter(tripPlan -> tripPlan.getIsDelete().equals(YnFlag.N))
            .sorted(Comparator.comparing(TripPlan::getStartDate).reversed());
    }

    public TripPlan getPlan(Long planId) {
         return tripPlanRepository.findById(planId).orElseThrow();
    }

    public TripPlan savePlan(TripPlan tripPlan) {
        return tripPlanRepository.save(tripPlan);
    }



}
