package skcc.arch.file.infrastructure.jpa;

import jakarta.persistence.*;
import lombok.*;
import skcc.arch.common.infrastructure.jpa.BaseEntity;
import skcc.arch.file.domain.FileModel;

@Getter
@Entity
@Table(name = "files")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class FileEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 100)
    private String orgName;

    @Column(length = 100)
    private String encName;

    @Column(nullable = false, length = 100)
    private String dirPath;

    @Column(nullable = false, length = 100)
    private long size;

    public static FileEntity from (FileModel model) {
        return FileEntity.builder()
                .id(model.getId())
                .orgName(model.getOrgName())
                .encName(model.getEncName())
                .dirPath(model.getDirPath())
                .size(model.getSize())
                .build();
    }

    public FileModel toModel() {
        return FileModel.builder()
                .id(this.id)
                .orgName(this.orgName)
                .encName(this.encName)
                .dirPath(this.dirPath)
                .size(this.size)
                .createdDate(super.getCreatedDate())
                .lastModifiedDate(super.getLastModifiedDate())
                .build();
    }
}
