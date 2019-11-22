/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.utility.builder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.utility.Scrambler;

/**
 *
 * @author user10
 */
public class NotificationListBuilder {

    private HttpServletRequest request;
    private HttpSession session;
    private DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    private ArrayList<Map<String, String>> notificationListReport = new ArrayList<Map<String, String>>();

    private ResultSet rs;

    public NotificationListBuilder() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    public Map<String, String> buildNotificationListCount(Integer entity, ResultSet resultSet) throws SQLException {

        // loop through resultSet for each row and put into Map
        Map<String, String> map = null;
        try {

            while (resultSet.next()) {
                String testOrdered = (String.valueOf(resultSet.getInt("notification_count")) == null) ? "" : String.valueOf(resultSet.getInt("notification_count"));

                // loop through resultSet for each row and put into Map
                map = new HashMap<>();

                int i = 0;

                switch (entity) {
                    case 1:
                        map.put("enrolled", testOrdered);
                        map.put("id", "1");
                        map.put("entity", "Number of clients enrolled but not on treatment");
                        map.put("entityCount", testOrdered);
                        map.put("type", "enrolled");
                        break;
                    case 2:
                        map.put("lostUnconfirmed", testOrdered);
                        map.put("id", "2");
                        map.put("entity", "Number of clients who are lost to follow-up unconfirmed");
                        map.put("entityCount", testOrdered);
                        map.put("type", "lostUnconfirmed");
                        break;
//                    case 3:
//                        map.put("txNoArv", testOrdered);
//                        map.put("id", "3");
//                        map.put("entity", "Number of clients on treatment but no first ARV dispensed");
//                        map.put("entityCount", testOrdered);
//                        map.put("type", "txNoArv");
//                    break;
                    case 3:
                        map.put("txNoVl", testOrdered);
                        map.put("id", "3");
                        map.put("entity", "Number of clients on treatment who are due for viral load test");
                        map.put("entityCount", testOrdered);
                        map.put("type", "txNoVl");
                        break;
                    case 4:
                        map.put("vlUnsupressed", testOrdered);
                        map.put("id", "4");
                        map.put("entity", "Number of clients on treatment with viral load un-suppressed");
                        map.put("entityCount", testOrdered);
                        map.put("type", "vlUnsupressed");
                        break;
                }

            }
        } catch (SQLException sqlException) {
            throw sqlException;
        }

        return map;
    }

    public ArrayList<Map<String, String>> buildNotificationListData(ResultSet resultSet) throws SQLException {

        Map<String, String> map = null;
        try {
            // loop through resultSet for each row and put into Map

            while (resultSet.next()) {
                String dateRegistration = (resultSet.getDate("date_registration") == null) ? "" : dateFormat.format(resultSet.getDate("date_registration"));
                String statusRegistration = (resultSet.getString("current_status") == null) ? "" : resultSet.getString("current_status");
                String surname = (resultSet.getString("surname") == null) ? "" : resultSet.getString("surname");
                String otherNames = (resultSet.getString("other_names") == null) ? "" : resultSet.getString("other_names");
                String hospitalNum = (resultSet.getString("hospital_num") == null) ? "" : resultSet.getString("hospital_num");
                String patientId = (resultSet.getString("patient_id") == null) ? "" : resultSet.getString("patient_id");

                map = new HashMap<>();
                String name = new Scrambler().unscrambleCharacters(surname).toUpperCase() + " " + new Scrambler().unscrambleCharacters(otherNames).toUpperCase();;
                map.put("name", name);
                map.put("dateRegistration", dateRegistration);
                map.put("statusRegistration", statusRegistration);
                map.put("hospitalNum", hospitalNum);
                map.put("patientId", patientId);

                notificationListReport.add(map);

            }
        } catch (SQLException sqlException) {
            throw sqlException;
        }
        return notificationListReport;
    }

    public Map<String, String> buildPrescriptionNotificationCount(Integer entity, ResultSet resultSet) throws SQLException {

        // loop through resultSet for each row and put into Map
        Map<String, String> map = null;
        try {

            resultSet.beforeFirst();
            while (resultSet.next()) {
                String prescribed = (String.valueOf(resultSet.getInt("notification_count")) == null) ? "" : String.valueOf(resultSet.getInt("notification_count"));

                // loop through resultSet for each row and put into Map
                map = new HashMap<>();

                int i = 0;

                switch (entity) {
                    case 1:
                        map.put("prescriptions", prescribed);
                        map.put("id", "1");
                        map.put("entity", "Number of clients with drug prescriptions");
                        map.put("entityCount", prescribed);
                        map.put("type", "prescriptions");
                        break;

                    case 2:
                        map.put("labtests", prescribed);
                        map.put("id", "2");
                        map.put("entity", "Number of clients with labtest prescriptions");
                        map.put("entityCount", prescribed);
                        map.put("type", "labtests");
                        break;

                }

            }
        } catch (SQLException sqlException) {
            throw sqlException;
        }

        return map;
    }
}
