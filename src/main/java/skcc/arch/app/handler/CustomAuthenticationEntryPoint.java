package skcc.arch.app.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import skcc.arch.app.dto.ApiResponse;
import skcc.arch.app.exception.CustomException;
import skcc.arch.app.exception.ErrorCode;
import skcc.arch.app.util.HttpResponseUtil;

import java.io.IOException;


/**
 * 인증되지 않은 사용자의 요청을 처리하기 위한 Spring Security AuthenticationEntryPoint 구현 클래스입니다.
 */
@Slf4j(topic = "UNAUTHORIZATION_EXCEPTION_HANDLER")
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    /**
     * 인증되지 않은 사용자의 요청이 들어왔을 때 호출됩니다.
     * 
     * @param request  클라이언트의 HttpServletRequest 객체
     * @param response 클라이언트의 HttpServletResponse 객체
     * @param authException 인증 관련 예외
     * @throws IOException 입출력 예외 발생 시
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        log.error("Not Authenticated Request", authException);
    
        ApiResponse<Void> failResponse = ApiResponse.fail(new CustomException(ErrorCode.UNAUTHORIZED));
        HttpResponseUtil.writeResponseBody(response,failResponse);
    }

}
