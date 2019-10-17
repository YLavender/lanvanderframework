package lanvander.framework.quartz.java.job;

import com.alibaba.fastjson.JSON;
import lanvander.utils.RandomUtils;
import lombok.Data;
import org.quartz.*;

import java.time.LocalTime;

@Data
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class JavaJob implements Job {

  /**
   * 需要Get、Set方法传值 <br>
   * 或者 <br>
   * 用JobDataMap传值,要确保key值一致
   */
  private String name;

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    if (name != null) {
      System.out.println("name: " + this.name);
    }
    try {
      System.out.println("local time: " + LocalTime.now());
      System.out.println("scheduler instance: " + context.getScheduler().getSchedulerInstanceId());
      System.out.println("scheduler name: " + context.getScheduler().getSchedulerName());
      System.out.println("job key: " + context.getJobDetail().getKey());
      System.out.println(
          "job data map: " + JSON.toJSONString(context.getJobDetail().getJobDataMap()));
      context
          .getJobDetail()
          .getJobDataMap()
          .put("name", name.concat(RandomUtils.randomRepeatablePositiveInteger() + ""));
      System.out.println("next: " + context.getTrigger().getNextFireTime());
      Thread.sleep(7000);
      System.out.println("thread name: " + Thread.currentThread().getName());
      System.out.println("end time: " + LocalTime.now());
    } catch (SchedulerException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
