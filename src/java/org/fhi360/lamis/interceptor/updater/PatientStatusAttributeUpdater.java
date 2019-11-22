/**
 *
 * @author user1
 */
package org.fhi360.lamis.interceptor.updater;

import java.util.*;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;

import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class PatientStatusAttributeUpdater {

    private String query;
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    private HttpServletRequest request;
    private ResultSet resultSet;

    public void updateStatus() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
        Date lastStatusDate;
        Date currentDate;

        request = ServletActionContext.getRequest();
        String patientId[] = {request.getParameter("patientId")};
        String currentStatus[] = {request.getParameter("currentStatus")};
        String dateCurrentStatus[] = {request.getParameter("dateCurrentStatus")};
        String dateTracked[] = {request.getParameter("dateTracked")};
        String agreedDate[] = {request.getParameter("agreedDate")};
        String causeDeath[] = {request.getParameter("causeDeath")};
        String outcome[] = {request.getParameter("outcome")};

        try {

            lastStatusDate = getDateCurrentStatus(Long.parseLong(request.getParameter("patientId")));
            currentDate = dateFormat2.parse(request.getParameter("dateCurrentStatus"));

            System.out.println("Current Date: " + currentDate);
            System.out.println("Previous Date: " + lastStatusDate);

            if (currentDate.compareTo(lastStatusDate) >= 0) {

                if (outcome[0].equals(Constants.ArtStatus.ART_START)) {
                    query = "UPDATE patient SET current_status = ?, "
                            + "date_current_status = ?, date_started = ?, outcome = ?, "
                            + "time_stamp = ? WHERE facility_id = ? AND patient_id = ?";

                    transactionTemplate.execute((ts) -> {
                        jdbcTemplate.update(query, Constants.ArtStatus.ART_START,
                                DateUtil.parseStringToSqlDate(dateCurrentStatus[0], "MM/dd/yyyy"),
                                DateUtil.parseStringToSqlDate(dateCurrentStatus[0], "MM/dd/yyyy"),
                                Constants.ArtStatus.ART_START,
                                new java.sql.Timestamp(new java.util.Date().getTime()),
                                (Long) ServletActionContext.getRequest().getSession().getAttribute("facilityId"),
                                Long.parseLong(request.getParameter("patientId")));
                        return null; //To change body of generated lambdas, choose Tools | Templates.
                    });

                } else {
                    outcome[0] = outcome[0].trim();
                    if (outcome[0].equalsIgnoreCase(Constants.TxMlStatus.TX_ML_NOT_TRACED)
                            || outcome[0].equalsIgnoreCase(Constants.TxMlStatus.TX_ML_TRACED)
                            || outcome[0].equalsIgnoreCase(Constants.TxMlStatus.TX_ML_TRANSFER)) {
                        query = "UPDATE patient SET outcome = ?, date_tracked = ?, "
                                + "agreed_date = ?, time_stamp = ? WHERE "
                                + "facility_id = ? AND patient_id = ?";
                        transactionTemplate.execute((ts) -> {
                            jdbcTemplate.update(query, outcome[0],
                                    DateUtil.parseStringToSqlDate(dateTracked[0], "MM/dd/yyyy"),
                                    agreedDate[0].isEmpty() ? null : DateUtil.parseStringToSqlDate(
                                    agreedDate[0], "MM/dd/yyyy"), new Date(),
                                    (Long) ServletActionContext.getRequest().getSession()
                                            .getAttribute("facilityId"), new Date(),
                                    (Long) ServletActionContext.getRequest().getSession()
                                            .getAttribute("facilityId"), Long.parseLong(patientId[0]));
                            return null; //To change body of generated lambdas, choose Tools | Templates.
                        });

                    } else {
                        if (outcome.equals(Constants.TxMlStatus.TX_ML_DIED)) {

                            query = "UPDATE patient SET current_status = ?, "
                                    + "date_current_status = ?, outcome = ?, "
                                    + "cause_death = ?, date_tracked = ?, "
                                    + "time_stamp = ? WHERE facility_id = ? "
                                    + "AND patient_id = ?";

                            transactionTemplate.execute((ts) -> {
                                jdbcTemplate.update(query, currentStatus[0],
                                        DateUtil.parseStringToSqlDate(dateCurrentStatus[0], "MM/dd/yyyy"),
                                        outcome[0], causeDeath[0],
                                        dateTracked[0].isEmpty() ? null : DateUtil.parseStringToSqlDate(dateTracked[0], "MM/dd/yyyy"),
                                        new java.sql.Timestamp(new java.util.Date().getTime()),
                                        (Long) ServletActionContext.getRequest().getSession().getAttribute("facilityId"),
                                        Long.parseLong(patientId[0]));
                                return null; //To change body of generated lambdas, choose Tools | Templates.
                            });

                        } else {
                            query = "UPDATE patient SET current_status = ?, "
                                    + "date_current_status = ?, outcome = ?, "
                                    + "date_tracked = ?, time_stamp = ? WHERE "
                                    + "facility_id = ? AND patient_id = ?";

                            transactionTemplate.execute((ts) -> {
                                jdbcTemplate.update(query, currentStatus[0],
                                        DateUtil.parseStringToSqlDate(dateCurrentStatus[0], "MM/dd/yyyy"),
                                        outcome[0],
                                        dateTracked[0].isEmpty() ? null : DateUtil.parseStringToSqlDate(dateTracked[0], "MM/dd/yyyy"),
                                        new java.sql.Timestamp(new java.util.Date().getTime()),
                                        (Long) ServletActionContext.getRequest().getSession().getAttribute("facilityId"),
                                        Long.parseLong(patientId[0]));
                                return null; //To change body of generated lambdas, choose Tools | Templates.
                            });
                        }
                    }

                }

            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void updateWithPreviousStatus() {
        request = ServletActionContext.getRequest();
        String patientId = request.getParameter("patientId");
        long facilityId = (Long) request.getSession().getAttribute("facilityId");
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        try {

            query = "SELECT current_status, date_current_status FROM statushistory "
                    + "WHERE facility_id = " + facilityId + " AND patient_id = "
                    + Long.parseLong(patientId) + " ORDER BY date_current_status DESC LIMIT 1";
            boolean[] found = {false};
            jdbcTemplate.query(query, resultSet -> {
                found[0] = true;
                if (resultSet.getDate("date_current_status") != null) {
                    String currentStatus[] = {resultSet.getString("current_status")};
                    String dateCurrentStatus[] = {dateFormat.format(resultSet.getDate("date_current_status"))};

                    query = "UPDATE patient SET current_status = ?, "
                            + "date_current_status = ?, time_stamp = ? WHERE "
                            + "facility_id = ? AND patient_id = ?";
                    transactionTemplate.execute((ts) -> {
                        jdbcTemplate.update(query, currentStatus[0],
                                DateUtil.parseStringToSqlDate(dateCurrentStatus[0], "MM/dd/yyyy"),
                                dateCurrentStatus[0].isEmpty() ? null : DateUtil.parseStringToSqlDate(dateCurrentStatus[0], "MM/dd/yyyy"),
                                new Date(), facilityId, Long.parseLong(patientId));
                        return null; //To change body of generated lambdas, choose Tools | Templates.
                    });
                }
            });
            if (!found[0]) {
                //If no status is found in the status history table reset current status to status at registration
                query = "UPDATE patient SET current_status = status_registration, "
                        + "date_current_status = date_registration, time_stamp = "
                        + "NOW() WHERE facility_id = ? AND patient_id = ?";
                transactionTemplate.execute((ts) -> {
                    jdbcTemplate.update(query, facilityId,
                            Long.parseLong(patientId));
                    return null; //To change body of generated lambdas, choose Tools | Templates.
                });
            }
        } catch (Exception exception) {

        }
    }

    public void updateStatusDefaulter() {
        request = ServletActionContext.getRequest();
        String patientId[] = {request.getParameter("patientId")};
        String dateTracked[] = {request.getParameter("dateTracked")};
        String outcome[] = {request.getParameter("outcome")};
        String agreedDate[] = {request.getParameter("agreedDate")};
        try {

            if (!dateTracked[0].equals("") && !dateTracked[0].isEmpty()) {
                if (!agreedDate[0].equals("") && !agreedDate[0].isEmpty()) {
                    query = "UPDATE patient SET current_status = ?, "
                            + "date_current_status = ?, date_tracked = ?, "
                            + "outcome = ?, agreed_date = ?, date_next_clinic = ?,"
                            + " date_next_refill = ?, time_stamp = ? WHERE "
                            + "facility_id = ? AND patient_id = ?";
                    transactionTemplate.execute((ts) -> {
                        jdbcTemplate.update(query, outcome[0],
                                (dateTracked[0].isEmpty() ? null : DateUtil.parseStringToSqlDate(dateTracked[0], "MM/dd/yyyy")),
                                (dateTracked[0].isEmpty() ? null : DateUtil.parseStringToSqlDate(dateTracked[0], "MM/dd/yyyy")),
                                outcome[0],
                                (agreedDate[0].isEmpty() ? null : DateUtil.parseStringToSqlDate(agreedDate[0], "MM/dd/yyyy")),
                                (agreedDate[0].isEmpty() ? null : DateUtil.parseStringToSqlDate(agreedDate[0], "MM/dd/yyyy")),
                                (agreedDate[0].isEmpty() ? null : DateUtil.parseStringToSqlDate(agreedDate[0], "MM/dd/yyyy")),
                                new Date(), (Long) ServletActionContext.getRequest().getSession().getAttribute("facilityId"),
                                Long.parseLong(patientId[0])
                        );
                        return null; //To change body of generated lambdas, choose Tools | Templates.
                    });

                } else {
                    query = "UPDATE patient SET current_status = ?, date_current_status = ?, "
                            + "date_tracked = ?, outcome = ?, agreed_date = ?, time_stamp = ? "
                            + "WHERE facility_id = ? AND patient_id = ?";

                    transactionTemplate.execute((ts) -> {
                        jdbcTemplate.update(query, outcome[0],
                                (dateTracked[0].isEmpty() ? null : DateUtil.parseStringToSqlDate(dateTracked[0], "MM/dd/yyyy")),
                                (dateTracked[0].isEmpty() ? null : DateUtil.parseStringToSqlDate(dateTracked[0], "MM/dd/yyyy")),
                                outcome[0],
                                null, new java.sql.Timestamp(new java.util.Date().getTime()),
                                (Long) ServletActionContext.getRequest().getSession().getAttribute("facilityId"),
                                Long.parseLong(patientId[0])
                        );
                        return null; //To change body of generated lambdas, choose Tools | Templates.
                    });

                }

            }
        } catch (Exception exception) {

        }
    }

    public void updateStatusDefaulter(Map map) {

        String patientId[] = {request.getParameter("patientId")};
        String dateTracked[] = {request.getParameter("dateTracked")};
        String outcome[] = {request.getParameter("outcome")};
        String agreedDate[] = {request.getParameter("agreedDate")};

        try {

            if (!dateTracked[0].equals("") && !dateTracked[0].isEmpty()) {
                if (!agreedDate.equals("") && !agreedDate[0].isEmpty()) {
                    query = "UPDATE patient SET current_status = ?, date_current_status = ?, "
                            + "date_tracked = ?, outcome = ?, agreed_date = ?, date_next_clinic = ?, "
                            + "date_next_refill = ?, time_stamp = ? WHERE facility_id = ? AND patient_id = ?";

                    transactionTemplate.execute((ts) -> {
                        jdbcTemplate.update(query, outcome[0],
                                (dateTracked[0].isEmpty() ? null : DateUtil.parseStringToSqlDate(dateTracked[0], "MM/dd/yyyy")),
                                (dateTracked[0].isEmpty() ? null : DateUtil.parseStringToSqlDate(dateTracked[0], "MM/dd/yyyy")),
                                outcome[0],
                                (agreedDate[0].isEmpty() ? null : DateUtil.parseStringToSqlDate(agreedDate[0], "MM/dd/yyyy")),
                                (agreedDate[0].isEmpty() ? null : DateUtil.parseStringToSqlDate(agreedDate[0], "MM/dd/yyyy")),
                                (agreedDate[0].isEmpty() ? null : DateUtil.parseStringToSqlDate(agreedDate[0], "MM/dd/yyyy")),
                                new java.sql.Timestamp(new java.util.Date().getTime()),
                                (Long) ServletActionContext.getRequest().getSession().getAttribute("facilityId"),
                                Long.parseLong(patientId[0])
                        );
                        return null; //To change body of generated lambdas, choose Tools | Templates.
                    });
                } else {
                    query = "UPDATE patient SET current_status = ?, "
                            + "date_current_status = ?, date_tracked = ?, "
                            + "outcome = ?, agreed_date = ?, time_stamp = ? "
                            + "WHERE facility_id = ? AND patient_id = ?";
                    transactionTemplate.execute((ts) -> {
                        jdbcTemplate.update(query, outcome[0],
                                (dateTracked[0].isEmpty() ? null : DateUtil
                                .parseStringToSqlDate(dateTracked[0], "MM/dd/yyyy")),
                                (dateTracked[0].isEmpty() ? null : DateUtil
                                .parseStringToSqlDate(dateTracked[0], "MM/dd/yyyy")),
                                outcome[0], null, new Date(),
                                (Long) ServletActionContext.getRequest().getSession().getAttribute("facilityId"),
                                Long.parseLong(patientId[0])
                        );
                        return null; //To change body of generated lambdas, choose Tools | Templates.
                    });
                }

            }
        } catch (Exception exception) {

        }
    }

    private Date getDateCurrentStatus(Long patientId) {
        String query = "select date_current_status from statushistory where patient_id = ? "
                + "order by date_current_status desc limit 2";
        return jdbcTemplate.query(query, rs -> {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, 1900);
            Date date = cal.getTime();
            while (rs.next()) {
                date = rs.getDate(1);
            }
            return date;
        }, patientId);
    }
}
