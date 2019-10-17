package lanvander.framework.quartz.springboot.handler;

import com.alibaba.fastjson.JSONObject;
import lanvander.framework.elasticjob.java.utils.PropertiesUtils;

public abstract class AbstractHandler {
  static {
    PropertiesUtils.loadProperties("message.properties");
  }

  public abstract boolean sendMessage(JSONObject jsonObject);
}
