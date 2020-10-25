package lanvander.framework.quartz.springboot.handler.message;

import com.alibaba.fastjson.JSONObject;
import lanvander.framework.quartz.springboot.handler.AbstractHandler;
import lanvander.framework.quartz.springboot.handler.annotate.HandlerType;
import org.springframework.stereotype.Component;

@Component
@HandlerType(value = "wechat", description = "微信消息发送处理器")
public class WeChatMessageSendHandlerImpl extends AbstractHandler {

    @Override
    public boolean sendMessage(JSONObject jsonObject) {
        System.out.println("wechat sending....");
        return false;
    }
}
