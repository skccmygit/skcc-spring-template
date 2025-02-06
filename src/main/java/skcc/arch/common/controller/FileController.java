package skcc.arch.common.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import skcc.arch.app.dto.ApiResponse;
import skcc.arch.app.dto.ExceptionDto;
import skcc.arch.app.file.FileService;
import skcc.arch.app.file.UploadFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileController {
    private final FileService fileService;

    /**
     * 단일 파일 업로드 API
     * @param file 업로드할 파일
     * @param policyKey 업로드 정책 키 (예: "imagePolicy")
     * @return 업로드된 파일 정보
     */
    @PostMapping("/upload")
    public ApiResponse<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("policyKey") String policyKey) {

        UploadFile storeFile = null;
        try {
            storeFile = fileService.storeFile(file, policyKey);
        } catch (IOException e) {
            return ApiResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionDto.builder().message("파일 저장 중 오류가 발생 하였습니다.").build() );
        }
        return ApiResponse.ok(storeFile);
    }

    /**
     * 다중 파일 업로드 API
     * @param files 업로드할 파일 리스트
     * @param policyKey 업로드 정책 키 (예: "documentPolicy")
     * @return 업로드된 파일들의 정보
     */
    @PostMapping("/upload/multiple")
    public ApiResponse<?> uploadMultipleFiles(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("policyKey") String policyKey) {

        List<UploadFile> storedFiles = null;
        try {
            storedFiles = fileService.storeFiles(files, policyKey);
        } catch (IOException e) {
            return ApiResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionDto.builder().message("파일 저장 중 오류가 발생 하였습니다.").build() );
        }
        return ApiResponse.ok(storedFiles);
    }

}
