package com.strictmanager.travelbudget.domain.member;

import com.strictmanager.travelbudget.domain.plan.PlanException;
import com.strictmanager.travelbudget.domain.plan.TripMember;
import com.strictmanager.travelbudget.domain.plan.TripPlan;
import com.strictmanager.travelbudget.domain.user.User;
import com.strictmanager.travelbudget.infra.persistence.jpa.TripMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final TripMemberRepository tripMemberRepository;


    public TripMember saveMember(TripMember member) {
        return tripMemberRepository.save(member);
    }

    public TripMember getMember(User user, TripPlan plan) {
        return tripMemberRepository.findByUserAndTripPlan(user, plan).orElseThrow(PlanException::new);
    }

    public TripMember getMember(Long id) {
        return tripMemberRepository.findById(id).orElseThrow(MemberException::new);

    }



}
