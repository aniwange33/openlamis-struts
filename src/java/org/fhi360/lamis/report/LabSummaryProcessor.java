/**
 *
 * @author AALOZIE
 */
package org.fhi360.lamis.report;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.report.indicator.LabSummaryIndicators;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.StringUtil;
import org.springframework.jdbc.core.JdbcTemplate;

public class LabSummaryProcessor {

    private int reportingMonth;
    private int reportingYear;
    private String reportingDateBegin;
    private String reportingDateEnd;
    private int[][] value = new int[19][2];
    private String[] indicator = new String[19];

    private HttpServletRequest request;
    private HttpSession session;
    private String query;

    private ArrayList<Map<String, Object>> reportList;
    private HashMap parameterMap;
    private long facilityId;
    String description, dateStarted, resultab, resultpc;
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    public LabSummaryProcessor() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    public ArrayList<Map<String, Object>> process() {
        reportList = new ArrayList<Map<String, Object>>();
        reportingMonth = DateUtil.getMonth(request.getParameter("reportingMonth"));
        reportingYear = Integer.parseInt(request.getParameter("reportingYear"));
        reportingDateBegin = DateUtil.parseDateToString(DateUtil.getFirstDateOfMonth(reportingYear, reportingMonth), "yyyy-MM-dd");
        reportingDateEnd = DateUtil.parseDateToString(DateUtil.getLastDateOfMonth(reportingYear, reportingMonth), "yyyy-MM-dd");
        facilityId = (Long) session.getAttribute("facilityId");
        indicator = new LabSummaryIndicators().initialize();

        try {
            query = "SELECT patient.patient_id, patient.gender, DATEDIFF(YEAR, patient.date_birth, '" + reportingDateBegin + "') AS age, patient.date_started, laboratory.resultab, laboratory.resultpc, labtest.description "
                    + " FROM patient JOIN laboratory ON patient.patient_id = laboratory.patient_id JOIN labtest ON laboratory.labtest_id = labtest.labtest_id WHERE patient.facility_id = " + facilityId + " AND laboratory.facility_id = " + facilityId + " AND MONTH(laboratory.date_reported) = " + reportingMonth + " AND YEAR(laboratory.date_reported) = " + reportingYear;
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    description = resultSet.getString("description");
                    resultab = (resultSet.getString("resultab") == null) ? "" : resultSet.getString("resultab");
                    resultpc = (resultSet.getString("resultpc") == null) ? "" : resultSet.getString("resultpc");
                    dateStarted = (resultSet.getDate("date_started") == null) ? "" : DateUtil.parseDateToString(resultSet.getDate("date_started"), "MM/dd/yyyy");
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");

                    // count number of CD4 count test done by patients
                    if (description.equals("CD4")) {
                        if (dateStarted.isEmpty()) {  //Non-ART patients
                            if (age >= 5 && !resultab.isEmpty() && StringUtil.isInteger(resultab)) {
                                if (Double.parseDouble(resultab) <= 200) {
                                    value[0][0]++;
                                } else {
                                    if (Double.parseDouble(resultab) >= 200 && Double.parseDouble(resultab) <= 300) {
                                        value[1][0]++;
                                    } else {
                                        value[2][0]++;
                                    }
                                }
                            } //age less than 5 years 
                            else {
                                if (!resultpc.isEmpty() && StringUtil.isInteger(resultpc)) {
                                    if (Double.parseDouble(resultpc) < 15) {
                                        value[3][0]++;
                                    } else {
                                        value[4][0]++;
                                    }
                                }
                            }
                        } else { // ART patients
                            if (age >= 5 && !resultab.isEmpty() && StringUtil.isInteger(resultab)) {
                                if (Double.parseDouble(resultab) <= 200) {
                                    value[0][1]++;
                                } else {
                                    if (Double.parseDouble(resultab) >= 200 && Double.parseDouble(resultab) <= 300) {
                                        value[1][1]++;
                                    } else {
                                        value[2][1]++;
                                    }
                                }
                            } //age less than 5 years 
                            else {
                                if (!resultpc.isEmpty() && StringUtil.isInteger(resultpc)) {
                                    if (Double.parseDouble(resultpc) < 15) {
                                        value[3][1]++;
                                    } else {
                                        value[4][1]++;
                                    }
                                }
                            }
                        }
                    }

                    // count number of FBC test done by patients
                    if (description.equals("WBC")) {
                        if (dateStarted.isEmpty()) {
                            value[5][0]++;
                        } else {
                            value[5][1]++;
                        }
                    }
                    // count number of GOT test done by patients
                    if (description.equals("AST/SGOT")) {
                        if (dateStarted.isEmpty()) {
                            value[6][0]++;
                        } else {
                            value[6][1]++;
                        }
                    }
                    // count number of GPT test done by patients
                    if (description.equals("ALT/SGPT")) {
                        if (dateStarted.isEmpty()) {
                            value[7][0]++;
                        } else {
                            value[7][1]++;
                        }
                    }
                    // count number of Creatine test done by patients
                    if (description.equals("Creatinine")) {
                        if (dateStarted.isEmpty()) {
                            value[8][0]++;
                        } else {
                            value[8][1]++;
                        }
                    }
                    // count number of K+ test done by patients
                    if (description.equals("Potasium (K+)")) {
                        if (dateStarted.isEmpty()) {
                            value[9][0]++;
                        } else {
                            value[9][1]++;
                        }
                    }
                    // count number of Glucose test done by patients
                    if (description.equals("GLUCOSE")) {
                        if (dateStarted.isEmpty()) {
                            value[10][0]++;
                        } else {
                            value[10][1]++;
                        }
                    }

                    // count number of VDRL test done by patients
                    if (description.equals("VDRL") && gender.equalsIgnoreCase("Female")) {
                        if (dateStarted.isEmpty()) {
                            if (resultab.equals("-")) {  // negative VDRL result Non-ART
                                value[11][0]++;
                            } else {
                                value[12][0]++;
                            }
                        } else {
                            if (resultab.equals("-")) {  // negative VDRL result ART
                                value[11][1]++;
                            } else {
                                value[12][1]++;
                            }
                        }
                    }

                    // count number of Pregnancy test done by patients
                    if (description.equals("Pregnancy") && gender.equalsIgnoreCase("Female")) {
                        if (dateStarted.isEmpty()) {
                            value[13][0]++;
                        } else {
                            value[13][1]++;
                        }
                    }
                    // count number of HBsAg test done by patients
                    if (description.equals("HBsAg")) {
                        if (dateStarted.isEmpty()) {
                            value[14][0]++;
                        } else {
                            value[14][1]++;
                        }
                    }
                    // count number of HCV test done by patients
                    if (description.equals("HCV")) {
                        if (dateStarted.isEmpty()) {
                            value[15][0]++;
                        } else {
                            value[15][1]++;
                        }
                    }
                    // count number of Malaria test done by patients
                    if (description.equals("Malaria")) {
                        if (dateStarted.isEmpty()) {
                            value[16][0]++;
                        } else {
                            value[16][1]++;
                        }
                    }
                    // count number of Stool microscopy test done by patients
                    if (description.equals("Stool microscopy")) {
                        if (dateStarted.isEmpty()) {
                            value[17][0]++;
                        } else {
                            value[17][1]++;
                        }
                    }
                    // count number of Sputum Smear test done by patients
                    if (description.equals("Sputum Smear")) {
                        if (dateStarted.isEmpty()) {
                            value[18][0]++;
                        } else {
                            value[18][1]++;
                        }
                    }
                } // end while loop
                return null;
            });

            for (int i = 0; i < 18; i++) {
                String nonart = Integer.toString(value[i][0]);
                String art = Integer.toString(value[i][1]);
                int total = value[i][0] + value[i][1];

                // create map of values 
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("indicator", indicator[i]);
                map.put("nonart", nonart);
                map.put("art", art);
                map.put("total", Integer.toString(total));
                reportList.add(map);

            }
        } catch (Exception exception) {
            exception.printStackTrace();  //disconnect from database
        }
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

}
