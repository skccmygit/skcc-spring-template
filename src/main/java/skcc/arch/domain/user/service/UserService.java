package skcc.arch.domain.user.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import skcc.arch.app.exception.CustomException;
import skcc.arch.app.exception.ErrorCode;
import skcc.arch.domain.user.model.User;
import skcc.arch.domain.user.dto.request.UserCreateRequestDto;
import skcc.arch.domain.user.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입 메서드
    public User create(UserCreateRequestDto userCreateRequestDto) {

        checkUserExistByEmail(userCreateRequestDto.getEmail());

        String encodedPassword = passwordEncoder.encode(userCreateRequestDto.getPassword()); // 비밀번호 암호화
        userCreateRequestDto.setPassword(encodedPassword);

        return userRepository.save(userCreateRequestDto);
    }

    // 로그인 처리
    public User login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ELEMENT));
        boolean matches = passwordEncoder.matches(rawPassword, user.getPassword());
        if(!matches) {
            throw new CustomException(ErrorCode.NOT_MATCHED_PASSWORD);
        }
        return user;
    }

    // 이메일로 존재여부 체크
    private void checkUserExistByEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isPresent()) {
            throw new CustomException(ErrorCode.EXIST_ELEMENT);
        }
    }
}