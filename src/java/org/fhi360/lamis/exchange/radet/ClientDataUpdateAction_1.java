/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.exchange.radet;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.dao.hibernate.ClinicDAO;
import org.fhi360.lamis.dao.hibernate.PatientDAO;
import org.fhi360.lamis.dao.hibernate.PharmacyDAO;
import org.fhi360.lamis.dao.jdbc.RegimenJDBC;
import org.fhi360.lamis.dao.hibernate.RegimenhistoryDAO;
import org.fhi360.lamis.dao.hibernate.StatushistoryDAO;
import org.fhi360.lamis.model.Clinic;
import org.fhi360.lamis.model.Facility;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.model.Pharmacy;
import org.fhi360.lamis.model.Regimenhistory;
import org.fhi360.lamis.model.Statushistory;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.JDBCUtil;
import org.fhi360.lamis.utility.PatientNumberNormalizer;
import org.fhi360.lamis.utility.Scrambler;

/**
 *
 * @author user1
 */
public class ClientDataUpdateAction_1 extends ActionSupport implements ModelDriven, Preparable {
    private HttpServletRequest request;
    private HttpSession session;
    private long facilityId;
    private long userId;
    private String query;

    private Patient patient;
    private Clinic clinic;
    private Pharmacy pharmacy;
    private Statushistory statushistory;
    private Regimenhistory regimenhistory;
    private long patientId;
    
    private String regimentype;
    private String regimen;

    private JDBCUtil jdbcUtil;
    private PreparedStatement preparedStatement;
    
    private Map<String, String> clientDto = new HashMap<String, String>();
    private String status;
    
