/**
 *
 * @author aalozie
 */

package org.fhi360.lamis.utility.builder;

import java.util.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.utility.DateUtil;

public class ClinicListBuilder {
    private HttpServletRequest request;
    private HttpSession session;
    private ArrayList<Map<String, String>> clinicList = new ArrayList<>();
    
    public ClinicListBuilder() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }
    
           
    public void buildClinicList(ResultSet resultSet) throws SQLException{
        System.out.println("retrieving.......");
        String hospitalNum = "";
        if(request.getParameterMap().containsKey("hospitalNum")) {
            hospitalNum = request.getParameter("hospitalNum");             
        }
        String currentStatus = "";
        if(request.getParameterMap().containsKey("currentStatus")) {
            currentStatus = request.getParameter("currentStatus"); 
        }
        String dateCurrentStatus = "";
        if(request.getParameterMap().containsKey("dateCurrentStatus")) {
            dateCurrentStatus = request.getParameter("dateCurrentStatus"); 
        }
        String dateLastClinic = "";
        if(request.getParameterMap().containsKey("dateLastClinic")) {
            dateLastClinic = request.getParameter("dateLastClinic"); 
        }
        try {
            // loop through resultSet for each row and put into Map
            while (resultSet.next()) { 
                System.out.println("retrieving.......data");
                String clinicId = Long.toString(resultSet.getLong("clinic_id")); 
                String patientId = Long.toString(resultSet.getLong("patient_id")); 
                String facilityId = Long.toString(resultSet.getLong("facility_id")); 
                String dateVisit = resultSet.getObject("date_visit") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_visit"), "MM/dd/yyyy");
                String clinicStage = resultSet.getObject("clinic_stage") == null ? "" : resultSet.getString("clinic_stage");
                String funcStatus = resultSet.getObject("func_status") == null ? "" : resultSet.getString("func_status");
                String tbStatus = resultSet.getObject("tb_status") == null ? "" : resultSet.getString("tb_status");
                String viralLoad = resultSet.getObject("viral_load") == null ? "" : Double.toString(resultSet.getDouble("viral_load"));
                String cd4 = resultSet.getObject("cd4") == null ? "" : resultSet.getDouble("cd4") == 0.0 ? "" : Double.toString(resultSet.getDouble("cd4"));
                String cd4p = resultSet.getObject("cd4p") == null ? "" : resultSet.getDouble("cd4p") == 0.0 ? "" : Double.toString(resultSet.getDouble("cd4p"));
                String regimentype = resultSet.getObject("regimentype") == null ? "" : resultSet.getString("regimentype");
                String gestationalAge = resultSet.getObject("gestational_age") == null ? "" : resultSet.getString("gestational_age");
                String regimen = resultSet.getObject("regimen") == null ? "" : resultSet.getString("regimen");
                String bodyWeight = resultSet.getObject("body_weight") == null ? "" : Double.toString(resultSet.getDouble("body_weight"));
                String height = resultSet.getObject("height") == null ? "" : Double.toString(resultSet.getDouble("height"));
                String waist = resultSet.getObject("waist") == null ? "" : Double.toString(resultSet.getDouble("waist"));
                String bp = resultSet.getObject("bp") == null ? "" : resultSet.getString("bp");
                String pregnant = resultSet.getObject("pregnant") == null ? "" : Integer.toString(resultSet.getInt("pregnant"));
                String lmp = resultSet.getObject("lmp") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("lmp"), "MM/dd/yyyy");
                String breastfeeding = resultSet.getObject("breastfeeding") == null ? "" : Integer.toString(resultSet.getInt("breastfeeding"));                
                String oiScreened = resultSet.getObject("oi_screened") == null ? "" : resultSet.getString("oi_screened");
                String oiIds = resultSet.getObject("oi_ids") == null ? "" : resultSet.getString("oi_ids");
                String adrScreened = resultSet.getObject("adr_screened") == null ? "" : resultSet.getString("adr_screened");
                String adrIds = resultSet.getObject("adr_ids") == null ? "" : resultSet.getString("adr_ids");
                String adherenceLevel = resultSet.getObject("adherence_level") == null ? "" : resultSet.getString("adherence_level");
                String adhereIds = resultSet.getObject("adhere_ids") == null ? "" : resultSet.getString("adhere_ids");
                String nextAppointment = resultSet.getObject("next_appointment") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("next_appointment"), "MM/dd/yyyy");
                String notes = resultSet.getObject("notes") == null ? "" : resultSet.getString("notes");
                String commence = resultSet.getObject("commence") == null ? "" :  Integer.toString(resultSet.getInt("commence"));

                // create an array from object properties 
                Map<String, String> map = new HashMap<>();
                map.put("clinicId", clinicId);
                map.put("patientId", patientId);
                map.put("facilityId", facilityId);
                map.put("hospitalNum", hospitalNum);
                map.put("currentStatus", currentStatus);
                map.put("dateCurrentStatus", dateCurrentStatus);
                map.put("dateLastClinic", dateLastClinic);
                map.put("dateVisit", dateVisit);
                map.put("clinicStage", clinicStage);
                map.put("gestationalAge", gestationalAge);
                map.put("funcStatus", funcStatus);
                map.put("tbStatus", tbStatus);
                map.put("viralLoad", viralLoad);
                map.put("cd4", cd4);
                map.put("cd4p", cd4p);
                map.put("regimentype", regimentype);
                map.put("regimen", regimen);
                map.put("bodyWeight", bodyWeight);
                map.put("height", height);
                map.put("waist", waist);
                map.put("bp", bp);
                map.put("pregnant", pregnant);
                map.put("lmp", lmp);
                map.put("breastfeeding", breastfeeding);
                map.put("oiScreened", oiScreened);
                map.put("oiIds", oiIds);
                map.put("adrScreened", adrScreened);
                map.put("adrIds", adrIds);
                map.put("adherenceLevel", adherenceLevel);
                map.put("adhereIds", adhereIds);
                map.put("nextAppointment", nextAppointment);
                map.put("notes", notes);
                map.put("commence", commence);        
                clinicList.add(map);
            }
            session.setAttribute("clinicList", clinicList);  
            resultSet = null;
            clinicList = null;
        }
        catch (SQLException sqlException) {
            resultSet = null;
            throw sqlException;  
        }            
    }
   
    public ArrayList<Map<String, String>> retrieveClinicList() {
        if(session.getAttribute("clinicList") != null) {
            clinicList = (ArrayList) session.getAttribute("clinicList");                        
        }
        return clinicList;
    }   

    public void clearClinicList() {
        clinicList = retrieveClinicList();
        clinicList.clear();
        session.setAttribute("clinicList", clinicList); 
    }
    
}
