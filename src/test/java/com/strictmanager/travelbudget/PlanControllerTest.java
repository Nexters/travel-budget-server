package com.strictmanager.travelbudget;

import com.strictmanager.travelbudget.web.AuthController;
import com.strictmanager.travelbudget.web.AuthController.JwtResponse;
import com.strictmanager.travelbudget.web.AuthController.KakaoUserRequest;
import com.strictmanager.travelbudget.web.AuthController.SignUpResponse;
import com.strictmanager.travelbudget.web.AuthController.TokenCreateRequest;
import com.strictmanager.travelbudget.web.PlanController;
import com.strictmanager.travelbudget.web.PlanController.CreatePlanRequest;
import java.util.Objects;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@ContextConfiguration(classes = TravelBudgetApplicationTests.class)
public class PlanControllerTest {

    @InjectMocks
    PlanController planController;

    @InjectMocks
    AuthController authController;

    @Autowired
    private MockMvc mockMvc;

//    @Mock
//    CreatePlanRequest createPlanRequest;
//
//    @Mock
//    KakaoUserRequest kakaoUserRequest;

    @Mock
    JwtResponse jwtResponse;

    @BeforeAll
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        System.out.println("init Mocks ---");

        //when
        KakaoUserRequest kakaoUserRequest = new KakaoUserRequest("kiyeon_kim1", "기연", null, null);
        ResponseEntity<SignUpResponse> createKakaoId = authController.kakaoSignin(kakaoUserRequest);

        String kakaoId = Objects.requireNonNull(createKakaoId.getBody().getKakaoId());
        TokenCreateRequest tokenCreateRequest = new TokenCreateRequest(kakaoId);

        ResponseEntity<JwtResponse> getToken = authController.createToken(tokenCreateRequest);

        jwtResponse = Objects.requireNonNull(getToken.getBody());
    }

    @Test
    public void addTestPlans() {
        System.out.println("addTestPlans start --- ");
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

    }
}
