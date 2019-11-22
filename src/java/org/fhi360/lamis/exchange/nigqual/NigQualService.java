/**
 *
 * @author user1
 */
package org.fhi360.lamis.exchange.nigqual;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.dao.jdbc.ClinicJDBC;
import org.fhi360.lamis.dao.jdbc.PharmacyJDBC;
import org.fhi360.lamis.service.DeleteService;
import org.fhi360.lamis.service.MonitorService;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.JDBCUtil;
import org.fhi360.lamis.utility.RandomNumberGenerator;
import org.springframework.jdbc.core.JdbcTemplate;

public class NigQualService {

    private JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private int reportingMonthBegin;
    private int reportingYearBegin;
    private int reportingMonthEnd;
    private int reportingYearEnd;
    private String reportingDateBegin;
    private String reportingDateEnd;
    private long facilityId;

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private JDBCUtil jdbcUtil;
    private PreparedStatement preparedStatement;

    private int reviewPeriodId;
    private int portalId;
    private int rnl;

    public NigQualService() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    public void generateCohort() {
        reportingMonthBegin = DateUtil.getMonth(request.getParameter("reportingMonthBegin"));
        reportingYearBegin = Integer.parseInt(request.getParameter("reportingYearBegin"));
        reportingMonthEnd = DateUtil.getMonth(request.getParameter("reportingMonthEnd"));
        reportingYearEnd = Integer.parseInt(request.getParameter("reportingYearEnd"));
        reportingDateBegin = DateUtil.parseDateToString(DateUtil.getFirstDateOfMonth(reportingYearBegin, reportingMonthBegin), "yyyy-MM-dd");
        reportingDateEnd = DateUtil.parseDateToString(DateUtil.getLastDateOfMonth(reportingYearEnd, reportingMonthEnd), "yyyy-MM-dd");
        facilityId = (Long) session.getAttribute("facilityId");
        reviewPeriodId = Integer.parseInt(request.getParameter("reviewPeriodId"));
        portalId = Integer.parseInt(request.getParameter("portalId"));
        rnl = Integer.parseInt(request.getParameter("rnl"));

        //delete all record for this review period for this facility
        DeleteService deleteService = new DeleteService();
        deleteService.deleteNigqual(facilityId, reviewPeriodId);
        //then log identity of the deleted nigqual record in the monitor for the server to effect changes with synced to the server
        String entityId = request.getParameter("reviewPeriodId");
        MonitorService.logEntity(entityId, "niqual", 3);

        generate("AD");
        generate("PD");
        //generate("PB");  //Booked pregnant women who received ante natal care 6 months prior to 
        //generate("PU");  //Unbooked pregnant women who delivered at the facility 6 months prior to 
        //generate("DL");  //All deliveries during the 6 months of review period
        //generate("DT");  //All deliveries 12 - 18 months prior to 
    }

