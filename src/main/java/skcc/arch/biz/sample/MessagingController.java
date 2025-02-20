package skcc.arch.biz.sample;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import skcc.arch.app.message.MessageService;

import java.util.Map;

@RestController
@RequestMapping("/api/messaging")
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(prefix = "spring.rabbitmq", name = "host")
public class MessagingController {

    private final MessageService messageService;

    @PostMapping("/pub")
    public String pub(@RequestBody Map<String, String> param) {
        messageService.publishMessage(param.get("destination"), param.get("message"));
        return "ok";
    }

    @PostMapping("/pull")
    public String subSync(@RequestBody Map<String, String> param) {
        String message = messageService.pullMessage(param.get("destination"));
        return message!=null ? "Received message: " + message : "No new messages available.";
    }


    @PostMapping("/sub")
    public String subAsync(@RequestBody Map<String, String> param) throws InterruptedException {

        try {
            messageService.subscribeToMessages(param.get("destination"), message -> {
                log.info("Received message: {}", message);
            });
            return "Subscribing successful.";
        } catch (Exception e) {
            return "Subscribing failed.";
        }

    }

}
