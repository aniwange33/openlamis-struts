/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.fhi360.lamis.controller.ClinicAction;
import org.fhi360.lamis.controller.LaboratoryAction;
import org.fhi360.lamis.controller.PatientAction;
import org.fhi360.lamis.controller.PharmacyAction;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.controller.CaseManagementAction;
import org.fhi360.lamis.controller.StatusHistoryAction;
import org.fhi360.lamis.interceptor.updater.CaseManagerAssignmentUpdater;
import org.fhi360.lamis.interceptor.updater.PatientClinicAttributeUpdater;
import org.fhi360.lamis.interceptor.updater.PatientLabAttributeUpdater;
import org.fhi360.lamis.interceptor.updater.PatientRefillAttributeUpdater;
import org.fhi360.lamis.interceptor.updater.PatientStatusAttributeUpdater;
import org.fhi360.lamis.interceptor.updater.StatusHistoryUpdater;

public class AfterUpdateInterceptor extends AbstractInterceptor {
    
    @Override    
    public String intercept(ActionInvocation invocation) throws Exception {
        String result = invocation.invoke();
        Object action = invocation.getAction();
        HttpSession session = ServletActionContext.getRequest().getSession();
        boolean isImport = false;
        if(session.getAttribute("isImport") != null) {
             isImport = (Boolean) session.getAttribute("isImport");
        }
        if(!isImport) {
            if(action instanceof PatientAction) {
                System.out.println("AfterUpdateInterceptor...... fired");
                new StatusHistoryUpdater().logStatusChange();
            }
            if(action instanceof ClinicAction) {
                System.out.println("Clinic action");
                new PatientClinicAttributeUpdater().lastClinicDate();
            }
            if(action instanceof PharmacyAction) {
                new PatientRefillAttributeUpdater().lastRefillDate(false);
            }
            if(action instanceof LaboratoryAction) {
                //Update the last cd4 and last viral load fields in patient table
                new PatientLabAttributeUpdater().lastCd4Date();
                new PatientLabAttributeUpdater().lastViralLoadDate();
            }        
            if(action instanceof StatusHistoryAction) {
                new PatientStatusAttributeUpdater().updateStatus();
            }
            if(action instanceof CaseManagementAction) {
                System.out.println("Got Here in log...");
                new CaseManagerAssignmentUpdater().logAssignmentChange();
            }
        }
        return result;
    }
}
