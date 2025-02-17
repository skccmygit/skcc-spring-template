package skcc.arch.app.http;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class UriBuilderTest {
    public static void main(String[] args) {
        // 테스트할 URL
        String url = "https://jsonplaceholder.typicode.com/todos";

        // 쿼리 파라미터 생성 (예시 데이터)
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("userId", 1);
        queryParams.put("completed", true);

        // URI 빌더 테스트
        String buildUr = buildUrl(url, queryParams);
        // 결과 출력
        System.out.println("Generated URI: " + buildUr);
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
}
