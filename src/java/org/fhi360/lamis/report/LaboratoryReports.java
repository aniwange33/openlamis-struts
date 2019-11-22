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
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.builder.LaboratoryListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;

public class LaboratoryReports {

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

    public LaboratoryReports() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    public ArrayList<Map<String, Object>> process() {
        System.out.println("Lab result query.....");
        reportTitle = "Patients Laboratory Result - " + request.getParameter("description");
        facilityId = (Long) session.getAttribute("facilityId");
        long labtestId = Long.parseLong(request.getParameter("labtestId"));
        reportingDateBegin = DateUtil.formatDateString(request.getParameter("reportingDateBegin"), "MM/dd/yyyy", "yyyy-MM-dd");
        reportingDateEnd = DateUtil.formatDateString(request.getParameter("reportingDateEnd"), "MM/dd/yyyy", "yyyy-MM-dd");
        query = "SELECT laboratory.*, patient.hospital_num, patient.surname, patient.other_names, patient.gender, patient.date_birth, DATEDIFF(YEAR, patient.date_birth, CURDATE()) AS age, patient.current_status, patient.date_current_status, patient.date_started, labtest.measureab, labtest.measurepc FROM laboratory JOIN patient ON laboratory.patient_id = patient.patient_id JOIN labtest ON laboratory.labtest_id = labtest.labtest_id WHERE laboratory.facility_id = " + facilityId + " AND laboratory.labtest_id = " + labtestId + " AND laboratory.date_reported >= '" + reportingDateBegin + "' AND laboratory.date_reported <= '" + reportingDateEnd + "' ORDER BY laboratory.date_reported";
        generateReportList(query);
        return reportList;
    }

    public void generateReportList(String query) {
        reportList = new ArrayList<Map<String, Object>>();
        try {
            // fetch the required records from the database
            jdbcTemplate.query(query, resultSet -> {
                reportList = new LaboratoryListBuilder().buildLabResultQueryList(resultSet);
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
            exception.printStackTrace();  //disconnect from database
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
