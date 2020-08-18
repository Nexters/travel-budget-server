package com.strictmanager.travelbudget.infra.persistence.predicate;

import com.querydsl.core.types.Predicate;
import com.strictmanager.travelbudget.domain.YnFlag;
import com.strictmanager.travelbudget.domain.plan.QTripMember;
import com.strictmanager.travelbudget.domain.user.User;
import java.time.LocalDate;

public class TripMemberPredicate {

    private final static QTripMember member = QTripMember.tripMember;

    public static Predicate searchComingPlans(User user) {
        LocalDate now = LocalDate.now();

        return member.user.eq(user)
            .and(member.tripPlan.startDate.after(now))
            .and(member.tripPlan.isDelete.eq(YnFlag.N));
    }


    public static Predicate searchDoingPlans(User user) {
        LocalDate now = LocalDate.now();

        return member.user.eq(user)
            .and(member.tripPlan.startDate.loe(now))
            .and(member.tripPlan.endDate.goe(now))
            .and(member.tripPlan.isDelete.eq(YnFlag.N));
    }

    public static Predicate searchFinishedPlans(User user) {
        LocalDate now = LocalDate.now();

        return member.user.eq(user)
            .and(member.tripPlan.endDate.before(now))
            .and(member.tripPlan.isDelete.eq(YnFlag.N));
    }

}
