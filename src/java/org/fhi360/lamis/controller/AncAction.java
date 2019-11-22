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
import java.sql.ResultSet;
import org.fhi360.lamis.model.Anc;
import org.fhi360.lamis.model.Partnerinformation;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.dao.hibernate.AncDAO;
import org.fhi360.lamis.dao.hibernate.PartnerinformationDAO;
import org.fhi360.lamis.dao.hibernate.PatientDAO;
import org.fhi360.lamis.dao.jdbc.AncJDBC;
import org.fhi360.lamis.dao.jdbc.MotherInformationJDBC;
import org.fhi360.lamis.model.Facility;
import org.fhi360.lamis.model.dto.PatientObjHandler;
import org.fhi360.lamis.service.DeleteService;
import org.fhi360.lamis.service.MonitorService;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.builder.AncListBuilder;
import org.fhi360.lamis.utility.builder.MotherInformationListBuilder;
import org.fhi360.lamis.utility.builder.PartnerInformationListBuilder;
import org.fhi360.lamis.utility.builder.PatientListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;

public class AncAction extends ActionSupport implements ModelDriven, Preparable {

    private Long facilityId;
    private Long patientId;
    private Patient patient;
    private Partnerinformation partnerinformation;
    private Long ancId;
    private Long userId;
    private Anc anc;
    private Set<Anc> ancs = new HashSet<>(0);
    private Facility facility;
    private HttpServletRequest request;
    private HttpSession session;
    private ArrayList<Map<String, String>> ancList = new ArrayList<>();
    private ArrayList<Map<String, String>> motherList = new ArrayList<>();
    private ArrayList<Map<String, String>> ancLast = new ArrayList<>();
    private ArrayList<Map<String, String>> partnerinformationList = new ArrayList<>();
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    @Override
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
        facilityId = (Long) session.getAttribute("facilityId");
        userId = (Long) session.getAttribute("userId");
    }

    // Retrieve persistent object and map attribute to form data 
    @Override
    public Object getModel() {
        patient = new Patient();
        anc = new Anc();
        partnerinformation = new Partnerinformation();
        facility = new Facility();
        return anc;
    }

    // Save new anc to database
    public String saveAnc() {
        try {
            System.out.println(request.getParameter("patientId"));
            patientId = Long.parseLong(request.getParameter("patientId"));
            patient.setPatientId(patientId);
            anc.setPatient(patient);
            anc.setFacilityId(facilityId);
            anc.setUserId(userId);
            anc.setDateEnrolledPmtct(DateUtil.parseStringToSqlDate(request.getParameter("dateVisit"), "MM/dd/yyyy"));
            anc.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
            AncDAO.save(anc);

            //Update patient Table to set Date PMTCT
            patient = PatientDAO.find(patientId);
            patient.setDateEnrolledPmtct(anc.getDateEnrolledPmtct());
            patient.setTimeHivDiagnosis(anc.getTimeHivDiagnosis());
            patient.setPregnant(1);
            PatientDAO.update(patient);

            // update partner information
            saveOrUpdatePartnerinformation(anc.getAncId());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ERROR;
        }

        return SUCCESS;
    }

    // Update anc in database
    public String updateAnc() {
        try {
            patientId = Long.parseLong(request.getParameter("patientId"));
            patient.setPatientId(patientId);
            anc.setPatient(patient);
            anc.setFacilityId(facilityId);
            anc.setAncId(Long.parseLong(request.getParameter("ancId")));
            anc.setUserId(userId);
            anc.setDateEnrolledPmtct(DateUtil.parseStringToDate(request.getParameter("dateVisit"), "MM/dd/yyyy"));
            anc.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
            AncDAO.update(anc);
            //Update patient Table to set Date PMTCT
            patient = PatientDAO.find(patientId);
            if (patient.getDateEnrolledPmtct().equals(anc.getDateVisit())) {
                patient.setTimeHivDiagnosis(anc.getTimeHivDiagnosis());
                PatientDAO.update(patient);
            }

            // update partner information
            saveOrUpdatePartnerinformation(anc.getAncId());
        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    private void saveOrUpdatePartnerinformation(long ancId) {

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
        if (!"".equals(partnerReferred)) {
            partnerReferred = partnerReferred.substring(0, partnerReferred.lastIndexOf(","));
        }
        partnerinformation.setPartnerReferred(partnerReferred);

        partnerinformation.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
        partnerinformation.setUserId(userId);

        if ("".equals(request.getParameter("partnerinformationId"))) { // a save operation
            PartnerinformationDAO.save(partnerinformation);
        } else { // an update operation
            partnerinformation.setPartnerinformationId(Long.parseLong(request.getParameter("partnerinformationId")));
            PartnerinformationDAO.update(partnerinformation);
        }

    }

    // Delete anc from database
    public String deleteAnc() {
        try {
            String entityId = ServletActionContext.getRequest().getParameter("hospitalNum") + "#" + ServletActionContext.getRequest().getParameter("dateVisit");
            MonitorService.logEntity(entityId, "anc", 3);
            ancId = Long.parseLong(request.getParameter("ancId"));
            DeleteService deleteService = new DeleteService();
            if (facilityId > 0L) {
                System.out.println(request.getParameter("patientId"));
                patientId = Long.parseLong(request.getParameter("patientId"));
                patient = PatientDAO.find(patientId);
                anc = AncDAO.find(ancId);
                System.out.println("Patient: " + patient.getDateEnrolledPmtct());
                System.out.println("Anc: " + anc.getDateEnrolledPmtct());
                System.out.println("Anc visit Out: " + anc.getDateVisit());
                System.out.println("Anc Equals: " + patient.getDateEnrolledPmtct().equals(anc.getDateVisit()));
                //System.out.println("Anc Equals1: "+patient.getDateEnrolledPmtct() == anc.getDateVisit());
                if (patient.getDateEnrolledPmtct().equals(anc.getDateVisit())) {
                    System.out.println("Anc visit: " + anc.getDateVisit());
                    patient.setDateEnrolledPmtct(null);
                    patient.setTimeHivDiagnosis(null);
                    patient.setPregnant(0);
                    PatientDAO.update(patient);
                }
                deleteService.deleteAnc(facilityId, ancId);
                return SUCCESS;
            } else {
                return ERROR;
            }
        } catch (Exception e) {
            return ERROR;
        }
    }

    public String findAnc() {
        try {
            new AncJDBC().findAnc(Long.parseLong(request.getParameter("patientId")), request.getParameter("dateVisit"));
            ancList = new AncListBuilder().retrieveAncList();
            findPatient();
        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String retrieveMotherList() {
        try {
            motherList = new MotherInformationListBuilder().retrieveMotherList();
            System.out.println("Mother List: " + motherList);
        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String retrieveMotherListForChild() {
        try {
            System.out.println("Mother ID is: " + request.getParameter("motherId"));
            String query = new MotherInformationJDBC()
                    .findMother(Long.parseLong(request.getParameter("motherId")));
            jdbcTemplate.query(query, resultSet -> {
                new AncListBuilder().buildMotherList(resultSet);
                return null;
            });
        } catch (Exception exception) {

            return ERROR;
        }
        motherList = new MotherInformationListBuilder().retrieveMotherList();
        return SUCCESS;
    }

    public String retrieveAncList() {
        System.out.println("Got Here!");
        if (request.getParameterMap().containsKey("last")) {
            facilityId = (Long) session.getAttribute("facilityId");
            patientId = Long.parseLong(request.getParameter("patientId"));
            try {
                String query = String.format("SELECT * FROM anc WHERE facility_id = %d AND patient_id = %d ORDER BY date_visit DESC LIMIT 1",
                        facilityId, patientId);
                jdbcTemplate.query(query, resultSet -> {
                    new AncListBuilder().buildAncList(resultSet);
                    return null;
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                return ERROR;
            }
        }
        ancList = new AncListBuilder().retrieveAncList();
        System.out.println(ancList);
        return SUCCESS;
    }

    public String retrieveAncListByDate() {
        System.out.println("Got Here By Date!");
        if (request.getParameterMap().containsKey("last")) {
            facilityId = (Long) session.getAttribute("facilityId");
            patientId = Long.parseLong(request.getParameter("patientId"));
            String dateVisit = request.getParameter("dateVisit");
            try {
                String query = String.format("SELECT * FROM anc WHERE facility_id = %d AND patient_id = %d ORDER BY date_visit DESC LIMIT 1",
                        facilityId, patientId);
                jdbcTemplate.query(query, resultSet -> {
                    new AncListBuilder().buildAncList(resultSet);
                    return null;
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                return ERROR;
            }
        }
        ancList = new AncListBuilder().retrieveAncList();
        System.out.println(ancList);
        return SUCCESS;
    }

    public String retrieveFirstAncList() {
        facilityId = (Long) session.getAttribute("facilityId");
        patientId = Long.parseLong(request.getParameter("patientId"));
        try {
            String query = String.format("SELECT * FROM anc WHERE facility_id = %d AND patient_id = %d ORDER BY date_visit ASC LIMIT 1",
                    facilityId, patientId);
            jdbcTemplate.query(query, resultSet -> {
                new AncListBuilder().buildAncList(resultSet);
                return null;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            return ERROR;
        }
        ancList = new AncListBuilder().retrieveAncList();
        return SUCCESS;
    }

    public String retrieveLastAncList() {
        ancLast = new AncListBuilder().retrieveAncList();
        return SUCCESS;
    }

    // Retrieve a patient in database
    private void findPatient() {
        patientId = Long.parseLong(request.getParameter("patientId"));
        patient = PatientDAO.find(patientId);
        PatientObjHandler.store(patient);
        new PatientListBuilder().buildPatientList(patient);
    }

    public String retrievePartnerinformationList() {
        partnerinformationList = new PartnerInformationListBuilder().retrievePartnerinformationList();
        return SUCCESS;
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
     * @return the ancId
     */
    public Long getAncId() {
        return ancId;
    }

    /**
     * @param ancId the ancId to set
     */
    public void setAncId(Long ancId) {
        this.ancId = ancId;
    }

    /**
     * @return the anc
     */
    public Anc getAnc() {
        return anc;
    }

    /**
     * @param anc the anc to set
     */
    public void setAnc(Anc anc) {
        this.anc = anc;
    }

    /**
     * @return the ancs
     */
    public Set<Anc> getAncs() {
        return ancs;
    }

    /**
     * @param ancs the ancs to set
     */
    public void setAncs(Set<Anc> ancs) {
        this.ancs = ancs;
    }

    /**
     * @return the ancList
     */
    public ArrayList<Map<String, String>> getAncList() {
        return ancList;
    }

    /**
     * @param ancList the ancList to set
     */
    public void setAncList(ArrayList<Map<String, String>> ancList) {
        this.ancList = ancList;
    }

    /**
     * @return the partnerinformationList
     */
    public ArrayList<Map<String, String>> getPartnerinformationList() {
        return partnerinformationList;
    }

    /**
     * @param partnerinformationList the partnerinformationList to set
     */
    public void setPartnerinformationList(ArrayList<Map<String, String>> partnerinformationList) {
        this.partnerinformationList = partnerinformationList;
    }

    public ArrayList<Map<String, String>> getAncLast() {
        return ancLast;
    }

    public void setAncLast(ArrayList<Map<String, String>> ancLast) {
        this.ancLast = ancLast;
    }

    public ArrayList<Map<String, String>> getMotherList() {
        return motherList;
    }

    public void setMotherList(ArrayList<Map<String, String>> motherList) {
        this.motherList = motherList;
    }

}
