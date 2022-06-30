package com.atom.quartz.controller;

import com.atom.quartz.service.QuartzService;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Atom
 */
@RestController
public class QuartzController {

    @Resource
    private QuartzService quartzService;

    /**
     * curl --location --request GET 'localhost:8080/insert?jName=aaaaa&jGroup=bbbbb&tName=com.atom.quartz.job.TestJob&tGroup=test&cron=0,10,20,30,40,50 * * * * ?'
     * 新增任务
     */
    @GetMapping("/insert")
    public String insertTask(String jName, String jGroup, String tName, String tGroup, String cron) {
        quartzService.addJob(jName, jGroup, tName, tGroup, cron);
        return "添加成功！";
    }

    /**
     * curl --location --request GET 'localhost:8080/pause?jName=aaaaa&jGroup=bbbbb'
     * 暂停任务
     */
    @GetMapping("/pause")
    public String pauseTask(String jName, String jGroup) {
        quartzService.pauseJob(jName, jGroup);
        return "暂停成功！";
    }

    /**
     * curl --location --request GET 'localhost:8080/resume?jName=aaaaa&jGroup=bbbbb'
     * 继续任务
     */
    @GetMapping("/resume")
    public String resumeTask(String jName, String jGroup) {
        quartzService.resumeJob(jName, jGroup);
        return "继续成功！";
    }

    /**
     * curl --location --request GET 'localhost:8080/delete?jName=aaaaa&jGroup=bbbbb'
     * 删除任务
     */
    @GetMapping("/delete")
    public String deleteTask(String jName, String jGroup) {
        quartzService.deleteJob(jName, jGroup);
        return "删除成功！";
    }


    /**
     * curl --location --request GET 'localhost:8080/modify?jName=aaaaa&jGroup=bbbbb&cron=0/5 * * * * ?'
     * 修改任务调度配置
     */
    @GetMapping("/modify")
    public String modifyTask(String jName, String jGroup, String cron) throws SchedulerException {
        quartzService.modifyJob(jName, jGroup, cron);
        return "修改成功！";
    }
}
