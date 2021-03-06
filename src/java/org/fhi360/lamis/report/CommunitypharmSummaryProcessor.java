/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.report;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.report.indicator.CommunitypharmSummaryIndicators;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.StringUtil;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author user1
 */
public class CommunitypharmSummaryProcessor {

    private int reportingMonth;
    private int reportingYear;
    private String reportingDateBegin;
    private String reportingDateEnd;
    private int[][] value = new int[22][3];
    private String[] indicator = new String[22];
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private ArrayList<Map<String, Object>> reportList;
    private HashMap parameterMap;
    private long facilityId;
    private long communitypharmId;
    //private static final Log log = LogFactory.getLog(ArtSummaryProcessor.class);

    public CommunitypharmSummaryProcessor() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    public ArrayList<Map<String, Object>> process() {
        reportList = new ArrayList<Map<String, Object>>();
        reportingMonth = DateUtil.getMonth(request.getParameter("reportingMonth"));
        reportingYear = Integer.parseInt(request.getParameter("reportingYear"));
        reportingDateBegin = dateFormat.format(DateUtil.getFirstDateOfMonth(reportingYear, reportingMonth));
        reportingDateEnd = dateFormat.format(DateUtil.getLastDateOfMonth(reportingYear, reportingMonth));
        facilityId = (Long) session.getAttribute("facilityId");
        indicator = new CommunitypharmSummaryIndicators().initialize();
        try {
            //Cummulative ART clients devolved to CP 
            query = "SELECT DISTINCT patient.patient_id, patient.gender, DATEDIFF(YEAR, patient.date_birth, '" + reportingDateBegin + "') AS age FROM patient JOIN devolve ON patient.patient_id = devolve.patient_id "
                    + " WHERE patient.facility_id = " + facilityId + " AND devolve.communitypharm_id = " + communitypharmId;
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");
                    disaggregate(gender, age, 0);
                }
                return null;
            });

            //ART clients devolved to CP this reporting period
            query = "SELECT DISTINCT patient.patient_id, patient.gender, DATEDIFF(YEAR, patient.date_birth, '" + reportingDateBegin + "') AS age FROM patient JOIN devolve ON patient.patient_id = devolve.patient_id "
                    + " WHERE patient.facility_id = " + facilityId + " AND devolve.communitypharm_id = " + communitypharmId + " AND YEAR(devolve.date_devolved) = " + reportingYear + " AND MONTH(devolve.date_devolved) = " + reportingMonth;
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");
                    disaggregate(gender, age, 1);
                }
                return null;
            });

            //Devolved ART clients who where scheduled to access ART refills in CP this reporting period
            query = "SELECT DISTINCT patient.patient_id, patient.gender, DATEDIFF(YEAR, patient.date_birth, '" + reportingDateBegin + "') AS age FROM patient JOIN encounter ON patient.patient_id = encounter.patient_id "
                    + " WHERE patient.facility_id = " + facilityId + " AND encounter.communitypharm_id = " + communitypharmId + " AND YEAR(encounter.next_refill) = " + reportingYear + " AND MONTH(encounter.next_refill) = " + reportingMonth;
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");
                    disaggregate(gender, age, 2);
                }
                return null;
            });

            //Devolved ART clients who accessed ART refills in CP this reporting period
            query = "SELECT DISTINCT patient.patient_id, patient.gender, DATEDIFF(YEAR, patient.date_birth, '" + reportingDateBegin + "') AS age FROM patient JOIN encounter ON patient.patient_id = encounter.patient_id "
                    + " WHERE patient.facility_id = " + facilityId + " AND encounter.communitypharm_id = " + communitypharmId + " AND YEAR(encounter.date_visit) = " + reportingYear + " AND MONTH(encounter.date_visit) = " + reportingMonth;
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");
                    //Devolved ART clients who accessed ART refills in CP this reporting period
                    disaggregate(gender, age, 3);

                    //Devolved ART clients who received refill for Cotrim prophylaxis (CTX) in CP this reporting period
                    String regimen2 = resultSet.getObject("regimen2") == null ? "" : resultSet.getString("regimen2");
                    if (!regimen2.trim().isEmpty()) {
                        disaggregate(gender, age, 6);
                    }

                    //Devolved ART clients who received refill for Isoniazid prophylaxis (INH) in CP this reporting period
                    String regimen3 = resultSet.getObject("regimen3") == null ? "" : resultSet.getString("regimen3");
                    if (!regimen3.trim().isEmpty()) {
                        disaggregate(gender, age, 7);
                    }
                }
                return null;
            });

            //Devolved ART clients who defaulted to come for a refill within 7 days of the appointment date at the CP this reporting period
            query = "SELECT DISTINCT patient.patient_id, patient.gender, DATEDIFF(YEAR, patient.date_birth, '" + reportingDateBegin + "') AS age FROM patient JOIN encounter ON patient.patient_id = encounter.patient_id "
                    + " WHERE patient.facility_id = " + facilityId + " AND encounter.communitypharm_id = " + communitypharmId + " AND YEAR(encounter.next_refill) = " + reportingYear + " AND MONTH(encounter.next_refill) = " + reportingMonth;
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");
                    long patientId = resultSet.getLong("patient_id");
                    query = "SELECT DISTINCT patient_id FROM encounter WHERE patient_id = " + patientId + " AND YEAR(encounter.date_visit) = " + reportingYear + " AND MONTH(encounter.date_visit) = " + reportingMonth;
                    jdbcTemplate.query(query, rs -> {
                        if (!rs.next()) {
                            disaggregate(gender, age, 4);
                        }
                        return null;
                    });
                }
                return null;
            });

            //Devolved ART clients who defaulted to come for a refill, were tracked and returned to pick up ART refill at the CP this reporting period
            //Devolved ART clients who were provided Chronic Care Screening services using care and support checklist this reporting period
            query = "SELECT DISTINCT patient.patient_id, patient.gender, DATEDIFF(YEAR, patient.date_birth, '" + reportingDateBegin + "') AS age FROM patient JOIN chroniccare ON patient.patient_id = chroniccare.patient_id "
                    + " WHERE patient.facility_id = " + facilityId + " AND chroniccare.communitypharm_id = " + communitypharmId + " AND YEAR(chroniccare.date_visit) = " + reportingYear + " AND MONTH(chroniccare.date_visit) = " + reportingMonth;
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");
                    //Devolved ART clients who were provided chronic care screening services using care and support checklist this reporting period
                    disaggregate(gender, age, 8);

                    //Devolved ART clients who were provided chronic care screening services using care and support checklist this reporting period
                    String phdp1 = resultSet.getObject("phdp1") == null ? "" : resultSet.getString("phdp1");
                    if (!phdp1.trim().isEmpty()) {
                        disaggregate(gender, age, 9);
                    }

                    //Devolved ART clients with suspected ADR this reporting period
                    String adrReported = resultSet.getObject("adr_reported") == null ? "" : resultSet.getString("adr_reported");
                    if (adrReported.equalsIgnoreCase("YES")) {
                        disaggregate(gender, age, 10);
                    }

                    //Devolved ART clients with suspected ADR this reporting period
                    String adrReferred = resultSet.getObject("adr_referred") == null ? "" : resultSet.getString("adr_referred");
                    if (!adrReferred.equalsIgnoreCase("YES")) {
                        disaggregate(gender, age, 11);
                    }
                }
                return null;
            });

            //Devolved ART clients who are eligible to return back hospital for semiannual clinical and laboratory reassessments this reporting period
            query = "SELECT DISTINCT patient.patient_id, patient.gender, DATEDIFF(YEAR, patient.date_birth, '" + reportingDateBegin + "') AS age FROM patient JOIN encounter ON patient.patient_id = encounter.patient_id "
                    + " WHERE patient.facility_id = " + facilityId + " AND encounter.communitypharm_id = " + communitypharmId + " AND YEAR(encounter.next_appointment) = " + reportingYear + " AND MONTH(encounter.next_appointment) = " + reportingMonth;
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");
                    disaggregate(gender, age, 12);
                }
                return null;
            });

            //Devolved ART clients who are eligible for viral load reassessments this reporting period
            //Devolved ART clients who accessed viral load re-assessment with viral load result this reporting period
            query = "SELECT DISTINCT patient.patient_id, patient.gender, DATEDIFF(YEAR, patient.date_birth, '" + reportingDateBegin + "') AS age FROM patient JOIN devolve ON patient.patient_id = devolve.patient_id "
                    + " WHERE patient.facility_id = " + facilityId + " AND devolve.communitypharm_id = " + communitypharmId;
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");
                    long patientId = resultSet.getLong("patient_id");
                    query = "SELECT * FROM laboratory WHERE patient_id = " + patientId + " AND YEAR(date_reported) = " + reportingYear + " AND MONTH(date_reported) = " + reportingMonth + " AND labtest_id = 16";
                    jdbcTemplate.query(query, rs -> {
                        while (rs.next()) {
                            //Devolved ART clients who accessed viral load re-assessment with viral load result this reporting period
                            disaggregate(gender, age, 15);

                            //Devolved ART clients who had viral load re-assessment and are virologically suppressed (VL <1000 c/ml) this reporting month
                            String viralLoad = resultSet.getString("resultab") == null ? "" : resultSet.getString("resultab");
                            if (!viralLoad.trim().isEmpty()) {
                                if (StringUtil.isInteger(viralLoad)) {
                                    if (Double.valueOf(StringUtil.stripCommas(viralLoad)) < 1000) {
                                        disaggregate(gender, age, 16);
                                    }
                                }
                            }
                        }
                        return null;
                    });
                }
                return null;
            });

            //Devolved ART clients who discontinued Community ART refill services and returned back to the Hospital to continue ART refill services this reporting 
            query = "SELECT DISTINCT patient.patient_id, patient.gender, DATEDIFF(YEAR, patient.date_birth, '" + reportingDateBegin + "') AS age FROM patient JOIN devolve ON patient.patient_id = devolve.patient_id "
                    + " WHERE patient.facility_id = " + facilityId + " AND devolve.communitypharm_id = " + communitypharmId + " AND YEAR(date_discontinued) = " + reportingYear + " AND MONTH(date_discontinued) = " + reportingMonth;
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");
                    disaggregate(gender, age, 17);
                }
                return null;
            });

            //Populating indicator values
            for (int i = 0; i < indicator.length; i++) {
                String male1 = Integer.toString(value[i][0]);
                String male2 = Integer.toString(value[i][1]);
                String fmale1 = Integer.toString(value[i][2]);
                String fmale2 = Integer.toString(value[i][3]);

                int total = value[i][0] + value[i][1] + value[i][2] + value[i][3];

                // create map of values 
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("sno", "ART" + Integer.toString(i + 1));
                map.put("indicator", indicator[i]);
                map.put("male1", male1);
                map.put("male2", male2);
                map.put("fmale1", fmale1);
                map.put("fmale2", fmale2);
                map.put("total", Integer.toString(total));
                reportList.add(map);
            }
        } catch (Exception exception) {
            //disconnect from database
        }
        return reportList;
    }

    public HashMap getReportParameters() {
        reportingMonth = DateUtil.getMonth(request.getParameter("reportingMonth"));
        reportingYear = Integer.parseInt(request.getParameter("reportingYear"));
        parameterMap = new HashMap();
        parameterMap.put("reportingMonth", request.getParameter("reportingMonth"));
        parameterMap.put("reportingYear", request.getParameter("reportingYear"));

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
        if (gender.trim().equalsIgnoreCase("Male")) {
            if (age < 15) {
                value[i][0]++;  //males < 15yr
            } else {
                value[i][1]++;  //males >= 15yrs
            }
        } else {
            if (age < 15) {
                value[i][2]++;  //fmales < 15yr
            } else {
                value[i][3]++;  //fmales >= 15yrs
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

}
