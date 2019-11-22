/**
 *
 * @author AALOZIE
 */
package org.fhi360.lamis.report;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.report.indicator.ServiceSummaryIndicators;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.springframework.jdbc.core.JdbcTemplate;

public class ServiceSummaryProcessor {

    private int reportingMonthBegin;
    private int reportingYearBegin;
    private int reportingMonthEnd;
    private int reportingYearEnd;
    private String reportingDateBegin;
    private String reportingDateEnd;
    private int[][] value = new int[12][6];
    private String[] indicator = new String[12];

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    private ArrayList<Map<String, Object>> reportList;
    private HashMap parameterMap;
    private long facilityId;

    public ServiceSummaryProcessor() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    public synchronized ArrayList<Map<String, Object>> process() {
        reportList = new ArrayList<Map<String, Object>>();
        reportingMonthBegin = DateUtil.getMonth(request.getParameter("reportingMonthBegin"));
        reportingYearBegin = Integer.parseInt(request.getParameter("reportingYearBegin"));
        reportingMonthEnd = DateUtil.getMonth(request.getParameter("reportingMonthEnd"));
        reportingYearEnd = Integer.parseInt(request.getParameter("reportingYearEnd"));
        reportingDateBegin = DateUtil.parseDateToString(DateUtil.getFirstDateOfMonth(reportingYearBegin, reportingMonthBegin), "yyyy-MM-dd");
        reportingDateEnd = DateUtil.parseDateToString(DateUtil.getLastDateOfMonth(reportingYearEnd, reportingMonthEnd), "yyyy-MM-dd");
        facilityId = (Long) session.getAttribute("facilityId");
        indicator = new ServiceSummaryIndicators().initialize();

        try {
            Date dateBegin = (Date) DateUtil.parseStringToDate(reportingDateBegin, "yyyy-MM-dd");
            Date dateEnd = (Date) DateUtil.parseStringToDate(reportingDateEnd, "yyyy-MM-dd");

            //patients on care and treatment 
            query = "SELECT DISTINCT patient_id, gender, DATEDIFF(YEAR, date_birth, '" + reportingDateBegin + "') AS age FROM patient WHERE facility_id = " + facilityId;
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");
                    long patientId = resultSet.getLong("patient_id");
                    query = "SELECT DISTINCT patient_id FROM clinic WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_visit >= '" + reportingDateBegin + "' AND date_visit <= '" + reportingDateEnd + "'";
                    if (found(query)) {
                        disaggregate(gender, age, 0);
                        disaggregate(gender, age, 1);
                    } else {
                        query = "SELECT DISTINCT patient_id FROM pharmacy WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_visit >= '" + reportingDateBegin + "' AND date_visit <= '" + reportingDateEnd + "'";
                        if (found(query)) {
                            disaggregate(gender, age, 0);
                            disaggregate(gender, age, 1);
                        } else {
                            query = "SELECT DISTINCT patient_id FROM laboratory WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_reported >= '" + reportingDateBegin + "' AND date_reported <= '" + reportingDateEnd + "'";
                            if (found(query)) {
                                disaggregate(gender, age, 0);
                                disaggregate(gender, age, 1);
                            }
                        }
                    }
                }
                return null;
            });

            //receiving contrimoxazole - select the last refill visit before last day of reporting period for all patients on care and treatment
            executeUpdate("DROP TABLE IF EXISTS refill");
            query = "CREATE TEMPORARY TABLE refill AS SELECT DISTINCT patient_id, MAX(date_visit) AS date_visit FROM pharmacy WHERE facility_id = " + facilityId + " AND date_visit <= '" + reportingDateBegin + "' GROUP BY patient_id";
            executeUpdate(query);

            query = "SELECT DISTINCT patient.patient_id, patient.gender, DATEDIFF(YEAR, patient.date_birth, '" + reportingDateBegin + "') AS age "
                    + " FROM patient JOIN pharmacy ON patient.patient_id = pharmacy.patient_id JOIN refill ON patient.patient_id = refill.patient_id WHERE patient.facility_id = " + facilityId + " AND pharmacy.facility_id = " + facilityId + " AND pharmacy.patient_id = refill.patient_id AND pharmacy.date_visit = refill.date_visit AND pharmacy.regimentype_id = 8";
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");
                    disaggregate(gender, age, 2);
                }
                return null;
            });

            //screened for TB in HIV care and treatment 
            query = "SELECT DISTINCT patient.patient_id, patient.gender, DATEDIFF(YEAR, patient.date_birth, '" + reportingDateBegin + "') AS age "
                    + " FROM patient JOIN clinic ON patient.patient_id = clinic.patient_id WHERE patient.facility_id = " + facilityId + " AND clinic.facility_id = " + facilityId + " AND clinic.date_visit >= '" + reportingDateBegin + "' AND clinic.date_visit <= '" + reportingDateEnd + "' AND clinic.tb_status != '' AND clinic.tb_status IS NOT NULL";
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");
                    disaggregate(gender, age, 3);
                }
                return null;
            });

            //started on TB treatment -> select all patients whose tb status at any visit before the begining of the reporting period is Currently on TB treatment
            executeUpdate("DROP TABLE IF EXISTS tb");
            query = "CREATE TEMPORARY TABLE tb AS SELECT DISTINCT patient_id FROM clinic WHERE facility_id = " + facilityId + " AND clinic.date_visit < '" + reportingDateBegin + "' AND clinic.tb_status = 'Currently on TB treatment'";
            executeUpdate(query);

            //select all patients whose tb status at any visit within the reporting period is Currently on TB treatment who are not in the tb table created above
            query = "SELECT DISTINCT patient.patient_id, patient.gender, DATEDIFF(YEAR, patient.date_birth, '" + reportingDateBegin + "') AS age "
                    + " FROM patient JOIN clinic ON patient.patient_id = clinic.patient_id WHERE patient.patient_id NOT IN (SELECT patient_id FROM tb) AND patient.facility_id = " + facilityId + " AND clinic.facility_id = " + facilityId + " AND clinic.date_visit >= '" + reportingDateBegin + "' AND clinic.date_visit <= '" + reportingDateEnd + "' AND clinic.tb_status = 'Currently on TB treatment'";
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");
                    disaggregate(gender, age, 4);
                }
                return null;
            });

            query = "SELECT DISTINCT patient_id, gender, DATEDIFF(YEAR, date_birth, '" + reportingDateBegin + "') AS age, date_registration, status_registration, date_started FROM patient WHERE facility_id = " + facilityId;
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    long patientId = resultSet.getLong("patient_id");
                    Date dateRegistration = resultSet.getDate("date_registration");
                    String statusRegistration = resultSet.getString("status_registration");
                    Date dateStarted = resultSet.getDate("date_started");
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");

                    Map map = getCurrentStatus(patientId, statusRegistration);
                    String currentStatus = map.isEmpty() ? "" : (String) map.get("currentStatus");
                    Date dateStatus = map.isEmpty() ? null : (Date) map.get("dateStatus");

                    if (dateStarted != null) {
                        if (DateUtil.isBetween(dateStarted, dateBegin, dateEnd)) {
                            disaggregate(gender, age, 5); //started on ART this reporting period 
                        }
                    }

                    if (dateStarted != null) {
                        if (DateUtil.isBetween(dateStarted, dateBegin, dateEnd)) {
                            disaggregate(gender, age, 5); //started on ART this reporting period 
                        }
                    }

                    if ((currentStatus.trim().equals("ART Start") || currentStatus.trim().equals("ART Restart") || currentStatus.trim().equals("ART Transfer In")) && dateStarted != null && dateStatus != null) {
                        if (DateUtil.isBetween(dateStatus, dateBegin, dateEnd)) {
                            disaggregate(gender, age, 6); //currently on ART this reporting period 
                        }
                    }

                    if (currentStatus.trim().equals("Lost to Follow Up") && dateStarted != null && dateStatus != null) {
                        if (DateUtil.isBetween(dateStatus, dateBegin, dateEnd)) {
                            disaggregate(gender, age, 7); //lost to follow up ART this reporting period 
                        }
                    }

                    if (currentStatus.trim().equals("Known Death") && dateStarted != null && dateStatus != null) {
                        if (DateUtil.isBetween(dateStatus, dateBegin, dateEnd)) {
                            disaggregate(gender, age, 8); //known death ART this reporting period
                        }
                    }
                }
                return null;
            });
            //defaulters for over 7 days of scheduled clinic appointment
            //create a temporary table of clinic appointment within the reporting period
            executeUpdate("DROP TABLE IF EXISTS appointment");
            query = "CREATE TEMPORARY TABLE appointment AS SELECT DISTINCT patient.patient_id, patient.gender, DATEDIFF(YEAR, patient.date_birth, '" + reportingDateBegin + "') AS age, clinic.next_appointment "
                    + " FROM patient JOIN clinic ON patient.patient_id = clinic.patient_id WHERE patient.facility_id = " + facilityId + " AND clinic.facility_id = " + facilityId + " AND clinic.next_appointment >= '" + reportingDateBegin + "' AND clinic.next_appointment <= '" + reportingDateEnd + "'";
            executeUpdate(query);
            executeUpdate("DROP TABLE IF EXISTS visit");  //patients who came within 7 days of appointment      
            query = "CREATE TEMPORARY TABLE visit AS SELECT DISTINCT clinic.patient_id FROM clinic JOIN appointment ON clinic.patient_id = appointment.patient_id WHERE clinic.facility_id = " + facilityId + " AND clinic.date_visit >= appointment.next_appointment-7 AND clinic.date_visit <= appointment.next_appointment+7";
            executeUpdate(query);

            query = "SELECT * FROM appointment WHERE patient_id NOT IN (SELECT patient_id FROM visit)"; //patients who defaulted
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");
                    disaggregate(gender, age, 9);
                }
                return null;
            });

            //documented ADR
            query = "SELECT DISTINCT patient.patient_id, patient.gender, DATEDIFF(YEAR, patient.date_birth, '" + reportingDateBegin + "') AS age, patient.date_registration, patient.status_registration "
                    + " FROM patient JOIN adrhistory ON patient.patient_id = adrhistory.patient_id WHERE patient.facility_id = " + facilityId + " AND adrhistory.facility_id = " + facilityId + " AND adrhistory.date_visit >= '" + reportingDateBegin + "' AND adrhistory.date_visit <= '" + reportingDateEnd + "'";
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    Date dateRegistration = resultSet.getDate("date_registration");
                    String statusRegistration = resultSet.getString("status_registration");
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");

                    if (!statusRegistration.trim().equals("HIV+ non ART") && dateRegistration != null) {
                        disaggregate(gender, age, 10);
                    }
                }
                return null;
            });

            query = "SELECT DISTINCT patient.patient_id, patient.gender, DATEDIFF(YEAR, patient.date_birth, '" + reportingDateBegin + "') AS age, patient.date_registration, patient.status_registration "
                    + " FROM patient JOIN adrhistory ON patient.patient_id = adrhistory.patient_id WHERE patient.facility_id = " + facilityId + " AND adrhistory.facility_id = " + facilityId + " AND adrhistory.date_visit >= '" + reportingDateBegin + "' AND adrhistory.date_visit <= '" + reportingDateEnd + "' AND adrhistory.severity > 2";
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    Date dateRegistration = resultSet.getDate("date_registration");
                    String statusRegistration = resultSet.getString("status_registration");
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");

                    if (!statusRegistration.trim().equals("HIV+ non ART") && dateRegistration != null) {
                        disaggregate(gender, age, 11);
                    }
                }
                return null;
            });

            //Populating indicator values
            for (int i = 0; i < indicator.length; i++) {
                String male1 = Integer.toString(value[i][0]);
                String male2 = Integer.toString(value[i][1]);
                String male3 = Integer.toString(value[i][2]);
                String fmale1 = Integer.toString(value[i][3]);
                String fmale2 = Integer.toString(value[i][4]);
                String fmale3 = Integer.toString(value[i][5]);

                int total = value[i][0] + value[i][1] + value[i][2] + value[i][3] + value[i][4] + value[i][5];

                // create map of values 
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("sno", Integer.toString(i + 1));
                map.put("indicator", indicator[i]);
                map.put("male1", male1);
                map.put("male2", male2);
                map.put("male3", male3);
                map.put("fmale1", fmale1);
                map.put("fmale2", fmale2);
                map.put("fmale3", fmale3);
                map.put("total", Integer.toString(total));
                reportList.add(map);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return reportList;
    }

    public HashMap getReportParameters() {
        parameterMap = new HashMap();
        String reportingPeriodBegin = request.getParameter("reportingMonthBegin") + " " + request.getParameter("reportingYearBegin");
        String reportingPeriodEnd = request.getParameter("reportingMonthEnd") + " " + request.getParameter("reportingYearEnd");
        parameterMap.put("reportingPeriodBegin", reportingPeriodBegin);
        parameterMap.put("reportingPeriodEnd", reportingPeriodEnd);
        facilityId = (Long) session.getAttribute("facilityId");

        try {

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

    private void disaggregate(String gender, int age, int i) {
        if (gender.trim().equals("Male")) {
            if (age < 1) {
                value[i][0]++;  //males < 1yr
            } else {
                if (age >= 1 && age < 14) {
                    value[i][1]++;  //males 1-14yrs
                } else {
                    value[i][2]++;  //males => 15yrs
                }
            }
        } else {
            if (age < 1) {
                value[i][3]++;  //fmales < 1yr
            } else {
                if (age >= 1 && age < 14) {
                    value[i][4]++;  //fmales 1-14yrs
                } else {
                    value[i][5]++;  //fmales => 15yrs
                }
            }
        }
    }

    private void executeUpdate(String query) {
        try {
            jdbcTemplate.execute(query);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private boolean found(String query) {
        boolean[] found = {false};
        try {
            jdbcTemplate.query(query, resultSet -> {
                if (resultSet.next()) {
                    found[0] = true;
                }
                return null;
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return found[0];
    }

    private Map getCurrentStatus(long patientId, String statusRegistration) {
        Map<String, Object> map = new HashMap<String, Object>();
        String[] currentStatus = {""};
        Date[] dateStatus = {null};
        try {
            query = "SELECT current_status, date_current_status FROM statushistory WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_current_status = (SELECT MAX(date_current_status) FROM statushistory WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_current_status <= '" + reportingDateEnd + "')";
            jdbcTemplate.query(query, rs -> {
                while (rs.next()) {
                    currentStatus[0] = rs.getString("current_status");
                    dateStatus[0] = rs.getDate("date_current_status");
                    if (!currentStatus[0].trim().equals(statusRegistration)) {
                        break;
                    }
                }
                return null;
            });
            map.put("currentStatus", currentStatus[0]);
            map.put("dateStatus", dateStatus[0]);
        } catch (Exception exception) {
            exception.printStackTrace();

        }
        return map;
    }

}


//query = "CREATE TEMPORARY TABLE refill AS SELECT patient_id, MAX(date_visit) AS date_refill FROM (SELECT DISTINCT patient_id, date_visit FROM pharmacy WHERE facility_id = " + facilityId + " AND date_visit <= '" + reportingDateEnd + "') AS t1 GROUP BY patient_id";
