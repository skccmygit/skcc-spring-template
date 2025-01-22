package skcc.arch.app.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import skcc.arch.app.dto.ApiResponse;
import skcc.arch.app.exception.CustomException;
import skcc.arch.app.exception.ErrorCode;
import skcc.arch.app.util.HttpResponseUtil;

import java.io.IOException;



/**
 * 이 클래스는 접근 거부 예외를 처리하기 위한 커스텀 핸들러입니다.
 * 접근 권한이 없는 요청에 대해 실패 응답을 보냅니다.
 */
@Slf4j(topic = "FORBIDDEN_EXCEPTION_HANDLER")
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * 접근 거부 예외를 처리합니다. 예외를 로깅하고,
     * 실패 응답을 생성하여 HTTP 응답 본문에 작성합니다.
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.error("No Authorities", accessDeniedException);
        ApiResponse<Void> failResponse = ApiResponse.fail(new CustomException(ErrorCode.ACCESS_DENIED));
        HttpResponseUtil.writeResponseBody(response,failResponse);
    }
}
