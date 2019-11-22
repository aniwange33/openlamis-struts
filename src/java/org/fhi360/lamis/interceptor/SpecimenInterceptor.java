/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.interceptor;

import org.fhi360.lamis.service.SpecimenProcessorService;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.struts2.ServletActionContext;

public class SpecimenInterceptor extends AbstractInterceptor {
    
    @Override    
    public String intercept(ActionInvocation invocation) throws Exception {
        String result = invocation.invoke();
        String testable = ServletActionContext.getRequest().getParameter("reasonNoTest");
        if(!testable.equalsIgnoreCase("Yes")){
            String hospitalNum = ServletActionContext.getRequest().getParameter("hospitalNum"); 
            String surname = ServletActionContext.getRequest().getParameter("surname"); 
            String otherNames = ServletActionContext.getRequest().getParameter("otherNames");
            String gender = ServletActionContext.getRequest().getParameter("gender");
            String reasonNoTest = ServletActionContext.getRequest().getParameter("reasonNoTest");
            String facilityName = ServletActionContext.getRequest().getParameter("facilityName");
            String phone = ServletActionContext.getRequest().getParameter("senderPhone");
            String message = "81Sample not testable, Please resend - Patient ID: " + hospitalNum + ", Name: " + surname + " " + otherNames + ", Gender: " + gender + ", Hospital: " + facilityName;
            SpecimenProcessorService.sendSms(phone, message);
        }
        return result;
    }
}
