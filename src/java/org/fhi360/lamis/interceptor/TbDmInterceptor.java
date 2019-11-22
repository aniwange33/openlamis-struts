/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.interceptor;

import java.util.*;
import java.sql.PreparedStatement;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.xwork.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.dao.hibernate.ChroniccareDAO;
import org.fhi360.lamis.model.Chroniccare;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class TbDmInterceptor extends AbstractInterceptor {

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    private ArrayList<Map<String, String>> tbscreenList = new ArrayList<>();
    private ArrayList<Map<String, String>> dmscreenList = new ArrayList<>();

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        String result = invocation.invoke();
        saveTbDm();
        return result;
    }

    private void saveTbDm() {
        try {
            request = ServletActionContext.getRequest();
            session = ServletActionContext.getRequest().getSession();
            long facilityId = (Long) session.getAttribute("facilityId");
            Long patientId = null;
            if (request.getParameter("chroniccareId") != null) {
                Chroniccare chroniccare = ChroniccareDAO.find(Long.parseLong(request.getParameter("chroniccareId")));
                patientId = chroniccare.getPatientId();
            } else {
                patientId = Long.parseLong(request.getParameter("patientId"));
            }
            String dateVisit = request.getParameter("dateVisit");

            query = "DELETE FROM tbscreenhistory WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_visit = '" + DateUtil.parseStringToSqlDate(dateVisit, "MM/dd/yyyy") + "'";
            executeUpdate(query);

            String value = "";
            String description = "";
            if (request.getParameterMap().containsKey("valueTb")) {
                value = request.getParameter("valueTb");
                description = request.getParameter("descriptionTb");
            }
            String desc = request.getParameter("description");

            if (StringUtils.isNotBlank(value) && StringUtils.isNotBlank(desc)) {
                query = "INSERT INTO tbscreenhistory (facility_id, patient_id, date_visit, description, value, time_stamp) VALUES(" + facilityId + ", " + patientId + ", '" + DateUtil.parseStringToSqlDate(dateVisit, "MM/dd/yyyy") + "', '" + description + "', '" + value + "', NOW())";
            }

            // retrieve the list stored as an attribute in session object
            if (session.getAttribute("tbscreenList") != null) {
                tbscreenList = (ArrayList) session.getAttribute("tbscreenList");
            }

            for (int i = 0; i < tbscreenList.size(); i++) {
                value = (String) tbscreenList.get(i).get("valueTb"); // retrieve id from list
                description = (String) tbscreenList.get(i).get("description");
                if (StringUtils.isNotBlank(value)) {
                    query = "INSERT INTO tbscreenhistory (facility_id, patient_id, date_visit, description, value, time_stamp) VALUES(" + facilityId + ", " + patientId + ", '" + DateUtil.parseStringToSqlDate(dateVisit, "MM/dd/yyyy") + "', '" + description + "', '" + value + "', NOW())";
                    executeUpdate(query);
                }
            }

            // save dm screening
            query = "DELETE FROM dmscreenhistory WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_visit = '" + DateUtil.parseStringToSqlDate(dateVisit, "MM/dd/yyyy") + "'";
            executeUpdate(query);

            value = "";
            description = "";
            if (request.getParameterMap().containsKey("valueDm")) {
                value = request.getParameter("valueDm");
                description = request.getParameter("descriptionDm");
            }
            if (StringUtils.isNotBlank(value) && StringUtils.isNotBlank(desc)) {
                query = "INSERT INTO dmscreenhistory (facility_id, patient_id, date_visit, description, value, time_stamp) VALUES(" + facilityId + ", " + patientId + ", '" + DateUtil.parseStringToSqlDate(dateVisit, "MM/dd/yyyy") + "', '" + description + "', '" + value + "', NOW())";
            }

            // retrieve the list stored as an attribute in session object
            if (session.getAttribute("dmscreenList") != null) {
                dmscreenList = (ArrayList) session.getAttribute("dmscreenList");
            }

            for (int i = 0; i < dmscreenList.size(); i++) {
                value = (String) dmscreenList.get(i).get("valueDm"); // retrieve id from list
                description = (String) dmscreenList.get(i).get("description");
                if (StringUtils.isNotBlank(value)) {
                    query = "INSERT INTO dmscreenhistory (facility_id, patient_id, date_visit, description, value, time_stamp) VALUES(" + facilityId + ", " + patientId + ", '" + DateUtil.parseStringToSqlDate(dateVisit, "MM/dd/yyyy") + "', '" + description + "', '" + value + "', NOW())";
                    executeUpdate(query);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeUpdate(String query) {
        try {
            transactionTemplate.execute((ts) -> {
                jdbcTemplate.execute(query);
                return null; //To change body of generated lambdas, choose Tools | Templates.
            });
        } catch (Exception exception) {

        }
    }

}
