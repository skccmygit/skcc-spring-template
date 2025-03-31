# Message Queue (Message Service) Usage Guide
> `MessageService` abstracts messaging systems (RabbitMQ, Kafka, etc.).  
> This abstraction allows flexible extension of various message broker implementations, enabling easy switching between brokers or supporting multiple brokers.

## **1. Interface Definition**
The interface is defined with publish, subscribe, and pull functionality.   
The subscription part is typically implemented in the target system's code, so it's omitted here.  
The subscription in this interface refers to real-time subscription, implemented based on verification.

```java
public interface MessageService {

    // Message Publishing
    void publishMessage(String destination, String message);

    // Message Subscription (destination and message receive handler)
    void subscribeToMessages(String destination, MessageHandler handler);

    // Pull Method
    String pullMessage(String destination);

    @FunctionalInterface
    interface MessageHandler {
        void handleMessage(String message);
    }
}
```

## **2. Key Points in Interface Design**
1. **Abstraction**:
    - `MessageService` provides a common interface for various message brokers like RabbitMQ, Kafka, and Redis Pub/Sub, allowing implementation changes or extensions through abstraction.

2. **Function Definition**:
    - **`publishMessage`**: Publishes messages to a specific destination (e.g., Queue, Exchange).
    - **`subscribeToMessages`**: Subscribes for message processing.
    - **`pullMessage`**: Pull functionality to retrieve messages (fetching from queue on request).
    - **`MessageHandler`**: Supports custom handler implementation for message processing.

3. **Extensibility**:
    - Based on the abstracted interface, it can be easily extended to message brokers like Kafka and Redis Pub/Sub, not just RabbitMQ.

4. **Flexibility**:
    - Supports both **Pub/Sub and Pull methods**.
    - Provides an interface suitable for asynchronous message processing.

## **3. Implementation Guide**
> Various message brokers can be implemented.  
> This guide provides implementation based on RabbitMQ.

## **3.1 RabbitMQ Implementation**
> For RabbitMQ, the destination field distinguishes between exchange and queue based on the presence of '@'.

1. **Dynamically Creating Exchange for Message Publishing**:
    - Uses destination containing '@' as Exchange.
    - Dynamically declares Exchange based on destination and publishes messages.

2. **Real-time Message Processing through Queue Subscription**:
    - Receives and processes messages in real-time using the **Pub/Sub pattern**.

3. **Pulling Messages from Queue**:
    - Method to retrieve messages one at a time from a specific destination (Queue).

## **3.2 KafkaMQ Implementation**
N/A 