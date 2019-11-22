/**
 *
 * @author AALOZIE
 */
package org.fhi360.lamis.report;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.Constants;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.builder.PatientReportListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;

public class PatientReports {

    private String reportingDateBegin;
    private String reportingDateEnd;
    private String reportTitle;

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    private ArrayList<Map<String, Object>> reportList;
    private HashMap parameterMap;
    private long facilityId;

    public PatientReports() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    public ArrayList<Map<String, Object>> listOfPatients() {
        reportTitle = "List of all Patients";
        facilityId = (Long) session.getAttribute("facilityId");
        query = "SELECT patient.*, DATEDIFF(YEAR, date_birth, CURDATE()) AS age FROM patient WHERE facility_id = " + facilityId;

        System.out.println(request.getParameter("dateCurrentStatusBegin"));
        if (!request.getParameter("gender").trim().isEmpty() && !request.getParameter("gender").trim().equals("--All--")) {
            query += " AND gender = '" + request.getParameter("gender") + "'";
        }
        if (!request.getParameter("ageBegin").trim().isEmpty() && !request.getParameter("ageEnd").trim().isEmpty()) {
            query += " AND age >= " + Integer.parseInt(request.getParameter("ageBegin")) + " AND age <= " + Integer.parseInt(request.getParameter("ageEnd"));
        }
        if (!request.getParameter("state").trim().isEmpty()) {
            query += " AND state = '" + request.getParameter("state") + "'";
        }
        if (!request.getParameter("lga").trim().isEmpty()) {
            query += " AND lga = '" + request.getParameter("lga") + "'";
        }

//       if(!request.getParameter("entryPoint").trim().isEmpty()) query += " AND entry_point = '" + request.getParameter("entryPoint") + "'";
        if (!request.getParameter("currentStatus").trim().isEmpty() && !request.getParameter("currentStatus").trim().equals("--All--")) {
            String currentStatus = (request.getParameter("currentStatus").trim().equals("HIV  non ART")) ? "HIV+ non ART" : request.getParameter("currentStatus");
            if (currentStatus.equals("Currently Active")) {
                query += " AND current_status IN (" + Constants.ClientStatus.ON_TREATMENT + ") AND DATEDIFF(DAY, date_last_refill + last_refill_duration, CURDATE()) <= " + Constants.LTFU.PEPFAR + " AND date_started IS NOT NULL";
            } else {
                query += " AND current_status = '" + currentStatus + "'";
            }
        }

        if (!request.getParameter("dateCurrentStatusBegin").trim().isEmpty() && !request.getParameter("dateCurrentStatusEnd").trim().isEmpty()) {
            query += " AND date_current_status >= '" + DateUtil.parseStringToSqlDate(request.getParameter("dateCurrentStatusBegin"), "MM/dd/yyyy") + "' AND date_current_status <= '" + DateUtil.parseStringToSqlDate(request.getParameter("dateCurrentStatusEnd"), "MM/dd/yyyy") + "'";
        }
        if (!request.getParameter("regimentype").trim().isEmpty()) {
            query += " AND regimentype = '" + request.getParameter("regimentype") + "'";
        }
        if (!request.getParameter("dateRegistrationBegin").trim().isEmpty() && !request.getParameter("dateRegistrationEnd").trim().isEmpty()) {
            query += " AND date_registration >= '" + DateUtil.parseStringToSqlDate(request.getParameter("dateRegistrationBegin"), "MM/dd/yyyy") + "' AND date_registration <= '" + DateUtil.parseStringToSqlDate(request.getParameter("dateRegistrationEnd"), "MM/dd/yyyy") + "'";
        }
        if (!request.getParameter("artStartDateBegin").trim().isEmpty() && !request.getParameter("artStartDateEnd").trim().isEmpty()) {
            query += " AND date_started >= '" + DateUtil.parseStringToSqlDate(request.getParameter("artStartDateBegin"), "MM/dd/yyyy") + "' AND date_started <= '" + DateUtil.parseStringToSqlDate(request.getParameter("artStartDateEnd"), "MM/dd/yyyy") + "'";
        }
        if (!request.getParameter("clinicStage").trim().isEmpty()) {
            query += " AND last_clinic_stage = '" + request.getParameter("clinicStage") + "'";
        }
        if (!request.getParameter("cd4Begin").trim().isEmpty() && !request.getParameter("cd4End").trim().isEmpty()) {
            query += " AND last_cd4 >= " + Double.parseDouble(request.getParameter("cd4Begin")) + " AND last_cd4 <= " + Double.parseDouble(request.getParameter("cd4End"));
        }
        if (!request.getParameter("viralloadBegin").trim().isEmpty() && !request.getParameter("viralloadEnd").trim().isEmpty()) {
            query += " AND last_viral_load >= " + Double.parseDouble(request.getParameter("viralloadBegin")) + " AND last_viral_load <= " + Double.parseDouble(request.getParameter("viralloadEnd"));
        }
        query += " ORDER BY current_status";
        System.out.println(query);

        generateReportList(query);
        return reportList;
    }

