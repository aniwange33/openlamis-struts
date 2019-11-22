/**
 *
 * @author AALOZIE
 */

package org.fhi360.lamis.exchange.nigqual;

import java.util.*;
import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class NigQualAction extends ActionSupport {
    private Integer page;
    private Integer rows;
    private String sidx;
    private String sord;
    private Integer totalpages;
    private Integer currpage;
    private Integer totalrecords;

    private String thermaticArea;
    private String status;
    private String fileName;
    
    private ArrayList<Map<String, String>> nigqualList = new ArrayList<Map<String, String>>();
    private ArrayList<Map<String, String>> cohortList = new ArrayList<Map<String, String>>();

    private ArrayList<Map<String, Object>> reportList = new ArrayList<Map<String, Object>>();
    private HashMap parameterMap = new HashMap();
   
    public String nigQualGrid() {
        //setTotalpages(1);
        //setCurrpage(1);
        //setTotalrecords(20);
        if(ServletActionContext.getRequest().getParameterMap().containsKey("detail")) {
            setCohortList(new NigQualGridBuilder().cohortList());
        }
        else {
            setNigqualList(new NigQualGridBuilder().nigQualList());
        }
        return SUCCESS;
    }

    public String generateCohort() {
        new NigQualService().generateCohort();
        return SUCCESS;        
    }
    
    public String dispatcher() {       
        if(thermaticArea.equalsIgnoreCase("AD")) {
            new PatientDemographyConverter().convertXml(thermaticArea); 
            new AdultDataConverter().convertXml(); 
        }
        if(thermaticArea.equalsIgnoreCase("PD")) {
            new PatientDemographyConverter().convertXml(thermaticArea); 
            new PediatricsDataConverter().convertXml(); 
        }
        if(thermaticArea.equalsIgnoreCase("PM")) {
            new PatientDemographyConverter().convertXml(thermaticArea); 
            new PmtctDataConverter().convertXml();               
        }
        return SUCCESS;        
    }
    
    public String nigqualReport() {
        NigQualReports nigQualReports = new NigQualReports();
        reportList = nigQualReports.nigqualReport(); 
        parameterMap = nigQualReports.getReportParameters(); 
        return SUCCESS;         
    }

    /**
     * @return the page
     */
    public Integer getPage() {
        return page;
    }

    /**
     * @param page the page to set
     */
    public void setPage(Integer page) {
        this.page = page;
    }

    /**
     * @return the limit
     */
    public Integer getRows() {
        return rows;
    }

    /**
     * @param limit the limit to set
     */
    public void setRows(Integer rows) {
        this.rows = rows;
    }

    /**
     * @return the sidx
     */
    public String getSidx() {
        return sidx;
    }

    /**
     * @param sidx the sidx to set
     */
    public void setSidx(String sidx) {
        this.sidx = sidx;
    }

    /**
     * @return the sord
     */
    public String getSord() {
        return sord;
    }

    /**
     * @param sord the sord to set
     */
    public void setSord(String sord) {
        this.sord = sord;
    }

    /**
     * @return the totalpages
     */
    public Integer getTotalpages() {
        return totalpages;
    }

    /**
     * @param totalpages the totalpages to set
     */
    public void setTotalpages(Integer totalpages) {
        this.totalpages = totalpages;
    }

    /**
     * @return the currpage
     */
    public Integer getCurrpage() {
        return currpage;
    }

    /**
     * @param currpage the currpage to set
     */
    public void setCurrpage(Integer currpage) {
        this.currpage = currpage;
    }

    /**
     * @return the totalrecords
     */
    public Integer getTotalrecords() {
        return totalrecords;
    }

    /**
     * @param totalrecords the totalrecords to set
     */
    public void setTotalrecords(Integer totalrecords) {
        this.totalrecords = totalrecords;
    }
    
    /**
     * @return the nigqualList
     */
    public ArrayList<Map<String, String>> getNigqualList() {
        return nigqualList;
    }

    /**
     * @param nigqualList the nigqualList to set
     */
    public void setNigqualList(ArrayList<Map<String, String>> nigqualList) {
        this.nigqualList = nigqualList;
    }

    /**
     * @return the cohortList
     */
    public ArrayList<Map<String, String>> getCohortList() {
        return cohortList;
    }

    /**
     * @param cohortList the cohortList to set
     */
    public void setCohortList(ArrayList<Map<String, String>> cohortList) {
        this.cohortList = cohortList;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the thermaticArea
     */
    public String getThermaticArea() {
        return thermaticArea;
    }

    /**
     * @param thermaticArea the thermaticArea to set
     */
    public void setThermaticArea(String thermaticArea) {
        this.thermaticArea = thermaticArea;
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
