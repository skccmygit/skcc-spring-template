package skcc.arch.biz.file.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@RequiredArgsConstructor
public final class FileModel {

    private final long id;
    private final String orgName;
    private final String encName;
    private final String dirPath;
    private final long size;
    private final LocalDateTime createdDate;
    private final LocalDateTime lastModifiedDate;

    public static FileModel from(FileCreate fileCreate) {

        return FileModel.builder()
                .orgName(fileCreate.getOrgName())
                .encName(fileCreate.getEncName())
                .dirPath(fileCreate.getDirPath())
                .size(fileCreate.getSize())
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();
    }
}
