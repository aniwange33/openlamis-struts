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

public class PcrLabSummaryProcessor {

    private int reportingMonth;
    private int reportingYear;
    private String reportingDateBegin;
    private String reportingDateEnd;

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private ArrayList<Map<String, Object>> reportList;
    private HashMap parameterMap;
    private long facilityId;

    public PcrLabSummaryProcessor() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    public ArrayList<Map<String, Object>> process() {
        reportList = new ArrayList<>();
        reportingMonth = DateUtil.getMonth(request.getParameter("reportingMonth"));
        reportingYear = Integer.parseInt(request.getParameter("reportingYear"));
        reportingDateBegin = DateUtil.parseDateToString(DateUtil.getFirstDateOfMonth(reportingYear, reportingMonth), "yyyy-MM-dd");
        reportingDateEnd = DateUtil.parseDateToString(DateUtil.getLastDateOfMonth(reportingYear, reportingMonth), "yyyy-MM-dd");
        facilityId = (Long) session.getAttribute("facilityId");

        query = "SELECT COUNT(*) AS count FROM specimen WHERE facility_id = " + facilityId + " AND MONTH(date_received) = " + reportingMonth + " AND YEAR(date_received) = " + reportingYear;
        int samplesReceived = getCount(query);

        query = "SELECT COUNT(*) AS count FROM specimen WHERE facility_id = " + facilityId + " AND MONTH(date_assay) = " + reportingMonth + " AND YEAR(date_assay) = " + reportingYear;
        int testDone = getCount(query);

        query = "SELECT COUNT(*) AS count FROM specimen WHERE facility_id = " + facilityId + " AND MONTH(date_dispatched) = " + reportingMonth + " AND YEAR(date_dispatched) = " + reportingYear + " AND result = 'Negative'";
        int resultSentNegative = getCount(query);

        query = "SELECT COUNT(*) AS count FROM specimen WHERE facility_id = " + facilityId + " AND MONTH(date_dispatched) = " + reportingMonth + " AND YEAR(date_dispatched) = " + reportingYear + " AND result = 'Positive'";
        int resultSentPositive = getCount(query);

        query = "SELECT COUNT(*) AS count FROM specimen WHERE facility_id = " + facilityId + " AND MONTH(date_dispatched) = " + reportingMonth + " AND YEAR(date_dispatched) = " + reportingYear + " AND result IN('Indeterminate', 'Fail', 'Invalid')";
        int resultSentIndeterminate = getCount(query);

        query = "SELECT COUNT(*) AS count FROM specimen WHERE facility_id = " + facilityId + " AND MONTH(date_dispatched) = " + reportingMonth + " AND YEAR(date_dispatched) = " + reportingYear + " AND DATEDIFF(DAY, date_received, date_assay) > 10";
        int testAboveThreshold = getCount(query);

        // create an array from object properties 
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("samplesReceived", samplesReceived);
        map.put("testDone", testDone);
        map.put("resultSentNegative", resultSentNegative);
        map.put("resultSentPositive", resultSentPositive);
        map.put("resultSentIndeterminate", resultSentIndeterminate);
        map.put("resultSentTotal", resultSentNegative + resultSentPositive + resultSentIndeterminate);
        map.put("testAboveThreshold", testAboveThreshold);
        reportList.add(map);
        return reportList;
    }

    public HashMap getReportParameters() {
        parameterMap = new HashMap();
        reportingMonth = DateUtil.getMonth(request.getParameter("reportingMonth"));
        reportingYear = Integer.parseInt(request.getParameter("reportingYear"));
        parameterMap.put("reportingMonth", request.getParameter("reportingMonth"));
        parameterMap.put("reportingYear", request.getParameter("reportingYear"));
        facilityId = (Long) session.getAttribute("facilityId");

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

}
