package skcc.arch.user.infrastructure.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserRepositoryMybatis {

    Optional<UserDto> findById(Long id);
    Optional<UserDto> findByEmail(String email);
    Long save(UserDto user);
    List<UserDto> findAll();
    Page<UserDto> findAll(Pageable pageable);
    List<UserDto> findAllWithPageable(@Param("offset") long offset, @Param("pageSize") int pageSize);
    long countAll();
}
