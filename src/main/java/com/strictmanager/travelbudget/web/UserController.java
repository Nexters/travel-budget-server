package com.strictmanager.travelbudget.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.strictmanager.travelbudget.domain.user.User;
import com.strictmanager.travelbudget.domain.user.UserService;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@ApiController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @ApiOperation(value = "내 정보 조회")
    @GetMapping("/users/me")
    public ResponseEntity<UserResponse> getMe(
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

    @ApiOperation(value = "내 정보 업데이트")
    @PutMapping("/users/me")
    public ResponseEntity<UpdateUserResponse> updateMe(
        @AuthenticationPrincipal User user,
        @RequestBody @Valid UpdateUserRequest request
    ) {
        userService.updateUserNickname(user.getId(), request.getNickname());
        return ResponseEntity.ok(new UpdateUserResponse(user.getId()));
    }

    @Getter
    private static class UserResponse {

        private final Long userId;
        private final String kakaoId;
        private final String nickname;
        private final String profileImage;
        private final String thumbnailImage;

        UserResponse(
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

    @Getter
    private static class UpdateUserRequest {
        private final String nickname;

        @JsonCreator
        UpdateUserRequest(
            @JsonProperty(value = "nickname", required = true) String nickname
        ) {
            this.nickname = nickname;
        }
    }

    @Getter
    private static class UpdateUserResponse {

        private final Long userId;

        UpdateUserResponse(Long userId) {
            this.userId = userId;
        }
    }
}
