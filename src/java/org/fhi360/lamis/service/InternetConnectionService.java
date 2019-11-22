/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service;

import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.struts2.ServletActionContext;

/**
 *
 * @author user1
 */
public class InternetConnectionService {
    //Implementing singleton using the classic design pattern
    private static InternetConnectionService INSTANCE = null; 
    protected InternetConnectionService() {};    
    public static InternetConnectionService getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new InternetConnectionService();
        }
        return INSTANCE;
    }

    public String getConnectionStatus() {
        try {
            String serverUrl = ServletActionContext.getServletContext().getInitParameter("serverUrl");
            URL url = new URL(serverUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if(connection.getResponseCode() == 200) {
                connection.disconnect();
                return "1";
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return "0";            
    }
    
}
//String host = url.getHost();
