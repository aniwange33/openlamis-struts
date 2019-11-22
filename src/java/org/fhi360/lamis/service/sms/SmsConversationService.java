/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.sms;

import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author user1
 */
public class SmsConversationService {

    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    public SmsConversationService() {
    }

    public void saveSms(Sms sms) {
        String message = sms.getText();
        String phone = sms.getFrom();
        long originatorId = sms.getOriginatorId();
        executeUpdate("INSERT INTO conversation (phone, message, date_message, originator_id, time_stamp, unread) VALUES('" + phone + "', '" + message + "', CURDATE()," + originatorId + ", NOW(), 1)");
    }

    public void sendSms(Sms sms) {
        String message = sms.getText();
        String phone = sms.getFrom();
        long originatorId = sms.getOriginatorId();
        new SmsMessageSender().send(message, phone);
        executeUpdate("INSERT INTO conversation (phone, message, date_message, originator_id, time_stamp, unread) VALUES('" + phone + "', '" + message + "', CURDATE()," + originatorId + ", NOW(), 0)");
    }

    public void unreadFlagUpdate(String phone) {
        executeUpdate("UPDATE conversation SET unread = 0 WHERE phone = '" + phone + "'");
    }

    private void executeUpdate(String query) {
        transactionTemplate.execute(ts -> {
            jdbcTemplate.execute(query);
            return null;
        });
    }
}
