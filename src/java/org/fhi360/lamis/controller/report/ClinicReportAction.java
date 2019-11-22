/**
 *
 * @author AALOZIE
 */

package org.fhi360.lamis.controller.report;

import java.util.*;
import com.opensymphony.xwork2.ActionSupport;
import org.fhi360.lamis.report.ClinicReports;

public class ClinicReportAction extends ActionSupport {
    private ArrayList<Map<String, Object>> reportList = new ArrayList<>();
    private HashMap parameterMap = new HashMap();
    
    public String eligibleForART() {
        ClinicReports clinicReports = new ClinicReports();
        reportList = clinicReports.eligibleForART(); 
        parameterMap = clinicReports.getReportParameters(); 
        return SUCCESS;         
    }

    public String cd4Due() {
        System.out.println("CD4 due");
        ClinicReports clinicReports = new ClinicReports();
        reportList = clinicReports.cd4Due(); 
        parameterMap = clinicReports.getReportParameters(); 
        return SUCCESS;         
    }
    
    public String clientsCd4Due() {
        ClinicReports clinicReports = new ClinicReports();
        reportList = clinicReports.clientsCd4Due(); 
        parameterMap = clinicReports.getReportParameters(); 
        return SUCCESS;         
    }

    public String cd4LessBaseline() {
        ClinicReports clinicReports = new ClinicReports();
        reportList = clinicReports.cd4LessBaseline(); 
        parameterMap = clinicReports.getReportParameters(); 
        return SUCCESS;         
    }

    public String viralLoadDue() {
        System.out.println("Viral due");
        ClinicReports clinicReports = new ClinicReports();
        reportList = clinicReports.viralLoadDue(); 
        parameterMap = clinicReports.getReportParameters(); 
        return SUCCESS;         
    }
    
    public String unassignedClients() {
        ClinicReports clinicReports = new ClinicReports();
        reportList = clinicReports.unassignedClients(); 
        parameterMap = clinicReports.getReportParameters(); 
        return SUCCESS;         
    }
    
    public String clientsViralLoadDue() {
        ClinicReports clinicReports = new ClinicReports();
        reportList = clinicReports.clientsViralLoadDue(); 
        parameterMap = clinicReports.getReportParameters(); 
        return SUCCESS;         
    }
    public String baselineViralLoadDue() {
        System.out.println("Got Here!!!");
        ClinicReports clinicReports = new ClinicReports();
        reportList = clinicReports.baselineViralLoadDue(); 
        parameterMap = clinicReports.getReportParameters(); 
        return SUCCESS;         
    }
    
    public String secondViralLoadDue() {
        //System.out.println("Viral due");
        ClinicReports clinicReports = new ClinicReports();
        reportList = clinicReports.secondViralLoadDue(); 
        parameterMap = clinicReports.getReportParameters(); 
        return SUCCESS;         
    }
    
    public String routineViralLoadDue() {
        //System.out.println("Viral due");
        ClinicReports clinicReports = new ClinicReports();
        reportList = clinicReports.routineViralLoadDue(); 
        parameterMap = clinicReports.getReportParameters(); 
        return SUCCESS;         
    }
    
    public String repeatViralLoadDue() {
        //System.out.println("Viral due");
        ClinicReports clinicReports = new ClinicReports();
        reportList = clinicReports.repeatViralLoadDue(); 
        parameterMap = clinicReports.getReportParameters(); 
        return SUCCESS;         
    }
    
    public String viralLoadSupressed() {
        ClinicReports clinicReports = new ClinicReports();
        reportList = clinicReports.viralLoadSupressed(); 
        parameterMap = clinicReports.getReportParameters(); 
        return SUCCESS;         
    }

    public String viralLoadUnsupressed() {
                System.out.println("Unsupressed due");

        ClinicReports clinicReports = new ClinicReports();
        reportList = clinicReports.viralLoadUnsupressed(); 
        parameterMap = clinicReports.getReportParameters(); 
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