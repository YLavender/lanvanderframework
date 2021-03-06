package lanvander.framework.quartz.springboot.handler.context;

import java.util.Map;
import lanvander.framework.quartz.springboot.handler.AbstractHandler;
import lanvander.framework.quartz.springboot.utils.BeanUtils;

public class HandlerContext {

  private final Map<String, Class<?>> handlerMap;

  public HandlerContext(Map<String, Class<?>> map) {
    this.handlerMap = map;
  }

  public AbstractHandler getHandler(String type) {
    Class<?> clazz = handlerMap.get(type);
    if (clazz == null) {
      throw new IllegalArgumentException("不支持的处理类型 " + type);
    }
    return (AbstractHandler) BeanUtils.getBean(clazz);
  }
}
