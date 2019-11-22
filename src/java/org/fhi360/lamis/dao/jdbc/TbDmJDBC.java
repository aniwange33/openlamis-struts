/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.dao.jdbc;

import java.util.ArrayList;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author user1
 */
public class TbDmJDBC {

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    private ArrayList<Map<String, String>> tbscreenList = new ArrayList<>();
    private ArrayList<Map<String, String>> dmscreenList = new ArrayList<>();

    public TbDmJDBC() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();

    }

    public void saveTbDm() {
        long facilityId = (Long) session.getAttribute("facilityId");
        long patientId = Long.parseLong(request.getParameter("patientId"));
        String dateVisit = request.getParameter("dateVisit");

        try {
            query = "DELETE FROM tbscreenhistory WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_visit = '" + DateUtil.parseStringToSqlDate(dateVisit, "MM/dd/yyyy") + "'";
            executeUpdate(query);

            String value = "";
            String description = "";
            if (request.getParameterMap().containsKey("valueTb")) {
                value = request.getParameter("valueTb");
                description = request.getParameter("descriptionTb");
            }

            if (!value.equals("") && !value.isEmpty() && !request.getParameter("description").equals("") && !request.getParameter("description").isEmpty()) {
                query = "INSERT INTO tbscreenhistory (facility_id, patient_id, date_visit, description, value, time_stamp) VALUES(" + facilityId + ", " + patientId + ", '" + DateUtil.parseStringToSqlDate(dateVisit, "MM/dd/yyyy") + "', '" + description + "', '" + value + "', NOW())";
            }

            // retrieve the list stored as an attribute in session object
            if (session.getAttribute("tbscreenList") != null) {
                tbscreenList = (ArrayList) session.getAttribute("tbscreenList");
            }

            for (int i = 0; i < tbscreenList.size(); i++) {
                value = (String) tbscreenList.get(i).get("valueTb"); // retrieve id from list
                description = (String) tbscreenList.get(i).get("description");
                if (!value.equals("") && !value.isEmpty()) {
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

            if (!value.equals("") && !value.isEmpty() && !request.getParameter("description").equals("") && !request.getParameter("description").isEmpty()) {
                query = "INSERT INTO dmscreenhistory (facility_id, patient_id, date_visit, description, value, time_stamp) VALUES(" + facilityId + ", " + patientId + ", '" + DateUtil.parseStringToSqlDate(dateVisit, "MM/dd/yyyy") + "', '" + description + "', '" + value + "', NOW())";
            }

            // retrieve the list stored as an attribute in session object
            if (session.getAttribute("dmscreenList") != null) {
                dmscreenList = (ArrayList) session.getAttribute("dmscreenList");
            }

            for (int i = 0; i < dmscreenList.size(); i++) {
                value = (String) dmscreenList.get(i).get("valueDm"); // retrieve id from list
                description = (String) dmscreenList.get(i).get("description");
                if (!value.equals("") && !value.isEmpty()) {
                    query = "INSERT INTO dmscreenhistory (facility_id, patient_id, date_visit, description, value, time_stamp) VALUES(" + facilityId + ", " + patientId + ", '" + DateUtil.parseStringToSqlDate(dateVisit, "MM/dd/yyyy") + "', '" + description + "', '" + value + "', NOW())";
                    executeUpdate(query);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {

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
