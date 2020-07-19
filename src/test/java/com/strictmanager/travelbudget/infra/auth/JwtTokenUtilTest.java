package com.strictmanager.travelbudget.infra.auth;

import com.strictmanager.travelbudget.domain.user.User;
import java.util.Date;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

public class JwtTokenUtilTest {
    private final JwtTokenUtil sut = new JwtTokenUtil("dHJhdmVsLWJ1ZGdldC1zZWNyZXQta2V5LXN0cmljdC1tYW5hZ2VyIUAj");

    @Test
    @DisplayName("Generate accessToken when user's id is 1L")
    void generateAccessToken_success() {
        // given
        UserDetails userDetails = User.builder()
            .id(1L)
            .nickname("testuser").build();

        // when
        String token = sut.generateToken(userDetails);

        // then
        Assertions.assertNotNull(token);
    }

    @Test
    @DisplayName("Generate refreshToken when user's id is 1L")
    void generateRefreshToken_success() {
        // given
        UserDetails userDetails = User.builder()
            .id(1L)
            .nickname("testuser").build();

        // when
        String token = sut.generateRefreshToken(userDetails);

        // then
        Assertions.assertNotNull(token);
    }

    @Test
    @DisplayName("Validate token")
    void validateToken_success() {
        // given
        UserDetails userDetails = User.builder()
            .id(1L)
            .nickname("testuser").build();
        String token = sut.generateRefreshToken(userDetails);

        // when
        boolean result = sut.validateToken(token, userDetails);

        // then
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("Get username from token")
    void getUsernameFromToken_success() {
        // given
        Long userId = 1L;
        UserDetails userDetails = User.builder()
            .id(userId)
            .nickname("testuser").build();
        String token = sut.generateRefreshToken(userDetails);

        // when
        String userIdStr = sut.getUsernameFromToken(token);

        // then
        Assertions.assertEquals(userId.toString(), userIdStr);
    }

    @Test
    @DisplayName("Get expiration date from token")
    void getExpirationDateFromToken_success() {
        // given
        Long userId = 1L;
        UserDetails userDetails = User.builder()
            .id(userId)
            .nickname("testuser").build();
        String token = sut.generateRefreshToken(userDetails);

        // when
        Date expirationDate = sut.getExpirationDateFromToken(token);

        // then
        Assertions.assertNotNull(expirationDate);
    }
}
