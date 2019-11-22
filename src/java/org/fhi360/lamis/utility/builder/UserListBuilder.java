/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.utility.builder;

import java.util.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;

public class UserListBuilder {

    private HttpServletRequest request;
    private HttpSession session;

    private ArrayList<Map<String, String>> userList = new ArrayList<>();
    private ArrayList<Map<String, String>> statesList = new ArrayList<>();

    public UserListBuilder() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    public void buildUserList(ResultSet resultSet) throws SQLException {
        try {
            // loop through resultSet for each row and put into Map
            while (resultSet.next()) {
                String userId = Long.toString(resultSet.getLong("user_id"));
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String facilityId = Long.toString(resultSet.getLong("facility_id"));
                String name = resultSet.getString("name") == null ? "" : resultSet.getString("name");
                String userGroup = resultSet.getString("user_group");
                String stateIds = resultSet.getString("state_ids");
                String viewIdentifier = Integer.toString(resultSet.getInt("view_identifier"));
                String timeLogin = resultSet.getString("time_login");
                String fullName = resultSet.getString("fullname");

                Map<String, String> map = new HashMap<>();
                map.put("userId", userId);
                map.put("username", username);
                map.put("password", password);
                map.put("facilityId", facilityId);
                map.put("name", name);
                map.put("userGroup", userGroup);
                map.put("stateIds", stateIds);
                map.put("viewIdentifier", viewIdentifier);
                map.put("timeLogin", timeLogin);
                map.put("fullName", fullName);
                userList.add(map);
            }
            session.setAttribute("userList", userList);
            userList = null;
        } catch (SQLException sqlException) {
            throw sqlException;
        }
    }

    public ArrayList<Map<String, String>> retrieveUserList() {
        // retrieve the user record store in session attribute
        if (session.getAttribute("userList") != null) {
            userList = (ArrayList) session.getAttribute("userList");
        }
        return userList;
    }

    public void buildStatesList(ResultSet resultSet) throws SQLException {
        try {
            // loop through resultSet for each row and put into Map
            while (resultSet.next()) {
                String stateId = Long.toString(resultSet.getLong("state_id"));
                String name = resultSet.getString("name") == null ? "" : resultSet.getString("name");

                Map<String, String> map = new HashMap<>();
                map.put("stateId", stateId);
                map.put("name", name);
                statesList.add(map);
            }
            session.setAttribute("statesList", statesList);
            resultSet = null;
            statesList = null;
        } catch (SQLException sqlException) {
            resultSet = null;
            throw sqlException;
        }
    }

    public ArrayList<Map<String, String>> retrieveStatesList() {
        // retrieve the user record store in session attribute
        if (session.getAttribute("statesList") != null) {
            statesList = (ArrayList) session.getAttribute("statesList");
        }
        return statesList;
    }

}
