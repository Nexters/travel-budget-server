package com.strictmanager.travelbudget.domain.plan.service;

import com.strictmanager.travelbudget.domain.plan.TripMember;
import com.strictmanager.travelbudget.domain.plan.TripPlan;
import com.strictmanager.travelbudget.domain.plan.TripPlan.DeleteYn;
import com.strictmanager.travelbudget.infra.persistence.jpa.TripMemberRepository;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final TripMemberRepository tripMemberRepository;

    @Override
    public List<TripPlan> getPlans(Long userId) {
        return tripMemberRepository.findByUser_Id(userId)
            .stream()
            .map(TripMember::getTripPlan)
            .filter(tripPlan -> tripPlan.getIsDelete().equals(DeleteYn.N))
            .sorted(Comparator.comparing(TripPlan::getStartDate).reversed())
            .collect(Collectors.toList());
    }
}
