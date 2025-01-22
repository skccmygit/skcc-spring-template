package skcc.arch.infrastructure.config;

import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import skcc.arch.common.infrastructure.mybatis.AuditingInterceptor;

import javax.sql.DataSource;
import java.io.IOException;


@TestConfiguration
public class MyBatisTestConfig {
    @Bean
    public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource) throws IOException {

        Configuration mybatisConfiguration = new Configuration();
        mybatisConfiguration.setMapUnderscoreToCamelCase(true);
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setMapperLocations(new org.springframework.core.io.support.PathMatchingResourcePatternResolver().getResources("classpath:mybatis/mapper/*.xml"));
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setPlugins(new AuditingInterceptor());
        sqlSessionFactoryBean.setConfiguration(mybatisConfiguration);
        return sqlSessionFactoryBean;
    }

}
