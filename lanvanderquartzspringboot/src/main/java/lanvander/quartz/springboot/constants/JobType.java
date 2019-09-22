package lanvander.quartz.springboot.constants;

import lanvander.quartz.springboot.job.QuartzSpringBootJobBean;
import lanvander.quartz.springboot.job.QuartzSpringBootSMSJobBean;
import lombok.Getter;

public enum JobType {
  NORMAL_JOB("normal", QuartzSpringBootJobBean.class),
  SMS_JOB("sms", QuartzSpringBootSMSJobBean.class);

  @Getter private String className;
  @Getter private Class jobClass;

  JobType(String className, Class jobClass) {
    this.className = className;
    this.jobClass = jobClass;
  }
}
