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
import org.springframework.transaction.support.TransactionTemplate;

public class SpecimenJDBC {

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    public SpecimenJDBC() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();

    }

    public static String findSpecimenByNumber(String labno) {
        long facilityId = (Long) ServletActionContext.getRequest()
                .getSession().getAttribute("facilityId");
        return "SELECT specimen.specimen_id, specimen.specimen_type, specimen.labno, "
                + "specimen.barcode, specimen.facility_id, specimen.state_id, "
                + "specimen.lga_id, specimen.treatment_unit_id, specimen.date_received, "
                + "specimen.date_collected, specimen.date_assay, specimen.date_reported, "
                + "specimen.date_dispatched, specimen.quality_cntrl, "
                + " specimen.hospital_num, specimen.result, specimen.reason_no_test, "
                + "specimen.surname, specimen.other_names, specimen.gender, "
                + "specimen.date_birth, specimen.age, specimen.age_unit, specimen.address,"
                + " specimen.phone, specimen.time_stamp, facility.name FROM specimen "
                + " JOIN facility ON specimen.treatment_unit_id = facility.facility_id "
                + "WHERE specimen.facility_id = " + facilityId + " AND (specimen.labno "
                + "LIKE '" + labno + "%'" + " OR specimen.barcode LIKE '" + labno + "%')";
    }

    public long getRegimenId(long facilityId, String labNo, String dateReceived) {

        query = "SELECT specimen_id FROM specimen WHERE facility_id = '" + facilityId + "' AND labno = " + labNo + " AND date_received = '" + dateReceived;
        Long id = 0L;
        try {
            id = jdbcTemplate.queryForObject(query, Long.class);
        } catch (Exception e) {
        }
        return id;
    }
}
