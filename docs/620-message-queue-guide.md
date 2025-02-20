### **메시지 큐(Message Service) 사용가이드**
> `MessageService`는 메시징 시스템(RabbitMQ, Kafka 등)을 추상화 하였습니다.  
이를 통해 다양한 메시지 브로커 구현체를 유연하게 확장 가능하며, 브로커 간 전환 또는 다중 브로커 지원 등을 쉽게 할 수 있습니다. 

### **1. 인터페이스 정의**
발행,구독,pull 기능으로 정의 하였습니다.   
구독의 경우 보통 구독할 대상 시스템에서 코드로 구현을 하기 때문에 생략하였습니다.  
해당 인터페이스의 구독은 실시간 구독을 의미하여 검증 기반으로 작성하였습니다.

```java
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
```
### **2. 인터페이스 설계의 주요 포인트**
1. **추상화**:
    - `MessageService`는 RabbitMQ, Kafka, Redis Pub/Sub 등 다양한 메시지 브로커에 대한 공통적인 인터페이스를 제공하여 구현체를 변경하거나 확장할 수 있도록 추상화되어 있습니다.

2. **기능 정의**:
    - **`publishMessage`**: 메시지를 특정 목적지(예: Queue, Exchange)에 발행(Publish).
    - **`subscribeToMessages`**: 메시지 처리를 위한 구독(Subscribe).
    - **`pullMessage`**: 메시지를 가져오는 Pull 기능(요청 시점에 큐로부터 가져오기).
    - **`MessageHandler`**: 사용자 정의 핸들러를 활용해 메시지를 처리하도록 지원.

3. **확장성**:
    - 추상화된 인터페이스를 기반으로 RabbitMQ뿐만 아니라 Kafka, Redis Pub/Sub 등의 메시지 브로커에도 쉽게 확장 가능합니다.

4. **유연성**:
    - **Pub/Sub 및 Pull 방식**을 모두 지원합니다.
    - 메시지를 비동기적으로 처리할 때 적합한 인터페이스를 제공합니다.

### **3. 구현체 가이드**
> 다양한 메시지 브로커들을 구현할 수 있습니다.  
> 본 가이드에서는 RabbitMQ 기준으로 구현하였습니다.

### **3.1 RabbitMQ 구현체**
> RabbitMQ의 경우 destination 필드에 @ 포함여부로 exchage 와 queue를 구분합니다.

1. **Exchange를 동적으로 생성하여 메시지 발행 (Publish)**:
    - `@`가 포함된 목적지를 Exchange로 사용.
    - 목적지에 따라 동적으로 Exchange를 선언하고 메시지를 발행.

2. **큐(Queue)를 구독하여 실시간으로 메시지 처리 (Subscribe)**:
    - **Pub/Sub 패턴**으로 메시지를 실시간 수신하고 처리 핸들러를 실행.

3. **큐에서 메시지를 Pull 방식으로 가져오기 (Pull)**:
    - 특정 목적지(Queue)에서 메시지를 한 번에 하나씩 가져오는 방식.

### **3.2 KafkaMQ 구현체**
N/A