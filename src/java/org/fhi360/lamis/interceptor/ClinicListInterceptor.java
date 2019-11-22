/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.fhi360.lamis.utility.builder.ClinicListBuilder;

public class ClinicListInterceptor extends AbstractInterceptor {
        
    @Override    
    public String intercept(ActionInvocation invocation) throws Exception {
        String result = invocation.invoke();
        new ClinicListBuilder().clearClinicList();        
        return result;
    }
}
