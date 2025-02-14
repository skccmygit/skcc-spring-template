package skcc.arch.app.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Map;

@Slf4j
@Component
public class HttpClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public HttpClient(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    private WebClient.RequestBodySpec createRequestSpec(HttpMethod method, String url, HttpHeaders headers, Map<String, Object> queryParams) {

        if(method == HttpMethod.GET && queryParams != null) {
            url = buildUrl(url, queryParams);
        }

        final String finalUrl = url;
        WebClient.RequestBodySpec body = webClient
                .method(method)
                .uri(finalUrl)
                .headers(httpHeaders -> {
                    if (headers != null) {
                        httpHeaders.addAll(headers);
                    }
                });

        return body;
    }

    private void addBody(WebClient.RequestBodySpec requestSpec, HttpMethod method, Object body) throws JsonProcessingException {
        if (method != HttpMethod.GET && body != null) {
            requestSpec.contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(objectMapper.writeValueAsString(body));
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T request(HttpMethod method, String url, HttpHeaders headers, Map<String, Object> queryParams, Object body,
                         Object responseType, HttpOptions options) {
        try {
            WebClient.RequestBodySpec requestSpec = createRequestSpec(method, url, headers, queryParams);
            addBody(requestSpec, method, body);

            // `responseType` 처리
            if (responseType instanceof Class<?>) {
                // 단순 객체 타입 처리
                return requestSpec.retrieve()
                        .bodyToMono((Class<T>) responseType)
                        .retry(options.getRetryAttempts())
                        .timeout(Duration.ofMillis(options.getTimeout()))
                        .onErrorResume(WebClientResponseException.class, e -> handleError(e, responseType))
                        .block();
            } else if (responseType instanceof ParameterizedTypeReference<?>) {
                // 제네릭 타입 처리
                return requestSpec.retrieve()
                        .bodyToMono((ParameterizedTypeReference<T>) responseType)
                        .retry(options.getRetryAttempts())
                        .timeout(Duration.ofMillis(options.getTimeout()))
                        .onErrorResume(WebClientResponseException.class, e -> handleError(e, responseType))
                        .block();
            } else {
                throw new IllegalArgumentException("Unsupported response type. Must be Class<?> or ParameterizedTypeReference<?>.");
            }

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

    private <T> T executeRequest(HttpMethod method, String url, HttpHeaders headers,
                                 Map<String, Object> queryParams, Object body, Object responseType, HttpOptions options) {
        return request(method, url, headers, queryParams, body, responseType, options);
    }

    public <T> T get(String url, Map<String, Object> queryParams, Object responseType) {
        return executeRequest(HttpMethod.GET, url, null, queryParams, null, responseType, HttpOptions.defaultOptions());
    }

    public <T> T get(String url, Map<String, Object> queryParams, Object responseType, HttpOptions options) {
        return executeRequest(HttpMethod.GET, url, null, queryParams, null, responseType, options);
    }

    public <T> T get(String url, HttpHeaders headers, Map<String, Object> queryParams, Object responseType) {
        return executeRequest(HttpMethod.GET, url, headers, queryParams, null, responseType, HttpOptions.defaultOptions());
    }

    public <T> T get(String url, HttpHeaders headers, Map<String, Object> queryParams, Object responseType, HttpOptions options) {
        return executeRequest(HttpMethod.GET, url, headers, queryParams, null, responseType, options);
    }

    public <T> T post(String url, Object body, Object responseType) {
        return executeRequest(HttpMethod.POST, url, null, null, body, responseType, HttpOptions.defaultOptions());
    }

    public <T> T post(String url, Object body, Object responseType, HttpOptions options) {
        return executeRequest(HttpMethod.POST, url, null, null, body, responseType, options);
    }

    public <T> T post(String url, HttpHeaders headers, Object body, Object responseType, HttpOptions options) {
        return executeRequest(HttpMethod.POST, url, headers, null, body, responseType, options);
    }

    public <T> T put(String url, HttpHeaders headers, Map<String, Object> queryParams, Object body, Object responseType, HttpOptions options) {
        return executeRequest(HttpMethod.PUT, url, headers, queryParams, body, responseType, options);
    }

    public <T> T delete(String url, HttpHeaders headers, Map<String, Object> queryParams, Object body, Object responseType, HttpOptions options) {
        return executeRequest(HttpMethod.DELETE, url, headers, queryParams, body, responseType, options);
    }


    private static String buildUrl(String fullUrl, Map<String, Object> queryParams) {
        try {
            URL url = new URL(fullUrl);
            return UriComponentsBuilder
                    .fromPath(url.getPath())
                    .scheme(url.getProtocol())
                    .host(url.getHost())
                    .port(url.getPort())
                    .queryParams(convertToMultiValueMap(queryParams))
                    .build()
                    .toString();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private static org.springframework.util.MultiValueMap<String, String> convertToMultiValueMap(Map<String, Object> queryParams) {
        org.springframework.util.LinkedMultiValueMap<String, String> multiValueMap = new org.springframework.util.LinkedMultiValueMap<>();
        if (queryParams != null) {
            queryParams.forEach((key, value) -> multiValueMap.add(key, value.toString()));
        }
        return multiValueMap;
    }

    @SuppressWarnings("unchecked")
    private <T> Mono<T> handleError(WebClientResponseException e, Object responseType) {
        try {
            String responseBody = e.getResponseBodyAsString();
            if (responseType instanceof ParameterizedTypeReference<?>) {
                return Mono.just(objectMapper.readValue(responseBody, objectMapper.getTypeFactory().constructType(((ParameterizedTypeReference<?>) responseType).getType())));
            } else if (responseType instanceof Class<?>) {
                return Mono.just(objectMapper.readValue(responseBody, (Class<T>) responseType));
            }
        } catch (JsonProcessingException ex) {
            log.error("Failed to parse error response body: ", ex);
            return Mono.error(ex);
        }
        return Mono.error(new IllegalArgumentException("Unsupported response type during error handling."));
    }

}