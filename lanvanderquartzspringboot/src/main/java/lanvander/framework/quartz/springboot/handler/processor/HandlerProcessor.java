package lanvander.framework.quartz.springboot.handler.processor;

import com.google.common.collect.Maps;
import java.util.Map;
import lanvander.framework.quartz.springboot.handler.annotate.HandlerType;
import lanvander.framework.quartz.springboot.handler.context.HandlerContext;
import lanvander.framework.quartz.springboot.handler.sanner.ClassScanner;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

@Component
public class HandlerProcessor implements BeanFactoryPostProcessor {

  private static final String[] HANDLER_PACKAGE = {"lanvander.quartz.springboot.handler.message"};

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
      throws BeansException {
    Map<String, Class<?>> handlerMap = Maps.newHashMap();
    ClassScanner.scan(HANDLER_PACKAGE, HandlerType.class)
        .forEach(
            clazz -> {
              String type = clazz.getAnnotation(HandlerType.class).value();
              handlerMap.put(type, clazz);
            });
    HandlerContext handlerContext = new HandlerContext(handlerMap);
    beanFactory.registerSingleton(HandlerContext.class.getName(), handlerContext);
  }
}
