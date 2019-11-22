/**
 *
 * @author aalozie
 */

package org.fhi360.lamis.service.sms;
import org.fhi360.lamis.utility.PropertyAccessor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.smslib.AGateway;
import org.smslib.AGateway.GatewayStatuses;
import org.smslib.AGateway.Protocols;
import org.smslib.ICallNotification;
import org.smslib.IGatewayStatusNotification;
import org.smslib.IInboundMessageNotification;
import org.smslib.IOrphanedMessageNotification;
import org.smslib.IOutboundMessageNotification;
import org.smslib.InboundMessage;
import org.smslib.InboundMessage.MessageClasses;
import org.smslib.Message.MessageTypes;
import org.smslib.OutboundMessage;
import org.smslib.SMSLibException;
import org.smslib.Service;
import org.smslib.Service.ServiceStatus;
import org.smslib.modem.SerialModemGateway;

public class ModemGatewayService {
    
    public void startSmsService() throws Exception {
        Map<String, Object> map = new PropertyAccessor().getSystemProperties();
        String model = (String) map.get("model");
        String comPort = (String) map.get("comPort");
        Integer baudRate = (Integer) map.get("baudRate");
        String Manufacturer = (String) map.get("Manufacturer");
        
        OutboundNotification outboundNotification = new OutboundNotification();         // Create the notification callback method for outbound & status report messages.
        InboundNotification inboundNotification = new InboundNotification();            // Create the notification callback method for inbound & status report messages.
        CallNotification callNotification = new CallNotification();                   // Create the notification callback method for inbound voice calls.
        GatewayStatusNotification statusNotification = new GatewayStatusNotification();               //Create the notification callback method for gateway statuses.
        OrphanedMessageNotification orphanedMessageNotification = new OrphanedMessageNotification();
        
        SerialModemGateway gateway = new SerialModemGateway("modem.com1", comPort, baudRate, Manufacturer, model);
        // Set the modem protocol to PDU (alternative is TEXT). PDU is the default
        gateway.setProtocol(Protocols.PDU);
        // Do we want the Gateway to be used for Inbound messages?
        gateway.setInbound(true);
        // Do we want the Gateway to be used for Outbound messages?
        gateway.setOutbound(true);
        // Let SMSLib know which is the SIM PIN.
        gateway.setSimPin("0000");
        //Set the storage location for messages from modem (another location is "SMME"); 
        gateway.getATHandler().setStorageLocations("MESM");  
        
        // Set up the notification methods.
        Service.getInstance().setOutboundMessageNotification(outboundNotification);
        Service.getInstance().setInboundMessageNotification(inboundNotification);
        //Service.getInstance().setCallNotification(callNotification);
        //Service.getInstance().setGatewayStatusNotification(statusNotification);
        //Service.getInstance().setOrphanedMessageNotification(orphanedMessageNotification);
        Service.getInstance().addGateway(gateway);
        Service.getInstance().startService();
        System.out.println("Service started......");
    }

