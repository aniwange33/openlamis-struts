/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.converter;

import org.fhi360.lamis.utility.FileUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class RetentionSummaryConverter {

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private long userId;

    public RetentionSummaryConverter() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    public synchronized String convertExcel() throws IOException {
        FileOutputStream outputStream = null;

        userId = (Long) session.getAttribute("userId");
        String fileName = "";
        try {
            String contextPath = ServletActionContext.getServletContext().getInitParameter("contextPath");
            String facilityIds = request.getParameter("facilityIds");
            String state = request.getParameter("state").toLowerCase();
            SXSSFWorkbook workbook = new SXSSFWorkbook(-1);  // turn off auto-flushing and accumulate all rows in memory
            Sheet sheet = workbook.createSheet();
            int rownum[] = {0};
            int cellnum[] = {0};
            Row[] row = {sheet.createRow(rownum[0]++)};
            Cell[] cell = {row[0].createCell(cellnum[0]++)};
            cell[0].setCellValue("Facility");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Started On ART");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("ART Transfer Out");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Lost to Follow Up");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Stopped Treament");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Known Death");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Alive and on Treatment");
            query = "SELECT DISTINCT patient.facility_id, facility.name FROM patient JOIN facility ON patient.facility_id = facility.facility_id WHERE patient.facility_id IN (" + facilityIds + ") ORDER BY facility.name";
            jdbcTemplate.query(query, (resultSet) -> {

                while (resultSet.next()) {
                    long facilityId = resultSet.getLong("facility_id");

                    row[0] = sheet.createRow(rownum[0]++);
                    cell[0] = row[0].createCell(rownum[0]++);
                    cell[0].setCellValue(resultSet.getString("name"));

                    int artStart[] = {0};
                    int artTransferOut[] = {0};
                    int LTFU[] = {0};
                    int stopped[] = {0};
                    int dead[] = {0};
                    int alive[] = {0};
                    int vlSuppressed[] = {0};
                    int vlDue[] = {0};

                    String dateBegin = "2016-10-01";
                    String dateEnd = "2017-09-30";

                    query = "SELECT current_status, date_started, last_viral_load, viral_load_due_date FROM patient WHERE facility_id = " + facilityId + " AND date_started >= '" + dateBegin + "' AND date_started <= '" + dateEnd + "' AND current_status IS NOT NULL";
                    jdbcTemplate.query(query, (rs) -> {

                        while (rs.next()) {
                            String currentStatus = rs.getString("current_status");
                            double lastViralLoad = rs.getDouble("last_viral_load");
                            Date viralLoadDueDate = rs.getDate("viral_load_due_date");

                            artStart[0]++;
                            if (currentStatus.equalsIgnoreCase("ART Start")) {
                                //check for virally suppresed and due for VL
                                if (lastViralLoad < 1000) {
                                    vlSuppressed[0]++;
                                }

                                if (viralLoadDueDate != null && viralLoadDueDate.after(new Date())) {
                                    vlDue[0]++;
                                }
                            }

                            if (currentStatus.equalsIgnoreCase("ART Transfer In")) {
                                //check for virally suppresed and due for VL
                                if (lastViralLoad < 1000) {
                                    vlSuppressed[0]++;
                                }

                                if (viralLoadDueDate != null && viralLoadDueDate.after(new Date())) {
                                    vlDue[0]++;
                                }
                            }

                            if (currentStatus.equalsIgnoreCase("ART Restart")) {
                                //check for virally suppresed and due for VL
                                if (lastViralLoad < 1000) {
                                    vlSuppressed[0]++;
                                }

                                if (viralLoadDueDate != null && viralLoadDueDate.after(new Date())) {
                                    vlDue[0]++;
                                }
                            }

                            if (currentStatus.equalsIgnoreCase("ART Transfer Out")) {
                                artTransferOut[0]++;
                            }

                            if (currentStatus.equalsIgnoreCase("Lost to Follow Up")) {
                                LTFU[0]++;
                            }

                            if (currentStatus.equalsIgnoreCase("Stopped Treatment")) {
                                stopped[0]++;
                            }

                            if (currentStatus.equalsIgnoreCase("Known Death")) {
                                dead[0]++;
                            }
                        }
                        //Alive and on treatment
                        alive[0] = artStart[0] - artTransferOut[0] - LTFU[0] - stopped[0] - dead[0];

                        cell[0] = row[0].createCell(rownum[0]++);
                        cell[0].setCellValue(artStart[0]);

                        cell[0] = row[0].createCell(rownum[0]++);
                        cell[0].setCellValue(artTransferOut[0]);

                        cell[0] = row[0].createCell(rownum[0]++);
                        cell[0].setCellValue(LTFU[0]);

                        cell[0] = row[0].createCell(rownum[0]++);
                        cell[0].setCellValue(stopped[0]);

                        cell[0] = row[0].createCell(rownum[0]++);
                        cell[0].setCellValue(dead[0]);

                        cell[0] = row[0].createCell(rownum[0]++);
                        cell[0].setCellValue(alive[0]);
                        return null; //To change body of generated lambdas, choose Tools | Templates.
                    });

                }

                return null;
            });
            String directory = contextPath + "transfer/";
            FileUtil fileUtil = new FileUtil();
            fileUtil.makeDir(directory);
            fileUtil.makeDir(request.getContextPath() + "/transfer/");
            fileName = "retention_" + state + "_" + Long.toString(userId) + ".xlsx";
            outputStream = new FileOutputStream(new File(directory + fileName));
            workbook.write(outputStream);
            outputStream.close();
            workbook.dispose();  // dispose of temporary files backing this workbook on disk
            //for servlets in the default(root) context, copy file to the transfer folder in root
            if (!contextPath.equalsIgnoreCase(request.getContextPath())) {
                fileUtil.copyFile(fileName, contextPath + "transfer/", request.getContextPath() + "/transfer/");
            }

        } catch (Exception ex) {
            Logger.getLogger(RetentionSummaryConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "transfer/" + fileName;
    }

}
