package skcc.arch.file.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import skcc.arch.file.domain.FileModel;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadResponse {

    private long id;
    private String encName;
    private String dirPath;
    private long size;

    public static FileUploadResponse from(FileModel fileModel) {
        return FileUploadResponse.builder()
                .id(fileModel.getId())
                .encName(fileModel.getEncName())
                .dirPath(fileModel.getDirPath())
                .size(fileModel.getSize())
                .build();
    }
}
