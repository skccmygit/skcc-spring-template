package skcc.arch.app.message;

public interface MessageService {

    // 메시지 발행
    void publishMessage(String destination, String message);

    // 메시지  구독 (구독할 곳과 메시지 수신 핸들러)
    void subscribeToMessages(String destination, MessageHandler handler);

    // Pull 방식
    String pullMessage(String destination);

    @FunctionalInterface
    interface MessageHandler {
        void handleMessage(String message);
    }
}