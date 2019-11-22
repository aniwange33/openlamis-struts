/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.converter;


import org.fhi360.lamis.utility.FileUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

/**
 *
 * @author user1
 */
public class ViralLoadSummaryConverter {
    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
     private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private long userId;
    
    public ViralLoadSummaryConverter() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();        
    } 
    
    public synchronized String convertExcel() {
        userId = (Long) session.getAttribute("userId");
        String fileName = "";
        
        String contextPath = ServletActionContext.getServletContext().getInitParameter("contextPath");
        String facilityIds = request.getParameter("facilityIds"); 
        String state = request.getParameter("state").toLowerCase();
        
        System.out.println("date1: "+ request.getParameter("reportingDateBegin"));
        System.out.println("date2: "+ request.getParameter("reportingDateEnd"));
  
        String reportingDateBegin = DateUtil.formatDateString(request.getParameter("reportingDateBegin"), "MM/dd/yyyy", "yyyy-MM-dd");         
        String reportingDateEnd = DateUtil.formatDateString(request.getParameter("reportingDateEnd"), "MM/dd/yyyy", "yyyy-MM-dd");
     
       System.out.println("date con: "+ reportingDateBegin);
       System.out.println("date con: "+ reportingDateEnd);
         
        SXSSFWorkbook workbook = new SXSSFWorkbook(-1);  // turn off auto-flushing and accumulate all rows in memory
        Sheet sheet = workbook.createSheet();
        
        try{
         
          
            int rownum[] = {0};
            int cellnum[] = {0};
            Row[] row = {sheet.createRow(rownum[0]++)};
            Cell[] cell = {row[0].createCell(cellnum[0]++)};
            cell[0].setCellValue("Facility");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("No of Viral Load Test");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("No of Patients Test");
            
            query = "SELECT COUNT(*) AS total, laboratory.facility_id, facility.name FROM laboratory JOIN facility ON laboratory.facility_id = facility.facility_id WHERE laboratory.facility_id IN (" + facilityIds + ")"
                    + " AND laboratory.date_reported >= '" + reportingDateBegin + "' AND laboratory.date_reported <= '" + reportingDateEnd + "' AND laboratory.labtest_id = 16 GROUP BY facility.facility_id ORDER BY facility.name"; 
           jdbcTemplate.query(query, (resultSet) -> {
                 while(resultSet.next()) {
                long facilityId = resultSet.getLong("facility_id");
             
                row[0] = sheet.createRow(rownum[0]++);
                 cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue(resultSet.getString("name"));
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue(resultSet.getInt("total"));
                                
                // Count the number of patient tested during the reporting period
                query = "SELECT COUNT(DISTINCT patient_id) AS subtotal FROM laboratory WHERE facility_id = " + facilityId 
                        + " AND laboratory.date_reported >= '" + reportingDateBegin + "' AND laboratory.date_reported <= '" + reportingDateEnd + "' AND laboratory.labtest_id = 16";
               jdbcTemplate.query(query, (rs) -> {
                     cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(rs.getInt("subtotal"));  
                   return null; //To change body of generated lambdas, choose Tools | Templates.
               });
                              
            } 

               return null; //To change body of generated lambdas, choose Tools | Templates.
           });
            
          
            String directory = contextPath+"transfer/";
            
            FileUtil fileUtil = new FileUtil();
            fileUtil.makeDir(directory);
            fileUtil.makeDir(request.getContextPath()+"/transfer/");
            
            fileName = "summary_"+state+"_"+Long.toString(userId)+".xlsx";
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
}
