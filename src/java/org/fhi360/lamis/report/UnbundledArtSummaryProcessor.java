/**
 *
 * @author AALOZIE
 */
package org.fhi360.lamis.report;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.utility.DateUtil;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.Constants;
import static org.fhi360.lamis.utility.StringUtil.isInteger;
import org.springframework.jdbc.core.JdbcTemplate;

public class UnbundledArtSummaryProcessor {

    private int reportingMonth;
    private int reportingYear;
    private String reportingDateBegin;
    private String reportingDateEnd;

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private HashMap parameterMap;
    private long facilityId;
    //private static final Log log = LogFactory.getLog(ArtSummaryProcessor.class);

    private int agem1, agem2, agem3, agem4, agem5, agem6, agem7, agem8, agem9, agem10, agem11, agem12;
    ;
    private int agef1, agef2, agef3, agef4, agef5, agef6, agef7, agef8, agef9, agef10, agef11, agef12;
    private int agem13_1, agem14_1, agem15_1, agem13_2, agem14_2, agem15_2;
    private int agef13_1, agef14_1, agef15_1, agef13_2, agef14_2, agef15_2;
    private int preg, feeding, tbm, tbf;

    public UnbundledArtSummaryProcessor() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    public HashMap process() {

        parameterMap = new HashMap();
        reportingMonth = DateUtil.getMonth(request.getParameter("reportingMonth"));
        reportingYear = Integer.parseInt(request.getParameter("reportingYear"));
        reportingDateBegin = dateFormat.format(DateUtil.getFirstDateOfMonth(reportingYear, reportingMonth));
        reportingDateEnd = dateFormat.format(DateUtil.getLastDateOfMonth(reportingYear, reportingMonth));
        facilityId = (Long) session.getAttribute("facilityId");

        try {
            //ResultSet rs;
            System.out.println("Computing ART1.....");
            //ART 1
            //Total Number of HIV-positive newly enrolled in clinical care during the month (excludes transfer-in)
            initVariables();
            query = "SELECT DISTINCT patient_id, gender, DATEDIFF(YEAR, date_birth, '" + reportingDateBegin + "') AS age, pregnant, breastfeeding, tb_status FROM patient WHERE facility_id = " + facilityId + " AND YEAR(date_registration) = " + reportingYear + " AND MONTH(date_registration) = " + reportingMonth + " AND status_registration = 'HIV+ non ART'";
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");
                    int pregnant = resultSet.getInt("pregnant");
                    int breastfeeding = resultSet.getInt("breastfeeding");
                    String tbStatus = resultSet.getString("tb_status") == null ? "" : resultSet.getString("tb_status");

                    disaggregate(gender, age);
                    if (gender.trim().equalsIgnoreCase("Male")) {
                        //check for TB status during enrolmemnt
                        if (tbStatus.equalsIgnoreCase("Currently on TB treatment") || tbStatus.equalsIgnoreCase("TB positive not on TB drugs")) {
                            tbm++;
                        }
                    } else {
                        //check if client is pregnant or breast feeding during enrolment
                        if (pregnant == 1) {
                            preg++;
                        } else {
                            if (breastfeeding == 1) {
                                feeding++;
                            }
                        }
                        //check for TB status during enrolmemnt
                        if (tbStatus.equalsIgnoreCase("Currently on TB treatment") || tbStatus.equalsIgnoreCase("TB positive not on TB drugs")) {
                            tbf++;
                        }
                    }
                }
                return null;
            });
            //Populate the report parameter map with values computed for ART 1
            parameterMap.put("art1m1", Integer.toString(agem1));
            parameterMap.put("art1f1", Integer.toString(agef1));
            parameterMap.put("art1t1", Integer.toString(agem1 + agef1));

            parameterMap.put("art1m2", Integer.toString(agem2));
            parameterMap.put("art1f2", Integer.toString(agef2));
            parameterMap.put("art1t2", Integer.toString(agem2 + agef2));

            parameterMap.put("art1m3", Integer.toString(agem3));
            parameterMap.put("art1f3", Integer.toString(agef3));
            parameterMap.put("art1t3", Integer.toString(agem3 + agef3));

            parameterMap.put("art1m4", Integer.toString(agem4));
            parameterMap.put("art1f4", Integer.toString(agef4));
            parameterMap.put("art1t4", Integer.toString(agem4 + agef4));

            parameterMap.put("art1m5", Integer.toString(agem5));
            parameterMap.put("art1f5", Integer.toString(agef5));
            parameterMap.put("art1t5", Integer.toString(agem5 + agef5));

            parameterMap.put("art1m6", Integer.toString(agem6));
            parameterMap.put("art1f6", Integer.toString(agef6));
            parameterMap.put("art1t6", Integer.toString(agem6 + agef6));

            parameterMap.put("art1m7", Integer.toString(agem7));
            parameterMap.put("art1f7", Integer.toString(agef7));
            parameterMap.put("art1t7", Integer.toString(agem7 + agef7));

            parameterMap.put("art1m8", Integer.toString(agem8));
            parameterMap.put("art1f8", Integer.toString(agef8));
            parameterMap.put("art1t8", Integer.toString(agem8 + agef8));

            parameterMap.put("art1m9", Integer.toString(agem9));
            parameterMap.put("art1f9", Integer.toString(agef9));
            parameterMap.put("art1t9", Integer.toString(agem9 + agef9));

            parameterMap.put("art1m10", Integer.toString(agem10));
            parameterMap.put("art1f10", Integer.toString(agef10));
            parameterMap.put("art1t10", Integer.toString(agem10 + agef10));

            parameterMap.put("art1m11", Integer.toString(agem11));
            parameterMap.put("art1f11", Integer.toString(agef11));
            parameterMap.put("art1t11", Integer.toString(agem11 + agef11));

            parameterMap.put("art1m12", Integer.toString(agem12));
            parameterMap.put("art1f12", Integer.toString(agef12));
            parameterMap.put("art1t12", Integer.toString(agem12 + agef12));

            parameterMap.put("art1f13", Integer.toString(preg));
            parameterMap.put("art1t13", Integer.toString(preg));

            parameterMap.put("art1f14", Integer.toString(feeding));
            parameterMap.put("art1t14", Integer.toString(feeding));

            parameterMap.put("art1m15", Integer.toString(tbm));
            parameterMap.put("art1f15", Integer.toString(tbf));
            parameterMap.put("art1t15", Integer.toString(tbm + tbf));

            System.out.println("Computing ART2.....");
            //ART 2
            //Total number of people living with HIV who are currently in HIV care who received at least one of the following
            //by the end of the month: clinical assessment(WHO staging) OR CD4 count OR viral load OR current on treatment           
            initVariables();

            executeUpdate("DROP INDEX IF EXISTS idx_visit");
            executeUpdate("DROP TABLE IF EXISTS visit");
            executeUpdate("CREATE TEMPORARY TABLE visit AS SELECT * FROM clinic WHERE facility_id = " + facilityId + " AND date_visit >= DATEADD('MONTH', -6, '" + reportingDateBegin + "') AND date_visit <= '" + reportingDateEnd + "' AND clinic_stage IS NOT NULL OR clinic_stage != ''");
            executeUpdate("CREATE INDEX idx_visit ON visit(patient_id)");

            executeUpdate("DROP INDEX IF EXISTS idx_preg");
            executeUpdate("DROP TABLE IF EXISTS preg");
            executeUpdate("CREATE TEMPORARY TABLE preg AS SELECT * FROM clinic WHERE facility_id = " + facilityId + " AND date_visit >= DATEADD('MONTH', -9, '" + reportingDateBegin + "') AND date_visit <= '" + reportingDateEnd + "' ORDER BY date_visit DESC LIMIT 1");
            executeUpdate("CREATE INDEX idx_preg ON preg(patient_id)");

            executeUpdate("DROP INDEX IF EXISTS idx_pharm");
            executeUpdate("DROP TABLE IF EXISTS pharm");
            executeUpdate("CREATE TEMPORARY TABLE pharm AS SELECT * FROM pharmacy WHERE facility_id = " + facilityId + " AND date_visit <= '" + reportingDateEnd + "' AND regimentype_id IN (1, 2, 3, 4, 14)");
            executeUpdate("CREATE INDEX idx_pharm ON pharm(patient_id)");

            executeUpdate("DROP INDEX IF EXISTS idx_lab");
            executeUpdate("DROP TABLE IF EXISTS lab");
            executeUpdate("CREATE TEMPORARY TABLE lab AS SELECT * FROM laboratory WHERE facility_id = " + facilityId + " AND date_reported >= DATEADD('MONTH', -6, '" + reportingDateBegin + "') AND date_reported <= '" + reportingDateEnd + "' AND labtest_id IN (1, 16)");
            executeUpdate("CREATE INDEX idx_lab ON lab(patient_id)");

            query = "SELECT DISTINCT patient_id, gender, DATEDIFF(YEAR, date_birth, '" + reportingDateBegin + "') AS age, date_started FROM patient WHERE facility_id = " + facilityId + " AND date_registration <= '" + reportingDateEnd + "' AND (current_status IN ('ART Start', 'ART Restart', 'ART Transfer In') OR (current_status IN ('ART Transfer Out', 'Lost to Follow Up', 'Stopped Treatment', 'Known Death') AND date_current_status > '" + reportingDateEnd + "'))";
            //query = "SELECT DISTINCT patient_id, gender, DATEDIFF(YEAR, date_birth, '" + reportingDateBegin + "') AS age FROM patient WHERE facility_id = " + facilityId + " AND date_registration <= '" + reportingDateEnd + "' AND  current_status IN (" + Constants.ClientStatus.ON_TREATMENT + ") OR (current_status IN (" + Constants.ClientStatus.LOSSES + ") AND date_current_status > '" + reportingDateEnd + "')";
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    long patientId = resultSet.getLong("patient_id");
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");
                    Date dateStarted = resultSet.getDate("date_started");

                    int[] pregnant = {0};
                    int[] breastfeeding = {0};

                    boolean[] count = {false};

                    query = "SELECT DISTINCT patient_id FROM visit WHERE patient_id = " + patientId;
                    if (found(query)) {
                        count[0] = true;
                    } else {
                        query = "SELECT DISTINCT patient_id FROM lab WHERE patient_id = " + patientId;
                        if (found(query)) {
                            count[0] = true;
                        } else {
                            if (dateStarted != null) {
                                jdbcTemplate.query(getLastRefillVisit(patientId), rs -> {
                                    while (rs != null && rs.next()) {
                                        Date dateLastRefill = rs.getDate("date_visit");
                                        int duration = rs.getInt("duration");
                                        int monthRefill = duration / 30;
                                        if (monthRefill <= 0) {
                                            monthRefill = 1;
                                        }

                                        if (dateLastRefill != null) {
                                            //If the last refill date plus refill duration plus 28 days is before the last day of the reporting month this patient is LTFU     if(DateUtil.addYearMonthDay(lastRefill, duration+90, "day(s)").before(reportingDateEnd)) 
                                            if (!DateUtil.addYearMonthDay(dateLastRefill, duration + Constants.Reports.LTFU_PEPFAR, "DAY").before(DateUtil.getLastDateOfMonth(reportingYear, reportingMonth))) {
                                                count[0] = true;
                                            }
                                        }
                                    }
                                    return null;
                                });
                            }
                        }
                    }

                    if (count[0]) {
                        disaggregate(gender, age);
                        if (gender.trim().equalsIgnoreCase("Female")) {
                            //check if client is pregnant or breast feeding during enrolment
                            query = "SELECT pregnant, breastfeeding FROM preg WHERE patient_id = " + patientId + " ORDER BY date_visit DESC LIMIT 1";
                            jdbcTemplate.query(query, rs -> {
                                while (rs.next()) {
                                    pregnant[0] = rs.getInt("pregnant");
                                    breastfeeding[0] = rs.getInt("breastfeeding");
                                }
                                return null;
                            });
                            if (pregnant[0] == 1) {
                                preg++;
                            } else {
                                if (breastfeeding[0] == 1) {
                                    feeding++;
                                }
                            }
                        }
                    }
                }
                return null;
            });
            //Populate the report parameter map with values computed for ART 2
            parameterMap.put("art2m1", Integer.toString(agem1));
            parameterMap.put("art2f1", Integer.toString(agef1));
            parameterMap.put("art2t1", Integer.toString(agem1 + agef1));

            parameterMap.put("art2m2", Integer.toString(agem2));
            parameterMap.put("art2f2", Integer.toString(agef2));
            parameterMap.put("art2t2", Integer.toString(agem2 + agef2));

            parameterMap.put("art2m3", Integer.toString(agem3));
            parameterMap.put("art2f3", Integer.toString(agef3));
            parameterMap.put("art2t3", Integer.toString(agem3 + agef3));

            parameterMap.put("art2m4", Integer.toString(agem4));
            parameterMap.put("art2f4", Integer.toString(agef4));
            parameterMap.put("art2t4", Integer.toString(agem4 + agef4));

            parameterMap.put("art2m5", Integer.toString(agem5));
            parameterMap.put("art2f5", Integer.toString(agef5));
            parameterMap.put("art2t5", Integer.toString(agem5 + agef5));

            parameterMap.put("art2m6", Integer.toString(agem6));
            parameterMap.put("art2f6", Integer.toString(agef6));
            parameterMap.put("art2t6", Integer.toString(agem6 + agef6));

            parameterMap.put("art2m7", Integer.toString(agem7));
            parameterMap.put("art2f7", Integer.toString(agef7));
            parameterMap.put("art2t7", Integer.toString(agem7 + agef7));

            parameterMap.put("art2m8", Integer.toString(agem8));
            parameterMap.put("art2f8", Integer.toString(agef8));
            parameterMap.put("art2t8", Integer.toString(agem8 + agef8));

            parameterMap.put("art2m9", Integer.toString(agem9));
            parameterMap.put("art2f9", Integer.toString(agef9));
            parameterMap.put("art2t9", Integer.toString(agem9 + agef9));

            parameterMap.put("art2m10", Integer.toString(agem10));
            parameterMap.put("art2f10", Integer.toString(agef10));
            parameterMap.put("art2t10", Integer.toString(agem10 + agef10));

            parameterMap.put("art2m11", Integer.toString(agem11));
            parameterMap.put("art2f11", Integer.toString(agef11));
            parameterMap.put("art2t11", Integer.toString(agem11 + agef11));

            parameterMap.put("art2m12", Integer.toString(agem12));
            parameterMap.put("art2f12", Integer.toString(agef12));
            parameterMap.put("art2t12", Integer.toString(agem12 + agef12));

            parameterMap.put("art2f13", Integer.toString(preg));
            parameterMap.put("art2t13", Integer.toString(preg));

            System.out.println("Computing ART3.....");
            //ART 3
            //Total number of people living with HIV newly started on ART during the month (excludes ART transfer-in)
            initVariables();

            executeUpdate("DROP INDEX IF EXISTS idx_preg");
            executeUpdate("DROP TABLE IF EXISTS preg");
            executeUpdate("CREATE TEMPORARY TABLE preg AS SELECT * FROM clinic WHERE facility_id = " + facilityId + " AND commence = 1");
            executeUpdate("CREATE INDEX idx_preg ON preg(patient_id)");

            query = "SELECT DISTINCT patient_id, gender, DATEDIFF(YEAR, date_birth, '" + reportingDateBegin + "') AS age FROM patient WHERE facility_id = " + facilityId + " AND YEAR(date_started) = " + reportingYear + " AND MONTH(date_started) = " + reportingMonth + " AND status_registration != 'ART Transfer In'";
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    long patientId = resultSet.getLong("patient_id");
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");

                    int[] pregnant = {0};
                    int[] breastfeeding = {0};

                    disaggregate(gender, age);

                    if (gender.trim().equalsIgnoreCase("Female")) {
                        //check if client is pregnant or breast feeding during visit
                        query = "SELECT pregnant, breastfeeding FROM preg WHERE patient_id = " + patientId + " ORDER BY date_visit DESC LIMIT 1";
                        jdbcTemplate.query(query, rs -> {
                            while (rs.next()) {
                                pregnant[0] = rs.getInt("pregnant");
                                breastfeeding[0] = rs.getInt("breastfeeding");
                            }
                            return null;
                        });
                        if (pregnant[0] == 1) {
                            preg++;
                        } else {
                            if (breastfeeding[0] == 1) {
                                feeding++;
                            }
                        }
                    }
                }
                return null;
            });
            //Populate the report parameter map with values computed for ART 3
            parameterMap.put("art3m1", Integer.toString(agem1));
            parameterMap.put("art3f1", Integer.toString(agef1));
            parameterMap.put("art3t1", Integer.toString(agem1 + agef1));

            parameterMap.put("art3m2", Integer.toString(agem2));
            parameterMap.put("art3f2", Integer.toString(agef2));
            parameterMap.put("art3t2", Integer.toString(agem2 + agef2));

            parameterMap.put("art3m3", Integer.toString(agem3));
            parameterMap.put("art3f3", Integer.toString(agef3));
            parameterMap.put("art3t3", Integer.toString(agem3 + agef3));

            parameterMap.put("art3m4", Integer.toString(agem4));
            parameterMap.put("art3f4", Integer.toString(agef4));
            parameterMap.put("art3t4", Integer.toString(agem4 + agef4));

            parameterMap.put("art3m5", Integer.toString(agem5));
            parameterMap.put("art3f5", Integer.toString(agef5));
            parameterMap.put("art3t5", Integer.toString(agem5 + agef5));

            parameterMap.put("art3m6", Integer.toString(agem6));
            parameterMap.put("art3f6", Integer.toString(agef6));
            parameterMap.put("art3t6", Integer.toString(agem6 + agef6));

            parameterMap.put("art3m7", Integer.toString(agem7));
            parameterMap.put("art3f7", Integer.toString(agef7));
            parameterMap.put("art3t7", Integer.toString(agem7 + agef7));

            parameterMap.put("art3m8", Integer.toString(agem8));
            parameterMap.put("art3f8", Integer.toString(agef8));
            parameterMap.put("art3t8", Integer.toString(agem8 + agef8));

            parameterMap.put("art3m9", Integer.toString(agem9));
            parameterMap.put("art3f9", Integer.toString(agef9));
            parameterMap.put("art3t9", Integer.toString(agem9 + agef9));

            parameterMap.put("art3m10", Integer.toString(agem10));
            parameterMap.put("art3f10", Integer.toString(agef10));
            parameterMap.put("art3t10", Integer.toString(agem10 + agef10));

            System.out.println("ART3M11........" + agem11);
            System.out.println("ART3F11........" + agef11);

            parameterMap.put("art3m11", Integer.toString(agem11));
            parameterMap.put("art3f11", Integer.toString(agef11));
            parameterMap.put("art3t11", Integer.toString(agem11 + agef11));

            parameterMap.put("art3m12", Integer.toString(agem12));
            parameterMap.put("art3f12", Integer.toString(agef12));
            parameterMap.put("art3t12", Integer.toString(agem12 + agef12));

            parameterMap.put("art3f13", Integer.toString(preg));
            parameterMap.put("art3t13", Integer.toString(preg));

            parameterMap.put("art3f14", Integer.toString(feeding));
            parameterMap.put("art3t14", Integer.toString(feeding));

            System.out.println("Computing ART4.....");
            //ART 4
            //Total number of people living with HIV who are currently receiving ART during the month (All regimen)
            initVariables();

            executeUpdate("DROP INDEX IF EXISTS idx_preg");
            executeUpdate("DROP TABLE IF EXISTS preg");
            executeUpdate("CREATE TEMPORARY TABLE preg AS SELECT * FROM clinic WHERE facility_id = " + facilityId + " AND date_visit >=  DATEADD('MONTH', -9, '" + reportingDateBegin + "') AND date_visit <= '" + reportingDateEnd + "'");
            executeUpdate("CREATE INDEX idx_preg ON preg(patient_id)");

            query = "SELECT DISTINCT patient_id, gender, DATEDIFF(YEAR, date_birth, '" + reportingDateBegin + "') AS age FROM patient WHERE facility_id = " + facilityId + " AND current_status IN ('ART Start', 'ART Restart', 'ART Transfer In') OR (current_status IN ('ART Transfer Out', 'Lost to Follow Up', 'Stopped Treatment', 'Known Death') AND date_current_status > '" + reportingDateEnd + "') AND date_started IS NOT NULL AND date_started <= '" + reportingDateEnd + "'";
            //query = "SELECT DISTINCT patient_id, gender, DATEDIFF(YEAR, date_birth, '" + reportingDateBegin + "') AS age FROM patient WHERE facility_id = " + facilityId + " AND current_status IN (" + Constants.ClientStatus.ON_TREATMENT + ") OR (current_status IN (" + Constants.ClientStatus.LOSSES + ") AND date_current_status > '" + reportingDateEnd + "') AND date_started IS NOT NULL AND date_started <= '" + reportingDateEnd + "'";
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    long patientId = resultSet.getLong("patient_id");
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");

                    int[] pregnant = {0};
                    int[] breastfeeding = {0};
                    jdbcTemplate.query(getLastRefillVisit(patientId), rs -> {
                        while (rs != null && rs.next()) {
                            Date dateLastRefill = rs.getDate("date_visit");
                            int duration = rs.getInt("duration");
                            int monthRefill = duration / 30;
                            if (monthRefill <= 0) {
                                monthRefill = 1;
                            }

                            if (dateLastRefill != null) {
                                //If the last refill date plus refill duration plus 90 days is before the last day of the reporting month this patient is LTFU     if(DateUtil.addYearMonthDay(lastRefill, duration+90, "day(s)").before(reportingDateEnd)) 

                                if (!DateUtil.addYearMonthDay(dateLastRefill, duration + Constants.Reports.LTFU_PEPFAR, "DAY").before(DateUtil.getLastDateOfMonth(reportingYear, reportingMonth))) {
                                    disaggregate(gender, age);

                                    long regimentypeId = rs.getLong("regimentype_id");
                                    if (gender.trim().equalsIgnoreCase("Male")) {
                                        if (age < 15) {
                                            if (regimentypeId == 1 || regimentypeId == 3) {
                                                agem13_1++;
                                            } else {
                                                if (regimentypeId == 2 || regimentypeId == 4) {
                                                    agem14_1++;
                                                } else {
                                                    agem15_1++;
                                                }
                                            }
                                        } else {
                                            if (regimentypeId == 1 || regimentypeId == 3) {
                                                agem13_2++;
                                            } else {
                                                if (regimentypeId == 2 || regimentypeId == 4) {
                                                    agem14_2++;
                                                } else {
                                                    agem15_2++;
                                                }
                                            }
                                        }
                                    } else {
                                        if (age < 15) {
                                            if (regimentypeId == 1 || regimentypeId == 3) {
                                                agef13_1++;
                                            } else {
                                                if (regimentypeId == 2 || regimentypeId == 4) {
                                                    agef14_1++;
                                                } else {
                                                    agef15_1++;
                                                }
                                            }
                                        } else {
                                            if (regimentypeId == 1 || regimentypeId == 3) {
                                                agef13_2++;
                                            } else {
                                                if (regimentypeId == 2 || regimentypeId == 4) {
                                                    agef14_2++;
                                                } else {
                                                    agef15_2++;
                                                }
                                            }
                                        }
                                    }

                                    if (gender.trim().equalsIgnoreCase("Female")) {
                                        //check if client is pregnant or breast feeding during enrolment
                                        query = "SELECT pregnant, breastfeeding FROM preg WHERE patient_id = " + patientId + " ORDER BY date_visit DESC LIMIT 1";
                                        jdbcTemplate.query(query, rs1 -> {
                                            if (rs1.next()) {
                                                pregnant[0] = rs1.getInt("pregnant");
                                                breastfeeding[0] = rs1.getInt("breastfeeding");
                                            }
                                            return null;
                                        });

                                        if (pregnant[0] == 1) {
                                            preg++;
                                        } else {
                                            if (breastfeeding[0] == 1) {
                                                feeding++;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        return null;
                    });
                }
                return null;
            });
            //Populate the report parameter map with values computed for ART 4
            parameterMap.put("art4m1", Integer.toString(agem1));
            parameterMap.put("art4f1", Integer.toString(agef1));
            parameterMap.put("art4t1", Integer.toString(agem1 + agef1));

            parameterMap.put("art4m2", Integer.toString(agem2));
            parameterMap.put("art4f2", Integer.toString(agef2));
            parameterMap.put("art4t2", Integer.toString(agem2 + agef2));

            parameterMap.put("art4m3", Integer.toString(agem3));
            parameterMap.put("art4f3", Integer.toString(agef3));
            parameterMap.put("art4t3", Integer.toString(agem3 + agef3));

            parameterMap.put("art4m4", Integer.toString(agem4));
            parameterMap.put("art4f4", Integer.toString(agef4));
            parameterMap.put("art4t4", Integer.toString(agem4 + agef4));

            parameterMap.put("art4m5", Integer.toString(agem5));
            parameterMap.put("art4f5", Integer.toString(agef5));
            parameterMap.put("art4t5", Integer.toString(agem5 + agef5));

            parameterMap.put("art4m6", Integer.toString(agem6));
            parameterMap.put("art4f6", Integer.toString(agef6));
            parameterMap.put("art4t6", Integer.toString(agem6 + agef6));

            parameterMap.put("art4m7", Integer.toString(agem7));
            parameterMap.put("art4f7", Integer.toString(agef7));
            parameterMap.put("art4t7", Integer.toString(agem7 + agef7));

            parameterMap.put("art4m8", Integer.toString(agem8));
            parameterMap.put("art4f8", Integer.toString(agef8));
            parameterMap.put("art4t8", Integer.toString(agem8 + agef8));

            parameterMap.put("art4m9", Integer.toString(agem9));
            parameterMap.put("art4f9", Integer.toString(agef9));
            parameterMap.put("art4t9", Integer.toString(agem9 + agef9));

            parameterMap.put("art4m10", Integer.toString(agem10));
            parameterMap.put("art4f10", Integer.toString(agef10));
            parameterMap.put("art4t10", Integer.toString(agem10 + agef10));

            parameterMap.put("art4m11", Integer.toString(agem11));
            parameterMap.put("art4f11", Integer.toString(agef11));
            parameterMap.put("art4t11", Integer.toString(agem11 + agef11));

            parameterMap.put("art4m12", Integer.toString(agem12));
            parameterMap.put("art4f12", Integer.toString(agef12));
            parameterMap.put("art4t12", Integer.toString(agem12 + agef12));

            parameterMap.put("art4m13_1", Integer.toString(agem13_1));
            parameterMap.put("art4f13_1", Integer.toString(agef13_1));
            parameterMap.put("art4m13_2", Integer.toString(agem13_2));
            parameterMap.put("art4f13_2", Integer.toString(agef13_2));
            parameterMap.put("art4t13", Integer.toString(agem13_1 + agef13_1 + agem13_2 + agef13_2));

            parameterMap.put("art4m14_1", Integer.toString(agem14_1));
            parameterMap.put("art4f14_1", Integer.toString(agef14_1));
            parameterMap.put("art4m14_2", Integer.toString(agem14_2));
            parameterMap.put("art4f14_2", Integer.toString(agef14_2));
            parameterMap.put("art4t14", Integer.toString(agem14_1 + agef14_1 + agem14_2 + agef14_2));

            parameterMap.put("art4m15_1", Integer.toString(agem15_1));
            parameterMap.put("art4f15_1", Integer.toString(agef15_1));
            parameterMap.put("art4m15_2", Integer.toString(agem15_2));
            parameterMap.put("art4f15_2", Integer.toString(agef15_2));
            parameterMap.put("art4t15", Integer.toString(agem15_1 + agef15_1 + agem15_2 + agef15_2));

            parameterMap.put("art4f16", Integer.toString(preg));
            parameterMap.put("art4f17", Integer.toString(feeding));

            parameterMap.put("art4t16", Integer.toString(preg));
            parameterMap.put("art4t17", Integer.toString(feeding));

            System.out.println("Computing ART5.....");
            //ART 5
            //Number of people living with HIV and on ART with a viral load test result during the month
            initVariables();

            executeUpdate("DROP INDEX IF EXISTS idx_lab");
            executeUpdate("DROP TABLE IF EXISTS lab");
            executeUpdate("CREATE TEMPORARY TABLE lab AS SELECT * FROM laboratory WHERE facility_id = " + facilityId + " AND YEAR(date_reported) = " + reportingYear + " AND MONTH(date_reported) = " + reportingMonth + " AND labtest_id = 16");
            executeUpdate("CREATE INDEX idx_lab ON lab(patient_id)");

            query = "SELECT DISTINCT patient_id, gender, DATEDIFF(YEAR, date_birth, '" + reportingDateBegin + "') AS age FROM patient WHERE facility_id = " + facilityId + " AND date_registration <= '" + reportingDateEnd + "' AND date_started IS NOT NULL AND DATEDIFF(MONTH, date_started, '" + reportingDateEnd + "') >= 6 AND date_started <= '" + reportingDateEnd + "'";
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    long patientId = resultSet.getLong("patient_id");
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");

                    //Check for viral load this reporting month
                    query = "SELECT patient_id FROM lab WHERE patient_id = " + patientId + " ORDER BY date_reported DESC LIMIT 1";
                    jdbcTemplate.query(query, rs -> {
                        while (rs.next()) {
                            disaggregate(gender, age);
                        }
                        return null;
                    });
                }
                return null;
            });
            //Populate the report parameter map with values computed for ART 5
            parameterMap.put("art5m1", Integer.toString(agem1));
            parameterMap.put("art5f1", Integer.toString(agef1));
            parameterMap.put("art5t1", Integer.toString(agem1 + agef1));

            parameterMap.put("art5m2", Integer.toString(agem2));
            parameterMap.put("art5f2", Integer.toString(agef2));
            parameterMap.put("art5t2", Integer.toString(agem2 + agef2));

            parameterMap.put("art5m3", Integer.toString(agem3));
            parameterMap.put("art5f3", Integer.toString(agef3));
            parameterMap.put("art5t3", Integer.toString(agem3 + agef3));

            parameterMap.put("art5m4", Integer.toString(agem4));
            parameterMap.put("art5f4", Integer.toString(agef4));
            parameterMap.put("art5t4", Integer.toString(agem4 + agef4));

            parameterMap.put("art5m5", Integer.toString(agem5));
            parameterMap.put("art5f5", Integer.toString(agef5));
            parameterMap.put("art5t5", Integer.toString(agem5 + agef5));

            parameterMap.put("art5m6", Integer.toString(agem6));
            parameterMap.put("art5f6", Integer.toString(agef6));
            parameterMap.put("art5t6", Integer.toString(agem6 + agef6));

            parameterMap.put("art5m7", Integer.toString(agem7));
            parameterMap.put("art5f7", Integer.toString(agef7));
            parameterMap.put("art5t7", Integer.toString(agem7 + agef7));

            parameterMap.put("art5m8", Integer.toString(agem8));
            parameterMap.put("art5f8", Integer.toString(agef8));
            parameterMap.put("art5t8", Integer.toString(agem8 + agef8));

            parameterMap.put("art5m9", Integer.toString(agem9));
            parameterMap.put("art5f9", Integer.toString(agef9));
            parameterMap.put("art5t9", Integer.toString(agem9 + agef9));

            parameterMap.put("art5m10", Integer.toString(agem10));
            parameterMap.put("art5f10", Integer.toString(agef10));
            parameterMap.put("art5t10", Integer.toString(agem10 + agef10));

            parameterMap.put("art5m11", Integer.toString(agem11));
            parameterMap.put("art5f11", Integer.toString(agef11));
            parameterMap.put("art5t11", Integer.toString(agem11 + agef11));

            parameterMap.put("art5m12", Integer.toString(agem12));
            parameterMap.put("art5f12", Integer.toString(agef12));
            parameterMap.put("art5t12", Integer.toString(agem12 + agef12));

            System.out.println("Computing ART6.....");
            //ART 6
            //Number of people living with HIV and on ART who are virologically suppressed (viral load < 1000 c/ml) during the month
            initVariables();

            executeUpdate("DROP INDEX IF EXISTS idx_lab");
            executeUpdate("DROP TABLE IF EXISTS lab");
            executeUpdate("CREATE TEMPORARY TABLE lab AS SELECT * FROM laboratory WHERE facility_id = " + facilityId + " AND YEAR(date_reported) = " + reportingYear + " AND MONTH(date_reported) = " + reportingMonth + " AND labtest_id = 16");
            executeUpdate("CREATE INDEX idx_lab ON lab(patient_id)");

            query = "SELECT DISTINCT patient_id, gender, DATEDIFF(YEAR, date_birth, '" + reportingDateBegin + "') AS age FROM patient WHERE facility_id = " + facilityId + " AND date_registration <= '" + reportingDateEnd + "' AND date_started IS NOT NULL AND DATEDIFF(MONTH, date_started, '" + reportingDateEnd + "') >= 6 AND date_started <= '" + reportingDateEnd + "'";
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    long patientId = resultSet.getLong("patient_id");
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");

                    //Check if the last viral load before the reporting month is less than 1000
                    query = "SELECT * FROM lab WHERE patient_id = " + patientId + " ORDER BY date_reported DESC LIMIT 1";
                    jdbcTemplate.query(query, rs -> {
                        while (rs.next()) {
                            String resultab = rs.getString("resultab");
                            if (isInteger(resultab)) {
                                if (Double.valueOf(resultab) < 1000) {
                                    disaggregate(gender, age);
                                }
                            }
                        }
                        return null;
                    });
                }
                return null;
            });
            //Populate the report parameter map with values computed for ART 6
            parameterMap.put("art6m1", Integer.toString(agem1));
            parameterMap.put("art6f1", Integer.toString(agef1));
            parameterMap.put("art6t1", Integer.toString(agem1 + agef1));

            parameterMap.put("art6m2", Integer.toString(agem2));
            parameterMap.put("art6f2", Integer.toString(agef2));
            parameterMap.put("art6t2", Integer.toString(agem2 + agef2));

            parameterMap.put("art6m3", Integer.toString(agem3));
            parameterMap.put("art6f3", Integer.toString(agef3));
            parameterMap.put("art6t3", Integer.toString(agem3 + agef3));

            parameterMap.put("art6m4", Integer.toString(agem4));
            parameterMap.put("art6f4", Integer.toString(agef4));
            parameterMap.put("art6t4", Integer.toString(agem4 + agef4));

            parameterMap.put("art6m5", Integer.toString(agem5));
            parameterMap.put("art6f5", Integer.toString(agef5));
            parameterMap.put("art6t5", Integer.toString(agem5 + agef5));

            parameterMap.put("art6m6", Integer.toString(agem6));
            parameterMap.put("art6f6", Integer.toString(agef6));
            parameterMap.put("art6t6", Integer.toString(agem6 + agef6));

            parameterMap.put("art6m7", Integer.toString(agem7));
            parameterMap.put("art6f7", Integer.toString(agef7));
            parameterMap.put("art6t7", Integer.toString(agem7 + agef7));

            parameterMap.put("art6m8", Integer.toString(agem8));
            parameterMap.put("art6f8", Integer.toString(agef8));
            parameterMap.put("art6t8", Integer.toString(agem8 + agef8));

            parameterMap.put("art6m9", Integer.toString(agem9));
            parameterMap.put("art6f9", Integer.toString(agef9));
            parameterMap.put("art6t9", Integer.toString(agem9 + agef9));

            parameterMap.put("art6m10", Integer.toString(agem10));
            parameterMap.put("art6f10", Integer.toString(agef10));
            parameterMap.put("art6t10", Integer.toString(agem10 + agef10));

            parameterMap.put("art6m11", Integer.toString(agem11));
            parameterMap.put("art6f11", Integer.toString(agef11));
            parameterMap.put("art6t11", Integer.toString(agem11 + agef11));

            parameterMap.put("art6m12", Integer.toString(agem12));
            parameterMap.put("art6f12", Integer.toString(agef12));
            parameterMap.put("art6t12", Integer.toString(agem12 + agef12));

            System.out.println("Computing ART7.....");
            //ART 7
            //Total number of people living with HIV known to have died during the month
            initVariables();
            query = "SELECT DISTINCT gender, DATEDIFF(YEAR, date_birth, '" + reportingDateBegin + "') AS age FROM patient WHERE facility_id = " + facilityId + " AND current_status = 'Known Death' AND YEAR(date_current_status) = " + reportingYear + " AND MONTH(date_current_status) = " + reportingMonth;
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");
                    if (gender.trim().equalsIgnoreCase("Male")) {
                        agem1++;
                    } else {
                        agef1++;
                    }
                }
                return null;
            });
            parameterMap.put("art7m1", Integer.toString(agem1));
            parameterMap.put("art7f1", Integer.toString(agef1));
            parameterMap.put("art7t1", Integer.toString(agem1 + agef1));

            System.out.println("Computing ART8.....");
            //ART 8
            //Number of People living with HIV who are lost to follow up during the month
            initVariables();
            query = "SELECT DISTINCT gender, DATEDIFF(YEAR, date_birth, '" + reportingDateBegin + "') AS age FROM patient WHERE patient_id IN (SELECT DISTINCT patient_id FROM statushistory WHERE facility_id = " + facilityId + " AND current_status = 'Lost to Follow Up' AND YEAR(date_current_status) = " + reportingYear + " AND MONTH(date_current_status) = " + reportingMonth + ")";
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");
                    if (gender.trim().equalsIgnoreCase("Male")) {
                        agem1++;
                    } else {
                        agef1++;
                    }
                }
                return null;
            });
            parameterMap.put("art8m1", Integer.toString(agem1));
            parameterMap.put("art8f1", Integer.toString(agef1));
            parameterMap.put("art8t1", Integer.toString(agem1 + agef1));

            System.out.println("Adding headers.....");
            //Include reproting period & facility details into report header 
            parameterMap.put("reportingMonth", request.getParameter("reportingMonth"));
            parameterMap.put("reportingYear", request.getParameter("reportingYear"));

            query = "SELECT DISTINCT facility.name, facility.address1, facility.address2, facility.phone1, facility.phone2, facility.email, lga.name AS lga, state.name AS state FROM facility JOIN lga ON facility.lga_id = lga.lga_id JOIN state ON facility.state_id = state.state_id WHERE facility_id = " + facilityId;
            jdbcTemplate.query(query, rs -> {
                while (rs.next()) {
                    parameterMap.put("facilityName", rs.getString("name"));
                    parameterMap.put("facilityType", "");
                    parameterMap.put("lga", rs.getString("lga"));
                    parameterMap.put("state", rs.getString("state"));
                    parameterMap.put("level", "");
                }
                return null;
            });
        } catch (Exception exception) {

        }
        return parameterMap;
    }

    private void disaggregate(String gender, int age) {
        if (gender.trim().equalsIgnoreCase("Male")) {
            if (age < 1) {
                agem1++;
            } else {
                if (age >= 1 && age <= 4) {
                    agem2++;
                } else {
                    if (age >= 5 && age <= 9) {
                        agem3++;
                    } else {
                        if (age >= 10 && age <= 14) {
                            agem4++;
                        } else {
                            if (age >= 15 && age <= 19) {
                                agem5++;
                            } else {
                                if (age >= 20 && age <= 24) {
                                    agem6++;
                                } else {
                                    if (age >= 25 && age <= 29) {
                                        agem7++;
                                    } else {
                                        if (age >= 30 && age <= 34) {
                                            agem8++;
                                        } else {
                                            if (age >= 35 && age <= 39) {
                                                agem9++;
                                            } else {
                                                if (age >= 40 && age <= 44) {
                                                    agem10++;
                                                } else {
                                                    if (age >= 45 && age <= 49) {
                                                        agem11++;
                                                    } else {
                                                        if (age >= 50) {
                                                            agem12++;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if (age < 1) {
                agef1++;
            } else {
                if (age >= 1 && age <= 4) {
                    agef2++;
                } else {
                    if (age >= 5 && age <= 9) {
                        agef3++;
                    } else {
                        if (age >= 10 && age <= 14) {
                            agef4++;
                        } else {
                            if (age >= 15 && age <= 19) {
                                agef5++;
                            } else {
                                if (age >= 20 && age <= 24) {
                                    agef6++;
                                } else {
                                    if (age >= 25 && age <= 29) {
                                        agef7++;
                                    } else {
                                        if (age >= 30 && age <= 34) {
                                            agef8++;
                                        } else {
                                            if (age >= 35 && age <= 39) {
                                                agef9++;
                                            } else {
                                                if (age >= 40 && age <= 44) {
                                                    agef10++;
                                                } else {
                                                    if (age >= 45 && age <= 49) {
                                                        agef11++;
                                                    } else {
                                                        if (age >= 50) {
                                                            agef12++;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void executeUpdate(String query) {
        try {
            jdbcTemplate.execute(query);
        } catch (Exception exception) {

        }
    }

    private boolean found(String query) {
        boolean[] found = {false};
        try {
            jdbcTemplate.query(query, rs -> {
                while (rs.next()) {
                    found[0] = true;
                }
                return null;
            });
        } catch (Exception exception) {
            
        }
        return found[0];
    }

    private String getLastRefillVisit(long patientId) {
        return "SELECT DISTINCT regimentype_id, regimen_id, date_visit, duration FROM pharm WHERE patient_id = " + patientId + " ORDER BY date_visit DESC LIMIT 1";
    }

    private void initVariables() {
        agem1 = 0;
        agem2 = 0;
        agem3 = 0;
        agem4 = 0;
        agem5 = 0;
        agem6 = 0;
        agem7 = 0;
        agem8 = 0;
        agem9 = 0;
        agem10 = 0;
        agem11 = 0;
        agem12 = 0;

        agef1 = 0;
        agef2 = 0;
        agef3 = 0;
        agef4 = 0;
        agef5 = 0;
        agef6 = 0;
        agef7 = 0;
        agef8 = 0;
        agef9 = 0;
        agef10 = 0;
        agef11 = 0;
        agef12 = 0;

        agem13_1 = 0;
        agem14_1 = 0;
        agem15_1 = 0;
        agem13_2 = 0;
        agem14_2 = 0;
        agem15_2 = 0;
        agef13_1 = 0;
        agef14_1 = 0;
        agef15_1 = 0;
        agef13_2 = 0;
        agef14_2 = 0;
        agef15_2 = 0;
        preg = 0;
        feeding = 0;
        tbm = 0;
        tbf = 0;
    }

}
