package com.atom.quartz.schedule.dailytimeintervaltrigger;

import com.atom.quartz.job.FirstJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

/**
 * 指定每天的某个时间段内，以一定的时间间隔执行任务，并且它可以支持指定星期。
 * 它适合的任务类似于：指定每天9：00 至 18：00 ，每隔70秒执行一次，并且只要周一至周五执行。
 * 它的属性有：
 * startTimeOfDay 每天开始时间
 * endTimeOfDay 每天结束时间
 * daysOfWeek 需要执行的星期
 * interval 执行间隔
 * intervalUnit 执行间隔的单位（秒，分钟，小时，天，月，年，星期）
 * repeatCount 重复次数
 * <p>
 * trigger 的 开始和结束时间 会覆盖 ScheduleBuilder 的 开始/结束时间。
 *
 * @author Atom
 */
public class FirstJobDailyTimeIntervalSchedule2 {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirstJobDailyTimeIntervalSchedule2.class);

    public static void main(String[] args) throws SchedulerException, InterruptedException {
        //创建调度器
        Scheduler defaultScheduler = StdSchedulerFactory.getDefaultScheduler();
        //构建触发器
        DailyTimeIntervalTrigger dailyTimeIntervalTrigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "triggerGroup1")
//                .startNow()//一旦加入scheduler，立即生效，即触发器开始时间
                .startAt(new GregorianCalendar(2022, 5, 29, 1, 48, 10).getTime())
                .endAt(new GregorianCalendar(2022, 5, 29, 17, 6, 10).getTime())
                .withSchedule(DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule()
                        // 每天9：00开始
                        .startingDailyAt(TimeOfDay.hourAndMinuteOfDay(9, 0))
                        // 每天18：00结束
                        .endingDailyAt(TimeOfDay.hourAndMinuteOfDay(17, 5))
                        // 每周一至周五执行
                        .onDaysOfTheWeek(DateBuilder.MONDAY, DateBuilder.TUESDAY, DateBuilder.WEDNESDAY, DateBuilder.THURSDAY, DateBuilder.FRIDAY)
                        //每隔1秒执行一次
                        .withIntervalInSeconds(1)
                        //最多重复100次（实际执行100+1次）
                        .withRepeatCount(100)
                ).build();

        //定义任务详情
        JobDetail firstJob = JobBuilder.newJob(FirstJob.class)
                .withIdentity("first job", "FirstJobGroup")
                .usingJobData("myJobData", "hello first quartz job")
                .build();

        //注册 任务 和 触发器 至调度器, 一个job,一个触发器，一对一关系。任何一方都不能一对多
        Date firstFireTime = defaultScheduler.scheduleJob(firstJob, dailyTimeIntervalTrigger);
        LOGGER.info("the job first fire time {}", firstFireTime);
        //启动调度器，调度器内部注册的所有触发器开始计时
        defaultScheduler.start();

        //关闭调度器
        TimeUnit.SECONDS.sleep(10000);
        defaultScheduler.shutdown();


    }
}
