/**
 *
 * @author AALOZIE
 */
package org.fhi360.lamis.controller;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;
import java.text.SimpleDateFormat;
import org.fhi360.lamis.dao.hibernate.PatientDAO;
import org.fhi360.lamis.dao.hibernate.StatushistoryDAO;
import org.fhi360.lamis.dao.jdbc.StatusHistoryJDBC;
import org.fhi360.lamis.interceptor.updater.PatientStatusAttributeUpdater;
import org.fhi360.lamis.interceptor.updater.StatusHistoryUpdater;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.model.Statushistory;
import org.fhi360.lamis.service.DeleteService;
import org.fhi360.lamis.service.MonitorService;
import org.fhi360.lamis.utility.DateUtil;

import org.fhi360.lamis.utility.builder.PatientListBuilder;
import org.fhi360.lamis.utility.builder.StatusHistoryBuilder;

public class StatusHistoryAction extends ActionSupport implements ModelDriven, Preparable {

    private Long facilityId;
    private Patient patient;
    private Statushistory statushistory;
    private String status;

    private HttpServletRequest request;
    private HttpSession session;
    private String query;

    private ArrayList<Map<String, String>> patientList = new ArrayList<>();
    private ArrayList<Map<String, String>> statusList = new ArrayList<>();
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
        try {
            if (facilityId > 0L) {
                patient.setPatientId(Long.parseLong(request.getParameter("patientId")));
                statushistory.setPatient(patient);
                statushistory.setFacilityId(facilityId);
                statushistory.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
                System.out.println("Status History : " + statushistory.toString());
                StatushistoryDAO.save(statushistory);
                return SUCCESS;
            } else {
                return ERROR;
            }
        } catch (Exception E) {
            return ERROR;
        }
    }

    public String updateStatus() {
        System.out.println("1");
        try {
            if (facilityId > 0L) {
               
                patient.setPatientId(Long.parseLong(request.getParameter("patientId")));
             
                statushistory.setPatient(patient);
               
                statushistory.setFacilityId(facilityId);
               
                statushistory.setHistoryId(Long.parseLong(request.getParameter("historyId")));
              
                statushistory.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
                StatushistoryDAO.update(statushistory);
                 System.out.println("5 ");
                return SUCCESS;
            } else {
                return ERROR;
            }
        } catch (Exception e) {
            return ERROR;
        }
    }

    public String updateStatusDefaulter() {

        String patientId = "";
        String updated = "";
        // retrieve the contact list store in session attribute
        defaulterList = new PatientListBuilder().retrieveDefaulterList();
        try {
            for (Map map : defaulterList) {
                patientId = (String) map.get("patientId");
                updated = (String) map.get("updated");
                if (request.getParameter("patientId").equals(patientId)) {
                    new PatientStatusAttributeUpdater().updateStatusDefaulter();
                    new StatusHistoryUpdater().logTrackingOutcome(Long.parseLong(patientId), request.getParameter("outcome"), request.getParameter("dateTracked"));
                } else {
                    if (updated.equals("true")) {
                        new PatientStatusAttributeUpdater().updateStatusDefaulter(map);
                        new StatusHistoryUpdater().logTrackingOutcome(Long.parseLong(patientId), (String) map.get("outcome"), (String) map.get("dateTracked"));
                    }
                }
            }
        } catch (Exception exception) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String deleteStatus() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            Statushistory history = StatushistoryDAO.find(Long.parseLong(
                    ServletActionContext.getRequest().getParameter("historyId")));
            Patient patient = PatientDAO.find(Long.parseLong(
                    ServletActionContext.getRequest().getParameter("patientId")));
            String entityId = patient.getHospitalNum() + "#"
                    + history.getCurrentStatus() + "#" + format.format(history.getDateCurrentStatus());
            MonitorService.logEntity(entityId, "statushistory", 3);
            DeleteService deleteService = new DeleteService();
            if (facilityId > 0L) {
                deleteService.deleteStatus(history.getFacilityId(), Long.parseLong(request.getParameter("patientId")),
                        history.getCurrentStatus(), history.getDateCurrentStatus());
                new PatientStatusAttributeUpdater().updateWithPreviousStatus();
                return SUCCESS;
            } else {
                return ERROR;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR;
        }
    }

    public String findStatusHistory() {
        try {
            new StatusHistoryJDBC().findStatusHistory(Long.parseLong(request.getParameter("historyId")), request.getParameter("dateCurrentStatus"));
            statusList = new StatusHistoryBuilder().retrieveStatusList();
        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String findStatus() {
        System.out.println("Action: Retrieveing status.....");
        System.out.println("PATIENT ID "+Long.parseLong(request.getParameter("patientId")));
        System.out.println(" dateCurrentStatud   "+request.getParameter("dateCurrentStatus"));
        new StatusHistoryJDBC().findStatus(Long.parseLong(request.getParameter("patientId")), request.getParameter("dateCurrentStatus"));
        statusList = new StatusHistoryBuilder().retrieveStatusList();
        System.out.println("Status....." + statusList);

        return SUCCESS;
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
     * @return the statushistory
     */
    public Statushistory getStatushistory() {
        return statushistory;
    }

    /**
     * @param statushistory the statushistory to set
     */
    public void setStatushistory(Statushistory statushistory) {
        this.statushistory = statushistory;
    }

    /**
     * @return the statusList
     */
    public ArrayList<Map<String, String>> getStatusList() {
        return statusList;
    }

    /**
     * @param statusList the statusList to set
     */
    public void setStatusList(ArrayList<Map<String, String>> statusList) {
        this.statusList = statusList;
    }

    /**
     * @return the defaulterList
     */
    public ArrayList<Map<String, String>> getDefaulterList() {
        return defaulterList;
    }

    /**
     * @param defaulterList the defaulterList to set
     */
    public void setDefaulterList(ArrayList<Map<String, String>> defaulterList) {
        this.defaulterList = defaulterList;
    }

    /**
     * @return the statusList
     */
    public ArrayList<Map<String, String>> getPatientList() {
        return patientList;
    }

    /**
     * @param statusList the statusList to set
     */
    public void setPatientList(ArrayList<Map<String, String>> patientList) {
        this.patientList = patientList;
    }
}
