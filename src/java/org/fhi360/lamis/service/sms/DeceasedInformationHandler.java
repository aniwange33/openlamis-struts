/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.sms;

/**
 *
 * @author user1
 */
public class DeceasedInformationHandler {
    private SmsOriginatorVerifier smsOriginatorVerifier = new SmsOriginatorVerifier();

    public String process(Sms sms) {
        String msg = "";        
        if(smsOriginatorVerifier.isOriginatorRegistered(sms.getFrom())) {
            if(smsOriginatorVerifier.isFacilityRegistered(sms.getText())) {
                msg = tracker(sms.getText());                             
            }
            else {
                msg = "Facility not registered, please register the facility first before sending notification";
            }
        }
        else {
            msg = "Notifier not registered, please register as a notifier";
        }
        return msg;
    }

    private String tracker(String text) {        
        return "";
    } 
    
}
