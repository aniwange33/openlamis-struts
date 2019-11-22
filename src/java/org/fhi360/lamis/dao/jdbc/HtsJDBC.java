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

import org.fhi360.lamis.utility.builder.HtsListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author user10
 */
public class HtsJDBC {

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    public HtsJDBC() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    public void findHts(long htsId) {
        try {
            // fetch the required records from the database

            query = "SELECT * FROM hts WHERE facility_id = ? AND hts_id = ?";

            jdbcTemplate.query(query, (resultSet) -> {
                new HtsListBuilder().buildHtsList(resultSet);
                return null; //To change body of generated lambdas, choose Tools | Templates.
            }, (Long) session.getAttribute("facilityId"), htsId);
        } catch (Exception exception) {

        }
    }

    public Long getHtsId(String clientCode, Long facilityId) {
        query = "SELECT hts_id FROM hts WHERE client_code = '" + clientCode + "' AND facility_id = " + facilityId;
       Long id = 0L;
        try {
            id = jdbcTemplate.queryForObject(query, Long.class);
        } catch (Exception e) {
        }
        return id;
    }

}
