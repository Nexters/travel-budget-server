package com.strictmanager.travelbudget.infra.persistence.jpa;

import com.strictmanager.travelbudget.domain.plan.TripMember;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripMemberRepository extends JpaRepository<TripMember, Long> {
    List<TripMember> findByUser_Id(Long userId);

    //여행 예정 목록,
    Stream<TripMember> findByUser_IdAndTripPlanEndDateAfter(Long userId, LocalDate nowDate);

    Stream<TripMember> findByUser_IdAndTripPlanStartDateBeforeAndTripPlanEndDateGreaterThanEqual(Long userId, LocalDate nowDate, LocalDate nowDate2);

    //다녀온 여행들
    Stream<TripMember> findByUser_IdAndTripPlanEndDateBefore(Long userId, LocalDate nowDate);
}
