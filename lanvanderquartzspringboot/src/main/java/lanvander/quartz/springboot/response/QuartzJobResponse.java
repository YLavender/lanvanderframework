package lanvander.quartz.springboot.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuartzJobResponse {
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

  private Map<String, Object> jobDataMap;
}
