package lanvander.framework.quartz.springboot.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import lanvander.base.exception.BusinessException;
import lanvander.framework.quartz.springboot.constants.JobType;
import lanvander.framework.quartz.springboot.domain.QuartzJob;
import lanvander.framework.quartz.springboot.mapper.QuartzJobMapper;
import lanvander.framework.quartz.springboot.request.QuartzJobRequest;
import lanvander.framework.quartz.springboot.response.QuartzJobResponse;
import lanvander.framework.quartz.springboot.service.QuartzSpringBootService;
import lanvander.utils.ByteArrayUtils;
import lanvander.utils.ClassFieldUtils;
import lanvander.utils.DateUtils;
import lanvander.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.DateBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class QuartzSpringBootServiceImpl implements QuartzSpringBootService {

  @Resource
  private QuartzJobMapper quartzJobMapper;

  @Value("${spring.quartz.scheduler-name}")
  private String schedulerName;

  @Value("${spring.quartz.restart-on-deploy}")
  private String isRestart;

  @Autowired
  @Qualifier("scheduler")
  private Scheduler scheduler;

  @PostConstruct
  private void restartJob() {
    if (!"1".equals(isRestart)) {
      return;
    }
    List<QuartzJob> quartzJobList = quartzJobMapper.getAllJobs();
    quartzJobList.stream()
        .filter(this::checkJobValid)
        .collect(Collectors.toList())
        .forEach(
            restartQuartzJob -> {
              try {
                JobDetail jobDetail = createJobDetail(null, restartQuartzJob);
                QuartzJobRequest request = new QuartzJobRequest();
                ClassFieldUtils.copyProperties(restartQuartzJob, request);
                request.setJobGroupName(restartQuartzJob.getJobGroup());
                request.setRepeatCount(
                    restartQuartzJob.getIntervalCount() == null
                        ? 0
                        : restartQuartzJob.getIntervalCount());
                Trigger trigger = getTrigger(request, getScheduleBuilder(request));
                scheduler.scheduleJob(jobDetail, trigger);
              } catch (SchedulerException ex) {
                log.error(
                    "重启定时任务失败, 异常原因: {}, 异常信息: {}, 定时任务信息: {}",
                    ex.getCause(),
                    ex.getMessage(),
                    restartQuartzJob.getJobGroup() + "." + restartQuartzJob.getJobName());
              }
            });
  }

  public void addJob(QuartzJobRequest request) {
    if (checkJobExists(request)) {
      throw new BusinessException("同组同名的任务已经在执行中, 无法创建.");
    }
    JobDetail jobDetail = createJobDetail(request, null);
    Trigger trigger = getTrigger(request, getScheduleBuilder(request));
    try {
      scheduler.scheduleJob(jobDetail, trigger);
      scheduler.start();
      quartzJobMapper.insertSelective(createInsertJob(request));
    } catch (SchedulerException ex) {
      log.error("创建定时任务失败, 异常原因: {}, 异常信息: {}", ex.getCause(), ex.getMessage());
      throw new BusinessException(ex);
    }
  }

  public void updateJob(QuartzJobRequest request) {
    if (!checkJobExists(request)) {
      throw new BusinessException("该定时任务不存在, 无法更新.");
    }
    try {
      TriggerKey triggerKey =
          TriggerKey.triggerKey(request.getTriggerName(), request.getTriggerGroupName());
      Trigger trigger = scheduler.getTrigger(triggerKey);
      ScheduleBuilder scheduleBuilder = getScheduleBuilder(request);
      trigger =
          trigger
              .getTriggerBuilder()
              .withIdentity(triggerKey)
              .withSchedule(scheduleBuilder)
              .build();
      scheduler.rescheduleJob(triggerKey, trigger);
      quartzJobMapper.updateByPrimaryKey(createUpdateJob(request, null));
    } catch (SchedulerException ex) {
      log.error("更新定时任务失败, 异常原因: {}, 异常信息: {}", ex.getCause(), ex.getMessage());
      throw new BusinessException(ex);
    }
  }

  public void updateTrigger(QuartzJobRequest request) {
    QuartzJob quartzJob =
        quartzJobMapper.selectByJob(request.getJobName(), request.getJobGroupName());
    if (quartzJob == null) {
      throw new BusinessException("更新触发器失败, 找不到对应的任务.");
    }
    try {
      TriggerKey jobTriggerKey =
          TriggerKey.triggerKey(quartzJob.getTriggerName(), quartzJob.getTriggerGroupName());
      TriggerKey triggerKey =
          TriggerKey.triggerKey(request.getTriggerName(), request.getTriggerGroupName());
      Trigger trigger = scheduler.getTrigger(jobTriggerKey);
      ScheduleBuilder scheduleBuilder = getScheduleBuilder(request);
      trigger =
          trigger
              .getTriggerBuilder()
              .withIdentity(triggerKey)
              .withSchedule(scheduleBuilder)
              .build();
      scheduler.rescheduleJob(triggerKey, trigger);
      quartzJobMapper.updateByPrimaryKey(createUpdateJob(request, quartzJob));
    } catch (SchedulerException ex) {
      log.error("更新定时任务失败, 异常原因: {}, 异常信息: {}", ex.getCause(), ex.getMessage());
      throw new BusinessException(ex);
    }
  }

  public QuartzJob deleteJob(QuartzJobRequest request) {
    QuartzJob quartzJob =
        quartzJobMapper.selectByJob(request.getJobName(), request.getJobGroupName());
    if (quartzJob == null) {
      throw new BusinessException("没有对应的任务, 无法删除.");
    }
    try {
      scheduler.deleteJob(new JobKey(request.getJobName(), request.getJobGroupName()));
      quartzJobMapper.deleteByPrimaryKey(quartzJob.getId());
      return quartzJob;
    } catch (SchedulerException ex) {
      log.error("删除定时任务失败, 异常原因: {}, 异常信息: {}", ex.getCause(), ex.getMessage());
      throw new BusinessException(ex);
    }
  }

  public void pauseJob(QuartzJobRequest request) {
    QuartzJob quartzJob =
        quartzJobMapper.selectByJob(request.getJobName(), request.getJobGroupName());
    if (quartzJob == null) {
      throw new BusinessException("没有对应的任务, 无法暂停.");
    }
    try {
      quartzJob.setIsPause("1");
      scheduler.pauseJob(new JobKey(request.getJobName(), request.getJobGroupName()));
      quartzJobMapper.updateByPrimaryKeySelective(quartzJob);
    } catch (SchedulerException ex) {
      log.error("暂停定时任务失败, 异常原因: {}, 异常信息: {}", ex.getCause(), ex.getMessage());
      throw new BusinessException(ex);
    }
  }

  public void resumeJob(QuartzJobRequest request) {
    QuartzJob quartzJob =
        quartzJobMapper.selectByJob(request.getJobName(), request.getJobGroupName());
    if (quartzJob == null) {
      throw new BusinessException("没有对应的任务, 无法唤醒.");
    }
    try {
      quartzJob.setIsPause("0");
      scheduler.resumeJob(new JobKey(request.getJobName(), request.getJobGroupName()));
      quartzJobMapper.updateByPrimaryKeySelective(quartzJob);
    } catch (SchedulerException ex) {
      log.error("唤醒定时任务失败, 异常原因: {}, 异常信息: {}", ex.getCause(), ex.getMessage());
      throw new BusinessException(ex);
    }
  }

  public void runJob(QuartzJobRequest request) {
    if (!checkJobExists(request)) {
      throw new BusinessException("没有对应的任务, 无法执行");
    }
    try {
      scheduler.triggerJob(new JobKey(request.getJobName(), request.getJobGroupName()));
    } catch (SchedulerException ex) {
      log.error("唤醒定时任务失败, 异常原因: {}, 异常信息: {}", ex.getCause(), ex.getMessage());
      throw new BusinessException(ex);
    }
  }

  public QuartzJobResponse getJob(String jobName, String jobGroupName) {
    QuartzJob quartzJob = quartzJobMapper.selectByJob(jobName, jobGroupName);
    if (quartzJob != null) {
      return assembleQuartzJobResponse(Arrays.asList(quartzJob)).get(0);
    }
    throw new BusinessException("查询不到数据");
  }

  public List<QuartzJobResponse> getAllJobs() {
    List<QuartzJob> resultList = quartzJobMapper.getAllJobs();
    if (resultList != null && resultList.size() > 0) {
      return assembleQuartzJobResponse(resultList);
    }
    throw new BusinessException("查询不到数据");
  }

  public List<QuartzJobResponse> getExecutingJobs() {
    List<QuartzJob> resultList = quartzJobMapper.getAllJobs();
    resultList =
        resultList.stream().filter(x -> "0".equals(x.getIsPause())).collect(Collectors.toList());
    if (resultList.size() > 0) {
      return assembleQuartzJobResponse(resultList);
    }
    throw new BusinessException("查询不到数据");
  }

  // 构建定时任务返回报文
  private List<QuartzJobResponse> assembleQuartzJobResponse(List<QuartzJob> quartzJobList) {
    List<QuartzJobResponse> responseList = new ArrayList<>();
    quartzJobList.stream()
        .forEach(
            quartzJob -> {
              QuartzJobResponse response = new QuartzJobResponse();
              ClassFieldUtils.copyProperties(quartzJob, response);
              response.setJobDataMap(JSONObject.parseObject(new String(quartzJob.getJobDataMap())));
              responseList.add(response);
            });
    return responseList;
  }

  // 根据类型变换任务间隔时间
  private long getIntervalTime(int intervalTime, String intervalTimeType) {
    switch (intervalTimeType) {
      case "ms":
        return intervalTime;
      case "s":
        return intervalTime * 1000L;
      case "m":
        return intervalTime * DateBuilder.MILLISECONDS_IN_MINUTE;
      case "h":
        return intervalTime * DateBuilder.MILLISECONDS_IN_HOUR;
      default:
        throw new IllegalArgumentException("参数错误, 间隔时间为天及以上请使用cron表达式.");
    }
  }

  // 根据数据创建定时任务构造器
  private ScheduleBuilder getScheduleBuilder(QuartzJobRequest request) {
    ScheduleBuilder scheduleBuilder;
    if (request.getCronExpression() != null) {
      scheduleBuilder =
          CronScheduleBuilder.cronSchedule(request.getCronExpression())
              .withMisfireHandlingInstructionFireAndProceed();
    } else {
      scheduleBuilder =
          SimpleScheduleBuilder.simpleSchedule()
              .withRepeatCount(
                  request.getRepeatCount() == 0
                      ? SimpleTrigger.REPEAT_INDEFINITELY
                      : request.getRepeatCount())
              .withIntervalInMilliseconds(
                  getIntervalTime(request.getIntervalTime(), request.getIntervalTimeType()));
    }
    return scheduleBuilder;
  }

  // 根据数据创建触发器
  private Trigger getTrigger(QuartzJobRequest request, ScheduleBuilder scheduleBuilder) {
    Trigger trigger;

    if (request.getCronExpression() != null) {
      trigger =
          TriggerBuilder.newTrigger()
              .withSchedule(scheduleBuilder)
              .withIdentity(request.getTriggerName(), request.getTriggerGroupName())
              .build();
    } else if (request.getStartDate() != null && request.getEndDate() != null) {
      trigger =
          TriggerBuilder.newTrigger()
              .withIdentity(request.getTriggerName(), request.getTriggerGroupName())
              .withSchedule(scheduleBuilder)
              .startAt(DateUtils.convertStringToDate(request.getStartDate()))
              .endAt(DateUtils.convertStringToDate(request.getEndDate()))
              .build();
    } else if (request.getStartDate() != null) {
      trigger =
          TriggerBuilder.newTrigger()
              .withIdentity(request.getTriggerName(), request.getTriggerGroupName())
              .withSchedule(scheduleBuilder)
              .startAt(DateUtils.convertStringToDate(request.getStartDate()))
              .build();
    } else {
      trigger =
          TriggerBuilder.newTrigger()
              .startNow()
              .withIdentity(request.getTriggerName(), request.getTriggerGroupName())
              .withSchedule(scheduleBuilder)
              .endAt(DateUtils.convertStringToDate(request.getEndDate()))
              .build();
    }

    return trigger;
  }

  // 创建插入的任务数据
  private QuartzJob createInsertJob(QuartzJobRequest request) {
    QuartzJob quartzJob = new QuartzJob();
    quartzJob.setJobName(request.getJobName());
    quartzJob.setJobGroup(request.getJobGroupName());
    quartzJob.setJobType(request.getJobType());
    quartzJob.setTriggerName(request.getTriggerName());
    quartzJob.setTriggerGroupName(request.getTriggerGroupName());
    quartzJob.setJobDataMap(ByteArrayUtils.convertToByteArray(request.getJobDataMap()));
    quartzJob.setDescription(
        request.getDescription() != null ? request.getDescription() : LocalDate.now() + " 新建的任务");
    if (request.getCronExpression() != null) {
      quartzJob.setCronExpression(request.getCronExpression());
    } else {
      quartzJob.setStartDate(DateUtils.convertStringToDate(request.getStartDate()));
      quartzJob.setEndDate(DateUtils.convertStringToDate(request.getEndDate()));
      quartzJob.setIntervalCount(request.getRepeatCount());
      quartzJob.setIntervalTime(request.getIntervalTime());
      quartzJob.setIntervalTimeType(request.getIntervalTimeType());
    }
    return quartzJob;
  }

  // 创建更新的任务数据
  private QuartzJob createUpdateJob(QuartzJobRequest request, QuartzJob origin) {
    QuartzJob quartzJob = new QuartzJob();
    if (origin == null) {
      quartzJob.setJobName(request.getJobName());
      quartzJob.setJobGroup(request.getJobGroupName());
      quartzJob.setDescription(request.getDescription());
      quartzJob.setJobDataMap(
          request.getJobDataMap() == null || request.getJobDataMap().isEmpty()
              ? quartzJob.getJobDataMap()
              : ByteArrayUtils.convertToByteArray(request.getJobDataMap()));
      if (request.getCronExpression() != null) {
        quartzJob.setCronExpression(request.getCronExpression());
        quartzJob.setIntervalTimeType("");
        quartzJob.setStartDate(null);
        quartzJob.setEndDate(null);
        quartzJob.setIntervalTime(null);
        quartzJob.setIntervalCount(null);
      } else {
        quartzJob.setCronExpression("");
        quartzJob.setStartDate(DateUtils.convertStringToDate(request.getStartDate()));
        quartzJob.setEndDate(DateUtils.convertStringToDate(request.getEndDate()));
        quartzJob.setIntervalCount(request.getRepeatCount());
        quartzJob.setIntervalTime(request.getIntervalTime());
        quartzJob.setIntervalTimeType(request.getIntervalTimeType());
      }
    } else {
      quartzJob.setTriggerName(request.getTriggerName());
      quartzJob.setTriggerGroupName(request.getTriggerGroupName());
    }
    return quartzJob;
  }

  // check任务是否存在
  private boolean checkJobExists(QuartzJobRequest request) {
    return quartzJobMapper.selectByJob(request.getJobName(), request.getJobGroupName()) != null;
  }

  // check任务是否是当前时间要执行的任务
  private boolean checkJobValid(QuartzJob quartzJob) {
    if (!StringUtils.isEmpty(quartzJob.getCronExpression()) && "0".equals(quartzJob.getIsPause())) {
      return true;
    }
    Date date = new Date();
    boolean endDateFlag = quartzJob.getEndDate() != null;
    boolean startDateFlag = quartzJob.getStartDate() != null;
    if (endDateFlag && date.after(quartzJob.getEndDate())) {
      if (!startDateFlag) {
        return true;
      } else {
        return date.after(quartzJob.getStartDate());
      }
    } else {
      return !endDateFlag;
    }
  }

  // 根据数据创建任务详细
  private JobDetail createJobDetail(QuartzJobRequest request, QuartzJob quartzJob) {
    if (request == null) {
      return createJobDetailByJob(quartzJob);
    } else {
      return createJobDetailByRequest(request);
    }
  }

  // 根据数据库的任务创建任务详细
  private JobDetail createJobDetailByJob(QuartzJob quartzJob) {
    JobDataMap jobDataMap = new JobDataMap();
    if (quartzJob.getJobDataMap() != null && quartzJob.getJobDataMap().length > 0) {
      JSONObject jsonObject =
          JSONObject.parseObject(JSON.toJSONString(new String(quartzJob.getJobDataMap())));
      jsonObject
          .keySet()
          .forEach(
              key -> {
                jobDataMap.put(key, jsonObject.get(key));
              });
    }
    JobDetail jobDetail =
        JobBuilder.newJob(
            Arrays.stream(JobType.values())
                .filter(jobType -> jobType.getClassName().equals(quartzJob.getJobType()))
                .collect(Collectors.toList())
                .get(0)
                .getJobClass())
            .withIdentity(quartzJob.getJobName(), quartzJob.getJobGroup())
            .requestRecovery()
            .usingJobData(jobDataMap)
            .withDescription(quartzJob.getDescription())
            .build();
    return jobDetail;
  }

  // 根据报文创建任务详细
  private JobDetail createJobDetailByRequest(QuartzJobRequest request) {
    JobDataMap jobDataMap = new JobDataMap();
    if (request.getJobDataMap() != null && !request.getJobDataMap().isEmpty()) {
      jobDataMap.put("data", request.getJobDataMap());
    }
    JobDetail jobDetail =
        JobBuilder.newJob(
            Arrays.stream(JobType.values())
                .filter(jobType -> jobType.getClassName().equals(request.getJobType()))
                .collect(Collectors.toList())
                .get(0)
                .getJobClass())
            .withIdentity(request.getJobName(), request.getJobGroupName())
            .requestRecovery()
            .usingJobData(jobDataMap)
            .withDescription(
                request.getDescription() == null
                    ? LocalDateTime.now() + " 新建的任务"
                    : request.getDescription())
            .build();
    return jobDetail;
  }
}
