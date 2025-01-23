package skcc.arch.app.filter;

import jakarta.servlet.*;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

public class LogTraceIdFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 초기화 로직 (필요 시 구현)
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // 요청마다 유니크 ID 생성
        String traceId = UUID.randomUUID().toString().substring(0,26);
        try {
            // MDC에 traceId 설정
            MDC.put("traceId", String.format("[%s]", traceId));
            chain.doFilter(request, response);  // 다음 필터로 요청 전달
        } finally {
            // 요청 종료 시 MDC에서 traceId 제거 (메모리 누수 방지)
            MDC.remove("traceId");
        }
    }

    @Override
    public void destroy() {
        // 종료 로직 (필요 시 구현)
    }
}