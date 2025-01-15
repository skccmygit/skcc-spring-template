package skcc.arch.domain.user.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import skcc.arch.domain.user.infrastructure.jpa.UserEntity;
import skcc.arch.domain.user.infrastructure.jpa.UserJpaRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@EnableJpaAuditing
@Sql("/sql/user-repository-test-data.sql")
class UserJpaRepositoryTest {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Test
    void findByEmail_로_유정정보를_찾을수_있다() throws Exception {
        //given
        String email = "bh.moon@sk.com";
        //when
        Optional<UserEntity> result = userJpaRepository.findByEmail(email);

        //then
        assertThat(result.isPresent()).isTrue();

    }
}