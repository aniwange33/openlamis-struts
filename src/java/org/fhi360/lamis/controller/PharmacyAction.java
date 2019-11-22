/**
 *
 * @author AALOZIE
 */
package org.fhi360.lamis.controller;

import org.fhi360.lamis.dao.hibernate.PatientDAO;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.ModelDriven;
import org.fhi360.lamis.model.Pharmacy;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.dao.hibernate.PharmacyDAO;
import org.fhi360.lamis.utility.builder.PharmacyListBuilder;
import org.fhi360.lamis.dao.jdbc.PharmacyJDBC;
import org.fhi360.lamis.dao.jdbc.PrescriptionJDBC;
import org.fhi360.lamis.model.dto.PatientObjHandler;
import org.fhi360.lamis.service.DeleteService;
import org.fhi360.lamis.interceptor.AdrList;
import org.fhi360.lamis.interceptor.updater.PatientRefillAttributeUpdater;
import org.fhi360.lamis.utility.Constants;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.builder.PatientListBuilder;

public class PharmacyAction extends ActionSupport implements ModelDriven, Preparable {

    private Long facilityId;
    private Long patientId;
    private Patient patient;
    private Long pharmacyId;
    private Long userId;
    private Pharmacy pharmacy;
    private Set<Pharmacy> pharmacies = new HashSet<>(0);

    private HttpServletRequest request;
    private HttpSession session;

