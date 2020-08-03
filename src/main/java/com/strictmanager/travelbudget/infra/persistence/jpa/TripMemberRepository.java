package com.strictmanager.travelbudget.infra.persistence.jpa;

import com.strictmanager.travelbudget.domain.plan.TripMember;
import com.strictmanager.travelbudget.domain.plan.TripPlan;
import com.strictmanager.travelbudget.domain.user.User;
import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripMemberRepository extends JpaRepository<TripMember, Long> {

    //여행 예정 목록
    Stream<TripMember> findByUserAndTripPlanEndDateAfter(User user, LocalDate nowDate);

    //여행중인 목록
    Stream<TripMember> findByUserAndTripPlanStartDateBeforeAndTripPlanEndDateGreaterThanEqual(
        User user, LocalDate nowDate, LocalDate nowDate2);

    //다녀온 여행 목록
    Stream<TripMember> findByUserAndTripPlanEndDateBefore(User user, LocalDate nowDate);


    Optional<TripMember> findByUserAndTripPlan(User user, TripPlan tripPlan);
}
