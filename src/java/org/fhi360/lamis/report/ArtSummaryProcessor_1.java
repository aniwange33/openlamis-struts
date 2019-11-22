/**
 *
 * @author AALOZIE
 */

package org.fhi360.lamis.report;

import java.util.*;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.JDBCUtil;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.fhi360.lamis.report.indicator.ArtSummaryIndicators;

public class ArtSummaryProcessor_1 {
    private int reportingMonth;
    private int reportingYear;
    private String reportingDateBegin;
    private String reportingDateEnd;
    private int [][] value = new int[18][6];
    private String [] indicator = new String[18];

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private JDBCUtil jdbcUtil;
    private PreparedStatement preparedStatement;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    private ArrayList<Map<String, Object>> reportList;
    private HashMap parameterMap;
    private long facilityId;
    //private static final Log log = LogFactory.getLog(ArtSummaryProcessor.class);

    public ArtSummaryProcessor_1() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();        
    } 
    
    public ArrayList<Map<String, Object>> process(){
        reportList = new ArrayList<Map<String, Object>>();
        reportingMonth = DateUtil.getMonth(request.getParameter("reportingMonth"));
        reportingYear = Integer.parseInt(request.getParameter("reportingYear"));
        reportingDateBegin = dateFormat.format(DateUtil.getFirstDateOfMonth(reportingYear, reportingMonth));
        reportingDateEnd = dateFormat.format(DateUtil.getLastDateOfMonth(reportingYear, reportingMonth));
        facilityId = (Long) session.getAttribute("facilityId");
        ResultSet resultSet;
        indicator = new ArtSummaryIndicators().initialize();

        try {
            jdbcUtil = new JDBCUtil();   
            //Computing status at registration this reporting period
            query = "SELECT DISTINCT patient_id, gender, DATEDIFF(YEAR, date_birth, '" + reportingDateBegin + "') AS age, status_registration FROM patient WHERE facility_id = " + facilityId + " AND YEAR(date_registration) = " + reportingYear + " AND MONTH(date_registration) = " + reportingMonth; 
            preparedStatement = jdbcUtil.getStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String statusRegistration = resultSet.getString("status_registration");
                String gender = resultSet.getString("gender");
                int age = resultSet.getInt("age");
                
                if(statusRegistration.trim().equalsIgnoreCase("HIV+ non ART")) {
                    disaggregate(gender, age, 0); //newly enrolled into care this reporting month excluding transfer in 
                }

                if(statusRegistration.trim().equalsIgnoreCase("Pre-ART Transfer In")) {
                    disaggregate(gender, age, 2); //transfer in Pre-ART this reporting month 
                }

                if(statusRegistration.trim().equalsIgnoreCase("ART Transfer In")) {
                    disaggregate(gender, age, 10); //transfer in ART this reporting month 
                }
            }

            //Computing ART start this reporting period
            query = "SELECT DISTINCT patient_id, gender, DATEDIFF(YEAR, date_birth, '" + reportingDateBegin + "') AS age, date_started, status_registration FROM patient WHERE facility_id = " + facilityId + " AND YEAR(date_started) = " + reportingYear + " AND MONTH(date_started) = " + reportingMonth ; 
            preparedStatement = jdbcUtil.getStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String statusRegistration = resultSet.getString("status_registration");
                String gender = resultSet.getString("gender");
                int age = resultSet.getInt("age");
                if(!statusRegistration.trim().equalsIgnoreCase("ART Transfer In")) {
                    disaggregate(gender, age, 6); //started on ART this reporting month                     
                }
            }
            
            //Computing current status this reporting period
            query = "SELECT DISTINCT patient_id, current_status FROM statushistory WHERE facility_id = " + facilityId + " AND YEAR(date_current_status) = " + reportingYear + " AND MONTH(date_current_status) = " + reportingMonth;             
            preparedStatement = jdbcUtil.getStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long patientId = resultSet.getLong("patient_id");
                String currentStatus = resultSet.getString("current_status");
                
                query = "SELECT gender, DATEDIFF(YEAR, date_birth, '" + reportingDateBegin + "') AS age, date_started FROM patient WHERE patient_id = " + patientId;
                preparedStatement = jdbcUtil.getStatement(query);
                ResultSet rs = preparedStatement.executeQuery();
                if(rs.next()) {
                    String dateStarted = (rs.getDate("date_started") == null)? "" :  DateUtil.parseDateToString(rs.getDate("date_started"), "MM/dd/yyyy");
                    String gender = rs.getString("gender");
                    int age = rs.getInt("age");

                    if(currentStatus.trim().equalsIgnoreCase("Pre-ART Transfer Out")) {
                        disaggregate(gender, age, 3); //transfer out Pre-ART this reporting month 
                    }

                    if(currentStatus.trim().equalsIgnoreCase("Lost to Follow Up") && dateStarted.isEmpty()) {
                        disaggregate(gender, age, 4); //lost to follow up Pre-ART this reporting month 
                    }

                    if(currentStatus.trim().equalsIgnoreCase("Known Death") && dateStarted.isEmpty()) {
                        disaggregate(gender, age, 5); //known death Pre-ART this reporting month 
                    }

                    if(currentStatus.trim().equalsIgnoreCase("Stopped Treatment") && !dateStarted.isEmpty()) {
                        disaggregate(gender, age, 11); //stopped ART this reporting month 
                    } 

                    if(currentStatus.trim().equalsIgnoreCase("Lost to Follow Up") && !dateStarted.isEmpty()) {
                        disaggregate(gender, age, 12); //lost to follow up ART this reporting month 
                    }

                    if(currentStatus.trim().equalsIgnoreCase("Known Death") && !dateStarted.isEmpty()) {
                        disaggregate(gender, age, 13); //known death ART this reporting month 
                    }

                    if(currentStatus.trim().equalsIgnoreCase("ART Transfer Out")) {
                        disaggregate(gender, age, 14); //transfer out ART this reporting month 
                    }

                    if(currentStatus.trim().equalsIgnoreCase("ART Restart")) {
                        disaggregate(gender, age, 15); //restart ART this reporting month 
                    }                                    
                }
            }
            
            //Computing regimen distribution this reporting period
            //query = "SELECT DISTINCT patient_id, gender, date_birth, DATEDIFF(YEAR, date_birth, '" + reportingDateBegin + "') AS age, date_registration, status_registration, current_status, date_current_status, date_started FROM patient WHERE facility_id = " + facilityId + " AND current_status IN ('ART Start', 'ART Restart', 'ART Transfer In') AND DATEDIFF(DAY, date_last_refill + last_refill_duration, '" + reportingDateEnd + "') <= 90 AND date_started IS NOT NULL ORDER BY current_status"; 
            query = "SELECT DISTINCT patient_id, gender, DATEDIFF(YEAR, date_birth, '" + reportingDateBegin + "') AS age, date_registration, status_registration FROM patient WHERE facility_id = " + facilityId + " AND ((current_status IN ('ART Start', 'ART Restart', 'ART Transfer In')) OR (current_status IN ('ART Transfer Out', 'Lost to Follow Up', 'Stopped Treatment', 'Known Death') AND date_current_status > '" + reportingDateEnd + "')) AND date_started IS NOT NULL AND date_started <= '" + reportingDateEnd + "'"; 
            preparedStatement = jdbcUtil.getStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long patientId = resultSet.getLong("patient_id");
                String gender = resultSet.getString("gender");
                int age = resultSet.getInt("age");                
                String regimentype = getCurrentRegimen(patientId);

                if(regimentype.trim().equalsIgnoreCase("Third Line")) {
                    disaggregate(gender, age, 9); //currently on third ARV
                }
                else {
                    if(regimentype.trim().equalsIgnoreCase("ART Second Line Adult") || regimentype.trim().equalsIgnoreCase("ART Second Line Children")) {
                        disaggregate(gender, age, 8); //currently on 2nd line
                    }
                    else {
                        disaggregate(gender, age, 7); //currently on 1st line                        
                    }
                }
            }
            
            //Computing cummulative enrolled this reporting period
            query = "SELECT DISTINCT patient_id, gender, DATEDIFF(YEAR, date_birth, '" + reportingDateBegin + "') AS age FROM patient WHERE facility_id = " + facilityId + " AND status_registration NOT IN ('Pre-ART Transfer In', 'ART Transfer In') AND date_registration <= '" + reportingDateEnd + "'"; 
            preparedStatement = jdbcUtil.getStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String gender = resultSet.getString("gender");
                int age = resultSet.getInt("age");
                disaggregate(gender, age, 1); //cummulatievly enrolled into care this reporting month excluding transfer in                        
              }
            
            //Computing pregnant women enrolled            
            query = "SELECT DISTINCT patient.patient_id, patient.gender, DATEDIFF(YEAR, patient.date_birth, patient.date_registration) AS age, patient.status_registration "
                    + " FROM patient JOIN clinic ON patient.patient_id = clinic.patient_id WHERE patient.facility_id = " + facilityId + " AND clinic.facility_id = " + facilityId + " AND YEAR(patient.date_registration) = " + reportingYear + " AND MONTH(patient.date_registration) = " + reportingMonth + " AND patient.status_registration = 'HIV+ non ART' AND YEAR(clinic.date_visit) = " + reportingYear + " AND MONTH(clinic.date_visit) = " + reportingMonth + " AND clinic.pregnant = 1"; 
            preparedStatement = jdbcUtil.getStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String gender = resultSet.getString("gender");
                int age = resultSet.getInt("age");
                disaggregate(gender, age, 16); //HIV+ pregnant women newly enrolled 
            }
            
            query = "SELECT DISTINCT patient.patient_id, patient.gender, DATEDIFF(YEAR, patient.date_birth, patient.date_registration) AS age "
                    + " FROM patient JOIN clinic ON patient.patient_id = clinic.patient_id WHERE patient.facility_id = " + facilityId + " AND clinic.facility_id = " + facilityId + " AND YEAR(clinic.date_visit) = " + reportingYear + " AND MONTH(clinic.date_visit) = " + reportingMonth + " AND clinic.pregnant = 1 AND clinic.commence = 1"; 
            preparedStatement = jdbcUtil.getStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String gender = resultSet.getString("gender");
                int age = resultSet.getInt("age");
                disaggregate(gender, age, 17); //HIV+ pregnant women initiated on ART 
            }
            
            //Populating indicator values
            for(int i = 0; i < indicator.length; i++) {
                String male1 = Integer.toString(value[i][0]);
                String male2 = Integer.toString(value[i][1]);
                String male3 = Integer.toString(value[i][2]);
                String fmale1 = Integer.toString(value[i][3]);
                String fmale2 = Integer.toString(value[i][4]);
                String fmale3 = Integer.toString(value[i][5]);
                
                int total = value[i][0]+value[i][1]+value[i][2]+value[i][3]+value[i][4]+value[i][5];

                // create map of values 
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("sno", "ART"+Integer.toString(i+1));
                map.put("indicator", indicator[i]);
                map.put("male1", male1);
                map.put("male2", male2);
                map.put("male3", male3);
                map.put("fmale1", fmale1);
                map.put("fmale2", fmale2);
                map.put("fmale3", fmale3);
                map.put("total", Integer.toString(total));
                reportList.add(map);
            }            
            resultSet = null;
        }
        catch (Exception exception) {
            resultSet = null;
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }          
        return reportList;
    }
   
    public HashMap getReportParameters(){
        int tbART1 = 0;
        int tbPreART1 = 0;
        int tbART2 = 0;
        int tbPreART2 = 0;
        int tbART3 = 0;
        int tbPreART3 = 0;
        int pregnantEnrol = 0;
        
        parameterMap = new HashMap();
        reportingMonth = DateUtil.getMonth(request.getParameter("reportingMonth"));
        reportingYear = Integer.parseInt(request.getParameter("reportingYear"));
        reportingDateBegin = dateFormat.format(DateUtil.getFirstDateOfMonth(reportingYear, reportingMonth));
        reportingDateEnd = dateFormat.format(DateUtil.getLastDateOfMonth(reportingYear, reportingMonth));
        facilityId = (Long) session.getAttribute("facilityId");
        ResultSet resultSet;

        try {
            jdbcUtil = new JDBCUtil();
            query = "SELECT DISTINCT patient.patient_id, patient.gender, DATEDIFF(YEAR, patient.date_birth, '" + reportingDateBegin + "') AS age, patient.date_started, clinic.pregnant, clinic.tb_status, clinic.date_visit "
                   + " FROM patient JOIN clinic ON patient.patient_id = clinic.patient_id WHERE patient.facility_id = " + facilityId + " AND clinic.facility_id = " + facilityId + " AND clinic.tb_status IS NOT NULL AND YEAR(clinic.date_visit) = " + reportingYear + " AND MONTH(clinic.date_visit) = " + reportingMonth; 
            preparedStatement = jdbcUtil.getStatement(query);
            resultSet = preparedStatement.executeQuery();            
            while (resultSet.next()) {
                String tbStatus = resultSet.getString("tb_status");
                String dateStarted = (resultSet.getDate("date_started") == null)? "" :  DateUtil.parseDateToString(resultSet.getDate("date_started"), "MM/dd/yyyy");
                int pregnant = resultSet.getInt("pregnant");
                
                if(!tbStatus.trim().isEmpty()) {
                    if(!dateStarted.isEmpty()) {
                        tbART1++;  //screened for TB ART
                    }
                    else {
                        tbPreART1++;  //screened for TB Pre-ART
                    }
                }
                if(tbStatus.trim().equals("Currently on TB treatment")) {
                    if(!dateStarted.isEmpty()) {
                        tbART2++;  //commenced on TB ART
                    }
                    else {
                        tbPreART2++;  //commenced on TB Pre-ART
                    }
                }                
                if(tbStatus.trim().equals("Currently on INH prophylaxis")) {
                    if(!dateStarted.isEmpty()) {
                        tbART3++;  //on INH prophylaxis ART
                    }
                    else {
                        tbPreART3++;  //on INH prophylaxis Pre-ART
                    }
                }
                if(pregnant == 1 && !dateStarted.isEmpty()) {
                    if(Integer.parseInt(dateStarted.substring(0,2)) == reportingMonth && Integer.parseInt(dateStarted.substring(6)) == reportingYear) {
                        pregnantEnrol++;  //pregnant women newly enrolled into ART
                    }                    
                }
            }
            parameterMap.put("tbART1", Integer.toString(tbART1));            
            parameterMap.put("tbPreART1", Integer.toString(tbPreART1));            
            parameterMap.put("tbART2", Integer.toString(tbART2));            
            parameterMap.put("tbPreART2", Integer.toString(tbPreART2));            
            parameterMap.put("tbART3", Integer.toString(tbART3));            
            parameterMap.put("tbPreART3", Integer.toString(tbPreART3)); 
            parameterMap.put("pregnantEnrol", Integer.toString(pregnantEnrol)); 
            parameterMap.put("reportingMonth",  request.getParameter("reportingMonth")); 
            parameterMap.put("reportingYear", request.getParameter("reportingYear")); 
            
            query = "SELECT DISTINCT facility.name, facility.address1, facility.address2, facility.phone1, facility.phone2, facility.email, lga.name AS lga, state.name AS state FROM facility JOIN lga ON facility.lga_id = lga.lga_id JOIN state ON facility.state_id = state.state_id WHERE facility_id = " + facilityId;
            preparedStatement = jdbcUtil.getStatement(query);
            resultSet = preparedStatement.executeQuery();
            
            if(resultSet.next()) {
                parameterMap.put("facilityName", resultSet.getString("name"));  
                parameterMap.put("lga", resultSet.getString("lga"));            
                parameterMap.put("state", resultSet.getString("state")); 
            }
            resultSet = null;
        }
        catch (Exception exception) {
            resultSet = null;
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }         
        return parameterMap;
    }
    
    private void disaggregate (String gender, int age, int i) {
        if(gender.trim().equalsIgnoreCase("Male")) {
            if(age < 1) {
                value[i][0]++;  //males < 1yr
            }
            else {
                if(age >= 1 && age < 15) {
                    value[i][1]++;  //males 1-14yrs
                }
                else {
                    value[i][2]++;  //males => 15yrs
                }
            }   
        } 
        else {
            if(age < 1) {
                value[i][3]++;  //fmales < 1yr
            }
            else {
                if(age >= 1 && age < 15) {
                    value[i][4]++;  //fmales 1-14yrs
                }
                else {
                    value[i][5]++;  //fmales => 15yrs
                }
            }                               
        }
    }
    
    private void executeUpdate(String query) {
        try {
            jdbcUtil = new JDBCUtil();
            preparedStatement = jdbcUtil.getStatement(query);
            preparedStatement.executeUpdate();
        }
        catch (Exception exception) {
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }        
    } 
    
    private Map getCurrentStatus(long patientId) {
        Map<String, Object> map = new HashMap<String, Object>();
        ResultSet rs;
        String currentStatus = "";
        String dateStatus = "";
        try {
            jdbcUtil = new JDBCUtil();
            query = "SELECT DISTINCT current_status, date_current_status FROM statushistory WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_current_status = (SELECT MAX(date_current_status) FROM statushistory WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_current_status <= '" + reportingDateEnd + "') ORDER BY date_current_status DESC";  
            preparedStatement = jdbcUtil.getStatement(query);
            rs = preparedStatement.executeQuery();
            if(rs.next()) {
                currentStatus = rs.getString("current_status") == null ? "" : rs.getString("current_status");
                dateStatus = (rs.getDate("date_current_status") == null)? "" :  DateUtil.parseDateToString(rs.getDate("date_current_status"), "MM/dd/yyyy");
            }
            map.put("currentStatus", currentStatus);
            map.put("dateStatus", dateStatus);            
        }
        catch (Exception exception) {
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }
        return map;
    }
    
    private String getCurrentRegimen(long patientId) {
        ResultSet rs;
        String regimentype = "";
        try {
            jdbcUtil = new JDBCUtil();
            query = "SELECT DISTINCT regimentype FROM regimenhistory WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_visit = (SELECT MAX(date_visit) FROM regimenhistory WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_visit <= '" + reportingDateEnd + "')";  
            preparedStatement = jdbcUtil.getStatement(query);
            rs = preparedStatement.executeQuery();
            while(rs.next()) {
                regimentype = rs.getString("regimentype");
                if(regimentype.contains("ART First Line") || regimentype.contains("ART Second Line") || regimentype.contains("Salvage Therapy") || regimentype.contains("ART Third Line")) break;
            }
        }
        catch (Exception exception) {
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }     
        return regimentype;
    }
}


