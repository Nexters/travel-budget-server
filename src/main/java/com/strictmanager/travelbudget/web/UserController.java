package com.strictmanager.travelbudget.web;

import com.strictmanager.travelbudget.domain.user.User;
import io.swagger.annotations.ApiOperation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@ApiController
@RequiredArgsConstructor
public class UserController {

    @ApiOperation(value = "내 정보 조회")
    @GetMapping("/users/me")
    public ResponseEntity<UserResponse> createPayment(
        @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(
          new UserResponse(
              user.getId(),
              user.getKakaoId(),
              user.getNickname(),
              user.getProfileImage(),
              user.getThumbnailImage()
          )
        );
    }

    @Getter
    private static class UserResponse {

        private final Long userId;
        private final String kakaoId;
        private final String nickname;
        private final String profileImage;
        private final String thumbnailImage;

        public UserResponse(
            Long userId,
            String kakaoId,
            String nickname,
            String profileImage,
            String thumbnailImage
        ) {
            this.userId = userId;
            this.kakaoId = kakaoId;
            this.nickname = nickname;
            this.profileImage = profileImage;
            this.thumbnailImage = thumbnailImage;
        }
    }
}
