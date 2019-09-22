package lanvander.quartz.springboot.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Slf4j
public class QuartzSpringBootJobBean extends QuartzJobBean {
  @Override
  protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
    JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
    String jobName = context.getJobDetail().getKey().getName();
    log.info(
        this.getClass() + "'s " + jobName + " running with data {}",
        jobDataMap == null ? "" : jobDataMap);
  }
}
