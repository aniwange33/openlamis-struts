/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.controller;

import org.fhi360.lamis.service.SyncAnalyzer;
import com.opensymphony.xwork2.ActionSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.struts2.ServletActionContext;

/**
 *
 * @author user1
 */
public class SyncAnalyzerAction extends ActionSupport{
    private ArrayList<Map<String, String>> syncList = new ArrayList<Map<String, String>>();
    private ArrayList<Map<String, String>> auditList = new ArrayList<Map<String, String>>();
    private  Map<String, String> syncMap = new HashMap<String, String>();
    private SyncAnalyzer syncAnalyzer = new SyncAnalyzer();
    
    public String syncAnalyzer() {
        setSyncList(syncAnalyzer.getAnalysis()); 
        return SUCCESS;         
    }

    public String syncAudit() {
        setAuditList(syncAnalyzer.audit(Long.parseLong(ServletActionContext.getRequest().getParameter("facilityId")))); 
        return SUCCESS;         
    }
    
    public String syncSummary() {
        setSyncMap(syncAnalyzer.getSummary()); 
        return SUCCESS;         
    }

    /**
     * @return the syncList
     */
    public ArrayList<Map<String, String>> getSyncList() {
        return syncList;
    }

    /**
     * @param syncList the syncList to set
     */
    public void setSyncList(ArrayList<Map<String, String>> syncList) {
        this.syncList = syncList;
    }

    /**
     * @return the map
     */
    public Map<String, String> getSyncMap() {
        return syncMap;
    }

    /**
     * @param map the map to set
     */
    public void setSyncMap(Map<String, String> syncMap) {
        this.syncMap = syncMap;
    }

    /**
     * @return the auditList
     */
    public ArrayList<Map<String, String>> getAuditList() {
        return auditList;
    }

    /**
     * @param auditList the auditList to set
     */
    public void setAuditList(ArrayList<Map<String, String>> auditList) {
        this.auditList = auditList;
    }
    
}
