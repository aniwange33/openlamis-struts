/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class TriggerInitInterceptor extends AbstractInterceptor {

    @Override   
    public String intercept(ActionInvocation invocation) throws Exception {
        String result = invocation.invoke();
        //new InitialTriggers().init();
        return result;
    }    
}
