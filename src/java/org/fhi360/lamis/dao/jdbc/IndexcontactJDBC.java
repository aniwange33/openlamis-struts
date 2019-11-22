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

import org.fhi360.lamis.utility.builder.IndexcontactListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author user10
 */
public class IndexcontactJDBC {

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    public IndexcontactJDBC() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    public void findIndexcontact(long indexcontactId) {
        try {
            System.out.println("FACILIYID " + session.getAttribute("facilityId"));
            // fetch the required records from the database

            query = "SELECT * FROM indexcontact WHERE facility_id = ? AND indexcontact_id = ? ";

            jdbcTemplate.query(query, (resultSet) -> {
                new IndexcontactListBuilder().buildIndexcontactList(resultSet);
                return null; //To change body of generated lambdas, choose Tools | Templates.
            }, (Long) session.getAttribute("facilityId"), indexcontactId);

        } catch (Exception exception) {

        }
    }

    public long getIndexcontactId(String indexContactCode, long facilityId) {

        query = "SELECT indexcontact_id FROM indexcontact WHERE index_contact_code = '" + indexContactCode + "' AND facility_id = " + facilityId;
        Long id = 0L;
        try {
            id = jdbcTemplate.queryForObject(query, Long.class);
        } catch (Exception e) {
        }
        return id;
    }

}
