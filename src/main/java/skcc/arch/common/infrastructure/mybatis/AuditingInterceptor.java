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

@Slf4j
@Component
@Intercepts({@Signature(type = Executor.class, method = "update" , args = {MappedStatement.class, Object.class})})
public class AuditingInterceptor implements Interceptor {


    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object obj = invocation.getArgs()[1];

        if (mappedStatement.getSqlCommandType()  == SqlCommandType.INSERT) {
            setAuditData(obj, "created_date");
            setAuditData(obj, "last_modified_date");
        } else if (mappedStatement.getSqlCommandType()  == SqlCommandType.UPDATE) {
            setAuditData(obj, "last_modified_date");
        }

        return invocation.proceed();
    }

    private static void setAuditData(Object obj, String filedName) throws IllegalAccessException {
        Field field = null;
        try {
            field = obj.getClass().getDeclaredField(filedName);
            field.setAccessible(true);
            field.set(obj, LocalDateTime.now());
        } catch (NoSuchFieldException e) {
            try {
                field = obj.getClass().getDeclaredField(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, filedName));
            } catch (NoSuchFieldException ex) {
                log.warn("No such field : {}", filedName);
            }
        }
        if(field != null) {
            field.setAccessible(true);
            field.set(obj, LocalDateTime.now());
        }
    }


}
