package skcc.arch.biz.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    // 기본 날짜 패턴
    private static final String DEFAULT_DATE_PATTERN = "yyyyMMdd";
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN);

    /**
     * 오늘 날짜를 기본 패턴(yyyyMMdd)으로 반환
     * @return 오늘 날짜 yyyyMMdd 형식의 String
     */
    public static String getToday() {
        return LocalDate.now().format(DEFAULT_FORMATTER);
    }

    /**
     * 오늘 날짜를 특정 포맷으로 반환
     * @param pattern 원하는 날짜 포맷 (예: yyyy-MM-dd)
     * @return 포맷팅된 오늘 날짜
     */
    public static String getToday(String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.now().format(formatter);
    }

    public static String getCurrent(String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.now().format(formatter);
    }

    /**
     * 현재 날짜와 시간을 기본 패턴(yyyyMMdd HH:mm:ss)으로 반환
     * @return 현재 날짜와 시간 (yyyyMMdd HH:mm:ss)
     */
    public static String getCurrentDateTime() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss");
        return LocalDateTime.now().format(dateTimeFormatter);
    }

    /**
     * 특정 LocalDate 객체를 지정한 포맷으로 변환
     * @param date LocalDate 객체
     * @param pattern 포맷 문자열 (예: yyyy-MM-dd)
     * @return 포맷된 날짜 문자열
     */
    public static String formatDate(LocalDate date, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return date.format(formatter);
    }
}