    private ArrayList<Map<String, String>> pharmacyList = new ArrayList<>();
    private ArrayList<Map<String, String>> dispenserList = new ArrayList<>();
    private ArrayList<Map<String, String>> prescribedList = new ArrayList<>();

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
        pharmacy = new Pharmacy();
        return pharmacy;
    }

    // Save new pharmacy record to database
    public String savePharmacy() {
        try {
            if (facilityId > 0L) {
                saveRecords();
                return SUCCESS;
            } else {
                return ERROR;
            }
        } catch (Exception e) {
            return ERROR;
        }
    }

    // Update pharmacy in database
    public String updatePharmacy() {
        try {
            if (facilityId > 0L) {
                deleteRecords();
                saveRecords();
                return SUCCESS;
            } else {
                return ERROR;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR;
        }
    }

    // Delete pharmacy from database
    public String deletePharmacy() {
        try {
            if (facilityId > 0L) {
                deleteRecords();
                new PatientRefillAttributeUpdater().lastRefillDate(true);
                return SUCCESS;
            } else {
                return ERROR;
            }
        } catch (Exception e) {
            return ERROR;
        }
    }

    // Save new records to database
    private void saveRecords() {

        String regimentypeId = "";
        String regimenId = "";
        String regimendrugId = "";
        Double morning = 0.0;
        Double afternoon = 0.0;
        Double evening = 0.0;
        Integer duration = 0;
        if (request.getParameterMap().containsKey("prescripError")) {
            pharmacy.setPrescripError(1);
        } else {
            pharmacy.setPrescripError(0);
        }
        if (request.getParameterMap().containsKey("adherence")) {
            pharmacy.setAdherence(1);
        } else {
            pharmacy.setAdherence(0);
        }
        //retrieve the dispenser list store in session attribute
        dispenserList = new PharmacyListBuilder().retrieveDispenserList();
        prescribedList = new PharmacyListBuilder().retrievePrescribedList();
        for (Map map : dispenserList) {
            System.out.println(map);
            regimendrugId = (String) map.get("regimendrugId");
            if (request.getParameter("regdrugId").equals(regimendrugId)) {
                regimentypeId = request.getParameter("regimentypeId");
                regimenId = request.getParameter("regimenId");
                morning = (request.getParameter("morning").equals("")) ? 0.0 : Double.parseDouble(request.getParameter("morning"));
                afternoon = (request.getParameter("afternoon").equals("")) ? 0.0 : Double.parseDouble(request.getParameter("afternoon"));
                evening = (request.getParameter("evening").equals("")) ? 0.0 : Double.parseDouble(request.getParameter("evening"));
                duration = (request.getParameter("duration").equals("")) ? 0 : Integer.parseInt(request.getParameter("duration"));
            } else {
                regimentypeId = (String) map.get("regimentypeId");
                regimenId = (String) map.get("regimenId");
                morning = (((String) map.get("morning")).equals("")) ? 0.0 : Double.parseDouble((String) map.get("morning"));
                afternoon = (((String) map.get("afternoon")).equals("")) ? 0.0 : Double.parseDouble((String) map.get("afternoon"));
                evening = (((String) map.get("evening")).equals("")) ? 0.0 : Double.parseDouble((String) map.get("evening"));
                duration = (((String) map.get("duration")).equals("")) ? 0 : Integer.parseInt((String) map.get("duration"));
            }
            patientId = Long.parseLong(request.getParameter("patientId"));
            patient.setPatientId(patientId);
            pharmacy.setPatient(patient);
            pharmacy.setFacilityId(facilityId);
            pharmacy.setRegimentypeId(Long.parseLong(regimentypeId));
            pharmacy.setRegimenId(Long.parseLong(regimenId));
            pharmacy.setRegimendrugId(Long.parseLong(regimendrugId));
            pharmacy.setMorning(morning);
            pharmacy.setAfternoon(afternoon);
            pharmacy.setEvening(evening);
            pharmacy.setDuration(duration);
            pharmacy.setAdrIds(AdrList.getIds());
            pharmacy.setUserId(userId);
            pharmacy.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
            PharmacyDAO.save(pharmacy);

            //Update prescription
            if (session.getAttribute("fromPrescription").equals("true")) {
                PrescriptionJDBC.changeDrugPrescriptionStatus(patientId, facilityId, Integer.parseInt(regimenId), Integer.parseInt(regimentypeId), Constants.Prescription.PRESCRIBED, Constants.Prescription.PRESCRIBED_DISPENSED);
            }
        }
        if (session.getAttribute("fromPrescription").equals("true")) {
            PrescriptionJDBC.changePrescriptionStatusUn(patientId, facilityId, Constants.Prescription.PRESCRIBED, Constants.Prescription.PRESCRIBED_NOT_DISPENSED, "drug");
        }
        session.removeAttribute("prescribedList");
        session.removeAttribute("fromPrescription");

    }

    // Delete records from database
    private void deleteRecords() {
        DeleteService deleteService = new DeleteService();
        Pharmacy pharmacy = PharmacyDAO.find(Long.parseLong(request.getParameter("pharmacyId")));
        deleteService.deletePharmacy(pharmacy.getFacilityId(), Long.parseLong(request.getParameter("patientId")), pharmacy.getDateVisit());
        pharmacy.setPharmacyId(Long.parseLong("0"));
    }

    // Retrieve a pharmacy record in database
    public String findPharmacy() {
        try {
            new PharmacyJDBC().findPharmacy(Long.parseLong(request.getParameter("patientId")), request.getParameter("dateVisit"));
            pharmacyList = new PharmacyListBuilder().retrievePharmacyList();
            findPatient();
        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String retrievePharmacyList() {
        // retrieve the pharmacy record store in session attribute
        pharmacyList = new PharmacyListBuilder().retrievePharmacyList();
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
     * @return the pharmacyId
     */
    public Long getPharmacyId() {
        return pharmacyId;
    }

    /**
     * @param pharmacyId the pharmacyId to set
     */
    public void setPharmacyId(Long pharmacyId) {
        this.pharmacyId = pharmacyId;
    }

    /**
     * @return the pharmacy
     */
    public Pharmacy getPharmacy() {
        return pharmacy;
    }

    /**
     * @param pharmacy the pharmacy to set
     */
    public void setPharmacy(Pharmacy pharmacy) {
        this.pharmacy = pharmacy;
    }

    /**
     * @return the pharmacies
     */
    public Set<Pharmacy> getPharmacies() {
        return pharmacies;
    }

    /**
     * @param pharmacies the pharmacies to set
     */
    public void setPharmacies(Set<Pharmacy> pharmacies) {
        this.pharmacies = pharmacies;
    }

    /**
     * @return the pharmacyList
     */
    public ArrayList<Map<String, String>> getPharmacyList() {
        return pharmacyList;
    }

    /**
     * @param pharmacyList the pharmacyList to set
     */
    public void setPharmacyList(ArrayList<Map<String, String>> pharmacyList) {
        this.pharmacyList = pharmacyList;
    }

    /**
     * @return the dispenserList
     */
    public ArrayList<Map<String, String>> getDispenserList() {
        return dispenserList;
    }

    /**
     * @param dispenserList the dispenserList to set
     */
    public void setDispenserList(ArrayList<Map<String, String>> dispenserList) {
        this.dispenserList = dispenserList;
    }

    public ArrayList<Map<String, String>> getPrescribedList() {
        return prescribedList;
    }

    public void setPrescribedList(ArrayList<Map<String, String>> prescribedList) {
        this.prescribedList = prescribedList;
    }

    private void changePrescriptionStatus(Long patientId, Long facilityId, int parseInt, int parseInt0, Integer PRESCRIBED, Integer PRESCRIBED_DISPENSED) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void changePrescriptionStatusUn(Long patientId, Long facilityId, Integer PRESCRIBED, Integer PRESCRIBED_NOT_DISPENSED) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
