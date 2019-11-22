/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.dao.jdbc;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.DateUtil;

import org.fhi360.lamis.utility.builder.StatusHistoryBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author user10
 */
public class StatusHistoryJDBC {

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    public StatusHistoryJDBC() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    public void findStatus(long patientId, String dateCurrentStatus) {
        long facilityId = (Long) session.getAttribute("facilityId");
        try {

//            query = "SELECT DISTINCT patient.facility_id, patient.patient_id, patient.date_registration, patient.status_registration, patient.date_started, statushistory.history_id, statushistory.current_status, statushistory.date_current_status "
//                    + " FROM patient JOIN statushistory ON patient.patient_id = statushistory.patient_id WHERE patient.facility_id = ? AND statushistory.facility_id = ? AND patient.patient_id = ? AND statushistory.date_current_status = ?";
            query = "SELECT DISTINCT patient.facility_id, patient.patient_id, patient.date_registration, patient.status_registration, patient.date_started, statushistory.* "
                    + " FROM patient JOIN statushistory ON patient.patient_id = statushistory.patient_id WHERE patient.facility_id = ? AND statushistory.facility_id = ? AND patient.patient_id = ? AND statushistory.date_current_status = ?";

            jdbcTemplate.query(query, resultSet -> {
                new StatusHistoryBuilder().buildStatusRecord(resultSet);
                return null;
            }, (Long) session.getAttribute("facilityId"), (Long) session.getAttribute("facilityId"), patientId, DateUtil.parseStringToSqlDate(dateCurrentStatus, "MM/dd/yyyy"));

        } catch (Exception exception) {

        }
    }

    public void findStatusHistory(long historyId, String dateCurrentStatus) {

        long facilityId = (Long) session.getAttribute("facilityId");
        try {
            query = "SELECT DISTINCT patient.facility_id, patient.patient_id, patient.date_registration, patient.status_registration, patient.date_started, statushistory.* "
                    + " FROM patient JOIN statushistory ON patient.patient_id = statushistory.patient_id WHERE patient.facility_id = ? AND statushistory.facility_id = ? AND statushistory.history_id = ? "
                    + " AND (statushistory.date_current_status = ? OR statushistory.date_tracked = ?)";

            jdbcTemplate.query(query, resultSet -> {
                new StatusHistoryBuilder().buildStatusRecord(resultSet);
                return null;
            }, (Long) session.getAttribute("facilityId"), (Long) session.getAttribute("facilityId"), historyId, DateUtil.parseStringToSqlDate(dateCurrentStatus, "MM/dd/yyyy"), DateUtil.parseStringToSqlDate(dateCurrentStatus, "MM/dd/yyyy"));

        } catch (Exception exception) {

        }
    }

    public long getStatusHistoryId(long facilityId, long patientId, String dateCurrentStatus) {
        query = "SELECT statushistory_id FROM statushistory WHERE facility_id = '" + facilityId + "' AND patient_id = " + patientId + " AND date_current_status = '" + dateCurrentStatus;
        Long id = 0L;
        try {
            id = jdbcTemplate.queryForObject(query, Long.class);
        } catch (Exception e) {
        }
        return id;
    }

    public long getStatushistoryId(long patientId, Date dateCurrentStatus) {
        query = "SELECT statushistory_id FROM statushistory WHERE patient_id = " + patientId + " AND date_current_status = '" + dateCurrentStatus;
        Long id = 0L;
        try {
            id = jdbcTemplate.queryForObject(query, Long.class);
        } catch (Exception e) {
        }
        return id;
    }
    
}
