package lanvander.framework.quartz.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@ComponentScan(basePackages = {"lanvander.base", "lanvander.framework"})
@EnableSwagger2
public class QuartzSpringBootApplication {
  public static void main(String[] args) {
    SpringApplication.run(QuartzSpringBootApplication.class, args);
  }
}
