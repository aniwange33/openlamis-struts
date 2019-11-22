/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.dao.jdbc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.builder.DevolveListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author user1
 */
public class DevolveJDBC {

    private String query;
    private HttpServletRequest request;
    private HttpSession session;
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    public DevolveJDBC() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    public void devolved(long patientId, long communitypharmId) {
        try {
            query = "UPDATE patient SET communitypharm_id = " + communitypharmId + 
                    " WHERE patient_id = " + patientId;
            jdbcTemplate.execute(query);
        } catch (Exception exception) {

        }
    }

    public long getDevolvedId(long facilityId, long patientId, String dateDelivery) {
        Long[] id = {0L};
        try {
            query = "SELECT devolve_id FROM devolve WHERE facility_id = " + 
                    facilityId + " AND patient_id = " + patientId + " AND "
                    + "date_devolved = '" + dateDelivery + "'";
            jdbcTemplate.query(query, resultSet -> {
                id[0] = resultSet.getLong("devolve_id");
            });
        } catch (Exception exception) {
        }
        return id[0];
    }

    public void devolved(long patientId) {

        query = "SELECT * FROM devolve WHERE patient_id = " + patientId
                + " ORDER BY date_devolved DESC LIMIT 1";
        jdbcTemplate.query(query, rs -> {
            devolved(patientId, rs.getLong("communitypharm_id"));
        });
    }

    public void findDevolve(long patientId, String dateVisit) {

        try {
            System.out.println("DevolveJDBC..........finding DMOC");
            query = String.format("SELECT devolve.*, patient.patient_id, "
                    + "patient.hospital_num, patient.unique_id, patient.gender,"
                    + " patient.age, patient.surname, patient.other_names, patient.date_started "
                    + " FROM devolve JOIN  patient ON devolve.patient_id = "
                    + "patient.patient_id WHERE devolve.patient_id = %d AND "
                    + "devolve.date_devolved = %s  LIMIT 1",
                    patientId, DateUtil.parseStringToSqlDate(dateVisit, "MM/dd/yyyy"));
            jdbcTemplate.query(query, resultSet -> {
                new DevolveListBuilder().buildDevolveDetailsList(resultSet);
                return null;
            });
        } catch (Exception exception) {

        }
    }

    public void findPatientToDevolve(long patientId) {
        try {

            executeUpdate("DROP TABLE IF EXISTS commence");
            query = "CREATE TEMPORARY TABLE commence AS SELECT * FROM clinic "
                    + "WHERE patient_id = " + patientId + " AND commence = 1";
            executeUpdate(query);

            query = "SELECT patient.patient_id, patient.facility_id, patient.hospital_num, "
                    + "patient.unique_id, patient.surname, patient.other_names, "
                    + "patient.gender, patient.date_birth, patient.age_unit, patient.age, "
                    + "patient.address, patient.date_started, patient.current_status, "
                    + "patient.date_current_status, "
                    + " patient.last_viral_load, patient.date_last_viral_load, "
                    + "patient.last_cd4, patient.last_cd4p, patient.date_last_cd4, "
                    + "patient.last_clinic_stage, patient.date_last_clinic, "
                    + "patient.date_last_refill, patient.date_next_clinic, "
                    + "patient.date_next_refill, "
                    + " commence.date_visit, commence.clinic_stage AS clinic_stage_commence, "
                    + "commence.cd4 AS cd4_commence, commence.cd4p AS cd4p_commence, "
                    + "commence.regimentype AS regimentype_commence, commence.regimen AS regimen_commence "
                    + " FROM patient LEFT OUTER JOIN commence ON patient.patient_id = "
                    + "commence.patient_id WHERE patient.patient_id = " + patientId;
            jdbcTemplate.query(query, resultSet -> {
                new DevolveListBuilder().buildPatientList(resultSet);
                return null;
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void executeUpdate(String query) {
        try {
            transactionTemplate.execute(ts -> {
                jdbcTemplate.execute(query);
                return null;
            });
        } catch (Exception exception) {

        }
    }
}
