package skcc.arch.app.http;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
class HttpClientTest {

    @Autowired
    private HttpClient httpClient;

    @Test
    void GET_쿼리_없는_호출() throws Exception {
        //given
        String url = "https://jsonplaceholder.typicode.com/todos/1";

        //when
        Todo obj = httpClient.get(url, null, Todo.class);

        //then
        assertNotNull(obj);
        assertThat(obj.getUserId()).isEqualTo(1);
    }

    @Test
    void GET_쿼리파라미터_있는_호출() throws Exception {
        //given
        String url = "https://jsonplaceholder.typicode.com/todos";
        Map<String, Object> params = Map.of("userId", 1);

        //when
        ParameterizedTypeReference<List<Todo>> responseType = new ParameterizedTypeReference<>() {};
        List<Todo> list = httpClient.get(url, params, responseType);

        //then
        assertNotNull(list);
        assertThat(list).hasSizeGreaterThan(0);
        assertThat(list.get(0).getUserId()).isEqualTo(1);

    }


    @Test
    void POST_호출() throws Exception {
        //given
        Post requestData = Post.builder()
                .title("test")
                .body("test")
                .userId(1)
                .build();

        //when
        Post resultData = httpClient.post("https://jsonplaceholder.typicode.com/posts", requestData, Post.class);

        //then
        assertThat(resultData.getId()).isEqualTo(101L);
        assertThat(resultData.getTitle()).isEqualTo(requestData.getTitle());
        assertThat(resultData.getBody()).isEqualTo(requestData.getBody());
        assertThat(resultData.getUserId()).isEqualTo(requestData.getUserId());

    }

    @Data
    @Builder
    static class Todo {
        private int userId;
        private int id;
        private String title;
        private boolean completed;
    }

    @Data
    @Builder
    static class Post {
        private String title;
        private String body;
        private int userId;
        private long id;
    }
}
