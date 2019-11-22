/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.converter;

import org.fhi360.lamis.utility.FileUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.model.Regimenhistory;
import org.fhi360.lamis.dao.jdbc.RegimenIntrospector;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.Scrambler;
import org.apache.commons.lang3.StringUtils;
import org.fhi360.lamis.exchange.radet.RegimenResolver;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class PatientDataConverter {

    private HttpServletRequest request;
    private HttpSession session;
    private ResultSet resultSet;
    private long userId;
    private Boolean viewIdentifier = false;
    private Scrambler scrambler;
    private RegimenResolver regimenResolver;

    private String regimentype1, regimentype2, regimen1, regimen2;
    private long facilityId, patientId;
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    public PatientDataConverter() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
        this.scrambler = new Scrambler();
        this.regimenResolver = new RegimenResolver();
        if (ServletActionContext.getRequest().getParameter("viewIdentifier") != null && !ServletActionContext.getRequest().getParameter("viewIdentifier").equals("false")) {
            this.viewIdentifier = true;
        }
    }

    public synchronized String convertExcel() {
        String fileName = "";
        DateFormat dateFormatExcel = new SimpleDateFormat("dd-MMM-yyyy");
        String contextPath = ServletActionContext.getServletContext().getInitParameter("contextPath");
        userId = (Long) session.getAttribute("userId");

        SXSSFWorkbook workbook = new SXSSFWorkbook(-1);  // turn off auto-flushing and accumulate all rows in memory
        workbook.setCompressTempFiles(true); // temp files will be gzipped
        Sheet sheet = workbook.createSheet();
        try {

            int[] rownum = {0};
            int[] cellnum = {0};
            Row[] row = {sheet.createRow(rownum[0]++)};
            Cell[] cell = {row[0].createCell(cellnum[0]++)};
            cell[0].setCellValue("Facility Id");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Facility Name");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("State");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("LGA");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Patient Id");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Hospital Num");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Unique ID");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Surname");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Other Names");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date Birth");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Age");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Age Unit");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Gender");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Marital Status");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Education");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Occupation");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("State of Residence");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Lga of Residence");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Address");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Phone");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Care Entry Point");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date of Confirmed HIV Test");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date Registration");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Status at Registration");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Current Status");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date Current Status");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("ART Start Date");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Baseline CD4");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Baseline CD4p");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Systolic BP");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Diastolic BP");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Baseline Clinic Stage");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Baseline Functional Status");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Baseline Weight (kg)");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Baseline Height (cm)");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("First Regimen Line");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("First Regimen");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("First NRTI");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("First NNRTI");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Current Regimen Line");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Current Regimen");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Current NRTI");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Current NNRTI");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date Subsituted/Switched");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date of Last Refill");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Last Refill Duration (days)");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date of Next Refill");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Last Clinic Stage");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date of Last Clinic");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date of Next Clinic");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Last CD4");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Last CD4p");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date of Last CD4");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Last Viral Load");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date of Last Viral Load");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Viral Load Due Date");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Viral Load Type");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("SMS consent");

            String query = "";
            //Check if the url has a parameter for entity;
            if (request.getParameterMap().containsKey("entity")) {
                query = getQueryNotification();
            } else if (request.getParameterMap().containsKey("casemanagerId")) {
                query = getQueryCaseManagement();
            } else {
                query = getQuery();
            }
            jdbcTemplate.query(query, resultSet -> {
                facilityId = 0;
                patientId = 0;
                while (resultSet.next()) {
                    regimentype1 = "";
                    regimentype2 = "";
                    regimen1 = "";
                    regimen2 = "";
                    if (resultSet.getLong("facility_id") != facilityId) {
                        executeUpdate("DROP TABLE IF EXISTS commence");
                        executeUpdate("CREATE TEMPORARY TABLE commence AS SELECT * FROM clinic WHERE facility_id = " + resultSet.getLong("facility_id") + " AND commence = 1");
                        executeUpdate("CREATE INDEX idx_visit ON commence(patient_id)");
                    }
                    if (resultSet.getLong("facility_id") != facilityId || resultSet.getLong("patient_id") != patientId) {
                        cellnum[0] = 0;
                        row[0] = sheet.createRow(rownum[0]++);
                        cell[0] = row[0].createCell(cellnum[0]++);
                        cell[0].setCellValue(resultSet.getLong("facility_id"));
                        facilityId = resultSet.getLong("facility_id");
                        Map facility = getFacility(facilityId);
                        cell[0] = row[0].createCell(cellnum[0]++);
                        cell[0].setCellValue((String) facility.get("facilityName"));
                        cell[0] = row[0].createCell(cellnum[0]++);
                        cell[0].setCellValue((String) facility.get("state"));
                        cell[0] = row[0].createCell(cellnum[0]++);
                        cell[0].setCellValue((String) facility.get("lga"));
                        cell[0] = row[0].createCell(cellnum[0]++);
                        cell[0].setCellValue(resultSet.getLong("patient_id"));
                        patientId = resultSet.getLong("patient_id");
                        cell[0] = row[0].createCell(cellnum[0]++);
                        cell[0].setCellValue(resultSet.getString("hospital_num"));
                        cell[0] = row[0].createCell(cellnum[0]++);
                        cell[0].setCellValue(resultSet.getString("unique_id"));

                        System.out.println("Hospital: " + facility.get("facilityName"));
                        System.out.println("Patient No: " + patientId);
                    }
                    cell[0] = row[0].createCell(cellnum[0]++);
                    String surname = resultSet.getObject("surname") == null ? "" : resultSet.getString("surname");
                    surname = (viewIdentifier) ? scrambler.unscrambleCharacters(surname) : surname;
                    surname = StringUtils.upperCase(surname);
                    cell[0].setCellValue(surname);
                    cell[0] = row[0].createCell(cellnum[0]++);
                    String otherNames = resultSet.getObject("other_names") == null ? "" : resultSet.getString("other_names");
                    otherNames = (viewIdentifier) ? scrambler.unscrambleCharacters(otherNames) : otherNames;
                    otherNames = StringUtils.capitalize(otherNames);
                    cell[0].setCellValue(otherNames);
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((resultSet.getDate("date_birth") == null) ? "" : dateFormatExcel.format(resultSet.getDate("date_birth")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(Integer.toString(resultSet.getInt("age")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("age_unit"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("gender"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("marital_status"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("education"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("occupation"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("state"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("lga"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    String address = resultSet.getString("address") == null ? "" : resultSet.getString("address");
                    address = (viewIdentifier) ? scrambler.unscrambleCharacters(address) : address;
                    address = StringUtils.capitalize(address);
                    cell[0].setCellValue(address);
                    cell[0] = row[0].createCell(cellnum[0]++);
                    String phone = resultSet.getString("phone") == null ? "" : resultSet.getString("phone");
                    phone = (viewIdentifier) ? scrambler.unscrambleNumbers(phone) : phone;
                    cell[0].setCellValue(phone);
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("entry_point"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("date_confirmed_hiv"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((resultSet.getDate("date_registration") == null) ? "" : dateFormatExcel.format(resultSet.getDate("date_registration")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("status_registration"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("current_status"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((resultSet.getDate("date_current_status") == null) ? "" : dateFormatExcel.format(resultSet.getDate("date_current_status")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((resultSet.getDate("date_started") == null) ? "" : dateFormatExcel.format(resultSet.getDate("date_started")));

                    //Adding baseline data
                    String q = "SELECT * FROM commence WHERE patient_id = " + patientId + " LIMIT 1";
                    boolean[] found = {false};
                    jdbcTemplate.query(q, rs -> {
                            found[0] = true;
                            cell[0] = row[0].createCell(cellnum[0]++);
                            cell[0].setCellValue(Double.toString(rs.getDouble("cd4")));
                            cell[0] = row[0].createCell(cellnum[0]++);
                            cell[0].setCellValue(Double.toString(rs.getDouble("cd4p")));
                            //Solve the BP
                            String[] bpData = (!"".equals(rs.getString("bp")) && rs.getString("bp") != null) ? rs.getString("bp").split("/") : new String[]{};
                            if (bpData.length == 2) {
                                cell[0] = row[0].createCell(cellnum[0]++);
                                cell[0].setCellValue(bpData[0] != "" ? bpData[0] : "");
                                cell[0] = row[0].createCell(cellnum[0]++);
                                cell[0].setCellValue(bpData[1] != "" ? bpData[1] : "");
                            } else {
                                cell[0] = row[0].createCell(cellnum[0]++);
                                cell[0].setCellValue("");
                                cell[0] = row[0].createCell(cellnum[0]++);
                                cell[0].setCellValue("");
                            }
                            cell[0] = row[0].createCell(cellnum[0]++);
                            cell[0].setCellValue(rs.getString("clinic_stage"));
                            cell[0] = row[0].createCell(cellnum[0]++);
                            cell[0].setCellValue(rs.getString("func_status"));
                            cell[0] = row[0].createCell(cellnum[0]++);
                            cell[0].setCellValue(Double.toString(rs.getDouble("body_weight")));
                            cell[0] = row[0].createCell(cellnum[0]++);
                            cell[0].setCellValue(Double.toString(rs.getDouble("height")));
                            cell[0] = row[0].createCell(cellnum[0]++);
                            cell[0].setCellValue(rs.getString("regimentype") == null ? "" : rs.getString("regimentype"));
                            cell[0] = row[0].createCell(cellnum[0]++);
                            cell[0].setCellValue(rs.getString("regimen") == null ? "" : regimenResolver.getRegimen(rs.getString("regimen")));
                            cell[0] = row[0].createCell(cellnum[0]++);
                            String nrti = rs.getString("regimen") == null ? "" : getNrti(rs.getString("regimen"));
                            cell[0].setCellValue(nrti);
                            cell[0] = row[0].createCell(cellnum[0]++);
                            String nnrti = rs.getString("regimen") == null ? "" : getNnrti(rs.getString("regimen"));
                            cell[0].setCellValue(nnrti);
                            regimentype1 = rs.getString("regimentype") == null ? "" : rs.getString("regimentype");
                            regimen1 = rs.getString("regimen") == null ? "" : rs.getString("regimen");
                    });

                    if (!found[0]) {
                        cell[0] = row[0].createCell(cellnum[0]++);
                        cell[0].setCellValue("");
                        cell[0] = row[0].createCell(cellnum[0]++);
                        cell[0].setCellValue("");
                        cell[0] = row[0].createCell(cellnum[0]++);
                        cell[0].setCellValue("");
                        cell[0] = row[0].createCell(cellnum[0]++);
                        cell[0].setCellValue("");
                        cell[0] = row[0].createCell(cellnum[0]++);
                        cell[0].setCellValue("");
                        cell[0] = row[0].createCell(cellnum[0]++);
                        cell[0].setCellValue("");
                        cell[0] = row[0].createCell(cellnum[0]++);
                        cell[0].setCellValue("");
                        cell[0] = row[0].createCell(cellnum[0]++);
                        cell[0].setCellValue("");
                        cell[0] = row[0].createCell(cellnum[0]++);
                        cell[0].setCellValue("");
                        cell[0] = row[0].createCell(cellnum[0]++);
                        cell[0].setCellValue("");
                        cell[0] = row[0].createCell(cellnum[0]++);
                        cell[0].setCellValue("");
                        cell[0] = row[0].createCell(cellnum[0]++);
                        cell[0].setCellValue("");
                    }
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("regimentype") == null ? "" : resultSet.getString("regimentype"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("regimen") == null ? "" : regimenResolver.getRegimen(resultSet.getString("regimen")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    String nrti = resultSet.getString("regimen") == null ? "" : getNrti(resultSet.getString("regimen"));
                    cell[0].setCellValue(nrti);
                    cell[0] = row[0].createCell(cellnum[0]++);
                    String nnrti = resultSet.getString("regimen") == null ? "" : getNnrti(resultSet.getString("regimen"));
                    cell[0].setCellValue(nnrti);

                    //Determining when regimen was subsituted or switched
                    cell[0] = row[0].createCell(cellnum[0]++);
                    regimentype2 = resultSet.getString("regimentype") == null ? "" : resultSet.getString("regimentype");
                    regimen2 = resultSet.getString("regimen") == null ? "" : resultSet.getString("regimen");
                    if (!regimentype1.isEmpty() && !regimentype2.isEmpty() && !regimen1.isEmpty() && !regimen2.isEmpty()) {
                        //If regimen was substituted or swicted, get the date of change
                        if (RegimenIntrospector.substitutedOrSwitched(regimen1, regimen2)) {
                            Regimenhistory regimenhistory = RegimenIntrospector.getRegimenHistory(patientId, regimentype2, regimen2);
                            System.out.println("Regimen history..." + regimenhistory.getDateVisit());
                            cell[0].setCellValue(regimenhistory.getDateVisit() == null ? "" : dateFormatExcel.format(regimenhistory.getDateVisit()));
                        }
                    }
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((resultSet.getDate("date_last_refill") == null) ? "" : dateFormatExcel.format(resultSet.getDate("date_last_refill")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(Integer.toString(resultSet.getInt("last_refill_duration")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((resultSet.getDate("date_next_refill") == null) ? "" : dateFormatExcel.format(resultSet.getDate("date_next_refill")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("last_clinic_stage"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((resultSet.getDate("date_last_clinic") == null) ? "" : dateFormatExcel.format(resultSet.getDate("date_last_clinic")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((resultSet.getDate("date_next_clinic") == null) ? "" : dateFormatExcel.format(resultSet.getDate("date_next_clinic")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(Double.toString(resultSet.getDouble("last_cd4")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(Double.toString(resultSet.getDouble("last_cd4p")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((resultSet.getDate("date_last_cd4") == null) ? "" : dateFormatExcel.format(resultSet.getDate("date_last_cd4")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(Double.toString(resultSet.getDouble("last_viral_load")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((resultSet.getDate("date_last_viral_load") == null) ? "" : dateFormatExcel.format(resultSet.getDate("date_last_viral_load")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((resultSet.getDate("viral_load_due_date") == null) ? "" : dateFormatExcel.format(resultSet.getDate("viral_load_due_date")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("viral_load_type"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getInt("send_message"));

                    if (rownum[0] % 100 == 0) {
                        try {
                            ((SXSSFSheet) sheet).flushRows(100); // retain 100 last rows and flush all others

                            // ((SXSSFSheet)sheet).flushRows() is a shortcut for ((SXSSFSheet)sheet).flushRows(0),
                            // this method flushes all rows
                        } catch (IOException ex) {
                            Logger.getLogger(PatientDataConverter.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                return null;
            });

            String directory = contextPath + "transfer/";
            FileUtil fileUtil = new FileUtil();
            fileUtil.makeDir(directory);
            fileUtil.makeDir(request.getContextPath() + "/transfer/");

            fileName = request.getParameterMap().containsKey("facilityIds") ? "patient_" + request.getParameter("state").toLowerCase() + "_" + Long.toString(userId) + ".xlsx" : "patient_" + Long.toString(userId) + ".xlsx";
            FileOutputStream outputStream = new FileOutputStream(new File(directory + fileName));
            workbook.write(outputStream);
            outputStream.close();
            workbook.dispose();  // dispose of temporary files backing this workbook on disk

            //for servlets in the default(root) context, copy file to the transfer folder in root 
            if (!contextPath.equalsIgnoreCase(request.getContextPath())) {
                fileUtil.copyFile(fileName, contextPath + "transfer/", request.getContextPath() + "/transfer/");
            }
            //resultSet = null;                        
        } catch (Exception exception) {
            resultSet = null;
            exception.printStackTrace();
        }
        return "transfer/" + fileName;
    }

    private String getQuery() {
        String facilityIds = "";
        String query = "";
        if (request.getParameterMap().containsKey("facilityIds")) {
            facilityIds = request.getParameter("facilityIds");
            System.out.println("Selected ids......" + facilityIds);
            query = "SELECT DISTINCT patient.*, DATEDIFF(YEAR, date_birth, CURDATE()) AS age FROM patient WHERE facility_id IN (" + facilityIds + ") ORDER BY facility_id, patient_id";
        } else {
            facilityIds = Long.toString((Long) session.getAttribute("facilityId"));
            query = "SELECT patient.*, DATEDIFF(YEAR, date_birth, CURDATE()) AS age FROM patient WHERE facility_id IN (" + facilityIds + ")";

            if (!request.getParameter("gender").trim().isEmpty() && !request.getParameter("gender").trim().equals("--All--")) {
                query += " AND gender = '" + request.getParameter("gender") + "'";
            }
            if (!request.getParameter("ageBegin").trim().isEmpty() && !request.getParameter("ageEnd").trim().isEmpty()) {
                query += " AND age >= " + Integer.parseInt(request.getParameter("ageBegin")) + " AND age <= " + Integer.parseInt(request.getParameter("ageEnd"));
            }
            if (!request.getParameter("state").trim().isEmpty()) {
                query += " AND state = '" + request.getParameter("state") + "'";
            }
            if (!request.getParameter("lga").trim().isEmpty()) {
                query += " AND lga = '" + request.getParameter("lga") + "'";
            }

            //if(!request.getParameter("entryPoint").trim().isEmpty()) query += " AND entry_point = '" + request.getParameter("entryPoint") + "'";
            if (!request.getParameter("currentStatus").trim().isEmpty() && !request.getParameter("currentStatus").trim().equals("--All--")) {
                String currentStatus = (request.getParameter("currentStatus").trim().equals("HIV  non ART")) ? "HIV+ non ART" : request.getParameter("currentStatus");
                if (currentStatus.equals("Currently Active")) {
                    query += " AND current_status IN (" + Constants.ClientStatus.ON_TREATMENT + ") AND DATEDIFF(DAY, date_last_refill + last_refill_duration, CURDATE()) <= " + Constants.LTFU.PEPFAR + " AND date_started IS NOT NULL";
                } else {
                    query += " AND current_status = '" + currentStatus + "'";
                }
            }

            if (!request.getParameter("dateCurrentStatusBegin").trim().isEmpty() && !request.getParameter("dateCurrentStatusEnd").trim().isEmpty()) {
                query += " AND date_current_status >= '" + DateUtil.parseStringToSqlDate(request.getParameter("dateCurrentStatusBegin"), "MM/dd/yyyy") + "' AND date_current_status <= '" + DateUtil.parseStringToSqlDate(request.getParameter("dateCurrentStatusEnd"), "MM/dd/yyyy") + "'";
            }
            if (!request.getParameter("regimentype").trim().isEmpty()) {
                query += " AND regimentype = '" + request.getParameter("regimentype") + "'";
            }
            if (!request.getParameter("dateRegistrationBegin").trim().isEmpty() && !request.getParameter("dateRegistrationEnd").trim().isEmpty()) {
                query += " AND date_registration >= '" + DateUtil.parseStringToSqlDate(request.getParameter("dateRegistrationBegin"), "MM/dd/yyyy") + "' AND date_registration <= '" + DateUtil.parseStringToSqlDate(request.getParameter("dateRegistrationEnd"), "MM/dd/yyyy") + "'";
            }
            if (!request.getParameter("artStartDateBegin").trim().isEmpty() && !request.getParameter("artStartDateEnd").trim().isEmpty()) {
                query += " AND date_started >= '" + DateUtil.parseStringToSqlDate(request.getParameter("artStartDateBegin"), "MM/dd/yyyy") + "' AND date_started <= '" + DateUtil.parseStringToSqlDate(request.getParameter("artStartDateEnd"), "MM/dd/yyyy") + "'";
            }
            if (!request.getParameter("clinicStage").trim().isEmpty()) {
                query += " AND last_clinic_stage = '" + request.getParameter("clinicStage") + "'";
            }
            if (!request.getParameter("cd4Begin").trim().isEmpty() && !request.getParameter("cd4End").trim().isEmpty()) {
                query += " AND last_cd4 >= " + Double.parseDouble(request.getParameter("cd4Begin")) + " AND last_cd4 <= " + Double.parseDouble(request.getParameter("cd4End"));
            }
            if (!request.getParameter("viralloadBegin").trim().isEmpty() && !request.getParameter("viralloadEnd").trim().isEmpty()) {
                query += " AND last_viral_load >= " + Double.parseDouble(request.getParameter("viralloadBegin")) + " AND last_viral_load <= " + Double.parseDouble(request.getParameter("viralloadEnd"));
            }
            query += " ORDER BY current_status";
        }
        return query;
    }

    public String getQueryNotification() {

        Integer entity = Integer.parseInt(request.getParameter("entity"));
        String facilityIds = Long.toString((Long) session.getAttribute("facilityId"));
        String query = "";

        switch (entity) {
            case 1:
                query = "SELECT patient.*, DATEDIFF(YEAR, date_birth, CURDATE()) AS age FROM patient WHERE facility_id IN (" + facilityIds + ") AND current_status IN ('HIV+ non ART', 'ART Start', 'ART Restart', 'ART Transfer In', 'Pre-ART Transfer In') AND date_started IS NULL ";
                break;

            case 2:
                query = "SELECT DISTINCT patient.* FROM patient WHERE facility_id IN (" + facilityIds + ") AND current_status IN ('ART Start', 'ART Restart', 'ART Transfer In') AND TIMESTAMPDIFF(DAY, date_last_refill + last_refill_duration, CURDATE()) > 90 AND date_started IS NOT NULL";
                break;

            case 3:
                query = "SELECT patient.*, DATEDIFF(YEAR, date_birth, CURDATE()) AS age FROM patient WHERE facility_id IN (" + facilityIds + ") AND current_status IN ('ART Start', 'ART Restart', 'ART Transfer In') AND date_last_refill IS NULL";
                break;

            case 4:
                query = "SELECT patient.*, DATEDIFF(YEAR, date_birth, CURDATE()) AS age FROM patient WHERE facility_id IN (" + facilityIds + ") AND current_status IN ('ART Start', 'ART Restart', 'ART Transfer In') AND date_last_viral_load IS NULL AND DATEDIFF(MONTH, date_started, CURDATE()) >= 6";
                break;

            case 5:
                query = "SELECT patient.*, DATEDIFF(YEAR, date_birth, CURDATE()) AS age FROM patient WHERE facility_id IN (" + facilityIds + ") AND current_status IN ('ART Start', 'ART Restart', 'ART Transfer In') AND last_viral_load >1000";
                break;
        }

        query += " ORDER BY current_status";
        return query;
    }

    public String getQueryCaseManagement() {
        Integer casemanagerId = Integer.parseInt(request.getParameter("casemanagerId"));
        Long facilityId = (Long) session.getAttribute("facilityId");
        String query = "SELECT patient.*, DATEDIFF(YEAR, date_birth, CURDATE()) AS age FROM patient WHERE facility_id = " + facilityId + " AND casemanager_id = " + casemanagerId + " ORDER BY current_status";
        return query;
    }

    private String getNrti(String regimen) {
        String nrti = regimen == null ? "" : "Other";
        if (regimen.contains("d4T")) {
            nrti = "D4T (Stavudine)";
        } else {
            if (regimen.contains("AZT")) {
                nrti = "AZT (Zidovudine)";
            } else {
                if (regimen.contains("TDF")) {
                    nrti = "TDF (Tenofovir)";
                } else {
                    if (regimen.contains("DDI")) {
                        nrti = "DDI (Didanosine)";
                    }
                }
            }
        }
        return nrti;
    }

    private String getNnrti(String regimen) {
        String nnrti = regimen == null ? "" : "Other";
        if (regimen.contains("EFV")) {
            nnrti = " EFV (Efavirenz)";
        } else {
            if (regimen.contains("NVP")) {
                nnrti = " NVP (Nevirapine)";
            }
        }
        return nnrti;
    }

    private Map getFacility(long facilityId) {
        Map<String, Object> facilityMap = new HashMap<String, Object>();
        try {
            // fetch the required records from the database
            String query = "SELECT DISTINCT facility.name, facility.address1, facility.address2, facility.phone1, facility.phone2, facility.email, lga.name AS lga, state.name AS state FROM facility JOIN lga ON facility.lga_id = lga.lga_id JOIN state ON facility.state_id = state.state_id WHERE facility_id = " + facilityId;
            jdbcTemplate.query(query, rs -> {
                while (rs.next()) {
                    facilityMap.put("facilityName", rs.getString("name"));
                    facilityMap.put("lga", rs.getString("lga"));
                    facilityMap.put("state", rs.getString("state"));
                }
                return null;
            });
        } catch (Exception exception) {

        }
        return facilityMap;
    }

    private void executeUpdate(String query) {
        try {
            jdbcTemplate.execute(query);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
