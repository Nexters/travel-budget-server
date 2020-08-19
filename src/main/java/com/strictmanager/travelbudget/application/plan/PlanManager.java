package com.strictmanager.travelbudget.application.plan;

import com.strictmanager.travelbudget.application.plan.PlanProfileVO.AmountVO;
import com.strictmanager.travelbudget.domain.YnFlag;
import com.strictmanager.travelbudget.domain.budget.Budget;
import com.strictmanager.travelbudget.domain.budget.BudgetService;
import com.strictmanager.travelbudget.domain.member.MemberException;
import com.strictmanager.travelbudget.domain.member.MemberException.MemberMessage;
import com.strictmanager.travelbudget.domain.member.MemberService;
import com.strictmanager.travelbudget.domain.payment.PaymentCase;
import com.strictmanager.travelbudget.domain.payment.PaymentCaseService;
import com.strictmanager.travelbudget.domain.plan.PlanException;
import com.strictmanager.travelbudget.domain.plan.PlanException.PlanMessage;
import com.strictmanager.travelbudget.domain.plan.PlanService;
import com.strictmanager.travelbudget.domain.plan.TripMember;
import com.strictmanager.travelbudget.domain.plan.TripMember.Authority;
import com.strictmanager.travelbudget.domain.plan.TripPlan;
import com.strictmanager.travelbudget.domain.user.User;
import com.strictmanager.travelbudget.utils.InviteCodeUtils;
import com.strictmanager.travelbudget.utils.LocalDateUtils;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlanManager {

    private static final long INIT_AMOUNT = 0L;

    private final PlanService planService;
    private final MemberService memberService;
    private final BudgetService budgetService;
    private final PaymentCaseService paymentCaseService;

    public List<PlanVO> getPlans(User user, boolean isComing) {

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

        return planStream.map(plan -> PlanVO.builder()
            .planId(plan.getId())
            .name(plan.getName())
            .startDate(plan.getStartDate())
            .endDate(plan.getEndDate())
            .purposeAmount(budgetFindFunction.apply(plan).getAmount())
            .usedAmount(budgetFindFunction.apply(plan).getPaymentAmount())
            .budgetId(Optional.ofNullable(
                budgetFindFunction.apply(plan).getId())
                .orElse(-1L)
            )
            .isPublic(plan.getIsPublic())
            .userCount(plan.getTripMembers().size())
            .isDoing(LocalDateUtils.checkIsDoing(plan.getStartDate(), plan.getEndDate()))
            .inviteCode(
                InviteCodeUtils.generatePlanInviteCode(plan.getId(), plan.getCreateUserId()))
            .build())
            .collect(Collectors.toList());
    }

    @Transactional
    public TripPlan createPlan(PlanCreateVO vo) {
        LocalDateUtils.checkDateValidation(vo.getStartDate(), vo.getEndDate());

        Budget budget = null;

        if (vo.getIsPublic().equals(YnFlag.Y)) {
            budget = budgetService.saveBudget(Budget.builder()
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
            .isPublic(vo.getIsPublic())
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

    public TripMember getMember(User user, TripPlan plan) {
        return memberService.getMember(user, plan);
    }

    public AmountItemVO getSharedPlanInfo(TripPlan plan) {
        return budgetService.getPublicBudget(plan).map(budget -> createPlanInfo(plan, budget))
            .orElse(null);
    }

    public AmountItemVO getPersonalPlanInfo(User user, TripPlan plan) {
        return budgetService.getPersonalBudget(user, plan)
            .map(budget -> createPlanInfo(plan, budget))
            .orElse(null);
    }

    private AmountItemVO createPlanInfo(TripPlan plan, Budget budget) {
        long readyUsePrice = paymentCaseService.getPaymentCaseByReady(budget)
            .stream()
            .mapToLong(PaymentCase::getPrice).sum();
        int planDayCnt = plan.getStartDate().until(plan.getEndDate()).getDays() + 1;

        return AmountItemVO.builder()
            .purposeAmount(budget.getAmount())
            .paymentAmount(budget.getPaymentAmount())
            .suggestAmount(
                (double) ((budget.getAmount() - readyUsePrice) / planDayCnt))
            .budgetId(budget.getId())
            .build();
    }

    @Transactional
    public void deleteMember(MemberVO vo) {
        TripMember requestMember = memberService.getMember(
            vo.getUser(),
            planService.getPlan(vo.getPlanId())
        );

        TripMember deleteTargetMember = memberService.getMember(vo.getMemberId());

        if (deleteTargetMember.getAuthority().equals(Authority.OWNER)) {
            throw new MemberException(MemberMessage.CAN_NOT_DELETE_OWNER);
        }

        if (!(requestMember.getAuthority().equals(Authority.MEMBER) &&
            vo.getMemberId().equals(requestMember.getId()))
        ) {
            throw new MemberException(MemberMessage.NOT_HAVE_PERMISSION);
        }

        memberService.deleteMember(deleteTargetMember);
    }

    @Transactional
    public Long createPlanMember(User user, String inviteCode) {

        Long planId = InviteCodeUtils
            .getPlanIdFromInviteCode(inviteCode)
            .orElseThrow(() -> new MemberException(MemberMessage.INVITE_CODE_INVALID));

        TripPlan plan = planService.getPlan(planId);

        if (plan.getIsPublic().equals(YnFlag.N)) {
            throw new MemberException(MemberMessage.CAN_NOT_JOIN_PERSONAL_PLAN);
        }

        plan.getTripMembers().stream()
            .map(TripMember::getUser)
            .map(User::getId)
            .forEach(userId -> {
                if (userId.equals(user.getId())) {
                    throw new MemberException(MemberMessage.IS_JOINED_MEMBER);
                }
            });

        final TripMember member = memberService.saveMember(TripMember.builder()
            .authority(Authority.MEMBER)
            .tripPlan(plan)
            .user(user)
            .build());

        return member.getId();
    }

    @Transactional
    public void deletePlan(User user, Long planId) {
        TripPlan plan = planService.getPlan(planId);

        if (!plan.getCreateUserId().equals(user.getId())) {
            throw new PlanException(PlanMessage.NO_AUTHORITY);
        }

        if (plan.getIsPublic().equals(YnFlag.N)) {
            if (plan.getTripMembers().size() > 1) {
                throw new PlanException(PlanMessage.DELETE_MUST_BE_ALONE);
            }
        }
        planService.savePlan(plan.deletePlan());
    }

    public PlanProfileVO getPlanProfile(User user, Long planId) {
        TripPlan plan = planService.getPlan(planId);

        TripMember member = memberService.getMember(user, plan);

        Function<Budget, AmountVO> convertBudgetToAmountObj = (budget ->
            AmountVO.builder()
                .budgetId(budget.getId())
                .amount(budget.getAmount())
                .build()
        );

        return PlanProfileVO.builder()
            .name(plan.getName())
            .startDate(plan.getStartDate())
            .endDate(plan.getEndDate())
            .authority(member.getAuthority())
            .personal(
                Optional.ofNullable(member.getBudget())
                    .map(convertBudgetToAmountObj)
                    .orElse(null)
            )
            .shared(
                Optional.ofNullable(plan.getBudget())
                    .map(convertBudgetToAmountObj)
                    .orElse(null)
            )
            .build();
    }
}
