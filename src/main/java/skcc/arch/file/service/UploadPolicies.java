package skcc.arch.file.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "file")
@Slf4j
public class UploadPolicies {

    private Map<String, UploadPolicy> uploadPolices; // Key: 정책 이름, Value: 특정 정책

    @Getter
    @Setter
    public static class UploadPolicy {
        private long maxFileSize; // 파일 크기 제한
        private String uploadDir; // 업로드 디렉토리
        private String[] allowedExtensions; // 허용 확장자
        private boolean saveDb;
    }

}