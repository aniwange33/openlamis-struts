/**
 *
 * @author aalozie
 */

package org.fhi360.lamis.converter;

import org.fhi360.lamis.utility.FileUtil;
import java.io.File;
import java.io.FileOutputStream;
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

public class SyncAuditConverter {
    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private long userId;
    
    public SyncAuditConverter() {
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
        
        try{
            
            
            int rownum[] = {0};
            int cellnum[] = {0};
            Row []row = {sheet.createRow(rownum[0]++)};
            Cell[] cell = {row[0].createCell(cellnum[0]++)};
            cell[0].setCellValue("Facility");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("No. of Registration");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("No. of Clinic");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("No. of Pharmacy");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("No. of Lab");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Total SMS Consent");
            
            query = "SELECT DISTINCT facility_id, name FROM facility WHERE facility_id IN (" + facilityIds + ") ORDER BY name"; 
            jdbcTemplate.query(query, (resultSet) -> {
                while(resultSet.next()) {
                long facilityId = resultSet.getLong("facility_id");
                
               
                row[0] = sheet.createRow(rownum[0]++);
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue(resultSet.getString("name"));
                
                query = "SELECT COUNT(*) AS count FROM (SELECT DISTINCT facility_id, patient_id FROM patient WHERE MONTH(time_stamp) = MONTH(CURDATE()) AND YEAR(time_stamp) = YEAR(CURDATE()) AND facility_id = " + facilityId +")";
                int[] newRecPatient = {getCount(query)};

                query = "SELECT COUNT(*) AS count FROM (SELECT DISTINCT facility_id, patient_id, date_visit FROM clinic WHERE MONTH(time_stamp) = MONTH(CURDATE()) AND YEAR(time_stamp) = YEAR(CURDATE()) AND facility_id = " + facilityId +")";
                int []newRecClinic =  {getCount(query)};

                query = "SELECT COUNT(*) AS count FROM (SELECT DISTINCT facility_id, patient_id, date_visit FROM pharmacy WHERE MONTH(time_stamp) = MONTH(CURDATE()) AND YEAR(time_stamp) = YEAR(CURDATE()) AND facility_id = " + facilityId +")";
                int[] newRecPharm = {getCount(query)};

                query = "SELECT COUNT(*) AS count FROM (SELECT DISTINCT facility_id, patient_id, date_reported FROM laboratory WHERE MONTH(time_stamp) = MONTH(CURDATE()) AND YEAR(time_stamp) = YEAR(CURDATE()) AND facility_id = " + facilityId +")";
                int[] newRecLab =  {getCount(query)};

                query = "SELECT COUNT(*) AS count FROM (SELECT DISTINCT facility_id, patient_id FROM patient WHERE send_message != 0 AND facility_id = " + facilityId +")";                       
                int[] smsConsent = {getCount(query)};
                
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue(newRecPatient[0]);

                  cell[0] = row[0].createCell(cellnum[0]++);
                  cell[0].setCellValue(newRecClinic[0]);

                   cell[0] = row[0].createCell(cellnum[0]++);
                  cell[0].setCellValue(newRecPharm[0]);

                    cell[0] = row[0].createCell(cellnum[0]++);
                  cell[0].setCellValue(newRecLab[0]);

                   cell[0] = row[0].createCell(cellnum[0]++);
                  cell[0].setCellValue(smsConsent[0]);               
            } 
                return null; //To change body of generated lambdas, choose Tools | Templates.
            });
            

            String directory = contextPath+"transfer/";
            
            FileUtil fileUtil = new FileUtil();
            fileUtil.makeDir(directory);
            fileUtil.makeDir(request.getContextPath()+"/transfer/");
            
            fileName = "sync_"+state+"_"+Long.toString(userId)+".xlsx";
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
    
    
    private int getCount(String query) {
       int count[]  = {0};
     
       try {
           jdbcTemplate.query(query, (rs) -> {
                   count[0] = rs.getInt("count");          
               return null; //To change body of generated lambdas, choose Tools | Templates.
           });
        
        }
        catch (Exception exception) {
           
        }
        return count[0];
    }
    
}
