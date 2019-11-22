/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import java.util.Date;
import org.fhi360.lamis.utility.PropertyAccessor;

public class DqaInterceptor extends AbstractInterceptor {
        
    @Override    
    public String intercept(ActionInvocation invocation) throws Exception {
        String result = invocation.invoke();
        new PropertyAccessor().setDateLastDqa(new Date());
        return result;
    }
}
