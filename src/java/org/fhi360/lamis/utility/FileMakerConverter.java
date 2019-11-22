package org.fhi360.lamis.utility;

import au.com.bytecode.opencsv.CSVReader;
import java.io.FileReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Map;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.dao.hibernate.ClinicDAO;
import org.fhi360.lamis.dao.hibernate.LaboratoryDAO;
import org.fhi360.lamis.dao.hibernate.PatientDAO;
import org.fhi360.lamis.dao.hibernate.PharmacyDAO;
import org.fhi360.lamis.dao.hibernate.StatushistoryDAO;
import org.fhi360.lamis.dao.jdbc.ClinicJDBC;
import org.fhi360.lamis.dao.jdbc.LaboratoryJDBC;
import org.fhi360.lamis.dao.jdbc.PatientJDBC;
import org.fhi360.lamis.dao.jdbc.PharmacyJDBC;
import org.fhi360.lamis.dao.jdbc.RegimenJDBC;
import org.fhi360.lamis.dao.jdbc.StatusHistoryJDBC;
import org.fhi360.lamis.model.Clinic;
import org.fhi360.lamis.model.Facility;
import org.fhi360.lamis.model.Laboratory;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.model.Pharmacy;
import org.fhi360.lamis.model.Statushistory;
import org.fhi360.lamis.service.DeleteService;

/**
 *
 * @author user10
 */
public class FileMakerConverter {

    private String query;
    private JDBCUtil jdbcUtil;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    private PatientJDBC patientJDBC = new PatientJDBC();
    private ClinicJDBC clinicJDBC = new ClinicJDBC();
    private PharmacyJDBC pharmacyJDBC = new PharmacyJDBC();
    private LaboratoryJDBC laboratoryJDBC = new LaboratoryJDBC();
    private StatusHistoryJDBC statushistoryJDBC = new StatusHistoryJDBC();

    private int facilityId = 3132;
    private Facility facility = new Facility();

