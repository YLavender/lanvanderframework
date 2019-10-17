package lanvander.framework.quartz.springboot.configuration;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfiguration {

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(apiInfo())
        .pathMapping("/")
        .select() // 选择那些路径和api会生成document
        .apis(RequestHandlerSelectors.any()) // 对所有api进行监控
        // 不显示错误的接口地址
        .paths(Predicates.not(PathSelectors.regex("/error.*"))) // 错误路径不监控
        .paths(PathSelectors.regex("/.*")) // 对根下所有路径进行监控
        .build();
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title("动态定时任务接口文档")
        .contact(new Contact("lanvander", "", "1992286873@qq.com"))
        .description("SpringBoot整合Quartz实现动态定时任务")
        .termsOfServiceUrl("http://www.baidu.com")
        .version("v1.0")
        .build();
  }
}
