/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.converter;

import org.fhi360.lamis.utility.FileUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.Deduplicator;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.Scrambler;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class DeduplicatorDataConverter {

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    private long userId;
    private Scrambler scrambler;
    private Boolean viewIdentifier;

    public DeduplicatorDataConverter() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
        this.scrambler = new Scrambler();
        if (ServletActionContext.getRequest().getSession().getAttribute("viewIdentifier") != null) {
            this.viewIdentifier = (Boolean) ServletActionContext.getRequest().getSession().getAttribute("viewIdentifier");
        }
    }

    public synchronized String convertExcel() {
        String fileName = "";

        DateFormat dateFormatExcel = new SimpleDateFormat("dd-MMM-yyyy");
        String contextPath = ServletActionContext.getServletContext().getInitParameter("contextPath");

        userId = (Long) session.getAttribute("userId");

        SXSSFWorkbook workbook = new SXSSFWorkbook(-1);  // turn off auto-flushing and accumulate all rows in memory

        Sheet sheet = workbook.createSheet();

        try {

            int rownum[] = {0};
            int cellnum[] = {0};
            Row[] row = {sheet.createRow(rownum[0]++)};
            Cell[] cell = {row[0].createCell(cellnum[0]++)};
            cell[0].setCellValue("Patient ID");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Hospital Num");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Unique ID");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Surname");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Other Names");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Gender");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date of Birth");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Address");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("ART Status");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Encounter");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Retain Patient");

            query = "SELECT patient_id, hospital_num, unique_id, surname, other_names, gender, date_birth, address, current_status FROM duplicate ORDER BY hospital_num";
            jdbcTemplate.query(query, (resultSet) -> {
                int retain = 1;
                while (resultSet.next()) {
                    Long count = 0L;
                    Long clinicCount = 0L;
                    Long pharmacyCount = 0L;
                    Long laboratoryCount = 0L;

                    clinicCount = jdbcTemplate.queryForObject("SELECT COUNT(DISTINCT date_visit) AS count FROM clinic WHERE patient_id = " + resultSet.getLong("patient_id"), Long.class);
                    count = count + clinicCount;
                    pharmacyCount = jdbcTemplate.queryForObject("SELECT COUNT(DISTINCT date_visit) AS count FROM pharmacy WHERE patient_id = " + resultSet.getLong("patient_id"), Long.class);
                    count = count + pharmacyCount;
                    laboratoryCount = jdbcTemplate.queryForObject("SELECT COUNT(DISTINCT date_reported) AS count FROM laboratory WHERE patient_id = " + resultSet.getLong("patient_id"), Long.class);
                    count = count + laboratoryCount;
                    String surname = scrambler.unscrambleCharacters(resultSet.getString("surname"));
                    String other_names = scrambler.unscrambleCharacters(resultSet.getString("other_names"));
                    String address = scrambler.unscrambleCharacters(resultSet.getString("address"));

                    row[0] = sheet.createRow(rownum[0]++);
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getLong("patient_id"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    sheet.setColumnHidden(0, true);
                    cell[0].setCellValue(resultSet.getString("hospital_num"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("unique_id"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(surname);
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(other_names);
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("gender"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((resultSet.getDate("date_birth") == null) ? "" : dateFormatExcel.format(resultSet.getDate("date_birth")));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(address);
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("current_status"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(count);
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(retain);
                }

                return null; //To change body of generated lambdas, choose Tools | Templates.
            });

            String directory = contextPath + "transfer/";

            FileUtil fileUtil = new FileUtil();
            fileUtil.makeDir(directory);
            fileUtil.makeDir(request.getContextPath() + "/transfer/");
            fileName = "deduplicator_" + Long.toString(userId) + ".xlsx";
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
