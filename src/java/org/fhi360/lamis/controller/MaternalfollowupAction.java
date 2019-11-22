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
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.ModelDriven;
import org.fhi360.lamis.model.Maternalfollowup;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.model.Partnerinformation;
import org.fhi360.lamis.dao.hibernate.MaternalfollowupDAO;
import org.fhi360.lamis.dao.hibernate.PartnerinformationDAO;
import org.fhi360.lamis.dao.hibernate.PatientDAO;
import org.fhi360.lamis.dao.jdbc.AncJDBC;
import org.fhi360.lamis.model.dto.PatientObjHandler;
import org.fhi360.lamis.service.DeleteService;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.builder.MaternalfollowupListBuilder;
import org.fhi360.lamis.utility.builder.PatientListBuilder;

public class MaternalfollowupAction extends ActionSupport implements ModelDriven, Preparable {

    private Long facilityId;
    private Long patientId;
    private Patient patient;
    private Partnerinformation partnerinformation;
    private Long maternalfollowupId;
    private Long userId;
    private Maternalfollowup maternalfollowup;
    private Set<Maternalfollowup> maternalfollowups = new HashSet<>(0);

    private HttpServletRequest request;
    private HttpSession session;

    private ArrayList<Map<String, String>> maternalfollowupList = new ArrayList<>();

    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
        facilityId = (Long) session.getAttribute("facilityId");
        userId = (Long) session.getAttribute("userId");
    }

    // Retrieve persistent object and map attribute to form data 
    public Object getModel() {
        patient = new Patient();
        maternalfollowup = new Maternalfollowup();
        partnerinformation = new Partnerinformation();
        return maternalfollowup;
    }

    // Save new maternalfollowup to database
    public String saveMaternalfollowup() {
        try {
            patient.setPatientId(Long.parseLong(request.getParameter("patientId")));
            maternalfollowup.setPatient(patient);
            maternalfollowup.setFacilityId(facilityId);
            maternalfollowup.setUserId(userId);

            if (request.getParameterMap().containsKey("counselNutrition")) {
                maternalfollowup.setCounselNutrition(1);
            } else {
                maternalfollowup.setCounselNutrition(0);
            }

            if (request.getParameterMap().containsKey("counselFeeding")) {
                maternalfollowup.setCounselFeeding(1);
            } else {
                maternalfollowup.setCounselFeeding(0);
            }

            if (request.getParameterMap().containsKey("counselFamilyPlanning")) {
                maternalfollowup.setCounselFamilyPlanning(1);
            } else {
                maternalfollowup.setCounselFamilyPlanning(0);
            }

            maternalfollowup.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
            MaternalfollowupDAO.save(maternalfollowup);

            // update partner information
            saveOrUpdatePartnerinformation();
        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    // Update maternalfollowup in database
    public String updateMaternalfollowup() {
        try{
        patient.setPatientId(Long.parseLong(request.getParameter("patientId")));
        maternalfollowup.setPatient(patient);
        maternalfollowup.setFacilityId(facilityId);
        maternalfollowup.setMaternalfollowupId(Long.parseLong(request.getParameter("maternalfollowupId")));
        maternalfollowup.setUserId(userId);

        if (request.getParameterMap().containsKey("counselNutrition")) {
            maternalfollowup.setCounselNutrition(1);
        } else {
            maternalfollowup.setCounselNutrition(0);
        }

        if (request.getParameterMap().containsKey("counselFeeding")) {
            maternalfollowup.setCounselFeeding(1);
        } else {
            maternalfollowup.setCounselFeeding(0);
        }

        if (request.getParameterMap().containsKey("counselFamilyPlanning")) {
            maternalfollowup.setCounselFamilyPlanning(1);
        } else {
            maternalfollowup.setCounselFamilyPlanning(0);
        }

        maternalfollowup.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
        MaternalfollowupDAO.update(maternalfollowup);

        // update partner information
        saveOrUpdatePartnerinformation();
        }catch(Exception e){
            return ERROR;
        }
        return SUCCESS;
    }

    private void saveOrUpdatePartnerinformation() {
        try{
        partnerinformation.setPatient(patient);
        partnerinformation.setFacilityId(facilityId);
        partnerinformation.setDateVisit(DateUtil.parseStringToDate(request.getParameter("dateVisit"), "MM/dd/yyyy"));
        partnerinformation.setPartnerNotification(request.getParameter("partnerNotification"));
        partnerinformation.setPartnerHivStatus(request.getParameter("partnerHivStatus"));
        String partnerReferred = "";
        if (request.getParameterMap().containsKey("fp")) {
            partnerReferred += "FP,";
        }
        if (request.getParameterMap().containsKey("art")) {
            partnerReferred += "ART,";
        }
        if (request.getParameterMap().containsKey("others")) {
            partnerReferred += "OTHERS,";
        }
        if (partnerReferred != "") {
            partnerReferred = partnerReferred.substring(0, partnerReferred.lastIndexOf(","));
        }
        partnerinformation.setPartnerReferred(partnerReferred);
        partnerinformation.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
        partnerinformation.setUserId(userId);

        if (request.getParameter("partnerinformationId") == "") { // a save operation
            PartnerinformationDAO.save(partnerinformation);
        } else { // an update operation
            partnerinformation.setPartnerinformationId(Long.parseLong(request.getParameter("partnerinformationId")));
            PartnerinformationDAO.update(partnerinformation);
        }
        }catch(Exception e){
            
        }
    }

    // Delete maternalfollowup from database
    public String deleteMaternalfollowup() {
        try{
        DeleteService deleteService = new DeleteService();
        if (facilityId > 0L) {
            deleteService.deleteMaternalfollowup(facilityId, Long.parseLong(request.getParameter("maternalfollowupId")));
            return SUCCESS;
        } else {
            return ERROR;
        }
        }catch(Exception e){
           return ERROR;  
        }
    }

    public String findMaternalfollowup() {
        System.out.println("Patient "+request.getParameter("patientId"));
        System.out.println("Date Visit "+request.getParameter("dateVisit"));
        try{
        new AncJDBC().findMaternalfollowup(Long.parseLong(request.getParameter("patientId")), request.getParameter("dateVisit"));
        maternalfollowupList = new MaternalfollowupListBuilder().retrieveMaternalfollowupList();
        findPatient();
        }catch(Exception e){
             return ERROR;
        }
        return SUCCESS;
    }

    public String retrieveMaternalfollowupList() {
        try{
        maternalfollowupList = new MaternalfollowupListBuilder().retrieveMaternalfollowupList();
        }catch(Exception e){
             return ERROR;
        }
        return SUCCESS;
    }

    // Retrieve a patient in database
    private void findPatient() {
        try{
        patientId = Long.parseLong(request.getParameter("patientId"));
        patient = PatientDAO.find(patientId);
        PatientObjHandler.store(patient);
        new PatientListBuilder().buildPatientList(patient);
        }catch(Exception e){
            
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
     * @return the maternalfollowupId
     */
    public Long getMaternalfollowupId() {
        return maternalfollowupId;
    }

    /**
     * @param maternalfollowupId the maternalfollowupId to set
     */
    public void setMaternalfollowupId(Long maternalfollowupId) {
        this.maternalfollowupId = maternalfollowupId;
    }

    /**
     * @return the maternalfollowup
     */
    public Maternalfollowup getMaternalfollowup() {
        return maternalfollowup;
    }

    /**
     * @param maternalFollowup the maternalFollowup to set
     */
    public void setMaternalfollowup(Maternalfollowup maternalfollowup) {
        this.maternalfollowup = maternalfollowup;
    }

    /**
     * @return the maternalFollowups
     */
    public Set<Maternalfollowup> getMaternalfollowups() {
        return maternalfollowups;
    }

    /**
     * @param maternalFollowups the maternalFollowups to set
     */
    public void setMaternalfollowups(Set<Maternalfollowup> maternalfollowups) {
        this.maternalfollowups = maternalfollowups;
    }

    /**
     * @return the maternalfollowupList
     */
    public ArrayList<Map<String, String>> getMaternalfollowupList() {
        return maternalfollowupList;
    }

    /**
     * @param maternalfollowupList the maternalfollowupList to set
     */
    public void setMaternalfollowupList(ArrayList<Map<String, String>> maternalfollowupList) {
        this.maternalfollowupList = maternalfollowupList;
    }
}
