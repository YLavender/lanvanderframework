package lanvander.framework.elasticjob.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@ComponentScan({"lanvander.base", "lanvander.framework"})
@EnableSwagger2
public class SpringBootElasticJobApplication {
  public static void main(String[] args) {
    SpringApplication.run(SpringBootElasticJobApplication.class, args);
  }
}
