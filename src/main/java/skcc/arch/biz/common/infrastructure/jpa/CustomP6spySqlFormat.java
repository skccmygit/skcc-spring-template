package skcc.arch.biz.common.infrastructure.jpa;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.hibernate.engine.jdbc.internal.FormatStyle;

import java.util.Locale;
import java.util.Stack;

public class CustomP6spySqlFormat implements MessageFormattingStrategy {

    // 표기에 허용되는 filter
    private String ALLOW_FILTER = "skcc.arch";

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {

        sql = formatSql(category, sql);
//        return sql + createStack(connectionId, elapsed);
        return sql + ";\nExecution Time: " + elapsed + " ms";
    }

    private String formatSql(String category,String sql) {
        if(sql ==null || sql.trim().isEmpty()) return sql;

        // Only format Statement, distinguish DDL And DML
        if (Category.STATEMENT.getName().equals(category)) {
            String tmpsql = sql.trim().toLowerCase(Locale.ROOT);
            if(tmpsql.startsWith("create") || tmpsql.startsWith("alter") || tmpsql.startsWith("comment")) {
                sql = FormatStyle.DDL.getFormatter().format(sql);
            }else {
                sql = FormatStyle.BASIC.getFormatter().format(sql);
            }
        }
        return sql;
    }

    // stack 콘솔 표기
    private String createStack(int connectionId, long elapsed) {
        Stack<String> callStack = new Stack<>();
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();

        for (StackTraceElement stackTraceElement : stackTrace) {
            String trace = stackTraceElement.toString();

            // trace 항목을 보고 내게 맞는 것만 필터
            if(trace.startsWith(ALLOW_FILTER)) {
                callStack.push(trace);
            }
        }

        StringBuffer sb = new StringBuffer();
        int order = 1;
        while (!callStack.isEmpty()) {
            sb.append("\n\t\t").append(order++).append(".").append(callStack.pop());
        }

        return "\n\n\tConnection ID:" + connectionId +
                " | Excution Time:" + elapsed + " ms\n" +
                "\n\tExcution Time:" + elapsed + " ms\n" +
                "\n\tCall Stack :" + sb + "\n" +
                "\n--------------------------------------";
    }
}
