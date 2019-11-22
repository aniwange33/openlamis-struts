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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.report.IndicatorMonitorReport;
import org.apache.poi.ss.util.*;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class TreatmentSummaryConverter {
    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private String reportingDateBegin;
  private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private long userId, state;
    private List<Map<String, Object>> reportParams = new ArrayList<>();
    private IndicatorMonitorReport iMReport = new IndicatorMonitorReport();

    public TreatmentSummaryConverter() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();        
    } 

    public synchronized String convertExcel() {
        userId = (Long) session.getAttribute("userId");
        state = Long.parseLong(request.getParameter("stateId"));
        reportingDateBegin = request.getParameter("reportingDateBegin");
        String fileName = "";
        
        String contextPath = ServletActionContext.getServletContext().getInitParameter("contextPath");
        
        SXSSFWorkbook workbook = new SXSSFWorkbook(-1);  // turn off auto-flushing and accumulate all rows in memory
        Sheet sheet = workbook.createSheet();
        
        try{
            
            int rownum = 0;
            int cellnum = 0;
            //Create a new Row...
            Row row = sheet.createRow(rownum++);
            //Creates a new cell in current row...
            Cell cell = row.createCell(cellnum++);
            cell.setCellValue("State");
            //Creates a new cell in current row...
            cell = row.createCell(cellnum++);
            cell.setCellValue("Facility");
            //Creates a new cell in current row...
            cell = row.createCell(cellnum++);
            cell.setCellValue(new XSSFRichTextString("TLD Transition"));
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 2, 4));
            
            cell = row.createCell(cellnum++);
            cell.setCellValue("");
            
            cell = row.createCell(cellnum++);
            cell.setCellValue("");
            //Creates a new cell in current row...
            cell = row.createCell(cellnum++);
            cell.setCellValue(new XSSFRichTextString("LPV/r"));
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 5, 7));
            
            cell = row.createCell(cellnum++);
            cell.setCellValue("");
            
            cell = row.createCell(cellnum++);
            cell.setCellValue("");
            //Creates a new cell in current row...
            cell = row.createCell(cellnum++);
            cell.setCellValue(new XSSFRichTextString("DMOC"));
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 8, 14));
            //Creates a new cell in current row...
            cell = row.createCell(cellnum++);
            cell.setCellValue("");
            //Creates a new cell in current row...
            cell = row.createCell(cellnum++);
            cell.setCellValue("");
            //Creates a new cell in current row...
            cell = row.createCell(cellnum++);
            cell.setCellValue("");
            
            cell = row.createCell(cellnum++);
            cell.setCellValue("");
            
            cell = row.createCell(cellnum++);
            cell.setCellValue("");
            
            cell = row.createCell(cellnum++);
            cell.setCellValue("");
            
            //Creates a new cell in current row...
            cell = row.createCell(cellnum++);
            cell.setCellValue(new XSSFRichTextString("TX_NEW"));
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 15, 17));
            
            cell = row.createCell(cellnum++);
            cell.setCellValue("");
            
            cell = row.createCell(cellnum++);
            cell.setCellValue("");
            
            //Creates a new cell in current row...
            cell = row.createCell(cellnum++);
            cell.setCellValue(new XSSFRichTextString("TX_ML"));
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 18, 21));
            
            cell = row.createCell(cellnum++);
            cell.setCellValue("");
            
            cell = row.createCell(cellnum++);
            cell.setCellValue("");
            
            cell = row.createCell(cellnum++);
            cell.setCellValue("");
            
            //Create the second Row...
            cellnum = 0;
            row = sheet.createRow(rownum++);
            cell = row.createCell(cellnum++);
            cell.setCellValue("");

            cell = row.createCell(cellnum++);
            cell.setCellValue("");

            //TLD empty
            cell = row.createCell(cellnum++);
            cell.setCellValue("Total");
            
            cell = row.createCell(cellnum++);
            cell.setCellValue("Expected");
            
            cell = row.createCell(cellnum++);
            cell.setCellValue("Percentage (%)");

            //LPV/r Empty
            cell = row.createCell(cellnum++);
            cell.setCellValue("Number on LPV");
            
            cell = row.createCell(cellnum++);
            cell.setCellValue("Current under 3 years");
            
            cell = row.createCell(cellnum++);
            cell.setCellValue("Percentage (%)");

            //DMOC Not Empty
            cell = row.createCell(cellnum++);
            cell.setCellValue("CPARP");

            cell = row.createCell(cellnum++);
            cell.setCellValue("CARC");

            cell = row.createCell(cellnum++);
            cell.setCellValue("MMS");

            cell = row.createCell(cellnum++);
            cell.setCellValue("MMD");
            
            cell = row.createCell(cellnum++);
            cell.setCellValue("TX_CURR");
            
            cell = row.createCell(cellnum++);
            cell.setCellValue("Expected");
            
            cell = row.createCell(cellnum++);
            cell.setCellValue("Percentage (%)");

            //TX_NEW empty
            cell = row.createCell(cellnum++);
            cell.setCellValue("New");
            
            cell = row.createCell(cellnum++);
            cell.setCellValue("Assigned");
            
            cell = row.createCell(cellnum++);
            cell.setCellValue("Percentage (%)");
            
            //TX_ML data
            cell = row.createCell(cellnum++);
            cell.setCellValue(Constants.TxMlStatus.TX_ML_DIED);
            
            cell = row.createCell(cellnum++);
            cell.setCellValue(Constants.TxMlStatus.TX_ML_TRANSFER);
            
            cell = row.createCell(cellnum++);
            cell.setCellValue(Constants.TxMlStatus.TX_ML_TRACED);
            
            cell = row.createCell(cellnum++);
            cell.setCellValue(Constants.TxMlStatus.TX_ML_NOT_TRACED);
        
            //Call method below...
            reportByActiveStates(state, reportingDateBegin);
            
            for(Map<String, Object> rParams : reportParams) {
              String stateName = rParams.get("stateName").toString();
              String facilityName = rParams.get("facilityName").toString();
              List<Integer> tld = (List<Integer>) rParams.get("tld");
              List<Integer> lpv = (List<Integer>) rParams.get("lpv/r");
              List<Integer> dmoc = (List<Integer>) rParams.get("dmoc");
              List<Integer> tx_new = (List<Integer>) rParams.get("tx_new");
              List<Integer> tx_ml1 = (List<Integer>) rParams.get("tx_ml1");
              List<Integer> tx_ml2 = (List<Integer>) rParams.get("tx_ml2");
              List<Integer> tx_ml3 = (List<Integer>) rParams.get("tx_ml3");
              List<Integer> tx_ml4 = (List<Integer>) rParams.get("tx_ml4");
                
                cellnum = 0;
                row = sheet.createRow(rownum++);
                cell = row.createCell(cellnum++);
                cell.setCellValue(stateName);

                cell = row.createCell(cellnum++);
                cell.setCellValue(facilityName);

                for(int tl : tld){
                    cell = row.createCell(cellnum++);
                    cell.setCellValue(tl);
                }

                for(int lp : lpv){
                    cell = row.createCell(cellnum++);
                    cell.setCellValue(lp);
                }

                for(int dm : dmoc){
                    cell = row.createCell(cellnum++);
                    cell.setCellValue(dm);
                }

                for(int tx : tx_new){
                    cell = row.createCell(cellnum++);
                    cell.setCellValue(tx);
                }
                
                for(int tx : tx_ml1){
                    cell = row.createCell(cellnum++);
                    cell.setCellValue(tx);
                }
                
                for(int tx : tx_ml2){
                    cell = row.createCell(cellnum++);
                    cell.setCellValue(tx);
                }
                
                for(int tx : tx_ml3){
                    cell = row.createCell(cellnum++);
                    cell.setCellValue(tx);
                }
                
                for(int tx : tx_ml4){
                    cell = row.createCell(cellnum++);
                    cell.setCellValue(tx);
                }

            } 

            String directory = contextPath+"transfer/";
            
            FileUtil fileUtil = new FileUtil();
            fileUtil.makeDir(directory);
            fileUtil.makeDir(request.getContextPath()+"/transfer/");
            
            fileName = "treatment_summary.xlsx";
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

    private void reportByActiveStates(Long state_id, String reportingDateBegin){
        try {
           
            if(state_id == null)
                query = "SELECT state_id, name FROM state WHERE state_id IN (2,3,4,5,6,8,9,12,18,20,25,33,36) ORDER BY name ASC";                                        
            else
                query = "SELECT state_id, name FROM state WHERE state_id = "+state_id+" ORDER BY name ASC";                                        
            jdbcTemplate.query(query, (resultSet) -> {
                  while(resultSet.next()) {
                String stateName = resultSet.getString("name");
                Long stateId = resultSet.getLong("state_id");
                
                //Perform the export for all facilities in this state...
                System.out.println("Started for: "+stateName);
                facilityByFacility(stateName, stateId, reportingDateBegin);
            }
                return null; //To change body of generated lambdas, choose Tools | Templates.
            });
                 
        }
        catch (Exception exception) {
            exception.printStackTrace();
         
        }
    }

    private void facilityByFacility(String stateName, long stateId, String reportingDateBegin){
      
       
        try {
          
            String query1 = "SELECT facility_id, name FROM facility WHERE facility_id IN (SELECT DISTINCT facility_id FROM patient) AND state_id = ? ORDER BY name ASC";                                        

         
            jdbcTemplate.query(query, (resultSet1) -> {
                  while(resultSet1.next()) {
                  Map<String, Object> result = new HashMap<>();
                String facilityName = resultSet1.getString("name");
                Long facilityId = resultSet1.getLong("facility_id");
                
                //Perform the Calculation and plug into the Excel Sheet
                System.out.println("Started for Facility Name: "+facilityName+" and id: "+facilityId);
                //Put Basic Information...
                result.put("stateName", stateName);
                result.put("facilityName", facilityName);

                //For TLD
                result.put("tld", iMReport.getTld(facilityId, stateId, reportingDateBegin));

                //For Lpv/r
                result.put("lpv/r", iMReport.getLpv(facilityId, reportingDateBegin));

                //For DMOC
                result.put("dmoc", iMReport.getDmoc(facilityId, reportingDateBegin));

                //For TX_NEW
                result.put("tx_new", iMReport.getTxNew(facilityId, reportingDateBegin));
                
                //For TX_NEW
                result.put("tx_ml1", iMReport.getTxML1(facilityId, reportingDateBegin));
                
                //For TX_NEW
                result.put("tx_ml2", iMReport.getTxML2(facilityId, reportingDateBegin));
                
                //For TX_NEW
                result.put("tx_ml3", iMReport.getTxML3(facilityId, reportingDateBegin));
                
                //For TX_NEW
                result.put("tx_ml4", iMReport.getTxML4(facilityId, reportingDateBegin));
                
                reportParams.add(result);
            }
                return null;
            },stateId);
                  
        }
        catch (Exception exception) {
            exception.printStackTrace();
        
        }
    }
            
}
