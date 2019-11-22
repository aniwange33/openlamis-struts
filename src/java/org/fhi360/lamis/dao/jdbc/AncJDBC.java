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
import org.fhi360.lamis.utility.builder.AncListBuilder;
import org.fhi360.lamis.utility.builder.ChildfollowupListBuilder;
import org.fhi360.lamis.utility.builder.DeliveryListBuilder;
import org.fhi360.lamis.utility.builder.MaternalfollowupListBuilder;
import org.fhi360.lamis.utility.builder.PartnerInformationListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;

public class AncJDBC {

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    public AncJDBC() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    public void findAnc(long patientId, String dateVisit) {
        try {
            query = String.format("SELECT * FROM anc WHERE facility_id = %d AND patient_id = %d AND date_visit = '%s'",
                    (Long) session.getAttribute("facilityId"), patientId, DateUtil.parseStringToSqlDate(dateVisit, "MM/dd/yyyy"));
            jdbcTemplate.query(query, resultSet -> {
                new AncListBuilder().buildAncList(resultSet);
                return null;
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public long findAnc(long facilityId, long patientId, String dateVisit) {
        long[] ancId = {0};
        try {
            query = "SELECT anc_id FROM anc WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_visit = '" + dateVisit + "'";
            jdbcTemplate.query(query, resultSet -> {
                if (resultSet.next()) {
                    ancId[0] = resultSet.getLong("anc_id");
                }
                return null;
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return ancId[0];
    }

    public void findDelivery(long patientId, String dateDelivery) {
        try {
            query = String.format("SELECT * FROM delivery WHERE facility_id = %d AND patient_id = %d AND date_delivery = '%s'",
                    (Long) session.getAttribute("facilityId"), patientId, DateUtil.parseStringToSqlDate(dateDelivery, "MM/dd/yyyy"));
            jdbcTemplate.query(query, resultSet -> {
                new DeliveryListBuilder().buildDeliveryList(resultSet);
                return null;
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void findMaternalfollowup(long patientId, String dateVisit) {
        try {
            query = String.format("SELECT * FROM maternalfollowup WHERE facility_id = %d AND patient_id = %d AND date_visit = '%s'",
                    (Long) session.getAttribute("facilityId"), patientId, DateUtil.parseStringToSqlDate(dateVisit, "MM/dd/yyyy"));

            jdbcTemplate.query(query, resultSet -> {
                new MaternalfollowupListBuilder().buildMaternalfollowupList(resultSet);
                return null;
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void findChildfollowup(long childId, String dateVisit) {
        try {
            query = String.format("SELECT * FROM childfollowup WHERE facility_id = %d AND child_id = %d AND date_visit = '%s'",
                    (Long) session.getAttribute("facilityId"), childId, DateUtil.parseStringToSqlDate(dateVisit, "MM/dd/yyyy"));
            jdbcTemplate.query(query, resultSet -> {
                new ChildfollowupListBuilder().buildChildfollowupList(resultSet);
                return null;
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void findPartnerInformation(long patientId) {
        try {
            query = String.format("SELECT * FROM partnerinformation WHERE facility_id = %d AND patient_id = %d AND date_visit = (SELECT MAX(date_visit) FROM partnerinformation WHERE facility_id = %d AND patient_id = %d)",
                    (Long) session.getAttribute("facilityId"), patientId, (Long) session.getAttribute("facilityId"), patientId);

            jdbcTemplate.query(query, resultSet -> {
                new PartnerInformationListBuilder().buildPartnerinformationList(resultSet);
                return null;
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public int getChildCount(long patientId) {
        int[] count = {0};
        try {
            query = String.format("SELECT COUNT(*) AS count FROM child WHERE facility_id = %d AND patient_id = %d",
                    (Long) session.getAttribute("facilityId"), patientId);
            jdbcTemplate.query(query, resultSet -> {
                if (resultSet.next()) {
                    count[0] = resultSet.getInt("count");
                }
                return null;
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return count[0];
    }
}
