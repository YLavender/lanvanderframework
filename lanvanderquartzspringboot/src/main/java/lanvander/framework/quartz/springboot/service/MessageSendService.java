package lanvander.framework.quartz.springboot.service;

public interface MessageSendService {

  boolean sendMessage(String json);
}
