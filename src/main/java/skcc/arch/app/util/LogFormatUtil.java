package skcc.arch.app.util;

import org.slf4j.MDC;

import java.util.ArrayDeque;
import java.util.Deque;

public abstract class LogFormatUtil {

    private static final String DEPTH_PREFIX = "depth";
    private static final String PREFIX_STR = "--";
    private static final String SIGNATURE_KEY = "signature";
    private static final ThreadLocal<Deque<Integer>> depthThreadLocal = ThreadLocal.withInitial(ArrayDeque::new);

    // Depth 초기화 (Controller 진입 시 호출됨)
    public static void initializeDepth() {
        Deque<Integer> stack = depthThreadLocal.get();
        stack.clear(); // 스택 초기화
        stack.push(0); // 기본 Depth 1 설정
        MDC.put(DEPTH_PREFIX, "");
    }

    // Depth 증가 (계층 호출 시)
    public static void incrementDepth() {
        Deque<Integer> stack = depthThreadLocal.get();
        int currentDepth = stack.peek(); // 현재 Depth
        int newDepth = currentDepth + 1;
        stack.push(newDepth); // Depth 증가시키며 스택에 저장
        MDC.put(DEPTH_PREFIX, String.format("[%s]", PREFIX_STR.repeat(newDepth))); // MDC에 새로운 Depth 설정
    }

    // Depth 감소 (계층 호출 완료 시)
    public static void decrementDepth() {
        Deque<Integer> stack = depthThreadLocal.get();
        stack.pop(); // 스택에서 마지막 Depth 제거
        if (!stack.isEmpty()) {
            MDC.put(DEPTH_PREFIX, String.valueOf(stack.peek())); // 이전 Depth 복원
        } else {
            MDC.remove(DEPTH_PREFIX); // 스택이 비어있으면 MDC에서 제거
        }
    }

    public static void clearDepth() {
        depthThreadLocal.remove();
        MDC.remove(DEPTH_PREFIX);
    }

    public static boolean isEmpty() {
        return depthThreadLocal.get().isEmpty();
    }

    public static void formatSignature(String loggerName, String methodName) {
        // Logger.Method로 연결 (20자리 고정된 Logger, 10자리 Method)
        String formattedValue = String.format("[%s.%s]",
                padLeft(loggerName, 30),  // Logger 이름 20자리로 자르거나 채움
                padLeft(methodName, 20)); // Method 이름 10자리로 자르거나 채움

        // 패딩된 값을 formattedLogger에 저장
        MDC.put(SIGNATURE_KEY, formattedValue);
    }

    // 패딩 메서드 (왼쪽으로 채움)
    private static String padLeft(String str, int length) {
        if (str == null) return ""; // null 처리
        if (str.length() > length) {
            return str.substring(0, length); // 잘라내기
        }
        return String.format("%" + length + "s", str); // 왼쪽으로 공백 채움
    }
}