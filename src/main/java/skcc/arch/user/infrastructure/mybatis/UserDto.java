package skcc.arch.user.infrastructure.mybatis;

import jakarta.persistence.Column;
import lombok.*;
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
                .status(status)
                .createdDate(createdDate)
                .lastModifiedDate(lastModifiedDate)
                .build();
    }

}
