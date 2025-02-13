package skcc.arch.app.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import skcc.arch.app.util.LogFormatUtil;

@Aspect
public class LogFormatAop {

    // Controller 계층 Pointcut
    @Pointcut("within(@org.springframework.stereotype.Controller *) || within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerLayer() {
    }

    // Service 계층 Pointcut
    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceLayer() {
    }

    // Repository 계층 Pointcut
    @Pointcut("within(@org.springframework.stereotype.Repository *)")
    public void repositoryLayer() {
    }

    // 모든 Pointcut 묶기
    @Pointcut("controllerLayer() || serviceLayer() || repositoryLayer()")
    public void allLayers() {
    }

    /**
     * 로그 메시지 앞에 Depth 를 추가한다
     */
    @Around("allLayers()")
    public Object manageDepth(ProceedingJoinPoint joinPoint) throws Throwable {
        boolean isFirst = LogFormatUtil.isEmpty();
        String signature = getSignature(joinPoint);

        updateLogDepth(isFirst, signature);

        try {
            return joinPoint.proceed();
        } finally {
            restoreLogDepth(isFirst);
        }
    }

    private String getSignature(ProceedingJoinPoint joinPoint) {
        return joinPoint.getTarget().getClass().getSimpleName() + "." + joinPoint.getSignature().getName();
    }

    private void updateLogDepth(boolean isFirst, String signature) {
        if (isFirst) {
            LogFormatUtil.initializeDepth(signature);
        } else {
            LogFormatUtil.incrementDepth(signature);
        }
    }

    private void restoreLogDepth(boolean isFirst) {
        if (isFirst) {
            LogFormatUtil.clearDepth();
        } else {
            LogFormatUtil.decrementDepth();
        }
    }
}