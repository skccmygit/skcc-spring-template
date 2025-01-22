package skcc.arch.app.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import skcc.arch.app.dto.ApiResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


/**
 * HTTP 응답을 표준화된 방식으로 처리하기 위한 유틸리티 클래스입니다.
 */
public abstract class HttpResponseUtil {

    /**
     * 객체를 JSON 문자열로 변환하기 위한 재사용 가능한 ObjectMapper 인스턴스입니다.
     */
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 주어진 ApiResponse 데이터를 JSON 문자열로 변환하여 HTTP 응답 본문에 작성합니다.
     *
     * @param response     HTTP 응답 객체입니다.
     * @param failResponse 응답에 포함할 ApiResponse 데이터입니다.
     * @throws IOException 쓰기 도중 I/O 오류가 발생하면 예외가 발생합니다.
     */
    public static void writeResponseBody(HttpServletResponse response, ApiResponse<?> failResponse) throws IOException {
        String responseBody = objectMapper.writeValueAsString(failResponse);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(failResponse.getStatus().value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(responseBody);
    }
}