    public ArrayList<Map<String, Object>> listOfPatientsNotification() {

        facilityId = (Long) session.getAttribute("facilityId");
        Integer entity = Integer.parseInt(request.getParameter("entity").trim());

        switch (entity) {
            case 1:
                reportTitle = "List of clients enrolled but not on treatment";
                query = "SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, state, lga, date_registration, status_registration, current_status, date_current_status, date_started, date_last_cd4, last_cd4, last_cd4p, date_last_viral_load, last_viral_load, date_last_refill, last_refill_duration FROM patient WHERE facility_id = " + facilityId + " AND current_status IN ('HIV+ non ART', 'ART Start', 'ART Restart', 'ART Transfer In', 'Pre-ART Transfer In') AND date_started IS NULL";
                break;

            case 2:
                reportTitle = "List of clients who are lost to follow-up unconfirmed";
                query = "SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, state, lga, date_registration, status_registration, current_status, date_current_status, date_started, date_last_cd4, last_cd4, last_cd4p, date_last_viral_load, last_viral_load, date_last_refill, last_refill_duration FROM patient WHERE facility_id = " + facilityId + " AND current_status IN ('ART Start', 'ART Restart', 'ART Transfer In') AND DATEDIFF(DAY, date_last_refill + last_refill_duration, CURDATE()) > " + Constants.LTFU.PEPFAR + " AND date_started IS NOT NULL";
                break;

//            case 3:
//                reportTitle = "List of clients on treatment but no first ARV dispensed";
//             query = "SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, state, lga, date_registration, status_registration, current_status, date_current_status, date_started FROM patient WHERE facility_id = " + facilityId + " AND current_status IN ('ART Start', 'ART Restart', 'ART Transfer In') AND date_last_refill IS NULL ";
//            break;
            case 3:
                reportTitle = "List of clients on treatment who are due for viral load test";
                query = "SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, state, lga, date_registration, status_registration, current_status, date_current_status, date_started, date_last_cd4, last_cd4, last_cd4p, date_last_viral_load, last_viral_load, date_last_refill, last_refill_duration FROM patient WHERE facility_id = " + facilityId + " AND viral_load_due_date <= CURDATE()";
                break;

            case 4:
                reportTitle = "List of clients on treatment with viral load un-suppressed.";
                query = "SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, state, lga, date_registration, status_registration, current_status, date_current_status, date_started, date_last_cd4, last_cd4, last_cd4p, date_last_viral_load, last_viral_load, date_last_refill, last_refill_duration FROM patient WHERE facility_id = " + facilityId + " AND current_status IN ('ART Start', 'ART Restart', 'ART Transfer In') AND last_viral_load >=1000";
                break;
        }
        query += " ORDER BY patient.current_status";

        System.out.println("The query is: " + query);

        generateReportList(query);
        return reportList;
    }

