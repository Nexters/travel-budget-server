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
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final TripPlanRepository tripPlanRepository;
    private final TripMemberRepository tripMemberRepository;

     public List<TripPlan> getComingPlans(Long userId) {
        return tripMemberRepository.findByUser_IdAndTripPlanEndDateAfter(userId, LocalDate.now())
            .map(TripMember::getTripPlan)
            .filter(tripPlan -> tripPlan.getStartDate().compareTo(LocalDate.now()) > 0)
            .filter(tripPlan -> tripPlan.getIsDelete().equals(YnFlag.N))
            .sorted(Comparator.comparing(TripPlan::getStartDate))
            .collect(Collectors.toList());
    }

    public List<TripPlan> getDoingPlans(Long userId) {
        return tripMemberRepository.findByUser_IdAndTripPlanStartDateBeforeAndTripPlanEndDateGreaterThanEqual(userId, LocalDate.now(), LocalDate.now())
            .map(TripMember::getTripPlan)
            .filter(tripPlan -> tripPlan.getIsDelete().equals(YnFlag.N))
            .sorted(Comparator.comparing(TripPlan::getStartDate))
            .collect(Collectors.toList());
    }


    /*
    여행 예정: 현재일 < 시작일
    여행 중: 시작일 < 현재일 < 종료일
    여행 완료: 종료일 < 현재일

     -- 시작일이 현재일보다 크면, 여행 예정
     -- 시작일이 현재보다 작고, 종료일이 현재보다 크면
     -- 시작일이 현재보다 작으면, 여행중 또는 여행 완료
     */


    public Stream<TripPlan> getFinishPlans(Long userId) {
        return tripMemberRepository.findByUser_IdAndTripPlanEndDateBefore(userId, LocalDate.now())
            .map(TripMember::getTripPlan)
            .filter(tripPlan -> tripPlan.getIsDelete().equals(YnFlag.N))
            .sorted(Comparator.comparing(TripPlan::getStartDate).reversed());
    }


    public TripPlan createPlan(TripPlan tripPlan) {
        return tripPlanRepository.save(tripPlan);
    }

    public TripMember createTripMember(TripMember tripMember) {
        return tripMemberRepository.save(tripMember);
    }

    public void checkDateValidation(LocalDate startDate, LocalDate endDate) {
        if (startDate.compareTo(endDate) > 0) {
            throw new PlanException();
        }
    }

}
