package skcc.arch.log.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import skcc.arch.log.infrastructure.LogRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogService {

    private final LogRepository logRepository;

    public void logTest() {
        log.debug("Service 로그 호출 시작 ");
        logRepository.logTest(100);
        log.debug("Repository 재호출 ");
        logRepository.logTest(100);
        log.debug("Service 로그 호출 종료 ");
    }

    public void logTest2() {
        log.debug("Service 로그 호출 시작 ");
        logRepository.logTest(200);
        log.debug("Service 로그 호출 종료 ");
    }
}
