package skcc.arch.app.exception;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;
import skcc.arch.app.config.MessageConfig;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = MessageConfig.class)
class CustomExceptionTest {


    @Test
    void ENUM타입으로_사용자정의_에러를_생성한다() throws Exception {

        //given & when
        LocaleContextHolder.setLocale(Locale.KOREAN);
        String msgKr = "요소가 존재하지 않습니다.";
        CustomException exception = new CustomException(ErrorCode.NOT_FOUND_ELEMENT);

        //then
        String exceptionMessage = exception.getMessage();
        assertEquals(msgKr, exceptionMessage);
        assertEquals(ErrorCode.NOT_FOUND_ELEMENT, exception.getErrorCode());

    }

    @Test
    void 영문_LOCALE_환경에서_메시지반환() throws Exception {
        //given
        String msgEn = "Element not found.";
        LocaleContextHolder.setLocale(Locale.ENGLISH);

        //when
        CustomException exception = new CustomException(ErrorCode.NOT_FOUND_ELEMENT);

        //then
        String exceptionMessage = exception.getMessage();
        assertEquals(msgEn, exceptionMessage);
        assertEquals(ErrorCode.NOT_FOUND_ELEMENT, exception.getErrorCode());

    }
    
    @Test
    void 동적메시지_파라미터_바인딩_정상확인() throws Exception {
        //given
        LocaleContextHolder.setLocale(Locale.KOREAN);
        String fieldMsg1 = "field1";
        String fieldMsg2 = "field2";
        String finalMsg = String.format("유효하지 않은 요청입니다. [필드: %s, 오류 메시지: %s]", fieldMsg1, fieldMsg2);

        //when
        CustomException exception = new CustomException(ErrorCode.INVALID_REQUEST, fieldMsg1, fieldMsg2);

        //then
        String exceptionMessage = exception.getMessage();
        assertEquals(finalMsg, exceptionMessage);
        assertEquals(ErrorCode.INVALID_REQUEST, exception.getErrorCode());

    }

}