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

import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author user10
 */
public class ChildJDBC {

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    public ChildJDBC() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();

    }

    public long getChildId(long facilityId, String referenceNum) {

        query = "SELECT patient_id FROM child WHERE facility_id = " + facilityId + " AND reference_num = '" + referenceNum + "'";
        Long id = jdbcTemplate.queryForObject(query, Long.class);
        return id != null ? id : 0L;

    }

    public long getChildId(long facilityId, long patientId, String referenceNum) {

        query = "SELECT child_id FROM child WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND reference_num = '" + referenceNum + "'";
        Long id = 0L;
        try {
            id = jdbcTemplate.queryForObject(query, Long.class);
        } catch (Exception e) {
        }
        return id;
    }
}
