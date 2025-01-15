package skcc.arch.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import skcc.arch.domain.user.dto.UserDto;
import skcc.arch.domain.user.model.UserStatus;

@Getter
@Builder
public class UserResponseDto {

    // 응답값으로 필요한 정보만 세팅
    private Long id;
    private String email;
    private String username;
    private UserStatus status;


    public static UserResponseDto fromUserDto (UserDto userDto) {
        return UserResponseDto.builder()
                .id(userDto.getId())
                .email(userDto.getEmail())
                .username(userDto.getUsername())
                .status(userDto.getStatus())
                .build();
    }
}
