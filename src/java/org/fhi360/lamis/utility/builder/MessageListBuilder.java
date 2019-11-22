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
import org.fhi360.lamis.utility.DateUtil;

public class MessageListBuilder {

    private HttpServletRequest request;
    private HttpSession session;
    private ArrayList<Map<String, String>> messageList = new ArrayList<Map<String, String>>();

    public MessageListBuilder() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    public void buildList(ResultSet resultSet) throws SQLException {
        try {
            // loop through resultSet for each row and put into Map
            while (resultSet.next()) {
                String messageId = Long.toString(resultSet.getLong("message_id"));
                String messageType = Integer.toString(resultSet.getInt("message_type"));
                String msgType = resultSet.getInt("message_type") == 1 ? "Appointment message" : resultSet.getInt("message_type") == 2 ? "Daily messages" : "Specific messages";
                String daysToAppointment = resultSet.getObject("days_to_appointment") == null ? "" : resultSet.getInt("days_to_appointment") == 0 ? "" : Integer.toString(resultSet.getInt("days_to_appointment"));
                String dateToSend = resultSet.getDate("date_to_send") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_to_send"), "MM/dd/yyyy");
                String message1 = resultSet.getString("message1") == null ? "" : resultSet.getString("message1");
                String message2 = resultSet.getString("message2") == null ? "" : resultSet.getString("message2");
                String message3 = resultSet.getString("message3") == null ? "" : resultSet.getString("message3");
                String message4 = resultSet.getString("message4") == null ? "" : resultSet.getString("message4");

                Map<String, String> map = new HashMap<>();
                map.put("messageId", messageId);
                map.put("messageType", messageType);
                map.put("msgType", msgType);
                map.put("daysToAppointment", daysToAppointment);
                map.put("dateToSend", dateToSend);
                map.put("message1", message1);
                map.put("message2", message2);
                map.put("message3", message3);
                map.put("message4", message4);
                messageList.add(map);
            }
            session.setAttribute("messageList", messageList);
            messageList = null;
        } catch (SQLException sqlException) {
            throw sqlException;
        }
    }

    public ArrayList<Map<String, String>> retrieveList() {
        // retrieve the message record store in session attribute
        if (session.getAttribute("messageList") != null) {
            messageList = (ArrayList) session.getAttribute("messageList");
        }
        return messageList;
    }

}
