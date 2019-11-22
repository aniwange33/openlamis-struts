/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.converter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
 * @author iduruanyanwu
 */
public class PatientEncounterSummaryConverter {

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    private long userId;

    public PatientEncounterSummaryConverter() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

//    public void convertByOption(){
//        
//    }
    public synchronized String convertExcel() {
        String fileName = "";
        int global_row = 0;

        DateFormat dateFormatExcel = new SimpleDateFormat("dd-MMM-yyyy");
        String contextPath = ServletActionContext.getServletContext().getInitParameter("contextPath");

        String facilityIds = request.getParameter("facilityIds");
        //String yearId = request.getParameter("year");
        String state = request.getParameter("state").toLowerCase();
        userId = (Long) session.getAttribute("userId");

        SXSSFWorkbook workbook = new SXSSFWorkbook(-1);  // turn off auto-flushing and accumulate all rows in memory
        Sheet sheet = workbook.createSheet();

        try {
            //We Define the first row, ie the headers...
            int rownum[] = {0};
            int cellnum[] = {0};

            Row[] row = {sheet.createRow(rownum[0]++)};
            Cell[] cell = {row[0].createCell(cellnum[0]++)};
            cell[0].setCellValue("State".toUpperCase());
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Facility Name".toUpperCase());
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Hospital Number".toUpperCase());
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date of registration".toUpperCase());
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("ART Start Date".toUpperCase());
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Current status".toUpperCase());
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date of current status".toUpperCase());
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("age".toUpperCase());
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("sex".toUpperCase());
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Number of Clinic Visits".toUpperCase());
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Number Pharmacy Visits".toUpperCase());
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Number of CD4 Tests".toUpperCase());
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Number of Viral Load Tests".toUpperCase());

            //Algorithm for SQL Query begins here...
            ArrayList<String> patient_names = new ArrayList<>();
            Scrambler scrambler = new Scrambler();

            //1. Select the distinct facility ids...
            if (facilityIds == "") {
                query = "SELECT state.name as state, facility.name as facility, patient.patient_id, patient.surname, patient.other_names, patient.hospital_num, patient.date_registration, patient.date_started, patient.current_status, patient.date_current_status, patient.age, patient.age_unit, patient.gender FROM patient JOIN facility ON patient.facility_id = facility.facility_id JOIN state ON facility.state_id = state.state_id  WHERE facility.active = 1 ORDER BY patient_id ASC";
            } else {
                query = "SELECT state.name as state, facility.name as facility, patient.patient_id, patient.surname, patient.other_names, patient.hospital_num, patient.date_registration, patient.date_started, patient.current_status, patient.date_current_status, patient.age, patient.age_unit, patient.gender FROM patient JOIN facility ON patient.facility_id = facility.facility_id JOIN state ON facility.state_id = state.state_id  WHERE facility.active = 1 AND facility.facility_id IN(" + facilityIds + ") ORDER BY patient_id ASC";
            }
            jdbcTemplate.query(query, (resultSet) -> {
                while (resultSet.next()) {

                    row[0] = sheet.createRow(rownum[0]++);
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("state") == "" ? "N/A" : resultSet.getString("state"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("facility") == "" ? "N/A" : resultSet.getString("facility"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("hospital_num") == "" ? "N/A" : resultSet.getString("hospital_num"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("date_registration") == "" ? "N/A" : resultSet.getString("date_registration"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("date_started") == "" ? "N/A" : resultSet.getString("date_started"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("current_status") == "" ? "N/A" : resultSet.getString("current_status"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("date_current_status") == "" ? "N/A" : resultSet.getString("date_current_status"));
                    String age = resultSet.getString("age");
                    String age_unit = resultSet.getString("age_unit");
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(age + " " + age_unit);
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("gender") == "" ? "N/A" : resultSet.getString("gender"));

                    System.out.println("Patient ID is: " + resultSet.getString("patient_id"));

                    //Setting the patient IDS
                    //patient_names.add(resultSet.getString("patient_id"));
                    String patient_id = resultSet.getString("patient_id");
                    //global_row = rownum;

                    //Clinic Visits
                    query = "SELECT count(date_visit) as report_count FROM clinic WHERE patient_id = '" + patient_id + "' GROUP BY patient_id ORDER BY patient_id ASC";
                    Long report_count = jdbcTemplate.queryForLong(query);
                    //cellnum = 9;
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(0);

                    cell[0].setCellValue(report_count);

                    //Pharmacy Visits
                    query = "SELECT count(DISTINCT date_visit) as report_count from pharmacy WHERE patient_id = '" + patient_id + "' GROUP BY patient_id ORDER BY patient_id ASC";
                    Long report_count1 = jdbcTemplate.queryForLong(query);

                    //cellnum = 10;
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(0);

                    cell[0].setCellValue(report_count1);

                    //CD4
                    query = "SELECT count(DISTINCT date_reported) as report_count from laboratory WHERE patient_id = '" + patient_id + "' AND labtest_id = 1 GROUP BY patient_id ORDER BY patient_id ASC";
                    Long report_count2 = jdbcTemplate.queryForLong(query);

                    //cellnum = 11;
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(0);

                    cell[0].setCellValue(report_count2);

                    //Viral Load
                    query = "SELECT count(DISTINCT date_reported) as report_count from laboratory WHERE patient_id = '" + patient_id + "' AND labtest_id = 16 GROUP BY patient_id ORDER BY patient_id ASC";
                   Long report_count3 = jdbcTemplate.queryForLong(query);

                    System.out.println("Patient ID in Lab Viral Load is: " + resultSet.getString("patient_id"));

                    //cellnum = 12;
                     cell[0] = row[0].createCell(cellnum[0]++);
                     cell[0].setCellValue(0);
                   
                         cell[0].setCellValue(report_count3);
                    
                }

                //Flusher
                if (rownum[0] % 100 == 0) {
                    try {
                        ((SXSSFSheet) sheet).flushRows(100); // retain 100 last rows and flush all others
                        
                        // ((SXSSFSheet)sheet).flushRows() is a shortcut for ((SXSSFSheet)sheet).flushRows(0),
                        // this method flushes all rows
                    } catch (IOException ex) {
                        Logger.getLogger(PatientEncounterSummaryConverter.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                return null; //To change body of generated lambdas, choose Tools | Templates.
            });

            String directory = contextPath + "transfer/";

            FileUtil fileUtil = new FileUtil();
            fileUtil.makeDir(directory);
            fileUtil.makeDir(request.getContextPath() + "/transfer/");

            fileName = "patient_encounter_summary.xlsx";
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
            return exception.getMessage();
        }
        return "transfer/" + fileName;
    }
    private Map<Integer, String> getMonthMap() {

        // Initialize the Month Map...
        Map<Integer, String> month_map = new HashMap<>();
        month_map.put(1, "Jan");
        month_map.put(2, "Feb");
        month_map.put(3, "Mar");
        month_map.put(4, "Apr");
        month_map.put(5, "May");
        month_map.put(6, "Jun");
        month_map.put(7, "Jul");
        month_map.put(8, "Aug");
        month_map.put(8, "Sep");
        month_map.put(10, "Oct");
        month_map.put(11, "Nov");
        month_map.put(12, "Dec");

        return month_map;
    }

    private Map<String, Integer> getReverseMonthMap() {

        // initialize the Month Map...
        Map<String, Integer> month_map = getMonthMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

        return month_map;
    }

    private String workMonth(String year, String month) {
        String return_value = year;

        try {
            if (month == "Apr" || month == "Jun" || month == "Sep" || month == "Nov") {
                return_value += "-" + getReverseMonthMap().get(month) + "-30";
            } else if (month == "Feb") {
                if (Integer.valueOf(year) % 4 == 0) { //leap year            
                    return_value += "-" + getReverseMonthMap().get(month) + "-29";
                } else {
                    return_value += "-" + getReverseMonthMap().get(month) + "-28";
                }
            } else if (month != "Apr" && month != "Jun" && month != "Sep" && month != "Nov" && month != "Feb") {
                return_value += "-" + getReverseMonthMap().get(month) + "-31";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return return_value;
    }
}
