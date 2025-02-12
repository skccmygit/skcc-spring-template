package skcc.arch.file.controller.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class FileDownloadRequest {
    private final long id;
    private final String filePath;
}
