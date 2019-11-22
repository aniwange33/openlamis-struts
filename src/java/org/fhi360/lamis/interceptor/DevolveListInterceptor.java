/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.fhi360.lamis.utility.builder.DevolveListBuilder;

/**
 *
 * @author user1
 */
public class DevolveListInterceptor extends AbstractInterceptor {
    
    @Override        
    public String intercept(ActionInvocation invocation) throws Exception {
        String result = invocation.invoke();
        new DevolveListBuilder().clearDevolveList();        
        return result;
    }
}
