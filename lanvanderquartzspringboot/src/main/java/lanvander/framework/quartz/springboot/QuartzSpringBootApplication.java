package lanvander.framework.quartz.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
@ComponentScan(basePackages = {"lanvander.base", "lanvander.framework"})
public class QuartzSpringBootApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuartzSpringBootApplication.class, args);
    }
}
