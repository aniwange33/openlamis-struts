/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.utility.builder;

import java.util.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.utility.DateUtil;

public class StatusHistoryBuilder {

    private HttpServletRequest request;
    private HttpSession session;

    private ArrayList<Map<String, String>> statusList = new ArrayList<>();

    public StatusHistoryBuilder() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    public void buildStatusList(ResultSet resultSet) throws SQLException {
        try {
            // loop through resultSet for each row and put into Map
            while (resultSet.next()) {
                String historyId = Long.toString(resultSet.getLong("history_id"));
                String patientId = Long.toString(resultSet.getLong("patient_id"));
                String facilityId = Long.toString(resultSet.getLong("facility_id"));
                String statusRegistration = resultSet.getObject("status_registration") == null ? "" : resultSet.getString("status_registration");
                String dateRegistration = resultSet.getObject("date_registration") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_registration"), "MM/dd/yyyy");
                String currentStatus = resultSet.getObject("current_status") == null ? "" : resultSet.getString("current_status");
                String dateCurrentStatus = resultSet.getObject("date_current_status") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_current_status"), "MM/dd/yyyy");
                String dateStarted = resultSet.getObject("date_started") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_started"), "MM/dd/yyyy");
                String outcome = resultSet.getObject("outcome") == null ? "" : resultSet.getString("outcome");
                String dateTracked = resultSet.getObject("date_tracked") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_tracked"), "MM/dd/yyyy");

                String deletable = "1";

                if (statusRegistration.equals(currentStatus) && dateRegistration.equals(dateCurrentStatus)) {
                    deletable = "0";
                }
                if (currentStatus.equals("ART Start") && dateStarted.equals(dateCurrentStatus)) {
                    deletable = "0";
                }

                Map<String, String> map = new HashMap<>();
                map.put("historyId", historyId);
                map.put("patientId", patientId);
                map.put("facilityId", facilityId);
                map.put("currentStatus", currentStatus);
                map.put("dateCurrentStatus", dateCurrentStatus);
                map.put("deletable", deletable);
                map.put("status", "");
                map.put("outcome", outcome);
                map.put("dateTracked", dateTracked);
                statusList.add(map);
            }
            session.setAttribute("statusList", statusList);
            statusList = null;
        } catch (SQLException sqlException) {
            throw sqlException;
        }
    }

    public void buildStatusRecord(ResultSet resultSet) throws SQLException {
        try {
            // loop through resultSet for each row and put into Map
            while (resultSet.next()) {
                String historyId = Long.toString(resultSet.getLong("history_id"));
                String patientId = Long.toString(resultSet.getLong("patient_id"));
                String facilityId = Long.toString(resultSet.getLong("facility_id"));
                String statusRegistration = resultSet.getObject("status_registration") == null ? "" : resultSet.getString("status_registration");
                String dateRegistration = resultSet.getObject("date_registration") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_registration"), "MM/dd/yyyy");
                String currentStatus = resultSet.getObject("current_status") == null ? "" : resultSet.getString("current_status");
                String dateCurrentStatus = resultSet.getObject("date_current_status") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_current_status"), "MM/dd/yyyy");
                String dateStarted = resultSet.getObject("date_started") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_started"), "MM/dd/yyyy");
                String outcome = resultSet.getObject("outcome") == null ? "" : resultSet.getString("outcome");
                String dateTracked = resultSet.getObject("date_tracked") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_tracked"), "MM/dd/yyyy");
                String reasonInterrupt = resultSet.getObject("reason_interrupt") == null ? "" : resultSet.getString("reason_interrupt");
                String causeDeath = resultSet.getObject("cause_death") == null ? "" : resultSet.getString("cause_death");
                String agreedDate = resultSet.getObject("agreed_date") == null ? "" : resultSet.getString("agreed_date");

                String deletable = "1";

                if (statusRegistration.equals(currentStatus) && dateRegistration.equals(dateCurrentStatus)) {
                    deletable = "0";
                }
                if (currentStatus.equals("ART Start") && dateStarted.equals(dateCurrentStatus)) {
                    deletable = "0";
                }

                Map<String, String> map = new HashMap<>();
                map.put("historyId", historyId);
                map.put("patientId", patientId);
                map.put("facilityId", facilityId);
                map.put("currentStatus", currentStatus);
                map.put("dateCurrentStatus", dateCurrentStatus);
                map.put("deletable", deletable);
                map.put("status", "");
                map.put("outcome", outcome);
                map.put("dateTracked", dateTracked);
                map.put("reasonInterrupt", reasonInterrupt);
                map.put("causeDeath", causeDeath);
                map.put("agreedDate", agreedDate);
                statusList.add(map);
            }
            session.setAttribute("statusList", statusList);
            statusList = null;
        } catch (SQLException sqlException) {
            throw sqlException;
        }
    }

    public ArrayList<Map<String, String>> retrieveStatusList() {
        // retrieve the status record store in session attribute
        if (session.getAttribute("statusList") != null) {
            statusList = (ArrayList) session.getAttribute("statusList");
        }
        return statusList;
    }

}
