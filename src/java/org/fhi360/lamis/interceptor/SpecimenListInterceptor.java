/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.fhi360.lamis.utility.builder.EidListBuilder;
import org.fhi360.lamis.utility.builder.SpecimenListBuilder;

public class SpecimenListInterceptor extends AbstractInterceptor {
    
    @Override        
    public String intercept(ActionInvocation invocation) throws Exception {
        String result = invocation.invoke();
        new SpecimenListBuilder().clearSpecimenList();        
        new EidListBuilder().clearEidList();        
        return result;
    }
}
