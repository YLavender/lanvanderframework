package lanvander.framework.quartz.springboot.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuartzJob {
  private Integer id;

  private String jobName;

  private String jobGroup;

  private String jobType;

  private String triggerName;

  private String triggerGroupName;

  private String description;

  private String cronExpression;

  private Date startDate;

  private Date endDate;

  private String isPause;

  private Integer intervalTime;

  private String intervalTimeType;

  private Integer intervalCount;

  private Date createTime;

  private Date updateTime;

  private Integer version;

  private byte[] jobDataMap;

  public QuartzJob(
      Integer id,
      String jobName,
      String jobGroup,
      String jobType,
      String triggerName,
      String triggerGroupName,
      String description,
      String cronExpression,
      Date startDate,
      Date endDate,
      String isPause,
      Integer intervalTime,
      String intervalTimeType,
      Integer intervalCount,
      Date createTime,
      Date updateTime,
      Integer version) {
    this.id = id;
    this.jobName = jobName;
    this.jobGroup = jobGroup;
    this.jobType = jobType;
    this.triggerName = triggerName;
    this.triggerGroupName = triggerGroupName;
    this.description = description;
    this.cronExpression = cronExpression;
    this.startDate = startDate;
    this.endDate = endDate;
    this.isPause = isPause;
    this.intervalTime = intervalTime;
    this.intervalTimeType = intervalTimeType;
    this.intervalCount = intervalCount;
    this.createTime = createTime;
    this.updateTime = updateTime;
    this.version = version;
  }
}
