package skcc.arch.infrastructure;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import skcc.arch.infrastructure.config.MyBatisTestConfig;
import skcc.arch.user.infrastructure.mybatis.UserDto;
import skcc.arch.user.infrastructure.mybatis.UserRepositoryMybatis;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Sql(scripts = {"/sql/user-mybatis-ddl.sql","/sql/user-repository-test-data.sql"}
        , executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@MybatisTest
@ContextConfiguration(classes = MyBatisTestConfig.class)
class UserRepositoryMybatisTest {

    @Autowired
    private UserRepositoryMybatis userRepository;

    @Test
    void findById_아이디로_유저정보를_찾을수_있다() throws Exception {
        //given
        Long id = 1L;

        //when
        Optional<UserDto> result = userRepository.findById(id);

        //then
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getId()).isEqualTo(id);

    }


    @Test
    void findByEmail_로_유정정보를_찾을수_있다() throws Exception {
        //given
        String email = "test1@sk.com";
        //when
        Optional<UserDto> result = userRepository.findByEmail(email);

        //then
        assertThat(result.isPresent()).isTrue();
    }

    @Test
    void findAll_로_모든_유저정보를_찾을수_있다() throws Exception {
        //when
        Iterable<UserDto> result = userRepository.findAll();

        //then
        assertThat(result).isNotNull();
        assertThat(result).hasSizeGreaterThan(0);
    }

    @Test
    void save_유저정보를_생성한다() throws Exception {
        //given
        UserDto userDto = UserDto.builder()
                .username("<USERNAME>")
                .password("<PASSWORD>")
                .email("<EMAIL>")
                .build();
        //when
        Long savedUserId = userRepository.save(userDto);

//        then
        assertThat(savedUserId).isEqualTo(1L);

    }


}