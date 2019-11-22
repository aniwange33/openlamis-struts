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
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.fhi360.lamis.utility.DateUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class PatientStatusSummaryConverter1 {

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private long userId;

    public PatientStatusSummaryConverter1() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    public synchronized String convertExcel() {
        userId = (Long) session.getAttribute("userId");
        String fileName = "";

        String contextPath = ServletActionContext.getServletContext().getInitParameter("contextPath");
        String facilityIds = request.getParameter("facilityIds");
        String state = request.getParameter("state").toLowerCase();

        SXSSFWorkbook workbook = new SXSSFWorkbook(-1);  // turn off auto-flushing and accumulate all rows in memory
        Sheet sheet = workbook.createSheet();

        try {

            int rownum[] = {0};
            int cellnum[] = {0};
            Row[] row = {sheet.createRow(rownum[0]++)};
            Cell[] cell = {row[0].createCell(cellnum[0]++)};
            cell[0].setCellValue("Facility");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Newly Enrolled (in this facility)");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Pre-ART Transfer In");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("ART Transfer In");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Newly Started (in this facility)");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("ART Restart");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Current On Treatment");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Active");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Pre-ART Transfer Out");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("ART Transfer Out");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Lost to Follow Up");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Stopped Treament");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Known Death");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Virally Suppressed");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Due for Viral Load");

