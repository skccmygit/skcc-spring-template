package skcc.arch.app.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import skcc.arch.common.infrastructure.mybatis.AuditingInterceptor;

import javax.sql.DataSource;

//@Configuration
public class MyBatisConfig {

//    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setMapperLocations(new org.springframework.core.io.support.PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
        factoryBean.setConfigLocation(new org.springframework.core.io.ClassPathResource("mybatis/mybatis-config.xml")); // XML 설정 파일 경로
        factoryBean.setPlugins(new AuditingInterceptor());
        return factoryBean.getObject();
    }
}