    public ArrayList<Map<String, Object>> caseManagerClientsList() {

        facilityId = (Long) session.getAttribute("facilityId");
        Long casemanagerId = Long.parseLong(request.getParameter("casemanagerId").trim());
        String[] casemanagerName = {""};

        String caseManagerQuery = "SELECT fullname from casemanager WHERE casemanager_id = " + casemanagerId;
        try {
            jdbcTemplate.query(caseManagerQuery, internalResultSet -> {
                while (internalResultSet.next()) {
                    casemanagerName[0] = internalResultSet.getString("fullname");
                }
                return null;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        reportTitle = "List of clients assigned to " + casemanagerName[0];
        query = "SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, state, lga, date_registration, status_registration, current_status, date_current_status, date_started, date_last_cd4, last_cd4, last_cd4p, date_last_viral_load, last_viral_load, date_last_refill, last_refill_duration FROM patient WHERE facility_id = " + facilityId + " AND casemanager_id = " + casemanagerId;
        query += " ORDER BY current_status";

        //System.out.println("The query is: "+query);
        generateReportList(query);
        return reportList;
    }

    public ArrayList<Map<String, Object>> currentOnCare() {
        reportTitle = "Patients Currently on Care (ART & Pre-ART)";
        facilityId = (Long) session.getAttribute("facilityId");
        query = "SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, state, lga, date_registration, status_registration, current_status, date_current_status, date_started, date_last_cd4, last_cd4, last_cd4p, date_last_viral_load, last_viral_load, date_last_refill, last_refill_duration FROM patient WHERE facility_id = " + facilityId + " AND current_status IN (" + Constants.ClientStatus.ON_CARE + ") ORDER BY current_status";
        generateReportList(query);
        return reportList;
    }

    public ArrayList<Map<String, Object>> currentOnTreatment() {
        reportTitle = "Patients Currently on Treatment (ART)";
        facilityId = (Long) session.getAttribute("facilityId");
        query = "SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, state, lga, date_registration, status_registration, current_status, date_current_status, date_started, date_last_cd4, last_cd4, last_cd4p, date_last_viral_load, last_viral_load, date_last_refill, last_refill_duration FROM patient WHERE facility_id = " + facilityId + " AND current_status IN (" + Constants.ClientStatus.ON_TREATMENT + ") AND DATEDIFF(DAY, date_last_refill + last_refill_duration, CURDATE()) <= " + Constants.LTFU.GON + " AND date_started IS NOT NULL";
        generateReportList(query);
        return reportList;
    }

    public ArrayList<Map<String, Object>> lostUnconfirmedPEPFAR() {
        reportTitle = "Patients Lost to Follow Up Unconfirmed - PEPFAR";
        facilityId = (Long) session.getAttribute("facilityId");
        query = "SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, state, lga, date_registration, status_registration, current_status, date_current_status, date_started, date_last_cd4, last_cd4, last_cd4p, date_last_viral_load, last_viral_load, date_last_refill, last_refill_duration FROM patient WHERE facility_id = " + facilityId + " AND current_status IN (" + Constants.ClientStatus.ON_TREATMENT + ") AND DATEDIFF(DAY, date_last_refill + last_refill_duration, CURDATE()) > " + Constants.LTFU.PEPFAR + " AND date_started IS NOT NULL ORDER BY current_status";
        generateReportList(query);
        return reportList;
    }

    public ArrayList<Map<String, Object>> lostUnconfirmedGON() {
        reportTitle = "Patients Lost to Follow Up Unconfirmed - GON";
        facilityId = (Long) session.getAttribute("facilityId");
        query = "SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, state, lga, date_registration, status_registration, current_status, date_current_status, date_started, date_last_cd4, last_cd4, last_cd4p, date_last_viral_load, last_viral_load, date_last_refill, last_refill_duration FROM patient WHERE facility_id = " + facilityId + " AND current_status IN ('ART Start', 'ART Restart', 'ART Transfer In') AND DATEDIFF(DAY, date_last_refill + last_refill_duration, CURDATE()) > " + Constants.LTFU.GON + " AND date_started IS NOT NULL ORDER BY current_status";
        generateReportList(query);
        return reportList;
    }

    public ArrayList<Map<String, Object>> appointment() {
        reportingDateBegin = DateUtil.formatDateString(request.getParameter("reportingDateBegin"), "MM/dd/yyyy", "yyyy-MM-dd");
        reportingDateEnd = DateUtil.formatDateString(request.getParameter("reportingDateEnd"), "MM/dd/yyyy", "yyyy-MM-dd");
        facilityId = (Long) session.getAttribute("facilityId");

        if (request.getParameter("reportType").equals("1")) {
            reportTitle = "Patients for Refill Appointment From " + reportingDateBegin + " To " + reportingDateEnd;
            query = "SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, date_next_refill AS date_visit, current_status FROM patient WHERE facility_id = " + facilityId + " AND current_status NOT IN ('HIV+ non ART', 'Pre-ART Transfer Out', 'ART Transfer Out', 'Known Death') AND date_next_refill >= '" + reportingDateBegin + "' AND date_next_refill <= '" + reportingDateEnd + "'";
        }
        if (request.getParameter("reportType").equals("2")) {
            reportTitle = "Patients for Clinic Appointment From " + reportingDateBegin + " To " + reportingDateEnd;
            query = "SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, date_next_clinic AS date_visit, current_status FROM patient WHERE facility_id = " + facilityId + " AND current_status NOT IN ('Pre-ART Transfer Out', 'ART Transfer Out', 'Known Death') AND date_next_clinic >= '" + reportingDateBegin + "' AND date_next_clinic <= '" + reportingDateEnd + "'";
        }
        if (request.getParameter("reportType").equals("3")) {
            reportTitle = "Patients for Tracking 'Return' Appointment From " + reportingDateBegin + " To " + reportingDateEnd;
            query = "SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, agreed_date AS date_visit, current_status FROM patient WHERE facility_id = " + facilityId + " AND current_status NOT IN ('Pre-ART Transfer Out', 'ART Transfer Out', 'Lost to Follow Up', 'Stopped Treatment', 'Known Death') AND agreed_date >= '" + reportingDateBegin + "' AND agreed_date <= '" + reportingDateEnd + "'";
        }

        generateReportList(query);
        return reportList;
    }

    public ArrayList<Map<String, Object>> visit() {
        reportingDateBegin = DateUtil.formatDateString(request.getParameter("reportingDateBegin"), "MM/dd/yyyy", "yyyy-MM-dd");
        reportingDateEnd = DateUtil.formatDateString(request.getParameter("reportingDateEnd"), "MM/dd/yyyy", "yyyy-MM-dd");
        facilityId = (Long) session.getAttribute("facilityId");

        if (request.getParameter("reportType").equals("4")) {
            reportTitle = "Patients Refill Visit From " + reportingDateBegin + " To " + reportingDateEnd;
            query = "SELECT DISTINCT pharmacy.patient_id, pharmacy.facility_id, pharmacy.date_visit, pharmacy.regimentype_id, pharmacy.regimen_id, pharmacy.duration, pharmacy.next_appointment, patient.hospital_num, patient.surname, patient.other_names, patient.gender, DATEDIFF(YEAR, patient.date_birth, CURDATE()) AS age, patient.phone, patient.address, patient.current_status, regimentype.description AS regimentype, regimen.description AS regimen "
                    + " FROM pharmacy JOIN patient ON pharmacy.patient_id = patient.patient_id JOIN regimentype ON pharmacy.regimentype_id = regimentype.regimentype_id JOIN regimen ON pharmacy.regimen_id = regimen.regimen_id WHERE pharmacy.regimentype_id IN (1, 2, 3, 4, 14) AND pharmacy.facility_id = " + facilityId + " AND pharmacy.date_visit >= '" + reportingDateBegin + "' AND pharmacy.date_visit <= '" + reportingDateEnd + "'";
        }

        if (request.getParameter("reportType").equals("3")) {
            reportTitle = "Patients Clinic Visit From " + reportingDateBegin + " To " + reportingDateEnd;
            query = "SELECT DISTINCT clinic.patient_id, clinic.facility_id, clinic.date_visit, clinic.clinic_stage, clinic.tb_status, clinic.next_appointment, patient.hospital_num, patient.surname, patient.other_names, patient.gender, DATEDIFF(YEAR, patient.date_birth, CURDATE()) AS age, patient.phone, patient.address, patient.current_status FROM clinic JOIN patient ON clinic.patient_id = patient.patient_id WHERE clinic.facility_id = " + facilityId + " AND clinic.date_visit >= '" + reportingDateBegin + "' AND clinic.date_visit <= '" + reportingDateEnd + "'";
        }

        generateReportList(query);
        return reportList;
    }

    public ArrayList<Map<String, Object>> defaulters() {
        reportingDateBegin = DateUtil.formatDateString(request.getParameter("reportingDateBegin"), "MM/dd/yyyy", "yyyy-MM-dd");
        reportingDateEnd = DateUtil.formatDateString(request.getParameter("reportingDateEnd"), "MM/dd/yyyy", "yyyy-MM-dd");
        facilityId = (Long) session.getAttribute("facilityId");

        if (request.getParameter("reportType").equals("6")) {
            reportTitle = "List of Missed Refill Appointment (defaulters)";

            //Retrieve all refill appointments for the period 
            executeUpdate("DROP TABLE IF EXISTS schedule");
            query = "CREATE TEMPORARY TABLE schedule AS SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, current_status, date_current_status, date_last_refill, date_last_clinic, date_next_clinic, date_next_refill FROM patient WHERE facility_id = " + facilityId + " AND date_next_refill >= '" + reportingDateBegin + "' AND date_next_refill <= '" + reportingDateEnd + "'";
            executeUpdate(query);

            //query = "CREATE TEMPORARY TABLE visit AS SELECT DISTINCT patient_id FROM clinic WHERE facility_id = " + facilityId + " AND date_visit >= '" + reportingDateBegin + "' AND date_visit <= '" + reportingDateEnd + "' UNION  SELECT DISTINCT patient_id FROM pharmacy WHERE facility_id = " + facilityId + " AND date_visit >= '" + reportingDateBegin + "' AND date_visit <= '" + reportingDateEnd + "'";
            //Retrieve all refill visits for the period
            executeUpdate("DROP TABLE IF EXISTS visit");
            query = "CREATE TEMPORARY TABLE visit AS SELECT DISTINCT patient_id FROM pharmacy WHERE facility_id = " + facilityId + " AND date_visit >= '" + reportingDateBegin + "' AND date_visit <= '" + reportingDateEnd + "'";
            executeUpdate(query);
        }

        if (request.getParameter("reportType").equals("7")) {
            reportTitle = "List of Missed Clinic Appointment (Defaulters)";

            //Retrieve all refill appointments for the period 
            executeUpdate("DROP TABLE IF EXISTS schedule");
            query = "CREATE TEMPORARY TABLE schedule AS SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, current_status, date_current_status, date_last_refill, date_last_clinic, date_next_clinic, date_next_refill FROM patient WHERE facility_id = " + facilityId + " AND date_next_clinic >= '" + reportingDateBegin + "' AND date_next_clinic <= '" + reportingDateEnd + "'";
            executeUpdate(query);

            //Retrieve all refill visits for the period
            executeUpdate("DROP TABLE IF EXISTS visit");
            query = "CREATE TEMPORARY TABLE visit AS SELECT DISTINCT patient_id FROM clinic WHERE facility_id = " + facilityId + " AND date_visit >= '" + reportingDateBegin + "' AND date_visit <= '" + reportingDateEnd + "'";
            executeUpdate(query);
        }

        if (request.getParameter("reportType").equals("8")) {
            reportTitle = "List of Missed Tracking 'Return' Appointment (based on agreed date of return)";

            //Retrieve all tracking 'return' appointments for the period 
            executeUpdate("DROP TABLE IF EXISTS schedule");
            query = "CREATE TEMPORARY TABLE schedule AS SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, current_status, date_current_status, date_last_refill, date_last_clinic, date_next_clinic, date_next_refill FROM patient WHERE facility_id = " + facilityId + " AND agreed_date >= '" + reportingDateBegin + "' AND agreed_date <= '" + reportingDateEnd + "'";
            executeUpdate(query);

            //Retrieve all refill or clinic visits for the period
            executeUpdate("DROP TABLE IF EXISTS visit");
            query = "CREATE TEMPORARY TABLE visit AS SELECT DISTINCT patient_id FROM clinic WHERE facility_id = " + facilityId + " AND date_visit >= '" + reportingDateBegin + "' AND date_visit <= '" + reportingDateEnd + "' UNION SELECT DISTINCT patient_id FROM pharmacy WHERE facility_id = " + facilityId + " AND date_visit >= '" + reportingDateBegin + "' AND date_visit <= '" + reportingDateEnd + "'";
            executeUpdate(query);
        }

        //Retrieve patients in schedule but not in visit 
        query = "SELECT * FROM schedule WHERE patient_id NOT IN (SELECT DISTINCT patient_id FROM visit)";
        generateReportList(query);
        return reportList;
    }

    public ArrayList<Map<String, Object>> clientAppointment() {
        reportingDateBegin = DateUtil.formatDateString(request.getParameter("reportingDateBegin"), "MM/dd/yyyy", "yyyy-MM-dd");
        reportingDateEnd = DateUtil.formatDateString(request.getParameter("reportingDateEnd"), "MM/dd/yyyy", "yyyy-MM-dd");
        facilityId = (Long) session.getAttribute("facilityId");
        Long casemanagerId = Long.parseLong(request.getParameter("casemanagerId").trim());
        String[] casemanagerName = {""};

        String caseManagerQuery = "SELECT fullname from casemanager WHERE casemanager_id = " + casemanagerId;
        try {
            jdbcTemplate.query(caseManagerQuery, internalResultSet -> {
                while (internalResultSet.next()) {
                    casemanagerName[0] = internalResultSet.getString("fullname");
                }
                return null;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        reportTitle = casemanagerName[0] + "'s Clients Due For Refill Appointment From " + reportingDateBegin + " To " + reportingDateEnd;
        query = "SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, date_next_refill AS date_visit, current_status FROM patient WHERE facility_id = " + facilityId + " AND casemanager_id = " + casemanagerId + "AND date_next_refill IS NOT NULL AND date_next_refill >= '" + reportingDateBegin + "' AND date_next_refill <= '" + reportingDateEnd + "'";

        generateReportList(query);
        return reportList;
    }

    public ArrayList<Map<String, Object>> defaulterRefill() {
        reportTitle = "List of Defaulters for ARV Refill";
        facilityId = (Long) session.getAttribute("facilityId");
        query = "SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, current_status, date_current_status, date_last_refill, date_last_clinic, date_next_clinic AS date_visit FROM patient WHERE facility_id = " + facilityId + " AND ((current_status IN ('ART Start', 'ART Restart', 'ART Transfer In') AND DATEDIFF(DAY, date_next_refill, CURDATE()) >= 1) OR (current_status IN ('Lost to Follow Up', 'Stopped Treatment') AND DATEDIFF(DAY, agreed_date, CURDATE()) >= 1)) AND date_started IS NOT NULL ORDER BY current_status";
        generateReportList(query);
        return reportList;
    }

    public ArrayList<Map<String, Object>> trackingOutcome() {
        reportTitle = "List of TX-ML Patients";
        facilityId = (Long) session.getAttribute("facilityId");
        String outcome = request.getParameter("outcome");
        query = "SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, current_status, date_current_status, date_last_refill, date_last_clinic, date_next_clinic AS date_visit, outcome, date_tracked, agreed_date, cause_death FROM patient WHERE facility_id = " + facilityId + " AND outcome = '" + outcome + "' ORDER BY current_status";
        if (outcome.equalsIgnoreCase("All")) {
            query = "SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, current_status, date_current_status, date_last_refill, date_last_clinic, date_next_clinic AS date_visit, outcome, date_tracked, agreed_date, cause_death FROM patient WHERE facility_id = " + facilityId + " AND outcome IN ('" + Constants.TxMlStatus.TX_ML_DIED + "', '" + Constants.TxMlStatus.TX_ML_TRANSFER + "', '" + Constants.TxMlStatus.TX_ML_TRACED + "', '" + Constants.TxMlStatus.TX_ML_NOT_TRACED + "') ORDER BY current_status";
        }
        if (outcome.equalsIgnoreCase(Constants.TxMlStatus.TX_ML_NOT_TRACED)) {
            query = "SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, current_status, date_current_status, date_last_refill, date_last_clinic, date_next_clinic AS date_visit, outcome, date_tracked, agreed_date, cause_death FROM patient WHERE facility_id = " + facilityId + " AND ((current_status IN ('ART Start', 'ART Restart', 'ART Transfer In') AND DATEDIFF(DAY, date_next_refill, CURDATE()) >= 1) OR (current_status IN ('Lost to Follow Up', 'Stopped Treatment') AND DATEDIFF(DAY, agreed_date, CURDATE()) >= 1)) AND date_started IS NOT NULL AND (outcome IS NULL OR outcome = '') ORDER BY current_status";
        }
        generateReportList(query);
        return reportList;
    }

    public ArrayList<Map<String, Object>> clientDefaulterRefill() {
        reportingDateBegin = DateUtil.formatDateString(request.getParameter("reportingDateBegin"), "MM/dd/yyyy", "yyyy-MM-dd");
        reportingDateEnd = DateUtil.formatDateString(request.getParameter("reportingDateEnd"), "MM/dd/yyyy", "yyyy-MM-dd");
        facilityId = (Long) session.getAttribute("facilityId");

        facilityId = (Long) session.getAttribute("facilityId");
        Long casemanagerId = Long.parseLong(request.getParameter("casemanagerId").trim());
        String[] casemanagerName = {""};

        String caseManagerQuery = "SELECT fullname from casemanager WHERE casemanager_id = " + casemanagerId;
        try {
            jdbcTemplate.query(caseManagerQuery, internalResultSet -> {
                while (internalResultSet.next()) {
                    casemanagerName[0] = internalResultSet.getString("fullname");
                }
                return null;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (request.getParameter("reportType").equals("6")) {
            reportTitle = "List of Missed Refill Appointment (defaulters) for Case Manager :" + casemanagerName[0];

            //Retrieve all refill appointments for the period 
            executeUpdate("DROP TABLE IF EXISTS schedule");
            query = "CREATE TEMPORARY TABLE schedule AS SELECT DISTINCT patient_id, hospital_num, surname, casemanager_id, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, current_status, date_current_status, date_last_refill, date_last_clinic, date_next_clinic, date_next_refill FROM patient WHERE facility_id = " + facilityId + " AND date_next_refill >= '" + reportingDateBegin + "' AND date_next_refill <= '" + reportingDateEnd + "' AND casemanager_id = " + casemanagerId + "";
            executeUpdate(query);

            //query = "CREATE TEMPORARY TABLE visit AS SELECT DISTINCT patient_id FROM clinic WHERE facility_id = " + facilityId + " AND date_visit >= '" + reportingDateBegin + "' AND date_visit <= '" + reportingDateEnd + "' UNION  SELECT DISTINCT patient_id FROM pharmacy WHERE facility_id = " + facilityId + " AND date_visit >= '" + reportingDateBegin + "' AND date_visit <= '" + reportingDateEnd + "'";
            //Retrieve all refill visits for the period
            executeUpdate("DROP TABLE IF EXISTS visit");
            query = "CREATE TEMPORARY TABLE visit AS SELECT DISTINCT patient_id FROM pharmacy WHERE facility_id = " + facilityId + " AND date_visit >= '" + reportingDateBegin + "' AND date_visit <= '" + reportingDateEnd + "'";
            executeUpdate(query);
        }

        if (request.getParameter("reportType").equals("7")) {
            reportTitle = "List of Missed Clinic Appointment (Defaulters) for Case Manager :" + casemanagerName[0];

            //Retrieve all refill appointments for the period 
            executeUpdate("DROP TABLE IF EXISTS schedule");
            query = "CREATE TEMPORARY TABLE schedule AS SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, casemanager_id, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, current_status, date_current_status, date_last_refill, date_last_clinic, date_next_clinic, date_next_refill FROM patient WHERE facility_id = " + facilityId + " AND date_next_clinic >= '" + reportingDateBegin + "' AND date_next_clinic <= '" + reportingDateEnd + "'  AND casemanager_id = " + casemanagerId + "";
            executeUpdate(query);

            //Retrieve all refill visits for the period
            executeUpdate("DROP TABLE IF EXISTS visit");
            query = "CREATE TEMPORARY TABLE visit AS SELECT DISTINCT patient_id FROM clinic WHERE facility_id = " + facilityId + " AND date_visit >= '" + reportingDateBegin + "' AND date_visit <= '" + reportingDateEnd + "'";
            executeUpdate(query);
        }

        if (request.getParameter("reportType").equals("8")) {
            reportTitle = "List of Missed Tracking 'Return' Appointment (based on agreed date of return ) for Case Manager :" + casemanagerName[0];

            //Retrieve all tracking 'return' appointments for the period 
            executeUpdate("DROP TABLE IF EXISTS schedule");
            query = "CREATE TEMPORARY TABLE schedule AS SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, casemanager_id DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, current_status, date_current_status, date_last_refill, date_last_clinic, date_next_clinic, date_next_refill FROM patient WHERE facility_id = " + facilityId + " AND agreed_date >= '" + reportingDateBegin + "' AND agreed_date <= '" + reportingDateEnd + "' AND casemanager_id = " + casemanagerId + "";
            executeUpdate(query);

            //Retrieve all refill or clinic visits for the period
            executeUpdate("DROP TABLE IF EXISTS visit");
            query = "CREATE TEMPORARY TABLE visit AS SELECT DISTINCT patient_id FROM clinic WHERE facility_id = " + facilityId + " AND date_visit >= '" + reportingDateBegin + "' AND date_visit <= '" + reportingDateEnd + "' UNION SELECT DISTINCT patient_id FROM pharmacy WHERE facility_id = " + facilityId + " AND date_visit >= '" + reportingDateBegin + "' AND date_visit <= '" + reportingDateEnd + "'";
            executeUpdate(query);
        }

        //Retrieve patients in schedule but not in visit 
        query = "SELECT * FROM schedule WHERE patient_id NOT IN (SELECT DISTINCT patient_id FROM visit)";
        generateReportList(query);
        return reportList;
    }

    //One day defualters...
//    public ArrayList<Map<String, Object>> clientDefaulterRefill(){
//        
//        
//        reportTitle = "List of Defaulters for ARV Refill for Case Manager :"  +casemanagerName;
//        query = "SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, current_status, date_current_status, date_last_refill, date_last_clinic, date_next_clinic AS date_visit FROM patient WHERE facility_id = " + facilityId + " AND ((current_status IN ('ART Start', 'ART Restart', 'ART Transfer In') AND DATEDIFF(DAY, date_next_refill, CURDATE()) >= 1) OR (current_status IN ('Lost to Follow Up', 'Stopped Treatment') AND DATEDIFF(DAY, agreed_date, CURDATE()) >= 1)) AND date_started IS NOT NULL AND casemanager_id = "+casemanagerId+" ORDER BY current_status"; 
//        generateReportList(query);
//        return reportList;
//    }
    public ArrayList<Map<String, Object>> lostFollowUp() {
        reportTitle = "List of Patients Lost to Followup";
        facilityId = (Long) session.getAttribute("facilityId");
        query = "SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, current_status, date_current_status, date_last_refill, date_last_clinic, date_next_clinic AS date_visit FROM patient WHERE facility_id = " + facilityId + " AND current_status = 'Lost to Follow Up'  ORDER BY current_status";
        generateReportList(query);
        return reportList;
    }

    public ArrayList<Map<String, Object>> coInfected() {
        reportTitle = "List of TB-HIV co-infected Patients";
        facilityId = (Long) session.getAttribute("facilityId");
        //started on TB treatment -> select all patients whose tb status at last visit is Currently on TB treatment
        executeUpdate("DROP TABLE IF EXISTS tb");
        query = "CREATE TEMPORARY TABLE tb AS SELECT DISTINCT patient_id, MAX(date_visit) FROM clinic WHERE facility_id = " + facilityId + " AND tb_status = 'Currently on TB treatment' GROUP BY patient_id";
        executeUpdate(query);

        query = "SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, state, lga, date_registration, status_registration, current_status, date_current_status, date_started FROM patient WHERE patient_id IN (SELECT patient_id FROM tb) ORDER BY current_status";
        generateReportList(query);
        return reportList;
    }

    public ArrayList<Map<String, Object>> patientsRegimen(String regimenType) {
        reportTitle = "Patients on First line Regimen";
        facilityId = (Long) session.getAttribute("facilityId");
        executeUpdate("DROP TABLE IF EXISTS original");
        query = "CREATE TEMPORARY TABLE original AS SELECT DISTINCT patient_id, regimen FROM clinic WHERE facility_id = " + facilityId + " AND commence = 1";
        executeUpdate(query);
        if (regimenType.equals("first")) {
            reportTitle = "Patients on First line Regimen";
            query = "SELECT DISTINCT patient.patient_id, patient.hospital_num, patient.surname, patient.other_names, patient.gender, patient.date_birth, DATEDIFF(YEAR, patient.date_birth, CURDATE()) AS age, patient.phone, patient.address, patient.date_started, patient.regimentype, patient.regimen, original.regimen AS original_regimen "
                    + " FROM patient LEFT OUTER JOIN original ON patient.patient_id = original.patient_id WHERE patient.facility_id = " + facilityId + " AND patient.current_status IN ('ART Start', 'ART Restart', 'ART Transfer In') AND patient.regimentype IN (SELECT description FROM regimentype WHERE regimentype_id = 1 OR regimentype_id = 3)";
        }
        if (regimenType.equals("second")) {
            reportTitle = "Patients on Second Therapy";
            query = "SELECT DISTINCT patient.patient_id, patient.hospital_num, patient.surname, patient.other_names, patient.gender, patient.date_birth, DATEDIFF(YEAR, patient.date_birth, CURDATE()) AS age, patient.phone, patient.address, patient.date_started, patient.regimentype, patient.regimen, original.regimen AS original_regimen "
                    + " FROM patient LEFT OUTER JOIN original ON patient.patient_id = original.patient_id WHERE patient.facility_id = " + facilityId + " AND patient.current_status IN ('ART Start', 'ART Restart', 'ART Transfer In') AND patient.regimentype IN (SELECT description FROM regimentype WHERE regimentype_id = 2 OR regimentype_id = 4)";
        }
        if (regimenType.equals("third")) {
            reportTitle = "Patients on Third Line Regimen";
            query = "SELECT DISTINCT patient.patient_id, patient.hospital_num, patient.surname, patient.other_names, patient.gender, patient.date_birth, DATEDIFF(YEAR, patient.date_birth, CURDATE()) AS age, patient.phone, patient.address, patient.date_started, patient.regimentype, patient.regimen, original.regimen AS original_regimen "
                    + " FROM patient LEFT OUTER JOIN original ON patient.patient_id = original.patient_id WHERE patient.facility_id = " + facilityId + " AND patient.current_status IN ('ART Start', 'ART Restart', 'ART Transfer In') AND patient.regimentype IN (SELECT description FROM regimentype WHERE regimentype_id = 14)";
        }
        generateReportList(query);
        return reportList;
    }

    public synchronized ArrayList<Map<String, Object>> regimenSummary() {
        reportTitle = "Patient Per Regimen Report";
        facilityId = (Long) session.getAttribute("facilityId");
        executeUpdate("DROP TABLE IF EXISTS ppr");
        query = "CREATE TEMPORARY TABLE ppr AS SELECT regimentype, regimen, COUNT(DISTINCT patient_id) AS number_of_patients FROM patient WHERE facility_id = " + facilityId + " AND regimentype IN (SELECT description FROM regimentype WHERE regimentype_id IN (1, 2, 3, 4, 14)) AND regimen != '' AND regimen IS NOT NULL AND current_status IN ('ART Start', 'ART Restart', 'ART Transfer In') GROUP BY regimentype, regimen HAVING regimen IS NOT NULL";
        executeUpdate(query);
        query = "SELECT * FROM ppr ORDER BY regimentype, regimen";
        generateReportList(query);
        return reportList;
    }

    public synchronized ArrayList<Map<String, Object>> devolvedSummary() {
        reportTitle = "Patient Devolvement Report";
        facilityId = (Long) session.getAttribute("facilityId");
        //executeUpdate("DROP TABLE IF EXISTS dev");  
        query = "SELECT pt.patient_id, pt.facility_id, cp.pharmacy, dv.date_devolved, pt.hospital_num, pt.surname, pt.other_names, pt.gender, pt.date_birth, DATEDIFF(YEAR, pt.date_birth, CURDATE()) AS age, pt.phone, pt.address, pt.state, pt.lga, pt.date_registration, pt.status_registration, pt.current_status, pt.date_current_status, pt.date_started, pt.date_last_cd4, pt.last_cd4, pt.last_cd4p, pt.date_last_viral_load, pt.last_viral_load, pt.date_last_refill, pt.last_refill_duration FROM patient AS pt JOIN devolve AS dv ON pt.patient_id = dv.patient_id JOIN communitypharm AS cp ON dv.communitypharm_id = cp.communitypharm_id WHERE pt.facility_id = " + facilityId + " ORDER BY cp.pharmacy";
        System.out.println("Query is: " + query);
        //executeUpdate(query);
        //query = "SELECT * FROM dev ORDER BY regimentype, regimen";
        generateReportList(query);
        return reportList;
    }

    public void generateReportList(String query) {
        reportList = new ArrayList<Map<String, Object>>();
        try {
            jdbcTemplate.query(query, resultSet -> {
                reportList = new PatientReportListBuilder().buildList(resultSet);
                return null;
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public HashMap getReportParameters() {
        parameterMap = new HashMap();
        facilityId = (Long) session.getAttribute("facilityId");

        Calendar today = new GregorianCalendar();
        if (request.getParameterMap().containsKey("reportingMonth")) {
            parameterMap.put("reportingMonth", request.getParameter("reportingMonth"));
        } else {
            parameterMap.put("reportingMonth", DateUtil.getMonth(today.get(Calendar.MONTH) + 1));
        }
        if (request.getParameterMap().containsKey("reportingYear")) {
            parameterMap.put("reportingYear", request.getParameter("reportingYear"));
        } else {
            parameterMap.put("reportingYear", Integer.toString(today.get(Calendar.YEAR)));
        }
        if (request.getParameterMap().containsKey("reportingMonthBegin")) {
            parameterMap.put("reportingMonthBegin", request.getParameter("reportingMonthBegin"));
        }
        if (request.getParameterMap().containsKey("reportingMonthEnd")) {
            parameterMap.put("reportingMonthEnd", request.getParameter("reportingMonthEnd"));
        }
        if (request.getParameterMap().containsKey("reportingYearBegin")) {
            parameterMap.put("reportingYearBegin", request.getParameter("reportingYearBegin"));
        }
        if (request.getParameterMap().containsKey("reportingYearEnd")) {
            parameterMap.put("reportingYearEnd", request.getParameter("reportingYearEnd"));
        }
        if (request.getParameterMap().containsKey("reportingDateBegin")) {
            parameterMap.put("reportingDateBegin", request.getParameter("reportingDateBegin"));
        }
        if (request.getParameterMap().containsKey("reportingDateEnd")) {
            parameterMap.put("reportingDateEnd", request.getParameter("reportingDateEnd"));
        }
        if (request.getParameterMap().containsKey("reportingDate")) {
            parameterMap.put("reportingDate", request.getParameter("reportingDate"));
        }
        parameterMap.put("reportTitle", reportTitle);

        try {
            // fetch the required records from the database
            query = "SELECT DISTINCT facility.name, facility.address1, facility.address2, facility.phone1, facility.phone2, facility.email, lga.name AS lga, state.name AS state FROM facility JOIN lga ON facility.lga_id = lga.lga_id JOIN state ON facility.state_id = state.state_id WHERE facility_id = " + facilityId;
            jdbcTemplate.query(query, resultSet -> {
                if (resultSet.next()) {
                    parameterMap.put("facilityName", resultSet.getString("name"));
                    parameterMap.put("lga", resultSet.getString("lga"));
                    parameterMap.put("state", resultSet.getString("state"));
                }
                return null;
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return parameterMap;
    }

    private void executeUpdate(String query) {
        try {
            jdbcTemplate.execute(query);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}

//        query = "SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, current_status, date_current_status, date_last_refill, date_last_clinic, date_next_clinic AS date_visit FROM patient WHERE facility_id = " + facilityId + " AND current_status NOT IN ('ART Transfer Out', 'Pre-ART Transfer Out', 'Lost to Follow Up', 'Stopped Treatment', 'Known Death') AND ((date_next_clinic IS NOT NULL AND DATEDIFF(DAY, date_next_clinic, CURDATE()) > 1) OR (date_next_refill IS NOT NULL AND DATEDIFF(DAY, date_next_refill, CURDATE()) > 1)) ORDER BY current_status"; 
//        query = "SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, current_status, date_current_status, date_last_refill, date_last_clinic, date_next_clinic AS date_visit FROM patient WHERE facility_id = " + facilityId + " AND current_status IN ('HIV+ non ART', 'ART Start', 'ART Restart', 'ART Transfer In') AND ((date_next_clinic IS NOT NULL AND DATEDIFF(DAY, date_next_clinic, CURDATE()) >= 90) OR (date_next_refill IS NOT NULL AND DATEDIFF(DAY, date_next_refill, CURDATE()) >= 90)) ORDER BY current_status"; 
