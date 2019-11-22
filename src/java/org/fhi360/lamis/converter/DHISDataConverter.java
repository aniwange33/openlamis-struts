/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.converter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.FileUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author user10
 */
public class DHISDataConverter {
    private HttpServletRequest request;
    private HttpSession session;
//    private final NamedParameterJdbcTemplate parameterJdbcTemplate = ContextProvider.getBean(NamedParameterJdbcTemplate.class);
//    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);


    public DHISDataConverter() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    public synchronized String convertExcel() {
//        String period = request.getParameter("period");
//        String stateId = request.getParameter("state");
//        String facilityId = request.getParameter("facility");
//        Map<String, Object> params = new HashMap<>();
//        params.put("period", period);
//        if (facilityId == null) {
//            List<Long> ids = jdbcTemplate.queryForList("select facility_id from facility where state_id = ?",
//                    Long.class, Long.parseLong(stateId));
//            params.put("facilities", ids);
//        } else {
//            params.put("facilities", Collections.singletonList(facilityId));
//        }
//
//        final String[] fileName = {""};
//        String contextPath = ServletActionContext.getServletContext().getInitParameter("contextPath");
//        Long userId = (Long) session.getAttribute("userId");
//        SXSSFWorkbook workbook = new SXSSFWorkbook(-1);
//        workbook.setCompressTempFiles(true);
//        Sheet sheet = workbook.createSheet();
//
//        String query = "SELECT d.period, d.value, i.description AS DATA_ELEMENT, c.description AS CATEGORY_COMB, " +
//                "f.name AS FACILITY, l.name as lga, s.name as state FROM dhisvalue d JOIN indicator i " +
//                "ON d.data_element_id = i.data_element_id JOIN " +
//                "categorycomb c ON d.category_id = c.category_id inner JOIN facility f ON d.facility_id = f.facility_id " +
//                "inner join lga l on l.lga_id = f.lga_id inner join state s on l.state_id = s.state_id " +
//                "WHERE d.period = :period and d.facility_id in (:facilities)";
//        parameterJdbcTemplate.query(query, params, rs -> {
//            int rownum = 0;
//            int cellnum = 0;
//            Row row = sheet.createRow(rownum++);
//            Cell cell = row.createCell(cellnum++);
//            cell.setCellValue("State");
//            cell = row.createCell(cellnum++);
//            cell.setCellValue("LGA");
//            cell = row.createCell(cellnum++);
//            cell.setCellValue("Facility");
//            cell = row.createCell(cellnum++);
//            cell.setCellValue("Period");
//            cell = row.createCell(cellnum++);
//            cell.setCellValue("Data Element");
//            cell = row.createCell(cellnum++);
//            cell.setCellValue("Category");
//            cell = row.createCell(cellnum);
//            cell.setCellValue("Value");
//            while (rs.next()) {
//                cellnum = 0;
//                row = sheet.createRow(rownum++);
//                cell = row.createCell(cellnum++);
//                cell.setCellValue(rs.getString("state"));
//                cell = row.createCell(cellnum++);
//                cell.setCellValue(rs.getString("lga"));
//                cell = row.createCell(cellnum++);
//                cell.setCellValue(rs.getString("facility"));
//                cell = row.createCell(cellnum++);
//                cell.setCellValue(rs.getString("period"));
//                cell = row.createCell(cellnum++);
//                cell.setCellValue(rs.getString("DATA_ELEMENT"));
//                cell = row.createCell(cellnum++);
//                cell.setCellValue(rs.getString("CATEGORY_COMB"));
//                cell = row.createCell(cellnum);
//                cell.setCellValue(rs.getDouble("value"));
//                if (rownum % 100 == 0) {
//                    try {
//                        ((SXSSFSheet) sheet).flushRows(100);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            String directory = contextPath + "transfer/";
//            FileUtil fileUtil = new FileUtil();
//            fileUtil.makeDir(directory);
//            fileUtil.makeDir(request.getContextPath() + "/transfer/");
//
//            fileName[0] = "dhis_daily_report_" + userId + ".xlsx";
//
//            try {
//                FileOutputStream outputStream = new FileOutputStream(new File(directory + fileName[0]));
//                workbook.write(outputStream);
//                outputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            workbook.dispose();
//            return null;
//        });
       return "transfer/"; //+ fileName[0];
    }

     /*try {
        executeUpdate("DROP TABLE IF EXISTS report");
        executeUpdate("CREATE TEMPORARY TABLE report AS SELECT d.period, d.value, i.description AS DATA_ELEMENT, c.description AS CATEGORY_COMB, f.name AS FACILITY FROM dhisvalue d JOIN indicator i ON d.data_element_id = i.data_element_id JOIN categorycomb c ON d.category_id = c.category_id JOIN facility f ON d.facility_id = f.facility_id WHERE d.period IN ('2019W14', '2019W15', '2019W16', '2019W17', '2019W18')");
        executeUpdate("call CSVWRITE('c:/lamis2/report.csv', 'select * from report')");

    } catch (Exception ex) {

    }*/

    public String test() {
        return "";
    }
}
