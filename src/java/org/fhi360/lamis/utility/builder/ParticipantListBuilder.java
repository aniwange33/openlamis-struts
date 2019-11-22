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
import org.fhi360.lamis.dao.jdbc.ConversationJDBC;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.springframework.jdbc.core.JdbcTemplate;

public class ParticipantListBuilder {

    private HttpServletRequest request;
    private HttpSession session;
    private ArrayList<Map<String, String>> participantList = new ArrayList<>();
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    public ParticipantListBuilder() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    public void buildList(ResultSet resultSet) throws SQLException {
        try {
            // loop through resultSet for each row and put into Map
            while (resultSet.next()) {
                String query = ConversationJDBC.findMessageByTime(resultSet.getString("phone"), resultSet.getTimestamp("time_stamp"));
                jdbcTemplate.query(query, resultSet1->{
                    String participantId = Long.toString(resultSet1.getLong("participant_id"));
                    String phone = resultSet1.getObject("phone") == null ? "" : resultSet1.getString("phone");
                    String age = resultSet1.getObject("age") == null ? "" : resultSet1.getInt("age") == 0 ? "" : Integer.toString(resultSet1.getInt("age"));
                    String gender = resultSet1.getObject("gender") == null ? "" : resultSet1.getString("gender");
                    String location = resultSet1.getObject("location") == null ? "" : resultSet1.getString("location");
                    String message = resultSet1.getObject("message") == null ? "" : resultSet1.getString("message");
                    String dateMessage = resultSet1.getObject("date_message") == null ? "" : DateUtil.parseDateToString(resultSet1.getDate("date_message"), "MM/dd/yyyy");
                    String unread = Integer.toString(resultSet1.getInt("unread"));

                    message = "#" + participantId + "  " + message;
                    Map<String, String> map = new HashMap<>();
                    map.put("participantId", participantId);
                    map.put("phone", phone);
                    map.put("age", age);
                    map.put("gender", gender);
                    map.put("location", location);
                    map.put("message", message);
                    map.put("dateMessage", dateMessage);
                    map.put("unread", unread);
                    participantList.add(map);
                });
            }
            session.setAttribute("participantList", participantList);
            participantList = null;
        } catch (SQLException sqlException) {
            throw sqlException;
        }
    }

    public ArrayList<Map<String, String>> retrieveList() {
        // retrieve the participant record store in session attribute
        if (session.getAttribute("participantList") != null) {
            participantList = (ArrayList) session.getAttribute("participantList");
        }
        return participantList;
    }

}
