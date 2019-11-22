/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.dao.jdbc;

import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author user10
 */
public class PatientJDBC {

    private String query;
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    public long getPatientId(long facilityId, String hospitalNum) {

        query = "SELECT patient_id FROM patient WHERE facility_id = " + facilityId + " AND hospital_num = '" + hospitalNum + "'";
        Long id = 0L;
        try {
            id = jdbcTemplate.queryForObject(query, Long.class);
        } catch (Exception e) {
        }
        return id;
    }

    public String getHospitalNum(long patientId) {

        String query = "SELECT hospital_num FROM patient WHERE patient_id = " + patientId;
        String hospitalNum = jdbcTemplate.queryForObject(query, String.class);
        return hospitalNum != null ? hospitalNum : null;
    }

    public boolean dueViralLoad(long patientId) {
        //Check if client is due for Viral Load test
        boolean due = false;
        try {

            query = "SELECT patient_id FROM patient WHERE patient_id = " + patientId
                    + " AND viral_load_due_date <= CURDATE()";
            try {
                Long patientIds = jdbcTemplate.queryForObject(query, Long.class);
                if (patientIds != null) {
                    due = true;
                }
            } catch (Exception e) {
            }
        } catch (Exception exception) {

        }

        return due;
    }

    public String getCurrentStatus(long patientId) {
        return "SELECT DISTINCT current_status, date_current_status "
                + "FROM statushistory WHERE patient_id = " + patientId
                + " ORDER BY date_current_status DESC LIMIT 1";
    }

    public String getCurrentStatus(long patientId, String dateCurrentStatus) {

        return "SELECT DISTINCT current_status, date_current_status FROM "
                + "statushistory WHERE patient_id = " + patientId + " AND "
                + "date_current_status <= '" + dateCurrentStatus + "' ORDER BY "
                + "date_current_status DESC LIMIT 1";
    }

}
