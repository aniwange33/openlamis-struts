/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.interceptor;

import java.util.*;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.utility.builder.ChroniccareListBuilder;

public class DmscreenList {   
    public static String getIds() {        
        List<Map<String, String>> dmscreenList = new ArrayList<>();
        HttpSession session = ServletActionContext.getRequest().getSession();
        String dmValues = "";
        if(ServletActionContext.getRequest().getParameterMap().containsKey("dmValue")) {
            String dmscreenId =  ServletActionContext.getRequest().getParameter("dmscreenId");
            String dmValue =  ServletActionContext.getRequest().getParameter("dmValue");
            if(!dmValue.trim().equals("") && !dmValue.trim().isEmpty()) {  //check if dm was selected 
                dmValues = dmscreenId + "," + dmValue;
            }
        }            
        // retrieve the list stored as an attribute in session object
        if(session.getAttribute("dmscreenList") != null) {
            dmscreenList = (ArrayList) session.getAttribute("dmscreenList");                        
        }
        for(int i = 0; i < dmscreenList.size(); i++) {
            String dmscreenId = (String) dmscreenList.get(i).get("dmscreenId"); // retrieve id from list
            String dmValue = (String) dmscreenList.get(i).get("dmValue"); // retrieve dm from list
            if(!dmValue.trim().equals("") && !dmValue.trim().isEmpty()) {                        
                if(!dmValues.trim().equals("") && !dmValues.trim().isEmpty()) {
                    dmValues = dmValues + "#" + dmscreenId + "," + dmValue;                   
                }
                else {
                    dmValues = dmscreenId + "," + dmValue;
                }
            }
        }      
        return dmValues;            
    }    

    public static void initList() {
        ArrayList<Map<String, String>> dmscreenList = new ArrayList<>();
        ArrayList<Map<String, String>> chroniccareList = new ArrayList<>();
        HttpSession session = ServletActionContext.getRequest().getSession();
        String dmValues = "";
        chroniccareList = new ChroniccareListBuilder().retrieveChroniccareList();
        dmValues = (String) chroniccareList.get(0).get("dmValues");

        // retrieve the list stored as an attribute in session object
        if(session.getAttribute("dmscreenList") != null) {
            dmscreenList = (ArrayList) session.getAttribute("dmscreenList");                        
            if(!dmValues.trim().equals("") && !dmValues.trim().isEmpty()) {
                String[] dmValue = dmValues.split("#"); //extract the dmscreen id/value
                for (String dmValue1 : dmValue) {
                    String[] values = dmValue1.split(","); 
                    for(int j = 0; j < dmscreenList.size(); j++) {
                        String dmscreenId = (String) dmscreenList.get(j).get("dmscreenId");
                        if(values[0].equals(dmscreenId)) {
                            dmscreenList.get(j).remove("dmValue");
                            dmscreenList.get(j).put("dmValue", values[1]);
                        }
                    }
                }
                session.setAttribute("dmscreenList", dmscreenList); 
            }
        }        
    }    
}
