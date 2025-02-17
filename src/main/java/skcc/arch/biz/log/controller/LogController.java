package skcc.arch.biz.log.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import skcc.arch.biz.log.service.LogService;

@RestController
@RequestMapping("/api/log")
@Slf4j
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @GetMapping("/1")
    public void getLog1() {
        String msg = "1번 로그입니당";
        log.debug("컨트롤러 호출 시작 : {}", msg);
        logService.logTest();
        log.debug("컨트롤러 호출 종료");
    }

    @GetMapping("/2")
    public void getLog2() {
        String msg = "2번 로그입니당";
        log.debug("컨트롤러 호출 시작 : {}", msg);
        logService.logTest2();
        log.debug("컨트롤러 호출 종료");
    }
}
