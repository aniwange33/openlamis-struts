/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.sms;

/**
 *
 * @author user1
 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsMessageHandlerDelegator {
    private static final String FACILITY_REGISTRATION_SMS_PATTERN = "^[a-zA-Z -']+#[a-zA-Z -']+#[a-zA-Z -']+#(?i)f$";
    private static final String NOTIFIER_REGISTRATION_SMS_PATTERN = "^[0-9]{4}#[a-zA-Z -']+#[a-zA-Z -']+#[0-9]+#(?i)n$";
    private static final String DEATH_NOTIFICATION_SMS_PATTERN = "^[0-9]{4}#[0-9]{1,2}#[0-9]{4}#(?i)dn$";
    private static final String DECEASED_INFORMATION_SMS_PATTERN = "^[a-zA-Z -']+#[0-9]{1,2}#(?i)(c|m|t|o)#(?i)(h|y|i|o)#(?i)di$";
    private static final String DEATH_INFORMATION_SMS_PATTERN = "^(?i)(h|f|o)#[1-5]{1}#[1-9]{1,2}#(?i)(n|y)#(?i)(d|a)#(?i)d$";  //H#5#1#N#A#D
    private static final String DELIVERY_SERVICE_SMS_PATTERN = "^[1-7]#(?i)ds$";    // example 1#DS
    
    private static final String PARTICIPANT_REGISTRATION_SMS_PATTERN = "^(?i)(join)$";    //example JOIN
    private static final String DEACTIVATE_SMS_PATTERN = "^(?i)(stop)$";    //example STOP
    private static final String AGE_SMS_PATTERN = "^[0-9]{2}$";    // example 35
    private static final String GENDER_SMS_PATTERN = "^(?i)(m|f)$";  // example m for Male
    private static final String COMFIRMATION_SMS_PATTERN = "^(?i)(y|n)$"; // example y for Yes
    
    //Implementing singleton using the classic design pattern
    private static SmsMessageHandlerDelegator INSTANCE = null; 
    protected SmsMessageHandlerDelegator() {};    
    public static SmsMessageHandlerDelegator getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new SmsMessageHandlerDelegator();
        }
        return INSTANCE;
    }
    
    public void delegate(Sms sms) {
        SmsOriginatorVerifier smsOriginatorVerifier = new SmsOriginatorVerifier();
        ParticipantRegistrationHandler participantRegistrationHandler = new ParticipantRegistrationHandler();
        SmsConversationService smsConversationService = new SmsConversationService();
        System.out.println("I got it ....."+sms.getFrom());

        String message = "";
        if(smsOriginatorVerifier.isOriginatorRegistered(sms.getFrom())) { 
            smsConversationService.saveSms(sms);  //save sms as a converstaion
        }
        else {
            if(isPatternValid(sms.getText(), PARTICIPANT_REGISTRATION_SMS_PATTERN)) {
                System.out.println("This is a JOIN from " + sms.getFrom() + " this is the message " + sms.getText());
                participantRegistrationHandler.savePhone(sms.getFrom()); //save phone no in sequencer table
            } 
            else {
                if(isPatternValid(sms.getText(), AGE_SMS_PATTERN)) {
                    System.out.println("This is a age from " + sms.getFrom() + " this is the message " + sms.getText());
                    participantRegistrationHandler.saveAge(sms.getText()); //save age in sequencer table
                }
                else {
                    if(isPatternValid(sms.getText(), GENDER_SMS_PATTERN)) {
                        System.out.println("This is a gender from " + sms.getFrom() + " this is the message " + sms.getText());
                        participantRegistrationHandler.saveGender(sms.getText()); //save gender in sequencer table
                    }
                    else {
                        System.out.println("This is a conversation from " + sms.getFrom() + " this is the message " + sms.getText());
                        participantRegistrationHandler.saveLocation(sms.getText()); //save location in sequencer table                        
                    }
                }
            }
            message = participantRegistrationHandler.getNextStepMsg(sms.getFrom());
            System.out.println(message);
            String msgDeliveryStatus = new SmsMessageSender().send(message, sms.getFrom());
        }        
    }

    private boolean isPatternValid(String text, String regex) {       
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }
    
}
