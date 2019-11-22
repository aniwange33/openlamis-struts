/**
 * @author AALOZIE
 */
package org.fhi360.lamis.service.scheduler;

import java.util.Date;

import org.fhi360.lamis.service.SyncService;
import org.fhi360.lamis.utility.PropertyAccessor;

import java.util.Map;

import org.fhi360.lamis.controller.AutoSyncAction;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class DataSyncJobScheduler extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext jec) throws JobExecutionException {
        //This job is executed every 3 hours triggered by the syncCronTrigger in the applicationContext.xml
        //The startTransaction() method loops through all available upload folders, check if the webservice has 
        //transfered xml files into the folder and locked the folder.
        //This job is executed only on the server 
        try {
            Map<String, Object> map = new PropertyAccessor().getSystemProperties();
            String appInstance = (String) map.get("appInstance");
            String enableSync = (String) map.get("enableSync");
            if (appInstance.equalsIgnoreCase("server") && enableSync.equalsIgnoreCase("1")) {
                Date threadDate = new Date();
                System.out.println("Started From the job scheduler at: " + threadDate);
                SyncService syncService = new SyncService();
                syncService.startTransaction();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
