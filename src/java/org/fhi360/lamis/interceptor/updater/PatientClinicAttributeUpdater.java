/**
 *
 * @author user1
 */
package org.fhi360.lamis.interceptor.updater;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;

import org.fhi360.lamis.utility.DateUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class PatientClinicAttributeUpdater {

    private String query;

    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    private HttpServletRequest request;

    //This method is called each time a clinic record is added or modfied to update the patient clinic attribute
    public void lastClinicDate() {
        request = ServletActionContext.getRequest();
        long patientId = Long.parseLong(request.getParameter("patientId"));
        String currentStatus = request.getParameter("currentStatus");
        String dateVisit = DateUtil.formatDateString(request.getParameter("dateVisit"), "MM/dd/yyyy", "yyyy-MM-dd");

        int commence = Integer.parseInt(request.getParameter("commence"));
        try {
            query = "SELECT date_visit, clinic_stage, next_appointment FROM clinic "
                    + "WHERE patient_id = " + patientId + " ORDER BY date_visit DESC LIMIT 1";
            boolean[] found = {false};
            jdbcTemplate.query(query, rs -> {
                found[0] = true;
                String date = DateUtil.parseDateToString(rs.getDate("date_visit"), "yyyy-MM-dd");
                String nextAppointment = (rs.getDate("next_appointment") == null) ? "" : DateUtil.parseDateToString(rs.getDate("next_appointment"), "yyyy-MM-dd");
                String lastClinicStage = (rs.getString("clinic_stage") == null) ? "" : rs.getString("clinic_stage");
                if (!nextAppointment.isEmpty()) {
                    executeUpdate("UPDATE patient SET last_clinic_stage = '"
                            + lastClinicStage + "', date_last_clinic = '" + date + "', "
                            + "date_next_clinic = '" + nextAppointment + "',"
                            + " time_stamp = NOW() WHERE patient_id = " + patientId);
                } else {
                    executeUpdate("UPDATE patient SET last_clinic_stage = '"
                            + lastClinicStage + "', date_last_clinic = '" + date + "', "
                            + "time_stamp = NOW() WHERE patient_id = " + patientId);
                }
            });
            if (!found[0]) {
                executeUpdate("UPDATE patient SET last_clinic_stage = '', "
                        + "date_last_clinic = null, date_next_clinic = null, "
                        + "time_stamp = NOW() WHERE patient_id = " + patientId);
            }

            if (commence == 1) {
                if (currentStatus.equalsIgnoreCase("HIV+ non ART") || 
                        currentStatus.equalsIgnoreCase("Pre-ART Transfer In")) {
                    query = "UPDATE patient SET date_started = '" + dateVisit
                            + "', current_status = 'ART Start', date_current_status = '"
                            + dateVisit + "', time_stamp = NOW() WHERE patient_id = " + patientId;
                } else {
                    query = "UPDATE patient SET date_started = '" + dateVisit + "', "
                            + "time_stamp = NOW() WHERE patient_id = " + patientId;
                }
                executeUpdate(query);
                //log ART start date in statushistory
                new StatusHistoryUpdater().logStatusChange(patientId, "ART Start", request.getParameter("dateVisit"));
                //log Regimen at ART commencement in regimenhistory
                if (!request.getParameter("regimentype").isEmpty()) {
                    logRegimenChange(patientId, request.getParameter("regimentype"), request.getParameter("regimen"), dateVisit);
                }
            }
            //Nullify defaulter tracking outcome if date of visit is equal or later than date of tracking
            if (commence == 0) {
                DefaulterAttributeUpdater.nullifyTrackingOutcome((Long) request.getSession().getAttribute("facilityId"), Long.parseLong(request.getParameter("patientId")), request.getParameter("dateVisit"));
            }
        } catch (Exception exception) {

        }
    }

    private void logRegimenChange(long patientId, String regimentype, String regimen, String dateVisit) {
        long facilityId = (Long) request.getSession().getAttribute("facilityId");
        //check if the last regimen was logged and log into the regimen history table if not
        try {
            boolean[] found = {false};
            jdbcTemplate.query("SELECT patient_id FROM regimenhistory WHERE "
                    + "patient_id = " + patientId + " AND date_visit = '" + dateVisit + "'", rs -> {
                        found[0] = true;
                    });
            if (!found[0]) {
                executeUpdate("INSERT INTO regimenhistory (patient_id, "
                        + "facility_id, regimentype, regimen, date_visit, "
                        + "reason_switched_subs, time_stamp) VALUES(" + patientId
                        + ", " + facilityId + ", '" + regimentype + "', '"
                        + regimen + "', '" + dateVisit + "', '', NOW())");
            }

        } catch (Exception exception) {

        }
    }

    //This method is called when an ART commencement record is deleted to nullify the patient date Started attribute
    public void nullifyStartDate() {
        String patientId = ServletActionContext.getRequest().getParameter("patientId");
        try {
            query = "UPDATE patient SET date_started = null, time_stamp = "
                    + "NOW() WHERE patient_id = " + patientId;
            executeUpdate(query);
        } catch (Exception exception) {

        }

    }

    private void executeUpdate(String query) {
        try {
            transactionTemplate.execute((ts) -> {
                jdbcTemplate.execute(query);
                return null; 
            });
        } catch (Exception exception) {

        }
    }

}
