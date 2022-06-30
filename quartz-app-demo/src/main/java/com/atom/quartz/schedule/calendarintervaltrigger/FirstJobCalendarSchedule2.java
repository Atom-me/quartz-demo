package com.atom.quartz.schedule.calendarintervaltrigger;

import com.atom.quartz.job.FirstJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

/**
 * 基于日历的调度
 * 类似与 SimpleTrigger,指定从某一个时间开始，以一定的时间间隔执行的任务，但是不同的是 SimpleTrigger指定的时间间隔为毫秒，没办法指定每隔一个月执行一次（每月的时间间隔不是固定值），
 * 而 CalendarIntervalTrigger 支持的间隔单位有 秒，分钟，小时，天，月，年，星期。
 *
 * @author Atom
 */
public class FirstJobCalendarSchedule2 {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirstJobCalendarSchedule2.class);

    public static void main(String[] args) throws SchedulerException, InterruptedException {
        //创建调度器
        Scheduler defaultScheduler = StdSchedulerFactory.getDefaultScheduler();
        //构建触发器
        CalendarIntervalTrigger calendarIntervalTrigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "triggerGroup1")
//                .startNow()//一旦加入scheduler，立即生效，即触发器开始时间
                .startAt(new GregorianCalendar(2022, 5, 29, 13, 48, 10).getTime())
                .endAt(new GregorianCalendar(2022, 5, 29, 15, 58, 10).getTime())
                .withSchedule(CalendarIntervalScheduleBuilder.calendarIntervalSchedule()
                        .withIntervalInSeconds(2)// 每2秒执行一次
//                        .withIntervalInDays(2)// 每2天执行一次
//                        .withIntervalInWeeks(1)// 每周执行一次
                ).build();

        //定义任务详情
        JobDetail firstJob = JobBuilder.newJob(FirstJob.class)
                .withIdentity("first job", "FirstJobGroup")
                .usingJobData("myJobData", "hello first quartz job")
                .build();

        //注册 任务 和 触发器 至调度器, 一个job,一个触发器，一对一关系。任何一方都不能一对多
        Date firstFireTime = defaultScheduler.scheduleJob(firstJob, calendarIntervalTrigger);
        LOGGER.info("the job first fire time {}", firstFireTime);
        //启动调度器，调度器内部注册的所有触发器开始计时
        defaultScheduler.start();

        //关闭调度器
        TimeUnit.SECONDS.sleep(10000);
        defaultScheduler.shutdown();


    }
}
