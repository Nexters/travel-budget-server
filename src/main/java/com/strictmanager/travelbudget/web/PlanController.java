package com.strictmanager.travelbudget.web;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.VisibleForTesting;
import com.strictmanager.travelbudget.application.member.MemberBudgetManager;
import com.strictmanager.travelbudget.application.member.PlanManager;
import com.strictmanager.travelbudget.application.member.PlanVO;
import com.strictmanager.travelbudget.domain.plan.TripMember.Authority;
import com.strictmanager.travelbudget.domain.plan.TripPlan;
import com.strictmanager.travelbudget.domain.plan.TripPlan.YnFlag;
import com.strictmanager.travelbudget.domain.user.User;
import com.strictmanager.travelbudget.utils.LocalDateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@ApiController
@RequiredArgsConstructor
public class PlanController {


    private final PlanManager planManager;
    private final MemberBudgetManager memberBudgetManager;

    @GetMapping("/plans")
    @ApiOperation(value = "여행 목록 조회")
    @Transactional(readOnly = true)
    public ResponseEntity<List<PlanResponse>> getPlans(
        @AuthenticationPrincipal User user,
        @RequestParam(name = "isComing") boolean isComing) {
        List<PlanResponse> responses = planManager.getPlans(user, isComing);

        return ResponseEntity.ok(responses);
    }

    @PostMapping("/plans")
    @ApiOperation(value = "여행 등록")
    public ResponseEntity<CreatePlanResponse> createPlan(@AuthenticationPrincipal @Valid User user,
        HttpServletRequest httpServletRequest,
        @RequestBody CreatePlanRequest param) {

        TripPlan plan = planManager.createPlan(PlanVO.builder()
            .user(user)
            .name(param.getName())
            .startDate(param.getStartDate())
            .endDate(param.getEndDate())
            .sharedBudget(param.getSharedBudget())
            .build());

        return ResponseEntity
            .created(URI.create(httpServletRequest.getRequestURI()))
            .body(CreatePlanResponse.builder()
                .planId(plan.getId())
                .build());
    }


    @GetMapping("/plans/{id}")
    @ApiOperation(value = "여행 상세 조회")
    @Transactional(readOnly = true)
    public ResponseEntity<PlanDetailResponse> getPlanDetail(@AuthenticationPrincipal User user,
        @PathVariable(value = "id") Long planId) {

        TripPlan plan = planManager.getPlan(planId);

        return ResponseEntity.ok(PlanDetailResponse.builder()
            .memberId(planManager.getMemberId(user, plan))
            .shared(planManager.getSharedPlanInfo(plan))
            .personal(planManager.getPersonalPlanInfo(user, plan))
            .dates(LocalDateUtils.getLocalDates(plan.getStartDate(), plan.getEndDate()))
            .build());
    }

    @GetMapping("/plans/{id}/members")
    @ApiOperation(value = "여행 친구목록 조회")
    @Transactional(readOnly = true)
    public ResponseEntity<List<MemberResponse>> getPlanMember(
        @PathVariable(value = "id") Long planId) {

        List<MemberResponse> planMembers = planManager.getMembers(planId).stream()
            .map(member -> MemberResponse.builder()
                .authority(member.getAuthority())
                .memberId(member.getId())
                .nickname(member.getUser().getNickname())
                .profileImage(member.getUser().getProfileImage()).build()
            ).collect(Collectors.toList());

        return ResponseEntity.ok(planMembers);
    }




    @Getter
    @ApiModel
    private static class MemberResponse {

        private final Long memberId;
        @ApiModelProperty(value = "사용자 닉네임")
        private final String nickname;
        @ApiModelProperty(value = "방 권한")
        private final Authority authority;
        @ApiModelProperty(value = "이미지 url")
        private final String profileImage;


        @Builder
        private MemberResponse(Long memberId, String nickname,
            Authority authority, String profileImage) {
            this.memberId = memberId;
            this.nickname = nickname;
            this.authority = authority;
            this.profileImage = profileImage;
        }
    }


    @Getter
    @ApiModel
    public static class PlanDetailResponse {

        @ApiModelProperty(value = "여행 멤버 id")
        private final Long memberId;

        private final AmountItem shared;
        private final AmountItem personal;

        @ApiModelProperty(value = "여행 일자 목록 yyyy-MM-dd")
        private final List<String> dates;