    public void stopSmsService() {
        try {
            Service.getInstance().stopService();
        } 
        catch (SMSLibException ex) {
            Logger.getLogger(SmsListener.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (IOException ex) {
            Logger.getLogger(SmsListener.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (InterruptedException ex) {
            Logger.getLogger(SmsListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deleteMsg(AGateway gateway, InboundMessage msg) {
        try {
            gateway.deleteMessage(msg);
        } 
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    
    public String sendSms(String message, String phone) throws Exception {
        Map<String, Object> map = new PropertyAccessor().getSystemProperties();
        String countryCode = (String) map.get("countryCode");
        countryCode = countryCode.replace("+", "");
        countryCode = "+"+countryCode;
        OutboundMessage msg = new OutboundMessage(phone.replaceFirst("\\d", countryCode), message);
        Service.getInstance().sendMessage(msg);
        return msg.getMessageStatus().name();
    }

    //This method is an alternative to reading message is the modem cannot poll sms through the inbound notification class
    public void readSms() throws Exception {
        Sms sms = new Sms();
        List<InboundMessage> msgList = new ArrayList<InboundMessage>();    // Define a list which will hold the read messages.
        Service.getInstance().readMessages(msgList, MessageClasses.ALL);
        for (InboundMessage msg : msgList) {
            String text = msg.getText().toString();
            String originator = msg.getOriginator().toString();
            String date = msg.getDate().toString(); 
            System.out.println(text);
            System.out.println(originator);
            System.out.println(date);
            sms.setFrom(originator);
            sms.setText(text);
            sms.setOriginatorId(0L);
            SmsMessageHandlerDelegator smsMessageHandlerDelegator = SmsMessageHandlerDelegator.getInstance();
            smsMessageHandlerDelegator.delegate(sms);            
        }
        Thread.sleep(30000);
    }
    
    public void delegate(String text, String originator) {
        Sms sms = new Sms();
        originator = originator.substring(originator.length()-11).replaceFirst("\\d", "0");
        sms.setFrom(originator);
        sms.setText(text);
        sms.setOriginatorId(0L);
        SmsMessageHandlerDelegator smsMessageHandlerDelegator = SmsMessageHandlerDelegator.getInstance();
        smsMessageHandlerDelegator.delegate(sms);                    
    }
    
    public boolean isStarted() {
        ServiceStatus status = Service.getInstance().getServiceStatus();
        if (status == ServiceStatus.STARTED || status == ServiceStatus.STARTING) {
            return true;
        } 
        else {
            return false;
        }        
    }
    
    public int getServiceStatus() {
        ServiceStatus serviceStatus = Service.getInstance().getServiceStatus();
        if (serviceStatus == ServiceStatus.STARTED) {
            return 1;  //Service started
        } 
        else if (serviceStatus == ServiceStatus.STARTING) {
            return 2;  //Service starting
        } 
        else if (serviceStatus == ServiceStatus.STOPPED) {
            return 3;  //Service stopped
        } 
        else {
            return 4;  //Service stopping
        }
    }

}
class OutboundNotification implements IOutboundMessageNotification {
    public void process(AGateway gateway, OutboundMessage msg) {
        System.out.println("Outbound handler called from Gateway: " + gateway.getGatewayId());
        System.out.println(msg);
    }
}

class InboundNotification implements IInboundMessageNotification {
    public void process(AGateway gateway, MessageTypes msgType, InboundMessage msg) {
        if (msgType == MessageTypes.INBOUND) {
            System.out.println(">>> New Inbound message detected from Gateway: " + gateway.getGatewayId());
            
            String text = msg.getText().toString();
            String originator = msg.getOriginator().toString();
            String date = msg.getDate().toString(); 
            
            System.out.println(text);
            System.out.println(originator);
            System.out.println(date);
            
            ModemGatewayService service = new ModemGatewayService();
            service.delegate(text, originator);
        }
        else if (msgType == MessageTypes.STATUSREPORT) {
            System.out.println(">>> New Inbound Status Report message detected from Gateway: " + gateway.getGatewayId());
        }
        try {
            gateway.deleteMessage(msg);
        } 
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}

class CallNotification implements ICallNotification {
    public void process(AGateway gateway, String callerId) {
        System.out.println(">>> New call detected from Gateway: " + gateway.getGatewayId() + " : " + callerId);
    }
}

class GatewayStatusNotification implements IGatewayStatusNotification {
    public void process(AGateway gateway, GatewayStatuses oldStatus, GatewayStatuses newStatus) {
        System.out.println(">>> Gateway Status change for " + gateway.getGatewayId() + ", OLD: " + oldStatus + " -> NEW: " + newStatus);
    }
}

class OrphanedMessageNotification implements IOrphanedMessageNotification {
    public boolean process(AGateway gateway, InboundMessage msg) {
        System.out.println(">>> Orphaned message part detected from " + gateway.getGatewayId());
        System.out.println(msg);
        return false;   // Since we are just testing, return FALSE and keep the orphaned message part.
    }
}

