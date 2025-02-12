package skcc.arch.file.controller.request;

public record FileDownloadRequest(
        long id,
        String filePath) {
}
