/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.fhi360.lamis.utility.builder.AncListBuilder;

public class AncListInterceptor extends AbstractInterceptor { 
    public String intercept(ActionInvocation invocation) throws Exception {
        String result = invocation.invoke();
        new AncListBuilder().clearAncList();
        return result;
    }
}
