/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.dao.jdbc;

import org.fhi360.lamis.service.beans.ContextProvider;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author aalozie
 */
public class RegimenJDBC {

    private static final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    public static String getRegimentype(Long id) {
        String query = "SELECT description FROM regimentype WHERE regimentype_id = " + id;
        return jdbcTemplate.queryForObject(query, String.class);
    }

    public static long getRegimentypeId(String regimentype) {
        String query = "SELECT regimentype_id FROM regimentype WHERE description = '" + regimentype + "'";
        return jdbcTemplate.queryForObject(query, Long.class);
    }

    public static String getRegimen(Long id) {
        String query = "SELECT description FROM regimen WHERE regimen_id = " + id;
        return jdbcTemplate.queryForObject(query, String.class);
    }

    public static long getRegimenId(String regimen) {
        String query = "SELECT regimen_id FROM regimen WHERE description = '" + regimen + "'";
        return jdbcTemplate.queryForObject(query, Long.class);
    }
}
