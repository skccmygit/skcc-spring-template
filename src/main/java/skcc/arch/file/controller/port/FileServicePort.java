package skcc.arch.file.controller.port;

import org.springframework.web.multipart.MultipartFile;
import skcc.arch.file.controller.request.FileDownloadRequest;
import skcc.arch.file.controller.response.FileDownloadResponse;
import skcc.arch.file.domain.FileModel;

import java.io.IOException;
import java.util.List;

public interface FileServicePort {

    FileModel storeFile(MultipartFile multipartFile, String policyKey) throws IOException;
    List<FileModel> storeFiles(List<MultipartFile> multipartFiles, String policyKey) throws IOException;
    FileDownloadResponse getFileDownload(FileDownloadRequest request);
}
