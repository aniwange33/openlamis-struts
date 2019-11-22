  /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.exchange.radet;

/**
 *
 * @author user1
 */
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.apache.commons.lang3.StringUtils;
import org.fhi360.lamis.dao.jdbc.PatientJDBC;
import org.fhi360.lamis.dao.jdbc.RegimenJDBC;
import org.fhi360.lamis.utility.Constants;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.JDBCUtil;
import org.fhi360.lamis.utility.Scrambler;

public class RadetService {

    private String query;
    private JDBCUtil jdbcUtil;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private Scrambler scrambler = new Scrambler();
    
    private long facilityId;
    private long patientId;
    private String uniqueId;
    private String hospitalNum;
    private String surname;
    private String otherNames;
    private String dateBirth;
    private String age;
    private String ageUnit;
    private String gender;
    private String dateRegistration;
    private String statusRegistration;
    private String enrollmentSetting;
    private String dateStarted;
    private String currentStatus;
    private String dateCurrentStatus;
    private String clinicStage;
    private String funcStatus;
    private String cd4;
    private String cd4p;
    private String dateLastRefill;
    private String duration;
    private String regimentype;
    private String regimen;
    private String regimentypeId;
    private String regimenId;
    private String regimenStart;
    private String regimentypeStart;
    private String dateCurrentViralLoad;
    private String dateCollected;
    private String viralLoad;
    private String viralLoadIndication;
    private String sortname;
    private String category;
    
    private ArrayList<Map<String, String>> clientList = new ArrayList<Map<String, String>>();
    private Map<String, Map<String, String>> sortedMaps = new TreeMap<String, Map<String, String>>();

    public RadetService() {
        facilityId = (Long) ServletActionContext.getRequest().getSession().getAttribute("facilityId"); 
        try {
            jdbcUtil = new JDBCUtil(); 
        }
        catch (Exception exception) {
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }        
    }
    
