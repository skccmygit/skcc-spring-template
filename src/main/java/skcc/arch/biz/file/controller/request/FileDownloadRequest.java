package skcc.arch.biz.file.controller.request;

import skcc.arch.biz.file.domain.FileModel;

public record FileDownloadRequest(
        long id,
        String filePath) {

    public FileModel toModel() {

        if (this.id <= 0) {
            return FileModel.builder()
                    .dirPath(getDirectoryPath(this.filePath))
                    .orgName(getFileName(this.filePath))
                    .build();
        }

        return FileModel.builder()
                .id(this.id)
                .build();
    }

    private String getFileName(String filePath) {
        // 마지막 슬래시(역슬래시 또는 슬래시) 위치 확인
        int lastSeparatorIndex = getLastSeparatorIndex(filePath);
        return (lastSeparatorIndex >= 0) ? filePath.substring(lastSeparatorIndex + 1) : filePath;
    }

    private String getDirectoryPath(String filePath) {
        // 마지막 슬래시(역슬래시 또는 슬래시) 위치 확인
        int lastSeparatorIndex = getLastSeparatorIndex(filePath);
        return (lastSeparatorIndex >= 0) ? filePath.substring(0, lastSeparatorIndex) : "";
    }

    private int getLastSeparatorIndex(String filePath) {
        int lastSeparatorIndex = filePath.lastIndexOf('/'); // UNIX 계열
        if (lastSeparatorIndex == -1) {
            lastSeparatorIndex = filePath.lastIndexOf('\\'); // Windows 계열
        }
        return lastSeparatorIndex;
    }


}
