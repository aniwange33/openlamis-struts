/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.dao.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author user10
 */
public class BiometricJDBC {

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    public BiometricJDBC() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();

    }

    public String getBiomtricId(long facilityId, long patientId, String hospital_num) {

        query = "SELECT biometric_id FROM biometric WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND hospital_num = '" + hospital_num + "'";
        String id = jdbcTemplate.queryForObject(query, String.class);
        return id != null ? id : "";

    }

    public String getBiomtricId(String uuid) {
        query = "SELECT biometric_id FROM biometric WHERE  uuid = '" + uuid + "'";
        String id = jdbcTemplate.queryForObject(query, String.class);
        return id != null ? id : "";

    }

}
