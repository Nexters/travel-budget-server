package com.strictmanager.travelbudget.web;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.strictmanager.travelbudget.domain.user.User;
import com.strictmanager.travelbudget.domain.user.UserException;
import com.strictmanager.travelbudget.domain.user.UserService;
import com.strictmanager.travelbudget.infra.auth.JwtTokenUtil;
import io.swagger.annotations.ApiOperation;
import java.util.Date;
import javax.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@ApiController
@RequiredArgsConstructor
public class AuthController {
    private static final String DEFAULT_NICKNAME = "엄격한관리자";
    private static final String DEFAULT_PROFILE_IMAGE = "https://i.ibb.co/bPkqftp/select-img.png";
    private static final String DEFAULT_THUMBNAIL_IMAGE = "https://i.ibb.co/bPkqftp/select-img.png";

    private final JwtTokenUtil jwtAccessTokenUtil;
    private final JwtTokenUtil jwtRefreshTokenUtil;
    private final UserService userService;

    @ApiOperation(value = "카카오 로그인")
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

    @ApiOperation(value = "JWT 토큰 생성")
    @PostMapping("/auth/token/create")
    public ResponseEntity<JwtResponse> createToken(@RequestBody @Valid TokenCreateRequest tokenCreateRequest) {
        log.debug("[createToken] params - {}", tokenCreateRequest);
        UserDetails user = userService.getUserByKakaoId(tokenCreateRequest.getKakaoId()).orElseThrow(UserException::new);

        final String accessToken = jwtAccessTokenUtil.generateToken(user);
        final String refreshToken = jwtRefreshTokenUtil.generateToken(user);
        return ResponseEntity.ok(new JwtResponse(accessToken, refreshToken, jwtAccessTokenUtil.getExpirationDateFromToken(accessToken)));
    }

    @ApiOperation(value = "JWT 토큰 갱신")
    @PostMapping("/auth/token/refresh")
    public ResponseEntity<JwtResponse> refreshToken(@RequestBody @Valid TokenRefreshRequest tokenRefreshRequest) {
        log.debug("[refreshToken] params - {}", tokenRefreshRequest);
        String userId = jwtRefreshTokenUtil.getUsernameFromToken(tokenRefreshRequest.getRefreshToken());
        UserDetails user = userService.loadUserByUsername(userId);

        if (!jwtRefreshTokenUtil.validateToken(tokenRefreshRequest.getRefreshToken(), user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        final String accessToken = jwtAccessTokenUtil.generateToken(user);
        final String refreshToken = jwtRefreshTokenUtil.generateToken(user);
        return ResponseEntity.ok(new JwtResponse(accessToken, refreshToken, jwtAccessTokenUtil.getExpirationDateFromToken(accessToken)));
    }

    @Getter
    @ToString
    private static class KakaoUserRequest {

        private final String kakaoId;
        private final String nickname;
        private final String thumbnailImage;
        private final String profileImage;

        @JsonCreator
        private KakaoUserRequest(
            @JsonProperty(value = "kakao_id", required = true) String kakaoId,
            @JsonProperty(value = "nickname") String nickname,
            @JsonProperty(value = "thumbnail_image") String thumbnailImage,
            @JsonProperty(value = "profile_image") String profileImage
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
        private TokenCreateRequest(
            @JsonProperty(value = "kakao_id", required = true) String kakaoId
        ) {
            this.kakaoId = kakaoId;
        }
    }

    @Getter
    @ToString
    private static class TokenRefreshRequest {

        private final String refreshToken;

        @JsonCreator
        private TokenRefreshRequest(
            @JsonProperty(value = "refresh_token", required = true) String refreshToken
        ) {
            this.refreshToken = refreshToken;
        }
    }

    @Getter
    private static class SignUpResponse {

        private final String kakaoId;

        private SignUpResponse(String kakaoId) {
            this.kakaoId = requireNonNull(kakaoId);
        }
    }

    @Getter
    private static class JwtResponse {

        private final String accessToken;
        private final String refreshToken;
        private final Long expireDt;
        private final String tokenType = "Bearer";

        private JwtResponse(String accessToken, String refreshToken, Date expireDt) {
            this.accessToken = requireNonNull(accessToken);
            this.refreshToken = requireNonNull(refreshToken);
            this.expireDt = requireNonNull(expireDt.getTime());
        }
    }
}