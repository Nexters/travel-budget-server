package com.strictmanager.travelbudget.application.member;

import com.strictmanager.travelbudget.domain.YnFlag;
import com.strictmanager.travelbudget.domain.budget.Budget;
import com.strictmanager.travelbudget.domain.budget.BudgetService;
import com.strictmanager.travelbudget.domain.member.MemberException;
import com.strictmanager.travelbudget.domain.member.MemberService;
import com.strictmanager.travelbudget.domain.payment.PaymentCase;
import com.strictmanager.travelbudget.domain.payment.PaymentCaseService;
import com.strictmanager.travelbudget.domain.plan.PlanService;
import com.strictmanager.travelbudget.domain.plan.TripMember;
import com.strictmanager.travelbudget.domain.plan.TripMember.Authority;
import com.strictmanager.travelbudget.domain.plan.TripPlan;
import com.strictmanager.travelbudget.domain.user.User;
import com.strictmanager.travelbudget.utils.LocalDateUtils;
import com.strictmanager.travelbudget.web.PlanController.PlanDetailResponse.AmountItem;
import com.strictmanager.travelbudget.web.PlanController.PlanResponse;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlanManager {

    private final PlanService planService;
    private final MemberService memberService;
    private final BudgetService budgetService;
    private final PaymentCaseService paymentCaseService;

    private final long INIT_AMOUNT = 0L;

    public List<PlanResponse> getPlans(User user, boolean isComing) {

        Function<TripPlan, Budget> budgetFindFunction = (plan) ->
            Objects.requireNonNullElseGet(plan.getBudget(),
                () -> Objects.requireNonNullElseGet(
                    memberService.getMember(user, plan).getBudget(),
                    () -> Budget.builder()
                        .createUserId(user.getId())
                        .amount(-1L)
                        .paymentAmount(-1L)
                        .build()
                )
            );


        Stream<TripPlan> planStream;
        if (isComing) {
            List<TripPlan> doingPlans = planService.getDoingPlans(user);
            doingPlans.addAll(planService.getComingPlans(user));

            planStream = doingPlans.stream();
        } else {
            planStream = planService.getFinishPlans(user);
        }

        return planStream.map(plan -> PlanResponse.builder()
            .planId(plan.getId())
            .name(plan.getName())
            .startDate(plan.getStartDate())
            .endDate(plan.getEndDate())
            .purposeAmount(budgetFindFunction.apply(plan).getAmount())
            .usedAmount(budgetFindFunction.apply(plan).getPaymentAmount())
            .budgetId(budgetFindFunction.apply(plan).getId())
            .isPublic(plan.getIsPublic())
            .userCount(plan.getTripMembers().size())
            .isDoing(LocalDateUtils.checkIsDoing(plan.getStartDate(), plan.getEndDate()))
            .build())
            .collect(Collectors.toList());
    }

    @Transactional
    public TripPlan createPlan(PlanVO vo) {
        LocalDateUtils.checkDateValidation(vo.getStartDate(), vo.getEndDate());

        Budget budget = null;

        if (vo.getIsPublic().equals(YnFlag.Y)) {
            budgetService.createBudget(Budget.builder()
                .createUserId(vo.getCreateUser().getId())
                .paymentAmount(INIT_AMOUNT)
                .amount(vo.getSharedBudget())
                .build());
        }

        TripPlan tripPlan = planService.savePlan(TripPlan.builder()
            .name(vo.getName())
            .startDate(vo.getStartDate())
            .endDate(vo.getEndDate())
            .budget(budget)
            .userId(vo.getCreateUser().getId())
            .build());

        memberService.saveMember(TripMember.builder()
            .authority(Authority.OWNER)
            .tripPlan(tripPlan)
            .user(vo.getCreateUser())
            .build());

        return tripPlan;
    }

    public TripPlan getPlan(Long planId) {
        return planService.getPlan(planId);
    }

    public List<TripMember> getMembers(Long planId) {
        return planService.getPlan(planId).getTripMembers();
    }

    public Long getMemberId(User user, TripPlan plan) {
        return memberService.getMember(user, plan).getId();
    }

    public AmountItem getSharedPlanInfo(TripPlan plan) {
        return budgetService.getPublicBudget(plan).map(budget -> createPlanInfo(plan, budget))
            .orElse(null);
    }


    public AmountItem getPersonalPlanInfo(User user, TripPlan plan) {
        return budgetService.getPersonalBudget(user, plan)
            .map(budget -> createPlanInfo(plan, budget))
            .orElse(null);
    }

    private AmountItem createPlanInfo(TripPlan plan, Budget budget) {
        long readyUsePrice = paymentCaseService.getPaymentCaseByReady(budget)
            .stream()
            .mapToLong(PaymentCase::getPrice).sum();
        int planDayCnt = plan.getStartDate().until(plan.getEndDate()).getDays() + 1;

        return AmountItem.builder()
            .purposeAmount(budget.getAmount())
            .paymentAmount(budget.getPaymentAmount())
            .suggestAmount(
                (double) ((budget.getAmount() - readyUsePrice) / planDayCnt))
            .budgetId(budget.getId())
            .build();
    }

    public void deleteMember(MemberVO vo) {
        TripMember requestMember = memberService.getMember(
            vo.getUser(),
            planService.getPlan(vo.getPlanId())
        );

        if(requestMember == null) {
            throw new MemberException();
        }

        if (requestMember.getAuthority().equals(Authority.MEMBER) || Objects
            .equals(vo.getMemberId(), requestMember.getId())) {
            throw new MemberException();
        }

        TripMember deleteTargetMember = memberService.getMember(vo.getMemberId());
        memberService.deleteMember(deleteTargetMember);
    }
}
