package skcc.arch.user.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import skcc.arch.app.exception.CustomException;
import skcc.arch.app.exception.ErrorCode;
import skcc.arch.app.util.JwtUtil;
import skcc.arch.user.domain.User;
import skcc.arch.user.domain.UserCreateRequest;
import skcc.arch.user.service.port.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements skcc.arch.user.controller.port.UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 회원가입 메서드
    @Override
    @Transactional
    public User create(UserCreateRequest userCreateRequest) {

        checkUserExistByEmail(userCreateRequest.getEmail());
        String encodedPassword = passwordEncoder.encode(userCreateRequest.getPassword()); // 비밀번호 암호화
        userCreateRequest.setPassword(encodedPassword);
        return userRepository.save(User.from(userCreateRequest));
    }

    // 인증
    @Override
    public String authenticate(String email, String rawPassword) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ELEMENT));
        boolean matches = passwordEncoder.matches(rawPassword, user.getPassword());
        if (!matches) {
            throw new CustomException(ErrorCode.NOT_MATCHED_PASSWORD);
        }

        // JWT Token 생성
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", user.getEmail());
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole());

        String token = jwtUtil.generateToken(claims);
        log.info("generated token : {}", token);

        return token;
    }

    // 이메일로 존재여부 체크
    private void checkUserExistByEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            throw new CustomException(ErrorCode.EXIST_ELEMENT);
        }
    }

    // 전체 사용자 조회
    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ELEMENT));
    }

}