    private void generate(String thermaticArea) {
        ResultSet resultSet;
        int sampleSize = 0;
        int reccount = 0;

        try {
            Map<Integer, Long> entityMap = new HashMap<>();
            if (thermaticArea.equals("AD")) {
                query = "SELECT DISTINCT patient_id, current_status FROM patient WHERE DATEDIFF(YEAR, date_birth, CURDATE()) >= 15 AND facility_id = " + facilityId + " AND date_registration < '" + reportingDateBegin + "' AND current_status IN ('HIV+ non ART', 'ART Start', 'ART Restart', 'ART Transfer In', 'Pre-ART Transfer In') ORDER BY patient_id";
            }
            if (thermaticArea.equals("PD")) {
                query = "SELECT DISTINCT patient_id, current_status FROM patient WHERE DATEDIFF(YEAR, date_birth, CURDATE()) < 15 AND facility_id = " + facilityId + " AND date_registration < '" + reportingDateBegin + "' AND current_status IN ('HIV+ non ART', 'ART Start', 'ART Restart', 'ART Transfer In', 'Pre-ART Transfer In') ORDER BY patient_id";
            }
            if (thermaticArea.equals("PM")) {
                query = "SELECT DISTINCT patient.patient_id, patient.current_status FROM patient JOIN anc ON patient.patient_id = anc.patient_id AND patient.facility_id = anc.facility_id JOIN delivery ON patient.patient_id = delivery.patient_id AND patient.facility_id = delivery.facility_id WHERE patient.gender = 'Female' AND patient.facility_id = " + facilityId + " AND patient.date_registration <= '" + reportingDateBegin + "' AND patient.current_status IN ('HIV+ non ART', 'ART Start', 'ART Restart', 'ART Transfer In', 'Pre-ART Transfer In', 'Known Death') ORDER BY patient.patient_id";
            }

            jdbcUtil = new JDBCUtil();
            preparedStatement = jdbcUtil.getStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (!exclude(resultSet.getLong("patient_id"), resultSet.getString("current_status"), thermaticArea)) {
                    reccount++;
                    entityMap.put(reccount, resultSet.getLong("patient_id"));
                    System.out.println("Processing......." + resultSet.getLong("patient_id"));
                }
            }

            //If Generate cohort using Random Number List is unchecked, use all clients in the selected cohort
            //else apply the RNL
            if (rnl == 0) {
                sampleSize = reccount;
            } else {
                if (reccount < 20) {
                    sampleSize = reccount;
                } else {
                    if (reccount >= 5000) {
                        sampleSize = 150;
                    } else {
                        sampleSize = getSampleSize(reccount);
                    }
                }
            }

//            RECCOUNT is the number of patients who were registered before the beginning of the review period 
//            with a clinic or refill visit 6/3 month before the beginning of the review period
//            
//            SAMPLE SIZE is the number of patients to be evaluated using RNL
//            
//            Generate random numbers beginning from 1 to reccount
//            Then get the hospital numbers from the entity map
            int numbers[] = new RandomNumberGenerator().randomUnique(sampleSize, 1, reccount);
            for (int i = 0; i <= numbers.length; i++) {
                long patientId = entityMap.get(numbers[i]);
                query = "INSERT INTO nigqual (facility_id, portal_id, reporting_date_begin, reporting_date_end, review_period_id, thermatic_area, patient_id, population, sample_size) VALUES(" + facilityId + ", " + portalId + ", '" + reportingDateBegin + "', '" + reportingDateEnd + "', '" + reviewPeriodId + "', '" + thermaticArea + "', " + patientId + ", " + reccount + "," + sampleSize + ")";
                executeUpdate(query);
            }
            resultSet = null;
        } catch (Exception exception) {
            resultSet = null;
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }
    }

    private int getSampleSize(int population) {
        int size = 0;
        try {
            //query = "SELECT size FROM population WHERE lower => " + population + " AND upper <= " + population; //This query is not the same as following query
            query = "SELECT size FROM samplesize WHERE " + population + " >= lower AND " + population + " <= upper";
            preparedStatement = jdbcUtil.getStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                size = resultSet.getInt("size");
            }
        } catch (Exception exception) {
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }
        return size;
    }

    private boolean exclude(long patientId, String currentStatus, String thermaticArea) {
        boolean[] noClinic = {false};
        boolean[] noRefill = {false};
        boolean[] exclude = {true};

        try {
            if (thermaticArea.equalsIgnoreCase("AD")) {

                //Get last refill visit
                query = new PharmacyJDBC().getLastRefillVisit(patientId);
                jdbcTemplate.query(query, rs -> {
                    noRefill[0] = rs.getDate("date_visit") == null ? true
                            : DateUtil.formatDate(rs.getDate("date_visit"), "MM/dd/yyyy")
                                    .before(DateUtil.addMonth(DateUtil.formatDateStringToDate(reportingDateBegin, "yyyy-MM-dd", "MM/dd/yyyy"), -3)) ? true : false;
                });

                //Get last clinic visit
                query= new ClinicJDBC().getLastClinicVisit(patientId);
                jdbcTemplate.query(query, rs->{
                    if (currentStatus.equalsIgnoreCase("HIV+ non ART")) {
                        noClinic[0] = rs.getDate("date_visit") == null ? true : 
                                DateUtil.formatDate(rs.getDate("date_visit"), "MM/dd/yyyy")
                                        .before(DateUtil.addMonth(DateUtil.formatDateStringToDate(reportingDateBegin, "yyyy-MM-dd", "MM/dd/yyyy"), -6)) ? true : false;
                    } else {
                        noClinic[0] = rs.getDate("date_visit") == null ? true : 
                                DateUtil.formatDate(rs.getDate("date_visit"), "MM/dd/yyyy")
                                        .before(DateUtil.addMonth(DateUtil.formatDateStringToDate(reportingDateBegin, "yyyy-MM-dd", "MM/dd/yyyy"), -3)) ? true : false;
                    }
                });
                //Exclude patient without clinic or refill visit 
                if (noRefill[0] || noClinic[0]) {
                    exclude[0] = true;
                } else {
                    exclude[0] = false;
                }

            }

            if (thermaticArea.equalsIgnoreCase("PD")) {
                //Get last clinic visit
                query = new ClinicJDBC().getLastClinicVisit(patientId);
                jdbcTemplate.query(query, rs->{
                    exclude[0] = rs.getDate("date_visit") == null ? true : 
                            DateUtil.formatDate(rs.getDate("date_visit"), "MM/dd/yyyy")
                                    .before(DateUtil.addMonth(DateUtil.formatDateStringToDate(reportingDateBegin, "yyyy-MM-dd", "MM/dd/yyyy"), -6)) ? true : false;
                });
            }
        } catch (Exception exception) {
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }
        return exclude[0];
    }

    private void executeUpdate(String query) {
        try {
            preparedStatement = jdbcUtil.getStatement(query);
            preparedStatement.executeUpdate();
        } catch (Exception exception) {
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }
    }

}
