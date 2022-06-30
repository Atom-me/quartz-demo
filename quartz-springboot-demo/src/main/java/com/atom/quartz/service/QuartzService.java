package com.atom.quartz.service;

import org.quartz.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(tName, tGroup)
                    .startNow()
                    .withSchedule(CronScheduleBuilder.cronSchedule(cron))
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
}
