/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

public class ClinicReports {

    private String reportTitle;

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    private ArrayList<Map<String, Object>> reportList;
    private HashMap parameterMap;
    private long facilityId;

    public ClinicReports() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    public ArrayList<Map<String, Object>> eligibleForART() {
        reportTitle = "Patients Eligible for ARV Initiation";
        facilityId = (Long) session.getAttribute("facilityId");
        query = "SELECT * FROM (SELECT DISTINCT patient.patient_id, patient.hospital_num, patient.surname, patient.other_names, patient.gender, patient.date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, patient.phone, patient.address, patient.current_status, patient.last_cd4, patient.last_cd4p, patient.last_viral_load, patient.last_clinic_stage "
                + " FROM patient WHERE patient.facility_id = " + facilityId + " AND patient.current_status = 'HIV+ non ART') AS ps "
                + " WHERE (ps.age >= 5 AND (ps.last_clinic_stage IN ('Stage III', 'Stage IV') OR (ps.last_cd4 > 0.0 AND ps.last_cd4 < 350))) OR (ps.age >= 2 AND ps.age < 5 AND (ps.last_clinic_stage IN ('Stage III', 'Stage IV') OR (ps.last_cd4p > 0.0 AND ps.last_cd4p < 25) OR (ps.last_cd4 > 0.0 AND ps.last_cd4 < 750))) OR ps.age < 2";
        generateReportList(query);
        return reportList;
    }

    public ArrayList<Map<String, Object>> cd4Due() {
        reportTitle = "Patients due for CD4 count Test";
        facilityId = (Long) session.getAttribute("facilityId");
        query = "SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, current_status, date_last_cd4, last_cd4, last_cd4p FROM patient WHERE facility_id = " + facilityId + " AND current_status IN ('HIV+ non ART', 'ART Start', 'ART Restart', 'ART Transfer In', 'Pre-ART Transfer In') AND DATEDIFF(MONTH, date_last_cd4, CURDATE()) > 3";
        generateReportList(query);
        return reportList;
    }

    public ArrayList<Map<String, Object>> clientsCd4Due() {

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

        reportTitle = casemanagerName[0] + "'s Clients due for CD4 count Test";

        System.out.println("The Report Title is: " + reportTitle);
        query = "SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, current_status, date_last_cd4, last_cd4, last_cd4p FROM patient WHERE facility_id = " + facilityId + " AND casemanager_id = " + casemanagerId + " AND DATEDIFF(MONTH, date_last_cd4, CURDATE()) > 3";
        generateReportList(query);
        return reportList;
    }

    public ArrayList<Map<String, Object>> unassignedClients() {

        reportTitle = "List of clients not assigned to a case manager";
        facilityId = (Long) session.getAttribute("facilityId");
        query = "SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, state, lga, date_registration, status_registration, current_status, date_current_status, date_started, date_last_cd4, last_cd4, last_cd4p, date_last_viral_load, last_viral_load, date_last_refill, last_refill_duration FROM patient WHERE facility_id = " + facilityId + " AND (casemanager_id is NULL OR casemanager_id = 0) AND current_status NOT IN ('Known Death', 'ART Transfer Out', 'Pre-ART Transfer Out')";
        query += " ORDER BY current_status";

        generateReportList(query);
        return reportList;
    }

    public ArrayList<Map<String, Object>> clientsViralLoadDue() {

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

        reportTitle = casemanagerName[0] + "'s Clients due for Viral Load Test";
        query = "SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, date_started, current_status, date_last_cd4, last_cd4, last_cd4p, date_last_viral_load, last_viral_load FROM patient WHERE facility_id = " + facilityId + " AND casemanager_id = " + casemanagerId + " AND ((DATEDIFF(MONTH, date_last_viral_load, CURDATE()) >= 6 AND viral_load_type != '" + Constants.TypeVL.VL_ROUTINE + "') OR (date_last_viral_load IS NULL AND DATEDIFF(MONTH, date_started, CURDATE()) >= 6))";
        generateReportList(query);
        return reportList;
    }

    public ArrayList<Map<String, Object>> cd4LessBaseline() {
        reportTitle = "Patients with current CD4 Count <= baseline value";
        facilityId = (Long) session.getAttribute("facilityId");
        query = "SELECT DISTINCT patient.patient_id, patient.hospital_num, patient.surname, patient.other_names, patient.gender, patient.date_birth, DATEDIFF(YEAR, patient.date_birth, CURDATE()) AS age, patient.current_status, patient.phone, patient.address, patient.date_started, patient.last_cd4, patient.last_cd4p, patient.date_last_cd4, clinic.cd4, clinic.cd4p FROM patient "
                + " JOIN clinic ON patient.facility_id = clinic.facility_id AND patient.patient_id = clinic.patient_id WHERE patient.facility_id = " + facilityId + " AND clinic.facility_id = " + facilityId + " AND patient.current_status IN ('ART Start', 'ART Restart', 'ART Transfer In') AND (patient.last_cd4 != 0 AND patient.last_cd4p != 0) AND (patient.last_cd4 <= clinic.cd4 OR patient.last_cd4p <= clinic.cd4p) AND clinic.commence = 1";
        generateReportList(query);
        return reportList;
    }

