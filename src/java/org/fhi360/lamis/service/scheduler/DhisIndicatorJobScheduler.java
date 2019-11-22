package org.fhi360.lamis.service.scheduler;

import org.fhi360.lamis.service.indicator.DhisIndicatorService;
import org.fhi360.lamis.service.indicator.FacilityPerformanceService;
import org.fhi360.lamis.service.indicator.IndicatorService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class DhisIndicatorJobScheduler extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
//        IndicatorService service = new DhisIndicatorService();
//        FacilityPerformanceService performanceService = new FacilityPerformanceService(service);
//        performanceService.process();
    }
}
