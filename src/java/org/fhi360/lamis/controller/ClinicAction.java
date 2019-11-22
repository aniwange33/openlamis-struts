/**
 *
 * @author AALOZIE
 */
package org.fhi360.lamis.controller;

import static com.opensymphony.xwork2.Action.ERROR;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.ModelDriven;
import java.text.SimpleDateFormat;
import org.fhi360.lamis.model.Clinic;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.dao.hibernate.ClinicDAO;
import org.fhi360.lamis.utility.builder.ClinicListBuilder;
import org.fhi360.lamis.dao.jdbc.ClinicJDBC;
import org.fhi360.lamis.dao.hibernate.PatientDAO;
import org.fhi360.lamis.model.dto.PatientObjHandler;
import org.fhi360.lamis.service.DeleteService;
import org.fhi360.lamis.interceptor.AdrList;
import static org.fhi360.lamis.interceptor.MonitorServiceInterceptor.clearPatientId;
import org.fhi360.lamis.interceptor.updater.PatientClinicAttributeUpdater;
import org.fhi360.lamis.interceptor.updater.PatientStatusAttributeUpdater;
import org.fhi360.lamis.service.MonitorService;
import org.fhi360.lamis.utility.builder.PatientListBuilder;

public class ClinicAction extends ActionSupport implements ModelDriven, Preparable {

    private Long facilityId;
    private Long patientId;
    private Patient patient;
    private Long clinicId;
    private Long userId;
    private Clinic clinic;
    private Set<Clinic> clinics = new HashSet<>(0);

    private HttpServletRequest request;
    private HttpSession session;

    private ArrayList<Map<String, String>> clinicList = new ArrayList<>();

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
        clinic = new Clinic();
        return clinic;
    }

    // Save new patient to database
    public String saveClinic() {
        try {
            patient.setPatientId(Long.parseLong(request.getParameter("patientId")));
            clinic.setPatient(patient);
            clinic.setFacilityId(facilityId);
            if (Integer.parseInt(request.getParameter("commence")) == 0) {
                clinic.setAdrIds(AdrList.getIds());
            }
            clinic.setUserId(userId);
            clinic.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
            ClinicDAO.save(clinic);

        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    // Update clinic in database
    public String updateClinic() {
        try {
            patient.setPatientId(Long.parseLong(request.getParameter("patientId")));
            clinic.setPatient(patient);
            clinic.setFacilityId(facilityId);
            if (Integer.parseInt(request.getParameter("commence")) == 0) {
                clinic.setAdrIds(AdrList.getIds());
            }
            clinic.setClinicId(Long.parseLong(request.getParameter("clinicId")));
            clinic.setUserId(userId);
            clinic.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
            ClinicDAO.update(clinic);

        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    // Delete clinic from database
    public String deleteClinic() {
        try {
            Clinic c = ClinicDAO.find(Long.parseLong(request.getParameter("clinicId")));
            String entityId = ServletActionContext.getRequest().getParameter("hospitalNum") + "#" + ServletActionContext.getRequest().getParameter("dateVisit");
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            String dateVisit = format.format(c.getDateVisit());
            MonitorService.logEntity(entityId, "clinic", 3);

            clearPatientId();
            DeleteService deleteService = new DeleteService();
            if (facilityId > 0L) {
                deleteService.deleteClinic(facilityId, Long.parseLong(request.getParameter("patientId")), c.getDateVisit());
                // new PatientClinicAttributeUpdater().lastClinicDate();
                if (c.getCommence() == 1) {
                    //If this clinic encounter is an ART commencement record delete the ART Start status record in Statushistory table
                    deleteService.deleteStatus(facilityId, Long.parseLong(request.getParameter("patientId")), "ART Start", c.getDateVisit());

                    //make sure that status attribute is updated in patient table
                    new PatientStatusAttributeUpdater().updateWithPreviousStatus();
                    //nullify ART Start date originally set on the patient date_started attribute
                    new PatientClinicAttributeUpdater().nullifyStartDate();
                    //then log identity of the deleted status record in the monitor for the server to effect changes with synced to the server
                    entityId = request.getParameter("hospitalNum") + "#ART Start#" + dateVisit;
                    MonitorService.logEntity(entityId, "statushistory", 3);
                }
                return SUCCESS;
            } 
            else {
                return ERROR;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR;
        }
    }
 
    public String findClinic() {
        try {
            new ClinicJDBC().findClinic(Long.parseLong(request.getParameter("patientId")), request.getParameter("dateVisit"));
            clinicList = new ClinicListBuilder().retrieveClinicList();
            findPatient();
        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String findCommence() {
        try {
            new ClinicJDBC().findCommence(Long.parseLong(request.getParameter("patientId")));
            clinicList = new ClinicListBuilder().retrieveClinicList();
            findPatient();
        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String retrieveClinicList() {
        clinicList = new ClinicListBuilder().retrieveClinicList();
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
     * @return the clinicId
     */
    public Long getClinicId() {
        return clinicId;
    }

    /**
     * @param clinicId the clinicId to set
     */
    public void setClinicId(Long clinicId) {
        this.clinicId = clinicId;
    }

    /**
     * @return the clinic
     */
    public Clinic getClinic() {
        return clinic;
    }

    /**
     * @param clinic the clinic to set
     */
    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }

    /**
     * @return the clinics
     */
    public Set<Clinic> getClinics() {
        return clinics;
    }

    /**
     * @param clinics the clinics to set
     */
    public void setClinics(Set<Clinic> clinics) {
        this.clinics = clinics;
    }

    /**
     * @return the clinicList
     */
    public ArrayList<Map<String, String>> getClinicList() {
        return clinicList;
    }

    /**
     * @param clinicList the clinicList to set
     */
    public void setClinicList(ArrayList<Map<String, String>> clinicList) {
        this.clinicList = clinicList;
    }
}
