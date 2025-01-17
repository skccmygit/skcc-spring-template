package skcc.arch.app.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;


@Component
public class PageArgumentResolver extends org.springframework.data.web.PageableHandlerMethodArgumentResolver {

    @Override
    public Pageable resolveArgument(MethodParameter methodParameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        // 파라미터값(page)이 없을 경우 default 값을 1로 설정
        String pageParam = webRequest.getParameter("page");
        int page = (pageParam != null ? Integer.parseInt(pageParam) : 1); // 기본값 1

        // Spring은 0부터 시작하므로 내부적으로 -1 , 0으로 할경우에는그냥 0으로
        if (page > 0) page -= 1;

        // size 파라미터 처리
        String sizeParam = webRequest.getParameter("size");
        int size = (sizeParam != null ? Integer.parseInt(sizeParam) : 10); // 기본값 10

        return PageRequest.of(page, size);
    }
}
