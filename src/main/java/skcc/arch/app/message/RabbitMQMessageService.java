package skcc.arch.app.message;

import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * RabbitMQMessageService
 * <p>
 * RabbitMQ를 기반으로 메시지 발행 및 구독 기능을 제공하는 서비스
 * 메시지를 특정 큐로 발행하거나, 특정 큐에서 메시지를 구독하도록 설정
 */
@Service
@Slf4j
@ConditionalOnProperty(prefix = "spring.rabbitmq", name = "host")
public class RabbitMQMessageService implements MessageService {

    private final RabbitTemplate rabbitTemplate; // RabbitMQ의 메시지 발행, 수신을 처리하는 템플릿
    private final RabbitAdmin rabbitAdmin; // 교환기와 큐를 RabbitMQ에 선언하거나 관리하는 도구

    /**
     * RabbitMQMessageService 생성자
     *
     * @param rabbitTemplate RabbitTemplate 인스턴스
     */
    public RabbitMQMessageService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitAdmin = new RabbitAdmin(rabbitTemplate);
    }

    /**
     * 메시지 발행 메서드
     * <p>
     * 지정된 대상 큐 혹은 교환기로 메시지를 발행
     *
     * @param destination 발행할 대상 (교환기와 큐 정보를 포함 가능)
     * @param message     발행할 메시지
     */
    @Override
    public void publishMessage(String destination, String message) {
        String exchangeName = getExchangeName(destination); // 대상에서 교환기 이름 추출
        String queueName = getQueueName(destination); // 대상에서 큐 이름 추출

        if (exchangeName != null) {
            setupExchangeAndQueue(exchangeName, queueName); // 교환기와 큐 설정
            rabbitTemplate.convertAndSend(exchangeName, queueName, message); // 메시지를 교환기로 발행
        } else {
            declareQueueIfNotExists(queueName); // 큐가 존재하지 않을 경우 선언
            rabbitTemplate.convertAndSend(queueName, message); // 메시지를 큐로 발행
        }
        log.info("Publish message [{}][{}] : {} ", exchangeName, queueName, message);
    }

    /**
     * 메시지 실시간 구독 메서드
     * <p>
     * 특정 대상 큐의 메시지를 구독하도록 설정하고, 수신된 메시지에 대해 MessageHandler 실행
     *
     * @param destination 구독할 대상 (채널과 큐)
     * @param handler     수신된 메시지를 처리하기 위한 핸들러
     */
    @Override
    public void subscribeToMessages(String destination, MessageHandler handler) {
        subscribeKeepChannel(destination, handler);
    }

    /**
     * 메시지 가져오기 메서드
     * <p>
     * 지정된 큐에서 메시지를 가져옴 (pull 방식)
     *
     * @param destination 가져올 대상 큐
     * @return 큐에서 가져온 메시지 또는 null
     */
    @Override
    public String pullMessage(String destination) {
        String queueName = getQueueName(destination); // 큐 이름 추출
        GetResponse response = null;
        try (Connection connection = rabbitTemplate.getConnectionFactory().createConnection();
             Channel channel = connection.createChannel(true);) {
            response = channel.basicGet(queueName, true);
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
        return (response != null) ? new String(response.getBody()) : null;
    }

    /**
     * 교환기 이름 추출 메서드
     * <p>
     * 대상 문자열에서 교환기 이름을 추출
     *
     * @param destination 대상 문자열
     * @return 교환기 이름 또는 null
     */
    public String getExchangeName(String destination) {
        return (destination != null && destination.contains("@"))
                ? destination.split("@")[0]
                : null;
    }

    /**
     * 큐 이름 추출 메서드
     * <p>
     * 대상 문자열에서 큐 이름을 추출
     *
     * @param destination 대상 문자열
     * @return 큐 이름
     */
    public String getQueueName(String destination) {
        return destination.contains("@")
                ? destination.split("@")[1]
                : destination;
    }

    /**
     * 교환기와 큐를 설정하는 메서드
     * <p>
     * 지정된 교환기와 큐가 없을 경우 선언하고 바인딩
     *
     * @param exchangeName 교환기 이름
     * @param queueName    큐 이름
     */
    private void setupExchangeAndQueue(String exchangeName, String queueName) {
        declareExchangeIfNotExists(exchangeName); // 교환기가 존재하지 않을 경우 선언
        declareQueueIfNotExists(queueName); // 큐가 존재하지 않을 경우 선언
        bindQueueToExchange(exchangeName, queueName); // 큐를 교환기에 바인딩
    }

    /**
     * 교환기를 선언하는 메서드
     *
     * @param exchangeName 교환기 이름
     */
    private void declareExchangeIfNotExists(String exchangeName) {
        DirectExchange exchange = new DirectExchange(exchangeName, true, false); // 내구성과 자동 삭제 설정
        rabbitAdmin.declareExchange(exchange); // 교환기 선언
    }

    /**
     * 큐를 선언하는 메서드
     *
     * @param queueName 큐 이름
     */
    private void declareQueueIfNotExists(String queueName) {
        Queue queue = new Queue(queueName, true); // 내구성 설정
        rabbitAdmin.declareQueue(queue); // 큐 선언
    }

    /**
     * 큐와 교환기를 바인딩하는 메서드
     * <p>
     * 지정된 큐와 교환기를 라우팅 키로 바인딩
     *
     * @param exchangeName 교환기 이름
     * @param queueName    큐 이름
     */
    private void bindQueueToExchange(String exchangeName, String queueName) {
        Binding binding = BindingBuilder.bind(new Queue(queueName, true))
                .to(new DirectExchange(exchangeName))
                .with(queueName); // 라우팅 키 사용하여 바인딩
        rabbitAdmin.declareBinding(binding); // 바인딩 선언
    }

    /**
     * 채널을 유지하면서 메시지를 구독하는 메서드
     * <p>
     * 특정 큐에 메시지 수신을 등록하고, 메시지가 도착하면 핸들러를 실행
     *
     * @param queueName 구독할 큐 이름
     * @param handler   수신된 메시지를 처리할 핸들러
     */
    private void subscribeKeepChannel(String queueName, MessageHandler handler) {
        Connection connection = null;
        Channel channel = null;

        try {
            connection = rabbitTemplate.getConnectionFactory().createConnection();
            channel = connection.createChannel(true);
            channel.basicConsume(queueName, true, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body);
                    log.info("Received message from queue [{}]: {}", queueName, message);
                    handler.handleMessage(message); // 메시지 핸들러 실행
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("Failed to subscribe to the queue: " + queueName, e);
        } finally {
            if (channel != null) {
                connection.close();
            }
        }
    }
}
