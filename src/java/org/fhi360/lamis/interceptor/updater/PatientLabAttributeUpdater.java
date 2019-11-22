/**
 *
 * @author user1
 */
package org.fhi360.lamis.interceptor.updater;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;

import org.fhi360.lamis.utility.DateUtil;
import static org.fhi360.lamis.utility.StringUtil.isInteger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class PatientLabAttributeUpdater {

    private String query;

    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    private HttpServletRequest request;

    //This method is called each time a lab record is added or modfied to update the patient lab attribute
    public void lastCd4Date() {
        request = ServletActionContext.getRequest();
        String dateReported = request.getParameter("dateReported");
        long patientId = Long.parseLong(request.getParameter("patientId"));

        try {

            query = "SELECT resultab, resultpc, date_reported FROM laboratory WHERE patient_id = " + patientId + " AND labtest_id = 1 ORDER BY date_reported DESC LIMIT 1";
            boolean[] found = {false};
            jdbcTemplate.query(query, rs -> {
                found[0] = true;
                String dateLast = (rs.getDate("date_reported") == null) ? "" : DateUtil.parseDateToString(rs.getDate("date_reported"), "yyyy-MM-dd");
                String resultab = rs.getString("resultab");
                String resultpc = rs.getString("resultpc");
                Double lastCd4 = 0.0;
                if (!dateLast.isEmpty()) {
                    if (isInteger(resultab)) {
                        lastCd4 = Double.valueOf(resultab);
                        executeUpdate("UPDATE patient SET last_cd4 = " + lastCd4 + ", date_last_cd4 = '" + dateLast + "', time_stamp = NOW() WHERE patient_id = " + patientId);
                    } else {
                        if (isInteger(resultpc)) {
                            lastCd4 = Double.valueOf(resultpc);
                            executeUpdate("UPDATE patient SET last_cd4p = " + lastCd4 + ", date_last_cd4 = '" + dateLast + "', time_stamp = NOW() WHERE patient_id = " + patientId);
                        }
                    }
                }
            });
            if (!found[0]) {
                executeUpdate("UPDATE patient SET last_cd4 = null, last_cd4p = null, date_last_cd4 = null, time_stamp = NOW() WHERE patient_id = " + patientId);
            }
            //Nullify defaulter tracking outcome if date of lab reporting is equal or later than date of tracking
            DefaulterAttributeUpdater.nullifyTrackingOutcome((Long) request.getSession().getAttribute("facilityId"), Long.parseLong(request.getParameter("patientId")), dateReported);
        } catch (Exception exception) {

        }
    }

    //This method is called each time a lab record is added or modfied to update the patient lab attribute
    public void lastViralLoadDate() {
        request = ServletActionContext.getRequest();
        String dateReported = request.getParameter("dateReported");
        long patientId = Long.parseLong(request.getParameter("patientId"));

        try {

            query = "SELECT resultab, resultpc, date_reported FROM laboratory WHERE patient_id = " + patientId + " AND labtest_id = 16 ORDER BY date_reported DESC LIMIT 1";
            //query = "SELECT resultab, resultpc, date_reported FROM laboratory WHERE patient_id = " + patientId + " AND date_reported = (SELECT MAX(date_reported) FROM laboratory WHERE patient_id = " + patientId + " AND labtest_id = 16)"; 
            boolean[] found = {false};
            jdbcTemplate.query(query, rs -> {
                found[0] = true;
                String dateLast = (rs.getDate("date_reported") == null) ? "" : DateUtil.parseDateToString(rs.getDate("date_reported"), "yyyy-MM-dd");
                String resultab = rs.getString("resultab");
                Double lastViralLoad = 0.0;
                if (!dateLast.isEmpty()) {
                    if (isInteger(resultab)) {
                        lastViralLoad = Double.valueOf(resultab);
                    }
                    executeUpdate("UPDATE patient SET last_viral_load = " + lastViralLoad + ", date_last_viral_load = '" + dateLast + "', time_stamp = NOW() WHERE patient_id = " + patientId);
                }
            });
            if (!found[0]) {
                executeUpdate("UPDATE patient SET last_viral_load = null, date_last_viral_load = null, time_stamp = NOW() WHERE patient_id = " + patientId);
            }
            //Nullify defaulter tracking outcome if date of lab reporting if equal or later than date of tracking
            DefaulterAttributeUpdater.nullifyTrackingOutcome((Long) request.getSession().getAttribute("facilityId"), Long.parseLong(request.getParameter("patientId")), dateReported);
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
