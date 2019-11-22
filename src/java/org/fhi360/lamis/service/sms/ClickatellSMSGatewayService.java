/**
 *
 * @author user1
 */
package org.fhi360.lamis.service.sms;


import org.fhi360.lamis.utility.PropertyAccessor;
import java.io.IOException;
import java.util.Map;
import org.marre.SmsSender;
import org.marre.sms.SmsException;

public class ClickatellSMSGatewayService {

    public String sendSms(String message, String phone) throws SmsException {
        String messageStatus = "UNSENT";  //String sender = "41798073045";
        Map<String, Object> map = new PropertyAccessor().getSystemProperties();
        //Format the phone number by adding country code and removing the plus sign (+)
        String countryCode = (String) map.get("countryCode");
        phone = phone.replaceFirst("\\d", countryCode).replace("+", "");
       
        SmsSender smsSender = SmsSender.getClickatellSender((String) map.get("userName"), (String) map.get("password"), (String) map.get("api"));               
        try {
            smsSender.connect();
            messageStatus = smsSender.sendTextSms(message, phone);  
            messageStatus = "SENT";
            smsSender.disconnect();
        }
        catch(IOException exception) {
            exception.printStackTrace();
        }
        catch(SmsException exception) {
            exception.printStackTrace();
        }
        return messageStatus;
    }       
            
}
