package com.strictmanager.travelbudget.infra.persistence.jpa;

import com.strictmanager.travelbudget.domain.plan.TripMember;
import com.strictmanager.travelbudget.domain.plan.TripPlan;
import com.strictmanager.travelbudget.domain.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TripMemberRepository extends JpaRepository<TripMember, Long>,
    QuerydslPredicateExecutor<TripMember> {

    Optional<TripMember> findByUserAndTripPlan(User user, TripPlan tripPlan);
}
