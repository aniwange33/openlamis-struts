/**
 *
 * @author AALOZIE
 */

package org.fhi360.lamis.controller.report;

import java.util.*;
import com.opensymphony.xwork2.ActionSupport;
import org.fhi360.lamis.report.ArtAddendumSummaryProcessor;
import org.fhi360.lamis.report.ArtSummaryProcessor;
import org.fhi360.lamis.report.ArtSummaryProcessor_1;
import org.fhi360.lamis.report.PatientReports;
import org.fhi360.lamis.report.CohortAnalysisProcessor;
import org.fhi360.lamis.report.ServiceSummaryProcessor;
import org.fhi360.lamis.report.PerformanceIndicatorProcessor;
import org.fhi360.lamis.report.QualityIndicatorProcessor;
import org.fhi360.lamis.report.TxMlSummaryProcessor;
import org.fhi360.lamis.report.UnbundledArtSummaryProcessor;

public class PatientReportAction extends ActionSupport {
    private ArrayList<Map<String, Object>> reportList = new ArrayList<>();
    private HashMap parameterMap = new HashMap();
    
    public String listOfPatients() {
        PatientReports reportQuery = new PatientReports();
        reportList = reportQuery.listOfPatients(); 
        parameterMap = reportQuery.getReportParameters(); 
        return SUCCESS;         
    }
    public String listOfPatientsNotification() {
        PatientReports reportQuery = new PatientReports();
        reportList = reportQuery.listOfPatientsNotification(); 
        parameterMap = reportQuery.getReportParameters();
        return SUCCESS;         
    }

    public String caseManagerClientsList() { 
        PatientReports reportQuery = new PatientReports();
        reportList = reportQuery.caseManagerClientsList(); 
        parameterMap = reportQuery.getReportParameters();
        return SUCCESS;         
    }

    public String currentOnCare() {
        PatientReports reportQuery = new PatientReports();
        reportList = reportQuery.currentOnCare(); 
        parameterMap = reportQuery.getReportParameters(); 
        return SUCCESS;         
    }

    public String currentOnTreatment() {
        PatientReports reportQuery = new PatientReports();
        reportList = reportQuery.currentOnTreatment();
        parameterMap = reportQuery.getReportParameters(); 
        return SUCCESS;         
    }
    
    public String appointment() {
        PatientReports reportQuery = new PatientReports();
        reportList = reportQuery.appointment(); 
        parameterMap = reportQuery.getReportParameters(); 
        return SUCCESS;         
    }
    
    public String clientAppointment() {
        PatientReports reportQuery = new PatientReports();
        reportList = reportQuery.clientAppointment(); 
        parameterMap = reportQuery.getReportParameters(); 
        return SUCCESS;         
    }
    
    public String visit() {
        PatientReports reportQuery = new PatientReports();
        reportList = reportQuery.visit(); 
        parameterMap = reportQuery.getReportParameters(); 
        return SUCCESS;         
    }
    
    public String defaulters() {
        PatientReports reportQuery = new PatientReports();
        reportList = reportQuery.defaulters(); 
        parameterMap = reportQuery.getReportParameters(); 
        return SUCCESS;         
    }
    
    public String clientDefaulterRefill() {
        PatientReports reportQuery = new PatientReports();
        reportList = reportQuery.clientDefaulterRefill(); 
        parameterMap = reportQuery.getReportParameters(); 
        return SUCCESS;         
    }

    public String lostUnconfirmedPepfar() {
        PatientReports reportQuery = new PatientReports();
        reportList = reportQuery.lostUnconfirmedPEPFAR(); 
        parameterMap = reportQuery.getReportParameters(); 
        return SUCCESS;         
    }
    
    public String lostUnconfirmedGon() {
        PatientReports reportQuery = new PatientReports();
        reportList = reportQuery.lostUnconfirmedGON(); 
        parameterMap = reportQuery.getReportParameters(); 
        return SUCCESS;         
    }

    public String defaulterRefill() {
        PatientReports reportQuery = new PatientReports();
        reportList = reportQuery.defaulterRefill(); 
        parameterMap = reportQuery.getReportParameters(); 
        return SUCCESS;         
    }
    

    public String listOfTxml() {
        PatientReports reportQuery = new PatientReports();
        reportList = reportQuery.trackingOutcome(); 
        parameterMap = reportQuery.getReportParameters(); 
        return SUCCESS;         
    }

    public String artSummaryOld() {
        ArtSummaryProcessor_1 artSummaryProcessor = new ArtSummaryProcessor_1();
        reportList = artSummaryProcessor.process(); 
        parameterMap = artSummaryProcessor.getReportParameters(); 
        return SUCCESS; 
    }

    public String unbundledArtSummary() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("stub", "");
        reportList.add(map);
        parameterMap = new UnbundledArtSummaryProcessor().process(); 
        return SUCCESS; 
    }

    public String artSummary() {
    Map<String, Object> map = new HashMap<String, Object>();
        map.put("stub", "");
        reportList.add(map);
        parameterMap = new ArtSummaryProcessor().process(); 
        return SUCCESS; 
    }

    public String txMlSummary() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("stub", "");
        reportList.add(map);
        parameterMap = new TxMlSummaryProcessor().process(); 
        return SUCCESS; 
    }
    
    public String artAddendumSummary() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("stub", "");
        reportList.add(map);
        parameterMap = new ArtAddendumSummaryProcessor().process(); 
        return SUCCESS; 
    }
    
    
    
    
    public String serviceSummary() {
        ServiceSummaryProcessor serviceSummaryProcessor = new ServiceSummaryProcessor();
        reportList = serviceSummaryProcessor.process(); 
        parameterMap = serviceSummaryProcessor.getReportParameters(); 
        return SUCCESS; 
    }
    
    public String qualityIndicator() {
        QualityIndicatorProcessor qualityIndicatorProcessor = new QualityIndicatorProcessor();
        reportList = qualityIndicatorProcessor.process(); 
        parameterMap = qualityIndicatorProcessor.getReportParameters(); 
        return SUCCESS; 
    }
    
    public String cohortAnalysis() {
        CohortAnalysisProcessor cohortAnalysisProcessor = new CohortAnalysisProcessor();
        reportList = cohortAnalysisProcessor.process(); 
        parameterMap = cohortAnalysisProcessor.getReportParameters(); 
        return SUCCESS; 
    }
    
    public String performanceIndicator() {
        PerformanceIndicatorProcessor performanceIndicatorProcessor = new PerformanceIndicatorProcessor();
        reportList = performanceIndicatorProcessor.process(); 
        parameterMap = performanceIndicatorProcessor.getReportParameters(); 
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