/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.controller.chart.treatment;

import static com.opensymphony.xwork2.Action.SUCCESS;
import com.opensymphony.xwork2.ActionSupport;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.chart.treatment.TreatmentSummaryChartService;
import org.fhi360.lamis.utility.ChartUtil;
import org.fhi360.lamis.utility.DateUtil;

/**
 *
 * @author user10
 */

public class TreatmentSummaryChartAction extends ActionSupport {
    private static final String DATA_ELEMENT_IDS = "35,11,1";   
    
    private ArrayList<Map<String, Object>> indicator;

    public String chartData() {
        HttpServletRequest request = ServletActionContext.getRequest();
        long ipId = Long.parseLong(request.getParameter("ipId"));
        long stateId = Long.parseLong(request.getParameter("stateId"));
        long lgaId = Long.parseLong(request.getParameter("lgaId"));
        long facilityId = Long.parseLong(request.getParameter("facilityId"));
        ChartUtil chartUtil = new ChartUtil();
        
        try {
                Date today = new Date();
                Date reportingDateBegin = (!request.getParameter("reportingDateBegin").isEmpty()) ? DateUtil.parseStringToDate(request.getParameter("reportingDateBegin"), "yyyy-MM-dd") : today;
                Date reportingDateEnd = (!request.getParameter("reportingDateEnd").isEmpty()) ? DateUtil.parseStringToDate(request.getParameter("reportingDateEnd"), "yyyy-MM-dd") : today;

                indicator = new TreatmentSummaryChartService().chartData(ipId, stateId, lgaId, facilityId, DATA_ELEMENT_IDS, reportingDateBegin, reportingDateEnd);

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return SUCCESS;
    }

    /**
     * @return the indicator
     */
    public ArrayList<Map<String, Object>> getIndicator() {
        return indicator;
    }

    /**
     * @param indicator the indicator to set
     */
    public void setIndicator(ArrayList<Map<String, Object>> indicator) {
        this.indicator = indicator;
    }

}
