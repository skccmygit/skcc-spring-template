package skcc.arch.app.file;

import lombok.Getter;
import org.springframework.core.io.Resource;

@Getter
public class DownloadFile {

    private String fileName;
    private String mimeType;
    private Resource resource;

    public DownloadFile(String fileName, String mimeType, Resource resource) {
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.resource = resource;
    }
}
