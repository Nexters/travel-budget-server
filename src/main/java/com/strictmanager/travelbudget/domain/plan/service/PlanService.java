package com.strictmanager.travelbudget.domain.plan.service;

import com.strictmanager.travelbudget.domain.plan.PlanException;
import com.strictmanager.travelbudget.domain.plan.TripMember;
import com.strictmanager.travelbudget.domain.plan.TripPlan;
import com.strictmanager.travelbudget.domain.plan.TripPlan.YnFlag;
import com.strictmanager.travelbudget.infra.persistence.jpa.TripMemberRepository;
import com.strictmanager.travelbudget.infra.persistence.jpa.TripPlanRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final TripPlanRepository tripPlanRepository;
    private final TripMemberRepository tripMemberRepository;

    public List<TripPlan> getPlans(Long userId) {
        return tripMemberRepository.findByUser_Id(userId)
            .stream()
            .map(TripMember::getTripPlan)
            .filter(tripPlan -> tripPlan.getIsDelete().equals(YnFlag.N))
            .sorted(Comparator.comparing(TripPlan::getStartDate).reversed())
            .collect(Collectors.toList());
    }


    public TripPlan createPlan(TripPlan tripPlan) {
        return tripPlanRepository.save(tripPlan);
    }

    public TripMember createTripMember(TripMember tripMember) {
        return tripMemberRepository.save(tripMember);
    }

    public void checkDateValidation(LocalDate startDate, LocalDate endDate) {
        if(startDate.compareTo(endDate) > 0) {
            throw new PlanException();
        }
    }

}
