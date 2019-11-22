/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import static com.opensymphony.xwork2.Action.ERROR;
import static com.opensymphony.xwork2.Action.SUCCESS;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;
import java.util.Date;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.dao.hibernate.DevolveDAO;
import org.fhi360.lamis.dao.hibernate.PatientDAO;
import org.fhi360.lamis.dao.jdbc.DevolveJDBC;
import org.fhi360.lamis.model.Devolve;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.model.dto.PatientObjHandler;
import org.fhi360.lamis.service.DeleteService;
import org.fhi360.lamis.service.MonitorService;
import org.fhi360.lamis.utility.builder.DevolveListBuilder;
import org.fhi360.lamis.utility.builder.PatientListBuilder;

/**
 *
 * @author user1
 */
public class DevolveAction extends ActionSupport implements ModelDriven, Preparable {

    private Long facilityId;
    private Long patientId;
    private Patient patient = new Patient();
    private Long devolveId;
    private Long userId;
    private Devolve devolve;
    private DevolveJDBC devolveJdbc = new DevolveJDBC();
    private Set<Devolve> devolves = new HashSet<>(0);

    private HttpServletRequest request;
    private HttpSession session;
    private String query;

    private ArrayList<Map<String, String>> devolveList = new ArrayList<>();
    private ArrayList<Map<String, String>> patientList = new ArrayList<>();

    @Override
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
        facilityId = (Long) session.getAttribute("facilityId");
        userId = (Long) session.getAttribute("userId");
    }

    @Override
    public Object getModel() {
        devolve = new Devolve();
        return devolve;
    }

    // Save new devolve to database
    public String saveDevolve() {
        try {
            Long patientId = Long.parseLong(request.getParameter("patientId"));
            patient.setPatientId(patientId);
            devolve.setPatient(patient);
            devolve.setFacilityId(facilityId);
            devolve.setUserId(userId);
            devolve.setTimeStamp(new Date());
            DevolveDAO.save(devolve);
            if (devolve.getCommunitypharmId() > 0) {
                devolveJdbc.devolved(patientId, Long.parseLong(request.getParameter("communitypharmId")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR;
        }
        return SUCCESS;
    }

    // Update devolve in database
    public String updateDevolve() {
        try {
            System.out.println("Devolve ID: " + request.getParameter("devolveId"));
            patient.setPatientId(Long.parseLong(request.getParameter("patientId")));
            devolve.setPatient(patient);
            devolve.setFacilityId(facilityId);
            devolve.setDevolveId(Long.parseLong(request.getParameter("devolveId")));
            devolve.setUserId(userId);
            devolve.setTimeStamp(new Date());
            DevolveDAO.update(devolve);
            if (devolve.getCommunitypharmId() > 0) {
                devolveJdbc.devolved(Long.parseLong(request.getParameter("patientId")), Long.parseLong(request.getParameter("communitypharmId")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR;
        }
        return SUCCESS;
    }

    // Delete devolve from database
    public String deleteDevolve() {
        try {
            String entityId = ServletActionContext.getRequest().getParameter("hospitalNum") + "#" + ServletActionContext.getRequest().getParameter("date_devolved");
            MonitorService.logEntity(entityId, "devolve", 3);
            DeleteService deleteService = new DeleteService();
            if (facilityId > 0L) {
                deleteService.deleteDevolve(facilityId, Long.parseLong(request.getParameter("devolveId")));
                devolveJdbc.devolved(Long.parseLong(request.getParameter("patientId")));
                return SUCCESS;
            } else {
                return ERROR;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR;
        }
    }

    public String findPatientToDevolve() {
        try {
            devolveJdbc.findPatientToDevolve(Long.parseLong(request.getParameter("patientId")));
            patientList = new DevolveListBuilder().retrieveDevolveList();
        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String findDevolve() {
        try {
            devolveJdbc.findDevolve(Long.parseLong(request.getParameter("patientId")), request.getParameter("dateDevolved"));
            devolveList = new DevolveListBuilder().retrieveDevolveList();
            findPatient();
        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String retrieveDevolveList() {
        devolveList = new DevolveListBuilder().retrieveDevolveList();
        return SUCCESS;
    }

    // Retrieve a patient in database
    private void findPatient() {
        try {
            patientId = Long.parseLong(request.getParameter("patientId"));
            patient = PatientDAO.find(patientId);
            PatientObjHandler.store(patient);
            new PatientListBuilder().buildPatientList(patient);
        } catch (Exception e) {
            // return ERROR;
        }
    }

    /**
     * @return the facilityId
     */
    public Long getFacilityId() {
        return facilityId;
    }

    /**
     * @param facilityId the facilityId to set
     */
    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }

    /**
     * @return the patientId
     */
    public Long getPatientId() {
        return patientId;
    }

    /**
     * @param patientId the patientId to set
     */
    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    /**
     * @return the patient
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     * @param patient the patient to set
     */
    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    /**
     * @return the devolveId
     */
    public Long getDevolveId() {
        return devolveId;
    }

    /**
     * @param devolveId the devolveId to set
     */
    public void setDevolveId(Long devolveId) {
        this.devolveId = devolveId;
    }

    /**
     * @return the userId
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return the devolveList
     */
    public ArrayList<Map<String, String>> getDevolveList() {
        return devolveList;
    }

    /**
     * @param devolveList the devolveList to set
     */
    public void setDevolveList(ArrayList<Map<String, String>> devolveList) {
        this.devolveList = devolveList;
    }

    /**
     * @return the devolve
     */
    public Devolve getDevolve() {
        return devolve;
    }

    /**
     * @param devolve the devolve to set
     */
    public void setDevolve(Devolve devolve) {
        this.devolve = devolve;
    }

    /**
     * @return the devolves
     */
    public Set<Devolve> getDevolves() {
        return devolves;
    }

    /**
     * @param devolves the devolves to set
     */
    public void setDevolves(Set<Devolve> devolves) {
        this.devolves = devolves;
    }

    /**
     * @return the patientList
     */
    public ArrayList<Map<String, String>> getPatientList() {
        return patientList;
    }

    /**
     * @param patientList the patientList to set
     */
    public void setPatientList(ArrayList<Map<String, String>> patientList) {
        this.patientList = patientList;
    }

}
