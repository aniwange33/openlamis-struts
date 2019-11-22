/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.sms;

/**
 *
 * @author user1
 */
public class FacilityRegistrationHandler {
    private SmsOriginatorVerifier smsOriginatorVerifier = new SmsOriginatorVerifier();
    
    public String process(Sms sms) {
        String msg = "";
        if(smsOriginatorVerifier.isOriginatorRegistered(sms.getFrom())) {
            if(smsOriginatorVerifier.isFacilityRegistered(sms.getText())) {
                msg = "Facility ready registered";
            }
            else {
                //register(text);
                msg = "Facility successfuly registered";
            }
        }
        else {
            msg = "Notifier not registered, please register as a notifier";
        }       
        return msg;
    }    
}
