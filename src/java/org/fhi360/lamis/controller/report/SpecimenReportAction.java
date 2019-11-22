/**
 *
 * @author AALOZIE
 */

package org.fhi360.lamis.controller.report;

import org.fhi360.lamis.report.PcrLabSummaryProcessor;
import java.util.*;
import com.opensymphony.xwork2.ActionSupport;
import org.fhi360.lamis.report.SpecimenReports;

public class SpecimenReportAction extends ActionSupport {
    private ArrayList<Map<String, Object>> reportList = new ArrayList<Map<String, Object>>();
    private HashMap parameterMap = new HashMap();
    
    public String eidRegister() {
        SpecimenReports specimenReports = new SpecimenReports();
        reportList = specimenReports.eidRegister(); 
        parameterMap = specimenReports.getReportParameters(); 
        return SUCCESS; 
    }

    public String eidSummary() {
        PcrLabSummaryProcessor eidSummaryProcessor = new PcrLabSummaryProcessor();
        reportList = eidSummaryProcessor.process(); 
        parameterMap = eidSummaryProcessor.getReportParameters(); 
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