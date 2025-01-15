package skcc.arch.domain.common.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass // 공통 매핑 정보를 공유
@EntityListeners(AuditingEntityListener.class) // Auditing 기능 사용
@Getter
@NoArgsConstructor
public abstract class BaseEntity {

    @CreatedDate // 엔티티 생성 시 자동으로 저장
    @Column(updatable = false, nullable = false) // 업데이트 방지
    private LocalDateTime createdDate;

    @LastModifiedDate // 엔티티 업데이트 시 자동으로 저장
    @Column(nullable = false)
    private LocalDateTime lastModifiedDate;

}