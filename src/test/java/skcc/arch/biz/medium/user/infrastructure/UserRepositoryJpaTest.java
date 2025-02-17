package skcc.arch.biz.medium.user.infrastructure;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import skcc.arch.biz.user.infrastructure.jpa.UserEntity;
import skcc.arch.biz.user.infrastructure.jpa.UserRepositoryJpa;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@EnableJpaAuditing
@Sql("/sql/user-repository-test-data.sql")
class UserRepositoryJpaTest {

    @Autowired
    private UserRepositoryJpa userRepositoryJpa;

    @Test
    void findByEmail_로_유정정보를_찾을수_있다() throws Exception {
        //given
        String email = "test1@sk.com";
        //when
        Optional<UserEntity> result = userRepositoryJpa.findByEmail(email);
    
        //then
        assertThat(result.isPresent()).isTrue();
    }
    
    @Test
    void findAllUser_로_모든_유저정보를_찾을수_있다() throws Exception {
        //when
        Iterable<UserEntity> result = userRepositoryJpa.findAll();
    
        //then
        assertThat(result).isNotNull();
        assertThat(result).hasSizeGreaterThan(0);
    }
    
    
}