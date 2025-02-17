package skcc.arch.app.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import skcc.arch.app.filter.JwtRequestFilter;
import skcc.arch.app.handler.CustomAccessDeniedHandler;
import skcc.arch.app.handler.CustomAuthenticationEntryPoint;
import skcc.arch.app.util.JwtUtil;
import skcc.arch.biz.user.service.CustomUserDetailService;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomUserDetailService customUserDetailService;
    private final JwtUtil jwtUtil;
    private static final String[] AUTH_WHITELIST = {
            // FIXME - 타임리프(추후제거)
            "/","/login", "/register",
            // FIXME - 정적파일(추후제건
            "/css/**", "/js/**", "/images/**", "/favicon.ico",
//            "/**",
            // 캐시, 파일, 로그, 인증
            "/api/cache/**",
            "/api/log/**",
            "/api/users/authenticate"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF
                .csrf(AbstractHttpConfigurer::disable)
                // 세션관리 상태없음
                .sessionManagement(session->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Form Login 및 FrameOption 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))

                // JWT 요청 필터를 UsernamePasswordAuthenticationFilter 전에 추가
                .addFilterBefore(new JwtRequestFilter(customUserDetailService, jwtUtil), UsernamePasswordAuthenticationFilter.class)


                .exceptionHandling((exceptionHandling) -> exceptionHandling
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                // 권한 규칙
                .authorizeHttpRequests(auth -> auth
                        // 화이트리스트는 허용
                        .requestMatchers(AUTH_WHITELIST) .permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        // 특정영역은 ADMIN 만 허용
                        .requestMatchers("/api/users/admin/**").hasRole("ADMIN")
                        // 나머지 요청은 인증 필요
                        .anyRequest().authenticated()
                )
        ;

        return http.build();
    }


}
