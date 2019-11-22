/**
 *
 * @author aalozie
 */

package org.fhi360.lamis.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.dao.hibernate.PatientDAO;
import org.fhi360.lamis.dao.hibernate.SentDAO;

public class AppointmentInterceptor extends AbstractInterceptor {
        
    @Override    
    public String intercept(ActionInvocation actionInvocation) throws Exception {
        String result = actionInvocation.invoke();
        // remove sent messages for the previous appointment from the database
        HttpServletRequest request = ServletActionContext.getRequest();
        Long patientId = Long.parseLong(request.getParameter("patientId"));
        String phone = PatientDAO.findPhone(patientId);
        if (phone != null) {
            SentDAO.delete(phone);
        }
        return result;
    }
}