    public ArrayList<Map<String, Object>> viralLoadDue() {
        reportTitle = "Patients due for Viral Load Test";
        facilityId = (Long) session.getAttribute("facilityId");
        query = "SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, date_started, current_status, date_last_cd4, last_cd4, last_cd4p, date_last_viral_load, last_viral_load FROM patient WHERE facility_id = " + facilityId + " AND current_status IN ('ART Start', 'ART Restart', 'ART Transfer In') AND (DATEDIFF(MONTH, date_last_viral_load, CURDATE()) >= 6) OR (date_last_viral_load IS NULL AND DATEDIFF(MONTH, date_started, CURDATE()) >= 6)";
        generateReportList(query);
        return reportList;
    }

    //Generate the List of Patients dur for Baseline Viral Load Test...
    public ArrayList<Map<String, Object>> baselineViralLoadDue() {
        reportTitle = "Patients due for Baseline Viral Load Test";
        facilityId = (Long) session.getAttribute("facilityId");
        query = "SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, date_started, current_status, date_last_cd4, last_cd4, last_cd4p, date_last_viral_load, last_viral_load FROM patient WHERE facility_id = " + facilityId + " AND viral_load_type = '" + Constants.TypeVL.VL_BASELINE + "' AND viral_load_due_date <= CURDATE()";
        generateReportList(query);
        return reportList;
    }

    public ArrayList<Map<String, Object>> secondViralLoadDue() {
        reportTitle = "Patients due for Second Viral Load Test";
        facilityId = (Long) session.getAttribute("facilityId");
        query = "SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, date_started, current_status, date_last_cd4, last_cd4, last_cd4p, date_last_viral_load, last_viral_load FROM patient WHERE facility_id = " + facilityId + " AND viral_load_type = '" + Constants.TypeVL.VL_SECOND + "' AND viral_load_due_date <= CURDATE()";
        generateReportList(query);
        return reportList;
    }

    public ArrayList<Map<String, Object>> routineViralLoadDue() {
        reportTitle = "Patients due for Routine Viral Load Test";
        facilityId = (Long) session.getAttribute("facilityId");
        query = "SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, date_started, current_status, date_last_cd4, last_cd4, last_cd4p, date_last_viral_load, last_viral_load FROM patient WHERE facility_id = " + facilityId + " AND viral_load_type = '" + Constants.TypeVL.VL_ROUTINE + "' AND viral_load_due_date <= CURDATE()";
        generateReportList(query);
        return reportList;
    }

    public ArrayList<Map<String, Object>> repeatViralLoadDue() {
        reportTitle = "Patients due for Repeat Viral Load Test";
        facilityId = (Long) session.getAttribute("facilityId");
        query = "SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, date_started, current_status, date_last_cd4, last_cd4, last_cd4p, date_last_viral_load, last_viral_load FROM patient WHERE facility_id = " + facilityId + " AND viral_load_type = '" + Constants.TypeVL.VL_REPEAT + "' AND viral_load_due_date <= CURDATE()";
        generateReportList(query);
        return reportList;
    }

    public ArrayList<Map<String, Object>> viralLoadSupressed() {
        reportTitle = "Patients with current Viral Load less than 1000 copies/ml";
        facilityId = (Long) session.getAttribute("facilityId");
        query = "SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, date_started, current_status, date_last_cd4, last_cd4, last_cd4p, date_last_viral_load, last_viral_load FROM patient WHERE facility_id = " + facilityId + " AND current_status IN ('ART Start', 'ART Restart', 'ART Transfer In') AND last_viral_load < 1000";
        generateReportList(query);
        return reportList;
    }

    public ArrayList<Map<String, Object>> viralLoadUnsupressed() {
        reportTitle = "Patients with current Viral Load more than or equal 1000 copies/ml";
        facilityId = (Long) session.getAttribute("facilityId");
        query = "SELECT DISTINCT patient_id, hospital_num, surname, other_names, gender, date_birth, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, phone, address, date_started, current_status, date_last_cd4, last_cd4, last_cd4p, date_last_viral_load, last_viral_load FROM patient WHERE facility_id = " + facilityId + " AND current_status IN ('ART Start', 'ART Restart', 'ART Transfer In') AND last_viral_load >= 1000";
        generateReportList(query);
        return reportList;
    }

    public void generateReportList(String query) {
        reportList = new ArrayList<Map<String, Object>>();
        try {
            // fetch the required records from the database
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
