/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.sms.SmsMessageSender;
import org.fhi360.lamis.utility.StringUtil;

/**
 *
 * @author user1
 */
public class CommunitypharmInterceptor extends AbstractInterceptor {
    @Override    
    public String intercept(ActionInvocation actionInvocation) throws Exception {
        String result = actionInvocation.invoke();
        //Send activation code to community pharmacist
        HttpServletRequest request = ServletActionContext.getRequest();
        String message = "Your account activation code is " + request.getParameter("pin");
        String phone = request.getParameter("phone");
        System.out.println("Message...."+message);
        if(isValidPhone(phone)) {
            new SmsMessageSender().send(message, phone);            
        }
        return result;
    }
    
    private boolean isValidPhone(String phone) {
        if(!StringUtil.isInteger(phone)) return false;
        return phone.length() == 11;
    }

}
