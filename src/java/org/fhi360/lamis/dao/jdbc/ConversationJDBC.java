/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.dao.jdbc;

/**
 *
 * @author Alozie
 */
import java.sql.Timestamp;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class ConversationJDBC {

    public ConversationJDBC() {
    }
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    public static String findRecentMessage(String phone) {
        return "SELECT * FROM conversation WHERE phone = '" + phone
                + "' AND time_stamp = SELECT MAX(time_stamp) FROM conversation"
                + " WHERE originator_id = 0  AND phone = '" + phone + "'";
    }

    public static String findMessageByTime(String phone, Timestamp time) {
        return "SELECT participant.*, conversation.* FROM participant JOIN conversation "
                + "ON participant.phone = conversation.phone WHERE conversation.originator_id = 0 "
                + " AND conversation.phone = '" + phone + "' AND conversation.time_stamp = '"
                + time + "'";
    }

}
