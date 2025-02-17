package skcc.arch.biz.file.controller.request;

public record FileDownloadRequest(
        long id,
        String filePath) {
}
