package skcc.arch.app.http;

import lombok.Builder;
import lombok.Data;

@Data
public class HttpOptions {
    private int retryAttempts;
    private long timeout;

    @Builder
    public HttpOptions(int retryAttempts, long timeout) {
        this.retryAttempts = retryAttempts;
        this.timeout = timeout;
        validate();
    }

    public HttpOptions() {
        this.retryAttempts = 0;
        this.timeout = 60000;
    }

    public static HttpOptions defaultOptions() {
        return new HttpOptions();
    }

    private void validate() {
        if (this.retryAttempts < 0) {
            throw new IllegalArgumentException("retryAttempts must be greater than or equal to zero");
        }

        if (this.timeout < 500) {
            throw new IllegalArgumentException("timeout must be greater than or equal to 500");
        }
    }
}
