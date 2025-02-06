package skcc.arch.app.file;

import lombok.Data;

@Data
public class UploadFile {

    private String uploadFileName;
    private String storeFileName;
    private String policy;

    public UploadFile(String uploadFileName, String storeFileName) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
    }

    public UploadFile(String uploadFileName, String storeFileName, String policy) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
        this.policy = policy;
    }
}
