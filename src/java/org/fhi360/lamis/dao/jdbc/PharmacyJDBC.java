/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.dao.jdbc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.builder.PharmacyListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class PharmacyJDBC {

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    public PharmacyJDBC() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    // Retrieve a pharmacy record in database
    public void findPharmacy(long patientId, String dateVisit) {
        try {
            // fetch the required records from the database

            query = "SELECT pharmacy.pharmacy_id, pharmacy.patient_id, pharmacy.facility_id, pharmacy.date_visit, pharmacy.duration, pharmacy.dmoc_type, pharmacy.morning, pharmacy.afternoon, pharmacy.evening, pharmacy.morning+pharmacy.afternoon+pharmacy.evening AS quantity, pharmacy.next_appointment, pharmacy.prescrip_error, pharmacy.adherence, pharmacy.adr_screened, pharmacy.adr_ids, pharmacy.regimentype_id, pharmacy.regimendrug_id, regimendrug.regimen_id, regimendrug.drug_id, drug.name, drug.strength "
                    + " FROM pharmacy JOIN regimendrug ON pharmacy.regimendrug_id = regimendrug.regimendrug_id JOIN drug ON regimendrug.drug_id = drug.drug_id WHERE pharmacy.facility_id = ? AND pharmacy.patient_id = ? AND pharmacy.date_visit = ?";

            jdbcTemplate.query(query, (resultSet) -> {
                new PharmacyListBuilder().buildPharmacyList(resultSet);
                return null; //To change body of generated lambdas, choose Tools | Templates.
            }, (Long) session.getAttribute("facilityId"), patientId, DateUtil.parseStringToSqlDate(request.getParameter("dateVisit"), "MM/dd/yyyy"));

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public long getPharmacyManagerId(long facilityId, long patientId, String dateAssigned) {

        query = "SELECT pharmacy_id FROM pharmacy WHERE facility_id = '" + facilityId + "' AND patient_id = " + patientId + " AND date_assigned = '" + dateAssigned;
        Long id = 0L;
        try {
            id = jdbcTemplate.queryForObject(query, Long.class);
        } catch (Exception e) {
        }
        return id;
    }

    public String getLastRefillVisit(long patientId) {

        return "SELECT DISTINCT regimentype_id, regimen_id, date_visit, duration "
                + "FROM pharmacy WHERE patient_id = " + patientId
                + " AND regimentype_id IN (1, 2, 3, 4, 14) ORDER BY date_visit DESC LIMIT 1";
    }

    public String getLastRefillVisit(long patientId, String dateVisit) {

        query = "SELECT DISTINCT regimentype_id, regimen_id, date_visit, "
                + "duration FROM pharmacy WHERE patient_id = " + patientId
                + " AND date_visit <= '" + dateVisit + "' AND regimentype_id "
                + "IN (1, 2, 3, 4, 14) ORDER BY date_visit DESC LIMIT 1";
        jdbcTemplate.query(query, (resultSet) -> {

            return resultSet; //To change body of generated lambdas, choose Tools | Templates.
        });

        return null;
    }

}
