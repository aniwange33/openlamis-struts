/**
 *
 * @author user1
 */
package org.fhi360.lamis.interceptor.updater;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;

import org.fhi360.lamis.dao.jdbc.RegimenJDBC;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.builder.PharmacyListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class PatientRefillAttributeUpdater {

    private String query;
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    private HttpServletRequest request;

    //This method is called each time a refill record is added or modfied to update the patient refill attribute
    //This method function can be implemented with trigger in the database
    public void lastRefillDate(boolean delete) {
        request = ServletActionContext.getRequest();
        long patientId = Long.parseLong(request.getParameter("patientId"));
        long facilityId = (Long) request.getSession().getAttribute("facilityId");

        // check if ARV was dispensed -> ids 1,2,3,4,14
        List<Map<String, String>> dispenserList = new ArrayList<>();
        dispenserList = new PharmacyListBuilder().retrieveDispenserList();
        String s = "1,2,3,4,14";
        boolean ARV = false;
        for (int i = 0; i < dispenserList.size(); i++) {
            String id = (String) dispenserList.get(i).get("regimentypeId"); // retrieve regimentype id from list
            if (s.contains(id)) {
                ARV = true;
            }
        }

        if (ARV) {
            try {
                query = "SELECT date_visit, regimen_id, regimentype_id, "
                        + "duration, next_appointment FROM pharmacy WHERE "
                        + "patient_id = " + patientId + " AND regimentype_id "
                        + "IN (1, 2, 3, 4, 14) ORDER BY date_visit DESC LIMIT 1";
                boolean found[] = {false};
                jdbcTemplate.query(query, rs -> {
                    found[0] = true;
                    String dateVisit = DateUtil.parseDateToString(rs.getDate("date_visit"), "yyyy-MM-dd");
                    double duration = rs.getDouble("duration");
                    String nextAppointment = (rs.getDate("next_appointment") == null)
                            ? "" : DateUtil.parseDateToString(rs.getDate("next_appointment"), "yyyy-MM-dd");
                    if (nextAppointment.isEmpty()) {
                        nextAppointment = DateUtil.parseDateToString(DateUtil
                                .addDay(rs.getDate("date_visit"), rs.getInt("duration")), "yyyy-MM-dd");
                    }
                    String regimentype = RegimenJDBC.getRegimentype(rs.getLong("regimentype_id"));
                    String regimen = RegimenJDBC.getRegimen(rs.getLong("regimen_id"));
                    executeUpdate("UPDATE patient SET regimentype = '"
                            + regimentype + "', regimen = '" + regimen + "', "
                            + "date_last_refill = '" + dateVisit + "', "
                            + "date_next_refill = '" + nextAppointment + "', "
                            + "last_refill_duration = " + duration + ", "
                            + "time_stamp = NOW() WHERE patient_id = " + patientId);

                    //check if the last regimen was logged and log into the regimen history table if not
                    String q = "SELECT patient_id FROM regimenhistory WHERE patient_id = "
                            + patientId + " AND regimentype = '" + regimentype
                            + "' AND regimen = '" + regimen + "'";
                    boolean[] found2 = {false};
                    jdbcTemplate.query(q, rse -> {
                        found2[0] = true;
                    });
                    if (!found2[0]) {
                        executeUpdate("INSERT INTO regimenhistory (patient_id, "
                                + "facility_id, regimentype, regimen, date_visit, "
                                + "reason_switched_subs, time_stamp) VALUES("
                                + patientId + ", " + facilityId + ", '" + regimentype + "', '"
                                + regimen + "', '" + dateVisit + "', '', NOW())");
                    }
                });
                if (!found[0]) {
                    executeUpdate("UPDATE patient SET regimentype = '', "
                            + "regimen = '', date_last_refill = null, date_next_refill = "
                            + "null, last_refill_duration = null, time_stamp = "
                            + "NOW() WHERE patient_id = " + patientId);
                    executeUpdate("DELETE FROM regimenhistory WHERE patient_id = " + patientId);
                }
                if (!delete) {
                    statusUpdateAfterRefill();
                }
            } catch (Exception exception) {

            }
        }
    }

    private void statusUpdateAfterRefill() {
        System.out.println("Updating status....");
        request = ServletActionContext.getRequest();
        String patientId = request.getParameter("patientId");
        String currentStatus[] = {request.getParameter("currentStatus")};
        String dateCurrentStatus = request.getParameter("dateCurrentStatus");
        String dateStarted = request.getParameter("dateStarted");
        String dateVisit = request.getParameter("dateVisit");

        try {

            if (!dateVisit.isEmpty() && (DateUtil.parseStringToDate(dateVisit, "MM/dd/yyyy"))
                    .after(DateUtil.parseStringToDate(dateCurrentStatus, "MM/dd/yyyy"))) {
                if (!dateStarted.isEmpty() && (currentStatus[0].trim()
                        .equalsIgnoreCase("Lost to Follow Up") || currentStatus[0]
                        .trim().equalsIgnoreCase("Stopped Treatment")
                        || currentStatus[0].trim().equalsIgnoreCase("ART Transfer Out"))) {
                    currentStatus[0] = "ART Restart";
                    query = "UPDATE patient SET current_status = ?, "
                            + "date_current_status = ?, time_stamp = ?, "
                            + "date_tracked = null, outcome = '', agreed_date = "
                            + "null WHERE facility_id = ? AND patient_id = ?";

                    transactionTemplate.execute((ts) -> {
                        jdbcTemplate.update(query, currentStatus[0],
                                DateUtil.parseStringToSqlDate(dateVisit, "MM/dd/yyyy"),
                                new Date(), (Long) request.getSession()
                                        .getAttribute("facilityId"), Long.parseLong(patientId));
                        return null; //To change body of generated lambdas, choose Tools | Templates.
                    });

                    //log ART Restart date in statushistory
                    new StatusHistoryUpdater().logStatusChange(Long.parseLong(patientId), currentStatus[0], dateVisit);

                }
            }
        } catch (Exception exception) {

        }
    }

    private void executeUpdate(String query) {
        try {
            transactionTemplate.execute((ts) -> {
                jdbcTemplate.execute(query);
                return null; //To change body of generated lambdas, choose Tools | Templates.
            });
        } catch (Exception exception) {

        }
    }

}
