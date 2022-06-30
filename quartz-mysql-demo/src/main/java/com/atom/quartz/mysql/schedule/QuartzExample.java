package com.atom.quartz.mysql.schedule;

import com.atom.quartz.mysql.job.SimpleJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

/**
 * @author Atom
 */
public class QuartzExample {

    public void run() throws SchedulerException, InterruptedException {
        // create the scheduler
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();

        // define the job and tie it to the SimpleJob class
        JobDetail job = JobBuilder.newJob(SimpleJob.class)
                .withIdentity("myJob", "myGroup")
                .build();

        // create the trigger and define its schedule to run every 3 seconds
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("myTrigger", "myGroup")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(3)
                        .repeatForever())
                .build();

        // add the job details to the scheduler and associate it with the trigger
        scheduler.scheduleJob(job, trigger);

        // start the scheduler
        scheduler.start();

        // wait long enough to see the job execution
        Thread.sleep(5 * 1000);

        // shutdown the scheduler
        scheduler.shutdown(true);

    }

    public static void main(String[] args) throws SchedulerException, InterruptedException {
        QuartzExample example = new QuartzExample();
        example.run();
    }
}
