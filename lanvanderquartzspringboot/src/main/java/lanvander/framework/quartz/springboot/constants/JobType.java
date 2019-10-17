package lanvander.framework.quartz.springboot.constants;

import lanvander.framework.quartz.springboot.job.QuartzSpringBootJobBean;
import lanvander.framework.quartz.springboot.job.QuartzSpringBootMessageJobBean;
import lombok.Getter;

public enum JobType {
  NORMAL_JOB("normal", QuartzSpringBootJobBean.class),
  SMS_JOB("message", QuartzSpringBootMessageJobBean.class);

  @Getter private String className;
  @Getter private Class jobClass;

  JobType(String className, Class jobClass) {
    this.className = className;
    this.jobClass = jobClass;
  }
}
