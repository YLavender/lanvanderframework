package lanvander.framework.quartz.springboot.handler.message;

import com.alibaba.fastjson.JSONObject;
import lanvander.framework.quartz.springboot.handler.AbstractHandler;
import lanvander.framework.quartz.springboot.handler.annotate.HandlerType;
import org.springframework.stereotype.Component;

@Component
@HandlerType("sms")
public class SMSMessageSendHandlerImpl extends AbstractHandler {

    @Override
    public boolean sendMessage(JSONObject jsonObject) {
        System.out.println("sms sending.....");
        return false;
    }
}
