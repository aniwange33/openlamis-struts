/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.exchange.radet;

import au.com.bytecode.opencsv.CSVReader;
import java.io.FileReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.dao.hibernate.ClinicDAO;
import org.fhi360.lamis.dao.hibernate.PatientDAO;
import org.fhi360.lamis.dao.hibernate.PharmacyDAO;
import org.fhi360.lamis.dao.hibernate.RegimenhistoryDAO;
import org.fhi360.lamis.dao.hibernate.StatushistoryDAO;
import org.fhi360.lamis.model.Clinic;
import org.fhi360.lamis.model.Facility;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.model.Pharmacy;
import org.fhi360.lamis.model.Regimenhistory;
import org.fhi360.lamis.model.Statushistory;
import org.fhi360.lamis.service.EntityIdentifier;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.JDBCUtil;
import org.fhi360.lamis.utility.PatientNumberNormalizer;

/**
 *
 * @author user10
 */
public class RadetDataParser {

    private String query;
    private JDBCUtil jdbcUtil;
    private PreparedStatement preparedStatement;

    private long facilityId;
    private long patientId;
    private String uniqueId;
    private String hospitalNum;
    private String dateStarted;
    private String dateLastRefill;
    private String duration;
    private String regimenStart;
    private String regimentypeStart;
    private String regimentype;
    private String regimen;
    private String currentStatus;
    private String enrollmentSetting;
    private long regimentypeId;
    private long regimenId;
    private Date dateCurrentStatus;

    private long userId;
    private Patient patient;
    private Clinic clinic;
    private Pharmacy pharmacy;
    private Statushistory statushistory;
    private Regimenhistory regimenhistory;
    private String dbSuffix;

    public RadetDataParser() {
        patient = new Patient();
        clinic = new Clinic();
        pharmacy = new Pharmacy();
        statushistory = new Statushistory();
        regimenhistory = new Regimenhistory();
        RandomStringUtils.randomAlphanumeric(6);
        try {
            this.jdbcUtil = new JDBCUtil();
        } catch (Exception exception) {
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }

    }

