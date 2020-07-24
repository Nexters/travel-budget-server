package com.strictmanager.travelbudget.infra.persistence.jpa;

import com.strictmanager.travelbudget.domain.plan.TripMember;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripMemberRepository extends JpaRepository<TripMember, Long> {
    List<TripMember> findByUser(Long userId);
}
