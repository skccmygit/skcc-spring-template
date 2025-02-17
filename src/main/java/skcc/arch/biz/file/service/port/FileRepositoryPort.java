package skcc.arch.biz.file.service.port;

import skcc.arch.biz.file.domain.FileModel;

public interface FileRepositoryPort {
    FileModel save(FileModel fileModel);
    FileModel findById(Long id);
}