    public void buildClientList() {
        Date today = Calendar.getInstance().getTime();
        String losses = "ART Transfer Out Stopped Treatment Known Death Lost to Follow Up"; 
        
        try {            
            executeUpdate("DROP TABLE IF EXISTS commence");        
            executeUpdate("CREATE TEMPORARY TABLE commence AS SELECT * FROM clinic WHERE facility_id = " + facilityId + " AND commence = 1");
            executeUpdate("CREATE INDEX idx_commence ON commence(patient_id)");

            executeUpdate("DROP TABLE IF EXISTS pharm");        
            executeUpdate("CREATE TEMPORARY TABLE pharm AS SELECT * FROM pharmacy WHERE facility_id = " + facilityId + " AND regimentype_id IN (1, 2, 3, 4, 14)");
            executeUpdate("CREATE INDEX idx_pharm ON pharm(patient_id)");
            
            query = "SELECT * FROM patient WHERE facility_id = " + facilityId + " AND date_started IS NOT NULL";
            resultSet = executeQuery(query);
            while(resultSet.next()) {
                initVariables();
                patientId = resultSet.getLong("patient_id");
                hospitalNum = resultSet.getString("hospital_num");
                uniqueId = resultSet.getString("unique_id");
                surname = (resultSet.getString("surname") == null)? "" : resultSet.getString("surname");
                surname = scrambler.unscrambleCharacters(surname);
                surname = StringUtils.upperCase(surname);
                otherNames = (resultSet.getString("other_names") == null)? "" : resultSet.getString("other_names");
                otherNames = scrambler.unscrambleCharacters(otherNames);
                otherNames = StringUtils.capitalize(otherNames);
                dateBirth = (resultSet.getDate("date_birth") == null) ? "" : DateUtil.parseDateToString(resultSet.getDate("date_birth"), "MM/dd/yyyy");
                age = (resultSet.getInt("age") == 0) ? "" : Integer.toString(resultSet.getInt("age"));
                ageUnit = (resultSet.getString("age_unit") == null) ? "" : resultSet.getString("age_unit");
                gender = (resultSet.getString("gender") == null) ? "" : resultSet.getString("gender");
                dateRegistration = (resultSet.getDate("date_registration") == null) ? "" : DateUtil.parseDateToString(resultSet.getDate("date_registration"), "MM/dd/yyyy");
                currentStatus = (resultSet.getString("current_status") == null) ? "" : resultSet.getString("current_status");
                statusRegistration = (resultSet.getString("status_registration") == null) ? "" : resultSet.getString("status_registration");
                enrollmentSetting = (resultSet.getString("enrollment_setting") == null) ? "" : resultSet.getString("enrollment_setting");
                dateCurrentStatus = (resultSet.getDate("date_current_status") == null) ? "" : DateUtil.parseDateToString(resultSet.getDate("date_current_status"), "MM/dd/yyyy");
                dateStarted = DateUtil.parseDateToString(resultSet.getDate("date_started"), "MM/dd/yyyy");
                
                //Get ART Commencement data
                query = "SELECT * FROM commence WHERE patient_id = " + patientId;
                ResultSet clinic = executeQuery(query);
                if (clinic.next()) {
                    regimentypeStart = (clinic.getString("regimentype") == null) ? "" : clinic.getString("regimentype");
                    regimenStart = (clinic.getString("regimen") == null) ? "" : clinic.getString("regimen");
                    clinicStage = (clinic.getString("clinic_stage") == null) ? "" : clinic.getString("clinic_stage");
                    funcStatus = (clinic.getString("func_status") == null) ? "" : clinic.getString("func_status");
                    cd4 = (clinic.getDouble("cd4") == 0) ? "" : Double.toString(clinic.getDouble("cd4"));
                    cd4p = (clinic.getDouble("cd4p") == 0) ? "" : Double.toString(clinic.getDouble("cd4p"));                               
                }

                //Get last refill record 
                ResultSet pharm = getLastRefillVisit(patientId);
                if (pharm.next()) {
                    regimentypeId = Long.toString(pharm.getLong("regimentype_id"));
                    regimenId = Long.toString(pharm.getLong("regimen_id"));
                    regimentype = RegimenJDBC.getRegimentype(pharm.getLong("regimentype_id"));
                    regimen = RegimenJDBC.getRegimen(pharm.getLong("regimen_id"));
                    duration = Integer.toString(pharm.getInt("duration"));
                    dateLastRefill = (pharm.getDate("date_visit") == null) ? "" : DateUtil.parseDateToString(pharm.getDate("date_visit"), "MM/dd/yyyy");                
                }
                //Set the default category to 5 meaning the client has no gaps in ARV refill or viral load test records
                sortname = "E" + Long.toString(patientId); 
                category = "5";
                
                //Check if client has no ARV refill
                if(regimentype.trim().isEmpty() || regimen.trim().isEmpty()) {
                    sortname = "A" + Long.toString(patientId); 
                    category = "1";                    
                }
                else {
                    //Check if client has no regimen at start of ART    
                    if(regimentypeStart.trim().isEmpty() || regimenStart.trim().isEmpty()) {
                        sortname = "B" + Long.toString(patientId); 
                        category = "2";
                    }
                    else {
                        if(!losses.contains(currentStatus)) {
                            //Check if the last refill is more than 28 days ( 0r 90 days) ago from the reporting date, then this client is lost to follow up
                            if(!dateLastRefill.isEmpty()) {
                                if(DateUtil.addYearMonthDay(pharm.getDate("date_visit"), pharm.getInt("duration")+Constants.LTFU.PEPFAR, "DAY").before(today)) {
                                    sortname = "C" + Long.toString(patientId); 
                                    category = "3";
                                }                            
                                else {
                                    //Check if client is due for Viral Load test
                                    boolean dueViralLoad = new PatientJDBC().dueViralLoad(patientId);   
                                    if(dueViralLoad) {
                                        sortname = "D" + Long.toString(patientId);
                                        category = "4";                                                                                
                                    }
                                }
                            }    
                        }
                    }    
                }
                
                // create an array from object properties 
                Map<String, String> map = new HashMap<String, String>();
                map.put("patientId", Long.toString(patientId));
                map.put("uniqueId", uniqueId);
                map.put("hospitalNum", hospitalNum);
                map.put("name", surname + " " + otherNames);
                map.put("surname", surname);
                map.put("otherNames", otherNames);
                map.put("dateBirth", dateBirth);
                map.put("age", age);
                map.put("ageUnit", ageUnit);
                map.put("gender", gender);
                map.put("dateRegistration", dateRegistration);
                map.put("statusRegistration", statusRegistration);
                map.put("enrollmentSetting", enrollmentSetting);
                map.put("dateStarted", dateStarted);
                map.put("currentStatus", currentStatus);
                map.put("dateCurrentStatus", dateCurrentStatus);
                map.put("clinicStage", clinicStage);
                map.put("funcStatus", funcStatus);
                map.put("cd4", cd4);
                map.put("cd4p", cd4p);
                map.put("dateLastRefill", dateLastRefill);
                map.put("duration", duration);
                map.put("regimentype", regimentype);
                map.put("regimen", regimen);
                map.put("regimentypeId", regimentypeId);
                map.put("regimenId", regimenId);
                map.put("regimentypeStart", regimentypeStart);
                map.put("regimenStart", regimenStart);
                map.put("dateCurrentViralLoad", dateCurrentViralLoad);
                map.put("dateCollected", dateCollected);
                map.put("viralLoad", viralLoad);
                map.put("viralLoadIndication", viralLoadIndication);
                map.put("category", category);
                sortedMaps.put(sortname, map);
            }

            for (Map.Entry<String, Map<String, String>> entry : sortedMaps.entrySet()) {
                clientList.add(entry.getValue());
            }
            ServletActionContext.getRequest().getSession().setAttribute("clientList", clientList);
        } 
        catch (Exception exception) {
            exception.printStackTrace();
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }          
    }
    
