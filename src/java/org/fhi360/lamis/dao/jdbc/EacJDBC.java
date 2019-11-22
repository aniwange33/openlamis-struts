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

import org.fhi360.lamis.utility.builder.EacListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author user10
 */
public class EacJDBC {

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    public EacJDBC() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    public void findEac(long patientId, String dateEac) {
        try {
            // fetch the required records from the database

            query = "SELECT * FROM eac WHERE facility_id = ? AND patient_id = ? AND date_eac1 = ?";

            jdbcTemplate.query(query, (resultSet) -> {
                new EacListBuilder().buildEacList(resultSet);
                return null; //To change body of generated lambdas, choose Tools | Templates.
            }, (Long) session.getAttribute("facilityId"), patientId, DateUtil.parseStringToSqlDate(dateEac, "MM/dd/yyyy"));

        } catch (Exception exception) {

        }
    }

    public long getEacId(long facilityId, long patientId, String dateEac1) {

        query = "SELECT eac_id FROM devolve WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_eac1 = '" + dateEac1 + "'";
        Long id = jdbcTemplate.queryForObject(query, Long.class);
        return id != null ? id : 0L;
    }

    public String getLastEacVisit(long patientId) {
        return String.format("SELECT * FROM eac WHERE facility_id = %s AND "
                + "patient_id = %s ORDER BY date_eac1 DESC LIMIT 1",
                (Long) session.getAttribute("facilityId"), patientId);
    }

}
