/**
 *
 * @author AALOZIE
 */

package org.fhi360.lamis.controller.report;

import java.util.*;
import com.opensymphony.xwork2.ActionSupport;
import org.fhi360.lamis.report.PatientReports;
import org.fhi360.lamis.report.PharmacySummaryProcessor;

public class PharmacyReportAction extends ActionSupport {
    private ArrayList<Map<String, Object>> reportList = new ArrayList<Map<String, Object>>();
    private HashMap parameterMap = new HashMap();
    
    public String patientsFirstLine() {
        PatientReports reportQuery = new PatientReports();
        reportList = reportQuery.patientsRegimen("first"); 
        parameterMap = reportQuery.getReportParameters(); 
        return SUCCESS;         
    }
    
    public String patientsSecondLine() {
        PatientReports reportQuery = new PatientReports();
        reportList = reportQuery.patientsRegimen("second"); 
        parameterMap = reportQuery.getReportParameters(); 
        return SUCCESS;         
    }

    public String patientsThird() {
        PatientReports reportQuery = new PatientReports();
        reportList = reportQuery.patientsRegimen("third"); 
        parameterMap = reportQuery.getReportParameters(); 
        return SUCCESS;         
    }
    
    public String regimenSummary() {
        PatientReports reportQuery = new PatientReports();
        reportList = reportQuery.regimenSummary(); 
        parameterMap = reportQuery.getReportParameters(); 
        return SUCCESS; 
    }
    
    public String devolvedSummary() {
        PatientReports reportQuery = new PatientReports();
        reportList = reportQuery.devolvedSummary(); 
        parameterMap = reportQuery.getReportParameters(); 
        return SUCCESS; 
    }

    public String dispensedSummary() {
        PharmacySummaryProcessor pharmacySummaryProcessor = new PharmacySummaryProcessor();
        reportList = pharmacySummaryProcessor.process(); 
        parameterMap = pharmacySummaryProcessor.getReportParameters(); 
        return SUCCESS; 
    }
    
    public String patientAdrReport() {
        //reportList = new AdrReportProcessor().process(); 
        //parameterMap = new AdrReportProcessor().getReportParameters(); 
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