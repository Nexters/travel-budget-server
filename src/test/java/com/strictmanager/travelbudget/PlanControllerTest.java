package com.strictmanager.travelbudget;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.strictmanager.travelbudget.infra.auth.JwtTokenUtil;
import com.strictmanager.travelbudget.web.AuthController;
import com.strictmanager.travelbudget.web.PlanController;
import com.strictmanager.travelbudget.web.PlanController.CreatePlanRequest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.LocalDate;
import java.util.Date;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@ContextConfiguration(classes = TravelBudgetApplicationTests.class)
@ActiveProfiles("local")
@ExtendWith(SpringExtension.class)
public class PlanControllerTest {

    @InjectMocks
    PlanController planController;

    @InjectMocks
    AuthController authController;

    @InjectMocks
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${auth.jwt.access.secret}")
    private String secret;

    @Value("${auth.jwt.access.expire-hours}")
    private Long expireHours;

    @Autowired
    private WebApplicationContext webApplicationContext;


    @BeforeAll
    public void initMocks() {
    }

    @Test
    public void addPlansTest() throws Exception {
        CreatePlanRequest request = new CreatePlanRequest("테스트 공 여행1",
            LocalDate.of(2020, 07, 28),
            LocalDate.of(2020, 07, 31),
            1000000L);

//        String token = createToken("kiyeon_kim1");
//        System.out.println("token = " + token);
        String testToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNTk1OTQ0MzU0LCJleHAiOjE1OTYwMzA3NTR9.n4TUppuWzrM9PRu-MOqpoBKID_Em41LeM1Usfb3yOOk";
        mockMvc.perform(MockMvcRequestBuilders.post("/plans")
            .header("Authorization", testToken)
//            .header("Accept", "application/json")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());
    }

    public String createToken(String userName) {
        final long tokenValidityMillis = expireHours * 60 * 60 * 1000;
        return Jwts.builder()
            .setSubject(userName)
            .setExpiration(new Date(System.currentTimeMillis() + tokenValidityMillis))
            .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
            .compact();
    }
}
