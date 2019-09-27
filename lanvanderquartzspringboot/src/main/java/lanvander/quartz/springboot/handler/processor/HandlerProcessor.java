package lanvander.quartz.springboot.handler.processor;

import com.google.common.collect.Maps;
import lanvander.quartz.springboot.handler.annotate.HandlerType;
import lanvander.quartz.springboot.handler.context.HandlerContext;
import lanvander.quartz.springboot.handler.sanner.ClassScanner;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@SuppressWarnings("unchecked")
public class HandlerProcessor implements BeanFactoryPostProcessor {

  private static final String[] HANDLER_PACKAGE = {"lanvander.quartz.springboot.handler.message"};

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
      throws BeansException {
    Map<String, Class> handlerMap = Maps.newHashMapWithExpectedSize(5);
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