    public ArrayList<Map<String, String>> radetReport(String reportType) {
        ArrayList<Map<String, String>> reportList = new ArrayList<Map<String, String>>();
        clientList = retrieveClientList();
        for(int i = 0; i < clientList.size(); i++) {
            String id = (String) clientList.get(i).get("category"); 
            if(id.equals(reportType)) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("uniqueId", clientList.get(i).get("uniqueId"));
                map.put("hospitalNum", clientList.get(i).get("hospitalNum"));
                map.put("name", clientList.get(i).get("name"));
                map.put("surname", clientList.get(i).get("surname"));
                map.put("otherNames", clientList.get(i).get("otherNames"));
                map.put("dateBirth", clientList.get(i).get("dateBirth"));
                map.put("age", clientList.get(i).get("age"));
                map.put("ageUnit", clientList.get(i).get("ageUnit"));
                map.put("gender", clientList.get(i).get("gender"));
                map.put("dateRegistration", clientList.get(i).get("dateRegistration"));
                map.put("statusRegistration", clientList.get(i).get("statusRegistration"));
                map.put("dateStarted", clientList.get(i).get("dateStarted"));
                map.put("currentStatus", clientList.get(i).get("currentStatus"));
                map.put("dateCurrentStatus", clientList.get(i).get("dateCurrentStatus"));
                map.put("dateLastRefill", clientList.get(i).get("dateLastRefill"));
                map.put("regimentype", clientList.get(i).get("regimentype"));
                map.put("regimen", clientList.get(i).get("regimen"));
                map.put("regimentypeStart", clientList.get(i).get("regimentypeStart"));
                map.put("regimenStart", clientList.get(i).get("regimenStart"));
                reportList.add(map);
            }
        }        
        return reportList;                
    }
    
    public ArrayList<Map<String, String>> retrieveClientList() {
        HttpSession session = ServletActionContext.getRequest().getSession();
        if (session.getAttribute("clientList") != null) {
            clientList = (ArrayList) session.getAttribute("clientList");
        }
        return clientList;
    }

    public void clearClientList() {
        clientList = retrieveClientList();
        clientList.clear();
        ServletActionContext.getRequest().getSession().setAttribute("clientList", clientList);
    }

    private void initVariables() {
        patientId = 0L;
        uniqueId = "";
        hospitalNum = "";
        surname = "";
        otherNames = "";
        dateBirth = "";
        age = "";
        ageUnit = "";
        gender = "";
        dateRegistration = "";
        statusRegistration = "";
        enrollmentSetting = "";
        dateStarted = "";
        currentStatus = "";
        dateCurrentStatus = "";
        clinicStage = "";
        funcStatus = "";
        cd4 = "";
        cd4p = "";
        dateLastRefill = "";
        duration = "";
        regimentype = "";
        regimen = "";
        regimentypeId = "";
        regimenId = "";
        regimenStart = "";
        regimentypeStart = "";
        dateCurrentViralLoad = "";
        dateCollected = "";
        viralLoad = "";
        viralLoadIndication = "";
        sortname = "";        
    }

    private void executeUpdate(String query) {
        try {
            preparedStatement = jdbcUtil.getStatement(query);
            preparedStatement.executeUpdate();
        } catch (Exception exception) {
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }
    }

    private ResultSet executeQuery(String query) {
        ResultSet rs = null;
        try {
            preparedStatement = jdbcUtil.getStatement(query);
            rs = preparedStatement.executeQuery();
        } catch (Exception exception) {
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }
        return rs;
    }

    private ResultSet getLastRefillVisit(long patientId) {
        ResultSet rs = null;
        try {
            query = "SELECT DISTINCT regimentype_id, regimen_id, date_visit, duration FROM pharm WHERE patient_id = " + patientId + " ORDER BY date_visit DESC LIMIT 1";  
            preparedStatement = jdbcUtil.getStatement(query);
            rs = preparedStatement.executeQuery();
        }
        catch (Exception exception) {
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }
        return rs;        
    }

}
