package lanvander.framework.quartz.springboot.configuration;

import lanvander.base.configuration.SwaggerConfiguration;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;

@Configuration
public class QuartzSwaggerConfiguration extends SwaggerConfiguration {

    @Override
    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("动态定时任务接口文档")
                .contact(new Contact("lanvander", "", "1992286873@qq.com"))
                .description("SpringBoot整合Quartz实现动态定时任务")
                .termsOfServiceUrl("http://www.baidu.com")
                .version("v1.0")
                .build();
    }
}
