package skcc.arch.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageInfo {
    private final int currentPage;     // 현재 페이지 번호(1부터 시작)
    private final int totalPages;      // 총 페이지 수
    private final long totalElements;  // 총 데이터 개수
    private final int pageSize;        // 페이지당 데이터 수
    private final boolean isFirst;     // 첫 번째 페이지 여부
    private final boolean isLast;      // 마지막 페이지 여부

    // 공통적으로 Page 객체로부터 PageInfo를 생성하는 유틸리티 메서드 추가
    public static PageInfo fromPage(org.springframework.data.domain.Page<?> page) {
        return new PageInfo(
                page.getNumber() + 1,     // 현재 페이지 번호(0부터 시작하므로 +1)
                page.getTotalPages(),     // 총 페이지 수
                page.getTotalElements(),  // 총 데이터 개수
                page.getSize(),           // 페이지당 데이터 수
                page.isFirst(),           // 첫 번째 페이지 여부
                page.isLast()             // 마지막 페이지 여부
        );
    }
}