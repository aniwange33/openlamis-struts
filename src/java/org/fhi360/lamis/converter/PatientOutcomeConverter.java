/**
 *
 * @author AALOZIE
 */
package org.fhi360.lamis.converter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.FileUtil;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class PatientOutcomeConverter {

    private HttpServletRequest request;
    private HttpSession session;

    private ArrayList<Map<String, Object>> patientList;
    private String fileName;
    private String facilityIds;
    String query;
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    public PatientOutcomeConverter() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }
    int i;

    public synchronized String convertExcel() {
        patientList = new ArrayList<>();

        if (request.getParameterMap().containsKey("facilityIds")) {
            facilityIds = request.getParameter("facilityIds");
        } else {
            facilityIds = Long.toString((Long) session.getAttribute("facilityId"));
        }

        executeUpdate("DROP TABLE IF EXISTS cohort");
        executeUpdate("CREATE TEMPORARY TABLE cohort (facility_id bigint, patient_id bigint, hospital_num varchar(25), gender varchar(7), age int, age_unit varchar(15), date_registration date, date_started date, mon12 varchar(75), mon24 varchar(75), mon36 varchar(75), mon48 varchar(75), dropped int)");
        executeUpdate("INSERT INTO cohort SELECT facility_id, patient_id, hospital_num, gender, age, age_unit, date_registration, date_started, '', '', '', '', 0 FROM patient WHERE facility_id IN (" + facilityIds + ")");
        System.out.println("Insert cohort data ......");
        query = "SELECT patient_id, hospital_num, gender, age, age_unit, date_registration, current_status, date_current_status, date_started FROM patient WHERE facility_id IN (" + facilityIds + ")";
        jdbcTemplate.query(query, (resultSet) -> {
            while (resultSet.next()) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("patientId", resultSet.getLong("patient_id"));
                map.put("dateRegistration", resultSet.getDate("date_registration"));
                patientList.add(map);
            }
            return null;
        });

        int month[] = {12, 24, 36, 48};
        String endDate[] = {""};

        for (i = 0; i <= 3; i++) {
            for (Map map : patientList) {
                long patientId[] = {(Long) map.get("patientId")};
                Date dateRegistration[] = {(Date) map.get("dateRegistration")};
                query = "SELECT patient_id FROM cohort WHERE patient_id = " + patientId[0] + " AND dropped = 0";
                jdbcTemplate.query(query, (resultSet) -> {

                    endDate[0] = DateUtil.parseDateToString(DateUtil.addMonth(dateRegistration[0], month[i]), "yyyy-MM-dd");
                    query = "SELECT current_status FROM statushistory WHERE patient_id = " + patientId[0] + " AND date_current_status = SELECT MAX(date_current_status) FROM statushistory WHERE patient_id = " + patientId[0] + " AND date_current_status <= '" + endDate[0] + "'";
                    jdbcTemplate.query(query, (resultSet2) -> {
                        String currentStatus = resultSet2.getString("current_status");
                        checkOutcome(patientId[0], currentStatus, i);
                        return null;
                    });
                    return null;
                });
            }

        }
        convert(); // convert cohort table to MS excel

        return "transfer/" + fileName;
    }

    private void checkOutcome(long patientId, String currentStatus, int i) {
        String mon12 = "";
        String mon24 = "";
        String mon36 = "";
        String mon48 = "";
        String status = "";
        int drop = 0;

        if (currentStatus.trim().equals("ART Transfer Out")) {
            status = "Transfer Out";
            drop = 1;
        }
        if (currentStatus.trim().equals("Stopped Treatment")) {
            status = "Stopped Treatment";
            drop = 1;
        }
        if (currentStatus.trim().equals("Lost to Follow Up")) {
            status = "Lost";
            drop = 1;
        }
        if (currentStatus.trim().equals("Known Death")) {
            status = "Dead";
            drop = 1;
        }
        if (drop == 1) {
            if (i == 0) {
                mon12 = status;
            } else {
                if (i == 1) {
                    mon12 = "Alive";
                    mon24 = status;
                } else {
                    if (i == 2) {
                        mon12 = "Alive";
                        mon24 = "Alive";
                        mon36 = status;
                    } else {
                        mon12 = "Alive";
                        mon24 = "Alive";
                        mon36 = "Alive";
                        mon48 = status;
                    }
                }
            }
        } else {
            if (i == 0) {
                mon12 = "Alive";
            } else {
                if (i == 1) {
                    mon12 = "Alive";
                    mon24 = "Alive";
                } else {
                    if (i == 2) {
                        mon12 = "Alive";
                        mon24 = "Alive";
                        mon36 = "Alive";
                    } else {
                        mon12 = "Alive";
                        mon24 = "Alive";
                        mon36 = "Alive";
                        mon48 = "Alive";
                    }
                }
            }
        }
        System.out.println("Saving data ......");
        executeUpdate("UPDATE cohort SET mon12 = '" + mon12 + "', mon24 = '" + mon24 + "', mon36 = '" + mon36 + "', mon48 = '" + mon48 + "', dropped = " + drop + " WHERE patient_id = " + patientId);
    }

    private void convert() {
        DateFormat dateFormatExcel = new SimpleDateFormat("dd-MMM-yyyy");
        String contextPath = ServletActionContext.getServletContext().getInitParameter("contextPath");
        long userId = (Long) session.getAttribute("userId");

        SXSSFWorkbook workbook = new SXSSFWorkbook(-1);  // turn off auto-flushing and accumulate all rows in memory
        workbook.setCompressTempFiles(true); // temp files will be gzipped
        Sheet sheet = workbook.createSheet();

        try {

            int rownum[] = {0};
            int cellnum[] = {0};
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
            cell[0].setCellValue("Age");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Age Unit");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Gender");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date Registration");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("ART Start Date");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Mon12");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Mon24");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Mon36");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Mon48");

            jdbcTemplate.query("SELECT * FROM cohort ORDER BY facility_id, patient_id", (resultSet) -> {
                long facilityId = 0;
                long patientId = 0;
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
                    cell[0].setCellValue(Integer.toString(resultSet.getInt("age")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("age_unit"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("gender"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((resultSet.getDate("date_registration") == null) ? "" : dateFormatExcel.format(resultSet.getDate("date_registration")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((resultSet.getDate("date_started") == null) ? "" : dateFormatExcel.format(resultSet.getDate("date_started")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("mon12"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("mon24"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("mon36"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("mon48"));

                    if (rownum[0] % 100 == 0) {
                        try {
                            ((SXSSFSheet) sheet).flushRows(100); // retain 100 last rows and flush all others

                            // ((SXSSFSheet)sheet).flushRows() is a shortcut for ((SXSSFSheet)sheet).flushRows(0),
                            // this method flushes all rows
                        } catch (IOException ex) {
                            Logger.getLogger(PatientOutcomeConverter.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                return null; //To change body of generated lambdas, choose Tools | Templates.
            });

            String directory = contextPath + "transfer/";
            FileUtil fileUtil = new FileUtil();
            fileUtil.makeDir(directory);
            fileUtil.makeDir(request.getContextPath() + "/transfer/");

            fileName = request.getParameterMap().containsKey("facilityIds") ? "treatment_outcome_" + request.getParameter("state").toLowerCase() + "_" + Long.toString(userId) + ".xlsx" : "treatment_outcome_" + Long.toString(userId) + ".xlsx";
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
                return null;
            });

        } catch (Exception exception) {

        }
        return facilityMap;
    }

    private void executeUpdate(String query) {
        try {
            transactionTemplate.execute((ts) -> {
                jdbcTemplate.execute(query);
                return null; //To change body of generated lambdas, choose Tools | Templates.
            });
        } catch (Exception exception) {

        }
    }
}
