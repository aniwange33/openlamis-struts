package org.fhi360.lamis.service.scheduler;

import org.fhi360.lamis.service.indicator.FacilityPerformanceService;
import org.fhi360.lamis.service.indicator.IndicatorService;
import org.fhi360.lamis.service.indicator.TreatmentIndicatorService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class TreatmentIndicatorJobScheduler extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        IndicatorService service = new TreatmentIndicatorService();
        FacilityPerformanceService performanceService = new FacilityPerformanceService(service);
        performanceService.process();
    }
}
