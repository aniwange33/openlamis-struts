/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.exchange.radet;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.dao.jdbc.RegimenIntrospector;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.Constants;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.FileUtil;
import org.fhi360.lamis.utility.StringUtil;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.util.Date;

/**
 * @author user1
 */
public class RadetConverter {

    private Date reportingDateBegin;
    private Date reportingDateEnd;
    private long facilityId;
    private String facilityName;

    private String query;
    private HttpServletRequest request;
    private HttpSession session;
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private String tpt = "";
    private Date dateTpt = null;
    private String regimentype = "";
    private String regimen = "";
    private boolean pregnant = false;
    private boolean breastfeeding = false;
    private String viralLoad = "";
    private Date dateOfViralLoad = null;
    private boolean unsuppressed = false;
    private String comment = "";
    private Date dateLastRefill = null;
    private int duration = 0;
    private int monthRefill = 0;
    private String currentStatus = "";
    private String adherence = "";
    private Date dateAdherence = null;
    private int sessions = 0;
    private String repeatVL = "";
    private Date dateSampleCollected = null;

    public RadetConverter() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();

    }

    public String convertExcel() {
        String fileName = "";

        //DateFormat dateFormatExcel = new SimpleDateFormat("dd-MMM-yyyy");        
        //DateFormat dateFormatExcel = new SimpleDateFormat("dd/MM/yy");        
        String contextPath = ServletActionContext.getServletContext().getInitParameter("contextPath");
        int cohortMonthBegin = DateUtil.getMonth(request.getParameter("cohortMonthBegin"));
        int cohortYearBegin = Integer.parseInt(request.getParameter("cohortYearBegin"));
        int cohortMonthEnd = DateUtil.getMonth(request.getParameter("cohortMonthEnd"));
        int cohortYearEnd = Integer.parseInt(request.getParameter("cohortYearEnd"));
        String cohortDateBegin = DateUtil.parseDateToString(DateUtil.getFirstDateOfMonth(cohortYearBegin, cohortMonthBegin), "yyyy-MM-dd");
        String cohortDateEnd = DateUtil.parseDateToString(DateUtil.getLastDateOfMonth(cohortYearEnd, cohortMonthEnd), "yyyy-MM-dd");

        int reportingMonth = DateUtil.getMonth(request.getParameter("reportingMonth"));
        int reportingYear = Integer.parseInt(request.getParameter("reportingYear"));
        reportingDateEnd = DateUtil.getLastDateOfMonth(reportingYear, reportingMonth);
        reportingDateBegin = DateUtil.getFirstDateOfMonth(reportingYear, reportingMonth);

        facilityId = (Long) session.getAttribute("facilityId");
        facilityName = (String) session.getAttribute("facilityName");

        SXSSFWorkbook workbook = new SXSSFWorkbook(-1);  // turn off auto-flushing and accumulate all rows in memory
        Sheet sheet = workbook.createSheet();

        //Create a new font
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setColor(new HSSFColor.WHITE().getIndex());

        //Create a style and set the font into it
        CellStyle[] style = {workbook.createCellStyle()};
        //style.setFillForegroundColor(new HSSFColor.WHITE().getIndex());
        style[0].setFillBackgroundColor(new HSSFColor.BLUE().getIndex());
        style[0].setFillPattern(HSSFCellStyle.FINE_DOTS);
        style[0].setFont(font);

        int[] rownum = {0};
        int[] cellnum = {0};
        Row[] row = {sheet.createRow(rownum[0]++)};
        Cell[] cell = {row[0].createCell(cellnum[0]++)};
        cell[0].setCellValue("S/No.");
        cell[0].setCellStyle(style[0]);
        cell[0] = row[0].createCell(cellnum[0]++);
        cell[0].setCellValue("Patient Id");
        cell[0].setCellStyle(style[0]);
        cell[0] = row[0].createCell(cellnum[0]++);
        cell[0].setCellValue("Hospital Num");
        cell[0].setCellStyle(style[0]);
        cell[0] = row[0].createCell(cellnum[0]++);
        cell[0].setCellValue("Sex");
        cell[0].setCellStyle(style[0]);
//        cell = row.createCell(cellnum++);
//        cell.setCellValue("Age at Start of ART (Years)");
//        cell.setCellStyle(style);
//        cell = row.createCell(cellnum++);
//        cell.setCellValue("Age at Start of ART (Months) Enter for under 5s");
//        cell.setCellStyle(style);

        cell[0] = row[0].createCell(cellnum[0]++);
        cell[0].setCellValue("Date of Birth");
        cell[0].setCellStyle(style[0]);

        cell[0] = row[0].createCell(cellnum[0]++);
        cell[0].setCellValue("ART Start Date (yyyy-mm-dd");
        cell[0].setCellStyle(style[0]);
        cell[0] = row[0].createCell(cellnum[0]++);
        cell[0].setCellValue("Last Pickup Date (yyyy-mm-dd)");
        cell[0].setCellStyle(style[0]);
        cell[0] = row[0].createCell(cellnum[0]++);
        cell[0].setCellValue("Months of ARV Refill");
        cell[0].setCellStyle(style[0]);

        cell[0] = row[0].createCell(cellnum[0]++);
        cell[0].setCellValue("TPT in the Last 2 years");
        cell[0].setCellStyle(style[0]);
        cell[0] = row[0].createCell(cellnum[0]++);
        cell[0].setCellValue("If Yes to TPT, date of TPT Start");
        cell[0].setCellStyle(style[0]);

        cell[0] = row[0].createCell(cellnum[0]++);
        cell[0].setCellValue("Regimen Line at ART Start");
        cell[0].setCellStyle(style[0]);
        cell[0] = row[0].createCell(cellnum[0]++);
        cell[0].setCellValue("Regimen at ART Start");
        cell[0].setCellStyle(style[0]);
        cell[0] = row[0].createCell(cellnum[0]++);
        cell[0].setCellValue("Current Regimen Line");
        cell[0].setCellStyle(style[0]);
        cell[0] = row[0].createCell(cellnum[0]++);
        cell[0].setCellValue("Current ART Regimen");
        cell[0].setCellStyle(style[0]);
        cell[0] = row[0].createCell(cellnum[0]++);
        cell[0].setCellValue("Pregnancy Status");
        cell[0].setCellStyle(style[0]);
        cell[0] = row[0].createCell(cellnum[0]++);
        cell[0].setCellValue("Current Viral Load (c/ml)");
        cell[0].setCellStyle(style[0]);
        cell[0] = row[0].createCell(cellnum[0]++);
        cell[0].setCellValue("Date of Current Viral Load (yyyy-mm-dd)");
        cell[0].setCellStyle(style[0]);
        cell[0] = row[0].createCell(cellnum[0]++);
        cell[0].setCellValue("Viral Load Indication");
        cell[0].setCellStyle(style[0]);
        cell[0] = row[0].createCell(cellnum[0]++);
        cell[0].setCellValue("Current ART Status");
        cell[0].setCellStyle(style[0]);
        cell[0] = row[0].createCell(cellnum[0]++);
        cell[0].setCellValue("ART Enrollment Setting");
        cell[0].setCellStyle(style[0]);

        cell[0] = row[0].createCell(cellnum[0]++);
        cell[0].setCellValue("Client Receiving DMOC Service?");
        cell[0].setCellStyle(style[0]);
        cell[0] = row[0].createCell(cellnum[0]++);
        cell[0].setCellValue("Date Commenced DMOC (yyyy-mm-dd)");
        cell[0].setCellStyle(style[0]);
        cell[0] = row[0].createCell(cellnum[0]++);
        cell[0].setCellValue("DMOC of Type");
        cell[0].setCellStyle(style[0]);

        cell[0] = row[0].createCell(cellnum[0]++);
        cell[0].setCellValue("Enhanced Adherence Counselling (EAC) Commenced?");
        cell[0].setCellStyle(style[0]);
        cell[0] = row[0].createCell(cellnum[0]++);
        cell[0].setCellValue("Date of Commencement of EAC (yyyy-mm-dd)");
        cell[0].setCellStyle(style[0]);
        cell[0] = row[0].createCell(cellnum[0]++);
        cell[0].setCellValue("Number of EAC Sessions Completed");
        cell[0].setCellStyle(style[0]);
        cell[0] = row[0].createCell(cellnum[0]++);
        cell[0].setCellValue("Repeat Viral Load - Post EAC VL Sample Collected?");
        cell[0].setCellStyle(style[0]);
        cell[0] = row[0].createCell(cellnum[0]++);
        cell[0].setCellValue("Date of Repeat Viral Load - Post EAC VL Sample Collected");
        cell[0].setCellStyle(style[0]);

        //Create a date format for date columns
        style[0] = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        style[0].setDataFormat(createHelper.createDataFormat().getFormat("yyyy/MM/dd"));
        style[0].setAlignment(CellStyle.ALIGN_RIGHT);

        try {
            executeUpdate("DROP INDEX IF EXISTS idx_visit");
            executeUpdate("DROP TABLE IF EXISTS visit");
            executeUpdate("CREATE TEMPORARY TABLE visit AS SELECT * FROM clinic WHERE facility_id = " + facilityId + " AND date_visit >= DATEADD('MONTH', -9, '" + DateUtil.parseDateToString(reportingDateBegin, "yyyy-MM-dd") + "') AND date_visit <= '" + DateUtil.parseDateToString(reportingDateEnd, "yyyy-MM-dd") + "'");
            executeUpdate("CREATE INDEX idx_visit ON visit(patient_id)");

            executeUpdate("DROP INDEX IF EXISTS idx_commence");
            executeUpdate("DROP TABLE IF EXISTS commence");
            executeUpdate("CREATE TEMPORARY TABLE commence AS SELECT * FROM clinic WHERE facility_id = " + facilityId + " AND commence = 1");
            executeUpdate("CREATE INDEX idx_commence ON commence(patient_id)");

            executeUpdate("DROP INDEX IF EXISTS idx_pharm");
            executeUpdate("DROP TABLE IF EXISTS pharm");
            executeUpdate("CREATE TEMPORARY TABLE pharm AS SELECT * FROM pharmacy WHERE facility_id = " + facilityId + " AND date_visit <= '" + DateUtil.parseDateToString(reportingDateEnd, "yyyy-MM-dd") + "' AND regimentype_id IN (1, 2, 3, 4, 14)");
            executeUpdate("CREATE INDEX idx_pharm ON pharm(patient_id)");

            //Retrieve all viral load test value done on or before the end of the reporting period
            executeUpdate("DROP INDEX IF EXISTS idx_viralload");
            executeUpdate("DROP TABLE IF EXISTS viralload");
            executeUpdate("CREATE TEMPORARY TABLE viralload AS SELECT patient_id, resultab, date_reported, comment FROM laboratory WHERE facility_id = " + facilityId + " AND date_reported <= '" + DateUtil.parseDateToString(reportingDateEnd, "yyyy-MM-dd") + "' AND labtest_id = 16 ORDER BY patient_id");
            executeUpdate("CREATE INDEX idx_viralload ON viralload(patient_id)");
            //executeUpdate("CREATE TEMPORARY TABLE viralload AS SELECT patient_id, resultab, date_reported, comment FROM laboratory WHERE facility_id = " + facilityId + " AND date_reported <= '" + dateFormat.format(reportingDateEnd)  + "' AND resultab REGEXP('(^[0-9]+$)') AND labtest_id = 16 ORDER BY patient_id");

            //Retrieve all isonaized dispensed  on or before the end of the reporting period
            executeUpdate("DROP INDEX IF EXISTS idx_inh");
            executeUpdate("DROP TABLE IF EXISTS inh");
            executeUpdate("CREATE TEMPORARY TABLE inh AS SELECT * FROM pharmacy WHERE facility_id = " + facilityId + " AND date_visit <= '" + DateUtil.parseDateToString(reportingDateEnd, "yyyy-MM-dd") + "' AND regimen_id = 115");
            executeUpdate("CREATE INDEX idx_inh ON inh(patient_id)");

            //Cohort of ART
            query = "SELECT DISTINCT patient_id, hospital_num, unique_id, enrollment_setting, gender, date_birth, date_started FROM patient WHERE facility_id = " + facilityId + " AND date_started >= '" + cohortDateBegin + "' AND date_started <= '" + cohortDateEnd + "'";
            System.out.println(query);
            jdbcTemplate.query(query, resultSet -> {
                int sno = 1;
                while (resultSet.next()) {
                    initVariable();
                    long patientId = resultSet.getLong("patient_id");
                    String uniqueId = resultSet.getString("unique_id") == null ? "" : resultSet.getString("unique_id");
                    String hospitalNum = resultSet.getString("hospital_num");
                    String gender = resultSet.getString("gender");
                    String enrollmentSetting = resultSet.getString("enrollment_setting") == null ? "" : resultSet.getString("enrollment_setting");
                    cellnum[0] = 0;
                    row[0] = sheet.createRow(rownum[0]++); //Create new row                
                    cell[0] = row[0].createCell(cellnum[0]++); //Create a cell on the new row
                    cell[0].setCellValue(sno++);
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(uniqueId);
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(hospitalNum);
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(gender);

                    System.out.println("Analysing patent Id" + patientId);

                    Date dateBirth = resultSet.getDate("date_birth");
                    Date dateStarted = resultSet.getDate("date_started");
                    int age = DateUtil.yearsBetweenIgnoreDays(dateBirth, dateStarted);
                    cell[0] = row[0].createCell(cellnum[0]++);
                    if (dateBirth != null) {
                        cell[0].setCellValue(dateBirth);
                        cell[0].setCellStyle(style[0]);
                    }
                    cell[0] = row[0].createCell(cellnum[0]++);
                    if (dateStarted != null) {
                        cell[0].setCellValue(dateStarted);
                        cell[0].setCellStyle(style[0]);
                    }

//                Date dateBirth = resultSet.getDate("date_birth");
//                int age = DateUtil.yearsBetweenIgnoreDays(dateBirth, dateStarted); 
//                cell = row.createCell(cellnum++);
//                if(age >= 5) cell.setCellValue(age);          //cell.setCellValue(Integer.toString(age));
//                cell = row.createCell(cellnum++);
//                if(age < 5) {
//                    age = DateUtil.monthsBetweenIgnoreDays(dateBirth, dateStarted); 
//                    cell.setCellValue(age);
//                }
                    //Date of last pickup before the reporting date
                    query = "SELECT pharm.date_visit, pharm.duration, regimentype.description AS regimentype, "
                            + "regimen.description AS regimen FROM pharm JOIN regimentype ON pharm.regimentype_id = "
                            + "regimentype.regimentype_id JOIN regimen ON pharm.regimen_id = regimen.regimen_id WHERE "
                            + "pharm.patient_id = " + patientId + " ORDER BY pharm.date_visit DESC LIMIT 1";
                    jdbcTemplate.query(query, rs -> {
                        dateLastRefill = rs.getDate("date_visit");
                        duration = rs.getInt("duration");
                        monthRefill = duration / 30;
                        if (monthRefill <= 0) {
                            monthRefill = 1;
                        }
                        //regimen at last pickup
                        regimentype = rs.getString("regimentype") == null ? ""
                                : rs.getString("regimentype");
                        if (regimentype.contains("ART First Line Adult")) {
                            regimentype = "Adult.1st.Line";
                        } else {
                            if (regimentype.contains("ART Second Line Adult")) {
                                regimentype = "Adult.2nd.Line";
                            } else {
                                if (regimentype.contains("ART First Line Children")) {
                                    regimentype = "Peds.1st.Line";
                                } else {
                                    if (regimentype.contains("ART Second Line Children")) {
                                        regimentype = "Peds.2nd.Line";
                                    } else {
                                        if (regimentype.contains("Third Line")) {
                                            if (age < 5) {
                                                regimentype = "Peds.3rd.Line";
                                            } else {
                                                regimentype = "Adult.3rd.Line";
                                            }
                                        } else {
                                            regimentype = "";
                                        }
                                    }
                                }
                            }
                        }
                        if (!regimentype.trim().isEmpty()) {
                            regimen = rs.getString("regimen") == null ? ""
                                    : RegimenIntrospector.resolveRegimen(rs.getString("regimen"));
                        }

                    });
                    cell[0] = row[0].createCell(cellnum[0]++);
                    if (dateLastRefill != null) {
                        cell[0].setCellValue(dateLastRefill);  //Set date value
                        cell[0].setCellStyle(style[0]);           //Appply date format
                    }
                    cell[0] = row[0].createCell(cellnum[0]++);
                    if (dateLastRefill != null) {
                        cell[0].setCellValue(monthRefill);
                    }

                    //TPT Start
                    query = "SELECT date_visit FROM inh WHERE patient_id = " + patientId
                            + " AND date_visit >= DATEADD('MONTH', -24, '"
                            + DateUtil.parseDateToString(reportingDateBegin, "yyyy-MM-dd") + "') "
                            + "AND date_visit <= '" + DateUtil.parseDateToString(reportingDateEnd,
                                    "yyyy-MM-dd") + "' ORDER BY date_visit ASC LIMIT 1";
                    jdbcTemplate.query(query, rs -> {
                        tpt = "Yes";
                        dateTpt = rs.getDate("date_visit");
                    });

                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(tpt);

                    cell[0] = row[0].createCell(cellnum[0]++);
                    if (dateTpt != null) {
                        cell[0].setCellValue(dateTpt);  //Set date value
                        cell[0].setCellStyle(style[0]);           //Appply date format
                    }

                    //Regimen at start of ART
                        final String[] regimentypeStart = {""};
                        final String[] regimenStart = {""};
                    query = "SELECT regimentype, regimen FROM commence WHERE patient_id = " + patientId + " LIMIT 1";
                    jdbcTemplate.query(query, rs -> {
                            regimentypeStart[0] = rs.getString("regimentype") == null ? ""
                                    : rs.getString("regimentype");
                            if (regimentypeStart[0].contains("ART First Line Adult")) {
                                regimentypeStart[0] = "Adult.1st.Line";
                            } else {
                                if (regimentypeStart[0].contains("ART Second Line Adult")) {
                                    regimentypeStart[0] = "Adult.2nd.Line";
                                } else {
                                    if (regimentypeStart[0].contains("ART First Line Children")) {
                                        regimentypeStart[0] = "Peds.1st.Line";
                                    } else {
                                        if (regimentypeStart[0].contains("ART Second Line Children")) {
                                            regimentypeStart [0]= "Peds.2nd.Line";
                                        } else {
                                            if (regimentypeStart[0].contains("Third Line")) {
                                                if (age < 5) {
                                                    regimentypeStart[0] = "Peds.3rd.Line";
                                                } else {
                                                    regimentypeStart[0]= "Adult.3rd.Line";
                                                }
                                            } else {
                                                regimentypeStart[0] = "";
                                            }
                                        }
                                    }
                                }
                            }
                            if (!regimentypeStart[0].trim().isEmpty()) {
                                regimenStart[0] = rs.getString("regimen") == null ? ""
                                        : RegimenIntrospector.resolveRegimen(rs.getString("regimen"));
                            }
                    });

                    //Regimen at start of ART
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(regimentypeStart[0]);
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(regimenStart[0]);

                    //Current regimen i.e regimen at last pickup
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(regimentype);
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(regimen);

                    //Pregnancy status as at the last clinic visit before the reporting date                  
                    if (gender.trim().equals("Female")) {
                        query = "SELECT pregnant, breastfeeding FROM visit WHERE patient_id = "
                                + patientId + " ORDER BY date_visit DESC LIMIT 1";
                        jdbcTemplate.query(query, rs -> {
                            if (rs.getInt("pregnant") == 1) {
                                pregnant = true;
                            }
                            if (rs.getInt("breastfeeding") == 1) {
                                breastfeeding = true;
                            }
                        });
                    }

                    cell[0] = row[0].createCell(cellnum[0]++);
                    if (gender.trim().equals("Female")) {
                        if (pregnant) {
                            cell[0].setCellValue("Pregnant");
                        } else {
                            if (breastfeeding) {
                                cell[0].setCellValue("Breastfeeding");
                            } else {
                                cell[0].setCellValue("Not pregnant");
                            }
                        }

                    }

                    //Last viral load test value on or before the end of reporting date                  
                    if (DateUtil.monthsBetweenIgnoreDays(dateStarted, reportingDateEnd) >= 3) {
                        query = "SELECT resultab, date_reported, comment FROM viralload WHERE patient_id = "
                                + patientId + " ORDER BY date_reported DESC LIMIT 1";
                        jdbcTemplate.query(query, rs -> {
                            viralLoad = rs.getString("resultab") == null ? ""
                                    : rs.getString("resultab");
                            dateOfViralLoad = rs.getDate("date_reported");
                            comment = rs.getString("comment") == null ? ""
                                    : rs.getString("comment");
                        });
                    }
                    cell[0] = row[0].createCell(cellnum[0]++);
                    if (!viralLoad.trim().isEmpty()) {
                        if (!StringUtil.isInteger(viralLoad)) {
                            viralLoad = "0.0";
                        }
                        Double value = Double.valueOf(StringUtil.stripCommas(viralLoad));

                        //Determine is this patient is unsuppressed based on this VL test result
                        if (value > 1000) {
                            unsuppressed = true;
                        }
                        cell[0].setCellValue(value.intValue());
                    }
                    cell[0] = row[0].createCell(cellnum[0]++);
                    if (dateOfViralLoad != null) {
                        cell[0].setCellValue(dateOfViralLoad);  //Set date value
                        cell[0].setCellStyle(style[0]);           //Appply date format
                    }
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(comment);

                    //Current status on or before the reporting date
                    query = "SELECT current_status FROM statushistory WHERE facility_id = " + facilityId
                            + " AND patient_id = " + patientId + " AND date_current_status <= '"
                            + DateUtil.parseDateToString(reportingDateEnd, "yyyy-MM-dd")
                            + "' ORDER BY date_current_status DESC LIMIT 1";
                    jdbcTemplate.query(query, rs -> {
                        currentStatus = rs.getString("current_status") == null ? ""
                                : rs.getString("current_status");
                    });

                    if (currentStatus.trim().equalsIgnoreCase("ART Transfer Out")) {
                        currentStatus = "Transferred Out";
                    } else {
                        if (currentStatus.trim().equalsIgnoreCase("Stopped Treatment")) {
                            currentStatus = "Stopped";
                        } else {
                            if (currentStatus.trim().equalsIgnoreCase("Known Death")) {
                                currentStatus = "Dead";
                            } else {
                                if (dateLastRefill != null) {
                                    //If the last refill date plus refill duration plus 28 days (or 30 days) is before the last day of the reporting month this patient is LTFU                                                                         
                                    if (DateUtil.addYearMonthDay(dateLastRefill, duration + Constants.LTFU.PEPFAR, "DAY").before(reportingDateBegin)) {
                                        currentStatus = "LTFU";
                                    } 
                                    else {
                                            currentStatus = currentStatus.trim().equalsIgnoreCase("ART Transfer In")
                                                    ? "Active-Transfer In" : "Active";
                                        }
                                } else {
                                    if (currentStatus.trim().equalsIgnoreCase("Lost to Follow Up")) {
                                        currentStatus = "LTFU";
                                    }
                                    //If no refill visit mark as Active if Transfer In or date started is not more than 28 days ( or 90 days)
                                    if (currentStatus.trim().equalsIgnoreCase("ART Transfer In")) {
                                        currentStatus = "Active-Transfer In";
                                    } else {
                                        currentStatus = "Active";
                                    }
                                }
                            }
                        }
                    }
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(currentStatus);

                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(enrollmentSetting);

                    //Devolvement Information
                    final String[] devolved = {""};
                    final Date[] dateDevolved = {null};
                    final String[] typeDmoc = {""};
                    if (unsuppressed) {
                        devolved[0] = "N/A";
                    } else {
                        query = "SELECT * FROM devolve WHERE patient_id = "
                                + patientId + " ORDER BY date_devolved ASC LIMIT 1";
                        boolean[] found = {false};
                        jdbcTemplate.query(query, rs -> {
                            found[0] = true;
                                devolved[0] = "Yes";
                                dateDevolved[0] = rs.getDate("date_devolved");
                                typeDmoc[0] = rs.getString("type_dmoc");
                        });
                        if (!found[0]) {
                            if (DateUtil.addYearMonthDay(dateStarted, 12, "MONTH")
                                    .before(reportingDateBegin)) {
                                devolved[0] = "N/A";
                            } else {
                                devolved[0] = "No";
                            }
                        }
                    }

                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(devolved[0]);

                    cell[0] = row[0].createCell(cellnum[0]++);
                    if (dateDevolved[0] != null) {
                        cell[0].setCellValue(dateDevolved[0]);  //Set date value
                        cell[0].setCellStyle(style[0]);           //Appply date format
                    }
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(typeDmoc[0]);

                    //Viral Load Monitoring/Enhanced Adherence Counseling
                    if (unsuppressed) {
                        repeatVL = "No";
                        query = "SELECT * FROM eac WHERE patient_id = " + patientId + " ORDER BY date_eac1 DESC LIMIT 1";
                        jdbcTemplate.query(query, rs -> {
                                adherence = "Yes";
                                dateAdherence = rs.getDate("date_eac1");
                                if (rs.getDate("date_eac1") != null) {
                                    sessions = 1;
                                }
                                if (rs.getDate("date_eac2") != null) {
                                    sessions = 2;
                                }
                                if (rs.getDate("date_eac3") != null) {
                                    sessions = 3;
                                }
                                dateSampleCollected = rs.getDate("date_sample_collected");
                                if (dateSampleCollected != null) {
                                    repeatVL = "Yes";
                                }                           
                        });
                    }
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(adherence);

                    cell[0] = row[0].createCell(cellnum[0]++);
                    if (dateAdherence != null) {
                        cell[0].setCellValue(dateAdherence);  //Set date value
                        cell[0].setCellStyle(style[0]);           //Appply date format
                    }
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(sessions);
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(repeatVL);

                    cell[0] = row[0].createCell(cellnum[0]++);
                    if (dateSampleCollected != null) {
                        cell[0].setCellValue(dateSampleCollected);  //Set date value
                        cell[0].setCellStyle(style[0]);           //Appply date format
                    }

                }
                return null;
            });

            String directory = contextPath + "transfer/";

            FileUtil fileUtil = new FileUtil();
            fileUtil.makeDir(directory);
            fileUtil.makeDir(request.getContextPath() + "/transfer/");

            fileName = "RADET_" + facilityName + "_" + Integer.toString(cohortYearBegin) + " to " + Integer.toString(cohortYearEnd) + ".xlsx";
            FileOutputStream outputStream = new FileOutputStream(new File(directory + fileName));
            workbook.write(outputStream);
            outputStream.close();
            workbook.dispose();  // dispose of temporary files backing this workbook on disk

            //for servlets in the default(root) context, copy file to the transfer folder in root 
            if (!contextPath.equalsIgnoreCase(request.getContextPath())) {
                fileUtil.copyFile(fileName, contextPath + "transfer/", request.getContextPath() + "/transfer/");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return "transfer/" + fileName;
    }

    private void initVariable() {
        this.tpt = "No";
        this.dateTpt = null;
        this.regimentype = "";
        this.regimen = "";
        this.pregnant = false;
        this.breastfeeding = false;
        this.viralLoad = "";
        this.dateOfViralLoad = null;
        this.unsuppressed = false;
        this.comment = "";
        this.dateLastRefill = null;
        this.duration = 0;
        this.monthRefill = 0;
        this.currentStatus = "";
        this.adherence = "No";
        this.dateAdherence = null;
        this.sessions = 0;
        this.repeatVL = "";
        this.dateSampleCollected = null;
    }

    private void executeUpdate(String query) {
        try {
            jdbcTemplate.execute(query);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}

//    if(dateLastRefill != null) {
//        //If the last refill date plus refill duration plus 90 days is before the last day of the reporting month this patient is LTFU     if(DateUtil.addYearMonthDay(lastRefill, duration+90, "day(s)").before(reportingDateEnd)) 
//        if(!DateUtil.addYearMonthDay(dateLastRefill, monthRefill+1, "MONTH").after(reportingDateBegin)) {
//            currentStatus = "LTFU";
//        }
//        else {
//            if(currentStatus.trim().equalsIgnoreCase("Lost to Follow Up")) {
//                currentStatus = "LTFU";
//            }
//            else {
//                currentStatus = currentStatus.trim().equalsIgnoreCase("ART Transfer In") ? "Active-Transfer In" : "Active";                                            
//            }
//        }                    
//    }