//create a temporary table of date of the latest status change on or before the last day of reporting month 
//executeUpdate("DROP TABLE IF EXISTS currentstatus");        
//query = "CREATE TEMPORARY TABLE currentstatus AS SELECT DISTINCT patient_id, MAX(date_current_status) AS date_status FROM statushistory WHERE facility_id = " + facilityId + " AND date_current_status <= '" + reportingDateEnd + "' GROUP BY patient_id";
//preparedStatement = jdbcUtil.getStatement(query);
//preparedStatement.executeUpdate();

//query = "SELECT DISTINCT patient.patient_id, patient.gender, DATEDIFF(YEAR, patient.date_birth, '" + reportingDateBegin + "') AS age, patient.date_registration, patient.status_registration, patient.date_started, statushistory.current_status, currentstatus.date_status "
        //+ " FROM patient JOIN statushistory ON patient.patient_id = statushistory.patient_id JOIN currentstatus ON patient.patient_id = currentstatus.patient_id WHERE patient.facility_id = " + facilityId + " AND statushistory.facility_id = " + facilityId + " AND statushistory.patient_id = currentstatus.patient_id AND statushistory.date_current_status = currentstatus.date_status"; 


// create a temporary table of date of the latest regimen change on or before the last day of reporting month 
//executeUpdate("DROP TABLE IF EXISTS currentregimen");
//query = "CREATE TEMPORARY TABLE currentregimen AS SELECT DISTINCT patient_id, MAX(date_visit) AS date_visit FROM regimenhistory WHERE facility_id = " + facilityId + " AND date_visit <= '" + reportingDateEnd + "' GROUP BY patient_id";
//preparedStatement = jdbcUtil.getStatement(query);
//preparedStatement.executeUpdate();

//query = "SELECT DISTINCT patient.patient_id, patient.gender, DATEDIFF(YEAR, patient.date_birth, '" + reportingDateBegin + "') AS age, patient.date_registration, patient.date_started, regimenhistory.regimentype, currentregimen.date_visit "
        //+ " FROM patient JOIN regimenhistory ON patient.patient_id = regimenhistory.patient_id JOIN currentregimen ON patient.patient_id = currentregimen.patient_id WHERE patient.facility_id = " + facilityId + " AND regimenhistory.facility_id = " + facilityId + " AND regimenhistory.patient_id = currentregimen.patient_id AND regimenhistory.date_visit = currentregimen.date_visit"; 
//preparedStatement = jdbcUtil.getStatement(query);


//query = "SELECT patient_id FROM clinic WHERE facility_id = " + facilityId + " AND date_visit BETWEEN DATEADD('MONTH', -3, '" + reportingDateEnd + "') AND " + reportingDateEnd + " AND pregnant = 1";
