package skcc.arch.user.infrastructure.mybatis;

import lombok.*;
import skcc.arch.user.domain.UserRole;
import skcc.arch.user.domain.User;
import skcc.arch.user.domain.UserStatus;

import java.time.LocalDateTime;

/**
 * MyBatis User Dto
 */

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String email;
    private String password;
    private String username;
    private UserRole role;
    private UserStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;


    public static UserDto from (User user){
        if (user == null) {
            return null;
        }
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .username(user.getUsername())
                .role(user.getRole())
                .status(user.getStatus())
                .createdDate(user.getCreatedDate())
                .lastModifiedDate(user.getLastModifiedDate())
                .build();
    }

    public User toModel() {
        return User.builder()
                .id(id)
                .email(email)
                .password(password)
                .username(username)
                .role(role)
                .status(status)
                .createdDate(createdDate)
                .lastModifiedDate(lastModifiedDate)
                .build();
    }

}
