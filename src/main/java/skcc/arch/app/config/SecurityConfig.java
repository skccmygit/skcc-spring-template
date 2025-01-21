package skcc.arch.app.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import skcc.arch.app.filter.JwtRequestFilter;
import skcc.arch.app.util.JwtUtil;
import skcc.arch.user.service.MyUserDetailService;


@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final MyUserDetailService myUserDetailService;
    private final JwtUtil jwtUtil;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                // 타임리프
                                "/","/login", "/register",

                                // API
                                "/api/users","/api/users/authenticate" , "/api/users/test-user",

                                // 정적 파일
                                "/css/**", "/js/**", "/images/**", "/favicon.ico")
                        .permitAll() // 로그인, 회원가입 및 리소스 접근 허용
//                        .requestMatchers("/home").hasAnyAuthority("USER")
//                        .requestMatchers("/**").permitAll()
                        .anyRequest().authenticated() // 나머지 요청은 인증 필요
                )
                .addFilterBefore(new JwtRequestFilter(myUserDetailService, jwtUtil), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        ;

        return http.build();
    }


}
