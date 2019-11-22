/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.sms;


import com.nexmo.messaging.sdk.NexmoSmsClient;
import com.nexmo.messaging.sdk.SmsSubmissionResult;
import com.nexmo.messaging.sdk.messages.TextMessage;
import com.nexmo.messaging.sdk.messages.WapPushMessage;
import java.util.Map;
import org.fhi360.lamis.utility.PropertyAccessor;

/**
 *
 * @author user1
 */

public class NexmoSMSGatewayService {
    public String sendSms(String message, String phone) {
        //message = "Hello, welcome to HEALTH MESSENGER a mobile platform that gives you tips on how to stay healthy and also reminds you of important medical appointments. "
               // + " You have been automatically enrolled for this service by your healthcare provider. To opt out please notify your healthcare provider.";
        String messageStatus;
        Map<String, Object> map = new PropertyAccessor().getSystemProperties();
        String API_KEY = (String) map.get("API_KEY");   
        String API_SECRET  = (String) map.get("API_SECRET");
        
        //Format the phone number by adding country code and removing the plus sign (+)
        String countryCode = (String) map.get("countryCode");
        phone = phone.replaceFirst("\\d", countryCode).replace("+", "");
        String SMS_FROM = "HEALTH MESSENGER";
        
        System.out.println("API_KEY......."+ API_KEY);
        System.out.println("API_SECRET......."+ API_SECRET);
        System.out.println(phone);
        System.out.println(message);
        
        
        /*
        String WAP_PUSH_FROM = "12345";
        String WAP_PUSH_TO = "447777111222";
        String WAP_PUSH_URL = "http://www.nexmo.com";
        String WAP_PUSH_TITLE = "Nexmo"; 
        */
        
       // Create a client for submitting to Nexmo
        NexmoSmsClient client = null;
        try {
            client = new NexmoSmsClient(API_KEY, API_SECRET);
        } 
        catch (Exception e) {
            messageStatus = "FAILED";
            System.err.println("Failed to instanciate a Nexmo Client");
            e.printStackTrace();
            throw new RuntimeException("Failed to instanciate a Nexmo Client");
        }

        // Create a Text SMS Message request object ...
        TextMessage text = new TextMessage(SMS_FROM, phone, message);
        
        // Create a Wap-Push Message request object ...
        //WapPushMessage message = new WapPushMessage(WAP_PUSH_FROM, WAP_PUSH_TO, WAP_PUSH_URL, WAP_PUSH_TITLE);

        // Use the Nexmo client to submit the Text Message ...
        SmsSubmissionResult[] results = null;
        try {
            results = client.submitMessage(text);
            messageStatus = "SENT";
        } 
        catch (Exception e) {
            messageStatus = "FAILED";
            System.err.println("Failed to communicate with the Nexmo Client");
            e.printStackTrace();
            throw new RuntimeException("Failed to communicate with the Nexmo Client");
        }

        // Evaluate the results of the submission attempt ...
        for (int i=0;i<results.length;i++) {
            if (results[i].getStatus() == SmsSubmissionResult.STATUS_OK) {
                messageStatus = "SENT";
                System.out.println("SUCCESS");                
            }
            else {
                messageStatus = "FAILED";
                if (results[i].getTemporaryError()) {
                    System.out.println("TEMPORARY FAILURE - PLEASE RETRY");                                    
                }
                else {
                    System.out.println("SUBMISSION FAILED!");
                    System.out.println("Message-Id [ " + results[i].getMessageId() + " ] ...");
                    System.out.println("Error-Text [ " + results[i].getErrorText() + " ] ...");                                    
                }
            } 
            if (results[i].getMessagePrice() != null) System.out.println("Message-Price [ " + results[i].getMessagePrice() + " ] ...");
            if (results[i].getRemainingBalance() != null) System.out.println("Remaining-Balance [ " + results[i].getRemainingBalance() + " ] ...");
        }
        return messageStatus;
    }  
}
