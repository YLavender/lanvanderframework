package lanvander.quartz.springboot.handler.message;

import com.alibaba.fastjson.JSONObject;
import lanvander.quartz.springboot.handler.AbstractHandler;
import lanvander.quartz.springboot.handler.annotate.HandlerType;
import org.springframework.stereotype.Component;

@Component
@HandlerType("wechat")
public class WeChatMessageSendHandlerImpl extends AbstractHandler {

  @Override
  public boolean sendMessage(JSONObject jsonObject) {
    System.out.println("wechat sending....");
    return false;
  }
}
