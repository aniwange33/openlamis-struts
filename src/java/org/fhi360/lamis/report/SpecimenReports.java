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
import org.fhi360.lamis.utility.builder.SpecimenReportListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;

public class SpecimenReports {

    private int reportingMonth;
    private int reportingYear;
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

    public SpecimenReports() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
        this.reportList = new ArrayList<Map<String, Object>>();
        this.reportingMonth = DateUtil.getMonth(request.getParameter("reportingMonth"));
        this.reportingYear = Integer.parseInt(request.getParameter("reportingYear"));
        this.reportingDateBegin = DateUtil.parseDateToString(DateUtil.getFirstDateOfMonth(reportingYear, reportingMonth), "yyyy-MM-dd");
        this.reportingDateEnd = DateUtil.parseDateToString(DateUtil.getLastDateOfMonth(reportingYear, reportingMonth), "yyyy-MM-dd");
    }

    public ArrayList<Map<String, Object>> eidRegister() {
        reportTitle = "PCR LABORATORY REGISTER";
        facilityId = (Long) session.getAttribute("facilityId");
        query = "SELECT specimen_type, labno, date_received, date_collected, DATEDIFF(DAY, date_collected, date_received) AS transit_time, date_assay, date_reported, date_dispatched, DATEDIFF(DAY, date_received, date_dispatched) AS turnaround_time, "
                + " hospital_num, result, final_result, surname, other_names, gender, date_birth, age, age_unit FROM specimen WHERE facility_id = " + facilityId + " AND MONTH(date_received) = " + reportingMonth + " AND YEAR(date_received) = " + reportingYear + " ORDER BY date_received";
        generateReportList(query);
        return reportList;
    }

    public ArrayList<Map<String, Object>> specimenSummary() {
        reportList = new ArrayList<Map<String, Object>>();
        reportTitle = "PCR LABORATORY SUMMARY";
        facilityId = (Long) session.getAttribute("facilityId");
        try {
            // fetch the required records from the database
            query = "SELECT specimen.treatment_unit_id, facility.name FROM specimen JOIN facility ON specimen.treatment_unit_id = facility.facility_id WHERE facility_id = " + facilityId + " AND MONTH(date_collected) = " + reportingMonth + " AND YEAR(date_collected) = " + reportingYear;
            jdbcTemplate.query(query, resultSet -> {
                if (resultSet.next()) {
                    long treatmentUnitId = resultSet.getLong("treatment_unit_id");
                    String treatmentUnit = resultSet.getString("name");
                    query = "SELECT COUNT(*) AS count FROM specimen WHERE treatment_unit_id = " + treatmentUnitId + " AND MONTH(date_collected) = " + reportingMonth + " AND YEAR(date_collected) = " + reportingYear;
                    int sampleCollected = getCount(query);

                    query = "SELECT COUNT(*) AS count FROM specimen WHERE treatment_unit_id = " + treatmentUnitId + " AND MONTH(date_dispatched) = " + reportingMonth + " AND YEAR(date_dispatched) = " + reportingYear;
                    int resultSent = getCount(query);

                    // create an array from object properties 
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("sampleCollected", sampleCollected);
                    map.put("resultSent", resultSent);
                    map.put("treatmentUnit", treatmentUnit);
                    reportList.add(map);
                }
                return null;
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return reportList;
    }

    public void generateReportList(String query) {
        reportList = new ArrayList<Map<String, Object>>();
        try {
            // fetch the required records from the database
            jdbcTemplate.query(query, resultSet -> {
                reportList = new SpecimenReportListBuilder().buildList(resultSet);
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

    private int getCount(String query) {
        int[] count = {0};
        try {
            jdbcTemplate.query(query, resultSet -> {
                if (resultSet.next()) {
                    count[0] = resultSet.getInt("count");
                }
                return null;
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return count[0];
    }

    private void executeUpdate(String query) {
        try {
            jdbcTemplate.execute(query);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
