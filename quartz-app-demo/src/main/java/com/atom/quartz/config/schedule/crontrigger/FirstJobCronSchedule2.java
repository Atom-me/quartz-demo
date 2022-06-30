package com.atom.quartz.config.schedule.crontrigger;

import com.atom.quartz.config.job.FirstJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

/**
 * 适合于更复杂的任务，它支持类似于 Linux cron 的语法（并且更强大）。基本上它覆盖了以上 simpleTriger ,CalendarIntervalTriger ,DailyTimeIntervalTriger的绝大部分能力（但不是全部）
 *
 * 它适合的任务类似于：每天 0:00 9:00 18:00 各执行一次。
 * 它的属性只有 cron表达式。
 *
 * @author Atom
 */
public class FirstJobCronSchedule2 {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirstJobCronSchedule2.class);

    public static void main(String[] args) throws SchedulerException, InterruptedException {
        //创建调度器
        Scheduler defaultScheduler = StdSchedulerFactory.getDefaultScheduler();
        //构建触发器
        CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "triggerGroup1")
//                .startNow()//一旦加入scheduler，立即生效，即触发器开始时间
                .startAt(new GregorianCalendar(2022, 5, 29, 1, 48, 10).getTime())
                .endAt(new GregorianCalendar(2022, 5, 29, 19, 6, 10).getTime())
                // 每天 10：00 - 19：00 ,每隔1分钟执行一次
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/1 10-19 * * ?")
                ).build();

        //定义任务详情
        JobDetail firstJob = JobBuilder.newJob(FirstJob.class)
                .withIdentity("first job", "FirstJobGroup")
                .usingJobData("myJobData", "hello first quartz job")
                .build();

        //注册 任务 和 触发器 至调度器, 一个job,一个触发器，一对一关系。任何一方都不能一对多
        Date firstFireTime = defaultScheduler.scheduleJob(firstJob, cronTrigger);
        LOGGER.info("the job first fire time {}", firstFireTime);
        //启动调度器，调度器内部注册的所有触发器开始计时
        defaultScheduler.start();

        //关闭调度器
        TimeUnit.SECONDS.sleep(10000);
        defaultScheduler.shutdown();


    }
}
