package skcc.arch.user.controller.response;

import lombok.Builder;
import lombok.Getter;
import skcc.arch.user.domain.User;
import skcc.arch.user.domain.UserStatus;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponseDto {

    // 응답값으로 필요한 정보만 세팅
    private Long id;
    private String email;
    private String username;
    private UserStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;


    public static UserResponseDto fromUser(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .status(user.getStatus())
                .createdDate(user.getCreatedDate())
                .lastModifiedDate(user.getLastModifiedDate())
                .build();
    }
}
