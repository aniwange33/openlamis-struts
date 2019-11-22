/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.interceptor;

import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import java.util.HashMap;
import java.util.Map;
import org.fhi360.lamis.controller.AncAction;
import org.fhi360.lamis.controller.ChildAction;
import org.fhi360.lamis.controller.ChildfollowupAction;
import org.fhi360.lamis.controller.ChroniccareAction;
import org.fhi360.lamis.controller.PatientAction;
import org.fhi360.lamis.controller.ClinicAction;
import org.fhi360.lamis.controller.DeliveryAction;
import org.fhi360.lamis.controller.DevolveAction;
import org.fhi360.lamis.controller.EacAction;
import org.fhi360.lamis.controller.HtsAction;
import org.fhi360.lamis.controller.IndexcontactAction;
import org.fhi360.lamis.controller.LaboratoryAction;
import org.fhi360.lamis.controller.MaternalfollowupAction;
import org.fhi360.lamis.controller.PharmacyAction;
import org.fhi360.lamis.controller.SpecimenAction;
import org.fhi360.lamis.controller.StatusHistoryAction;
import org.fhi360.lamis.service.MonitorService;

public class MonitorServiceInterceptor extends AbstractInterceptor {

    private int operationId;

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        String result = invocation.invoke();
        Object action = invocation.getAction();
        try {
            if (action instanceof PatientAction) {
                String entityId = ServletActionContext.getRequest().getParameter("hospitalNum");
                MonitorService.logEntity(entityId, "patient", getOperationId());
            }
            if (action instanceof ClinicAction) {
                String entityId = ServletActionContext.getRequest().getParameter("hospitalNum") + "#" + ServletActionContext.getRequest().getParameter("dateVisit");
                MonitorService.logEntity(entityId, "clinic", getOperationId());
                if (getOperationId() == 3) {
                    clearPatientId();
                } else {
                    persistPatientDetail();
                }
            }
            if (action instanceof PharmacyAction) {
                String entityId = ServletActionContext.getRequest().getParameter("hospitalNum") + "#" + ServletActionContext.getRequest().getParameter("dateVisit");
                MonitorService.logEntity(entityId, "pharmacy", getOperationId());
                if (getOperationId() == 3) {
                    clearPatientId();
                } else {
                    persistPatientDetail();
                }
            }
            if (action instanceof LaboratoryAction) {
                String entityId = ServletActionContext.getRequest().getParameter("hospitalNum") + "#" + ServletActionContext.getRequest().getParameter("dateReported");
                MonitorService.logEntity(entityId, "laboratory", getOperationId());
                if (getOperationId() == 3) {
                    clearPatientId();
                } else {
                    persistPatientDetail();
                }
            }
            if (action instanceof StatusHistoryAction) {
                String entityId = ServletActionContext.getRequest().getParameter("hospitalNum") + "#" + ServletActionContext.getRequest().getParameter("currentStatus") + "#" + ServletActionContext.getRequest().getParameter("dateCurrentStatus");
                MonitorService.logEntity(entityId, "statushistory", getOperationId());
            }
            if (action instanceof SpecimenAction) {
                String entityId = ServletActionContext.getRequest().getParameter("labno");
                MonitorService.logEntity(entityId, "specimen", getOperationId());
            }
            if (action instanceof ChroniccareAction) {
                String entityId = ServletActionContext.getRequest().getParameter("hospitalNum") + "#" + ServletActionContext.getRequest().getParameter("dateVisit");
                MonitorService.logEntity(entityId, "chroniccare", getOperationId());
            }
            if (action instanceof AncAction) {
                String entityId = ServletActionContext.getRequest().getParameter("hospitalNum") + "#" + ServletActionContext.getRequest().getParameter("dateVisit");
                MonitorService.logEntity(entityId, "anc", getOperationId());
            }
            if (action instanceof DeliveryAction) {
                String entityId = ServletActionContext.getRequest().getParameter("hospitalNum") + "#" + ServletActionContext.getRequest().getParameter("dateDelivery");
                MonitorService.logEntity(entityId, "delivery", getOperationId());
            }
            if (action instanceof ChildAction) {
                String entityId = ServletActionContext.getRequest().getParameter("hospitalNum");
                MonitorService.logEntity(entityId, "child", getOperationId());
            }
            if (action instanceof ChildfollowupAction) {
                String entityId = ServletActionContext.getRequest().getParameter("hospitalNum") + "#" + ServletActionContext.getRequest().getParameter("dateVisit");
                MonitorService.logEntity(entityId, "childfollowup", getOperationId());
            }
            if (action instanceof MaternalfollowupAction) {
                String entityId = ServletActionContext.getRequest().getParameter("hospitalNum") + "#" + ServletActionContext.getRequest().getParameter("dateVisit");
                MonitorService.logEntity(entityId, "maternalfollowup", getOperationId());
            }

            if (action instanceof DevolveAction) {
                String entityId = ServletActionContext.getRequest().getParameter("hospitalNum") + "#" + ServletActionContext.getRequest().getParameter("date_devolved");
                MonitorService.logEntity(entityId, "devolve", getOperationId());
            }

            if (action instanceof HtsAction) {
                String entityId = ServletActionContext.getRequest().getParameter("clientCode");
                MonitorService.logEntity(entityId, "hts", getOperationId());
            }
            if (action instanceof IndexcontactAction) {
                String entityId = ServletActionContext.getRequest().getParameter("indexContactCode");
                MonitorService.logEntity(entityId, "indexcontact", getOperationId());
            }

            if (action instanceof EacAction) {
                String entityId = ServletActionContext.getRequest().getParameter("hospitalNum") + "#" + ServletActionContext.getRequest().getParameter("date_devolved");
                MonitorService.logEntity(entityId, "eac", getOperationId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private void persistPatientDetail() {
        Map<String, String> map = new HashMap<>();
        map.put("patientId", ServletActionContext.getRequest().getParameter("patientId"));
        map.put("hospitalNum", ServletActionContext.getRequest().getParameter("hospitalNum"));
        map.put("name", ServletActionContext.getRequest().getParameter("name"));
        ServletActionContext.getRequest().getSession().setAttribute("patientDetail", map);
    }

    public static void clearPatientId() {
        ServletActionContext.getRequest().getSession().setAttribute("patientObj", null);
    }

    /**
     * @param operationId the operationId to set
     */
    public void setOperationId(int operationId) {
        this.operationId = operationId;
    }

    /**
     * @return the operationId
     */
    public int getOperationId() {
        return operationId;
    }

}
