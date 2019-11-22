/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.sms;

import java.util.Map;
import org.fhi360.lamis.utility.PropertyAccessor;

/**
 *
 * @author user1
 */
public class SmsMessageSender {

    public String send(String message, String phone) {
        
        Map<String, Object> map = new PropertyAccessor().getSystemProperties();
        String modemStatus = (String) map.get("modemStatus");
        String apiName = (String) map.get("apiName");
        String messageStatus = "";
        
        try {
            if(modemStatus.trim().equals("ON")) {
                messageStatus = new ModemGatewayService().sendSms(message, phone);
            }
            else {
                System.out.println("Sending message to ...... "+phone);
                if(apiName.trim().equalsIgnoreCase("Clickatell")) {
                    messageStatus = new ClickatellSMSGatewayService().sendSms(message, phone);  
                }
                else {
                    messageStatus = new NexmoSMSGatewayService().sendSms(message, phone);                     
                }
            }
        }
        catch(Exception exception) {
            exception.printStackTrace();
        }
        return messageStatus;
    }
}
