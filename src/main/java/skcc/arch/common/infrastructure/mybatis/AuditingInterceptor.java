package skcc.arch.common.infrastructure.mybatis;


import com.google.common.base.CaseFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

// MyBatis 업데이트 및 삽입 작업을 가로채어 감사 데이터를 자동으로 설정하는 인터셉터
@Slf4j
@Component
@Intercepts({@Signature(type = Executor.class, method = "update" , args = {MappedStatement.class, Object.class})})
public class AuditingInterceptor implements Interceptor {


    // MyBatis 인터셉터의 핵심 메소드로, 삽입 및 업데이트 작업을 가로채어 필요한 감사 데이터를 설정한다.
    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object obj = invocation.getArgs()[1];

        // SQL 명령어 유형이 INSERT인 경우, 생성일(created_date)과 마지막 수정일(last_modified_date)을 설정한다.
        if (mappedStatement.getSqlCommandType()  == SqlCommandType.INSERT) {
            setAuditData(obj, "created_date");
            setAuditData(obj, "last_modified_date");
        // SQL 명령어 유형이 UPDATE인 경우, 마지막 수정일(last_modified_date)만 설정한다.    
        } else if (mappedStatement.getSqlCommandType()  == SqlCommandType.UPDATE) {
            setAuditData(obj, "last_modified_date");
        }

        return invocation.proceed();
    }

    // 주어진 객체(obj)의 특정 필드(예: created_date 또는 last_modified_date)에 현재 시각을 설정하는 유틸리티 메소드
    private static void setAuditData(Object obj, String filedName) throws IllegalAccessException {
        Field field = null;
        try {
            // 필드 이름(filedName)에 해당하는 선언된 필드를 찾는다.
            field = obj.getClass().getDeclaredField(filedName);
            field.setAccessible(true); // 필드에 접근할 수 있도록 설정
            field.set(obj, LocalDateTime.now()); // 선택한 필드에 현재 시간을 설정
        } catch (NoSuchFieldException e) {
            try {
                // 만약 필드를 찾지 못했다면, 'LOWER_UNDERSCORE'에서 'LOWER_CAMEL' 형식으로 필드 이름 변경 후 재시도
                field = obj.getClass().getDeclaredField(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, filedName));
            } catch (NoSuchFieldException ex) {
                // 여전히 필드가 없을 경우 경고 로그 출력
                log.warn("No such field : {}", filedName);
            }
        }
        if(field != null) {
            field.setAccessible(true);
            field.set(obj, LocalDateTime.now());
        }
    }


}
