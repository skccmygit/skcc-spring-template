package skcc.arch.app.http;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import skcc.arch.app.dto.ApiResponse;

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
    void GET_헤더포함하여_호출() throws Exception {
        //given
        String url = "https://jsonplaceholder.typicode.com/todos";
        Map<String, Object> params = Map.of("userId", 1);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsInVpZCI6InRlc3RAZ21haWwuY29tIiwicm9sZSI6IlVTRVIiLCJlbWFpbCI6InRlc3RAZ21haWwuY29tIiwidXNlcm5hbWUiOiLthYzsiqTtirgiLCJpYXQiOjE3Mzk1MDYxMzEsImV4cCI6MTczOTUwNzkzMX0.bt_XWKvYigydSj69RAKals6fhU9aaKdRbjTLZbDYgMI");

        //when
        ParameterizedTypeReference<List<Todo>> responseType = new ParameterizedTypeReference<>() {};
        List<Todo> list = httpClient.get(url, headers, params, responseType);

        //then
        assertNotNull(list);
        assertThat(list).hasSizeGreaterThan(0);
        assertThat(list.get(0).getUserId()).isEqualTo(1);

    }

    @Test
    void HttpOptions값_세팅하여_호출() throws Exception {
        //given
        String url = "http://localhost:8080/api/users";
        HttpOptions httpOptions = HttpOptions.builder().retryAttempts(3).timeout(3000).build();

        //when
        ParameterizedTypeReference<ApiResponse<Void>> responseType = new ParameterizedTypeReference<>() {};
        Object result = httpClient.get(url, null, responseType, httpOptions);

        //then
        assertNotNull(result);

    }

    @Test
    void HttpOptions값_에러_리턴() throws Exception {
        //given
        String url = "http://localhost:8080/api/users";

        //when
//        ParameterizedTypeReference<ApiResponse<Void>> responseType = new ParameterizedTypeReference<>() {};
        Object result = httpClient.get(url, null, ApiResponse.class);

        //then
        assertNotNull(result);

    }
    @Test
    void HttpOptions값_에러_리턴_2() throws Exception {
        //given
        String url = "http://localhost:8080/api/users";

        //when
        ParameterizedTypeReference<ApiResponse<Void>> responseType = new ParameterizedTypeReference<>() {};
        ApiResponse<Void> result = httpClient.get(url, null, responseType);

        //then
        assertNotNull(result);

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

    @Test
    void HttpOption_잘못된값으로_생성() throws Exception {
        //given
        //when
        //then
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            HttpOptions.builder().retryAttempts(0).timeout(300).build();
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            HttpOptions.builder().retryAttempts(-1).timeout(3000).build();
        });

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
