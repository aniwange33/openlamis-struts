/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class DmscreenListInterceptor extends AbstractInterceptor {

    @Override    
    public String intercept(ActionInvocation invocation) throws Exception {
        String result = invocation.invoke();
        DmscreenList.initList();
        return result;
    }    
}
