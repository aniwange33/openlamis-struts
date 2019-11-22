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

public class LabDataConverter {
    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private long userId;
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    
    public LabDataConverter() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();        
    } 
    
    public synchronized String convertExcel() {
        String fileName = "";
        
        DateFormat dateFormatExcel = new SimpleDateFormat("dd-MMM-yyyy");      
        String contextPath = ServletActionContext.getServletContext().getInitParameter("contextPath");
        String facilityIds = request.getParameter("facilityIds");
        long labtestId = Long.parseLong(request.getParameter("labtestId"));
        String state = request.getParameter("state").toLowerCase();
        userId = (Long) session.getAttribute("userId");
        
        SXSSFWorkbook workbook = new SXSSFWorkbook(-1);  // turn off auto-flushing and accumulate all rows in memory
        Sheet sheet = workbook.createSheet();
        
        try {
            
            query = "SELECT MAX(visit) AS count FROM (SELECT patient_id, COUNT(DISTINCT date_reported) AS visit FROM laboratory WHERE facility_id IN (" + facilityIds + ") AND labtest_id = " + labtestId + " GROUP BY facility_id, patient_id) AS t1";
            int max_col = getCount(query);

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
            cell[0].setCellValue("Test Id");
            
            for(int i = 1; i <= max_col; i++) {
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue("Sample Collect"+Integer.toString(i));
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue("Date Report"+Integer.toString(i));                 
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue("Result"+Integer.toString(i));                 
            }

            query = "SELECT DISTINCT laboratory.facility_id, laboratory.patient_id, laboratory.date_reported, laboratory.resultab, laboratory.resultpc, laboratory.labtest_id, laboratory.date_collected, patient.hospital_num FROM laboratory JOIN patient ON laboratory.patient_id = patient.patient_id WHERE laboratory.facility_id IN (" + facilityIds + ") AND laboratory.labtest_id = " + labtestId + " ORDER BY laboratory.facility_id, laboratory.patient_id, laboratory.date_reported";
            
            jdbcTemplate.query(query, resultSet -> {
            long facilityId = 0;
            long patientId = 0;
            while(resultSet.next()) {
                if(resultSet.getLong("facility_id") != facilityId || resultSet.getLong("patient_id") != patientId) {
                    cellnum[0] = 0;
                    row[0] = sheet.createRow(rownum[0]++);
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getLong("facility_id"));
                    facilityId = resultSet.getLong("facility_id"); 
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getLong("patient_id"));
                    patientId = resultSet.getLong("patient_id"); 
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("hospital_num"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("labtest_id"));
                }
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue((resultSet.getDate("date_collected") == null)? "" : dateFormatExcel.format(resultSet.getDate("date_collected")));
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue((resultSet.getDate("date_reported") == null)? "" : dateFormatExcel.format(resultSet.getDate("date_reported")));
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue((resultSet.getString("resultab") == null)? resultSet.getString("resultpc") : resultSet.getString("resultab"));

                if(rownum[0] % 100 == 0) {
                    try {
                        ((SXSSFSheet)sheet).flushRows(100); // retain 100 last rows and flush all others
                        
                        // ((SXSSFSheet)sheet).flushRows() is a shortcut for ((SXSSFSheet)sheet).flushRows(0),
                        // this method flushes all rows
                    } catch (IOException ex) {
                        Logger.getLogger(LabDataConverter.class.getName()).log(Level.SEVERE, null, ex);
                    }
               }                                
            }           
            return null;
            });
            
            String directory = contextPath+"transfer/";            
            FileUtil fileUtil = new FileUtil();
            fileUtil.makeDir(directory);
            fileUtil.makeDir(request.getContextPath()+"/transfer/");
            
            fileName = "lab_"+state+"_"+Long.toString(userId)+".xlsx";
            FileOutputStream outputStream = new FileOutputStream(new File(directory+fileName));
            workbook.write(outputStream);
            outputStream.close();
            workbook.dispose();  // dispose of temporary files backing this workbook on disk
            
            //for servlets in the default(root) context, copy file to the transfer folder in root 
            if(!contextPath.equalsIgnoreCase(request.getContextPath())) fileUtil.copyFile(fileName, contextPath+"transfer/", request.getContextPath()+"/transfer/");                    
                       
        }
        catch (Exception exception) {        
            exception.printStackTrace();
        }
        return "transfer/"+fileName;
    }
    
    private void executeUpdate(String query) {
        try {
            jdbcTemplate.execute(query);
        }
        catch (Exception exception) {
            
        }        
    }        
    
    private int getCount(String query) {
       int[] count  = {0};
       try {
           jdbcTemplate.query(query, resultSet -> {
            if(resultSet.next()) {
                count[0] = resultSet.getInt("count");
            }
            return null;
           });
        }
        catch (Exception exception) {
            //jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }
        return count[0];
    }      

    private String getContextPath() {
        String contextPath = ServletActionContext.getServletContext().getInitParameter("contextPath");
        //String contextPath = ServletActionContext.getServletContext().getRealPath(File.separator).replace("\\", "/");
        return contextPath;
    }

}
