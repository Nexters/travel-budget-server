package com.strictmanager.travelbudget.domain.member;

import com.strictmanager.travelbudget.domain.member.MemberException.MemberMessage;
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
        return tripMemberRepository.findByUserAndTripPlan(user, plan)
            .orElseThrow(() -> new MemberException(MemberMessage.CAN_NOT_FIND_MEMBER));
    }

    public TripMember getMember(Long id) {
        return tripMemberRepository.findById(id).orElseThrow(() -> new MemberException(
            MemberMessage.CAN_NOT_FIND_MEMBER));
    }

    public void deleteMember(TripMember member) {
        tripMemberRepository.delete(member);
    }


}