    public void parse() {
        ResultSet rs = null;
        facilityId = (Long) ServletActionContext.getRequest().getSession().getAttribute("facilityId");
        EntityIdentifier entityIdentifier = new EntityIdentifier();
        entityIdentifier.createEntities(facilityId, "patient", dbSuffix);

        String contextPath = ServletActionContext.getServletContext().getInitParameter("contextPath");
        String fileName = contextPath + "transfer/radet.csv";
        String[] row = null;
        try {
            CSVReader reader = new CSVReader(new FileReader(fileName));
            while ((row = reader.readNext()) != null) {
                initVariables();
                if (row[1].trim().equals("") && row[5].trim().equals("")) {
                    break;
                }
                uniqueId = row[0];
                hospitalNum = row[1];
                dateStarted = row[5];
                dateLastRefill = row[6];
                duration = row[7];
                regimentypeStart = row[8];
                regimenStart = row[9];
                regimentype = row[10];
                regimen = row[11];
                currentStatus = row[16];
                enrollmentSetting = row[17];

                if (regimentype.contains("Adult.1st.Line")) {
                    regimentype = "ART First Line Adult";
                    regimentypeId = 1;
                } else {
                    if (regimentype.contains("Adult.2nd.Line")) {
                        regimentype = "ART Second Line Adult";
                        regimentypeId = 2;
                    } else {
                        if (regimentype.contains("Peds.1st.Line")) {
                            regimentype = "ART First Line Children";
                            regimentypeId = 3;
                        } else {
                            if (regimentype.contains("Peds.2nd.Line")) {
                                regimentype = "ART Second Line Children";
                                regimentypeId = 4;
                            } else {
                                if (regimentype.contains("Adult.3rd.Line")) {
                                    regimentype = "Salvage Therapy";
                                    regimentypeId = 14;
                                } else {
                                    if (regimentype.contains("Peds.3rd.Line")) {
                                        regimentype = "Salvage Therapy";
                                        regimentypeId = 14;
                                    } else {
                                        regimentype = "";
                                    }
                                }
                            }
                        }
                    }
                }
                //Determine the regimen and regimen ID
                if (!regimentype.trim().isEmpty()) {
                    regimen = regimen == null ? "" : resolveRegimen(regimen);
                    query = "SELECT regimen_id AS id FROM regimen WHERE description = '" + regimen + "' AND regimentype_id = " + regimentypeId;
                    regimenId = getId(query);
                }

                if (regimentypeStart.contains("Adult.1st.Line")) {
                    regimentypeStart = "ART First Line Adult";
                } else {
                    if (regimentypeStart.contains("Adult.2nd.Line")) {
                        regimentypeStart = "ART Second Line Adult";
                    } else {
                        if (regimentypeStart.contains("Peds.1st.Line")) {
                            regimentypeStart = "ART First Line Children";
                        } else {
                            if (regimentypeStart.contains("Peds.2nd.Line")) {
                                regimentypeStart = "ART Second Line Children";
                            } else {
                                if (regimentypeStart.contains("Adult.3rd.Line")) {
                                    regimentypeStart = "Salvage Therapy";
                                } else {
                                    if (regimentypeStart.contains("Peds.3rd.Line")) {
                                        regimentypeStart = "Salvage Therapy";
                                    } else {
                                        regimentypeStart = "";
                                    }
                                }
                            }
                        }
                    }
                }
                if (!regimentypeStart.trim().isEmpty()) {
                    regimenStart = regimenStart == null ? "" : resolveRegimen(regimenStart);
                }

                if (currentStatus.trim().equalsIgnoreCase("Transferred Out")) {
                    currentStatus = "ART Transfer Out";
                } else {
                    if (currentStatus.trim().equalsIgnoreCase("Stopped")) {
                        currentStatus = "Stopped Treatment";
                    } else {
                        if (currentStatus.trim().equalsIgnoreCase("Dead")) {
                            currentStatus = "Known Death";
                        } else {
                            if (currentStatus.trim().equalsIgnoreCase("LTFU")) {
                                currentStatus = "Lost to Follow Up";
                            } else {
                                currentStatus = "";
                            }
                        }
                    }
                }

                int days = duration.trim().isEmpty() ? 0 : Integer.parseInt(duration);
                Date date = DateUtil.parseStringToDate(dateLastRefill, "yyyy-MM-dd");
                dateCurrentStatus = DateUtil.addDay(date, days);

                System.out.println("Hospital Num: " + hospitalNum);
                System.out.println("currentStatus: " + currentStatus);
                System.out.println("DatecurrentStatus: " + dateCurrentStatus);
                System.out.println("Date Refill: " + dateLastRefill);
                System.out.println("Regimen: " + regimen);
                System.out.println("Regimen id: " + regimenId);
                System.out.println("Regimen line Id: " + regimentypeId);
                System.out.println("............................");

                query = "SELECT id_on_server FROM entity_" + dbSuffix + " WHERE TRIM(LEADING '0' FROM hospital_num) = '" + PatientNumberNormalizer.unpadNumber(hospitalNum) + "'";
                patientId = entityIdentifier.getIdOnServer(query); //check if patient exist on the server
                if (patientId != 0) {
                    Facility facility = new Facility();
                    facility.setFacilityId(facilityId);
                    patient = PatientDAO.find(patientId);
                    patient.setUniqueId(uniqueId);
                    patient.setEnrollmentSetting(enrollmentSetting);
                    patient.setDateStarted(DateUtil.parseStringToDate(dateStarted, "yyyy-MM-dd"));
                    patient.setRegimentype(regimentype);
                    patient.setRegimen(regimen);

                    //Update ART commencement record if exist or save a new clinic record
                    if (!dateStarted.isEmpty()) {
                        query = "SELECT clinic_id AS id FROM clinic WHERE patient_id = " + patientId + " AND commence = 1";
                        long id = getId(query);
                        if (id != 0L) {
                            query = "UPDATE clinic SET date_visit = '" + dateStarted + "', regimentype = '" + regimentypeStart + "', regimen = '" + regimenStart + "' WHERE clinic_id = " + id;
                            executeUpdate(query);
                        } else {
                            saveClinic();
                        }
                    }

                    //Update current status of the patient if LTFU, Dead, Transfer Out, Stop 
                    if (!currentStatus.isEmpty()) {
                        query = "SELECT current_status FROM patient WHERE patient_id = " + patientId + " AND current_status = '" + currentStatus + "'";
                        rs = executeQuery(query);
                        if (rs != null && rs.next()) {
                            if (!currentStatus.equalsIgnoreCase(rs.getString("current_status"))) {
                                patient.setDateCurrentStatus(dateCurrentStatus);
                                patient.setCurrentStatus(currentStatus);
                            }
                        }
                        query = "SELECT history_id AS id FROM statushistory WHERE patient_id = " + patientId + " AND date_current_status = '" + dateCurrentStatus + "'";
                        long id = getId(query);
                        if (id != 0L) {
                            query = "UPDATE statushistory SET current_status = '" + currentStatus + "' WHERE history_id = " + id;
                            executeUpdate(query);
                        } else {
                            saveStatushistory();
                        }
                    }

                    //Save a new pharmacy record if not exist and log in regimen history
                    query = "SELECT pharmacy_id AS id FROM pharmacy WHERE patient_id = " + patientId + " AND regimentype_id IN (1, 2, 3, 4, 14) AND date_visit = '" + dateLastRefill + "'";
                    if (getId(query) == 0L) {
                        System.out.println("Saving .......drug" + regimen);
                        if (!dateLastRefill.isEmpty() && regimenId != 0L && !duration.isEmpty()) {
                            savePharmacy();

                            //Log dispensed ARV in regimenhistory
                            query = "SELECT history_id AS id FROM regimenhistory WHERE patient_id = " + patientId + " AND regimentype = '" + regimentype + "' AND regimen = '" + regimen + "'";
                            if (getId(query) == 0L) {
                                saveRegimenhistory();
                            }
                        }
                    }
                    //Update last refill date
                    query = "SELECT patient_id FROM patient WHERE patient_id = " + patientId + " AND date_last_refill > '" + dateLastRefill + "'";
                    rs = executeQuery(query);
                    if (rs == null || !rs.next()) {
                        patient.setDateLastRefill(DateUtil.parseStringToDate(dateLastRefill, "yyyy-MM-dd"));
                    }

                    //Save patient updated record
                    patient.setUserId(userId);
                    patient.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
                    PatientDAO.update(patient);
                }
            }
            reader.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }
    }

