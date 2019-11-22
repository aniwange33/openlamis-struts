/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.exchange.radet;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.dao.hibernate.ClinicDAO;
import org.fhi360.lamis.dao.hibernate.LaboratoryDAO;
import org.fhi360.lamis.dao.hibernate.PatientDAO;
import org.fhi360.lamis.dao.hibernate.PharmacyDAO;
import org.fhi360.lamis.dao.jdbc.RegimenJDBC;
import org.fhi360.lamis.dao.hibernate.RegimenhistoryDAO;
import org.fhi360.lamis.dao.hibernate.StatushistoryDAO;
import org.fhi360.lamis.interceptor.updater.DefaulterAttributeUpdater;
import org.fhi360.lamis.model.Clinic;
import org.fhi360.lamis.model.Facility;
import org.fhi360.lamis.model.Laboratory;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.model.Pharmacy;
import org.fhi360.lamis.model.Regimenhistory;
import org.fhi360.lamis.model.Statushistory;
import org.fhi360.lamis.service.ViralLoadMontiorService;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.Scrambler;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author user1
 */
public class ClientDataUpdateAction extends ActionSupport implements ModelDriven, Preparable {

    private HttpServletRequest request;
    private HttpSession session;
    private long facilityId;
    private long userId;
    private String query;

    private Patient patient;
    private Clinic clinic;
    private Pharmacy pharmacy;
    private Laboratory laboratory;
    private Statushistory statushistory;
    private Regimenhistory regimenhistory;
    private long patientId;

