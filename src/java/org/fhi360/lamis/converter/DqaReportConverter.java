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
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.FileUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author user10
 */
public class DqaReportConverter {

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    private long userId;

    public DqaReportConverter() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    public synchronized String convertExcel() {
        String fileName = "";

        DateFormat dateFormatExcel = new SimpleDateFormat("dd-MMM-yyyy");
        String contextPath = ServletActionContext.getServletContext().getInitParameter("contextPath");
        String facilityIds = request.getParameter("facilityIds");
        String state = request.getParameter("state").toLowerCase();
        userId = (Long) session.getAttribute("userId");

        SXSSFWorkbook workbook = new SXSSFWorkbook(-1);  // turn off auto-flushing and accumulate all rows in memory
        Sheet sheet = workbook.createSheet();

        try {

            query = "SELECT MAX(visit) AS count FROM (SELECT patient_id, COUNT(DISTINCT date_visit) AS visit FROM pharmacy WHERE facility_id IN (" + facilityIds + ") GROUP BY facility_id, patient_id) AS t1";
            Long max_col = jdbcTemplate.queryForLong(query);

            int rownum[] = {0};
            int cellnum[] = {0};
            Row[] row = {sheet.createRow(rownum[0]++)};
            Cell[] cell = {row[0].createCell(cellnum[0]++)};

            cell[0].setCellValue("Facility Id");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Patient Id");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Hospital Num");

            for (int i = 1; i <= max_col; i++) {
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue("Date Visit" + Integer.toString(i));
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue("Regimen Line" + Integer.toString(i));
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue("Regimen" + Integer.toString(i));
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue("Refill" + Integer.toString(i));
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue("Next Appoint" + Integer.toString(i));
            }

            query = "SELECT DISTINCT pharmacy.facility_id, pharmacy.patient_id, pharmacy.date_visit, pharmacy.regimentype_id, pharmacy.regimen_id, pharmacy.duration, pharmacy.next_appointment, patient.hospital_num FROM pharmacy JOIN patient ON pharmacy.patient_id = patient.patient_id WHERE pharmacy.facility_id IN (" + facilityIds + ") ORDER BY pharmacy.facility_id, pharmacy.patient_id, pharmacy.date_visit";
            jdbcTemplate.query(query, (ResultSet resultSet) -> {
                long facilityId = 0;
                long patientId = 0;
                while (resultSet.next()) {
                    if (resultSet.getLong("facility_id") != facilityId || resultSet.getLong("patient_id") != patientId) {

                        row[0] = sheet.createRow(cellnum[0]++);
                        cell[0] = row[0].createCell(cellnum[0]++);
                        cell[0].setCellValue(resultSet.getLong("facility_id"));
                        facilityId = resultSet.getLong("facility_id");
                        cell[0] = row[0].createCell(cellnum[0]++);
                        cell[0].setCellValue(resultSet.getLong("patient_id"));
                        patientId = resultSet.getLong("patient_id");
                        cell[0] = row[0].createCell(cellnum[0]++);
                        cell[0].setCellValue(resultSet.getString("hospital_num"));
                    }
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((resultSet.getDate("date_visit") == null) ? "" : dateFormatExcel.format(resultSet.getDate("date_visit")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getLong("regimentype_id"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getLong("regimen_id"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getInt("duration"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((resultSet.getDate("next_appointment") == null) ? "" : dateFormatExcel.format(resultSet.getDate("next_appointment")));

                    if (cellnum[0] % 100 == 0) {
                        try {
                            ((SXSSFSheet) sheet).flushRows(100); // retain 100 last rows and flush all others
                            
                            // ((SXSSFSheet)sheet).flushRows() is a shortcut for ((SXSSFSheet)sheet).flushRows(0),
                            // this method flushes all rows
                        } catch (IOException ex) {
                            Logger.getLogger(DqaReportConverter.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }

                return null; //To change body of generated lambdas, choose Tools | Templates.
            });

            String directory = contextPath + "transfer/";

            FileUtil fileUtil = new FileUtil();
            fileUtil.makeDir(directory);
            fileUtil.makeDir(request.getContextPath() + "/transfer/");

            fileName = "pharmacy_" + state + "_" + Long.toString(userId) + ".xlsx";
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



    private String getContextPath() {
        String contextPath = ServletActionContext.getServletContext().getInitParameter("contextPath");
        //String contextPath = ServletActionContext.getServletContext().getRealPath(File.separator).replace("\\", "/");
        return contextPath;
    }

}
