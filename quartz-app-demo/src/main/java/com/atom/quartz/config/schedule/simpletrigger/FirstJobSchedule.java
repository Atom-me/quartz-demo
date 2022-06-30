package com.atom.quartz.config.schedule.simpletrigger;

import com.atom.quartz.config.job.FirstJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Atom
 */
public class FirstJobSchedule {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirstJobSchedule.class);

    public static void main(String[] args) throws SchedulerException, InterruptedException {
        //创建调度器
        Scheduler defaultScheduler = StdSchedulerFactory.getDefaultScheduler();
        //构建触发器
        SimpleTrigger simpleTrigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "triggerGroup1")
//                .withIdentity(new TriggerKey("trigger1", "triggerGroup1"))
                .startNow()//一旦加入scheduler，立即生效，即触发器开始时间
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(2)
//                        .repeatForever()
                                .withRepeatCount(10)
                )
                .usingJobData("myJobData","mytrigger data")
                .build();

        //定义任务详情
        JobDetail firstJob = JobBuilder.newJob(FirstJob.class)
                .withIdentity("first job","FirstJobGroup")
//                .withIdentity(new JobKey("first job","FirstJobGroup"))
                .usingJobData("myJobData","hello first quartz job")
                .build();


        //注册 任务 和 触发器 至调度器, 一个job,一个触发器，一对一关系。任何一方都不能一对多
        Date firstFireTime = defaultScheduler.scheduleJob(firstJob, simpleTrigger);
        LOGGER.info("the job first fire time {}",firstFireTime);
        //启动调度器，调度器内部注册的所有触发器开始计时
        defaultScheduler.start();

        //关闭调度器
        TimeUnit.SECONDS.sleep(100);
        defaultScheduler.shutdown();


    }
}
