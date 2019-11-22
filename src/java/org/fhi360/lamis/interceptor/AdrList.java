/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.interceptor;

import java.util.*;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.utility.builder.PharmacyListBuilder;
import org.fhi360.lamis.utility.builder.ClinicListBuilder;

public class AdrList {   
    public static String getIds() {        
        ArrayList<Map<String, String>> adrList = new ArrayList<Map<String, String>>();
        HttpSession session = ServletActionContext.getRequest().getSession();
        String adrIds = "";
        if(ServletActionContext.getRequest().getParameterMap().containsKey("severity")) {
            String adrId =  ServletActionContext.getRequest().getParameter("adrId");
            String severity =  ServletActionContext.getRequest().getParameter("severity");
            if(!severity.trim().equals("") && !severity.trim().isEmpty()) {  //check if adr was selected 
                adrIds = adrId + "," + severity.replace("Grade ", "");
            }
        }            
        // retrieve the list stored as an attribute in session object
        if(session.getAttribute("adrList") != null) {
            adrList = (ArrayList) session.getAttribute("adrList");                        
        }
        for(int i = 0; i < adrList.size(); i++) {
            String adrId = (String) adrList.get(i).get("adrId"); // retrieve id from list
            String severity = (String) adrList.get(i).get("severity"); // retrieve severity from list
            if(!severity.trim().equals("") && !severity.trim().isEmpty()) {                        
                if(!adrIds.trim().equals("") && !adrIds.trim().isEmpty()) {
                    adrIds = adrIds + "#" + adrId + "," + severity.replace("Grade ", "");                   
                }
                else {
                    adrIds = adrId + "," + severity.replace("Grade ", "");
                }
            }
        }      
        return adrIds;            
    }    

    public static void initList(String name) {
        ArrayList<Map<String, String>> adrList = new ArrayList<Map<String, String>>();
        ArrayList<Map<String, String>> clinicList = new ArrayList<Map<String, String>>();
        ArrayList<Map<String, String>> pharmacyList = new ArrayList<Map<String, String>>();
        HttpSession session = ServletActionContext.getRequest().getSession();
        String ids = "";
        if(name.equals("clinic")) {
            clinicList = new ClinicListBuilder().retrieveClinicList();
            ids = (String) clinicList.get(0).get("adrIds");
        } 
        else {
            pharmacyList = new PharmacyListBuilder().retrievePharmacyList();
            ids = (String) pharmacyList.get(0).get("adrIds");                        
        }

        // retrieve the list stored as an attribute in session object
        if(session.getAttribute("adrList") != null) {
            adrList = (ArrayList) session.getAttribute("adrList");                        
            if(!ids.trim().equals("") && !ids.trim().isEmpty()) {
                String[] adrIds = ids.split("#"); //extract the adr id/severity
                for(int i = 0; i < adrIds.length; i++) {
                    String[] values = adrIds[i].split(","); 
                    for(int j = 0; j < adrList.size(); j++) {
                        String adrId = (String) adrList.get(j).get("adrId");
                        if(values[0].equals(adrId)) {
                            adrList.get(j).remove("severity");
                            adrList.get(j).put("severity", "Grade " + values[1]);
                        }
                    }
                }
                session.setAttribute("adrList", adrList); 
            }
        }        
    }

        
    // This method is not in use, only necessary if adrIds is in the format id1, id2, id3 # severity1, severity2, severity3
    public static void initList1(String name) {
        ArrayList<Map<String, String>> adrList = new ArrayList<Map<String, String>>();
        ArrayList<Map<String, String>> clinicList = new ArrayList<Map<String, String>>();
        ArrayList<Map<String, String>> pharmacyList = new ArrayList<Map<String, String>>();
        HttpSession session = ServletActionContext.getRequest().getSession();
        String adrIds = "";
        if(name.equals("clinic")) {
            clinicList = new ClinicListBuilder().retrieveClinicList();
            adrIds = (String) clinicList.get(0).get("adrIds");            
        } else {
            pharmacyList = new PharmacyListBuilder().retrievePharmacyList();
            adrIds = (String) pharmacyList.get(0).get("adrIds");                        
        }
        if(!adrIds.trim().equals("") || !adrIds.trim().isEmpty()) {
            String ids = adrIds.substring(0, adrIds.indexOf("#")); //extract the adr ids
            String[] idList = ids.split(",");
            ids = adrIds.substring(adrIds.indexOf("#") + 1); //extract the severity grade
            String[] gradeList = ids.split(",");
            for(int i = 0; i < idList.length; i++) {
                String id = idList[i];
                for(int j = 0; j < adrList.size(); j++) {
                    String adrId = (String) adrList.get(j).get("adrId");
                    if(id.equals(adrId)) {
                        adrList.get(j).remove("severity");
                        adrList.get(j).put("severity", "Grade " + gradeList[i]);
                    }
                }
            }                    
        }
    }
    
}
