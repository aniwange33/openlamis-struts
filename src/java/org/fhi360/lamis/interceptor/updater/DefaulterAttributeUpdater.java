/**
 *
 * @author user1
 */
package org.fhi360.lamis.interceptor.updater;

import java.util.Date;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class DefaulterAttributeUpdater {

    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    static String query;
    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    static String currentStatus;

    public static void nullifyTrackingOutcome(long facilityId, long patientId, String dateVisit) {
        try {
            query = "SELECT current_status, date_started, date_tracked FROM "
                    + "patient WHERE facility_id = ? AND patient_id = ? AND "
                    + "date_tracked <= ?";
            jdbcTemplate.query(query, (resultSet) -> {
                if (resultSet.next()) {
                    currentStatus = resultSet.getString("current_status");
                    if ((currentStatus.trim().equalsIgnoreCase("Lost to Follow Up") || 
                            currentStatus.trim().equalsIgnoreCase("Stopped Treatment") || 
                            currentStatus.trim().equalsIgnoreCase("ART Transfer Out")) && 
                            resultSet.getDate("date_started") != null) {
                        currentStatus = "ART Restart";
                        query = "UPDATE patient SET current_status = ?, "
                                + "date_current_status = ?, time_stamp = ?, "
                                + "date_tracked = null, outcome = '', agreed_date = "
                                + "null WHERE facility_id = ? AND patient_id = ?";

                        transactionTemplate.execute((ts) -> {
                            jdbcTemplate.update(query, currentStatus,
                                    (dateVisit.isEmpty() ? null : DateUtil.parseStringToSqlDate(dateVisit, "MM/dd/yyyy")),
                                    new Date(), facilityId, patientId);
                            return null; //To change body of generated lambdas, choose Tools | Templates.
                        });

                        //log ART Restart date in statushistory
                        new StatusHistoryUpdater().logStatusChange(patientId, currentStatus, dateVisit);
                    } else {
                        //Clear tracking outcome and resolve status manually by running DQA
                        query = "UPDATE patient SET time_stamp = ?, "
                                + "date_tracked = null, outcome = '',  "
                                + "agreed_date = null WHERE facility_id = ? AND patient_id = ?";
                         transactionTemplate.execute((ts) -> {
                            jdbcTemplate.update(query, new Date(), facilityId, patientId);
                            return null; //To change body of generated lambdas, choose Tools | Templates.
                        });

                    }
                }
                return null;
            }, facilityId, patientId, (dateVisit.isEmpty() ? null : 
                    DateUtil.parseStringToSqlDate(dateVisit, "MM/dd/yyyy")));

        } catch (Exception exception) {

        }
    }
}
