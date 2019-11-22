/**
 *
 * @author AALOZIE
 */
package org.fhi360.lamis.dao.jdbc;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.builder.ClinicListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class ClinicJDBC {

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    public ClinicJDBC() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    public long getClinicId(long patientId, Date dateVisit) {
        String date = DateUtil.parseDateToString(dateVisit, "yyyy-MM-dd");

        query = "SELECT clinic_id FROM clinic WHERE patient_id = " + patientId + " AND date_visit = '" + date + "'";
        Long id = 0L;
        try {
            id = jdbcTemplate.queryForObject(query, Long.class);
        } catch (Exception e) {
        }
        return id;
    }

    public void findCommence(long patientId) {
        try {
            // fetch the required records from the database

            query = "SELECT * FROM clinic WHERE facility_id = ? AND patient_id = ? AND commence = 1";

            jdbcTemplate.query(query, (resultSet) -> {
                new ClinicListBuilder().buildClinicList(resultSet);
                return null;
            }, (Long) session.getAttribute("facilityId"), patientId);

        } catch (Exception exception) {

        }
    }

    public void findClinic(long patientId, String dateVisit) {
        try {
            // fetch the required records from the database

            query = "SELECT * FROM clinic WHERE facility_id = ? AND patient_id = ? AND date_visit = ?";

            jdbcTemplate.query(query, (resultSet) -> {
                new ClinicListBuilder().buildClinicList(resultSet);
                return null;
            }, (Long) session.getAttribute("facilityId"), patientId, DateUtil.parseStringToSqlDate(dateVisit, "MM/dd/yyyy"));

        } catch (Exception exception) {

        }
    }

    public String getLastClinicVisit(long patientId) {

        return String.format("SELECT * FROM clinic WHERE facility_id = %s"
                + " AND patient_id = %s ORDER BY date_visit DESC LIMIT 1",
                session.getAttribute("facilityId"), patientId);
    }

    public String getArtCommencement(long patientId) {
        return String.format("SELECT * FROM clinic WHERE facility_id = %s AND "
                + "patient_id = %s AND commence = 1",
                session.getAttribute("facilityId"), patientId);
    }
}
