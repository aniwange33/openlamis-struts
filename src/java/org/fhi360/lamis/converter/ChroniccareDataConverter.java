/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.converter;


import org.fhi360.lamis.utility.FileUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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
import org.springframework.jdbc.core.JdbcTemplate;

public class ChroniccareDataConverter {

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private long userId;
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    public ChroniccareDataConverter() {
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
            int[] rownum = {0};
            int[] cellnum = {0};
            Row[] row = {sheet.createRow(rownum[0]++)};
            Cell[] cell = {row[0].createCell(cellnum[0]++)};
            cell[0].setCellValue("Facility Id");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Patient Id");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Hospital Num");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date of Visit");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Client Type");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Current Status");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Clinical Stage");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Pregnancy Status");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Last CD4");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date of Last CD4");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Last Viral Load");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date of Last Viral Load");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Eligible for CD4");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Eligible for Last Viral Load");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Currently on TB treatment");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date Started TB");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Referred for TB Diagnosis");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Currently on IPT");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Received INH past 2 year");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Eligible for IPT");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Body Weight");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Height");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("BMI (Adult)");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("MUAC (under 5yrs)");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("MUAC Pregnant");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Hypertensive");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("First Hypertensive");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("BP above 140/90mmHg");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("BP Referred");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Diabetic");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Diabetic Referred");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("First Diabetic");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("DM Referred");

            query = "SELECT * FROM chroniccare WHERE facility_id IN (" + facilityIds + ") ORDER BY facility_id, patient_id, date_visit";
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    cellnum[0] = 0;
                    row[0] = sheet.createRow(rownum[0]++);
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getLong("facility_id"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getLong("patient_id"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    //cell.setCellValue(resultSet.getString("hospital_num"));                
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((resultSet.getDate("date_visit") == null) ? "" : dateFormatExcel.format(resultSet.getDate("date_visit")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("client_type"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("current_status"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("clinic_stage"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("pregnancy_status"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(Double.toString(resultSet.getDouble("last_cd4")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((resultSet.getDate("date_last_cd4") == null) ? "" : dateFormatExcel.format(resultSet.getDate("date_last_cd4")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(Double.toString(resultSet.getDouble("last_viral_load")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((resultSet.getDate("date_last_viral_load") == null) ? "" : dateFormatExcel.format(resultSet.getDate("date_last_viral_load")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("eligible_cd4"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("eligible_viral_load"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("tb_treatment"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((resultSet.getDate("date_started_tb_treatment") == null) ? "" : dateFormatExcel.format(resultSet.getDate("date_started_tb_treatment")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("tb_referred"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("ipt"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("inh"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("eligible_ipt"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(Double.toString(resultSet.getDouble("body_weight")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(Double.toString(resultSet.getDouble("height")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("bmi"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(Double.toString(resultSet.getDouble("muac")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("muac_pediatrics"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("muac_pregnant"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("hypertensive"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("first_hypertensive"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("bp_above"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("bp_referred"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("diabetic"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("first_diabetic"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("dm_referred"));

                    if (rownum[0] % 100 == 0) {
                        try {
                            ((SXSSFSheet) sheet).flushRows(100); // retain 100 last rows and flush all others

                            // ((SXSSFSheet)sheet).flushRows() is a shortcut for ((SXSSFSheet)sheet).flushRows(0),
                            // this method flushes all rows
                        } catch (IOException ex) {
                            Logger.getLogger(ChroniccareDataConverter.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                return null;
            });

            String directory = contextPath + "transfer/";

            FileUtil fileUtil = new FileUtil();
            fileUtil.makeDir(directory);
            fileUtil.makeDir(request.getContextPath() + "/transfer/");

            fileName = "chroniccare_" + state + "_" + Long.toString(userId) + ".xlsx";
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

    private void executeUpdate(String query) {
        try {
            jdbcTemplate.execute(query);
        } catch (Exception exception) {

        }
    }

    private int getCount(String query) {
        int[] count = {0};
        try {
            jdbcTemplate.query(query, resultSet -> {
                if (resultSet.next()) {
                    count[0] = resultSet.getInt("count");
                }
                return null;
            });
        } catch (Exception exception) {

        }
        return count[0];
    }

}
