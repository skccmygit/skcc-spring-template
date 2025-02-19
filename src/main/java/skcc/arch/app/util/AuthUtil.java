package skcc.arch.app.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

    public String getUID() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalStateException("인증 정보가 없습니다. SecurityContext에서 UID를 가져올 수 없습니다.");
        }
        return authentication.getName();
    }
}
