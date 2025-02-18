package skcc.arch.biz.file.domain;

import org.springframework.core.io.Resource;

public record FileDownload(
        String fileName,
        Resource resource
){
}
