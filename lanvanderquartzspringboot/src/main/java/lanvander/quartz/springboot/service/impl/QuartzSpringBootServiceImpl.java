package lanvander.quartz.springboot.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lanvander.domain.specific.ServerResponse;
import lanvander.quartz.springboot.constants.JobType;
import lanvander.quartz.springboot.mapper.JobMapper;
import lanvander.quartz.springboot.request.Job;
import lanvander.quartz.springboot.request.QuartzJobRequest;
import lanvander.quartz.springboot.response.QuartzJobResponse;
import lanvander.quartz.springboot.service.QuartzSpringBootService;
import lanvander.utils.ByteArrayUtils;
import lanvander.utils.ClassFieldUtils;
import lanvander.utils.DateUtils;
import lanvander.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class QuartzSpringBootServiceImpl implements QuartzSpringBootService {

  @Resource private JobMapper jobMapper;

  @Value("${spring.quartz.scheduler-name}")
  private String schedulerName;

  @Value("${spring.quartz.restart-on-deploy}")
  private String isRestart;

  @Autowired
  @Qualifier("scheduler")
  private Scheduler scheduler;

  @PostConstruct
  private void restartJob() {
    if (!"true".equals(isRestart)) return;
    List<Job> jobList = jobMapper.getAllJobs();
    jobList.stream()
        .filter(this::checkJobValid)
        .collect(Collectors.toList())
        .forEach(
            restartJob -> {
              try {
                JobDetail jobDetail = createJobDetail(null, restartJob);
                QuartzJobRequest request = new QuartzJobRequest();
                ClassFieldUtils.copyProperties(restartJob, request);
                request.setJobGroupName(restartJob.getJobGroup());
                request.setRepeatCount(
                    restartJob.getIntervalCount() == null ? 0 : restartJob.getIntervalCount());
                Trigger trigger = getTrigger(request, getScheduleBuilder(request));
                scheduler.scheduleJob(jobDetail, trigger);
              } catch (SchedulerException ex) {
                log.error(
                    "重启定时任务失败, 异常原因: {}, 异常信息: {}, 定时任务信息: {}",
                    ex.getCause(),
                    ex.getMessage(),
                    restartJob.getJobGroup() + "." + restartJob.getJobName());
              }
            });
  }

  public ServerResponse addJob(QuartzJobRequest request) {
    if (checkJobExists(request)) return ServerResponse.error("同组同名的任务已经在执行中, 无法创建.");
    JobDetail jobDetail = createJobDetail(request, null);
    Trigger trigger = getTrigger(request, getScheduleBuilder(request));
    try {
      jobMapper.insertSelective(createInsertJob(request));
      scheduler.scheduleJob(jobDetail, trigger);
      scheduler.start();
      return ServerResponse.success();
    } catch (SchedulerException ex) {
      log.error("创建定时任务失败, 异常原因: {}, 异常信息: {}", ex.getCause(), ex.getMessage());
      return ServerResponse.error(ex);
    }
  }

  public ServerResponse updateJob(QuartzJobRequest request) {
    if (!checkJobExists(request)) return ServerResponse.error("该定时任务不存在, 无法更新.");
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
      jobMapper.updateByPrimaryKey(createUpdateJob(request, null));
      scheduler.rescheduleJob(triggerKey, trigger);
      return ServerResponse.success();
    } catch (SchedulerException ex) {
      log.error("更新定时任务失败, 异常原因: {}, 异常信息: {}", ex.getCause(), ex.getMessage());
      return ServerResponse.error(ex);
    }
  }

  public ServerResponse updateTrigger(QuartzJobRequest request) {
    Job job = jobMapper.selectByJob(request.getJobName(), request.getJobGroupName());
    if (job == null) return ServerResponse.error("更新触发器失败, 找不到对应的任务.");
    try {
      TriggerKey jobTriggerKey =
          TriggerKey.triggerKey(job.getTriggerName(), job.getTriggerGroupName());
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
      jobMapper.updateByPrimaryKey(createUpdateJob(request, job));
      scheduler.rescheduleJob(triggerKey, trigger);
      return ServerResponse.success();
    } catch (SchedulerException ex) {
      log.error("更新定时任务失败, 异常原因: {}, 异常信息: {}", ex.getCause(), ex.getMessage());
      return ServerResponse.error(ex);
    }
  }

  public ServerResponse deleteJob(QuartzJobRequest request) {
    Job job = jobMapper.selectByJob(request.getJobName(), request.getJobGroupName());
    if (job == null) return ServerResponse.error("没有对应的任务, 无法删除.");
    try {
      jobMapper.deleteByPrimaryKey(job.getId());
      scheduler.deleteJob(new JobKey(request.getJobName(), request.getJobGroupName()));
      return ServerResponse.success();
    } catch (SchedulerException ex) {
      log.error("删除定时任务失败, 异常原因: {}, 异常信息: {}", ex.getCause(), ex.getMessage());
      return ServerResponse.error(ex);
    }
  }

  public ServerResponse pauseJob(QuartzJobRequest request) {
    Job job = jobMapper.selectByJob(request.getJobName(), request.getJobGroupName());
    if (job == null) return ServerResponse.error("没有对应的任务, 无法暂停.");
    try {
      job.setIsPause("1");
      jobMapper.updateByPrimaryKeySelective(job);
      scheduler.pauseJob(new JobKey(request.getJobName(), request.getJobGroupName()));
      return ServerResponse.success();
    } catch (SchedulerException ex) {
      log.error("暂停定时任务失败, 异常原因: {}, 异常信息: {}", ex.getCause(), ex.getMessage());
      return ServerResponse.error(ex);
    }
  }

  public ServerResponse resumeJob(QuartzJobRequest request) {
    Job job = jobMapper.selectByJob(request.getJobName(), request.getJobGroupName());
    if (job == null) return ServerResponse.error("没有对应的任务, 无法唤醒.");
    try {
      job.setIsPause("0");
      jobMapper.updateByPrimaryKeySelective(job);
      scheduler.resumeJob(new JobKey(request.getJobName(), request.getJobGroupName()));
      return ServerResponse.success();
    } catch (SchedulerException ex) {
      log.error("唤醒定时任务失败, 异常原因: {}, 异常信息: {}", ex.getCause(), ex.getMessage());
      return ServerResponse.error(ex);
    }
  }

  public ServerResponse runJob(QuartzJobRequest request) {
    if (!checkJobExists(request)) return ServerResponse.error("没有对应的任务, 无法执行");
    try {
      scheduler.triggerJob(new JobKey(request.getJobName(), request.getJobGroupName()));
      return ServerResponse.success();
    } catch (SchedulerException ex) {
      log.error("唤醒定时任务失败, 异常原因: {}, 异常信息: {}", ex.getCause(), ex.getMessage());
      return ServerResponse.error(ex);
    }
  }

  public ServerResponse<List<QuartzJobResponse>> getAllJobs() {
    List<Job> resultList = jobMapper.getAllJobs();
    if (resultList != null && resultList.size() > 0) {
      return ServerResponse.success(assembleQuartzJobResponse(resultList));
    }
    return ServerResponse.error("查询不到数据");
  }

  public ServerResponse<List<QuartzJobResponse>> getExecutingJobs() {
    List<Job> resultList = jobMapper.getAllJobs();
    resultList =
        resultList.stream().filter(x -> "0".equals(x.getIsPause())).collect(Collectors.toList());
    if (resultList.size() > 0) {
      return ServerResponse.success(assembleQuartzJobResponse(resultList));
    }
    return ServerResponse.error("查询不到数据");
  }

  private List<QuartzJobResponse> assembleQuartzJobResponse(List<Job> jobList) {
    List<QuartzJobResponse> responseList = new ArrayList<>();
    jobList.stream()
        .forEach(
            job -> {
              QuartzJobResponse response = new QuartzJobResponse();
              ClassFieldUtils.copyProperties(job, response);
              response.setJobDataMap(JSONObject.parseObject(new String(job.getJobDataMap())));
              responseList.add(response);
            });
    return responseList;
  }

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

  private ScheduleBuilder getScheduleBuilder(QuartzJobRequest request) {
    ScheduleBuilder scheduleBuilder;
    if (request.getCronExpression() != null)
      scheduleBuilder =
          CronScheduleBuilder.cronSchedule(request.getCronExpression())
              .withMisfireHandlingInstructionFireAndProceed();
    else
      scheduleBuilder =
          SimpleScheduleBuilder.simpleSchedule()
              .withRepeatCount(
                  request.getRepeatCount() == 0
                      ? SimpleTrigger.REPEAT_INDEFINITELY
                      : request.getRepeatCount())
              .withIntervalInMilliseconds(
                  getIntervalTime(request.getIntervalTime(), request.getIntervalTimeType()));
    return scheduleBuilder;
  }

  private Trigger getTrigger(QuartzJobRequest request, ScheduleBuilder scheduleBuilder) {
    Trigger trigger;

    if (request.getCronExpression() != null)
      trigger =
          TriggerBuilder.newTrigger()
              .withSchedule(scheduleBuilder)
              .withIdentity(request.getTriggerName(), request.getTriggerGroupName())
              .build();
    else if (request.getStartDate() != null && request.getEndDate() != null)
      trigger =
          TriggerBuilder.newTrigger()
              .withIdentity(request.getTriggerName(), request.getTriggerGroupName())
              .withSchedule(scheduleBuilder)
              .startAt(DateUtils.convertStringToDate(request.getStartDate()))
              .endAt(DateUtils.convertStringToDate(request.getEndDate()))
              .build();
    else if (request.getStartDate() != null)
      trigger =
          TriggerBuilder.newTrigger()
              .withIdentity(request.getTriggerName(), request.getTriggerGroupName())
              .withSchedule(scheduleBuilder)
              .startAt(DateUtils.convertStringToDate(request.getStartDate()))
              .build();
    else
      trigger =
          TriggerBuilder.newTrigger()
              .startNow()
              .withIdentity(request.getTriggerName(), request.getTriggerGroupName())
              .withSchedule(scheduleBuilder)
              .endAt(DateUtils.convertStringToDate(request.getEndDate()))
              .build();

    return trigger;
  }

  private Job createInsertJob(QuartzJobRequest request) {
    Job job = new Job();
    job.setJobName(request.getJobName());
    job.setJobGroup(request.getJobGroupName());
    job.setJobType(request.getJobType());
    job.setTriggerName(request.getTriggerName());
    job.setTriggerGroupName(request.getTriggerGroupName());
    job.setJobDataMap(ByteArrayUtils.convertToByteArray(request.getJobDataMap()));
    job.setDescription(
        request.getDescription() != null ? request.getDescription() : LocalDate.now() + " 新建的任务");
    if (request.getCronExpression() != null) job.setCronExpression(request.getCronExpression());
    else {
      job.setStartDate(DateUtils.convertStringToDate(request.getStartDate()));
      job.setEndDate(DateUtils.convertStringToDate(request.getEndDate()));
      job.setIntervalCount(request.getRepeatCount());
      job.setIntervalTime(request.getIntervalTime());
      job.setIntervalTimeType(request.getIntervalTimeType());
    }
    return job;
  }

  private Job createUpdateJob(QuartzJobRequest request, Job origin) {
    Job job = new Job();
    if (origin == null) {
      job.setJobName(request.getJobName());
      job.setJobGroup(request.getJobGroupName());
      job.setDescription(request.getDescription());
      if (request.getCronExpression() != null) {
        job.setCronExpression(request.getCronExpression());
        job.setIntervalTimeType("");
        job.setStartDate(null);
        job.setEndDate(null);
        job.setIntervalTime(null);
        job.setIntervalCount(null);
      } else {
        job.setCronExpression("");
        job.setStartDate(DateUtils.convertStringToDate(request.getStartDate()));
        job.setEndDate(DateUtils.convertStringToDate(request.getEndDate()));
        job.setIntervalCount(request.getRepeatCount());
        job.setIntervalTime(request.getIntervalTime());
        job.setIntervalTimeType(request.getIntervalTimeType());
      }
    } else {
      job.setTriggerName(request.getTriggerName());
      job.setTriggerGroupName(request.getTriggerGroupName());
    }
    return job;
  }

  private boolean checkJobExists(QuartzJobRequest request) {
    return jobMapper.selectByJob(request.getJobName(), request.getJobGroupName()) != null;
  }

  private boolean checkJobValid(Job job) {
    if (!StringUtils.isEmpty(job.getCronExpression()) && "0".equals(job.getIsPause())) return true;
    Date date = new Date();
    boolean endDateFlag = job.getEndDate() != null;
    boolean startDateFlag = job.getStartDate() != null;
    if (endDateFlag && date.after(job.getEndDate())) {
      if (!startDateFlag) return true;
      else return date.after(job.getStartDate());
    } else return !endDateFlag;
  }

  private JobDetail createJobDetail(QuartzJobRequest request, Job job) {
    if (request == null) return createJobDetailByJob(job);
    else return createJobDetailByRequest(request);
  }

  private JobDetail createJobDetailByJob(Job job) {
    JobDataMap jobDataMap = new JobDataMap();
    if (job.getJobDataMap() != null && job.getJobDataMap().length > 0) {
      JSONObject jsonObject =
          JSONObject.parseObject(JSON.toJSONString(new String(job.getJobDataMap())));
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
                    .filter(jobType -> jobType.getClassName().equals(job.getJobType()))
                    .collect(Collectors.toList())
                    .get(0)
                    .getJobClass())
            .withIdentity(job.getJobName(), job.getJobGroup())
            .requestRecovery()
            .usingJobData(jobDataMap)
            .withDescription(job.getDescription())
            .build();
    return jobDetail;
  }

  private JobDetail createJobDetailByRequest(QuartzJobRequest request) {
    JobDataMap jobDataMap = new JobDataMap();
    if (request.getJobDataMap() != null && !request.getJobDataMap().isEmpty()) {
      request
          .getJobDataMap()
          .keySet()
          .forEach(
              key -> {
                jobDataMap.put(key, request.getJobDataMap().get(key));
              });
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
