package skcc.arch.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import skcc.arch.user.infrastructure.UserRepositoryJpaCustomImpl;
import skcc.arch.user.infrastructure.UserRepositoryMybatisImpl;
import skcc.arch.user.infrastructure.jpa.UserRepositoryJpa;
import skcc.arch.user.infrastructure.mybatis.UserRepositoryMybatis;
import skcc.arch.user.service.port.UserRepository;

@Configuration
public class AppConfig {

    @Autowired
    private UserRepositoryJpa userRepositoryJpa;

    @Autowired
    private UserRepositoryMybatis userRepositoryMybatis;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Jpa 또는 MyBatis 구현체 선택
     */
    @Bean
    public UserRepository userRepository() {
//        return new UserRepositoryJpaCustomImpl(userRepositoryJpa);
        return new UserRepositoryMybatisImpl(userRepositoryMybatis);
    }
}