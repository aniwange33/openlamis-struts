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
import java.text.SimpleDateFormat;
import org.fhi360.lamis.dao.hibernate.ChroniccareDAO;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.dao.hibernate.PatientDAO;
import org.fhi360.lamis.dao.jdbc.ChroniccareJDBC;
import org.fhi360.lamis.interceptor.DmscreenList;
import org.fhi360.lamis.interceptor.TbscreenList;
import org.fhi360.lamis.model.dto.PatientObjHandler;
import org.fhi360.lamis.service.DeleteService;
import org.fhi360.lamis.model.Chroniccare;
import org.fhi360.lamis.service.MonitorService;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.builder.ChroniccareListBuilder;
import org.fhi360.lamis.utility.builder.PatientListBuilder;

public class ChroniccareAction extends ActionSupport implements ModelDriven, Preparable {

    private Long facilityId;
    private Long patientId;
    private Patient patient;
    private Long userId;
    private Long chroniccareId;
    private Chroniccare chroniccare;
    private Set<Chroniccare> chroniccares = new HashSet<>(0);

    private HttpServletRequest request;
    private HttpSession session;

    private ArrayList<Map<String, String>> chroniccareList = new ArrayList<>();

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
        chroniccare = new Chroniccare();
        return chroniccare;
    }

    // Save new chroniccare to database
    public String saveChroniccare() {
        try {
            System.out.println("Date of Visit 3: " + request.getParameter("dateVisit"));
            System.out.println("Date1: " + request.getParameter("date1"));
            if (request.getParameterMap().containsKey("cotrimEligibility1")) {
                chroniccare.setCotrimEligibility1(1);
            } else {
                chroniccare.setCotrimEligibility1(0);
            }
            if (request.getParameterMap().containsKey("cotrimEligibility2")) {
                chroniccare.setCotrimEligibility2(1);
            } else {
                chroniccare.setCotrimEligibility2(0);
            }
            if (request.getParameterMap().containsKey("cotrimEligibility3")) {
                chroniccare.setCotrimEligibility3(1);
            } else {
                chroniccare.setCotrimEligibility3(0);
            }
            if (request.getParameterMap().containsKey("cotrimEligibility4")) {
                chroniccare.setCotrimEligibility4(1);
            } else {
                chroniccare.setCotrimEligibility4(0);
            }
            if (request.getParameterMap().containsKey("cotrimEligibility5")) {
                chroniccare.setCotrimEligibility5(1);
            } else {
                chroniccare.setCotrimEligibility5(0);
            }

            String additionalPhdpServices = "";
            if (request.getParameterMap().containsKey("phdp91")) {
                additionalPhdpServices += request.getParameter("phdp91") + ",";
            }
            if (request.getParameterMap().containsKey("phdp92")) {
                additionalPhdpServices += request.getParameter("phdp92") + ",";
            }
            if (request.getParameterMap().containsKey("phdp93")) {
                additionalPhdpServices += request.getParameter("phdp93") + ",";
            }
            if (request.getParameterMap().containsKey("phdp94")) {
                additionalPhdpServices += request.getParameter("phdp94") + ",";
            }

            if (request.getParameterMap().containsKey("phdp95")) {
                additionalPhdpServices += request.getParameter("phdp95") + ",";
            }
            if (request.getParameterMap().containsKey("phdp96")) {
                additionalPhdpServices += request.getParameter("phdp96") + ",";
            }
            if (request.getParameterMap().containsKey("phdp97")) {
                additionalPhdpServices += request.getParameter("phdp97") + ",";
            }
            if (request.getParameterMap().containsKey("phdp98")) {
                additionalPhdpServices += request.getParameter("phdp98") + ",";
            }
            if (request.getParameterMap().containsKey("phdp99")) {
                additionalPhdpServices += request.getParameter("phdp99") + ",";
            }
            if (request.getParameterMap().containsKey("phdp910")) {
                additionalPhdpServices += request.getParameter("phdp910") + ",";
            }
            if (request.getParameterMap().containsKey("phdp911")) {
                additionalPhdpServices += request.getParameter("phdp911") + ",";
            }

            if (!additionalPhdpServices.equals("")) {
                additionalPhdpServices = additionalPhdpServices.substring(0, additionalPhdpServices.lastIndexOf(","));
            }
            chroniccare.setPhdp9ServicesProvided(additionalPhdpServices);
            chroniccare.setTbValues(TbscreenList.getIds());
            chroniccare.setDmValues(DmscreenList.getIds());

            patient.setPatientId(Long.parseLong(request.getParameter("patientId")));
            chroniccare.setPatient(patient);
            chroniccare.setFacilityId(facilityId);
            chroniccare.setUserId(userId);
            chroniccare.setTimeStamp(new Date());
            ChroniccareDAO.save(chroniccare);
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR;
        }
        return SUCCESS;
    }

    // Update chroniccare in database
    public String updateChroniccare() {
        try {
            if (request.getParameterMap().containsKey("cotrimEligibility1")) {
                chroniccare.setCotrimEligibility1(1);
            } else {
                chroniccare.setCotrimEligibility1(0);
            }
            if (request.getParameterMap().containsKey("cotrimEligibility2")) {
                chroniccare.setCotrimEligibility2(1);
            } else {
                chroniccare.setCotrimEligibility2(0);
            }
            if (request.getParameterMap().containsKey("cotrimEligibility3")) {
                chroniccare.setCotrimEligibility3(1);
            } else {
                chroniccare.setCotrimEligibility3(0);
            }
            if (request.getParameterMap().containsKey("cotrimEligibility4")) {
                chroniccare.setCotrimEligibility4(1);
            } else {
                chroniccare.setCotrimEligibility4(0);
            }
            if (request.getParameterMap().containsKey("cotrimEligibility5")) {
                chroniccare.setCotrimEligibility5(1);
            } else {
                chroniccare.setCotrimEligibility5(0);
            }

            String additionalPhdpServices = "";
            if (request.getParameterMap().containsKey("phdp91")) {
                additionalPhdpServices += request.getParameter("phdp91") + ",";
            }
            if (request.getParameterMap().containsKey("phdp92")) {
                additionalPhdpServices += request.getParameter("phdp92") + ",";
            }
            if (request.getParameterMap().containsKey("phdp93")) {
                additionalPhdpServices += request.getParameter("phdp93") + ",";
            }
            if (request.getParameterMap().containsKey("phdp94")) {
                additionalPhdpServices += request.getParameter("phdp94") + ",";
            }

            if (request.getParameterMap().containsKey("phdp95")) {
                additionalPhdpServices += request.getParameter("phdp95") + ",";
            }
            if (request.getParameterMap().containsKey("phdp96")) {
                additionalPhdpServices += request.getParameter("phdp96") + ",";
            }
            if (request.getParameterMap().containsKey("phdp97")) {
                additionalPhdpServices += request.getParameter("phdp97") + ",";
            }
            if (request.getParameterMap().containsKey("phdp98")) {
                additionalPhdpServices += request.getParameter("phdp98") + ",";
            }

            if (request.getParameterMap().containsKey("phdp99")) {
                additionalPhdpServices += request.getParameter("phdp99") + ",";
            }
            if (request.getParameterMap().containsKey("phdp910")) {
                additionalPhdpServices += request.getParameter("phdp910") + ",";
            }
            if (request.getParameterMap().containsKey("phdp911")) {
                additionalPhdpServices += request.getParameter("phdp911") + ",";
            }

            if (!additionalPhdpServices.equals("")) {
                additionalPhdpServices = additionalPhdpServices.substring(0, additionalPhdpServices.lastIndexOf(","));
            }
            chroniccare.setPhdp9ServicesProvided(additionalPhdpServices);
            chroniccare.setTbValues(TbscreenList.getIds());
            chroniccare.setDmValues(DmscreenList.getIds());

            patient.setPatientId(Long.parseLong(request.getParameter("patientId")));
            chroniccare.setPatient(patient);
            chroniccare.setFacilityId(facilityId);
            chroniccare.setUserId(userId);
            chroniccare.setChroniccareId(Long.parseLong(request.getParameter("chroniccareId")));
            chroniccare.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
            ChroniccareDAO.update(chroniccare);
        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    // Delete chroniccare from database
    public String deleteChroniccare() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            Chroniccare care = ChroniccareDAO.find(Long.parseLong(ServletActionContext.getRequest().getParameter("chroniccareId")));
            Patient patient = PatientDAO.find(Long.parseLong(ServletActionContext.getRequest().getParameter("patientId")));
            String entityId = patient.getHospitalNum() + "#" + format.format(care.getDateVisit());
            MonitorService.logEntity(entityId, "chroniccare", 3);
            DeleteService deleteService = new DeleteService();
            if (facilityId > 0L) {
                deleteService.deleteChroniccare(care.getFacilityId(), Long.parseLong(request.getParameter("patientId")),
                        care.getDateVisit());
                return SUCCESS;
            } else {
                return ERROR;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR;
        }
    }

    public String findChroniccare() {
        try {
            new ChroniccareJDBC().findChroniccare();
            chroniccareList = new ChroniccareListBuilder().retrieveChroniccareList();
            findPatient();
        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String retrieveChroniccareList() {
        chroniccareList = new ChroniccareListBuilder().retrieveChroniccareList();
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
     * @return the chroniccareList
     */
    public ArrayList<Map<String, String>> getChroniccareList() {
        return chroniccareList;
    }

    /**
     * @param chroniccareList the chroniccareList to set
     */
    public void setChroniccareList(ArrayList<Map<String, String>> chroniccareList) {
        this.chroniccareList = chroniccareList;
    }

    /**
     * @return the chroniccareId
     */
    public Long getChroniccareId() {
        return chroniccareId;
    }

    /**
     * @param chroniccareId the chroniccareId to set
     */
    public void setChroniccareId(Long chroniccareId) {
        this.chroniccareId = chroniccareId;
    }

    /**
     * @return the chroniccare
     */
    public Chroniccare getChroniccare() {
        return chroniccare;
    }

    /**
     * @param chroniccare the chroniccare to set
     */
    public void setChroniccare(Chroniccare chroniccare) {
        this.chroniccare = chroniccare;
    }

    /**
     * @return the chroniccares
     */
    public Set<Chroniccare> getChroniccares() {
        return chroniccares;
    }

    /**
     * @param chroniccares the chroniccares to set
     */
    public void setChroniccares(Set<Chroniccare> chroniccares) {
        this.chroniccares = chroniccares;
    }
}
