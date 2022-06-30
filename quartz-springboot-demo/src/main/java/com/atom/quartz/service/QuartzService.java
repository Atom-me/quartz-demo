package com.atom.quartz.service;

import com.alibaba.druid.util.StringUtils;
import org.quartz.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author Atom
 */
@Service
public class QuartzService {
    @Resource
    private Scheduler scheduler;

    /**
     * 新增一个定时任务
     *
     * @param jName  任务名称
     * @param jGroup 任务组
     * @param tName  触发器名称。即className。即实现了job的任务全包名
     * @param tGroup 触发器组
     * @param cron   cron表达式
     */
    public void addJob(String jName, String jGroup, String tName, String tGroup, String cron) {
        try {
            //根据报名tName即className映射java类
            Class<? extends Job> clazz = (Class<? extends Job>) Class.forName(tName);

            //将实现job的任务类放到任务描述中
            JobDetail jobDetail = JobBuilder.newJob(clazz)
                    .withIdentity(jName, jGroup)
                    .build();

            //给数据库cronTrigger表添加任务记录
            CronTrigger trigger = newTrigger()
                    .withIdentity(tName, tGroup)
                    .startNow()
                    .withSchedule(cronSchedule(cron))
                    .build();

            scheduler.start();
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 暂停定时任务
     *
     * @param jName  任务名
     * @param jGroup 任务组
     */
    public void pauseJob(String jName, String jGroup) {
        try {
            scheduler.pauseJob(JobKey.jobKey(jName, jGroup));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 继续定时任务
     *
     * @param jName  任务名
     * @param jGroup 任务组
     */
    public void resumeJob(String jName, String jGroup) {
        try {
            scheduler.resumeJob(JobKey.jobKey(jName, jGroup));
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除定时任务
     *
     * @param jName  任务名
     * @param jGroup 任务组
     */
    public void deleteJob(String jName, String jGroup) {
        try {
            scheduler.deleteJob(JobKey.jobKey(jName, jGroup));
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public void modifyJob(String jName, String jGroup, String cron) throws SchedulerException {

        JobKey jobKey = new JobKey(jName, jGroup);
        if (!scheduler.checkExists(jobKey)) {
            throw new RuntimeException("job do not exists...");
        }
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);

        TriggerKey triggerKey = TriggerKey.triggerKey(jName, jGroup);

        // 按新的cronExpression表达式重新构建trigger
        CronTrigger cronTrigger = newTrigger()
                .withIdentity(triggerKey)
                .withSchedule(cronSchedule(cron))
                .forJob(jobDetail)
                .build();

        if (scheduler.checkExists(triggerKey)) {
            CronTrigger oldCronTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            String oldCronExpression = oldCronTrigger.getCronExpression();
            if (!StringUtils.equalsIgnoreCase(cron, oldCronExpression)) {
                // reschedule job trigger
                scheduler.rescheduleJob(triggerKey, cronTrigger);
            }
        } else {
            scheduler.scheduleJob(cronTrigger);
        }
    }
}
