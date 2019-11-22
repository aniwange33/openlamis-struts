/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.converter;

import org.fhi360.lamis.utility.FileUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class EidDataConverter {

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    private long userId;

    public EidDataConverter() {
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

            int rownum[] = {0};
            int cellnum[] = {0};
            Row[] row = {sheet.createRow(rownum[0]++)};
            Cell[] cell = {row[0].createCell(cellnum[0]++)};
            cell[0].setCellValue("Treatment Unit");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Specimen Type");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Lab No");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date of Received");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date Sample Collected");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date of Assay");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date Result Reported");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date Result Dispatched");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Test Result");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Reason Sample Untestable");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Hospital No.");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Gender");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date of Birth");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Age");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Age Unit");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Reason for PCR");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Rapid Test Done");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date Rapid Test");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Rapid Test Result");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("ART Received Mother");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Prophylax Received Mother");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Prophylax Received Child");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Ever Breast");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Breastfeding Now");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Cessation Age");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Cotrim");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Next Appointment");

            query = "SELECT specimen.*, eid.* FROM specimen JOIN eid ON specimen.labno = eid.labno ORDER BY specimen.treatment_unit_id";
            jdbcTemplate.query(query, (ResultSet resultSet) -> {
                while (resultSet.next()) {

                    row[0] = sheet.createRow(rownum[0]++);
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getLong("treatment_unit_id"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("specimen_type"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("labno"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((resultSet.getDate("date_received") == null) ? "" : dateFormatExcel.format(resultSet.getDate("date_received")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((resultSet.getDate("date_collected") == null) ? "" : dateFormatExcel.format(resultSet.getDate("date_collected")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((resultSet.getDate("date_assay") == null) ? "" : dateFormatExcel.format(resultSet.getDate("date_assay")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((resultSet.getDate("date_reported") == null) ? "" : dateFormatExcel.format(resultSet.getDate("date_reported")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((resultSet.getDate("date_dispatched") == null) ? "" : dateFormatExcel.format(resultSet.getDate("date_dispatched")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("result"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("reason_no_test"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("hospital_num"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0] .setCellValue(resultSet.getString("gender"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((resultSet.getDate("date_birth") == null) ? "" : dateFormatExcel.format(resultSet.getDate("date_birth")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(Integer.toString(resultSet.getInt("age")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("age_unit"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("reason_pcr"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("rapid_test_done"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((resultSet.getDate("date_rapid_test") == null) ? "" : dateFormatExcel.format(resultSet.getDate("date_rapid_test")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("rapid_test_result"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("mother_art_received"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("mother_prophylax_received"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("child_prophylax_received"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("breastfed_ever"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("breastfed_now"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(Integer.toString(resultSet.getInt("feeding_cessation_age")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("cotrim"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((resultSet.getDate("next_appointment") == null) ? "" : dateFormatExcel.format(resultSet.getDate("next_appointment")));

                    if (rownum[0] % 100 == 0) {
                        try {
                            ((SXSSFSheet) sheet).flushRows(100); // retain 100 last rows and flush all others
                            
                            // ((SXSSFSheet)sheet).flushRows() is a shortcut for ((SXSSFSheet)sheet).flushRows(0),
                            // this method flushes all rows
                        } catch (IOException ex) {
                            Logger.getLogger(EidDataConverter.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                return null;
            });

            String directory = contextPath + "transfer/";

            FileUtil fileUtil = new FileUtil();
            fileUtil.makeDir(directory);
            fileUtil.makeDir(request.getContextPath() + "/transfer/");

            fileName = "eid_" + Long.toString(userId) + ".xlsx";
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

    
}
