package skcc.arch.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import skcc.arch.domain.user.model.User;
import skcc.arch.domain.user.model.UserStatus;

@Getter
@Builder
public class UserResponseDto {

    // 응답값으로 필요한 정보만 세팅
    private Long id;
    private String email;
    private String username;
    private UserStatus status;


    public static UserResponseDto fromUserDto (User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .status(user.getStatus())
                .build();
    }
}
