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

import org.fhi360.lamis.utility.builder.AssessmentListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author user10
 */
public class AssessmentJDBC {

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    public AssessmentJDBC() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    public void findAssessment(long assessmentId) {
        try {
            // fetch the required records from the database

            query = "SELECT * FROM assessment WHERE facility_id = ? AND assessmentId = ?";

            jdbcTemplate.query(query, (resultSet) -> {
                new AssessmentListBuilder().buildAssessmentList(resultSet);
                return null; //To change body of generated lambdas, choose Tools | Templates.
            }, (Long) session.getAttribute("facilityId"), assessmentId);

        } catch (Exception exception) {

        }
    }

    public long getHtsAssessment(String clientCode, Long facilityId) {
        query = "SELECT assessment_id FROM assessment WHERE client_code = '" + clientCode + "' AND facility_id = " + facilityId;
         Long id = 0L;
        try {
            id = jdbcTemplate.queryForObject(query, Long.class);
        } catch (Exception e) {
        }
        return id;
    }

}
