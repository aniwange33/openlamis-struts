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
import org.fhi360.lamis.utility.DateUtil;
import org.springframework.jdbc.core.JdbcTemplate;

public class CohortAnalysisProcessor {

    private int cohortMonthBegin;
    private int cohortYearBegin;
    private int cohortMonthEnd;
    private int cohortYearEnd;
    private String cohortDateBegin;
    private String cohortDateEnd;
    private int[][] cohort = new int[5][6];

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    private ArrayList<Map<String, Object>> patientList;
    private ArrayList<Map<String, Object>> reportList;
    private HashMap parameterMap;
    private long facilityId;

//    Variuables
    int totalCohort = 0;
    long patientId;
    String endDate;
    Date dateStarted;
    int month[] = {6, 12, 18, 24, 30, 36};

    public CohortAnalysisProcessor() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    public synchronized ArrayList<Map<String, Object>> process() {
        patientList = new ArrayList<Map<String, Object>>();
        reportList = new ArrayList<Map<String, Object>>();
        cohortMonthBegin = DateUtil.getMonth(request.getParameter("reportingMonthBegin"));
        cohortYearBegin = Integer.parseInt(request.getParameter("reportingYearBegin"));
        cohortMonthEnd = DateUtil.getMonth(request.getParameter("reportingMonthEnd"));
        cohortYearEnd = Integer.parseInt(request.getParameter("reportingYearEnd"));
        processor();
        return getReportList();
    }

    public synchronized int[][] process(String reportingMonthBegin, String reportingYearBegin, String reportingMonthEnd, String reportingYearEnd) {
        patientList = new ArrayList<Map<String, Object>>();
        reportList = new ArrayList<Map<String, Object>>();
        cohortMonthBegin = DateUtil.getMonth(request.getParameter("reportingMonthBegin"));
        cohortYearBegin = Integer.parseInt(request.getParameter("reportingYearBegin"));
        cohortMonthEnd = DateUtil.getMonth(request.getParameter("reportingMonthEnd"));
        cohortYearEnd = Integer.parseInt(request.getParameter("reportingYearEnd"));
        processor();
        return cohort;
    }

    private void processor() {
        cohortDateBegin = DateUtil.parseDateToString(DateUtil.getFirstDateOfMonth(cohortYearBegin, cohortMonthBegin), "yyyy-MM-dd");
        cohortDateEnd = DateUtil.parseDateToString(DateUtil.getLastDateOfMonth(cohortYearEnd, cohortMonthEnd), "yyyy-MM-dd");
        facilityId = (Long) session.getAttribute("facilityId");

        try {

            executeUpdate("DROP TABLE IF EXISTS dropped");
            executeUpdate("CREATE TEMPORARY TABLE dropped (patient_id bigint)");
            //patients on care and treatment 
            query = "SELECT patient_id, date_started FROM patient WHERE facility_id = " + facilityId + " AND date_started >= '" + cohortDateBegin + "' AND date_started <= '" + cohortDateEnd + "'"; // AND (current_status != 'ART Transfer Out' AND date_current_status <= '" + cohortDateBegin + "')";
            jdbcTemplate.query(query, resultSet -> {

                while (resultSet.next()) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("patientId", resultSet.getLong("patient_id"));
                    map.put("dateStarted", resultSet.getDate("date_started"));
                    patientList.add(map);
                    totalCohort++;
                }
                return null;
            });
            session.setAttribute("totalCohort", Integer.toString(totalCohort));
            int alive = totalCohort;

            for (int ii = 0; ii <= 5; ii++) {
                final int i = ii;
                patientList.stream().map((map) -> {
                    patientId = (Long) map.get("patientId");
                    return map;
                }).map((map) -> {
                    dateStarted = (Date) map.get("dateStarted");
                    return map;
                }).map((_item) -> {
                    query = "SELECT patient_id FROM dropped WHERE patient_id = " + patientId;
                    return _item;
                }).forEachOrdered((_item) -> {
                    jdbcTemplate.query(query, resultSet1 -> {
                        if (!resultSet1.next()) {
                            endDate = DateUtil.parseDateToString(DateUtil.addMonth(dateStarted, month[i]), "yyyy-MM-dd");
                            query = "SELECT current_status FROM statushistory WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_current_status = SELECT MAX(date_current_status) FROM statushistory WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_current_status <= '" + endDate + "'";
                            jdbcTemplate.query(query, resultSet2 -> {
                                if (resultSet2.next()) {
                                    String currentStatus = resultSet2.getString("current_status");
                                    accummulator(patientId, currentStatus, i);
                                }
                                return null;
                            });
                        }
                        return null;
                    });
                });
                alive = alive - (cohort[0][i] + cohort[1][i] + cohort[2][i] + cohort[3][i]);  //alive and on ART at the end of cohort month
                cohort[4][i] = alive;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void accummulator(long patientId, String currentStatus, int i) {
        boolean drop = false;
        if (currentStatus.trim().equals("ART Transfer Out")) {
            cohort[0][i]++;
            drop = true;
        }
        if (currentStatus.trim().equals("Stopped Treatment")) {
            cohort[1][i]++;
            drop = true;
        }
        if (currentStatus.trim().equals("Lost to Follow Up")) {
            cohort[2][i]++;
            drop = true;
        }
        if (currentStatus.trim().equals("Known Death")) {
            cohort[3][i]++;
            drop = true;
        }
        query = "INSERT INTO dropped(patient_id) VALUES(" + patientId + ")";
        if (drop) {
            executeUpdate(query);
        }
    }

    private ArrayList<Map<String, Object>> getReportList() {
        String status[] = {"ART Tranfer Out", "Stopped Treatment", "Lost to Follow Up", "Known Death", "Alive and on ART"};
        for (int i = 0; i <= 4; i++) {
            // create map of values 
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("status", status[i]);
            map.put("mon6", Integer.toString(cohort[i][0]));
            map.put("mon12", Integer.toString(cohort[i][1]));
            map.put("mon18", Integer.toString(cohort[i][2]));
            map.put("mon24", Integer.toString(cohort[i][3]));
            map.put("mon30", Integer.toString(cohort[i][4]));
            map.put("mon36", Integer.toString(cohort[i][5]));
            reportList.add(map);
        }
        return reportList;
    }

    public HashMap getReportParameters() {
        parameterMap = new HashMap();
        String reportingPeriodBegin = request.getParameter("reportingMonthBegin").substring(0, 3) + " " + request.getParameter("reportingYearBegin");
        String reportingPeriodEnd = request.getParameter("reportingMonthEnd").substring(0, 3) + " " + request.getParameter("reportingYearEnd");
        parameterMap.put("reportingPeriodBegin", reportingPeriodBegin);
        parameterMap.put("reportingPeriodEnd", reportingPeriodEnd);
        parameterMap.put("totalCohort", (String) session.getAttribute("totalCohort"));
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

    private void executeUpdate(String query) {
        try {
            jdbcTemplate.execute(query);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
