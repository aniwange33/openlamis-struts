/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.interceptor;

import java.util.*;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.utility.builder.ChroniccareListBuilder;

public class TbscreenList {   
    public static String getIds() {        
        ArrayList<Map<String, String>> tbscreenList = new ArrayList<>();
        HttpSession session = ServletActionContext.getRequest().getSession();
        String tbValues = "";
        if(ServletActionContext.getRequest().getParameterMap().containsKey("tbValue")) {
            String tbscreenId =  ServletActionContext.getRequest().getParameter("tbscreenId");
            String tbValue =  ServletActionContext.getRequest().getParameter("tbValue");
            if(!tbValue.trim().equals("") && !tbValue.trim().isEmpty()) {  //check if tb was selected 
                tbValues = tbscreenId + "," + tbValue;
            }
        }            
        // retrieve the list stored as an attribute in session object
        if(session.getAttribute("tbscreenList") != null) {
            tbscreenList = (ArrayList) session.getAttribute("tbscreenList");                        
        }
        for(int i = 0; i < tbscreenList.size(); i++) {
            String tbscreenId = (String) tbscreenList.get(i).get("tbscreenId"); // retrieve id from list
            String tbValue = (String) tbscreenList.get(i).get("tbValue"); // retrieve tb from list
            if(!tbValue.trim().equals("") && !tbValue.trim().isEmpty()) {                        
                if(!tbValues.trim().equals("") && !tbValues.trim().isEmpty()) {
                    tbValues = tbValues + "#" + tbscreenId + "," + tbValue;                   
                }
                else {
                    tbValues = tbscreenId + "," + tbValue;
                }
            }
        }      
        return tbValues;            
    }    

    public static void initList() {
        ArrayList<Map<String, String>> tbscreenList = new ArrayList<>();
        ArrayList<Map<String, String>> chroniccareList = new ArrayList<>();
        HttpSession session = ServletActionContext.getRequest().getSession();
        String tbValues = "";
        chroniccareList = new ChroniccareListBuilder().retrieveChroniccareList();
        tbValues = (String) chroniccareList.get(0).get("tbValues");

        // retrieve the list stored as an attribute in session object
        if(session.getAttribute("tbscreenList") != null) {
            tbscreenList = (ArrayList) session.getAttribute("tbscreenList");                        
            if(!tbValues.trim().equals("") && !tbValues.trim().isEmpty()) {
                String[] tbValue = tbValues.split("#"); //extract the tbscreen id/value
                for(int i = 0; i < tbValue.length; i++) {
                    String[] values = tbValue[i].split(","); 
                    for(int j = 0; j < tbscreenList.size(); j++) {
                        String tbscreenId = (String) tbscreenList.get(j).get("tbscreenId");
                        if(values[0].equals(tbscreenId)) {
                            tbscreenList.get(j).remove("tbValue");
                            tbscreenList.get(j).put("tbValue", values[1]);
                        }
                    }
                }
                session.setAttribute("tbscreenList", tbscreenList); 
            }
        }        
    }    
}
