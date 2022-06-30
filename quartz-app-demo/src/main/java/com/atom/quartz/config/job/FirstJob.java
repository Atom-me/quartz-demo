package com.atom.quartz.config.job;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * job类的具体实现，即需要定时执行的具体 某个任务。
 *
 * @author Atom
 */
public class FirstJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirstJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDetail jobDetail = jobExecutionContext.getJobDetail();
        JobDataMap mergeMap = jobExecutionContext.getTrigger().getJobDataMap();
        // 任务名称
        String jobName = jobDetail.getKey().getName();
        //任务组名
        String jobGroup = jobDetail.getKey().getGroup();
        // 任务中的数据
        String jobData = jobDetail.getJobDataMap().getString("myJobData");

        LOGGER.info("first job 执行， job名称：{}, jobGroup：{},jobData：{}, 当前时间：{}", jobName, jobGroup, jobData, LocalDateTime.now().toLocalTime());
        LOGGER.info("mergeMap {}", mergeMap.getString("myJobData"));


    }
}
