package skcc.arch.app.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import skcc.arch.app.util.LogFormatUtil;

@Aspect
public class LogFormatAop {

    // Controller 계층 Pointcut
    @Pointcut("within(@org.springframework.stereotype.Controller *) || within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerLayer() {}

    // Service 계층 Pointcut
    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceLayer() {}

    // Repository 계층 Pointcut
    @Pointcut("within(@org.springframework.stereotype.Repository *)")
    public void repositoryLayer() {}

    // 모든 Pointcut 묶기
    @Pointcut("controllerLayer() || serviceLayer() || repositoryLayer()")
    public void allLayers() {}

    /**
     * 로그 메시지 앞에 Depth 를 추가한다
     */
    @Around("allLayers()")
    public Object manageDepth(ProceedingJoinPoint joinPoint) throws Throwable {

        boolean isFirst = LogFormatUtil.isEmpty();
        try {
            // 비었을 경우
            if(isFirst) {
                LogFormatUtil.initializeDepth(); // Depth 스택 및 MDC 초기화
            }else {
                LogFormatUtil.incrementDepth();
            }
            return joinPoint.proceed();
        } finally {
            if (isFirst) {
                LogFormatUtil.clearDepth();
            } else {
                LogFormatUtil.decrementDepth();
            }
        }
    }

    /**
     * 클래스명 + 메서드명 정렬
     */
    @Before("allLayers()")
    public void genSignatureFormat(JoinPoint joinPoint) {
        // 호출된 클래스 이름과 메서드 이름 추출
        String loggerName = joinPoint.getTarget().getClass().getSimpleName(); // 호출 클래스명
        String methodName = joinPoint.getSignature().getName();
        // LogFormatter를 통해 값을 MDC에 저장
        LogFormatUtil.formatSignature(loggerName, methodName);
    }
}