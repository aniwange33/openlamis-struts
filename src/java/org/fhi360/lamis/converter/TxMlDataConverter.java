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
import java.util.HashMap;
import java.util.Map;
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
import org.fhi360.lamis.utility.Scrambler;
import org.apache.commons.lang3.StringUtils;
import org.fhi360.lamis.exchange.radet.RegimenResolver;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;


public class TxMlDataConverter {
    private HttpServletRequest request;
    private HttpSession session;
    private long userId;
    private Boolean viewIdentifier = false;
    private Scrambler scrambler;
    private RegimenResolver regimenResolver;  
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
   
    private String regimentype1, regimentype2, regimen1, regimen2;
    private long facilityId, patientId;
    
    public TxMlDataConverter() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();        
        this.scrambler = new Scrambler();
        this.regimenResolver = new RegimenResolver();
        if(ServletActionContext.getRequest().getParameter("viewIdentifier") != null && !ServletActionContext.getRequest().getParameter("viewIdentifier").equals("false")) {
            this.viewIdentifier = true;                        
        }

        this.facilityId = (Long) session.getAttribute("facilityId");
    } 
    
    public synchronized String convertExcel() {
        String fileName = "";        
        DateFormat dateFormatExcel = new SimpleDateFormat("dd-MMM-yyyy");      
        String contextPath = ServletActionContext.getServletContext().getInitParameter("contextPath");
        userId = (Long) session.getAttribute("userId");
        
        SXSSFWorkbook workbook = new SXSSFWorkbook(-1);  // turn off auto-flushing and accumulate all rows in memory
        workbook.setCompressTempFiles(true); // temp files will be gzipped
        Sheet sheet = workbook.createSheet();        
        try{
            
            int[] rownum = {0};
            int[] cellnum = {0};
            Row[] row = {sheet.createRow(rownum[0]++)};
            Cell[] cell = {row[0].createCell(cellnum[0]++)};
            cell[0].setCellValue("Facility Id");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Facility Name");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("State");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("LGA");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Patient Id");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Hospital Num");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Unique ID");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Surname");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Other Names");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date Birth");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Age");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Age Unit");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Gender");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Marital Status");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Education");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Occupation");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("State of Residence");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Lga of Residence");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Address");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Phone");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Care Entry Point");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date of Confirmed HIV Test");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date Registration");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Status at Registration");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Current Status");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date Current Status");
            cell[0] = row[0].createCell(cellnum[0]++);
            
            //ARV information
            cell[0].setCellValue("ART Start Date");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Current Regimen Line");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Current Regimen");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date of Last Refill");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Last Refill Duration (days)");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date of Next Refill");

            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date of Last Clinic");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Last Clinic Stage");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date of Next Clinic");
            
            //Contact tracking information
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date of Tracking");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Tracking Outcome");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Cause of Death");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date Agreed to Return");
            
            //VL information
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Last Viral Load");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Date of Last Viral Load");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Viral Load Due Date");
            cell[0] = row[0].createCell(cellnum[0]++);
            cell[0].setCellValue("Viral Load Type");

            String query = "SELECT patient.*, DATEDIFF(YEAR, date_birth, CURDATE()) AS age FROM patient WHERE facility_id = " + facilityId + " AND outcome IN ('" + Constants.TxMlStatus.TX_ML_DIED + "', '" + Constants.TxMlStatus.TX_ML_TRANSFER + "', '" +  Constants.TxMlStatus.TX_ML_TRACED + "', '" + Constants.TxMlStatus.TX_ML_NOT_TRACED + "')";
            System.out.println("string query ..........."+query);
            String outcome = request.getParameter("outcome").trim();
           
            if(outcome.equalsIgnoreCase("All")) query = "SELECT patient.*, DATEDIFF(YEAR, date_birth, CURDATE()) AS age FROM patient WHERE facility_id = " + facilityId + " AND outcome IN ('" + Constants.TxMlStatus.TX_ML_DIED + "', '" + Constants.TxMlStatus.TX_ML_TRANSFER + "', '" +  Constants.TxMlStatus.TX_ML_TRACED + "', '" + Constants.TxMlStatus.TX_ML_NOT_TRACED + "') ORDER BY current_status"; 
            if(outcome.equalsIgnoreCase(Constants.TxMlStatus.TX_ML_DIED)) query = "SELECT patient.*, DATEDIFF(YEAR, date_birth, CURDATE()) AS age FROM patient WHERE facility_id = " + facilityId + " AND outcome = '" + Constants.TxMlStatus.TX_ML_DIED + "'";
            if(outcome.equalsIgnoreCase(Constants.TxMlStatus.TX_ML_TRANSFER)) query = "SELECT patient.*, DATEDIFF(YEAR, date_birth, CURDATE()) AS age FROM patient WHERE facility_id = " + facilityId + " AND outcome = '" + Constants.TxMlStatus.TX_ML_TRANSFER + "'";
            if(outcome.equalsIgnoreCase(Constants.TxMlStatus.TX_ML_TRACED)) query = "SELECT patient.*, DATEDIFF(YEAR, date_birth, CURDATE()) AS age FROM patient WHERE facility_id = " + facilityId + " AND outcome = '" + Constants.TxMlStatus.TX_ML_TRACED + "'";
            if(outcome.equalsIgnoreCase(Constants.TxMlStatus.TX_ML_NOT_TRACED)) query = "SELECT patient.*, DATEDIFF(YEAR, date_birth, CURDATE()) AS age FROM patient WHERE facility_id = " + facilityId + " AND outcome = '" + Constants.TxMlStatus.TX_ML_NOT_TRACED + "'";

            jdbcTemplate.query(query, resultSet -> {       
            patientId = 0;
            while(resultSet.next()) {
                regimentype1 = "";
                regimentype2 = "";
                regimen1 = "";
                regimen2 = "";
                executeUpdate("DROP TABLE IF EXISTS commence");        
                executeUpdate("CREATE TEMPORARY TABLE commence AS SELECT * FROM clinic WHERE facility_id = " + facilityId + " AND commence = 1");
                executeUpdate("CREATE INDEX idx_visit ON commence(patient_id)");             
                if(resultSet.getLong("facility_id") != facilityId || resultSet.getLong("patient_id") != patientId) {
                    cellnum[0] = 0;
                    row[0] = sheet.createRow(rownum[0]++);
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getLong("facility_id"));
                    facilityId = resultSet.getLong("facility_id"); 
                    Map facility = getFacility(facilityId);
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((String) facility.get("facilityName"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((String) facility.get("state"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue((String) facility.get("lga"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getLong("patient_id"));
                    patientId = resultSet.getLong("patient_id"); 
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("hospital_num"));
                    cell[0] = row[0].createCell(cellnum[0]++);
                    cell[0].setCellValue(resultSet.getString("unique_id"));
                }
                cell[0] = row[0].createCell(cellnum[0]++);
                String surname = resultSet.getObject("surname") == null ? "" : resultSet.getString("surname");
                surname = (viewIdentifier)? scrambler.unscrambleCharacters(surname) : surname;
                surname = StringUtils.upperCase(surname);                
                cell[0].setCellValue(surname);
                cell[0] = row[0].createCell(cellnum[0]++);
                String otherNames = resultSet.getObject("other_names") == null ? "" : resultSet.getString("other_names");
                otherNames = (viewIdentifier)? scrambler.unscrambleCharacters(otherNames) : otherNames;
                otherNames = StringUtils.capitalize(otherNames);
                cell[0].setCellValue(otherNames);
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue((resultSet.getDate("date_birth") == null)? "" : dateFormatExcel.format(resultSet.getDate("date_birth")));
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue(Integer.toString(resultSet.getInt("age")));
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue(resultSet.getString("age_unit"));
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue(resultSet.getString("gender"));
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue(resultSet.getString("marital_status"));
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue(resultSet.getString("education"));
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue(resultSet.getString("occupation"));
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue(resultSet.getString("state"));
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue(resultSet.getString("lga"));
                cell[0] = row[0].createCell(cellnum[0]++);
                String address = resultSet.getString("address") == null ? "" : resultSet.getString("address");
                address = (viewIdentifier)? scrambler.unscrambleCharacters(address) : address;
                address = StringUtils.capitalize(address);                               
                cell[0].setCellValue(address);
                cell[0] = row[0].createCell(cellnum[0]++);
                String phone = resultSet.getString("phone") == null ? "" :resultSet.getString("phone");
                phone = (viewIdentifier)? scrambler.unscrambleNumbers(phone) : phone;
                cell[0].setCellValue(phone);
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue(resultSet.getString("entry_point"));
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue(resultSet.getString("date_confirmed_hiv"));
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue((resultSet.getDate("date_registration") == null)? "" : dateFormatExcel.format(resultSet.getDate("date_registration")));
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue(resultSet.getString("status_registration"));
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue(resultSet.getString("current_status"));
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue((resultSet.getDate("date_current_status") == null)? "" : dateFormatExcel.format(resultSet.getDate("date_current_status")));
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue((resultSet.getDate("date_started") == null)? "" : dateFormatExcel.format(resultSet.getDate("date_started")));                
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue(resultSet.getString("regimentype") == null? "" : resultSet.getString("regimentype"));
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue(resultSet.getString("regimen") == null? "" : regimenResolver.getRegimen(resultSet.getString("regimen")));
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue((resultSet.getDate("date_last_refill") == null)? "" : dateFormatExcel.format(resultSet.getDate("date_last_refill")));
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue(Integer.toString(resultSet.getInt("last_refill_duration")));
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue((resultSet.getDate("date_next_refill") == null)? "" : dateFormatExcel.format(resultSet.getDate("date_next_refill")));

                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue((resultSet.getDate("date_last_clinic") == null)? "" : dateFormatExcel.format(resultSet.getDate("date_last_clinic")));
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue(resultSet.getString("last_clinic_stage"));
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue((resultSet.getDate("date_next_clinic") == null)? "" : dateFormatExcel.format(resultSet.getDate("date_next_clinic")));
                cell[0] = row[0].createCell(cellnum[0]++);


                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue((resultSet.getDate("date_tracked") == null)? "" : dateFormatExcel.format(resultSet.getDate("date_tracked")));
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue(resultSet.getString("outcome"));
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue(resultSet.getString("cause_death"));
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue((resultSet.getDate("agreed_date") == null)? "" : dateFormatExcel.format(resultSet.getDate("agreed_date")));
                cell[0] = row[0].createCell(cellnum[0]++);



                cell[0].setCellValue(Double.toString(resultSet.getDouble("last_viral_load")));
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue((resultSet.getDate("date_last_viral_load") == null)? "" : dateFormatExcel.format(resultSet.getDate("date_last_viral_load")));
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue((resultSet.getDate("viral_load_due_date") == null)? "" : dateFormatExcel.format(resultSet.getDate("viral_load_due_date")));
                cell[0] = row[0].createCell(cellnum[0]++);
                cell[0].setCellValue(resultSet.getString("viral_load_type"));
                
                if(rownum[0] % 100 == 0) {
                    try {
                        ((SXSSFSheet)sheet).flushRows(100); // retain 100 last rows and flush all others
                        
                        // ((SXSSFSheet)sheet).flushRows() is a shortcut for ((SXSSFSheet)sheet).flushRows(0),
                        // this method flushes all rows
                    } catch (IOException ex) {
                        Logger.getLogger(TxMlDataConverter.class.getName()).log(Level.SEVERE, null, ex);
                    }
               }                                
            } 
             return null;
            });

            String directory = contextPath+"transfer/";            
            FileUtil fileUtil = new FileUtil();
            fileUtil.makeDir(directory);
            fileUtil.makeDir(request.getContextPath()+"/transfer/");
            
            fileName = request.getParameterMap().containsKey("facilityIds")? "patient_"+request.getParameter("state").toLowerCase()+"_"+Long.toString(userId)+".xlsx" : "patient_"+Long.toString(userId)+".xlsx";
            FileOutputStream outputStream = new FileOutputStream(new File(directory+fileName));
            workbook.write(outputStream);
            outputStream.close();
            workbook.dispose();  // dispose of temporary files backing this workbook on disk
            
            //for servlets in the default(root) context, copy file to the transfer folder in root 
            if(!contextPath.equalsIgnoreCase(request.getContextPath())) fileUtil.copyFile(fileName, contextPath+"transfer/", request.getContextPath()+"/transfer/");                    
            //resultSet = null;                        
        }
        catch (Exception exception) {          
            exception.printStackTrace();
        }
        return "transfer/"+fileName;
    }
          
    private String getNrti(String regimen) {
        String nrti = regimen == null? "" : "Other";
        if(regimen.contains("d4T")) {
            nrti = "D4T (Stavudine)";
        }
        else {
            if(regimen.contains("AZT")) {
                nrti = "AZT (Zidovudine)";
            }
            else {
                if(regimen.contains("TDF")) {
                    nrti = "TDF (Tenofovir)";
                }
                else {
                    if(regimen.contains("DDI")) {
                        nrti = "DDI (Didanosine)";
                    }
                }
            }
        } 
        return nrti;
    }

    private String getNnrti(String regimen) {
        String nnrti = regimen == null? "" : "Other";
        if(regimen.contains("EFV")) {
            nnrti = " EFV (Efavirenz)";
        }
        else {
            if(regimen.contains("NVP")) {
                nnrti = " NVP (Nevirapine)";
            }
        } 
        return nnrti;
    }
    

    private Map getFacility(long facilityId) {
        Map<String, Object> facilityMap = new HashMap<String, Object>(); 
        try{
            // fetch the required records from the database
            String query = "SELECT DISTINCT facility.name, facility.address1, facility.address2, facility.phone1, facility.phone2, facility.email, lga.name AS lga, state.name AS state FROM facility JOIN lga ON facility.lga_id = lga.lga_id JOIN state ON facility.state_id = state.state_id WHERE facility_id = " + facilityId;
            jdbcTemplate.query(query, rs -> {
            if(rs.next()) {
                facilityMap.put("facilityName", rs.getString("name"));  
                facilityMap.put("lga", rs.getString("lga"));            
                facilityMap.put("state", rs.getString("state")); 
            }
            return null;
            });
        }
        catch (Exception exception) {
            
        }                
        return facilityMap;
    }

    private void executeUpdate(String query) {
        try {
            jdbcTemplate.execute(query);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }        
    }            

}
