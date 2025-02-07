package skcc.arch.app.file;

import lombok.Getter;
import org.springframework.core.io.Resource;

@Getter
public class DownloadFile {

    private String fileName;
    private Resource resource;

    public DownloadFile(String fileName, Resource resource) {
        this.fileName = fileName;
        this.resource = resource;
    }
}
