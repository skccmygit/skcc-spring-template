package skcc.arch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ThymeleafSpringApplication {


    public static void main(String[] args) {
        SpringApplication.run(ThymeleafSpringApplication.class, args);
    }

}
