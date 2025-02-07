package skcc.arch.app.file;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import skcc.arch.app.exception.CustomException;
import skcc.arch.app.exception.ErrorCode;
import skcc.arch.app.util.DateUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class FileService {

    private final UploadPolicies uploadPolicies;

    public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles, String policy) throws IOException {
        List<UploadFile> storeFileResult = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                storeFileResult.add(storeFile(multipartFile, policy));
            }
        }
        return storeFileResult;
    }

    public UploadFile storeFile(MultipartFile multipartFile, String policyKey) throws IOException
    {
        if (multipartFile.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        UploadPolicies.UploadPolicy policy = getPolicy(policyKey);
        validateFile(multipartFile, policy);

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);

        //경로 체크
        File file = new File(getFullPath(storeFileName, policyKey));
        if (!file.getParentFile().exists()) {
            if(!file.getParentFile().mkdirs()) {
                throw new RuntimeException("폴도 생성 실패");
            }
        }
        multipartFile.transferTo(file);

        // TODO - 만약 파일을 DB에 저장한다면
        return new UploadFile(originalFilename, storeFileName, policyKey);
    }

    public String getFullPath(String filename, String policyKey) {
        UploadPolicies.UploadPolicy policy = getPolicy(policyKey);
        return replaceUploadPath(policy.getUploadDir()) + "/" + filename;
    }

    public DownloadFile getDownloadFileByFilepath(String filePath) {

        Path path = Paths.get(filePath);
        InputStreamResource resource = null;
        try {
            resource = new InputStreamResource(Files.newInputStream(path));
        } catch (IOException e) {
            throw new CustomException(ErrorCode.NOT_FOUND_FILE);
        }
        String fileName = path.getFileName().toString();
        return new DownloadFile(fileName, resource);
    }

    private static String replaceUploadPath(String uploadPath) {
        // 날짜 치환
        Pattern pattern = Pattern.compile("(\\{date:)(\\w+)\\}");
        Matcher match = pattern.matcher(uploadPath);
        if (match.find()) {
            return uploadPath.replace(match.group(), DateUtil.getCurrent(match.group(2)));
        }
        return uploadPath;
    }

    private void validateFile(MultipartFile file, UploadPolicies.UploadPolicy policy) {
        // 1. 파일 크기 검증
        if (file.getSize() > policy.getMaxFileSize()) {
            throw new IllegalArgumentException("파일 크기가 최대 허용 크기를 초과했습니다. (" + policy.getMaxFileSize()/1024/1024 + " MB)");
        }

        // 2. 확장자 검증
        String extension = extractExt(file.getOriginalFilename());
        if (!Arrays.asList(policy.getAllowedExtensions()).contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("허용되지 않는 파일 형식입니다: " + extension);
        }
    }

    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    private UploadPolicies.UploadPolicy getPolicy(String policyKey) {
        UploadPolicies.UploadPolicy uploadPolicy = uploadPolicies.getUploadPolices().get(policyKey);
        return uploadPolicy;
    }
}
