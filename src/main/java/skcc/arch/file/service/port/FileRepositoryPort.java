package skcc.arch.file.service.port;

import skcc.arch.file.domain.FileModel;

public interface FileRepositoryPort {
    FileModel save(FileModel fileModel);
    FileModel findById(Long id);
}
