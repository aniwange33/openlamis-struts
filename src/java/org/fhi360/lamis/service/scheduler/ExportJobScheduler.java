/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.scheduler;

import java.util.Date;
import java.util.Map;
import org.fhi360.lamis.controller.AutoSyncAction;
import org.fhi360.lamis.utility.PropertyAccessor;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 *
 * @author user10
 */
public class ExportJobScheduler extends QuartzJobBean { 
    
    @Override
    protected void executeInternal(JobExecutionContext jec) throws JobExecutionException {
        try {
            
            //Do export
        }
        catch (Exception exception) {
        }
    }

    
}