    private String regimentype;
    private String regimen;
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    private Map<String, String> clientDto = new HashMap<String, String>();
    private String status;

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
        pharmacy = new Pharmacy();
        laboratory = new Laboratory();
        statushistory = new Statushistory();
        regimenhistory = new Regimenhistory();
        return patient;
    }

    public String update() {
        Scrambler scrambler = new Scrambler();

        try {
            //Save patient object
            String uniqueId = clientDto.get("uniqueId");
            String surname = scrambler.scrambleCharacters(clientDto.get("surname"));
            String otherNames = scrambler.scrambleCharacters(clientDto.get("otherNames"));
            int age = clientDto.get("age").trim().equals("") ? 0 : Integer.parseInt(clientDto.get("age"));
            String ageUnit = clientDto.get("ageUnit");
            Date dateRegistration = DateUtil.parseStringToDate(clientDto.get("dateRegistration"), "MM/dd/yyyy");
            Date dateBirth = null;
            if (clientDto.get("dateBirth").isEmpty()) {
                dateBirth = DateUtil.addYearMonthDay(dateRegistration, -age, ageUnit);
            } else {
                dateBirth = DateUtil.parseStringToDate(clientDto.get("dateBirth"), "MM/dd/yyyy");
            }
            String gender = clientDto.get("gender");
            String statusRegistration = clientDto.get("statusRegistration");
            String enrollmentSetting = clientDto.get("enrollmentSetting");
            Date dateStarted = DateUtil.parseStringToDate(clientDto.get("dateStarted"), "MM/dd/yyyy");
            String currentStatus = clientDto.get("currentStatus");
            Date dateCurrentStatus = DateUtil.parseStringToDate(clientDto.get("dateCurrentStatus"), "MM/dd/yyyy");
            String clinicStage = clientDto.get("clinicStage");
            String funcStatus = clientDto.get("funcStatus");
            Double cd4 = clientDto.get("cd4").trim().equals("") ? null : Double.parseDouble(clientDto.get("cd4"));
            Double cd4p = clientDto.get("cd4p").trim().equals("") ? null : Double.parseDouble(clientDto.get("cd4p"));
            Date dateLastRefill = DateUtil.parseStringToDate(clientDto.get("dateLastRefill"), "MM/dd/yyyy");
            String regimentypeStart = clientDto.get("regimentypeStart");
            String regimenStart = clientDto.get("regimenStart");
            regimentype = RegimenJDBC.getRegimentype(Long.parseLong(clientDto.get("regimentypeId")));
            regimen = RegimenJDBC.getRegimen(Long.parseLong(clientDto.get("regimenId")));
            int duration = clientDto.get("duration").trim().equals("") ? 0 : Integer.parseInt(clientDto.get("duration"));
            Date dateCurrentViralLoad = DateUtil.parseStringToDate(clientDto.get("dateCurrentViralLoad"), "MM/dd/yyyy");
            Double viralLoad = clientDto.get("viralLoad").trim().equals("") ? null : Double.parseDouble(clientDto.get("viralLoad"));
            String viralLoadIndication = clientDto.get("viralLoadIndication");
            //Determine date of next appointment
            Date dateNextRefill = null;
            if (dateLastRefill != null) {
                dateNextRefill = DateUtil.addDay(dateLastRefill, duration);
            }

            Facility facility = new Facility();
            facility.setFacilityId(facilityId);
            patientId = Long.parseLong(clientDto.get("patientId"));
            patient = PatientDAO.find(patientId);
            patient.setUniqueId(uniqueId);
            patient.setSurname(surname);
            patient.setOtherNames(otherNames);
            patient.setAge(age);
            patient.setAgeUnit(ageUnit);
            patient.setDateBirth(dateBirth);
            patient.setGender(gender);
            patient.setStatusRegistration(statusRegistration);
            patient.setDateRegistration(dateRegistration);
            patient.setEnrollmentSetting(enrollmentSetting);
            patient.setCurrentStatus(currentStatus);
            patient.setDateCurrentStatus(dateCurrentStatus);
            patient.setDateStarted(dateStarted);
            if (!clientDto.get("dateLastRefill").isEmpty()) {
                patient.setDateLastRefill(dateLastRefill);
                patient.setDateNextRefill(dateNextRefill);
                patient.setRegimentype(regimentype);
                patient.setRegimen(regimen);
                patient.setLastRefillDuration(duration);
            }
            if (!clientDto.get("dateCurrentViralLoad").isEmpty()) {
                patient.setDateLastViralLoad(dateCurrentViralLoad);
                patient.setLastViralLoad(viralLoad);
            }
            patient.setUserId(userId);
            patient.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
            PatientDAO.update(patient);

            //Update ART commencement record if exist or save a new clinic record
            if (!clientDto.get("dateStarted").isEmpty()) {
                query = "SELECT clinic_id AS id FROM clinic WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND commence = 1";
                long id = getId(query);
                if (id != 0L) {
                    query = "UPDATE clinic SET date_visit = '" + DateUtil.parseDateToString(dateStarted, "yyyy-MM-dd") + "', cd4 = " + cd4 + ", cd4p = " + cd4p + ", regimentype = '" + regimentypeStart + "', regimen = '" + regimenStart + "', clinic_stage = '" + clinicStage + "', func_status = '" + funcStatus + "' WHERE clinic_id = " + id;
                    executeUpdate(query);
                } else {
                    saveClinic();
                }
            }

            //Save a new pharmacy record if not exist and log in regimen history
            query = "SELECT pharmacy_id AS id FROM pharmacy WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND regimentype_id IN (1, 2, 3, 4, 14) AND date_visit = '" + DateUtil.parseDateToString(dateLastRefill, "yyyy-MM-dd") + "'";
            if (getId(query) == 0L) {
                if (!clientDto.get("dateLastRefill").isEmpty() && !clientDto.get("regimenId").isEmpty() && !clientDto.get("duration").isEmpty()) {
                    savePharmacy();
                    DefaulterAttributeUpdater.nullifyTrackingOutcome(facilityId, patientId, clientDto.get("dateLastRefill"));

                    //Log dispensed ARV in regimenhistory
                    query = "SELECT history_id AS id FROM regimenhistory WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND regimentype = '" + regimentype + "' AND regimen = '" + regimen + "'";
                    if (getId(query) == 0L) {
                        saveRegimenhistory();
                    }
                }
            }

            //Save a new viral load record if not exist
            if (!clientDto.get("dateCurrentViralLoad").isEmpty()) {
                query = "SELECT laboratory_id AS id FROM laboratory WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND labtest_id = 16 AND date_reported = '" + DateUtil.parseDateToString(dateCurrentViralLoad, "yyyy-MM-dd") + "'";
                long id = getId(query);
                if (id != 0L) {
                    query = "UPDATE laboratory SET resultab = '" + clientDto.get("viralLoad") + "' AND comment = '" + viralLoadIndication + "' WHERE laboratory_id = " + id;
                    executeUpdate(query);
                } else {
                    saveLaboratory();
                }
            }

            if (!clientDto.get("dateCurrentStatus").isEmpty() && !clientDto.get("currentStatus").isEmpty()) {
                query = "SELECT history_id AS id FROM statushistory WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_current_status = '" + DateUtil.parseDateToString(dateCurrentStatus, "yyyy-MM-dd") + "'";
                long id = getId(query);
                if (id != 0L) {
                    query = "UPDATE statushistory SET current_status = '" + currentStatus + "' WHERE history_id = " + id;
                    executeUpdate(query);
                } else {
                    saveStatushistory();
                }
            }
            new ViralLoadMontiorService().updateViralLoadDue(Long.parseLong(clientDto.get("patientId")), DateUtil.parseDateToString(dateStarted, "yyyy-MM-dd"));
            updateClientList();
        } catch (Exception exception) {
        }
        return SUCCESS;
    }

    private void saveClinic() {
        clinic.setPatient(patient);
        clinic.setFacilityId(facilityId);
        clinic.setDateVisit(DateUtil.parseStringToDate(clientDto.get("dateStarted"), "MM/dd/yyyy"));
        clinic.setCd4(clientDto.get("cd4").trim().equals("") ? null : Double.parseDouble(clientDto.get("cd4")));
        clinic.setCd4p(clientDto.get("cd4p").trim().equals("") ? null : Double.parseDouble(clientDto.get("cd4p")));
        clinic.setClinicStage(clientDto.get("clinicStage"));
        clinic.setFuncStatus(clientDto.get("funcStatus"));
        clinic.setRegimentype(clientDto.get("regimentypeStart"));
        clinic.setRegimen(clientDto.get("regimenStart"));
        clinic.setCommence(1);
        clinic.setUserId(userId);
        clinic.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
        ClinicDAO.save(clinic);
    }

    private void savePharmacy() {
        pharmacy.setPatient(patient);
        pharmacy.setFacilityId(facilityId);
        pharmacy.setDateVisit(DateUtil.parseStringToDate(clientDto.get("dateLastRefill"), "MM/dd/yyyy"));
        pharmacy.setDuration(clientDto.get("duration").trim().equals("") ? 0 : Integer.parseInt(clientDto.get("duration")));
        pharmacy.setRegimentypeId((Long.parseLong(clientDto.get("regimentypeId"))));
        pharmacy.setRegimenId((Long.parseLong(clientDto.get("regimenId"))));
        pharmacy.setUserId(userId);
        pharmacy.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));

        query = "SELECT drug.name, drug.strength, drug.morning, drug.afternoon, drug.evening, regimendrug.regimendrug_id, regimendrug.regimen_id, regimendrug.drug_id, regimen.regimentype_id "
                + " FROM drug JOIN regimendrug ON regimendrug.drug_id = drug.drug_id JOIN regimen ON regimendrug.regimen_id = regimen.regimen_id WHERE regimen.regimen_id = " + clientDto.get("regimenId");
        try {
            jdbcTemplate.query(query, rs -> {
                while (rs.next()) {
                    pharmacy.setMorning(rs.getDouble("morning"));
                    pharmacy.setAfternoon(rs.getDouble("afternoon"));
                    pharmacy.setEvening(rs.getDouble("evening"));
                    pharmacy.setRegimendrugId(rs.getLong("regimendrug_id"));
                    PharmacyDAO.save(pharmacy);
                }
                return null;
            });
        } catch (Exception exception) {
            
        }
    }

    private void saveStatushistory() {
        statushistory.setPatient(patient);
        statushistory.setFacilityId(facilityId);
        statushistory.setCurrentStatus(clientDto.get("currentStatus"));
        statushistory.setDateCurrentStatus(DateUtil.parseStringToDate(clientDto.get("dateCurrentStatus"), "MM/dd/yyyy"));
        statushistory.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
        StatushistoryDAO.save(statushistory);
    }

    private void saveRegimenhistory() {
        regimenhistory.setPatient(patient);
        regimenhistory.setFacilityId(facilityId);
        regimenhistory.setDateVisit(DateUtil.parseStringToDate(clientDto.get("dateLastRefill"), "MM/dd/yyyy"));
        regimenhistory.setRegimentype(regimentype);
        regimenhistory.setRegimen(regimen);
        regimenhistory.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
        RegimenhistoryDAO.save(regimenhistory);
    }

    private void saveLaboratory() {
        laboratory.setPatient(patient);
        laboratory.setFacilityId(facilityId);
        laboratory.setLabtestId(16);
        laboratory.setDateReported(DateUtil.parseStringToDate(clientDto.get("dateCurrentViralLoad"), "MM/dd/yyyy"));
        laboratory.setDateCollected(DateUtil.parseStringToDate(clientDto.get("dateCollected"), "MM/dd/yyyy"));
        laboratory.setResultab(clientDto.get("viralLoad"));
        laboratory.setComment(clientDto.get("viralLoadIndication"));
        laboratory.setUserId(userId);
        laboratory.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
        LaboratoryDAO.save(laboratory);
    }

    private void updateClientList() {
        ArrayList<Map<String, String>> clientList = new RadetService().retrieveClientList();

        for (int i = 0; i < clientList.size(); i++) {
            String id = (String) clientList.get(i).get("patientId"); // retrieve patient ID from list
            if (id.equals(clientDto.get("patientId"))) {
                clientList.get(i).remove("uniqueId");
                clientList.get(i).put("uniqueId", clientDto.get("uniqueId"));
                clientList.get(i).remove("surname");
                clientList.get(i).put("surname", clientDto.get("surname"));
                clientList.get(i).remove("otherNames");
                clientList.get(i).put("otherNames", clientDto.get("otherNames"));
                clientList.get(i).remove("name");
                clientList.get(i).put("name", clientDto.get("surname") + " " + clientDto.get("otherNames"));
                clientList.get(i).remove("age");
                clientList.get(i).put("age", clientDto.get("age"));
                clientList.get(i).remove("ageUnit");
                clientList.get(i).put("ageUnit", clientDto.get("ageUnit"));
                clientList.get(i).remove("dateRegistration");
                clientList.get(i).put("dateRegistration", clientDto.get("dateRegistration"));
                clientList.get(i).remove("dateBirth");
                clientList.get(i).put("dateBirth", clientDto.get("dateBirth"));
                clientList.get(i).remove("gender");
                clientList.get(i).put("gender", clientDto.get("gender"));
                clientList.get(i).remove("statusRegistration");
                clientList.get(i).put("statusRegistration", clientDto.get("statusRegistration"));
                clientList.get(i).put("enrollmentSetting", clientDto.get("enrollmentSetting"));
                clientList.get(i).remove("dateStarted");
                clientList.get(i).put("dateStarted", clientDto.get("dateStarted"));
                clientList.get(i).remove("currentStatus");
                clientList.get(i).put("currentStatus", clientDto.get("currentStatus"));
                clientList.get(i).remove("dateCurrentStatus");
                clientList.get(i).put("dateCurrentStatus", clientDto.get("dateCurrentStatus"));
                clientList.get(i).remove("clinicStage");
                clientList.get(i).put("clinicStage", clientDto.get("clinicStage"));
                clientList.get(i).remove("funcStatus");
                clientList.get(i).put("funcStatus", clientDto.get("funcStatus"));
                clientList.get(i).remove("cd4");
                clientList.get(i).put("cd4", clientDto.get("cd4"));
                clientList.get(i).remove("cd4p");
                clientList.get(i).put("cd4p", clientDto.get("cd4p"));
                clientList.get(i).remove("dateLastRefill");
                clientList.get(i).put("dateLastRefill", clientDto.get("dateLastRefill"));
                clientList.get(i).remove("duration");
                clientList.get(i).put("duration", clientDto.get("duration"));
                clientList.get(i).remove("regimentype");
                clientList.get(i).put("regimentype", regimentype);
                clientList.get(i).remove("regimen");
                clientList.get(i).put("regimen", regimen);
                clientList.get(i).remove("regimentypeId");
                clientList.get(i).put("regimentypeId", clientDto.get("regimentypeId"));
                clientList.get(i).remove("regimenId");
                clientList.get(i).put("regimenId", clientDto.get("regimenId"));
                clientList.get(i).remove("regimentypeStart");
                clientList.get(i).put("regimentypeStart", clientDto.get("regimentypeStart"));
                clientList.get(i).remove("regimenStart");
                clientList.get(i).put("regimenStart", clientDto.get("regimenStart"));
                clientList.get(i).put("dateCurrentViralLoad", clientDto.get("dateCurrentViralLoad"));
                clientList.get(i).put("dateCollected", clientDto.get("dateCollected"));
                clientList.get(i).put("viralLoad", clientDto.get("viralLoad"));
                clientList.get(i).put("viralLoadIndication", clientDto.get("viralLoadIndication"));
                clientList.get(i).remove("category");
                clientList.get(i).put("category", "5");
            }
        }
        session.setAttribute("clientList", clientList);
    }

    private long getId(String query) {
        long[] id = {0L};
        try {
            jdbcTemplate.query(query, rs -> {
            while (rs.next()) {
                id[0] = rs.getLong("id");
            }
            return null;
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return id[0];
    }

    private void executeUpdate(String query) {
        try {
            jdbcTemplate.execute(query);
        } catch (Exception exception) {
            exception.printStackTrace();
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

    /**
     * @return the clientDto
     */
    public Map<String, String> getClientDto() {
        return clientDto;
    }

    /**
     * @param clientDto the clientDto to set
     */
    public void setClientDto(Map<String, String> clientDto) {
        this.clientDto = clientDto;
    }
}
