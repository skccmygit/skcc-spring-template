package skcc.arch.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import skcc.arch.app.exception.CustomException;
import skcc.arch.app.exception.ErrorCode;
import skcc.arch.user.domain.User;
import skcc.arch.user.service.port.UserRepository;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MyUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User myUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ELEMENT));
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(myUser.getRole().name()));

        return org.springframework.security.core.userdetails.User.builder()
                .username(myUser.getEmail())
                .password(myUser.getPassword())
                .authorities(authorities)
                .build();
    }
}
