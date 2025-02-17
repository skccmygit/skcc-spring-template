package skcc.arch.biz.log.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class LogRepository {

    public void logTest(int number) {
        log.debug("Repository 로그 시작");
        log.debug("Repository 로그 입니다 : {}", number);
        log.debug("Repository 로그 종료");

    }
}
