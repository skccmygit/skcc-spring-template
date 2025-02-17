package skcc.arch.biz.file.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import skcc.arch.biz.util.DateUtil;
import skcc.arch.biz.file.service.UploadPolicies;

import java.util.Arrays;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Builder
@Getter
@RequiredArgsConstructor
public final class FileCreate {

    private final String orgName;
    private final String encName;
    private final String dirPath;
    private final long size;

    public static FileCreate from(MultipartFile multipartFile, UploadPolicies.UploadPolicy policy) {

        // 파일 유효성 검증
        validateFileByPolicy(multipartFile, policy);
        String originalFilename = multipartFile.getOriginalFilename();
        // 저장할 파일명
        String storeFileName = generateUniqueFileName(originalFilename);
        // 업로드 경로
        String uploadDir = replaceUploadDir(policy.getUploadDir());
        // 파일 사이즈
        long fileSize = multipartFile.getSize();

        return FileCreate.builder()
                .orgName(originalFilename)
                .encName(storeFileName)
                .size(fileSize)
                .dirPath(uploadDir)
                .build();
    }

    /**
     * 정책에 정규표현식을 변환한다
     */
    private static String replaceUploadDir(String uploadPath) {
        // 날짜 치환
        Pattern pattern = Pattern.compile("(\\{date:)(\\w+)\\}");
        Matcher match = pattern.matcher(uploadPath);
        if (match.find()) {
            return uploadPath.replace(match.group(), DateUtil.getCurrent(match.group(2)));
        }
        return uploadPath;
    }

    /**
     * 업로드 파일에 대하여 정책별 유효성 검증
     * 1. 파일크기
     * 2. 확장자
     */
    private static void validateFileByPolicy(MultipartFile file, UploadPolicies.UploadPolicy policy) {

        // 1. 파일 크기 검증
        if (file.getSize() > policy.getMaxFileSize()) {
            throw new IllegalArgumentException("파일 크기가 최대 허용 크기를 초과했습니다. (" + policy.getMaxFileSize() / 1024 / 1024 + " MB)");
        }

        // 2. 확장자 검증
        String extension = extractExt(file.getOriginalFilename());
        if (!Arrays.asList(policy.getAllowedExtensions()).contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("허용되지 않는 파일 형식입니다: " + extension);
        }
    }

    private static String generateUniqueFileName(String originalFilename) {
        String uuid = UUID.randomUUID().toString();
        String ext = extractExt(originalFilename);
        return uuid + "." + ext;
    }

    private static String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

}
