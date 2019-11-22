/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.controller;

import com.opensymphony.xwork2.ActionSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.fhi360.lamis.dao.jdbc.FacilityJDBC;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.FileUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author MEdor
 */
public class SyncDownloadAction extends ActionSupport implements ServletRequestAware {

    private HttpServletRequest request;
    private String status;
    private String fileName = "Sync.xlsx";

    private InputStream stream;
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    public SyncDownloadAction() {
    }

    public InputStream getStream() {
        return stream;
    }

    public String downloadSync() {
        // SyncUtilService.syncFolder();
        String contextPath = ServletActionContext.getServletContext().getInitParameter("contextPath");
        String directory = contextPath + "sync/";
        SXSSFWorkbook workbook = new SXSSFWorkbook(-1);  // turn off auto-flushing and accumulate all rows in memory
        Sheet sheet = workbook.createSheet();

        try {

            String query = "select * from synchistory where upload_date between ? and ? order by facility_id";

            System.out.println("Before try...");

            jdbcTemplate.query(query, resultSet -> {
                int rownum = 0;
                int cellnum = 0;
                Row row = sheet.createRow(rownum++);
                Cell cell = row.createCell(cellnum++);
                cell.setCellValue("State");
                cell = row.createCell(cellnum++);
                cell.setCellValue("LGA");
                cell = row.createCell(cellnum++);
                cell.setCellValue("Facility Id");
                cell = row.createCell(cellnum++);
                cell.setCellValue("Facility Name");
                cell = row.createCell(cellnum++);
                cell.setCellValue("Upload Date");

                SimpleDateFormat format = new SimpleDateFormat("dd MMM, yyyy");
                System.out.println("'Date format " + format);
                while (resultSet.next()) {
                    Long facilityId = resultSet.getLong("facility_id");
                    String stateName = FacilityJDBC.getStateNameForFacility(facilityId);
                    String lgaName = FacilityJDBC.getLgaNameForFacility(facilityId);
                    cellnum = 0;
                    row = sheet.createRow(rownum++);
                    cell = row.createCell(cellnum++);
                    cell.setCellValue(stateName);

                    cell = row.createCell(cellnum++);

                    cell.setCellValue(lgaName);

                    cell = row.createCell(cellnum++);
                    cell.setCellValue(facilityId);

                    cell = row.createCell(cellnum++);
                    cell.setCellValue(resultSet.getString("facility_name"));

                    cell = row.createCell(cellnum++);
                    Date date = resultSet.getDate("upload_date");
                    cell.setCellValue(format.format(date));
                }
                return null; //To change body of generated lambdas, choose Tools | Templates.
            }, new Date(), getDateParameter("to"));
            System.out.println("After try...");
            FileUtil fileUtil = new FileUtil();
            fileUtil.makeDir(directory);

            try (FileOutputStream outputStream = new FileOutputStream(new File(directory + fileName))) {
                workbook.write(outputStream);
            }
            System.out.println("Before dispose...");
            workbook.dispose();
            stream = new FileInputStream(new File(directory + fileName));
            System.out.println("Stream..." + stream);
        } catch (Exception exception) {
        }

        return SUCCESS;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private Date getDateParameter(String date) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return format.parse(request.getParameter(date));
        } catch (ParseException ex) {
        }
        return null;
    }

    @Override
    public void setServletRequest(HttpServletRequest hsr) {
        request = hsr;
    }
}
