/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.fhi360.lamis.utility.builder.PharmacyListBuilder;

public class PharmacyListInterceptor extends AbstractInterceptor {
    
    @Override        
    public String intercept(ActionInvocation invocation) throws Exception {
        String result = invocation.invoke();
        new PharmacyListBuilder().clearPharmacyList();        
        return result;
    }
}