        @Builder
        public PlanDetailResponse(
            Long memberId, AmountItem shared,
            AmountItem personal, List<LocalDate> dates) {
            this.memberId = memberId;
            this.shared = shared;
            this.personal = personal;
            this.dates = dates.stream().map(LocalDate::toString).collect(Collectors.toList());
        }


        @Getter
        @ApiModel
        public static class AmountItem {

            @ApiModelProperty(value = "여행 전체 목표 금액")
            private final Long purposeAmount;
            @ApiModelProperty(value = "하루 사용 제안 금액")
            private final Double suggestAmount;
            @ApiModelProperty(value = "사용된 예산")
            private final Long paymentAmount;

            @ApiModelProperty(value = "예산 id")
            private final Long budgetId;


            @Builder
            public AmountItem(Long purposeAmount, Double suggestAmount, Long paymentAmount,
                Long budgetId) {
                this.purposeAmount = purposeAmount;
                this.suggestAmount = suggestAmount;
                this.paymentAmount = paymentAmount;
                this.budgetId = budgetId;
            }
        }
    }


    @Getter
    @ApiModel
    public static class PlanResponse {

        private final Long planId;
        @ApiModelProperty(value = "budgetKey", reference = "-1: 예산 미지정 (함께하는 여행: 공용 budget_id, 혼자하는여행: 개인 budget_id")
        private final Long budgetId;
        @ApiModelProperty(value = "여행명")
        private final String name;
        @ApiModelProperty(value = "여행기간")
        private final String planPeriod;
        @ApiModelProperty(value = "D-DAY")
        private final String dayCount;
        @ApiModelProperty(value = "목표 예산 (개인 여행 및 목표금액 미설정시 -1)")
        private final Long amount; // 금액 미입력시 -1
        @ApiModelProperty(value = "여행 참가인원 수")
        private final int userCount;
        @ApiModelProperty(value = "여행 공용 여부 Y, N")
        private final YnFlag isPublic;

        @Builder
        public PlanResponse(
            Long planId,
            Long budgetId,
            String name,
            LocalDate startDate,
            LocalDate endDate,
            Long amount,
            int userCount,
            YnFlag isPublic
        ) {
            this.planId = planId;
            this.budgetId = budgetId;
            this.name = name;
            this.planPeriod = convertPlanPeriod(startDate, endDate);
            this.dayCount = calculateDay(startDate);
            this.amount = amount;
            this.userCount = userCount;
            this.isPublic = isPublic;
        }

        private String calculateDay(LocalDate startDate) {
            StringBuilder dDayBuilder = new StringBuilder("D");

            ChronoUnit chronoUnit = ChronoUnit.DAYS;

            long day = chronoUnit.between(startDate, LocalDate.now());

            if (day > 0) {
                dDayBuilder.append("+").append(day);
            } else if (day == 0) {
                dDayBuilder.append("-DAY!");
            } else {
                dDayBuilder.append(day);
            }
            return dDayBuilder.toString();
        }


        private String convertPlanPeriod(LocalDate startDate, LocalDate endDate) {
            StringBuilder planPeriodBuilder = new StringBuilder();
            Function<LocalDate, String> dtConverter = date ->
                Objects.requireNonNull(date)
                    .format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                    .substring(5);

            planPeriodBuilder.append(startDate.getYear())
                .append(".")
                .append(dtConverter.apply(startDate))
                .append(" - ");

            if (startDate.getYear() != endDate.getYear()) {
                planPeriodBuilder.append(endDate.getYear()).append(".");
            }

            return planPeriodBuilder.append(dtConverter.apply(endDate)).toString();
        }
    }


    @Getter
    @ToString
    @VisibleForTesting
    private static class CreatePlanRequest {

        private final String name;
        private final LocalDate startDate;
        private final LocalDate endDate;

        private final Long sharedBudget;

        @JsonCreator
        public CreatePlanRequest(
            @JsonProperty(value = "name", defaultValue = "여행을 떠나요") String name,
            @JsonProperty(value = "start_date", required = true) LocalDate startDate,
            @JsonProperty(value = "end_date", required = true) LocalDate endDate,
            @JsonProperty(value = "shared_budget", required = false) Long sharedBudget
        ) {
            this.name = name;
            this.startDate = startDate;
            this.endDate = endDate;
            this.sharedBudget = sharedBudget;
        }
    }

    @Getter
    @ToString
    private static class CreatePlanResponse {

        private final Long planId;

        @Builder
        private CreatePlanResponse(Long planId) {
            this.planId = planId;
        }
    }

}
