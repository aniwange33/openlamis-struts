/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.interceptor;

import java.util.*;
import java.sql.PreparedStatement;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.controller.ClinicAction;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class OiAdrInterceptor extends AbstractInterceptor {
    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    private ArrayList<Map<String, String>> adrList = new ArrayList<>();
    private ArrayList<Map<String, String>> oiList = new ArrayList<>();
    private ArrayList<Map<String, String>> adhereList = new ArrayList<>();
    
    @Override    
    public String intercept(ActionInvocation invocation) throws Exception {
        String result = invocation.invoke();
        Object action = invocation.getAction();
        if(action instanceof ClinicAction) {
            saveOiAdhere();
        }
        saveAdr();
        return result;
    }
    
    private void saveOiAdhere() {
        request = ServletActionContext.getRequest();
        session = ServletActionContext.getRequest().getSession();
        long facilityId = (Long) session.getAttribute("facilityId");
        long patientId = Long.parseLong(request.getParameter("patientId"));
        String dateVisit = request.getParameter("dateVisit");

        //check if ois were present and save
        if(request.getParameterMap().containsKey("oiIds")) {
            query = "DELETE FROM oihistory WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_visit = '" + DateUtil.parseStringToSqlDate(dateVisit, "MM/dd/yyyy") + "'";
            executeUpdate(query);
            String ids = request.getParameter("oiIds");
            String[] idList =  ids.split(",");

            // retrieve the list stored as an attribute in session object
            if(session.getAttribute("oiList") != null) {
                oiList = (ArrayList) session.getAttribute("oiList");                        
            }

            // find the target oi and update with values of request parameters
            for(String id : idList) {
                for(int i = 0; i < oiList.size(); i++) {
                    String oiId = (String) oiList.get(i).get("oiId"); // retrieve oi id from list
                    String description = (String) oiList.get(i).get("description");
                    if(id.equals(oiId)) {
                        query = "INSERT INTO oihistory (facility_id, patient_id, date_visit, oi, time_stamp) VALUES(" + facilityId + ", " + patientId + ", '" + DateUtil.parseStringToSqlDate(dateVisit, "MM/dd/yyyy") + "', '" + description + "', NOW())";
                        executeUpdate(query);
                    }
                }        
            }
        }         

        //check if adhere was accessed
        if(request.getParameterMap().containsKey("adhereIds")) {
            query = "DELETE FROM adherehistory WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_visit = '" + DateUtil.parseStringToSqlDate(dateVisit, "MM/dd/yyyy") + "'";
            executeUpdate(query);
            String ids = request.getParameter("adhereIds");
            String[] idList =  ids.split(",");

            // retrieve the list stored as an attribute in session object
            if(session.getAttribute("adhereList") != null) {
                adhereList = (ArrayList) session.getAttribute("adhereList");                        
            }

            // find the target oi and update with values of request parameters
            for(String id : idList) {
                for(int i = 0; i < adhereList.size(); i++) {
                    String adhereId = (String) adhereList.get(i).get("adhereId"); // retrieve reason id from list
                    String description = (String) adhereList.get(i).get("description");
                    if(id.equals(adhereId)) {
                        query = "INSERT INTO adherehistory (facility_id, patient_id, date_visit, reason, time_stamp) VALUES(" + facilityId + ", " + patientId + ", '" + DateUtil.parseStringToSqlDate(dateVisit, "MM/dd/yyyy") + "', '" + description + "', NOW())";
                        executeUpdate(query);
                    }
                }        
            }            
        }       
    }
    
    private void saveAdr() {
        request = ServletActionContext.getRequest();
        session = ServletActionContext.getRequest().getSession();
        long facilityId = (Long) session.getAttribute("facilityId");
        long patientId = Long.parseLong(request.getParameter("patientId"));
        String dateVisit = request.getParameter("dateVisit");
        int screener = Integer.parseInt(request.getParameter("screener"));

        query = "DELETE FROM adrhistory WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_visit = '" + DateUtil.parseStringToSqlDate(dateVisit, "MM/dd/yyyy") + "'";
        executeUpdate(query);
        
        String severity = "";
        String description = "";
        if(request.getParameterMap().containsKey("severity")) {
            severity =  request.getParameter("severity");
            description = request.getParameter("description");
        }

        if(!severity.equals("") && !severity.isEmpty() && !request.getParameter("description").equals("") && !request.getParameter("description").isEmpty()) {
            query = "INSERT INTO adrhistory (facility_id, patient_id, date_visit, adr, severity, screener, time_stamp) VALUES(" + facilityId + ", " + patientId + ", '" + DateUtil.parseStringToSqlDate(dateVisit, "MM/dd/yyyy") + "', '" + description + "', " + Integer.parseInt(severity.replace("Grade ", "")) + ", " + screener + ", NOW())";
        }
        // retrieve the list stored as an attribute in session object
        if(session.getAttribute("adrList") != null) {
            adrList = (ArrayList) session.getAttribute("adrList");                        
        }

        for(int i = 0; i < adrList.size(); i++) {
            severity = (String) adrList.get(i).get("severity"); // retrieve id from list
            description = (String) adrList.get(i).get("description");
            if(!severity.equals("") && !severity.isEmpty()) {
                query = "INSERT INTO adrhistory (facility_id, patient_id, date_visit, adr, severity, screener, time_stamp) VALUES(" + facilityId + ", " + patientId + ", '" + DateUtil.parseStringToSqlDate(dateVisit, "MM/dd/yyyy") + "', '" + description + "', " + Integer.parseInt(severity.replace("Grade ", "")) + ", " + screener + ", NOW())";
                executeUpdate(query);
            }
        }
    }

    private void executeUpdate(String query) {
        try {
         transactionTemplate.execute((ts) -> {
             jdbcTemplate.execute(query);
             return null; //To change body of generated lambdas, choose Tools | Templates.
         });
        }
        catch (Exception exception) {
           
        }        
    }        
    
    //This method is not in use, proposed if adrIds can be generated from the clinic_data jsp
    public void saveAdr1() {
        //deleteRecord("adrhistory");
        String severity = "";
        String adrIds = request.getParameter("adrIds");
        if(!adrIds.equals("") || !adrIds.isEmpty()) {
            String ids = adrIds.substring(0, adrIds.indexOf("#")); //extract the adr ids
            String[] idList = ids.split(",");
            ids = adrIds.substring(adrIds.indexOf("#") + 1); //extract the severity grade
            String[] grades = ids.split(",");
            for(int i = 0; i < idList.length; i++) {
                String id = idList[i];
                severity = grades[i];
                for(int j = 0; j < adrList.size(); j++) {
                    String adrId = (String) adrList.get(j).get("adrId");
                    String description = (String) adrList.get(j).get("description");
                    if(id.equals(adrId)) {
                        query = "INSERT INTO adrhistory (facility_id, patient_id, date_visit, adr, time_stamp, severity, screener) VALUES(?, ?, ?, ?, ?, ?, ?)";
                    }
                }
            }                    
        }
    }
    
}
