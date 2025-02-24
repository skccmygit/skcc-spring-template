package skcc.arch.app.util;

import org.slf4j.MDC;

import java.util.ArrayDeque;
import java.util.Deque;

public abstract class LogFormatUtil {

    private static final String START_PREFIX = "-->";
    private static final String COMPLETE_PREFIX = "<--";

    private static final String DEPTH_KEY = "depth";
    private static final ThreadLocal<Deque<Integer>> depthThreadLocal = ThreadLocal.withInitial(ArrayDeque::new);
    private static final ThreadLocal<Deque<String>> signatureThreadLocal = ThreadLocal.withInitial(ArrayDeque::new);

    /**
     * 로그의 초기 깊이 및 서명을 설정합니다.
     *
     * @param signature 로그 서명
     */
    public static void initializeDepth(String signature) {
        initializeThreadLocal(depthThreadLocal, 0);
        initializeThreadLocal(signatureThreadLocal, signature);
        updateMDC(START_PREFIX, 0, signature);
    }

    /**
     * 로그 깊이를 증가시키고 새로운 서명을 추가합니다.
     *
     * @param signature 추가할 로그 서명
     */
    public static void incrementDepth(String signature) {
        int newDepth = pushToThreadLocal(depthThreadLocal, depthThreadLocal.get().peek() + 1);
        pushToThreadLocal(signatureThreadLocal, signature);
        updateMDC(START_PREFIX, newDepth, signature);
    }

    /**
     * 로그 깊이를 감소시키고 서명을 제거합니다.
     */
    public static void decrementDepth() {
        if (depthThreadLocal.get().size() > 1) {
            depthThreadLocal.get().pop();
            signatureThreadLocal.get().pop();
            updateMDC(COMPLETE_PREFIX, depthThreadLocal.get().peek(), signatureThreadLocal.get().peek());
        } else {
            clearDepth();
        }
    }

    /**
     * 로그 깊이 관련 정보와 MDC 데이터를 초기화합니다.
     */
    public static void clearDepth() {
        depthThreadLocal.remove();
        signatureThreadLocal.remove();
        MDC.remove(DEPTH_KEY);
    }

    /**
     * 로그 깊이가 비어 있는지 확인합니다.
     *
     * @return 깊이가 비어 있으면 true, 아니면 false
     */
    public static boolean isEmpty() {
        return depthThreadLocal.get().isEmpty();
    }

    /**
     * ThreadLocal을 초기화하고 기본값을 설정합니다.
     *
     * @param threadLocal 초기화할 ThreadLocal
     * @param value       설정할 기본값
     * @param <T>         타입 매개변수
     */
    private static <T> void initializeThreadLocal(ThreadLocal<Deque<T>> threadLocal, T value) {
        threadLocal.get().clear();
        threadLocal.get().push(value);
    }

    /**
     * ThreadLocal에 값을 추가합니다.
     *
     * @param threadLocal 값을 추가할 ThreadLocal
     * @param value       추가할 값
     * @param <T>         타입 매개변수
     * @return 추가된 값
     */
    private static <T> T pushToThreadLocal(ThreadLocal<Deque<T>> threadLocal, T value) {
        threadLocal.get().push(value);
        return value;
    }

    /**
     * MDC에 로그 깊이와 서명을 업데이트합니다.
     *
     * @param depth     로그 깊이
     * @param signature 로그 서명
     */
    private static void updateMDC(String prefix, int depth, String signature) {
        MDC.put(DEPTH_KEY, String.format("[ %s%s]", addSpace(prefix, depth), signature));
    }

    private static String addSpace(String prefix, int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append( (i == level - 1) ? "|" + prefix : "| ");
        }
        return sb.toString();
    }
}