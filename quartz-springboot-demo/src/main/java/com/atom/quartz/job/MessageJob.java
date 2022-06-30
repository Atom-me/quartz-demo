package com.atom.quartz.job;

import com.atom.quartz.model.Message;
import com.atom.quartz.respository.MessageRepository;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

/**
 * @author Atom
 */
public class MessageJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(MessageJob.class);

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        /* Get message id recorded by scheduler during scheduling */
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();

        String messageId = dataMap.getString("messageId");

        log.info("=============================================Executing job for message id {}", messageId);

        /* Get message from database by id */
        int id = Integer.parseInt(messageId);
        Optional<Message> messageOpt = messageRepository.findById(id);

        /* update message visible in database */
        Message message = messageOpt.get();
        message.setVisible(true);
        messageRepository.save(message);

        /* unschedule or delete after job gets executed */

        try {
            context.getScheduler().deleteJob(new JobKey(messageId));

            TriggerKey triggerKey = new TriggerKey(messageId);

            context.getScheduler().unscheduleJob(triggerKey);

        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

}