package lanvander.framework.quartz.springboot.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lanvander.framework.quartz.springboot.handler.AbstractHandler;
import lanvander.framework.quartz.springboot.handler.context.HandlerContext;
import lanvander.framework.quartz.springboot.service.MessageSendService;
import lanvander.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageSendServiceImpl implements MessageSendService {

    /**
     * 在 HandlerProcessor 中动态注册 handlerContext 的 bean
     */
    @Autowired
    private HandlerContext handlerContext;

    @Override
    public boolean sendMessage(String json) {
        JSONArray jsonArray = JSONArray.parseArray(json);
        jsonArray.forEach(
                object -> {
                    if (object instanceof JSONObject) {
                        JSONObject jsonObject = (JSONObject) object;
                        if (StringUtils.isEmpty(jsonObject.getString("content")))
                            throw new IllegalArgumentException("参数错误, 没有传入发送的消息内容.");
                        AbstractHandler handler = handlerContext.getHandler(jsonObject.getString("type"));
                        handler.sendMessage(jsonObject);
                    }
                });
        return true;
    }
}
