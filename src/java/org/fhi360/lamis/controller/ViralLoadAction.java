/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.controller;

import static com.opensymphony.xwork2.Action.ERROR;
import static com.opensymphony.xwork2.Action.SUCCESS;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;
import java.util.ArrayList;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.dao.hibernate.StatushistoryDAO;
import org.fhi360.lamis.interceptor.updater.PatientStatusAttributeUpdater;
import org.fhi360.lamis.interceptor.updater.StatusHistoryUpdater;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.model.Statushistory;
import org.fhi360.lamis.service.DeleteService;
import org.fhi360.lamis.utility.DateUtil;

import org.fhi360.lamis.utility.builder.PatientListBuilder;

/**
 *
 * @author user10
 */
public class ViralLoadAction extends ActionSupport implements ModelDriven, Preparable  {
    private Long facilityId;
    private Patient patient;
    private Statushistory statushistory;
    private String status;
    
    private HttpServletRequest request;
    private HttpSession session;
    private String query;
  
    private ArrayList<Map<String, String>> defaulterList = new ArrayList<>();

    @Override
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
        facilityId = (Long) session.getAttribute("facilityId");
    }

    @Override
    public Object getModel() {
        patient = new Patient();
        statushistory = new Statushistory();
        return statushistory;
    }    
    
    public String saveStatus() {
        if(facilityId > 0L) {
            patient.setPatientId(Long.parseLong(request.getParameter("patientId")));        
            statushistory.setPatient(patient);
            statushistory.setFacilityId(facilityId);
            statushistory.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
            StatushistoryDAO.save(statushistory);
            return SUCCESS;            
        }
        else {
            return ERROR;
        }
    }
    
    public String updateStatus() {
        if(facilityId > 0L) {
            patient.setPatientId(Long.parseLong(request.getParameter("patientId")));        
            statushistory.setPatient(patient);
            statushistory.setFacilityId(facilityId);
            statushistory.setHistoryId(Long.parseLong(request.getParameter("historyId")));
            statushistory.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
            StatushistoryDAO.update(statushistory);
            return SUCCESS;            
        }
        else {
            return ERROR;
        }
    }
    
    public String updateStatusDefaulter() {
        String patientId = "";
        String updated = "";
        // retrieve the contact list store in session attribute
        defaulterList = new PatientListBuilder().retrieveDefaulterList();
        try {
            for(Map map : defaulterList) {
                patientId = (String) map.get("patientId");
                updated = (String) map.get("updated");
                if(request.getParameter("patientId").equals(patientId)) {
                    new PatientStatusAttributeUpdater().updateStatusDefaulter();
                    new StatusHistoryUpdater().logTrackingOutcome(Long.parseLong(patientId), request.getParameter("outcome"), request.getParameter("dateTracked"));        
                }
                else {
                    if(updated.equals("true")){
                        new PatientStatusAttributeUpdater().updateStatusDefaulter(map);
                        new StatusHistoryUpdater().logTrackingOutcome(Long.parseLong(patientId), (String) map.get("outcome"), (String) map.get("dateTracked"));        
                    }
                }
            }
        }
        catch (Exception exception) {
            return ERROR;
        }
        return SUCCESS;
    }   

    public String deleteStatus() {
        DeleteService deleteService = new DeleteService();
        if(facilityId > 0L) {
            deleteService.deleteStatus(facilityId, Long.parseLong(request.getParameter("patientId")), request.getParameter("currentStatus"),  DateUtil.parseStringToDate(request.getParameter("dateCurrentStatus"), "MM/dd/yyyy"));
            new PatientStatusAttributeUpdater().updateWithPreviousStatus();
            return SUCCESS; 
        }
        else {
            return ERROR;
        }
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
}
