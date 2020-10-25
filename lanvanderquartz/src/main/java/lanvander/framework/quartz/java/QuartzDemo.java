package lanvander.framework.quartz.java;

import java.util.Date;
import lanvander.framework.quartz.java.job.JavaJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.DateBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class QuartzDemo {

  public static void main(String[] args) throws SchedulerException, InterruptedException {
    Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
    scheduler.start();

    JobDataMap jobDataMap = new JobDataMap();
    jobDataMap.put("name", "lanvander");
    jobDataMap.put("project", "framework");
    jobDataMap.put("module", "quartz");

    JobDetail jobDetail =
        JobBuilder.newJob(JavaJob.class)
            .withIdentity("jobName", "jobGroupName")
            .usingJobData(jobDataMap)
            .build();

    /*  // 无限重复，永不关闭，创建同时执行
        Trigger trigger =
            TriggerBuilder.newTrigger()
                .startNow()
                .withSchedule(
                    SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(5).repeatForever())
                .build();
    */

    Date startDate = DateBuilder.futureDate(15, DateBuilder.IntervalUnit.SECOND);
    Date endDate = DateBuilder.futureDate(35, DateBuilder.IntervalUnit.SECOND);

    Trigger trigger =
        TriggerBuilder.newTrigger()
            .startAt(startDate)
            .withSchedule(
                SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(5).withRepeatCount(4))
            .endAt(endDate)
            .withPriority(2)
            .build();

    JobDataMap jobDataMapPriority = new JobDataMap();
    jobDataMapPriority.put("name", "lanvanderPriority");
    jobDataMapPriority.put("project", "framework");
    jobDataMapPriority.put("module", "quartz");

    JobDetail jobDetailPriority =
        JobBuilder.newJob(JavaJob.class)
            .withIdentity("jobNamePriority", "jobGroupNamePriority")
            .usingJobData(jobDataMapPriority)
            .build();

    Trigger triggerPriority =
        TriggerBuilder.newTrigger()
            .startAt(startDate)
            .withSchedule(
                SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(5).withRepeatCount(4))
            .endAt(endDate)
            .withPriority(5)
            .build();

    JobDataMap cronJobDataMap = new JobDataMap();
    cronJobDataMap.put("name", "lanvanderCron");
    cronJobDataMap.put("project", "framework");
    cronJobDataMap.put("module", "quartz");

    JobDetail cronJobDetail =
        JobBuilder.newJob(JavaJob.class)
            .withIdentity("cronJobName", "cronJobGroupName")
            .usingJobData(cronJobDataMap)
            .build();

    /*
     * cron表达式
     * 秒        分          小时        月内日期             月           周内日期             年(可选)
     * , - * /   , - * /    , - * /     , - * ? / W L C    , - * /     , - * ? / L C #     , - * /
     */
    Trigger cronTrigger =
        TriggerBuilder.newTrigger()
            .startAt(startDate)
            .withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 21 5 ? *"))
            .endAt(endDate)
            .withPriority(9)
            .build();

    //    scheduler.scheduleJob(jobDetail, trigger);
    //    scheduler.scheduleJob(jobDetailPriority, triggerPriority);
    scheduler.scheduleJob(cronJobDetail, cronTrigger);

    //    Thread.sleep(100000);

    //    scheduler.shutdown();
  }
}
