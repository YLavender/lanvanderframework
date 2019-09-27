package lanvander.quartz.springboot.handler.context;

import lanvander.quartz.springboot.handler.AbstractHandler;
import lanvander.quartz.springboot.utils.BeanUtils;

import java.util.Map;

public class HandlerContext {
  private Map<String, Class> handlerMap;

  public HandlerContext(Map<String, Class> map) {
    this.handlerMap = map;
  }

  public AbstractHandler getHandler(String type) {
    Class clazz = handlerMap.get(type);
    if (clazz == null) throw new IllegalArgumentException("不支持的处理类型 " + type);
    return (AbstractHandler) BeanUtils.getBean(clazz);
  }
}
