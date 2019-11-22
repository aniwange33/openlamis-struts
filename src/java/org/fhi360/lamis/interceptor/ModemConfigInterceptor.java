/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.interceptor;

import org.fhi360.lamis.controller.ModemConfigAction;
import org.fhi360.lamis.service.sms.ModemGatewayService;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class ModemConfigInterceptor extends AbstractInterceptor {
    
    @Override        
    public String intercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();
        String result = invocation.invoke();
        if(action instanceof ModemConfigAction) {
            //stop the gateway, so that when the sms scheduler fires the next time the SmsGateWay service will be restarted
            //using the new values from the sms configuration table
            if(new ModemGatewayService().isStarted()) new ModemGatewayService().stopSmsService();
        }           
        return result;
    }    

    
}
