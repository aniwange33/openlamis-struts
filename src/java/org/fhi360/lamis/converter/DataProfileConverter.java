/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.converter;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
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
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author user10
 */
public class DataProfileConverter {

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    private long userId, facilityId;

    public DataProfileConverter() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    public synchronized String convertExcel() {
        String fileName = "";

        DateFormat dateFormatExcel = new SimpleDateFormat("dd-MMM-yyyy");
        String contextPath = ServletActionContext.getServletContext().getInitParameter("contextPath");
        String year[] = {request.getParameter("year")};
        String month[] = {request.getParameter("month")};
        userId = (Long) session.getAttribute("userId");

        SXSSFWorkbook workbook = new SXSSFWorkbook(-1);  // turn off auto-flushing and accumulate all rows in memory
        Sheet sheet = workbook.createSheet();

        facilityId = (Long) session.getAttribute("facilityId");

        try {

            int rownum[] = {0};
            int cellnum[] = {0};
            Row[] row = {sheet.createRow(rownum[0]++)};
            final Cell[] cell = {row[0].createCell(cellnum[0]++)};
            cell[0].setCellValue("Date");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("User");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Enrolment Records");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Clinic Records");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Pharmacy Records");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Laboratory Records");

            //Get all the users in this facility...
            query = "SELECT DISTINCT user_id, username FROM user where facility_id = '" + facilityId + "'";
            jdbcTemplate.query(query, (ResultSet resultSet) -> {
                while (resultSet.next()) {
                    String userId1 = Integer.toString(resultSet.getInt("user_id"));
                    String username = resultSet.getString("username");
                    row[0] = sheet.createRow(rownum[0]++);
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(getMonthMap().get(month[0]) + " " + year[0]);
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(username);
                    //The Enrolment records
                    query = "SELECT count(*) as enrolment_count FROM monitor where user_id = '" + userId1 + "' AND table_name = 'patient' AND YEAR(time_stamp) = '" + year + "' AND MONTH(time_stamp) = '" + month + "'";
                    Long enrolment_count = jdbcTemplate.queryForLong(query);
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(0);
                    cell[0].setCellValue(enrolment_count);
                    //The Clinic records
                    query = "SELECT count(*) as clinic_count FROM monitor where user_id = '" + userId1 + "' AND table_name = 'clinic' AND YEAR(time_stamp) = '" + year + "' AND MONTH(time_stamp) = '" + month + "'";
                    Long clinic_count = jdbcTemplate.queryForLong(query);
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(0);
                    cell[0].setCellValue(clinic_count);
                    //The Pharmacy records
                    query = "SELECT count(*) as pharmacy_count FROM monitor where user_id = '" + userId1 + "' AND table_name = 'pharmacy' AND YEAR(time_stamp) = '" + year + "' AND MONTH(time_stamp) = '" + month + "'";
                    Long pharmacy_count = jdbcTemplate.queryForLong(query);
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(0);
                    cell[0].setCellValue(pharmacy_count);
                    //The Laboratory records
                    query = "SELECT count(*) as laboratory_count FROM monitor where user_id = '" + userId1 + "' AND table_name = 'laboratory' AND YEAR(time_stamp) = '" + year + "' AND MONTH(time_stamp) = '" + month + "'";
                    Long laboratory_count = jdbcTemplate.queryForLong(query);
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(0);
                    cell[0].setCellValue(laboratory_count);
                }
                return null;
            });
            if (rownum[0] % 100 == 0) {
                ((SXSSFSheet) sheet).flushRows(100); // retain 100 last rows and flush all others

                // ((SXSSFSheet)sheet).flushRows() is a shortcut for ((SXSSFSheet)sheet).flushRows(0),
                // this method flushes all rows
            }

            String directory = contextPath + "transfer/";

            FileUtil fileUtil = new FileUtil();
            fileUtil.makeDir(directory);
            fileUtil.makeDir(request.getContextPath() + "/transfer/");

            fileName = "profiling_" + getMonthMap().get(month) + "_" + year + "_" + Long.toString(userId) + ".xlsx";
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

    private Map<String, String> getMonthMap() {

        // Initialize the Month Map...
        Map<String, String> month_map = new HashMap<>();
        month_map.put("01", "Jan");
        month_map.put("02", "Feb");
        month_map.put("03", "Mar");
        month_map.put("04", "Apr");
        month_map.put("05", "May");
        month_map.put("06", "Jun");
        month_map.put("07", "Jul");
        month_map.put("08", "Aug");
        month_map.put("09", "Sep");
        month_map.put("10", "Oct");
        month_map.put("11", "Nov");
        month_map.put("12", "Dec");

        return month_map;
    }

}
