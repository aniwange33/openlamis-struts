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
public class ChildFollowUpJDBC {

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    public ChildFollowUpJDBC() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    public Long getChildFollowUpId(long facilityId, String referenceNum, String dateVisit) {
        query = "SELECT childfollowup_id FROM childfollowup WHERE facility_id = " + facilityId + " AND reference_num = " + referenceNum + " AND date_visit = '" + dateVisit + "'";
        Long id = jdbcTemplate.queryForObject(query, Long.class);
		return id != null ? id: 0L;
    }
}
