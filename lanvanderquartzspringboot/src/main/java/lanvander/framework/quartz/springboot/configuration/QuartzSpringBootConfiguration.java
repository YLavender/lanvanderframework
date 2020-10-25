package lanvander.framework.quartz.springboot.configuration;

import lanvander.framework.quartz.springboot.factory.QuartzSpringBootJobBeanFactory;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Properties;

@Configuration
@Slf4j
public class QuartzSpringBootConfiguration {

    @Resource
    private QuartzSpringBootJobBeanFactory quartzSpringBootJobBeanFactory;

    @Bean(name = "schedulerFactory")
    public SchedulerFactoryBean createSchedulerFactoryBean() {
        SchedulerFactoryBean bean = new SchedulerFactoryBean();
        bean.setJobFactory(quartzSpringBootJobBeanFactory);
        bean.setQuartzProperties(readQuartzProperties());
        return bean;
    }

    @Bean
    public Properties readQuartzProperties() {
        Properties properties = new Properties();
        PropertiesFactoryBean bean = new PropertiesFactoryBean();
        bean.setLocation(new ClassPathResource("/application.yml"));
        try {
            bean.afterPropertiesSet();
            properties = bean.getObject();
        } catch (IOException ex) {
            log.error("获取Quartz配置异常, 异常原因: {}, 异常信息: {}", ex.getCause(), ex.getMessage());
        }
        return properties;
    }

    @Bean
    public QuartzInitializerListener createQuartzListener() {
        return new QuartzInitializerListener();
    }

    @Bean(name = "scheduler")
    public Scheduler createScheduler() {
        return createSchedulerFactoryBean().getScheduler();
    }
}
