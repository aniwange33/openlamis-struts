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
public class SmsOriginatorVerifier {
    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);


    public SmsOriginatorVerifier() {
    }

    public boolean isOriginatorRegistered(String phone) {
        String phonePorted = phone;
        if (phone.length() > 12) {
            phonePorted = phone.substring(2);
        }
        String query = "SELECT DISTINCT participant_id FROM participant WHERE phone = '" + phone + "' OR phone = '" + phonePorted + "'";
        Long id = jdbcTemplate.queryForObject(query, Long.class);
        return id != null;
    }

    public boolean isFacilityRegistered(String text) {

        return true;
    }
}
