package skcc.arch.app.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import skcc.arch.app.aop.LogFormatAop;
import skcc.arch.app.filter.LogTraceIdFilter;
import skcc.arch.user.infrastructure.UserRepositoryJpaCustomImpl;
import skcc.arch.user.infrastructure.jpa.UserRepositoryJpa;
import skcc.arch.user.infrastructure.mybatis.UserRepositoryMybatis;
import skcc.arch.user.service.port.UserRepository;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final UserRepositoryJpa userRepositoryJpa;
    private final JPAQueryFactory jpaQueryFactory;
    private final UserRepositoryMybatis userRepositoryMybatis;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Jpa 또는 MyBatis 구현체 선택
     */
    @Bean
    public UserRepository userRepository() {
        return new UserRepositoryJpaCustomImpl(userRepositoryJpa, jpaQueryFactory);
//        return new UserRepositoryMybatisImpl(userRepositoryMybatis);
    }


    /**
     * LogTraceId 적용
     */
    @Bean
    public LogTraceIdFilter logTraceIdFilter() {
        return new LogTraceIdFilter();
    }

    /**
     * Log Format Custom AOP 적용 (시그니처 + Depth 표현)
     */
    @Bean
    public LogFormatAop logFormatAop() {
        return new LogFormatAop();
    }

}