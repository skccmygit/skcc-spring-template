package skcc.arch.app.http;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HttpOptions {
    private int retryAttempts = 0;
    private long timeout = 60000;

    public static HttpOptions defaultOptions() {
        return HttpOptions.builder()
                .retryAttempts(0)
                .timeout(60000) //기본 60초
                .build();
    }
}
