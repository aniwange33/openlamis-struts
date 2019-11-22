/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.dao.jdbc;

/**
 *
 * @author Alozie
 */
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;

public class EidJDBC {

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    public EidJDBC() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    public static String findEidByLabno(String labno) {
        long facilityId = (Long) ServletActionContext.getRequest()
                .getSession().getAttribute("facilityId");
        return "SELECT * FROM eid WHERE facility_id = " + facilityId
                + " AND labno = '" + labno + "'";
    }

    public long getEidId(long facilityId, String labno) {

        query = "SELECT eid_id FROM eid WHERE facility_id = " + facilityId + " AND labno = " + labno;
        Long id = 0L;
        try {
            id = jdbcTemplate.queryForObject(query, Long.class);
        } catch (Exception e) {
        }
        return id;
    }
}
