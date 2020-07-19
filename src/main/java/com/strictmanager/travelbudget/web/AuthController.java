package com.strictmanager.travelbudget.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.strictmanager.travelbudget.domain.user.User;
import com.strictmanager.travelbudget.infra.auth.JwtTokenUtil;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@ApiController
@RequiredArgsConstructor
public class AuthController {
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/kakao/signin")
    public ResponseEntity<?> kakaoSignin(@RequestBody @Valid KakaoUserRequest kakaoUserRequest) {
        log.debug("[kakaoSignin] params - {}", kakaoUserRequest);
        // TODO: Create kakaoUser
        UserDetails user = new User("test");
        final String accessToken = jwtTokenUtil.generateToken(user);
        final String refreshToken = jwtTokenUtil.generateRefreshToken(user);
        return ResponseEntity.ok(new JwtResponse(accessToken, refreshToken));
    }

    @Getter
    @ToString
    private static class KakaoUserRequest {
        private final String kakaoId;

        private final String nickname;

        private final String thumbnailImage;

        private final String profileImage;

        @JsonCreator
        public KakaoUserRequest(
            @JsonProperty(value = "kakao_id", required = true) String kakaoId,
            @JsonProperty("nickname") String nickname,
            @JsonProperty("thumbnail_image") String thumbnailImage,
            @JsonProperty("profile_image") String profileImage
        ) {
            this.kakaoId = kakaoId;
            this.nickname = nickname;
            this.thumbnailImage = thumbnailImage;
            this.profileImage = profileImage;
        }
    }

    @Getter
    private static class JwtResponse {
        private final String accessToken;
        private final String refreshToken;
        private final String tokenType = "bearer";

        private JwtResponse(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }
}