/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.controller.report;

import static com.opensymphony.xwork2.Action.SUCCESS;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.fhi360.lamis.report.CommunitypharmSummaryProcessor;

/**
 *
 * @author user1
 */
public class CommunitypharmReportAction {
    private ArrayList<Map<String, Object>> reportList = new ArrayList<>();
    private HashMap parameterMap = new HashMap();

    public String communitypharmSummary() {
        CommunitypharmSummaryProcessor communitypharmSummaryProcessor = new CommunitypharmSummaryProcessor();
        reportList = communitypharmSummaryProcessor.process(); 
        parameterMap = communitypharmSummaryProcessor.getReportParameters(); 
        return SUCCESS; 
    }

    /**
     * @return the reportList
     */
    public ArrayList<Map<String, Object>> getReportList() {
        return reportList;
    }

    /**
     * @param reportList the reportList to set
     */
    public void setReportList(ArrayList<Map<String, Object>> reportList) {
        this.reportList = reportList;
    }

    /**
     * @return the parameterMap
     */
    public HashMap getParameterMap() {
        return parameterMap;
    }

    /**
     * @param parameterMap the parameterMap to set
     */
    public void setParameterMap(HashMap parameterMap) {
        this.parameterMap = parameterMap;
    }
    
}
