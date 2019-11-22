/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.dao.jdbc;

import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author user1
 */
public class LabtestJDBC {

    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    public static String getLabtest(Long id) {

        String query = "SELECT description FROM labtest WHERE labtest_id = ?";
        String description = "";
        try {
            description = jdbcTemplate.queryForObject(query, String.class);
        } catch (Exception e) {
        }
        return description;
    }

}
