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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.dao.hibernate.ChroniccareDAO;
import org.fhi360.lamis.dao.hibernate.EacDAO;
import org.fhi360.lamis.dao.hibernate.PatientDAO;
import org.fhi360.lamis.dao.jdbc.EacJDBC;
import org.fhi360.lamis.model.Chroniccare;
import org.fhi360.lamis.model.Eac;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.model.dto.PatientObjHandler;
import org.fhi360.lamis.service.DeleteService;
import org.fhi360.lamis.service.MonitorService;
import org.fhi360.lamis.utility.builder.EacListBuilder;
import org.fhi360.lamis.utility.builder.PatientListBuilder;

/**
 *
 * @author user10
 */
public class EacAction extends ActionSupport implements ModelDriven, Preparable {

    private Long facilityId;
    private Long patientId;
    private Patient patient;
    private Long eacId;
    private Long userId;
    private Eac eac;
    private Set<Eac> eacs = new HashSet<>(0);

    private HttpServletRequest request;
    private HttpSession session;

    private ArrayList<Map<String, String>> eacList = new ArrayList<>();

    @Override
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
        facilityId = (Long) session.getAttribute("facilityId");
        userId = (Long) session.getAttribute("userId");
    }

    @Override
    public Object getModel() {
        patient = new Patient();
        eac = new Eac();
        return eac;
    }

    // Save new patient to database
    public String saveEac() {
        try {
            System.out.println("SAVEING ");
            patient.setPatientId(Long.parseLong(request.getParameter("patientId")));
            eac.setPatient(patient);
            eac.setFacilityId(facilityId);
            eac.setUserId(userId);
            eac.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
            EacDAO.save(eac);
        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    // Update eac in database
    public String updateEac() {
        try {
            patient.setPatientId(Long.parseLong(request.getParameter("patientId")));
            eac.setPatient(patient);
            eac.setFacilityId(facilityId);
            eac.setEacId(Long.parseLong(request.getParameter("eacId")));
            eac.setUserId(userId);
            eac.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
            EacDAO.update(eac);
        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    // Delete eac from database
    public String deleteEac() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            Eac eac = EacDAO.find(Long.parseLong(ServletActionContext.getRequest().getParameter("eacId")));
            Patient patient = PatientDAO.find(Long.parseLong(ServletActionContext.getRequest().getParameter("patientId")));
            String entityId = patient.getHospitalNum() + "#" + format.format(eac.getDateEac1());
            MonitorService.logEntity(entityId, "eac", 3);
            DeleteService deleteService = new DeleteService();
            if (facilityId > 0L) {
                deleteService.deleteEac(facilityId, Long.parseLong(request.getParameter("eacId")));
                return SUCCESS;
            } else {
                return ERROR;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR;
        }
    }

    public String findEac() {
        try {
            new EacJDBC().findEac(Long.parseLong(request.getParameter("patientId")), request.getParameter("dateEac1"));
            eacList = new EacListBuilder().retrieveEacList();
            findPatient();
        } catch (Exception E) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String retrieveEacList() {
        try {
            eacList = new EacListBuilder().retrieveEacList();
        } catch (Exception e) {
            return ERROR;
        }
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
     * @return the eacId
     */
    public Long getEacId() {
        return eacId;
    }

    /**
     * @param eacId the eacId to set
     */
    public void setEacId(Long eacId) {
        this.eacId = eacId;
    }

    /**
     * @return the eac
     */
    public Eac getEac() {
        return eac;
    }

    /**
     * @param eac the eac to set
     */
    public void setEac(Eac eac) {
        this.eac = eac;
    }

    /**
     * @return the eacs
     */
    public Set<Eac> getEacs() {
        return eacs;
    }

    /**
     * @param eacs the eacs to set
     */
    public void setEacs(Set<Eac> eacs) {
        this.eacs = eacs;
    }

    /**
     * @return the eacList
     */
    public ArrayList<Map<String, String>> getEacList() {
        return eacList;
    }

    /**
     * @param eacList the eacList to set
     */
    public void setEacList(ArrayList<Map<String, String>> eacList) {
        this.eacList = eacList;
    }
}
