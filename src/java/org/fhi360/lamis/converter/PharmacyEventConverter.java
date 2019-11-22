/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.converter;

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
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.FileUtil;

import org.fhi360.lamis.utility.Scrambler;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author user1
 */
public class PharmacyEventConverter {

    private HttpServletRequest request;
    private HttpSession session;
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private long userId;
    private Boolean viewIdentifier;
    private Scrambler scrambler;

    private long facilityId, patientId;

    public PharmacyEventConverter() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
        this.scrambler = new Scrambler();
        if (ServletActionContext.getRequest().getSession().getAttribute("viewIdentifier") != null) {
            this.viewIdentifier = (Boolean) session.getAttribute("viewIdentifier");
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
            cell[0].setCellValue("Unique Id");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date Birth");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Age");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Age Unit");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Gender");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Weight");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Height");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Type of Encounter");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Encounter Date");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Did patient come with completed devolvement &/or encounter form from");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Did patient com with completed Pharmacy Order Forms for 4 months ARV refill at CP from the HF");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Did patient confirm that he/she was dispensed two months ARV refill at the health facility at the last visit");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Was chronic care screening services provided at the CP during the visit");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Was medication adherence assessment & counselling done at the CP during this visit");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Were prescribed ARVs dispensed at the CP during this visit");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Were prescribed concomitant drugs dispensed at the CP during the visit");

            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Medicine dispensed #1");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Duration #1");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Qty Prescribed #1");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Qty Dispensed #1");

            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Medicine dispensed #2");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Duration #2");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Qty Prescribed #2");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Qty Dispensed #2");

            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Medicine dispensed #3");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Duration #3");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Qty Prescribed #3");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Qty Dispensed #3");

            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Medicine dispensed #4");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Duration #4");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Qty Prescribed #4");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Qty Dispensed #4");

            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date of next ARV refills");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date of next appointment for lab & clinic evaluation at HF");

            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Devolvement date");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Is patient stable on ART");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date ART started");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Has patient completed and signed the consent form for develvement");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("CD4 at start of ART");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Current CD4");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date of current CD4");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Current Viral load");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date of current Viral load");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Current clinical stage");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date of current clinical stage");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("First ART Regimen");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Current ART Regimen");

            jdbcTemplate.query(getQuery(), (ResultSet resultSet) -> {
                facilityId = 0;
                patientId = 0;
                while (resultSet.next()) {
                    if (resultSet.getLong("facility_id") != facilityId || resultSet.getLong("patient_id") != patientId) {

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
                    }
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
                    cell[0].setCellValue(resultSet.getString("status_registration"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((resultSet.getDate("date_registration") == null) ? "" : dateFormatExcel.format(resultSet.getDate("date_registration")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("current_status"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((resultSet.getDate("date_current_status") == null) ? "" : dateFormatExcel.format(resultSet.getDate("date_current_status")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((resultSet.getDate("date_started") == null) ? "" : dateFormatExcel.format(resultSet.getDate("date_started")));

                    //Adding baseline data
                    boolean[] found = {false};
                    jdbcTemplate.query("SELECT * FROM clinic WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND commence = 1", (rs) -> {
                        found[0] = true;
                        while (rs.next()) {
                            cell[0] = row[0].createCell(cellnum[0]++);
                            cell[0].setCellValue(Double.toString(rs.getDouble("cd4")));
                            cell[0] = row[0].createCell(cellnum[0]++);
                            cell[0].setCellValue(Double.toString(rs.getDouble("cd4p")));
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
                            //cell.setCellValue(rs.getString("regimen") == null? "" : regimenResolver.getRegimen(rs.getString("regimen")));                    
                        }
                        return null; //To change body of generated lambdas, choose Tools | Templates.
                    });
                    if (!found[0]) {
                        cell[0] = row[0].createCell(cellnum[0]++);
                        cell[0] = row[0].createCell(cellnum[0]++);
                        cell[0] = row[0].createCell(cellnum[0]++);
                        cell[0] = row[0].createCell(cellnum[0]++);
                        cell[0] = row[0].createCell(cellnum[0]++);
                        cell[0] = row[0].createCell(cellnum[0]++);
                        cell[0] = row[0].createCell(cellnum[0]++);
                        cell[0] = row[0].createCell(cellnum[0]++);
                        cell[0] = row[0].createCell(cellnum[0]++);
                        cell[0] = row[0].createCell(cellnum[0]++);
                    }

                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("last_clinic_stage"));
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
                    cell[0].setCellValue((resultSet.getDate("date_last_refill") == null) ? "" : dateFormatExcel.format(resultSet.getDate("date_last_refill")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((resultSet.getDate("date_next_refill") == null) ? "" : dateFormatExcel.format(resultSet.getDate("date_next_refill")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((resultSet.getDate("date_last_clinic") == null) ? "" : dateFormatExcel.format(resultSet.getDate("date_last_clinic")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((resultSet.getDate("date_next_clinic") == null) ? "" : dateFormatExcel.format(resultSet.getDate("date_next_clinic")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getInt("send_message"));

                    if (rownum[0] % 100 == 0) {
                        try {
                            ((SXSSFSheet) sheet).flushRows(100); // retain 100 last rows and flush all others

                            // ((SXSSFSheet)sheet).flushRows() is a shortcut for ((SXSSFSheet)sheet).flushRows(0),
                            // this method flushes all rows
                        } catch (IOException ex) {
                            Logger.getLogger(PharmacyEventConverter.class.getName()).log(Level.SEVERE, null, ex);
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

            exception.printStackTrace();
        }
        return "transfer/" + fileName;
    }

    private String getQuery() {
        String facilityIds = "";
        String query = "";
        return query;
    }

    private Map getFacility(long facilityId) {
        Map<String, Object> facilityMap = new HashMap<>();
        try {
            // fetch the required records from the database

            String query = "SELECT DISTINCT facility.name, facility.address1, facility.address2, facility.phone1, facility.phone2, facility.email, lga.name AS lga, state.name AS state FROM facility JOIN lga ON facility.lga_id = lga.lga_id JOIN state ON facility.state_id = state.state_id WHERE facility_id = " + facilityId;
            jdbcTemplate.query(query, (rs) -> {
                facilityMap.put("facilityName", rs.getString("name"));
                facilityMap.put("lga", rs.getString("lga"));
                facilityMap.put("state", rs.getString("state"));
                return null; //To change body of generated lambdas, choose Tools | Templates.
            });

        } catch (Exception exception) {

        }
        return facilityMap;
    }

}