    @Override    
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
        facilityId = (Long) session.getAttribute("facilityId");                        
        userId = (Long) session.getAttribute("userId");
        try {
            jdbcUtil = new JDBCUtil(); 
        }
        catch (Exception exception) {
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }        
    }
    
    @Override
    public Object getModel() {
        patient = new Patient();
        clinic = new Clinic();
        pharmacy = new Pharmacy();
        statushistory = new Statushistory();
        regimenhistory = new Regimenhistory();
        return patient;        
    }
        
    public String update() {
        try {
            String hospitalNum = clientDto.get("hospitalNum");
            String uniqueId = clientDto.get("uniqueId");
            String surname = clientDto.get("surname");
            String otherNames = clientDto.get("otherNames");
            int age = clientDto.get("age").trim().equals("")? 0 : Integer.parseInt(clientDto.get("age"));
            String ageUnit = clientDto.get("ageUnit");
            Date dateRegistration = DateUtil.parseStringToDate(clientDto.get("dateRegistration"), "MM/dd/yyyy");
            Date dateBirth = null;
            if(clientDto.get("dateBirth").isEmpty()) {
                dateBirth = DateUtil.addYearMonthDay(dateRegistration, -age, ageUnit);
            }
            else {
                dateBirth = DateUtil.parseStringToDate(clientDto.get("dateBirth"), "MM/dd/yyyy");           
            }
            String gender = clientDto.get("gender");
            String maritalStatus = clientDto.get("maritalStatus");
            String address = clientDto.get("address");
            String phone = clientDto.get("phone");
            String state = clientDto.get("state");
            String lga = clientDto.get("lga");
            String statusRegistration = clientDto.get("statusRegistration");
            Date dateStarted = DateUtil.parseStringToDate(clientDto.get("dateStarted"), "MM/dd/yyyy");
            String currentStatus = clientDto.get("currentStatus");
            Date dateCurrentStatus = DateUtil.parseStringToDate(clientDto.get("dateCurrentStatus"), "MM/dd/yyyy");
            String clinicStage = clientDto.get("clinicStage");
            String funcStatus = clientDto.get("funcStatus");
            Double cd4 = clientDto.get("cd4").trim().equals("")? 0.0 : Double.parseDouble(clientDto.get("cd4"));
            Double cd4p = clientDto.get("cd4p").trim().equals("")? 0.0 : Double.parseDouble(clientDto.get("cd4p"));
            Date dateLastRefill = DateUtil.parseStringToDate(clientDto.get("dateLastRefill"), "MM/dd/yyyy");
            String regimentypeStart = clientDto.get("regimentypeStart");
            String regimenStart = clientDto.get("regimenStart");
            regimentype = RegimenJDBC.getRegimentype(Long.parseLong(clientDto.get("regimentypeId")));
            regimen = RegimenJDBC.getRegimen(Long.parseLong(clientDto.get("regimenId")));

            Facility facility = new Facility();
            facility.setFacilityId(facilityId);
            if(clientDto.get("patientId").trim().equals("")) {
                hospitalNum = PatientNumberNormalizer.normalize(hospitalNum, facilityId);
                patient.setHospitalNum(hospitalNum);
                patient.setUniqueId(uniqueId);
                patient.setSurname(surname);
                patient.setOtherNames(otherNames);
                patient.setState(state);
                patient.setLga(lga);
                patient.setAddress(address);
                patient.setPhone(phone);
                patient.setAge(age);
                patient.setAgeUnit(ageUnit);
                patient.setDateBirth(dateBirth);
                patient.setGender(gender);
                patient.setMaritalStatus(maritalStatus);
                patient.setEducation("");
                patient.setOccupation("");
                patient.setNextKin("");
                patient.setAddressKin("");
                patient.setPhoneKin("");
                patient.setRelationKin(""); 
                patient.setStatusRegistration(statusRegistration);
                patient.setDateRegistration(dateRegistration);
                patient.setCurrentStatus(currentStatus);
                patient.setDateCurrentStatus(dateCurrentStatus);
                patient.setDateStarted(dateStarted);
                patient.setDateLastRefill(dateLastRefill);
                patient.setRegimentype(regimentype);
                patient.setRegimen(regimen);
                patient.setFacility(facility);
                patient.setSendMessage(0);
                patient.setUserId(userId);
                patient.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
                scrambleIdentifier();
                patientId = PatientDAO.save(patient);

                //Save other client information
                patient.setPatientId(patientId);                    
                saveClinic();
                savePharmacy();
                saveStatushistory(true);
                saveStatushistory(false);
                saveRegimenhistory();
                updatePatientList();
            }
            else {
                patientId = Long.parseLong(clientDto.get("patientId"));
                patient = PatientDAO.find(patientId);
                patient.setUniqueId(uniqueId);
                patient.setSurname(surname);
                patient.setOtherNames(otherNames);
                patient.setState(state);
                patient.setLga(lga);
                patient.setAddress(address);
                patient.setPhone(phone);
                patient.setAge(age);
                patient.setAgeUnit(ageUnit);
                patient.setDateBirth(dateBirth);
                patient.setGender(gender);
                patient.setMaritalStatus(maritalStatus);
                patient.setStatusRegistration(statusRegistration);
                patient.setDateRegistration(dateRegistration);
                patient.setCurrentStatus(currentStatus);
                patient.setDateCurrentStatus(dateCurrentStatus);
                patient.setDateStarted(dateStarted);
                patient.setDateLastRefill(dateLastRefill);
                patient.setRegimentype(regimentype);
                patient.setRegimen(regimen);
                patient.setUserId(userId);
                patient.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));            
                scrambleIdentifier();
                PatientDAO.update(patient);

                query = "SELECT clinic_id AS id FROM clinic WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND commence = 1";
                long id = getId(query);
                if(id != 0L) {
                    System.out.println("Updating clinic....");
                    query = "UPDATE clinic SET date_visit = '" + DateUtil.parseDateToString(dateStarted, "yyyy-MM-dd") + "', cd4 = " + cd4 + ", cd4p = " + cd4p + ", regimentype = '" + regimentypeStart + "', regimen = '" + regimenStart + "', clinic_stage = '" + clinicStage + "', func_status = '" + funcStatus + "' WHERE clinic_id = " + id;
                    executeUpdate(query);
                }
                else {
                    saveClinic();
                }

                query = "SELECT pharmacy_id AS id FROM pharmacy WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND regimentype_id IN (1, 2, 3, 4, 14) AND date_visit = '" + DateUtil.parseDateToString(dateLastRefill, "yyyy-MM-dd") + "'";
                if(getId(query) == 0L) {
                    savePharmacy();
                }

                query = "SELECT history_id AS id FROM statushistory WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_current_status = '" + DateUtil.parseDateToString(dateCurrentStatus, "yyyy-MM-dd") + "'";
                id = getId(query);
                if(id != 0L) {
                    System.out.println("Updating status....");
                    query = "UPDATE statushistory SET current_status = '" + currentStatus + "' WHERE history_id = " + id;
                    executeUpdate(query);
                }
                else {
                    saveStatushistory(false);
                }

                query = "SELECT history_id AS id FROM regimenhistory WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND regimentype = '" + regimentype + "' AND regimen = '" + regimen + "'";
                if(getId(query) == 0L) {
                    saveRegimenhistory();  
                }
            }
            updatePatientList();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }          
        finally {
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        } 
        return SUCCESS;
    }

    private void saveClinic() {
        clinic.setDateVisit(DateUtil.parseStringToDate(clientDto.get("dateStarted"), "MM/dd/yyyy"));
        clinic.setCd4(clientDto.get("cd4").trim().equals("")? 0.0 : Double.parseDouble(clientDto.get("cd4")));
        clinic.setCd4p(clientDto.get("cd4p").trim().equals("")? 0.0 : Double.parseDouble(clientDto.get("cd4p")));
        clinic.setClinicStage(clientDto.get("clinicStage"));
        clinic.setFuncStatus(clientDto.get("funcStatus"));
        clinic.setRegimentype(clientDto.get("regimentypeStart"));
        clinic.setRegimen(clientDto.get("regimenStart"));
        clinic.setCommence(1);
        clinic.setPatient(patient);
        clinic.setFacilityId(facilityId);
        clinic.setUserId(userId);
        clinic.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));        
        ClinicDAO.save(clinic);
    }
    
    private void savePharmacy() {        
        pharmacy.setDateVisit(DateUtil.parseStringToDate(clientDto.get("dateLastRefill"), "MM/dd/yyyy"));
        pharmacy.setDuration(clientDto.get("duration").trim().equals("")? 0 : Integer.parseInt(clientDto.get("duration")));
        pharmacy.setRegimentypeId((Long.parseLong(clientDto.get("regimentypeId"))));
        pharmacy.setRegimenId((Long.parseLong(clientDto.get("regimenId"))));
        pharmacy.setPatient(patient);
        pharmacy.setFacilityId(facilityId);
        pharmacy.setUserId(userId);
        pharmacy.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));

        query = "SELECT drug.name, drug.strength, drug.morning, drug.afternoon, drug.evening, regimendrug.regimendrug_id, regimendrug.regimen_id, regimendrug.drug_id, regimen.regimentype_id "
                + " FROM drug JOIN regimendrug ON regimendrug.drug_id = drug.drug_id JOIN regimen ON regimendrug.regimen_id = regimen.regimen_id WHERE regimen.regimen_id = " + clientDto.get("regimenId");
        try {
            preparedStatement = jdbcUtil.getStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                pharmacy.setMorning(rs.getDouble("morning"));
                pharmacy.setAfternoon(rs.getDouble("afternoon"));
                pharmacy.setEvening(rs.getDouble("evening"));
                pharmacy.setRegimendrugId(rs.getLong("regimendrug_id"));
                PharmacyDAO.save(pharmacy);
            }            
        }
        catch (Exception exception) {
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }                               
    }
    
    private void saveStatushistory(boolean registration) {
        if(registration){
            statushistory.setCurrentStatus(clientDto.get("statusRegistration"));
            statushistory.setDateCurrentStatus(DateUtil.parseStringToDate(clientDto.get("dateRegistration"), "MM/dd/yyyy"));            
        } 
        else {
            statushistory.setCurrentStatus(clientDto.get("currentStatus"));
            statushistory.setDateCurrentStatus(DateUtil.parseStringToDate(clientDto.get("dateCurrentStatus"), "MM/dd/yyyy"));            
        } 
        statushistory.setPatient(patient);
        statushistory.setFacilityId(facilityId);
        statushistory.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
        StatushistoryDAO.save(statushistory);
    }
    
    private void saveRegimenhistory() {
        regimenhistory.setDateVisit(DateUtil.parseStringToDate(clientDto.get("dateLastRefill"), "MM/dd/yyyy"));
        regimenhistory.setRegimentype(regimentype);
        regimenhistory.setRegimen(regimen);
        regimenhistory.setPatient(patient);
        regimenhistory.setFacilityId(facilityId);
        regimenhistory.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
        RegimenhistoryDAO.save(regimenhistory);                                   
    }
    
    private void updatePatientList() {
        ArrayList<Map<String, String>> clientList = new RadetService().retrieveClientList();
        
        for(int i = 0; i < clientList.size(); i++) {
            String id = (String) clientList.get(i).get("rowcount"); // retrieve rowcount from list
            if(id.equals(clientDto.get("rowcount"))) {
                clientList.get(i).remove("patientId");
                clientList.get(i).put("patientId", Long.toString(patientId));
                clientList.get(i).remove("hospitalNum");
                clientList.get(i).put("hospitalNum", clientDto.get("hospitalNum"));
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
                clientList.get(i).remove("maritalStatus");
                clientList.get(i).put("maritalStatus", clientDto.get("maritalStatus"));
                clientList.get(i).remove("address");
                clientList.get(i).put("address", clientDto.get("address"));
                clientList.get(i).remove("phone");
                clientList.get(i).put("phone", clientDto.get("phone"));
                clientList.get(i).remove("state");
                clientList.get(i).put("state", clientDto.get("state"));
                clientList.get(i).remove("lga");
                clientList.get(i).put("lga", clientDto.get("lga"));
                clientList.get(i).remove("statusRegistration");
                clientList.get(i).put("statusRegistration", clientDto.get("statusRegistration"));
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
                clientList.get(i).remove("updated");
                clientList.get(i).put("updated", "4");                
            }
        }        
        session.setAttribute("clientList", clientList);  
    }
    
    private void scrambleIdentifier() {
        Scrambler scrambler = new Scrambler();
        patient.setSurname(scrambler.scrambleCharacters(patient.getSurname()));
        patient.setOtherNames(scrambler.scrambleCharacters(patient.getOtherNames()));
        patient.setAddress(scrambler.scrambleCharacters(patient.getAddress()));
        patient.setPhone(scrambler.scrambleNumbers(patient.getPhone()));
    }

    
    private long getId(String query) {
        long id = 0L;
        try {
            preparedStatement = jdbcUtil.getStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()) id = rs.getLong("id");
        }
        catch (Exception exception) {
            status = "error";
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }                
        return id;
    }
    
    private void executeUpdate(String query) {
        try {
            preparedStatement = jdbcUtil.getStatement(query);
            preparedStatement.executeUpdate();
        }
        catch (Exception exception) {
            status = "error";
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
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
