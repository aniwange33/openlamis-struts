/**
 *
 * @author AALOZIE
 */
package org.fhi360.lamis.dao.jdbc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.builder.ChroniccareListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class ChroniccareJDBC {

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    public ChroniccareJDBC() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    public void findChroniccare() {
        try {

            query = "SELECT * FROM chroniccare WHERE facility_id = ? AND patient_id = ? AND date_visit = ?";

            jdbcTemplate.query(query, (resultSet) -> {
                new ChroniccareListBuilder().buildChroniccareList(resultSet);
                return null; //To change body of generated lambdas, choose Tools | Templates.
            }, (Long) session.getAttribute("facilityId"), Long.parseLong(request.getParameter("patientId")), DateUtil.parseStringToSqlDate(request.getParameter("dateVisit"), "MM/dd/yyyy"));

        } catch (Exception exception) {

        }
    }

    public long getChroniccareId(long facilityId, long patientId, String dateVisit) {

        query = "SELECT chroniccare_id FROM chroniccare WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_visit = '" + dateVisit + "'";
        Long id = 0L;
        try {
            id = jdbcTemplate.queryForObject(query, Long.class);
        } catch (Exception e) {
        }
        return id;
    }

    public String getLastClinicVisit() {
        return String.format("SELECT * FROM chroniccare WHERE facility_id = %s"
                + " AND patient_id = %s ORDER BY date_visit DESC LIMIT 1",
                (Long) session.getAttribute("facilityId"),
                Long.parseLong(request.getParameter("patientId")));
    }
}
