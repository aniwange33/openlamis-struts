/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.sms;

import org.apache.commons.lang3.StringUtils;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author user1
 */
public class ParticipantRegistrationHandler {

    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    public ParticipantRegistrationHandler() {
    }

    public void savePhone(String phone) {
        executeUpdate("DELETE FROM sequencer WHERE phone = '" + phone + "'");
        executeUpdate("INSERT INTO sequencer (phone, active) VALUES('" + phone + "', 1)");
    }

    public void saveAge(String age) {
        executeUpdate("UPDATE sequencer SET age = " + Integer.parseInt(age));
    }

    public void saveGender(String gender) {
        executeUpdate("UPDATE sequencer SET gender = '" + gender + "'");
    }

    public void saveLocation(String location) {
        executeUpdate("UPDATE sequencer SET location = '" + location + "'");
    }

    public String getNextStepMsg(String phone) {
        final String message[] = {""};
        String query = "SELECT * FROM sequencer WHERE phone = '" + phone + "'";
        jdbcTemplate.query(query, resultSet -> {
            int age = resultSet.getInt("age");
            String gender = (resultSet.getString("gender") == null) ? "" : resultSet.getString("gender");
            String location = (resultSet.getString("location") == null) ? "" : resultSet.getString("location");
            if (age == 0) {
                message[0] = "What is your age?";
            } else {
                System.out.println("gender: " + gender);
                if (gender.trim().isEmpty()) {
                    message[0] = "What is your gender? M/F";
                } else {
                    System.out.println("location: " + location);
                    if (location.trim().isEmpty()) {
                        message[0] = "Where is your location?";
                    } else {
                        executeUpdate("INSERT INTO participant(phone, age, gender, location, active) SELECT phone, age, gender, location, active FROM sequencer WHERE phone = '" + phone + "'");
                        executeUpdate("DELETE FROM sequencer WHERE phone = '" + phone + "'");
                        message[0] = "Thanks for joining our forum";
                    }
                }
            }
        });
        if (StringUtils.isBlank(message[0])) {
            message[0] = "Send JOIN to commence registration";
        }
        return message[0];
    }

    private void executeUpdate(String query) {
        transactionTemplate.execute(ts -> {
            jdbcTemplate.execute(query);
            return null;
        });
    }
}
