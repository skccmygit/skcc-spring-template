package skcc.arch.app.http;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class HttpClientTest {

    @Autowired
    private HttpClient httpClient;

    @Test
    void GET_메서드_호출() throws Exception {
        //given
        String url = "https://jsonplaceholder.typicode.com/todos/1";

        //when
        Map result = httpClient.get(url, null, Map.class);

        //then
        log.info(result.toString());

    }
}