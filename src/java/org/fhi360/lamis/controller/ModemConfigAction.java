/**
 *
 * @author AALOZIE
 */
package org.fhi360.lamis.controller;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import org.fhi360.lamis.utility.PropertyAccessor;

public class ModemConfigAction extends ActionSupport implements Preparable {

    private ArrayList<Map<String, Object>> modemList = new ArrayList<>();

    private HttpServletRequest request;
    private HttpSession session;

    @Override
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
    }

    // Save modem settings
    public String saveModem() {
        try {
            if (session.getAttribute("userGroup") != null) {
                if (((String) request.getSession().getAttribute("userGroup")).equals("Administrator")) {
                    Map<String, Object> map = new PropertyAccessor().getSystemProperties();
                    map.remove("model");
                    map.put("model", request.getParameter("model"));
                    map.remove("manufacturer");
                    map.put("manufacturer", request.getParameter("manufacturer"));
                    map.remove("comPort");
                    map.put("comPort", request.getParameter("comPort"));
                    map.remove("baudRate");
                    map.put("baudRate", request.getParameter("baudRate") == null ? null : Integer.parseInt(request.getParameter("baudRate")));
                    map.remove("countryCode");
                    map.put("countryCode", request.getParameter("countryCode"));
                    map.remove("modemStatus");
                    map.put("modemStatus", request.getParameter("modemStatus"));
                    map.remove("userName");
                    map.put("userName", request.getParameter("userName"));
                    map.remove("password");
                    map.put("password", request.getParameter("password"));
                    map.remove("api");
                    map.put("api", request.getParameter("api"));

                    new PropertyAccessor().setSystemProperties(map);
                    return SUCCESS;
                } else {
                    return ERROR;
                }
            } else {
                return ERROR;
            }
        } catch (Exception exception) {
            return ERROR;
        }
    }

    // Retieve modem settings
    public String retrieveModem() {
        try {
            if (session.getAttribute("userGroup") != null) {
                if (((String) request.getSession().getAttribute("userGroup")).equals("Administrator")) {
                    Map<String, Object> map = new PropertyAccessor().getSystemProperties();
                    modemList.add(map);
                    return SUCCESS;
                } else {
                    return ERROR;
                }
            } else {
                return ERROR;
            }
        } catch (Exception e) {
            return ERROR;
        }
    }

    /**
     * @return the modemList
     */
    public ArrayList<Map<String, Object>> getModemList() {
        return modemList;
    }

    /**
     * @param modemList the modemList to set
     */
    public void setModemList(ArrayList<Map<String, Object>> modemList) {
        this.modemList = modemList;
    }

}
