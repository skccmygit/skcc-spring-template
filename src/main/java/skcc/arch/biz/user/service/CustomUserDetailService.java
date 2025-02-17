package skcc.arch.biz.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import skcc.arch.app.exception.CustomException;
import skcc.arch.app.exception.ErrorCode;
import skcc.arch.biz.user.domain.User;
import skcc.arch.biz.user.service.port.UserRepositoryPort;


/**
 * 사용자 인증 및 사용자 세부 정보 검색을 관리하기 위한 서비스 구현입니다.
 * 이 클래스는 Spring Security의 인증 과정에서 사용됩니다.
 */
@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

    /**
     * 데이터 소스에서 사용자 정보를 액세스하기 위한 리포지토리 인터페이스입니다.
     */
    private final UserRepositoryPort userRepositoryPort;

    /**
     * 주어진 이메일을 통해 사용자 세부 정보를 로드합니다.
     *
     * @param email 로드할 사용자의 이메일
     * @return Spring Security를 위한 사용자 자격증명 및 역할이 포함된 UserDetails
     * @throws UsernameNotFoundException 지정된 이메일의 사용자를 찾을 수 없는 경우 예외가 발생합니다.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 사용자를 찾을 수 없으면 사용자 정의 예외를 던집니다.
        User myUser = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ELEMENT));
        return org.springframework.security.core.userdetails.User.builder()
                .username(myUser.getEmail())
                .password(myUser.getPassword())
                .roles(myUser.getRole().name())
                .build();
    }
}
