package lanvander.framework.quartz.springboot.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class QuartzJobRequest {

  @ApiModelProperty(
      value = "JOB_NAME",
      notes = "任务名",
      example = "job",
      required = true,
      dataType = "String")
  @JsonProperty("JOB_NAME")
  private String jobName;

  @ApiModelProperty(
      value = "JOB_GROUP_NAME",
      notes = "组名",
      example = "group",
      required = true,
      dataType = "String")
  @JsonProperty("JOB_GROUP_NAME")
  private String jobGroupName;

  @ApiModelProperty(
      value = "DESCRIPTION",
      notes = "描述",
      example = "description",
      dataType = "String")
  @JsonProperty("DESCRIPTION")
  private String description;

  @ApiModelProperty(
      value = "CRON_EXPRESSION",
      notes = "cron表达式",
      example = "0/5 * * * * ?",
      dataType = "String",
      reference = "优先处理Cron表达式")
  @JsonProperty("CRON_EXPRESSION")
  private String cronExpression;

  @ApiModelProperty(
      value = "INTERVAL_TIME",
      notes = "间隔时间",
      example = "5",
      dataType = "int",
      reference = "优先处理Cron表达式")
  @JsonProperty("INTERVAL_TIME")
  private int intervalTime;

  @ApiModelProperty(
      value = "INTERVAL_TIME_TYPE",
      notes = "间隔时间类型",
      example = "s",
      dataType = "String",
      reference = "优先处理Cron表达式")
  @JsonProperty("INTERVAL_TIME_TYPE")
  private String intervalTimeType;

  @ApiModelProperty(
      value = "REPEAT_COUNT",
      notes = "重复次数",
      example = "5",
      dataType = "int",
      reference = "优先处理Cron表达式")
  @JsonProperty("REPEAT_COUNT")
  private int repeatCount;

  @ApiModelProperty(
      value = "END_DATE",
      notes = "结束时间",
      example = "20190909",
      dataType = "String",
      reference = "优先处理Cron表达式")
  @JsonProperty("END_DATE")
  private String endDate;

  @ApiModelProperty(
      value = "START_DATE",
      notes = "开始时间",
      example = "20190909",
      dataType = "String",
      reference = "优先处理Cron表达式")
  @JsonProperty("START_DATE")
  private String startDate;

  @ApiModelProperty(
      value = "TRIGGER_NAME",
      notes = "触发器名",
      example = "triggerName",
      required = true,
      dataType = "String")
  @JsonProperty("TRIGGER_NAME")
  private String triggerName;

  @ApiModelProperty(
      value = "TRIGGER_GROUP_NAME",
      notes = "触发器组名",
      example = "triggerGroupName",
      required = true,
      dataType = "String")
  @JsonProperty("TRIGGER_GROUP_NAME")
  private String triggerGroupName;

  @ApiModelProperty(
      value = "JOB_TYPE",
      notes = "执行任务的属性",
      example = "normal",
      required = true,
      dataType = "String")
  @JsonProperty("JOB_TYPE")
  private String jobType;

  @ApiModelProperty(value = "JOB_DATA_MAP", notes = "任务执行交互数据", example = "", dataType = "list")
  @JsonProperty("JOB_DATA_MAP")
  private List<Map<String, Object>> jobDataMap;
}
