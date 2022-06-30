package com.atom.quartz.controller;

import com.atom.quartz.config.QuartzConfig;
import com.atom.quartz.dto.MessageDto;
import com.atom.quartz.job.MessageJob;
import com.atom.quartz.model.Message;
import com.atom.quartz.respository.MessageRepository;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

/**
 * @author Atom
 */
@RestController
@RequestMapping(path = "/messages")
public class MessageSchedulingController {

    @Autowired
    private QuartzConfig quartzConfig;
    @Autowired
    private MessageRepository messageRepository;

    @PostMapping(path = "/schedule-visibility")
    public @ResponseBody MessageDto scheduleMessageVisibility(@RequestBody MessageDto messageDto) {
        try {
            // save messages in table
            Message message = new Message();
            message.setContent(messageDto.getContent());
            message.setVisible(false);
            message.setMakeVisibleAt(messageDto.getMakeVisibleAt());

            message = messageRepository.save(message);

            // Creating JobDetail instance
            String id = String.valueOf(message.getId());
            JobDetail jobDetail = JobBuilder.newJob(MessageJob.class).withIdentity(id).build();

            // Adding JobDataMap to jobDetail
            jobDetail.getJobDataMap().put("messageId", id);

            // Scheduling time to run job
            Date triggerJobAt = new Date(message.getMakeVisibleAt());

            SimpleTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(id)
                    .startAt(triggerJobAt)
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withMisfireHandlingInstructionFireNow())
                    .build();
            // Getting scheduler instance
            Scheduler scheduler = quartzConfig.schedulerFactoryBean().getScheduler();
            scheduler.scheduleJob(jobDetail, trigger);
            scheduler.start();

            messageDto.setStatus("SUCCESS");

        } catch (IOException | SchedulerException e) {
            // scheduling failed
            messageDto.setStatus("FAILED");
            e.printStackTrace();
        }
        return messageDto;
    }


    @DeleteMapping(path = "/{messageId}/unschedule-visibility")
    public @ResponseBody MessageDto unscheduleMessageVisibility(
            @PathVariable(name = "messageId") Integer messageId) {

        MessageDto messageDto = new MessageDto();

        Optional<Message> messageOpt = messageRepository.findById(messageId);
        if (!messageOpt.isPresent()) {
            messageDto.setStatus("Message Not Found");
            return messageDto;
        }

        Message message = messageOpt.get();
        message.setVisible(false);
        messageRepository.save(message);

        String id = String.valueOf(message.getId());

        try {
            Scheduler scheduler = quartzConfig.schedulerFactoryBean().getScheduler();

            scheduler.deleteJob(new JobKey(id));
            TriggerKey triggerKey = new TriggerKey(id);
            scheduler.unscheduleJob(triggerKey);
            messageDto.setStatus("SUCCESS");

        } catch (IOException | SchedulerException e) {
            messageDto.setStatus("FAILED");
            e.printStackTrace();
        }
        return messageDto;
    }
}
