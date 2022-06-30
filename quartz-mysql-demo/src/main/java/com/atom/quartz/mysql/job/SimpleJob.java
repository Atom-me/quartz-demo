package com.atom.quartz.mysql.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Atom
 */
public class SimpleJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        LOGGER.info("========================simple job executed!======================================");
    }
}
