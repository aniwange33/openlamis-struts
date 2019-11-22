/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author iduruanyanwu
 */
package org.fhi360.lamis.converter;
import org.fhi360.lamis.utility.FileUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class SummaryFormConverter {
    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private long userId;
    
    public SummaryFormConverter() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();        
    } 
    
//    public void convertByOption(){
//        
//    }
    
    public synchronized String convertExcel() {
        String fileName = "";
        int global_row = 0;
        
        DateFormat dateFormatExcel = new SimpleDateFormat("dd-MMM-yyyy");      
        String contextPath = ServletActionContext.getServletContext().getInitParameter("contextPath");

        String facilityIds = request.getParameter("facilityIds");
        String yearId = request.getParameter("year");
        String state = request.getParameter("state").toLowerCase();
        userId = (Long) session.getAttribute("userId");
        
        SXSSFWorkbook workbook = new SXSSFWorkbook(-1);  // turn off auto-flushing and accumulate all rows in memory
        Sheet sheet = workbook.createSheet();
        
        try{
            
            //We Define the first row, ie the headers...
            int rownum[] = {0};
            int cellnum[] = {0};
            Row[] row = {sheet.createRow(rownum[0]++)};
            Cell[] cell = {row[0].createCell(cellnum[0]++)};
            cell[0].setCellValue("FACILITY");
            
            //Build the Excel file headers...
            ArrayList<Integer> month_array = new ArrayList<>();
            for(int i = 1; i< 13; i++){
                month_array.add(i);
              
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("PRE_ART_"+getMonthMap().get(i).toString().toUpperCase());
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("TX_NEW_"+getMonthMap().get(i).toString().toUpperCase());
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("TX_CURR_"+getMonthMap().get(i).toString().toUpperCase());
            }
            
            System.out.println("Facility IDs are: "+facilityIds);
            
            //Algorithm for SQL Query begins here...
            ArrayList<String> facility_names = new ArrayList<>();
            
            //1. Select the distinct facility ids...
            if(facilityIds == "")
                query = "SELECT DISTINCT facility.name as facility FROM facility JOIN patient ON patient.facility_id = facility.facility_id WHERE facility.active ORDER by facility.name ASC";
            else
                query = "SELECT DISTINCT facility.name as facility FROM facility JOIN patient ON patient.facility_id = facility.facility_id WHERE facility.active AND facility.facility_id IN("+facilityIds+") ORDER by facility.name ASC";
                
        jdbcTemplate.query(query, (resultSet) -> {
              while(resultSet.next()) {              
                
                row[0] =sheet.createRow(rownum[0]++);
                 cell[0] = row[0].createCell(cellnum[0]++);
                 cell[0].setCellValue(resultSet.getString("facility"));
                facility_names.add(resultSet.getString("facility"));
             }
            return null; //To change body of generated lambdas, choose Tools | Templates.
        });
          
            
             
            //2. Select the various parameters to add...
            //Perform this in a loop for each month...
            int inter_num[] = {0};
            for(int ii = 0; ii< 12; ii++){
                
                //PRE_ART
                int i = ii + 1;
                //System.out.println("The Count Now is: "+i);
                //System.out.println("The Count Now is: "+i+" and this is first");
                query = "SELECT DISTINCT facility.name as facility, count(patient.status_registration) as count FROM patient JOIN facility ON patient.facility_id = facility.facility_id WHERE YEAR(patient.date_registration) = '" + yearId + "' AND MONTH(patient.date_registration) = '" + month_array.get(ii)+"' AND patient.status_registration = 'HIV+ non ART' AND facility.active GROUP BY facility.name ORDER by facility.name ASC";
              jdbcTemplate.query(query, (resultSet) -> {
                      Map<String, Integer> countsMap = new HashMap<>();
                while(resultSet.next()) {
                    countsMap.put(resultSet.getString("facility"), resultSet.getInt("count"));
                    //System.out.println(resultSet.getString("facility") +" - "+resultSet.getInt("count"));
                }
                  
                cellnum[0] = i + inter_num[0];
                for(int j = 0; j<facility_names.size(); j++){
                    //row = sheet.createRow(rownum++);                  
                     cell[0] = sheet.getRow(j + 1).createCell(cellnum[0]);
                    //System.out.println(countsMap.get(facility_names.get(j)));
                    //System.out.println(sheet.getRow(j + 1).getCell(0).getStringCellValue());
                    if(countsMap.get(facility_names.get(j)) != null){
                         cell[0].setCellValue(countsMap.get(facility_names.get(j)));
                    }else{
                         cell[0].setCellValue(0);
                    }
                } 
                  return null;
              });
                
            
              
                
                //TX_NEW
                //System.out.println("The Count Now is: "+i+" and this is second");
                inter_num[0]++;
                query = "SELECT DISTINCT facility.name as facility, count(status_registration) as count FROM patient JOIN facility ON patient.facility_id = facility.facility_id WHERE YEAR(date_started) = '"+yearId+"' AND MONTH(date_started) = '" + month_array.get(ii)+"' AND status_registration='ART Transfer In' AND facility.active GROUP BY facility.name ORDER by facility.name ASC";
             jdbcTemplate.query(query, (resultSet) -> {
                    Map<String, Integer> countsMap_2 = new HashMap<>();
                while(resultSet.next()) {
                    countsMap_2.put(resultSet.getString("facility"), resultSet.getInt("count"));
                }
                
                cellnum[0] = i + inter_num[0];
                for(int j = 0; j<facility_names.size(); j++){
                   
                     cell[0] = sheet.getRow(j + 1).createCell(cellnum[0]);
                    
                    if(countsMap_2.get(facility_names.get(j)) != null ){
                         cell[0].setCellValue(countsMap_2.get(facility_names.get(j)));
                    }else{
                         cell[0].setCellValue(0);
                    }
                } 
                
                 return null; //To change body of generated lambdas, choose Tools | Templates.
             });
             
                //TX_CURR
                
                String period = workMonth(yearId, getMonthMap().get(month_array.get(ii)));
                inter_num[0]++;
                query = "SELECT DISTINCT facility.name as facility, count(patient.current_status) as count FROM patient JOIN facility ON patient.facility_id = facility.facility_id WHERE ((current_status IN ('ART Start', 'ART Restart', 'ART Transfer In')) OR (current_status IN ('ART Transfer Out', 'Lost to Follow Up', 'Stopped Treatment', 'Known Death') AND date_current_status > '"+period+"')) AND date_started IS NOT NULL AND date_started <= '"+period+"' AND facility.active GROUP BY facility.name ORDER by facility.name ASC";
               jdbcTemplate.query(query, (resultSet) -> {
                     Map<String, Integer> countsMap_3 = new HashMap<>();
                while(resultSet.next()) {
                    countsMap_3.put(resultSet.getString("facility"), resultSet.getInt("count"));
                }
                
                cellnum[0] = i + inter_num[0];
                for(int j = 0; j<facility_names.size(); j++){
                    //row = sheet.createRow(rownum++);
                     cell[0] = sheet.getRow(j + 1).createCell(cellnum[0]);
                    //System.out.println(countsMap_3.get(facility_names.get(j)));
                    //System.out.println(sheet.getRow(j + 1).getCell(0).getStringCellValue());
                    if(countsMap_3.get(facility_names.get(j)) != null){
                         cell[0].setCellValue(countsMap_3.get(facility_names.get(j)));
                    }else{
                         cell[0].setCellValue(0);
                    }    
                }
                   return null; //To change body of generated lambdas, choose Tools | Templates.
               });
               
            }
            
            //Flusher
            if(rownum[0] % 100 == 0) {
                ((SXSSFSheet)sheet).flushRows(100); // retain 100 last rows and flush all others

                // ((SXSSFSheet)sheet).flushRows() is a shortcut for ((SXSSFSheet)sheet).flushRows(0),
                // this method flushes all rows
            }    

            String directory = contextPath+"transfer/";
            
            FileUtil fileUtil = new FileUtil();
            fileUtil.makeDir(directory);
            fileUtil.makeDir(request.getContextPath()+"/transfer/");
            
            fileName = "summary_form_"+yearId+"_"+Long.toString(userId)+".xlsx";
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
    
    
    private Map<Integer, String> getMonthMap(){
        
        // Initialize the Month Map...
        Map<Integer, String> month_map = new HashMap<>();
        month_map.put(1, "Jan"); month_map.put(2, "Feb"); month_map.put(3, "Mar");
        month_map.put(4, "Apr"); month_map.put(5, "May"); month_map.put(6, "Jun"); 
        month_map.put(7, "Jul"); month_map.put(8, "Aug"); month_map.put(9, "Sep"); 
        month_map.put(10, "Oct"); month_map.put(11, "Nov"); month_map.put(12, "Dec"); 
            
        return month_map;
    }
    
    private Map<String, Integer> getReverseMonthMap(){
        
        // initialize the Month Map...
        Map<String, Integer> month_map = getMonthMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
            
        return month_map;
    }
    
    private String workMonth(String year, String month){
        String return_value = year;
        
        try{
            if(month == "Apr" || month == "Jun" || month == "Sep" || month == "Nov"){
                if(getReverseMonthMap().get(month) < 10)
                    return_value +="-0"+getReverseMonthMap().get(month)+"-30";
                else if(getReverseMonthMap().get(month) >= 10)
                    return_value +="-"+getReverseMonthMap().get(month)+"-30";
            }else if(month == "Feb"){
                if(Integer.valueOf(year) % 4 == 0){ //leap year      
                     if(getReverseMonthMap().get(month) < 10)
                        return_value +="-0"+getReverseMonthMap().get(month)+"-29";
                     else if(getReverseMonthMap().get(month) >= 10)
                        return_value +="-"+getReverseMonthMap().get(month)+"-29";
                }else{
                    if(getReverseMonthMap().get(month) < 10)
                        return_value +="-0"+getReverseMonthMap().get(month)+"-28";
                    else if(getReverseMonthMap().get(month) >= 10)
                        return_value +="-"+getReverseMonthMap().get(month)+"-28";
                }
            }else if(month != "Apr" && month != "Jun" && month != "Sep" && month != "Nov" && month != "Feb"){
                if(getReverseMonthMap().get(month) < 10)
                    return_value +="-0"+getReverseMonthMap().get(month)+"-31";
                else if(getReverseMonthMap().get(month) >= 10)
                    return_value +="-"+getReverseMonthMap().get(month)+"-31";
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        
        return return_value;
    }
}