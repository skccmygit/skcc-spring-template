package skcc.arch.app.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.net.URI;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class HttpClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public HttpClient(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    private WebClient.RequestBodySpec createRequestSpec(HttpMethod method, String url, Map<String, String> headers, Map<String, Object> queryParams) {
        return webClient
                .method(method)
                .uri(uri -> {
                    if (queryParams != null) {
                        queryParams.forEach(uri::queryParam);
                    }
                    return URI.create(url);
                })
                .headers(httpHeaders -> Optional.ofNullable(headers).ifPresent(httpHeaders::setAll));
    }

    private void addBody(WebClient.RequestBodySpec requestSpec, HttpMethod method, Object body) throws JsonProcessingException {
        if (method != HttpMethod.GET && body != null) {
            requestSpec.contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(objectMapper.writeValueAsString(body));
        }
    }

    public <T> T request(HttpMethod method, String url, Map<String, String> headers, Map<String, Object> queryParams, Object body,
                         Class<T> responseType, HttpOptions options) {
        try {
            WebClient.RequestBodySpec requestSpec = createRequestSpec(method, url, headers, queryParams);
            addBody(requestSpec, method, body);

            return requestSpec.retrieve()
                    .bodyToMono(responseType)
                    .retry(options.getRetryAttempts())
                    .timeout(Duration.ofMillis(options.getTimeout()))
                    .block();
        } catch (WebClientResponseException e) {
            log.error("HTTP Request failed: {} - {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw e;
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize request body: ", e);
            throw new RuntimeException("Failed to serialize request body", e);
        } catch (Exception e) {
            log.error("HTTP Request failed: ", e);
            throw new RuntimeException("HTTP Request failed", e);
        }
    }

    private <T> T executeRequest(HttpMethod method, String url, Map<String, String> headers,
                                 Map<String, Object> queryParams, Object body, Class<T> responseType, HttpOptions options) {
        return request(method, url, headers, queryParams, body, responseType, options);
    }

    public <T> T get(String url, Map<String, Object> queryParams, Class<T> responseType) {
        return executeRequest(HttpMethod.GET, url, null, queryParams, null, responseType, HttpOptions.defaultOptions());
    }

    public <T> T get(String url, Map<String, String> headers, Map<String, Object> queryParams, Class<T> responseType) {
        return executeRequest(HttpMethod.GET, url, headers, queryParams, null, responseType, HttpOptions.defaultOptions());
    }

    public <T> T get(String url, Map<String, String> headers, Map<String, Object> queryParams, Class<T> responseType, HttpOptions options) {
        return executeRequest(HttpMethod.GET, url, headers, queryParams, null, responseType, options);
    }

    public <T> T post(String url, Object body, Class<T> responseType) {
        return executeRequest(HttpMethod.POST, url, null, null, body, responseType, HttpOptions.defaultOptions());
    }

    public <T> T post(String url, Object body, Class<T> responseType, HttpOptions options) {
        return executeRequest(HttpMethod.POST, url, null, null, body, responseType, options);
    }

    public <T> T post(String url, Map<String, String> headers, Object body, Class<T> responseType, HttpOptions options) {
        return executeRequest(HttpMethod.POST, url, headers, null, body, responseType, options);
    }

    public <T> T put(String url, Map<String, String> headers, Map<String, Object> queryParams, Object body, Class<T> responseType, HttpOptions options) {
        return executeRequest(HttpMethod.PUT, url, headers, queryParams, body, responseType, options);
    }

    public <T> T delete(String url, Map<String, String> headers, Map<String, Object> queryParams, Object body, Class<T> responseType, HttpOptions options) {
        return executeRequest(HttpMethod.DELETE, url, headers, queryParams, body, responseType, options);
    }

}