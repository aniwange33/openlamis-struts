/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.fhi360.lamis.service.AsyncTaskService;

public class AsyncTaskInterceptor extends AbstractInterceptor {

    @Override   
    public String intercept(ActionInvocation invocation) throws Exception {
        String result = invocation.invoke();
        new AsyncTaskService().doPerformanceAnalyzer();
        return result;
    }    
}