//            cell = row.createCell(cellnum++);
//            cell.setCellValue("Started this month");
//
//            cell = row.createCell(cellnum++);
//            cell.setCellValue("Current this month");
            query = "SELECT DISTINCT patient.facility_id, facility.name FROM patient JOIN facility ON patient.facility_id = facility.facility_id WHERE patient.facility_id IN (" + facilityIds + ") ORDER BY facility.name";
            jdbcTemplate.query(query, (resultSet) -> {
                while (resultSet.next()) {
                    try {
                        long facilityId = resultSet.getLong("facility_id");

                        row[0] = sheet.createRow(rownum[0]++);
                        cell[0] = row[0].createCell(cellnum[0]++);
                        cell[0].setCellValue(resultSet.getString("name"));

                        int newlyEnrolled[] = {0};
                        int preArtTransferIn[] = {0};
                        int artTransferIn[] = {0};
                        int newlyStarted[] = {0};
                        int restart[] = {0};
                        int currentOnTreatment[] = {0};
                        int preArtTransferOut[] = {0};
                        int artTransferOut[] = {0};
                        int LTFU[] = {0};
                        int stopped[] = {0};
                        int dead[] = {0};
                        int vlSuppressed[] = {0};
                        int vlDue[] = {0};
                        int active[] = {0};

                        int newlyStartedPeriod[] = {0};
                        int currentOnTreatmentPeriod[] = {0};

                        SimpleDateFormat dateformat3 = new SimpleDateFormat("dd/MM/yyyy");
                        Date reportingDateBegin = dateformat3.parse("30/09/2018");
                        //Date reportingDateBegin = new Date();

                        query = "SELECT status_registration, current_status, date_started, last_viral_load, viral_load_due_date, date_last_refill, last_refill_duration FROM patient WHERE facility_id = " + facilityId + " AND status_registration IS NOT NULL AND current_status IS NOT NULL";

                        jdbcTemplate.query(query, (rs) -> {
                            while (rs.next()) {
                                String statusRegistration = rs.getString("status_registration");
                                String currentStatus = rs.getString("current_status");
                                Date dateStarted = rs.getDate("date_started");
                                double lastViralLoad = rs.getDouble("last_viral_load");
                                Date viralLoadDueDate = rs.getDate("viral_load_due_date");
                                Date dateLastRefill = rs.getDate("date_last_refill");
                                int duration = rs.getInt("last_refill_duration");

                                int monthRefill = duration / 30;
                                if (monthRefill <= 0) {
                                    monthRefill = 1;
                                }

                                if (currentStatus.equalsIgnoreCase("ART Start") || currentStatus.equalsIgnoreCase("ART Restart") || currentStatus.equalsIgnoreCase("ART Transfer In")) {
                                    if (dateLastRefill != null) {
                                        if (DateUtil.addYearMonthDay(dateLastRefill, monthRefill + 3, "MONTH").after(reportingDateBegin)) {
                                            active[0]++;
                                        }
                                    } else {
                                        if (dateStarted != null) {
                                            if (DateUtil.addYearMonthDay(dateStarted, 3, "MONTH").before(reportingDateBegin)) {
                                                active[0]++;
                                            }
                                        }
                                    }

                                    //query = "SELECT DISTINCT patient_id FROM patient WHERE patient_id = " + patientId + " AND current_status IN ('ART Start', 'ART Restart', 'ART Transfer In') AND DATEDIFF(DAY, date_last_refill + last_refill_duration, CURDATE()) <= 90 AND date_started IS NOT NULL ORDER BY current_status";
                                }

                                int year = 0;
                                int month = 0;
                                if (dateStarted != null) {
                                    year = DateUtil.getYear(dateStarted);
                                    month = DateUtil.getMonth(dateStarted);
                                }

                                System.out.println("Year/Month: " + year + "/" + month);

                                if (statusRegistration.equalsIgnoreCase("HIV+ non ART")) {
                                    newlyEnrolled[0]++;
                                }
                                if (statusRegistration.equalsIgnoreCase("Pre-ART Transfer In")) {
                                    preArtTransferIn[0]++;
                                }
                                if (statusRegistration.equalsIgnoreCase("ART Transfer In")) {
                                    artTransferIn[0]++;
                                }
                                if (!statusRegistration.equalsIgnoreCase("ART Transfer In") && dateStarted != null) {
                                    newlyStarted[0]++;
                                    if (year == 2017 && month == 9) {
                                        newlyStartedPeriod[0]++;
                                    }
                                }

                                if (currentStatus.equalsIgnoreCase("ART Start") && dateStarted != null) {
                                    currentOnTreatment[0]++;
                                    if (year <= 2017) {
                                        currentOnTreatmentPeriod[0]++;
                                    }

                                    //check for virally suppresed and due for VL
                                    if (lastViralLoad < 1000) {
                                        vlSuppressed[0]++;
                                    }

                                    if (viralLoadDueDate != null && viralLoadDueDate.after(new Date())) {
                                        vlDue[0]++;
                                    }
                                }

                                if (currentStatus.equalsIgnoreCase("ART Transfer In") && dateStarted != null) {
                                    currentOnTreatment[0]++;
                                    if (year <= 2017) {
                                        currentOnTreatmentPeriod[0]++;
                                    }

                                    //check for virally suppresed and due for VL
                                    if (lastViralLoad < 1000) {
                                        vlSuppressed[0]++;
                                    }

                                    if (viralLoadDueDate != null && viralLoadDueDate.after(new Date())) {
                                        vlDue[0]++;
                                    }
                                }

                                if (currentStatus.equalsIgnoreCase("ART Restart") && dateStarted != null) {
                                    restart[0]++;
                                    currentOnTreatment[0]++;
                                    if (year <= 2017) {
                                        currentOnTreatmentPeriod[0]++;
                                    }

                                    //check for virally suppresed and due for VL
                                    if (lastViralLoad < 1000) {
                                        vlSuppressed[0]++;
                                    }

                                    if (viralLoadDueDate != null && viralLoadDueDate.after(new Date())) {
                                        vlDue[0]++;
                                    }
                                }

                                if (currentStatus.equalsIgnoreCase("Pre-ART Transfer Out")) {
                                    preArtTransferOut[0]++;
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

                            return null; //To change body of generated lambdas, choose Tools | Templates.
                        });

                        cell[0] = row[0].createCell(cellnum[0]++);
                         cell[0].setCellValue(newlyEnrolled[0]);

                          cell[0] = row[0].createCell(cellnum[0]++);
                         cell[0].setCellValue(preArtTransferIn[0]);

                         cell[0] = row[0].createCell(cellnum[0]++);
                         cell[0].setCellValue(artTransferIn[0]);

                         cell[0] = row[0].createCell(cellnum[0]++);
                         cell[0].setCellValue(newlyStarted[0]);

                           cell[0] = row[0].createCell(cellnum[0]++);
                         cell[0].setCellValue(restart[0]);

                       cell[0] = row[0].createCell(cellnum[0]++);
                         cell[0].setCellValue(currentOnTreatment[0]);

                          cell[0] = row[0].createCell(cellnum[0]++);
                         cell[0].setCellValue(active[0]);

                          cell[0] = row[0].createCell(cellnum[0]++);
                         cell[0].setCellValue(preArtTransferOut[0]);

                           cell[0] = row[0].createCell(cellnum[0]++);
                         cell[0].setCellValue(artTransferOut[0]);

                          cell[0] = row[0].createCell(cellnum[0]++);
                         cell[0].setCellValue(LTFU[0]);

                          cell[0] = row[0].createCell(cellnum[0]++);
                         cell[0].setCellValue(stopped[0]);

                           cell[0] = row[0].createCell(cellnum[0]++);
                         cell[0].setCellValue(dead[0]);

                           cell[0] = row[0].createCell(cellnum[0]++);
                         cell[0].setCellValue(vlSuppressed[0]);

                          cell[0] = row[0].createCell(cellnum[0]++);
                         cell[0].setCellValue(vlDue[0]);
                    } catch (ParseException ex) {
                        Logger.getLogger(PatientStatusSummaryConverter1.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
                return null; //To change body of generated lambdas, choose Tools | Templates.
            });

            String directory = contextPath + "transfer/";

            FileUtil fileUtil = new FileUtil();
            fileUtil.makeDir(directory);
            fileUtil.makeDir(request.getContextPath() + "/transfer/");

            fileName = "summary_" + state + "_" + Long.toString(userId) + ".xlsx";
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