    private void initVariables() {
        patientId = 0L;
        uniqueId = "";
        hospitalNum = "";
        dateStarted = "";
        dateLastRefill = "";
        duration = "";
        regimenStart = "";
        regimentypeStart = "";
        regimentype = "";
        regimen = "";
        regimentypeId = 0L;
        regimenId = 0L;
        currentStatus = "";
        dateCurrentStatus = null;
        enrollmentSetting = "";
    }

    private void saveClinic() {
        clinic.setPatient(patient);
        clinic.setFacilityId(facilityId);
        clinic.setDateVisit(DateUtil.parseStringToDate(dateStarted, "yyyy-MM-dd"));
        clinic.setRegimentype(regimentypeStart);
        clinic.setRegimen(regimenStart);
        clinic.setCommence(1);
        clinic.setUserId(userId);
        clinic.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
        ClinicDAO.save(clinic);
    }

    private void savePharmacy() {
        pharmacy.setPatient(patient);
        pharmacy.setFacilityId(facilityId);
        pharmacy.setDateVisit(DateUtil.parseStringToDate(dateLastRefill, "yyyy-MM-dd"));
        pharmacy.setDuration(duration.trim().equals("") ? 0 : Integer.parseInt(duration));
        pharmacy.setRegimentypeId((regimentypeId));
        pharmacy.setRegimenId((regimenId));
        pharmacy.setUserId(userId);
        pharmacy.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));

        query = "SELECT drug.name, drug.strength, drug.morning, drug.afternoon, drug.evening, regimendrug.regimendrug_id, regimendrug.regimen_id, regimendrug.drug_id, regimen.regimentype_id "
                + " FROM drug JOIN regimendrug ON regimendrug.drug_id = drug.drug_id JOIN regimen ON regimendrug.regimen_id = regimen.regimen_id WHERE regimen.regimen_id = " + regimenId;
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
        } catch (Exception exception) {
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }
    }

    private void saveStatushistory() {
        statushistory.setPatient(patient);
        statushistory.setFacilityId(facilityId);
        statushistory.setCurrentStatus(currentStatus);
        statushistory.setDateCurrentStatus(dateCurrentStatus);
        statushistory.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
        StatushistoryDAO.save(statushistory);
    }

    private void saveRegimenhistory() {
        regimenhistory.setPatient(patient);
        regimenhistory.setFacilityId(facilityId);
        regimenhistory.setDateVisit(DateUtil.parseStringToDate(dateLastRefill, "yyyy-MM-dd"));
        regimenhistory.setRegimentype(regimentype);
        regimenhistory.setRegimen(regimen);
        regimenhistory.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
        RegimenhistoryDAO.save(regimenhistory);
    }

    private String resolveRegimen(String regimen) {
        String regimensys = "";
        query = "SELECT regimensys FROM regimenresolver WHERE regimen = '" + regimen + "'";
        try {
            preparedStatement = jdbcUtil.getStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                regimensys = rs.getString("regimensys");
            }
        } catch (Exception exception) {
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }
        return regimensys;
    }

    private long getId(String query) {
        ResultSet rs = null;
        long id = 0L;
        try {
            preparedStatement = jdbcUtil.getStatement(query);
            rs = preparedStatement.executeQuery();
            if (rs.next()) {
                System.out.println("Id found.....");
                id = rs.getLong("id");
            }
        } catch (Exception exception) {
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }
        return id;
    }

    private void executeUpdate(String query) {
        try {
            preparedStatement = jdbcUtil.getStatement(query);
            preparedStatement.executeUpdate();
        } catch (Exception exception) {
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }
    }

    private ResultSet executeQuery(String query) {
        ResultSet rs = null;
        try {
            preparedStatement = jdbcUtil.getStatement(query);
            rs = preparedStatement.executeQuery();
        } catch (Exception exception) {
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }
        return rs;
    }

}
