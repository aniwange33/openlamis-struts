/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.dao.jdbc;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.builder.LaboratoryListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class LaboratoryJDBC {

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    public LaboratoryJDBC() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    // Retrieve a laboratory record in database
    public void findLaboratory(long patientId, String dateReported) {
        try {
            // fetch the required records from the database

            query = "SELECT laboratory.laboratory_id, laboratory.patient_id, laboratory.facility_id, laboratory.date_collected, laboratory.date_reported, laboratory.labno, laboratory.resultab, laboratory.resultpc, laboratory.comment, laboratory.labtest_id, labtest.description, labtest.measureab, labtest.measurepc "
                    + " FROM laboratory JOIN labtest ON laboratory.labtest_id = labtest.labtest_id WHERE laboratory.facility_id = ? AND laboratory.patient_id = ? AND laboratory.date_reported = ?";

            jdbcTemplate.query(query, (resultSet) -> {
                new LaboratoryListBuilder().buildLaboratoryList(resultSet);
                return null;
            }, (Long) session.getAttribute("facilityId"), patientId, DateUtil.parseStringToSqlDate(dateReported, "MM/dd/yyyy"));

        } catch (Exception exception) {

        }
    }

    public String getLastCd4(long patientId) {
        try {
            // fetch the required records from the database

            query = String.format("SELECT * FROM laboratory WHERE facility_id = %s "
                    + "AND patient_id = %s AND labtest_id = 1 ORDER BY date_reported "
                    + "DESC LIMIT 1",(Long) session.getAttribute("facilityId"), patientId);

            jdbcTemplate.query(query, (resultSet) -> {
                new LaboratoryListBuilder().buildLaboratoryList(resultSet);
                return null;
            }, (Long) session.getAttribute("facilityId"), patientId);

        } catch (Exception exception) {

        }
        return query;
    }

    public String getHighestCd4Absolute(long patientId) {
        try {
            // fetch the required records from the database

            //query = "SELECT resultab, date_reported FROM laboratory WHERE facility_id = ? AND patient_id = ? AND resultab = (SELECT MAX(CONVERT(resultab, INT)) FROM laboratory WHERE facility_id = ? AND patient_id = ? AND labtest_id = 1)"; 
            query = String.format("SELECT resultab, date_reported FROM laboratory "
                    + "WHERE facility_id = %s AND patient_id = %s AND resultab = "
                    + "(SELECT MAX(resultab REGEXP('(^[0-9]+$)')) FROM laboratory "
                    + "WHERE facility_id = %s AND patient_id = %s AND labtest_id = 1)",
                    (Long) session.getAttribute("facilityId"), patientId, (Long) 
                    session.getAttribute("facilityId"), patientId);

            jdbcTemplate.query(query, (resultSet) -> {
                new LaboratoryListBuilder().buildLaboratoryList(resultSet);
                return null;
            });

        } catch (Exception exception) {

        }
        return query;
    }

    public String getHighestCd4AbsoluteAfterArt(long patientId, Date dateStarted) {
        try {
            // fetch the required records from the database

            //query = "SELECT resultab, date_reported FROM laboratory WHERE facility_id = ? AND patient_id = ? AND date_reported > '" + dateStarted + "' AND resultab = (SELECT MAX(CONVERT(resultab, INT)) FROM laboratory WHERE facility_id = ? AND patient_id = ? AND labtest_id = 1)"; 
            query = String.format("SELECT resultab, date_reported FROM laboratory WHERE"
                    + " facility_id = %s AND patient_id = %s AND date_reported > '"
                    + dateStarted + "' AND resultab = (SELECT MAX(resultab REGEXP('(^[0-9]+$)')) "
                    + "FROM laboratory WHERE facility_id = %s AND patient_id = %s "
                    + "AND labtest_id = 1)",
                    (Long) session.getAttribute("facilityId"), patientId, (Long) session.getAttribute("facilityId"), patientId);

            jdbcTemplate.query(query, (resultSet) -> {
                new LaboratoryListBuilder().buildLaboratoryList(resultSet);
                return null;
            });

        } catch (Exception exception) {
            System.out.println("Error....");

        }
        return query;
    }

    public String getHighestCd4Percentage(long patientId) {
        try {
            // fetch the required records from the database

            //query = "SELECT resultpc, date_reported FROM laboratory WHERE facility_id = ? AND patient_id = ? AND resultpc = (SELECT MAX(CONVERT(resultpc, INT)) FROM laboratory WHERE facility_id = ? AND patient_id = ? AND labtest_id = 1)"; 
            query = String.format("SELECT resultpc, date_reported FROM laboratory WHERE "
                    + "facility_id = %s AND patient_id = %s AND resultpc = "
                    + "(SELECT MAX(resultpc REGEXP('(^[0-9]+$)')) FROM laboratory "
                    + "WHERE facility_id = %s AND patient_id = %s AND labtest_id = 1)",
                    (Long) session.getAttribute("facilityId"), patientId, (Long) session.getAttribute("facilityId"), patientId);

            jdbcTemplate.query(query, (resultSet) -> {
                new LaboratoryListBuilder().buildLaboratoryList(resultSet);
                return null;
            });

        } catch (Exception exception) {

        }
        return query;
    }

    public String getHighestCd4PercentageAfterArt(long patientId, Date dateStarted) {
        try {
            // fetch the required records from the database

            //query = "SELECT resultpc, date_reported FROM laboratory WHERE facility_id = ? AND patient_id = ? AND date_reported > '" + dateStarted + "' AND resultpc = (SELECT MAX(CONVERT(resultpc, INT)) FROM laboratory WHERE facility_id = ? AND patient_id = ? AND labtest_id = 1)"; 
            query = String.format("SELECT resultpc, date_reported FROM laboratory "
                    + "WHERE facility_id = %s AND patient_id = %s AND date_reported > '"
                    + dateStarted + "' AND resultpc = (SELECT MAX(resultpc "
                    + "REGEXP('(^[0-9]+$)')) FROM laboratory WHERE facility_id = %s "
                    + "AND patient_id = %s AND labtest_id = 1)",
                    (Long) session.getAttribute("facilityId"), patientId, (Long) session.getAttribute("facilityId"), patientId);

            jdbcTemplate.query(query, (resultSet) -> {
                new LaboratoryListBuilder().buildLaboratoryList(resultSet);
                return null;
            });

        } catch (Exception exception) {

        }
        return query;
    }

    public String getLastViralLoad(long patientId) {
        return String.format("SELECT * FROM laboratory WHERE facility_id = %s "
                + "AND patient_id = %s AND labtest_id = 16 ORDER BY date_"
                + "reported DESC LIMIT 1",
                (Long) session.getAttribute("facilityId"), patientId);
    }

    public String getLastViralLoad(long patientId, String dateReported) {
        return "SELECT * FROM laboratory WHERE patient_id = " + patientId
                + " AND date_reported <= '" + dateReported + "' AND labtest_id = 16 "
                + "ORDER BY date_reported DESC LIMIT 1";
    }

    public long getLabouratoryId(long patientId, Long facilityId, String dateReported) {

        query = "SELECT labtest_id FROM hts WHERE facility_id = '" + facilityId + "' AND patient_id = " + facilityId + " AND date_reported = " + dateReported;
        Long id = 0L;
        try {
            id = jdbcTemplate.queryForObject(query, Long.class);
        } catch (Exception e) {
        }
        return id;
    }

}


//query = "SELECT resultab, resultpc, date_reported FROM laboratory WHERE facility_id = ? AND patient_id = ? AND date_reported = (SELECT MAX(date_reported) FROM laboratory WHERE facility_id = ? AND patient_id = ? AND labtest_id = (SELECT DISTINCT labtest_id FROM labtest WHERE description = 'HIV Viral Load'))"; 
