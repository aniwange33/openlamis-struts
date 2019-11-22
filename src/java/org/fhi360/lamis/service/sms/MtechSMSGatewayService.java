/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.sms;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author user10
 */
public class MtechSMSGatewayService {
    private final String USER_AGENT = "Mozilla/5.0";

    public String sendSms(String message, String phone) {
        HttpURLConnection urlConnection = null;
        StringBuilder response = new StringBuilder();
        try {
            String url = "http://xxx.xxx.xxx.xxx/?user=fhi360&password=fhi360&from=shortcode&to="+phone+"&message="+message;
            URL obj = new URL(url);
            urlConnection = (HttpURLConnection) obj.openConnection();

            //Set request header
            urlConnection.setRequestProperty("User-Agent", USER_AGENT);
            urlConnection.setRequestMethod("GET");

            //Read the response
            int statusCode = urlConnection.getResponseCode();
            if(statusCode == 200) {
                String line = "";
                BufferedReader reader = new BufferedReader(new InputStreamReader((urlConnection.getInputStream())));
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                if(reader != null) reader.close();
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return response.toString();
    }
    
}
