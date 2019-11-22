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
public class MotherInformationJDBC {

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    public MotherInformationJDBC() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();

    }

    public String findMother(long motherId) {
        return String.format("SELECT * FROM motherinformation WHERE facility_id = %s"
                + " AND motherinformation_id = %s",
                (Long) session.getAttribute("facilityId"), motherId);
    }

}
