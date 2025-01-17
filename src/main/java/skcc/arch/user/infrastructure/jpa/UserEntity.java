package skcc.arch.user.infrastructure.jpa;

import jakarta.persistence.*;
import lombok.Getter;
import skcc.arch.common.infrastructure.jpa.BaseEntity;
import skcc.arch.user.domain.User;
import skcc.arch.user.domain.UserStatus;

@Getter
@Entity
@Table(name = "users")
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String email;

    private String password;

    private String username;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    public static UserEntity from(User user) {
        UserEntity userEntity = new UserEntity();
        userEntity.id = user.getId();
        userEntity.email = user.getEmail();
        userEntity.password = user.getPassword();
        userEntity.username = user.getUsername();
        userEntity.status = user.getStatus();
        return userEntity;
    }

    public User toModel() {
        return User.builder()
                .id(id)
                .email(email)
                .password(password)
                .username(username)
                .status(status)
                .createdDate(super.getCreatedDate())
                .lastModifiedDate(super.getLastModifiedDate())
                .build();
    }

}