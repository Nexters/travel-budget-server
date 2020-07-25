package com.strictmanager.travelbudget.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.strictmanager.travelbudget.domain.user.User;
import com.strictmanager.travelbudget.domain.user.UserException;
import com.strictmanager.travelbudget.domain.user.UserService;
import com.strictmanager.travelbudget.infra.auth.JwtTokenUtil;
import java.security.Principal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Objects;
import javax.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@ApiController
@RequiredArgsConstructor
public class AuthController {
    private static final String DEFAULT_NICKNAME = "엄격한관리자";
    private static final String DEFAULT_PROFILE_IMAGE = "https://publicdomainvectors.org/photos/abstract-user-flat-4.png";
    private static final String DEFAULT_THUMBNAIL_IMAGE = "https://publicdomainvectors.org/photos/abstract-user-flat-4.png";

    private final JwtTokenUtil jwtAccessTokenUtil;
    private final JwtTokenUtil jwtRefreshTokenUtil;
    private final UserService userService;

    @PostMapping("/kakao/signup")
    public ResponseEntity<SignUpResponse> kakaoSignin(@RequestBody @Valid KakaoUserRequest kakaoUserRequest) {
        log.debug("[kakaoSignin] params - {}", kakaoUserRequest);
        User signUpUser = userService.signUp(
            User.builder()
                .kakaoId(kakaoUserRequest.getKakaoId())
                .nickname(ObjectUtils.defaultIfNull(kakaoUserRequest.getNickname(), DEFAULT_NICKNAME))
                .profileImage(ObjectUtils.defaultIfNull(kakaoUserRequest.getProfileImage(), DEFAULT_PROFILE_IMAGE))
                .thumbnailImage(ObjectUtils.defaultIfNull(kakaoUserRequest.getThumbnailImage(), DEFAULT_THUMBNAIL_IMAGE))
                .build()
        );
        return ResponseEntity.ok(new SignUpResponse(signUpUser.getKakaoId()));
    }

    @PostMapping("/auth/token/create")
    public ResponseEntity<JwtResponse> createToken(@RequestBody @Valid TokenCreateRequest tokenCreateRequest) {
        log.debug("[createToken] params - {}", tokenCreateRequest);
        UserDetails user = userService.getUserByKakaoId(tokenCreateRequest.getKakaoId()).orElseThrow(UserException::new);

        final String accessToken = jwtAccessTokenUtil.generateToken(user);
        final String refreshToken = jwtRefreshTokenUtil.generateToken(user);
        return ResponseEntity.ok(new JwtResponse(accessToken, refreshToken, jwtAccessTokenUtil.getExpirationDateFromToken(accessToken)));
    }

    @PostMapping("/auth/token/refresh")
    public ResponseEntity<JwtResponse> refreshToken(@RequestBody @Valid TokenRefreshRequest tokenRefreshRequest) {
        log.debug("[refreshToken] params - {}", tokenRefreshRequest);
        UserDetails user = userService.getUserByKakaoId(tokenRefreshRequest.getKakaoId()).orElseThrow(UserException::new);

        if (!jwtRefreshTokenUtil.validateToken(tokenRefreshRequest.getRefreshToken(), user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        final String accessToken = jwtAccessTokenUtil.generateToken(user);
        final String refreshToken = jwtRefreshTokenUtil.generateToken(user);
        return ResponseEntity.ok(new JwtResponse(accessToken, refreshToken, jwtAccessTokenUtil.getExpirationDateFromToken(accessToken)));
    }

    // TODO: Remove
    @GetMapping("/me")
    public ResponseEntity<?> me(final Principal principal) {
        log.debug("userId: {}", principal.getName());
        return ResponseEntity.ok().build();
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
    @ToString
    private static class TokenCreateRequest {

        private final String kakaoId;

        @JsonCreator
        public TokenCreateRequest(
            @JsonProperty(value = "kakao_id", required = true) String kakaoId
        ) {
            this.kakaoId = kakaoId;
        }
    }

    @Getter
    @ToString
    private static class TokenRefreshRequest {

        private final String kakaoId;
        private final String refreshToken;

        @JsonCreator
        public TokenRefreshRequest(
            @JsonProperty(value = "kakao_id", required = true) String kakaoId,
            @JsonProperty(value = "refresh_token", required = true) String refreshToken
        ) {
            this.kakaoId = kakaoId;
            this.refreshToken = refreshToken;
        }
    }

    @Getter
    private static class SignUpResponse {

        private final String kakaoId;

        private SignUpResponse(String kakaoId) {
            this.kakaoId = Objects.requireNonNull(kakaoId);
        }
    }

    @Getter
    private static class JwtResponse {

        private final String accessToken;
        private final String refreshToken;
        private final ZonedDateTime expireDt;
        private final String tokenType = "bearer";

        private JwtResponse(String accessToken, String refreshToken, Date expireDt) {
            this.accessToken = Objects.requireNonNull(accessToken);
            this.refreshToken = Objects.requireNonNull(refreshToken);
            this.expireDt = Objects.requireNonNull(ZonedDateTime.ofInstant(expireDt.toInstant(), ZoneId.systemDefault()));
        }
    }
}