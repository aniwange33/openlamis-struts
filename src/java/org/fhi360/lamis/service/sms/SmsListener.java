/**
 *
 * @author AALOZIE
 */
package org.fhi360.lamis.service.sms;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class SmsListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        new SmsService().init(); //Initialize smslog at the start of application 
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if(new ModemGatewayService().isStarted()) new ModemGatewayService().stopSmsService();
    }    
}
