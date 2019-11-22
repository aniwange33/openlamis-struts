/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.dao.jdbc;

import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author user10
 */
public class DrugtherapyJDBC {

    private String query;
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    public long getDrugtherapyId(long facilityId, long patientId, String dateVisit) {

        query = "SELECT drugtherapy_id FROM Drugtherapy WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_visit = '" + dateVisit + "'";
        Long id = 0L;
        try {
            id = jdbcTemplate.queryForObject(query, Long.class);
        } catch (Exception e) {
        }
        return id;
    }
}
