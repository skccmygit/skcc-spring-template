package skcc.arch.app.http;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HttpOptions {
    private int retryAttempts;
    private long timeout;

    public static HttpOptions defaultOptions() {
        return HttpOptions.builder()
                .retryAttempts(0)
                .timeout(3000)
                .build();
    }
}