    public void convert() {
        facility.setFacilityId(facilityId);
        ServletActionContext.getRequest().getSession().setAttribute("processingStatus", "Complete");
        try {
            jdbcUtil = new JDBCUtil();
            patient();
            clinic();
            pharm();
            lab();
            outcome();
            //cleanupData();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void patient() {
        ServletActionContext.getRequest().getSession().setAttribute("processingStatus", "Patient");
        System.out.println("Processing patient.........");
        String fileName = "C:/LAMIS3/NIMR/patient.csv";
        long patientId = 0;

        String[] row = null;
        int rowcount = 0;
        try {

            CSVReader csvReader = new CSVReader(new FileReader(fileName));
            while ((row = csvReader.readNext()) != null) {
                rowcount++;
                if (rowcount > 1) {
                    String hospitalNum = normalize(row[0].trim());
                    String dateRegistration = row[1].trim();
                    String gender = row[2].trim();
                    String dateBirth = row[3].trim();
                    String education = row[4].trim();
                    String maritalStatus = row[5].trim();
                    String occupation = row[6].trim();
                    String state = row[7].trim();
                    String entryPoint = row[8].trim();
                    String dateConfirmedHiv = row[9].trim();

                    String otherNames = "";
                    String surname = "NIMR" + rowcount;

                    System.out.println("Date Registration: " + dateRegistration);
                    System.out.println("Date Birth: " + dateBirth);
                    System.out.println("Length:  " + dateRegistration.length());

                    if (dateBirth.contains("/")) {
                        dateBirth = DateUtil.formatDateString(dateBirth, "dd/MM/yyyy", "yyyy-MM-dd");
                    }else {
                        dateBirth = DateUtil.formatDateString(dateBirth, "dd-MM-yyyy", "yyyy-MM-dd");
                    }
                    
                    if (dateRegistration.contains("/")) {
                        dateRegistration = DateUtil.formatDateString(dateRegistration, "dd/MM/yyyy", "yyyy-MM-dd");
                    }else  {
                        dateRegistration = DateUtil.formatDateString(dateRegistration, "dd-MM-yyyy", "yyyy-MM-dd");
                    }
                    if (dateConfirmedHiv.contains("/")) {
                        dateConfirmedHiv = DateUtil.formatDateString(dateConfirmedHiv, "dd/MM/yyyy", "yyyy-MM-dd");
                    }else {
                        dateConfirmedHiv = DateUtil.formatDateString(dateConfirmedHiv, "dd-MM-yyyy", "yyyy-MM-dd");
                    }

                    int age = 0;
                    String ageUnit = "";
                    if (!dateBirth.isEmpty() && !dateRegistration.isEmpty()) {
                        Map map = DateUtil.getAge(DateUtil.parseStringToDate(dateBirth, "yyyy-MM-dd"), DateUtil.parseStringToDate(dateRegistration, "yyyy-MM-dd"));
                        age = (int) map.get("age");
                        ageUnit = (String) map.get("ageUnit");
                        String statusRegistration = "HIV+ none ART";
                        String currentStatus = statusRegistration;
                        String dateCurrentStatus = dateRegistration;

                        Patient patient = new Patient();
                        patient.setFacility(facility);
                        patient.setSurname(surname);
                        patient.setAge(age);
                        patient.setAgeUnit(ageUnit);
                        patient.setGender(gender);
                        patient.setDateBirth(DateUtil.parseStringToDate(dateBirth, "yyyy-MM-dd"));
                        patient.setEducation(education);
                        patient.setOccupation(occupation);
                        patient.setMaritalStatus(maritalStatus);
                        patient.setState(state);
                        patient.setCurrentStatus(currentStatus);
                        patient.setDateCurrentStatus(DateUtil.parseStringToDate(dateCurrentStatus, "yyyy-MM-dd"));
                        patient.setDateRegistration(DateUtil.parseStringToDate(dateRegistration, "yyyy-MM-dd"));
                        patient.setUploaded(0);
                        patient.setUserId(1L);
                        patient.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));

                        patientId = patientJDBC.getPatientId(facilityId, hospitalNum);
                        if (patientId == 0L) {
                            long id = PatientDAO.save(patient);
                            Statushistory statushistory = new Statushistory();
                            statushistory.setPatientId(id);
                            statushistory.setFacilityId(facilityId);
                            statushistory.setCurrentStatus(currentStatus);
                            statushistory.setDateCurrentStatus(DateUtil.parseStringToDate(dateCurrentStatus, "yyyy-MM-dd"));
                            statushistory.setUploaded(0);
                            statushistory.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
                        } else {
                            patient.setFacility(facility);
                            PatientDAO.update(patient);
                        }
                    }
                }
            }
            csvReader.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    private void clinic() {
        ServletActionContext.getRequest().getSession().setAttribute("processingStatus", "Clinic");
        System.out.println("Processing clinic.........");
        String fileName = "C:/LAMIS3/NIMR/clinic.csv";
        String[] row = null;
        int rowcount = 0;
        long patientId = 0;
        long clinicId = 0;

        try {
            CSVReader csvReader = new CSVReader(new FileReader(fileName));
            while ((row = csvReader.readNext()) != null) {
                rowcount++;
                if (rowcount > 1) {
                    String hospitalNum = normalize(row[0].trim());
                    String dateVisit = row[1].trim();
                    String bodyWeight = row[2].trim();
                    String systolic = row[3].trim();
                    String diastolic = row[4].trim();
                    String height = row[5].trim();
                    String tbStatus = row[6].trim();
                    String nextAppointment = row[7].trim();


                    if (dateVisit.contains("/")) {
                        dateVisit = DateUtil.formatDateString(dateVisit, "dd/MM/yyyy", "yyyy-MM-dd");
                    }else  {
                        dateVisit = DateUtil.formatDateString(dateVisit, "dd-MM-yyyy", "yyyy-MM-dd");
                    }

                    if(!nextAppointment.isEmpty()) {
                            if (nextAppointment.contains("/")) {
                                nextAppointment = DateUtil.formatDateString(nextAppointment, "dd/MM/yyyy", "yyyy-MM-dd");
                            }else  {
                                nextAppointment = DateUtil.formatDateString(nextAppointment, "dd-MM-yyyy", "yyyy-MM-dd");
                            }                        
                    }

                    if (tbStatus.trim().equalsIgnoreCase("No")) {
                        tbStatus = "No sign or symptoms of TB";
                    }
                    if (tbStatus.trim().equalsIgnoreCase("Yes")) {
                        tbStatus = "Currently on TB treatment";
                    }

                    patientId = patientJDBC.getPatientId(facilityId, hospitalNum);
                    if (!dateVisit.isEmpty() && patientId != 0L) {
                        Clinic clinic = new Clinic();
                        Patient patient = new Patient();
                        patient.setPatientId(patientId);
                        clinic.setPatient(patient);

                        clinic.setFacilityId(facilityId);
                        clinic.setDateVisit(DateUtil.parseStringToDate(dateVisit, "yyyy-MM-dd"));
                        if (StringUtil.isInteger(bodyWeight)) {
                            clinic.setBodyWeight(Double.valueOf(bodyWeight));
                        }
                        if (StringUtil.isInteger(height)) {
                            clinic.setHeight(Double.valueOf(height));
                        }
                        //clinic.setBp(systolic+"/"+diastolic);
                        clinic.setTbStatus(tbStatus);
                        clinic.setNextAppointment(DateUtil.parseStringToDate(nextAppointment, "yyyy-MM-dd"));
                        clinic.setCommence(0);
                        clinic.setUploaded(0);
                        clinic.setUserId(1L);
                        clinic.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));

                        clinicId = clinicJDBC.getClinicId(patientId, DateUtil.parseStringToDate(dateVisit, "yyyy-MM-dd"));
                        if (clinicId == 0L) {
                            ClinicDAO.save(clinic);
                        } else {
                            clinic.setClinicId(clinicId);
                            ClinicDAO.update(clinic);
                        }

                    }
                }
            }
            csvReader.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void pharm() {
        ServletActionContext.getRequest().getSession().setAttribute("processingStatus", "Pharmacy");
        System.out.println("Processing pharmacy.........");
        String fileName = "C:/LAMIS3/NIMR/pharmacy.csv";
        String[] row = null;
        int rowcount = 0;
        long patientId = 0;
        long pharmacyId = 0;

        try {
            CSVReader csvReader = new CSVReader(new FileReader(fileName));
            while ((row = csvReader.readNext()) != null) {
                rowcount++;
                if (rowcount > 1) {
                    String hospitalNum = normalize(row[0].trim());
                    String dateVisit = row[1].trim();
                    String regimenType = row[2].trim();
                    String regimen = row[3].trim();
                    String duration = row[4].trim();

                    if (dateVisit.contains("/")) {
                        dateVisit = DateUtil.formatDateString(dateVisit, "dd/MM/yyyy", "yyyy-MM-dd");
                    }else {
                        dateVisit = DateUtil.formatDateString(dateVisit, "dd-MM-yyyy", "yyyy-MM-dd");
                    }

                    patientId = patientJDBC.getPatientId(facilityId, hospitalNum);
                    Patient patient = PatientDAO.find(patientId);

                    if (!dateVisit.isEmpty() && !regimen.isEmpty() && !duration.isEmpty() && patientId != 0L) {
                        DeleteService deleteService = new DeleteService();
                        deleteService.deletePharmacy(facilityId, patientId, DateUtil.parseStringToDate(dateVisit, "yyyy-MM-dd"));

                        //Determine the regimen line id
                        long regimentypeId = 0L;
                        if (regimenType.equalsIgnoreCase("1st Line")) {
                            if (patient.getAge() > 14) {
                                regimentypeId = 1;
                            }
                        } else {
                            regimentypeId = 3;
                        }
                        if (regimenType.equalsIgnoreCase("2nd Line")) {
                            if (patient.getAge() > 14) {
                                regimentypeId = 2;
                            }
                        } else {
                            regimentypeId = 4;
                        }

                        //Determine the regimen id
                        long regimenId = 0L;
                        query = "SELECT sys_description FROM nimrcodeset WHERE code_set_nm = 'REGIMEN' AND code_description = '" + regimen + "'";
                        resultSet = executeQuery(query);
                        if (resultSet.next()) {
                            regimen = resultSet.getString("sys_description");
                            regimenId = RegimenJDBC.getRegimenId(regimen);
                        }

                        //Determine the regimendrug ids
                        long regimendrugId = 0L;
                        query = "SELECT regimendrug_id FROM regimendrug WHERE regimen_id = " + regimenId;
                        resultSet = executeQuery(query);
                        while (resultSet.next()) {
                            regimendrugId = resultSet.getLong("regimendrug_id");

                            //Save each drug dispensed
                            Pharmacy pharmacy = new Pharmacy();
                            pharmacy.setPatient(patient);

                            pharmacy.setFacilityId(facilityId);
                            pharmacy.setDateVisit(DateUtil.parseStringToDate(dateVisit, "yyyy-MM-dd"));
                            pharmacy.setRegimentypeId(regimentypeId);
                            pharmacy.setRegimenId(regimenId);
                            pharmacy.setRegimendrugId(regimendrugId);
                            pharmacy.setDuration(Integer.valueOf(duration));
                            Date nextAppointment = DateUtil.addDay(DateUtil.parseStringToDate(dateVisit, "yyyy-MM-dd"), Integer.valueOf(duration));
                            pharmacy.setNextAppointment(nextAppointment);
                            pharmacy.setUploaded(0);
                            pharmacy.setUserId(1L);
                            pharmacy.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
                            PharmacyDAO.save(pharmacy);
                        }
                    }
                }
            }
            csvReader.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void lab() {
        ServletActionContext.getRequest().getSession().setAttribute("processingStatus", "Laboratory");
        String fileName = "C:/LAMIS3/nimr/lab1.csv";
        String[] row = null;
        int rowcount = 0;
        long patientId = 0;
        long laboratoryId = 0;

        try {
            CSVReader csvReader = new CSVReader(new FileReader(fileName));
            while ((row = csvReader.readNext()) != null) {
                rowcount++;
                if (rowcount > 1) {
                    String hospitalNum = normalize(row[0].trim());
                    String dateCollected = row[1].trim();
                    String dateReported = row[2].trim();
                    String resultab = row[3].trim();
                    String labno = row[4].trim();
                    System.out.println("Date Collected first.............: " + dateCollected);

                    if (!resultab.equalsIgnoreCase("Invalid")) {

                        if (dateCollected.contains("/")) {
                            dateCollected = DateUtil.formatDateString(dateCollected, "dd/MM/yyyy", "yyyy-MM-dd");
                        } else {
                            dateCollected = DateUtil.formatDateString(dateCollected, "dd-MM-yyyy", "yyyy-MM-dd");
                        }
                        if (dateReported.contains("/")) {
                            dateReported = DateUtil.formatDateString(dateReported, "dd/MM/yyyy", "yyyy-MM-dd");
                        }else{
                            dateReported = DateUtil.formatDateString(dateReported, "dd-MM-yyyy", "yyyy-MM-dd");
                        }

                        System.out.println("Hospital Number..........: " + hospitalNum);
                        System.out.println("Date Collected..............: " + dateCollected);
                        System.out.println("Viral Load Result..........: " + resultab);

                        patientId = patientJDBC.getPatientId(facilityId, hospitalNum);
                        if (!dateReported.isEmpty() && !resultab.isEmpty() && patientId != 0L) {
                            DeleteService deleteService = new DeleteService();
                            deleteService.deleteLaboratory(facilityId, patientId, DateUtil.parseStringToDate(dateReported, "yyyy-MM-dd"));

                            Laboratory laboratory = new Laboratory();
                            Patient patient = new Patient();
                            patient.setPatientId(patientId);
                            laboratory.setPatient(patient);

                            laboratory.setFacilityId(facilityId);
                            laboratory.setLabtestId(16);
                            laboratory.setDateCollected(DateUtil.parseStringToDate(dateCollected, "yyyy-MM-dd"));
                            laboratory.setDateReported(DateUtil.parseStringToDate(dateReported, "yyyy-MM-dd"));
                            if (resultab.length() > 10) {
                                resultab = resultab.substring(0, 9);             //resultab.substring(0, resultab.indexOf("."))
                            }
                            laboratory.setResultab(resultab);
                            //laboratory.setLabno(labno);
                            laboratory.setUploaded(0);
                            laboratory.setUserId(1L);
                            laboratory.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
                            LaboratoryDAO.save(laboratory);
                        }
                    }
                }
            }
            csvReader.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void outcome() {
        ServletActionContext.getRequest().getSession().setAttribute("processingStatus", "Statushistory");
        System.out.println("Processing outcome.........");
        String fileName = "C:/LAMIS3/NIMR/outcome.csv";
        String[] row = null;
        int rowcount = 0;
        long patientId = 0;
        long clinicId = 0;
        long statushistoryId = 0;

        try {
            CSVReader csvReader = new CSVReader(new FileReader(fileName));
            while ((row = csvReader.readNext()) != null) {
                rowcount++;
                if (rowcount > 1) {
                    String hospitalNum = normalize(row[0].trim());
                    String dateStarted = row[1].trim();
                    String dateOutcome = row[2].trim();
                    String outcome = row[3].trim();

                    if (dateStarted.contains("/")) {
                        dateStarted = DateUtil.formatDateString(dateStarted, "dd/MM/yyyy", "yyyy-MM-dd");
                    }else {
                        dateStarted = DateUtil.formatDateString(dateStarted, "dd-MM-yyyy", "yyyy-MM-dd");
                    }                   
                    if (dateOutcome.contains("/")) {
                        dateOutcome = DateUtil.formatDateString(dateOutcome, "dd/MM/yyyy", "yyyy-MM-dd");
                    }else {
                        dateOutcome = DateUtil.formatDateString(dateOutcome, "dd-MM-yyyy", "yyyy-MM-dd");
                    }

                    System.out.println("Date Started ART:" + dateStarted);
                    System.out.println("Outcome Date:" + dateOutcome);

                    if (outcome.equalsIgnoreCase("Dead")) {
                        outcome = "Known Death";
                    }
                    if (outcome.equalsIgnoreCase("Transferred Out")) {
                        outcome = "ART Transfer Out";
                    }
                    if (outcome.equalsIgnoreCase("LTFU")) {
                        outcome = "Lost to Follow Up";
                    }

                    patientId = patientJDBC.getPatientId(facilityId, hospitalNum);
                    if (patientId != 0L) {
                        Patient patient = null;
                        //Save ART commencement
                        if (!dateStarted.isEmpty() && patientId != 0L) {
                            Clinic clinic = new Clinic();
                            patient = PatientDAO.find(patientId);
                            clinic.setPatient(patient);

                            clinic.setFacilityId(facilityId);
                            clinic.setDateVisit(DateUtil.parseStringToDate(dateStarted, "yyyy-MM-dd"));
                            clinic.setCommence(1);
                            clinic.setUploaded(0);
                            clinic.setUserId(1L);
                            clinic.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));

                            clinicId = clinicJDBC.getClinicId(patientId, DateUtil.parseStringToDate(dateStarted, "yyyy-MM-dd"));
                            if (clinicId == 0L) {
                                ClinicDAO.save(clinic);
                            } else {
                                clinic.setClinicId(clinicId);
                                ClinicDAO.update(clinic);
                            }
                            patient.setDateStarted(DateUtil.parseStringToDate(dateStarted, "yyyy-MM-dd"));
                            patient.setDateCurrentStatus(DateUtil.parseStringToDate(dateStarted, "yyyy-MM-dd"));
                            patient.setCurrentStatus("ART Start");
                        }

                        ///Save Outcomes 
                        if (!dateOutcome.isEmpty() && !outcome.isEmpty()) {
                            Statushistory statushistory = new Statushistory();
                            patient = PatientDAO.find(patientId);
                            statushistory.setPatient(patient);

                            statushistory.setDateCurrentStatus(DateUtil.parseStringToDate(dateStarted, "yyyy-MM-dd"));
                            statushistory.setCurrentStatus(outcome);
                            statushistory.setUploaded(0);
                            statushistory.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));

                            statushistoryId = statushistoryJDBC.getStatushistoryId(patientId, DateUtil.parseStringToDate(dateOutcome, "yyyy-MM-dd"));
                            if (statushistoryId == 0L) {
                                StatushistoryDAO.save(statushistory);
                            } else {
                                statushistory.setHistoryId(statushistoryId);
                                StatushistoryDAO.update(statushistory);
                            }

                            patient.setDateCurrentStatus(DateUtil.parseStringToDate(dateOutcome, "yyyy-MM-dd"));
                            patient.setCurrentStatus(outcome);
                        }

                        //Update patient  details
                        PatientDAO.update(patient);
                    }

                }
            }
            csvReader.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void cleanupData() {
        try {
            query = "SELECT clinic_id, patient_id, date_visit FROM clinic WHERE facility_id = " + facilityId + " AND commence = 1 LIMIT 1";
            resultSet = executeQuery(query);
            if (resultSet.next()) {
                long patientId = resultSet.getLong("patient_id");
                String dateStarted = DateUtil.parseDateToString(resultSet.getDate("date_visit"), "yyyy-MM-dd");
                executeUpdate("UPDATE patient SET date_started = '" + dateStarted + "', time_stamp = NOW() WHERE patient_id = " + patientId);
            }

            //new CleanupService().cleanupData();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private String normalize(String hospitalNum) {
        String zeros = "";
        int MAX_LENGTH = 7;
        if (hospitalNum.length() < MAX_LENGTH) {
            for (int i = 0; i < MAX_LENGTH - hospitalNum.length(); i++) {
                zeros = zeros + "0";
            }
        }
        return zeros + hospitalNum;
    }

    private void executeUpdate(String query) {
        try {
            jdbcUtil = new JDBCUtil();
            preparedStatement = jdbcUtil.getStatement(query);
            preparedStatement.executeUpdate();
        } catch (Exception exception) {
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }
    }

    private ResultSet executeQuery(String query) {
        ResultSet rs = null;
        try {
            jdbcUtil = new JDBCUtil();
            preparedStatement = jdbcUtil.getStatement(query);
            rs = preparedStatement.executeQuery();
        } catch (Exception exception) {
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }
        return rs;
    }

}
