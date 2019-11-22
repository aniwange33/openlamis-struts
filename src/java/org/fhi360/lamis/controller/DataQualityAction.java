/**
 *
 * @author AALOZIE
 */

package org.fhi360.lamis.controller;

import java.util.*;
import com.opensymphony.xwork2.ActionSupport;
import org.fhi360.lamis.service.CleanupService;
import org.fhi360.lamis.service.DataQualityService;
  

public class DataQualityAction extends ActionSupport  {
    private Integer page;
    private Integer rows;
    private String sidx;
    private String sord;
    private Integer totalpages;
    private Integer currpage;
    private Integer totalrecords;
    private String[] facilityIds;

    
    private ArrayList<Map<String, Object>> elementList = new ArrayList<Map<String, Object>>();    
    private ArrayList<Map<String, Object>> reportList = new ArrayList<Map<String, Object>>();
    private HashMap parameterMap = new HashMap();

    public String cleanupData() {
        new CleanupService().cleanupData();  
        return SUCCESS;        
    }
    
    public String dqaGrid() {
        //setTotalpages(1);
        //setCurrpage(1);
        //setTotalrecords(20);
        elementList = new DataQualityService().analysis();
        return SUCCESS;        
    }
    
    public String dqaReport() {
        reportList = new DataQualityService().dqaReport(); 
        parameterMap = new DataQualityService().getReportParameters(); 
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
     * @return the elementList
     */
    public ArrayList<Map<String, Object>> getElementList() {
        return elementList;
    }

    /**
     * @param elementList the elementList to set
     */
    public void setElementList(ArrayList<Map<String, Object>> elementList) {
        this.elementList = elementList;
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
