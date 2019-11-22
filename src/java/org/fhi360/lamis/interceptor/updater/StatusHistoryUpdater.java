/**
 *
 * @author user1
 */
package org.fhi360.lamis.interceptor.updater;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class StatusHistoryUpdater {

    private String query;
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private Patient patientObjSession;
    private Patient patient;
    private HttpServletRequest request;

    //This method is called from the AfterUpdateInterceptor when a new patient is saved or modified
    public void logStatusChange() {
        request = ServletActionContext.getRequest();
        System.out.println("StatusHistoryUpdater...... called");

        if (request.getSession().getAttribute("patient") != null) {

            patient = (Patient) request.getSession().getAttribute("patient");  //retrieve patient object from session                                
            if (request.getSession().getAttribute("patientObjSession") != null) {
                patientObjSession = (Patient) request.getSession().getAttribute("patientObjSession");
                System.out.println("StatusHistoryUpdate......  status modify: " + patientObjSession.getStatusRegistration());
                modifyStatus(patient.getPatientId(), patient.getStatusRegistration(), patient.getDateRegistration(), patientObjSession.getStatusRegistration(), patientObjSession.getDateRegistration());
            } else {
                System.out.println("StatusHistoryUpdate......  status save: " + patient.getStatusRegistration());
                saveStatus(patient.getPatientId(), patient.getStatusRegistration(), patient.getDateRegistration());
            }
        }
    }

    //This method is called by DefaulterAttributeUpdater class when LTFU and Stopped Treatment changes to Restart
    //It is also called by PatientClinicAttributeUpdater class when ART commencement record is entered
    //It checks for the status ART Start or ART Restart and insert if not exist
    public void logStatusChange(Long patientId, String currentStatus, String dateCurrentStatus) {
        long facilityId = (Long) ServletActionContext.getRequest().getSession().getAttribute("facilityId");
        try {
            query = "SELECT history_id FROM statushistory WHERE facility_id = ? "
                    + "AND patient_id = ? AND current_status = ?";
            boolean[] found = {false};
            jdbcTemplate.query(query, resultSet -> {
                found[0] = true;
                query = "UPDATE statushistory SET date_current_status = ?, time_stamp = ? WHERE history_id = ?";
                transactionTemplate.execute(ts -> {
                    try {
                        jdbcTemplate.update(query, DateUtil.parseStringToSqlDate(dateCurrentStatus, "MM/dd/yyyy"),
                                new Date(), resultSet.getLong("history_id"));
                    } catch (Exception e) {
                    }
                    return null;
                });
            }, facilityId, patientId, currentStatus);
            if (!found[0]) {
                query = "INSERT INTO statushistory(patient_id, facility_id, "
                        + "current_status, date_current_status, time_stamp) "
                        + "VALUES (?, ?, ?, ?, ?)";

                transactionTemplate.execute(ts -> {
                    try {
                        jdbcTemplate.update(query, patientId,
                                facilityId, currentStatus, DateUtil.parseStringToSqlDate(dateCurrentStatus, "MM/dd/yyyy"), new Date());
                    } catch (Exception e) {
                    }
                    return null;
                });

            }
        } catch (Exception exception) {

        }
    }

    //This method is called by StatusHistoryAction class when client tracking details is saved
    //It checks if the outcome does not exist in statushistory table and insert it
    //If the same date of outcome is found the status is update 
    public void logTrackingOutcome(Long patientId, String currentStatus, String dateCurrentStatus) {
        long facilityId = (Long) ServletActionContext.getRequest().getSession().getAttribute("facilityId");
        try {
            query = "SELECT * FROM statushistory WHERE facility_id = ? AND "
                    + "patient_id = ? AND (current_status = ? OR date_current_status = ?)";

            boolean[] found = {false};
            jdbcTemplate.query(query, resultSet -> {
                found[0] = true;

                query = "SELECT * FROM statushistory WHERE facility_id = ? AND patient_id = ? AND date_current_status = ?";

                boolean[] found2 = {false};
                jdbcTemplate.query(query, resultSet1 -> {
                    found2[0] = true;
                    modifyStatus(resultSet1.getLong("history_id"), currentStatus, DateUtil.parseStringToDate(dateCurrentStatus, "MM/dd/yyyy"));
                }, facilityId, patientId, DateUtil.parseStringToSqlDate(dateCurrentStatus, "MM/dd/yyyy"));
                if (!found[0]) {
                    query = "SELECT * FROM statushistory WHERE facility_id = ? AND patient_id = ? AND current_status = ?";
                    jdbcTemplate.query(query, resultSet1 -> {
                        if (!resultSet1.getString("current_status").equals("Lost to Follow Up")) {
                            modifyStatus(resultSet1.getLong("history_id"), currentStatus, DateUtil.parseStringToDate(dateCurrentStatus, "MM/dd/yyyy"));
                        } else {
                            saveStatus(patientId, currentStatus, DateUtil.parseStringToDate(dateCurrentStatus, "MM/dd/yyyy"));
                        }
                    }, facilityId, patientId, currentStatus);
                }

            }, facilityId, patientId, currentStatus, DateUtil.parseStringToSqlDate(dateCurrentStatus, "MM/dd/yyyy"));
            if (!found[0]) {
                saveStatus(patientId, currentStatus, DateUtil.parseStringToDate(dateCurrentStatus, "MM/dd/yyyy"));
            }
        } catch (Exception exception) {

        }
    }

    public void saveStatus(Long patientId, String currentStatus, Date dateCurrentStatus) {
        long facilityId = (Long) ServletActionContext.getRequest().getSession().getAttribute("facilityId");
        try {

            query = "INSERT INTO statushistory(patient_id, facility_id, current_status, date_current_status, time_stamp) VALUES (?, ?, ?, ?, ?)";

            transactionTemplate.execute(ts -> {
                try {
                    jdbcTemplate.update(query, patientId,
                            facilityId, currentStatus, dateCurrentStatus, new Date());
                } catch (Exception e) {
                }
                return null;
            });

        } catch (Exception exception) {
            exception.printStackTrace();

        }
    }

    public void modifyStatus(Long patientId, String currentStatus, Date dateCurrentStatus, String previousStatus, Date datePreviousStatus) {
        try {

            // status at registration or date of registration modified
            if (!currentStatus.equals(previousStatus) || dateCurrentStatus.compareTo(datePreviousStatus) != 0) {
                query = "UPDATE statushistory SET current_status = ?, "
                        + "date_current_status = ?, time_stamp = ? WHERE patient_id = ? "
                        + "AND current_status = ? AND date_current_status = ?";

                transactionTemplate.execute(ts -> {
                    try {
                        jdbcTemplate.update(query,
                                currentStatus, dateCurrentStatus, new Date(), patientId, previousStatus, new Date());
                    } catch (Exception e) {
                    }
                    return null;
                });

            }
        } catch (Exception exception) {

        }
    }

    public void modifyStatus(Long historyId, String currentStatus, Date dateCurrentStatus) {
        try {

            query = "UPDATE statushistory SET current_status = ?, date_current_status = ?, time_stamp = ? WHERE history_id = ?";
            transactionTemplate.execute(ts -> {
                try {
                    jdbcTemplate.update(query,
                            currentStatus, dateCurrentStatus, new Date(), historyId);
                } catch (Exception e) {
                }
                return null;
            });

        } catch (Exception exception) {

        }
    }

}
