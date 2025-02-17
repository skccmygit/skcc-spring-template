package skcc.arch.biz.file.controller.response;

import org.springframework.core.io.Resource;

public record FileDownloadResponse(
        String fileName,
        Resource resource
){
}
