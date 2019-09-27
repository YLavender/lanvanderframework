package lanvander.quartz.springboot.job;

import com.alibaba.fastjson.JSON;
import lanvander.quartz.springboot.service.MessageSendService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.List;
import java.util.Map;

@Slf4j
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class QuartzSpringBootMessageJobBean extends QuartzJobBean {

  @Setter private String content;

  @Setter private String type;

  @Autowired private MessageSendService messageSendService;

  @Override
  protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
    JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
    if (jobDataMap == null
        || jobDataMap.get("data") == null
        || ((List) jobDataMap.get("data")).size() == 0) {
      throw new JobExecutionException("消息定时任务需要有数据才能执行.");
    }
    for (Map map : (List<Map<String, Object>>) jobDataMap.get("data")) {
      checkValid(map);
    }
    while (!messageSendService.sendMessage(JSON.toJSONString(jobDataMap.get("data")))) {
      try {
        Thread.currentThread().wait(10000);
      } catch (InterruptedException ex) {
        log.error("消息发送失败, 异常原因: {}, 异常信息: {}", ex.getCause(), ex.getMessage());
      }
    }
  }

  private void checkValid(Map<String, Object> jobDataMap) throws JobExecutionException {
    if (jobDataMap.get("content") == null) {
      throw new JobExecutionException("消息发送定时任务需要有消息内容.");
    }
    if (jobDataMap.get("type") == null) {
      throw new JobExecutionException("消息发送定时任务需要有消息类型.");
    }
  }
}
