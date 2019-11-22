/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.sms;

/**
 *
 * @author user1
 */
public class NotifierRegistrationHandler {
    
    public String process(Sms sms) {        
        String msg = "";
        SmsOriginatorVerifier smsOriginatorVerifier = new SmsOriginatorVerifier();
        if(smsOriginatorVerifier.isOriginatorRegistered(sms.getFrom())) {
            msg = "Notifier already registered in the system";
        }
        else {
            //register(from);
            msg = "Notifier successfuly registered";
        }       
        return msg;
    }     
}
