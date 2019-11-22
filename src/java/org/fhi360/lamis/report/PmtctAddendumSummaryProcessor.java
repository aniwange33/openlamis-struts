/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.report;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.Constants;
import org.fhi360.lamis.utility.DateUtil;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author user10
 */
public class PmtctAddendumSummaryProcessor {

    private int reportingMonth;
    private int reportingYear;
    private String reportingDateBegin;
    private String reportingDateEnd;

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private HashMap parameterMap;
    private long facilityId;
    //private static final Log log = LogFactory.getLog(ArtSummaryProcessor.class);

    private int ageaf1, ageaf2, ageaf3, ageaf4, ageaf5, ageaf6, ageaf7, ageaf8, ageaf9, ageaf10;
    private int agelf1, agelf2, agelf3, agelf4, agelf5, agelf6, agelf7, agelf8, agelf9, agelf10;
    private int agepf1, agepf2, agepf3, agepf4, agepf5, agepf6, agepf7, agepf8, agepf9, agepf10;

    public PmtctAddendumSummaryProcessor() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    public HashMap process() {

        parameterMap = new HashMap();
        reportingMonth = DateUtil.getMonth(request.getParameter("reportingMonth"));
        reportingYear = Integer.parseInt(request.getParameter("reportingYear"));
        reportingDateBegin = dateFormat.format(DateUtil.getFirstDateOfMonth(reportingYear, reportingMonth));
        reportingDateEnd = dateFormat.format(DateUtil.getLastDateOfMonth(reportingYear, reportingMonth));
        facilityId = (Long) session.getAttribute("facilityId");

        try {
            System.out.println("Computing PMTCT 1.....");
            //PMTCT 1
            //Number of New ANC Clients...
            initVariables();

            query = "SELECT DISTINCT patient.patient_id, DATEDIFF(YEAR, patient.date_birth, '" + reportingDateBegin + "') AS age FROM anc JOIN patient  ON anc.patient_id = patient.patient_id WHERE anc.facility_id = " + facilityId + " "
                    + "AND anc.date_visit >= '" + reportingDateBegin + "' AND anc.date_visit <= '" + reportingDateEnd + "'";

            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    int age = resultSet.getInt("age");
                    disaggregate(age, Constants.PMTCTEntryPoint.ANC);
                }
                return null;
            });

            //Populate the report parameter map with values computed for PMTCT 1
            parameterMap.put("pmtct1aa", Integer.toString(ageaf1));
            parameterMap.put("pmtct1at", Integer.toString(ageaf1));

            parameterMap.put("pmtct1ba", Integer.toString(ageaf2));
            parameterMap.put("pmtct1bt", Integer.toString(ageaf2));

            parameterMap.put("pmtct1ca", Integer.toString(ageaf3));
            parameterMap.put("pmtct1ct", Integer.toString(ageaf3));

            parameterMap.put("pmtct1da", Integer.toString(ageaf4));
            parameterMap.put("pmtct1dt", Integer.toString(ageaf4));

            parameterMap.put("pmtct1ea", Integer.toString(ageaf5));
            parameterMap.put("pmtct1et", Integer.toString(ageaf5));

            parameterMap.put("pmtct1fa", Integer.toString(ageaf6));
            parameterMap.put("pmtct1ft", Integer.toString(ageaf6));

            parameterMap.put("pmtct1ga", Integer.toString(ageaf7));
            parameterMap.put("pmtct1gt", Integer.toString(ageaf7));

            parameterMap.put("pmtct1ha", Integer.toString(ageaf8));
            parameterMap.put("pmtct1ht", Integer.toString(ageaf8));

            parameterMap.put("pmtct1ia", Integer.toString(ageaf9));
            parameterMap.put("pmtct1it", Integer.toString(ageaf9));

            parameterMap.put("pmtct1ja", Integer.toString(ageaf10));
            parameterMap.put("pmtct1jt", Integer.toString(ageaf10));

            System.out.println("Computing PMTCT 2.....");
            //PMTCT 2
            //No. of pregnant women with previously known HIV +ve infection

            //ANC
            initVariables();
            query = "SELECT patient.patient_id, DATEDIFF(YEAR, patient.date_birth, '" + reportingDateBegin + "') AS age FROM anc JOIN patient ON anc.patient_id = patient.patient_id WHERE anc.facility_id = " + facilityId + " "
                    + "AND anc.date_visit >= '" + reportingDateBegin + "' AND anc.date_visit <= '" + reportingDateEnd + "' "
                    + "AND anc.time_hiv_diagnosis = '" + Constants.TimeDiagnosis.PREVIOUS_ANC + "'";
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    int age = resultSet.getInt("age");
                    disaggregate(age, Constants.PMTCTEntryPoint.ANC);
                }
                return null;
            });

            //LABOUR
            query = "SELECT patient.patient_id, DATEDIFF(YEAR, patient.date_birth, '" + reportingDateBegin + "') AS age FROM delivery JOIN patient ON delivery.patient_id = patient.patient_id WHERE delivery.facility_id = " + facilityId + " "
                    + "AND delivery.date_delivery >= '" + reportingDateBegin + "' AND delivery.date_delivery <= '" + reportingDateEnd + "' "
                    + "AND delivery.time_hiv_diagnosis = '" + Constants.TimeDiagnosis.PREVIOUS_LD + "'";
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    int age = resultSet.getInt("age");
                    disaggregate(age, Constants.PMTCTEntryPoint.LABOUR);
                }
                return null;
            });
            //PP
            query = "SELECT patient.patient_id, DATEDIFF(YEAR, patient.date_birth, '" + reportingDateBegin + "') AS age FROM maternalfollowup JOIN patient ON maternalfollowup.patient_id = patient.patient_id WHERE maternalfollowup.facility_id = " + facilityId + " "
                    + "AND maternalfollowup.date_visit >= '" + reportingDateBegin + "' AND maternalfollowup.date_visit <= '" + reportingDateEnd + "' "
                    + "AND maternalfollowup.time_hiv_diagnosis = '" + Constants.TimeDiagnosis.PREVIOUS_PP_LESS + "'";
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    int age = resultSet.getInt("age");
                    disaggregate(age, Constants.PMTCTEntryPoint.PP);
                }
                return null;
            });

            //Populate the report parameter map with values computed for PMTCT 2
            parameterMap.put("pmtct2aa", Integer.toString(ageaf1));
            parameterMap.put("pmtct2al", Integer.toString(agelf1));
            parameterMap.put("pmtct2ap", Integer.toString(agepf1));
            parameterMap.put("pmtct2at", Integer.toString(ageaf1 + agelf1 + agepf1));

            parameterMap.put("pmtct2ba", Integer.toString(ageaf2));
            parameterMap.put("pmtct2bl", Integer.toString(agelf2));
            parameterMap.put("pmtct2bp", Integer.toString(agepf2));
            parameterMap.put("pmtct2bt", Integer.toString(ageaf2 + agelf2 + agepf2));

            parameterMap.put("pmtct2ca", Integer.toString(ageaf3));
            parameterMap.put("pmtct2cl", Integer.toString(agelf3));
            parameterMap.put("pmtct2cp", Integer.toString(agepf3));
            parameterMap.put("pmtct2ct", Integer.toString(ageaf3 + agelf3 + agepf3));

            parameterMap.put("pmtct2da", Integer.toString(ageaf4));
            parameterMap.put("pmtct2dl", Integer.toString(agelf4));
            parameterMap.put("pmtct2dp", Integer.toString(agepf4));
            parameterMap.put("pmtct2dt", Integer.toString(ageaf4 + agelf4 + agepf4));

            parameterMap.put("pmtct2ea", Integer.toString(ageaf5));
            parameterMap.put("pmtct2el", Integer.toString(agelf5));
            parameterMap.put("pmtct2ep", Integer.toString(agepf5));
            parameterMap.put("pmtct2et", Integer.toString(ageaf5 + agelf5 + agepf5));

            parameterMap.put("pmtct2fa", Integer.toString(ageaf6));
            parameterMap.put("pmtct2fl", Integer.toString(agelf6));
            parameterMap.put("pmtct2fp", Integer.toString(agepf6));
            parameterMap.put("pmtct2ft", Integer.toString(ageaf6 + agelf6 + agepf6));

            parameterMap.put("pmtct2ga", Integer.toString(ageaf7));
            parameterMap.put("pmtct2gl", Integer.toString(agelf7));
            parameterMap.put("pmtct2gp", Integer.toString(agepf7));
            parameterMap.put("pmtct2gt", Integer.toString(ageaf7 + agelf7 + agepf7));

            parameterMap.put("pmtct2ha", Integer.toString(ageaf8));
            parameterMap.put("pmtct2hl", Integer.toString(agelf8));
            parameterMap.put("pmtct2hp", Integer.toString(agepf8));
            parameterMap.put("pmtct2ht", Integer.toString(ageaf8 + agelf8 + agepf8));

            parameterMap.put("pmtct2ia", Integer.toString(ageaf9));
            parameterMap.put("pmtct2il", Integer.toString(agelf9));
            parameterMap.put("pmtct2ip", Integer.toString(agepf9));
            parameterMap.put("pmtct2it", Integer.toString(ageaf9 + agelf9 + agepf9));

            parameterMap.put("pmtct2ja", Integer.toString(ageaf10));
            parameterMap.put("pmtct2jl", Integer.toString(agelf10));
            parameterMap.put("pmtct2jp", Integer.toString(agepf10));
            parameterMap.put("pmtct2jt", Integer.toString(ageaf10 + agelf10 + agepf10));

            System.out.println("Computing PMTCT 3.....");
            //PMTCT 3
            //No. of pregnant woman HIV tested and received results
            initVariables();
            //ANC
//            query = "SELECT patient_id, DATEDIFF(YEAR, date_birth, '" + reportingDateBegin + "') AS age FROM eac WHERE YEAR(date_eac2) = " + reportingYear + " AND MONTH(date_eac2) = " + reportingMonth;
//            preparedStatement = jdbcUtil.getStatement(query);
//            resultSet = preparedStatement.executeQuery();
//            while (resultSet.next()) {
//                int age = resultSet.getInt("age");
//                disaggregate(age, Constants.PMTCTEntryPoint.ANC);
//            }

            //LABOUR
//            query = "SELECT patient_id, gender, DATEDIFF(YEAR, date_birth, '" + reportingDateBegin + "') AS age FROM eac WHERE YEAR(date_eac2) = " + reportingYear + " AND MONTH(date_eac2) = " + reportingMonth;
//            preparedStatement = jdbcUtil.getStatement(query);
//            resultSet = preparedStatement.executeQuery();
//            while (resultSet.next()) {
//                int age = resultSet.getInt("age");
//                disaggregate(age, Constants.PMTCTEntryPoint.LABOUR);
//            }
            //PP
//            query = "SELECT patient_id, gender, DATEDIFF(YEAR, date_birth, '" + reportingDateBegin + "') AS age FROM eac WHERE YEAR(date_eac2) = " + reportingYear + " AND MONTH(date_eac2) = " + reportingMonth;
//            preparedStatement = jdbcUtil.getStatement(query);
//            resultSet = preparedStatement.executeQuery();
//            while (resultSet.next()) {
//                int age = resultSet.getInt("age");
//                disaggregate(age, Constants.PMTCTEntryPoint.PP);
//            }
            //Populate the report parameter map with values computed for PMTCT 3
            parameterMap.put("pmtct3aa", Integer.toString(ageaf1));
            parameterMap.put("pmtct3al", Integer.toString(agelf1));
            parameterMap.put("pmtct3ap", Integer.toString(agepf1));
            parameterMap.put("pmtct3at", Integer.toString(ageaf1 + agelf1 + agepf1));

            parameterMap.put("pmtct3ba", Integer.toString(ageaf2));
            parameterMap.put("pmtct3bl", Integer.toString(agelf2));
            parameterMap.put("pmtct3bp", Integer.toString(agepf2));
            parameterMap.put("pmtct3bt", Integer.toString(ageaf2 + agelf2 + agepf2));

            parameterMap.put("pmtct3ca", Integer.toString(ageaf3));
            parameterMap.put("pmtct3cl", Integer.toString(agelf3));
            parameterMap.put("pmtct3cp", Integer.toString(agepf3));
            parameterMap.put("pmtct3ct", Integer.toString(ageaf3 + agelf3 + agepf3));

            parameterMap.put("pmtct3da", Integer.toString(ageaf4));
            parameterMap.put("pmtct3dl", Integer.toString(agelf4));
            parameterMap.put("pmtct3dp", Integer.toString(agepf4));
            parameterMap.put("pmtct3dt", Integer.toString(ageaf4 + agelf4 + agepf4));

            parameterMap.put("pmtct3ea", Integer.toString(ageaf5));
            parameterMap.put("pmtct3el", Integer.toString(agelf5));
            parameterMap.put("pmtct3ep", Integer.toString(agepf5));
            parameterMap.put("pmtct3et", Integer.toString(ageaf5 + agelf5 + agepf5));

            parameterMap.put("pmtct3fa", Integer.toString(ageaf6));
            parameterMap.put("pmtct3fl", Integer.toString(agelf6));
            parameterMap.put("pmtct3fp", Integer.toString(agepf6));
            parameterMap.put("pmtct3ft", Integer.toString(ageaf6 + agelf6 + agepf6));

            parameterMap.put("pmtct3ga", Integer.toString(ageaf7));
            parameterMap.put("pmtct3gl", Integer.toString(agelf7));
            parameterMap.put("pmtct3gp", Integer.toString(agepf7));
            parameterMap.put("pmtct3gt", Integer.toString(ageaf7 + agelf7 + agepf7));

            parameterMap.put("pmtct3ha", Integer.toString(ageaf8));
            parameterMap.put("pmtct3hl", Integer.toString(agelf8));
            parameterMap.put("pmtct3hp", Integer.toString(agepf8));
            parameterMap.put("pmtct3ht", Integer.toString(ageaf8 + agelf8 + agepf8));

            parameterMap.put("pmtct3ia", Integer.toString(ageaf9));
            parameterMap.put("pmtct3il", Integer.toString(agelf9));
            parameterMap.put("pmtct3ip", Integer.toString(agepf9));
            parameterMap.put("pmtct3it", Integer.toString(ageaf9 + agelf9 + agepf9));

            parameterMap.put("pmtct3ja", Integer.toString(ageaf10));
            parameterMap.put("pmtct3jl", Integer.toString(agelf10));
            parameterMap.put("pmtct3jp", Integer.toString(agepf10));
            parameterMap.put("pmtct3jt", Integer.toString(ageaf10 + agelf10 + agepf10));

            System.out.println("Computing PMTCT 4.....");
            //PMTCT 4
            //No. tested HIV positive
            initVariables();
            query = "SELECT patient.patient_id, DATEDIFF(YEAR, patient.date_birth, '" + reportingDateBegin + "') AS age FROM anc JOIN patient ON anc.patient_id = patient.patient_id WHERE anc.facility_id = " + facilityId + " "
                    + "AND anc.date_visit >= '" + reportingDateBegin + "' AND anc.date_visit <= '" + reportingDateEnd + "'"
                    + "AND anc.time_hiv_diagnosis = '" + Constants.TimeDiagnosis.NEW_ANC + "'";
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    int age = resultSet.getInt("age");
                    disaggregate(age, Constants.PMTCTEntryPoint.ANC);
                }
                return null;
            });

            //LABOUR
            query = "SELECT patient.patient_id, DATEDIFF(YEAR, patient.date_birth, '" + reportingDateBegin + "') AS age FROM delivery JOIN patient ON delivery.patient_id = patient.patient_id WHERE delivery.facility_id = " + facilityId + " "
                    + "AND delivery.date_delivery >= '" + reportingDateBegin + "' AND delivery.date_delivery <= '" + reportingDateEnd + "' AND delivery.time_hiv_diagnosis = '" + Constants.TimeDiagnosis.NEW_LD + "'";
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    int age = resultSet.getInt("age");
                    disaggregate(age, Constants.PMTCTEntryPoint.LABOUR);
                }
                return null;
            });
            //PP
            query = "SELECT patient.patient_id, DATEDIFF(YEAR, patient.date_birth, '" + reportingDateBegin + "') AS age FROM maternalfollowup JOIN patient ON maternalfollowup.patient_id = patient.patient_id WHERE maternalfollowup.facility_id = " + facilityId + " "
                    + "AND maternalfollowup.date_visit >= '" + reportingDateBegin + "' AND maternalfollowup.date_visit <= '" + reportingDateEnd + "' AND maternalfollowup.time_hiv_diagnosis = '" + Constants.TimeDiagnosis.NEW_PP_LESS + "'";
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    int age = resultSet.getInt("age");
                    disaggregate(age, Constants.PMTCTEntryPoint.PP);
                }
                return null;
            });

            //Populate the report parameter map with values computed for PMTCT 4
            parameterMap.put("pmtct4aa", Integer.toString(ageaf1));
            parameterMap.put("pmtct4al", Integer.toString(agelf1));
            parameterMap.put("pmtct4ap", Integer.toString(agepf1));
            parameterMap.put("pmtct4at", Integer.toString(ageaf1 + agelf1 + agepf1));

            parameterMap.put("pmtct4ba", Integer.toString(ageaf2));
            parameterMap.put("pmtct4bl", Integer.toString(agelf2));
            parameterMap.put("pmtct4bp", Integer.toString(agepf2));
            parameterMap.put("pmtct4bt", Integer.toString(ageaf2 + agelf2 + agepf2));

            parameterMap.put("pmtct4ca", Integer.toString(ageaf3));
            parameterMap.put("pmtct4cl", Integer.toString(agelf3));
            parameterMap.put("pmtct4cp", Integer.toString(agepf3));
            parameterMap.put("pmtct4ct", Integer.toString(ageaf3 + agelf3 + agepf3));

            parameterMap.put("pmtct4da", Integer.toString(ageaf4));
            parameterMap.put("pmtct4dl", Integer.toString(agelf4));
            parameterMap.put("pmtct4dp", Integer.toString(agepf4));
            parameterMap.put("pmtct4dt", Integer.toString(ageaf4 + agelf4 + agepf4));

            parameterMap.put("pmtct4ea", Integer.toString(ageaf5));
            parameterMap.put("pmtct4el", Integer.toString(agelf5));
            parameterMap.put("pmtct4ep", Integer.toString(agepf5));
            parameterMap.put("pmtct4et", Integer.toString(ageaf5 + agelf5 + agepf5));

            parameterMap.put("pmtct4fa", Integer.toString(ageaf6));
            parameterMap.put("pmtct4fl", Integer.toString(agelf6));
            parameterMap.put("pmtct4fp", Integer.toString(agepf6));
            parameterMap.put("pmtct4ft", Integer.toString(ageaf6 + agelf6 + agepf6));

            parameterMap.put("pmtct4ga", Integer.toString(ageaf7));
            parameterMap.put("pmtct4gl", Integer.toString(agelf7));
            parameterMap.put("pmtct4gp", Integer.toString(agepf7));
            parameterMap.put("pmtct4gt", Integer.toString(ageaf7 + agelf7 + agepf7));

            parameterMap.put("pmtct4ha", Integer.toString(ageaf8));
            parameterMap.put("pmtct4hl", Integer.toString(agelf8));
            parameterMap.put("pmtct4hp", Integer.toString(agepf8));
            parameterMap.put("pmtct4ht", Integer.toString(ageaf8 + agelf8 + agepf8));

            parameterMap.put("pmtct4ia", Integer.toString(ageaf9));
            parameterMap.put("pmtct4il", Integer.toString(agelf9));
            parameterMap.put("pmtct4ip", Integer.toString(agepf9));
            parameterMap.put("pmtct4it", Integer.toString(ageaf9 + agelf9 + agepf9));

            parameterMap.put("pmtct4ja", Integer.toString(ageaf10));
            parameterMap.put("pmtct4jl", Integer.toString(agelf10));
            parameterMap.put("pmtct4jp", Integer.toString(agepf10));
            parameterMap.put("pmtct4jt", Integer.toString(ageaf10 + agelf10 + agepf10));

            System.out.println("Computing PMTCT 5.....");
            //PMTCT 5
            //Post_ANC 1 - No. of preg woman retested after initial HIV negative test
            initVariables();
//            query = "SELECT DISTINCT patient_id, DATEDIFF(YEAR, date_birth, '" + reportingDateBegin + "') AS age FROM eac WHERE YEAR(date_eac2) = " + reportingYear + " AND MONTH(date_eac2) = " + reportingMonth;
//            preparedStatement = jdbcUtil.getStatement(query);
//            resultSet = preparedStatement.executeQuery();
//            while (resultSet.next()) {
//                int age = resultSet.getInt("age");
//                disaggregate(age, Constants.PMTCTEntryPoint.ANC);
//            }

            //LABOUR
//            query = "SELECT DISTINCT patient_id, gender, DATEDIFF(YEAR, date_birth, '" + reportingDateBegin + "') AS age FROM eac WHERE YEAR(date_eac2) = " + reportingYear + " AND MONTH(date_eac2) = " + reportingMonth;
//            preparedStatement = jdbcUtil.getStatement(query);
//            resultSet = preparedStatement.executeQuery();
//            while (resultSet.next()) {
//                int age = resultSet.getInt("age");
//                disaggregate(age, Constants.PMTCTEntryPoint.LABOUR);
//            }
            //PP
//            query = "SELECT DISTINCT patient_id, gender, DATEDIFF(YEAR, date_birth, '" + reportingDateBegin + "') AS age FROM eac WHERE YEAR(date_eac2) = " + reportingYear + " AND MONTH(date_eac2) = " + reportingMonth;
//            preparedStatement = jdbcUtil.getStatement(query);
//            resultSet = preparedStatement.executeQuery();
//            while (resultSet.next()) {
//                int age = resultSet.getInt("age");
//                disaggregate(age, Constants.PMTCTEntryPoint.PP);
//            }
            //Populate the report parameter map with values computed for PMTCT 5
            parameterMap.put("pmtct5aa", Integer.toString(ageaf1));
            parameterMap.put("pmtct5al", Integer.toString(agelf1));
            parameterMap.put("pmtct5ap", Integer.toString(agepf1));
            parameterMap.put("pmtct5at", Integer.toString(ageaf1 + agelf1 + agepf1));

            parameterMap.put("pmtct5ba", Integer.toString(ageaf2));
            parameterMap.put("pmtct5bl", Integer.toString(agelf2));
            parameterMap.put("pmtct5bp", Integer.toString(agepf2));
            parameterMap.put("pmtct5bt", Integer.toString(ageaf2 + agelf2 + agepf2));

            parameterMap.put("pmtct5ca", Integer.toString(ageaf3));
            parameterMap.put("pmtct5cl", Integer.toString(agelf3));
            parameterMap.put("pmtct5cp", Integer.toString(agepf3));
            parameterMap.put("pmtct5ct", Integer.toString(ageaf3 + agelf3 + agepf3));

            parameterMap.put("pmtct5da", Integer.toString(ageaf4));
            parameterMap.put("pmtct5dl", Integer.toString(agelf4));
            parameterMap.put("pmtct5dp", Integer.toString(agepf4));
            parameterMap.put("pmtct5dt", Integer.toString(ageaf4 + agelf4 + agepf4));

            parameterMap.put("pmtct5ea", Integer.toString(ageaf5));
            parameterMap.put("pmtct5el", Integer.toString(agelf5));
            parameterMap.put("pmtct5ep", Integer.toString(agepf5));
            parameterMap.put("pmtct5et", Integer.toString(ageaf5 + agelf5 + agepf5));

            parameterMap.put("pmtct5fa", Integer.toString(ageaf6));
            parameterMap.put("pmtct5fl", Integer.toString(agelf6));
            parameterMap.put("pmtct5fp", Integer.toString(agepf6));
            parameterMap.put("pmtct5ft", Integer.toString(ageaf6 + agelf6 + agepf6));

            parameterMap.put("pmtct5ga", Integer.toString(ageaf7));
            parameterMap.put("pmtct5gl", Integer.toString(agelf7));
            parameterMap.put("pmtct5gp", Integer.toString(agepf7));
            parameterMap.put("pmtct5gt", Integer.toString(ageaf7 + agelf7 + agepf7));

            parameterMap.put("pmtct5ha", Integer.toString(ageaf8));
            parameterMap.put("pmtct5hl", Integer.toString(agelf8));
            parameterMap.put("pmtct5hp", Integer.toString(agepf8));
            parameterMap.put("pmtct5ht", Integer.toString(ageaf8 + agelf8 + agepf8));

            parameterMap.put("pmtct5ia", Integer.toString(ageaf9));
            parameterMap.put("pmtct5il", Integer.toString(agelf9));
            parameterMap.put("pmtct5ip", Integer.toString(agepf9));
            parameterMap.put("pmtct5it", Integer.toString(ageaf9 + agelf9 + agepf9));

            parameterMap.put("pmtct5ja", Integer.toString(ageaf10));
            parameterMap.put("pmtct5jl", Integer.toString(agelf10));
            parameterMap.put("pmtct5jp", Integer.toString(agepf10));
            parameterMap.put("pmtct5jt", Integer.toString(ageaf10 + agelf10 + agepf10));

            System.out.println("Computing PMTCT 6.....");
            //PMTCT 6
            //Post_ANC 1 POS - No. of preg women retested who seroconverted to HIV positive after initial HIV test
            initVariables();
//            query = "SELECT DISTINCT patient_id, DATEDIFF(YEAR, date_birth, '" + reportingDateBegin + "') AS age FROM eac WHERE YEAR(date_eac2) = " + reportingYear + " AND MONTH(date_eac2) = " + reportingMonth;
//            preparedStatement = jdbcUtil.getStatement(query);
//            resultSet = preparedStatement.executeQuery();
//            while (resultSet.next()) {
//                int age = resultSet.getInt("age");
//                disaggregate(age, Constants.PMTCTEntryPoint.ANC);
//            }

            //LABOUR
//            query = "SELECT DISTINCT patient_id, gender, DATEDIFF(YEAR, date_birth, '" + reportingDateBegin + "') AS age FROM eac WHERE YEAR(date_eac2) = " + reportingYear + " AND MONTH(date_eac2) = " + reportingMonth;
//            preparedStatement = jdbcUtil.getStatement(query);
//            resultSet = preparedStatement.executeQuery();
//            while (resultSet.next()) {
//                int age = resultSet.getInt("age");
//                disaggregate(age, Constants.PMTCTEntryPoint.LABOUR);
//            }
            //PP
//            query = "SELECT DISTINCT patient_id, gender, DATEDIFF(YEAR, date_birth, '" + reportingDateBegin + "') AS age FROM eac WHERE YEAR(date_eac2) = " + reportingYear + " AND MONTH(date_eac2) = " + reportingMonth;
//            preparedStatement = jdbcUtil.getStatement(query);
//            resultSet = preparedStatement.executeQuery();
//            while (resultSet.next()) {
//                int age = resultSet.getInt("age");
//                disaggregate(age, Constants.PMTCTEntryPoint.PP);
//            }
            //Populate the report parameter map with values computed for PMTCT 6
            parameterMap.put("pmtct6aa", Integer.toString(ageaf1));
            parameterMap.put("pmtct6al", Integer.toString(agelf1));
            parameterMap.put("pmtct6ap", Integer.toString(agepf1));
            parameterMap.put("pmtct6at", Integer.toString(ageaf1 + agelf1 + agepf1));

            parameterMap.put("pmtct6ba", Integer.toString(ageaf2));
            parameterMap.put("pmtct6bl", Integer.toString(agelf2));
            parameterMap.put("pmtct6bp", Integer.toString(agepf2));
            parameterMap.put("pmtct6bt", Integer.toString(ageaf2 + agelf2 + agepf2));

            parameterMap.put("pmtct6ca", Integer.toString(ageaf3));
            parameterMap.put("pmtct6cl", Integer.toString(agelf3));
            parameterMap.put("pmtct6cp", Integer.toString(agepf3));
            parameterMap.put("pmtct6ct", Integer.toString(ageaf3 + agelf3 + agepf3));

            parameterMap.put("pmtct6da", Integer.toString(ageaf4));
            parameterMap.put("pmtct6dl", Integer.toString(agelf4));
            parameterMap.put("pmtct6dp", Integer.toString(agepf4));
            parameterMap.put("pmtct6dt", Integer.toString(ageaf4 + agelf4 + agepf4));

            parameterMap.put("pmtct6ea", Integer.toString(ageaf5));
            parameterMap.put("pmtct6el", Integer.toString(agelf5));
            parameterMap.put("pmtct6ep", Integer.toString(agepf5));
            parameterMap.put("pmtct6et", Integer.toString(ageaf5 + agelf5 + agepf5));

            parameterMap.put("pmtct6fa", Integer.toString(ageaf6));
            parameterMap.put("pmtct6fl", Integer.toString(agelf6));
            parameterMap.put("pmtct6fp", Integer.toString(agepf6));
            parameterMap.put("pmtct6ft", Integer.toString(ageaf6 + agelf6 + agepf6));

            parameterMap.put("pmtct6ga", Integer.toString(ageaf7));
            parameterMap.put("pmtct6gl", Integer.toString(agelf7));
            parameterMap.put("pmtct6gp", Integer.toString(agepf7));
            parameterMap.put("pmtct6gt", Integer.toString(ageaf7 + agelf7 + agepf7));

            parameterMap.put("pmtct6ha", Integer.toString(ageaf8));
            parameterMap.put("pmtct6hl", Integer.toString(agelf8));
            parameterMap.put("pmtct6hp", Integer.toString(agepf8));
            parameterMap.put("pmtct6ht", Integer.toString(ageaf8 + agelf8 + agepf8));

            parameterMap.put("pmtct6ia", Integer.toString(ageaf9));
            parameterMap.put("pmtct6il", Integer.toString(agelf9));
            parameterMap.put("pmtct6ip", Integer.toString(agepf9));
            parameterMap.put("pmtct6it", Integer.toString(ageaf9 + agelf9 + agepf9));

            parameterMap.put("pmtct6ja", Integer.toString(ageaf10));
            parameterMap.put("pmtct6jl", Integer.toString(agelf10));
            parameterMap.put("pmtct6jp", Integer.toString(agepf10));
            parameterMap.put("pmtct6jt", Integer.toString(ageaf10 + agelf10 + agepf10));

            System.out.println("Computing PMTCT 7.....");
            //PMTCT 7
            //No. of HIV+ pregnant women already on ART prior to this pregnancy
            initVariables();
            query = "SELECT p.patient_id, DATEDIFF(YEAR, p.date_birth, '" + reportingDateBegin + "') AS age FROM clinic c JOIN anc a ON c.patient_id = a.patient_id JOIN patient p ON a.patient_id = p.patient_id WHERE c.commence = 1 AND c.facility_id = " + facilityId + " "
                    + " AND c.date_visit >= '" + reportingDateBegin + "' AND c.date_visit <= '" + reportingDateEnd + "' "
                    + " AND a.time_hiv_diagnosis = '" + Constants.TimeDiagnosis.PREVIOUS_ANC + "'";
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    int age = resultSet.getInt("age");
                    disaggregate(age, Constants.PMTCTEntryPoint.ANC);
                }
                return null;
            });

            //LABOUR
            query = "SELECT p.patient_id, DATEDIFF(YEAR, p.date_birth, '" + reportingDateBegin + "') AS age FROM clinic c JOIN delivery d ON c.patient_id = d.patient_id JOIN patient p ON d.patient_id = p.patient_id WHERE c.commence = 1 AND d.facility_id = " + facilityId + " "
                    + " AND d.date_delivery >= '" + reportingDateBegin + "' AND d.date_delivery <= '" + reportingDateEnd + "' "
                    + " AND d.time_hiv_diagnosis = '" + Constants.TimeDiagnosis.PREVIOUS_LD + "'";
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    int age = resultSet.getInt("age");
                    disaggregate(age, Constants.PMTCTEntryPoint.LABOUR);
                }
                return null;
            });
            //PP
            query = "SELECT p.patient_id, DATEDIFF(YEAR, p.date_birth, '" + reportingDateBegin + "') AS age FROM clinic c JOIN maternalfollowup m ON c.patient_id = m.patient_id JOIN patient p ON m.patient_id = p.patient_id WHERE c.commence = 1 AND m.facility_id = " + facilityId + " "
                    + " AND m.date_visit >= '" + reportingDateBegin + "' AND m.date_visit <= '" + reportingDateEnd + "' "
                    + " AND (m.time_hiv_diagnosis = '" + Constants.TimeDiagnosis.PREVIOUS_PP_LESS + "' AND "
                    + " m.time_hiv_diagnosis = '" + Constants.TimeDiagnosis.PREVIOUS_PP_GREATER + "')";
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    int age = resultSet.getInt("age");
                    disaggregate(age, Constants.PMTCTEntryPoint.PP);
                }
                return null;
            });

            //Populate the report parameter map with values computed for PMTCT 7
            parameterMap.put("pmtct7aa", Integer.toString(ageaf1));
            parameterMap.put("pmtct7al", Integer.toString(agelf1));
            parameterMap.put("pmtct7ap", Integer.toString(agepf1));
            parameterMap.put("pmtct7at", Integer.toString(ageaf1 + agelf1 + agepf1));

            parameterMap.put("pmtct7ba", Integer.toString(ageaf2));
            parameterMap.put("pmtct7bl", Integer.toString(agelf2));
            parameterMap.put("pmtct7bp", Integer.toString(agepf2));
            parameterMap.put("pmtct7bt", Integer.toString(ageaf2 + agelf2 + agepf2));

            parameterMap.put("pmtct7ca", Integer.toString(ageaf3));
            parameterMap.put("pmtct7cl", Integer.toString(agelf3));
            parameterMap.put("pmtct7cp", Integer.toString(agepf3));
            parameterMap.put("pmtct7ct", Integer.toString(ageaf3 + agelf3 + agepf3));

            parameterMap.put("pmtct7da", Integer.toString(ageaf4));
            parameterMap.put("pmtct7dl", Integer.toString(agelf4));
            parameterMap.put("pmtct7dp", Integer.toString(agepf4));
            parameterMap.put("pmtct7dt", Integer.toString(ageaf4 + agelf4 + agepf4));

            parameterMap.put("pmtct7ea", Integer.toString(ageaf5));
            parameterMap.put("pmtct7el", Integer.toString(agelf5));
            parameterMap.put("pmtct7ep", Integer.toString(agepf5));
            parameterMap.put("pmtct7et", Integer.toString(ageaf5 + agelf5 + agepf5));

            parameterMap.put("pmtct7fa", Integer.toString(ageaf6));
            parameterMap.put("pmtct7fl", Integer.toString(agelf6));
            parameterMap.put("pmtct7fp", Integer.toString(agepf6));
            parameterMap.put("pmtct7ft", Integer.toString(ageaf6 + agelf6 + agepf6));

            parameterMap.put("pmtct7ga", Integer.toString(ageaf7));
            parameterMap.put("pmtct7gl", Integer.toString(agelf7));
            parameterMap.put("pmtct7gp", Integer.toString(agepf7));
            parameterMap.put("pmtct7gt", Integer.toString(ageaf7 + agelf7 + agepf7));

            parameterMap.put("pmtct7ha", Integer.toString(ageaf8));
            parameterMap.put("pmtct7hl", Integer.toString(agelf8));
            parameterMap.put("pmtct7hp", Integer.toString(agepf8));
            parameterMap.put("pmtct7ht", Integer.toString(ageaf8 + agelf8 + agepf8));

            parameterMap.put("pmtct7ia", Integer.toString(ageaf9));
            parameterMap.put("pmtct7il", Integer.toString(agelf9));
            parameterMap.put("pmtct7ip", Integer.toString(agepf9));
            parameterMap.put("pmtct7it", Integer.toString(ageaf9 + agelf9 + agepf9));

            parameterMap.put("pmtct7ja", Integer.toString(ageaf10));
            parameterMap.put("pmtct7jl", Integer.toString(agelf10));
            parameterMap.put("pmtct7jp", Integer.toString(agepf10));
            parameterMap.put("pmtct7jt", Integer.toString(ageaf10 + agelf10 + agepf10));

            System.out.println("Computing PMTCT 8.....");
            //PMTCT 8
            //No. of HIV+ pregnant women newly started on ART during ANC <36wks of pregnancy
            initVariables();
            query = "SELECT p.patient_id, DATEDIFF(YEAR, p.date_birth, '" + reportingDateBegin + "') AS age FROM clinic c JOIN anc a ON c.patient_id = a.patient_id JOIN patient p ON a.patient_id = p.patient_id WHERE c.commence = 1 AND c.facility_id = " + facilityId + " "
                    + " AND c.date_visit >= '" + reportingDateBegin + "' AND c.date_visit <= '" + reportingDateEnd + "' "
                    + " AND a.gestational_age <= '" + 36 + "' AND a.time_hiv_diagnosis = '" + Constants.TimeDiagnosis.NEW_ANC + "'";
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    int age = resultSet.getInt("age");
                    disaggregate(age, Constants.PMTCTEntryPoint.ANC);
                }
                return null;
            });

            //Populate the report parameter map with values computed for PMTCT 8
            parameterMap.put("pmtct8aa", Integer.toString(ageaf1));
            parameterMap.put("pmtct8at", Integer.toString(ageaf1));

            parameterMap.put("pmtct8ba", Integer.toString(ageaf2));
            parameterMap.put("pmtct8bt", Integer.toString(ageaf2));

            parameterMap.put("pmtct8ca", Integer.toString(ageaf3));
            parameterMap.put("pmtct8ct", Integer.toString(ageaf3));

            parameterMap.put("pmtct8da", Integer.toString(ageaf4));
            parameterMap.put("pmtct8dt", Integer.toString(ageaf4));

            parameterMap.put("pmtct8ea", Integer.toString(ageaf5));
            parameterMap.put("pmtct8et", Integer.toString(ageaf5));

            parameterMap.put("pmtct8fa", Integer.toString(ageaf6));
            parameterMap.put("pmtct8ft", Integer.toString(ageaf6));

            parameterMap.put("pmtct8ga", Integer.toString(ageaf7));
            parameterMap.put("pmtct8gt", Integer.toString(ageaf7));

            parameterMap.put("pmtct8ha", Integer.toString(ageaf8));
            parameterMap.put("pmtct8ht", Integer.toString(ageaf8));

            parameterMap.put("pmtct8ia", Integer.toString(ageaf9));
            parameterMap.put("pmtct8it", Integer.toString(ageaf9));

            parameterMap.put("pmtct8ja", Integer.toString(ageaf10));
            parameterMap.put("pmtct8jt", Integer.toString(ageaf10));

            System.out.println("Computing PMTCT 9.....");
            //PMTCT 9
            //No. of HIV+ pregnant women newly started on ART during ANC >36wks of pregnancy
            initVariables();
            query = "SELECT p.patient_id, DATEDIFF(YEAR, p.date_birth, '" + reportingDateBegin + "') AS age FROM clinic c JOIN anc a ON c.patient_id = a.patient_id JOIN patient p ON a.patient_id = p.patient_id WHERE c.commence = 1 AND c.facility_id = " + facilityId + " "
                    + " AND c.date_visit >= '" + reportingDateBegin + "' AND c.date_visit <= '" + reportingDateEnd + "' "
                    + " AND a.gestational_age > '" + 36 + "' AND a.time_hiv_diagnosis = '" + Constants.TimeDiagnosis.NEW_ANC + "'";
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    int age = resultSet.getInt("age");
                    disaggregate(age, Constants.PMTCTEntryPoint.ANC);
                }
                return null;
            });

            //Populate the report parameter map with values computed for PMTCT 9
            parameterMap.put("pmtct9aa", Integer.toString(ageaf1));
            parameterMap.put("pmtct9at", Integer.toString(ageaf1));

            parameterMap.put("pmtct9ba", Integer.toString(ageaf2));
            parameterMap.put("pmtct9bt", Integer.toString(ageaf2));

            parameterMap.put("pmtct9ca", Integer.toString(ageaf3));
            parameterMap.put("pmtct9ct", Integer.toString(ageaf3));

            parameterMap.put("pmtct9da", Integer.toString(ageaf4));
            parameterMap.put("pmtct9dt", Integer.toString(ageaf4));

            parameterMap.put("pmtct9ea", Integer.toString(ageaf5));
            parameterMap.put("pmtct9et", Integer.toString(ageaf5));

            parameterMap.put("pmtct9fa", Integer.toString(ageaf6));
            parameterMap.put("pmtct9ft", Integer.toString(ageaf6));

            parameterMap.put("pmtct9ga", Integer.toString(ageaf7));
            parameterMap.put("pmtct9gt", Integer.toString(ageaf7));

            parameterMap.put("pmtct9ha", Integer.toString(ageaf8));
            parameterMap.put("pmtct9ht", Integer.toString(ageaf8));

            parameterMap.put("pmtct9ia", Integer.toString(ageaf9));
            parameterMap.put("pmtct9it", Integer.toString(ageaf9));

            parameterMap.put("pmtct9ja", Integer.toString(ageaf10));
            parameterMap.put("pmtct9jt", Integer.toString(ageaf10));

            System.out.println("Adding headers.....");
            //Include reproting period & facility details into report header 
            parameterMap.put("reportingMonth", request.getParameter("reportingMonth"));
            parameterMap.put("reportingYear", request.getParameter("reportingYear"));

            query = "SELECT DISTINCT facility.name, facility.address1, facility.address2, facility.phone1, facility.phone2, facility.email, lga.name AS lga, state.name AS state FROM facility JOIN lga ON facility.lga_id = lga.lga_id JOIN state ON facility.state_id = state.state_id WHERE facility_id = " + facilityId;
            jdbcTemplate.query(query, rs -> {
                while (rs.next()) {
                    parameterMap.put("facilityName", rs.getString("name"));
                    parameterMap.put("facilityType", "");
                    parameterMap.put("lga", rs.getString("lga"));
                    parameterMap.put("state", rs.getString("state"));
                    parameterMap.put("level", "");
                }
                return null;
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return parameterMap;
    }

    private void disaggregate(int age, String category) {
        switch (category) {
            case "ANC":
                if (age < 10) {
                    ageaf1++;
                } else {
                    if (age >= 10 && age <= 14) {
                        ageaf2++;
                    } else {
                        if (age >= 15 && age <= 19) {
                            ageaf3++;
                        } else {
                            if (age >= 20 && age <= 24) {
                                ageaf4++;
                            } else {
                                if (age >= 25 && age <= 29) {
                                    ageaf5++;
                                } else {
                                    if (age >= 30 && age <= 34) {
                                        ageaf6++;
                                    } else {
                                        if (age >= 35 && age <= 39) {
                                            ageaf7++;
                                        } else {
                                            if (age >= 40 && age <= 44) {
                                                ageaf8++;
                                            } else {
                                                if (age >= 45 && age <= 49) {
                                                    ageaf9++;
                                                } else {
                                                    if (age >= 50) {
                                                        ageaf10++;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            case "LABOUR":
                if (age < 10) {
                    agelf1++;
                } else {
                    if (age >= 10 && age <= 14) {
                        agelf2++;
                    } else {
                        if (age >= 15 && age <= 19) {
                            agelf3++;
                        } else {
                            if (age >= 20 && age <= 24) {
                                agelf4++;
                            } else {
                                if (age >= 25 && age <= 29) {
                                    agelf5++;
                                } else {
                                    if (age >= 30 && age <= 34) {
                                        agelf6++;
                                    } else {
                                        if (age >= 35 && age <= 39) {
                                            agelf7++;
                                        } else {
                                            if (age >= 40 && age <= 44) {
                                                agelf8++;
                                            } else {
                                                if (age >= 45 && age <= 49) {
                                                    agelf9++;
                                                } else {
                                                    if (age >= 50) {
                                                        agelf10++;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                break;

            case "PP":
                if (age < 10) {
                    agepf1++;
                } else {
                    if (age >= 10 && age <= 14) {
                        agepf2++;
                    } else {
                        if (age >= 15 && age <= 19) {
                            agepf3++;
                        } else {
                            if (age >= 20 && age <= 24) {
                                agepf4++;
                            } else {
                                if (age >= 25 && age <= 29) {
                                    agepf5++;
                                } else {
                                    if (age >= 30 && age <= 34) {
                                        agepf6++;
                                    } else {
                                        if (age >= 35 && age <= 39) {
                                            agepf7++;
                                        } else {
                                            if (age >= 40 && age <= 44) {
                                                agepf8++;
                                            } else {
                                                if (age >= 45 && age <= 49) {
                                                    agepf9++;
                                                } else {
                                                    if (age >= 50) {
                                                        agepf10++;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                break;
        }
    }

    private void executeUpdate(String query) {
        try {
            jdbcTemplate.execute(query);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private boolean found(String query) {
        boolean[] found = {false};
        try {
            jdbcTemplate.query(query, rs -> {
                while (rs.next()) {
                    found[0] = true;
                }
                return null;
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return found[0];
    }

    private void initVariables() {
        ageaf1 = 0;
        ageaf2 = 0;
        ageaf3 = 0;
        ageaf4 = 0;
        ageaf5 = 0;
        ageaf6 = 0;
        ageaf7 = 0;
        ageaf8 = 0;
        ageaf9 = 0;
        ageaf10 = 0;

        agelf1 = 0;
        agelf2 = 0;
        agelf3 = 0;
        agelf4 = 0;
        agelf5 = 0;
        agelf6 = 0;
        agelf7 = 0;
        agelf8 = 0;
        agelf9 = 0;
        agelf10 = 0;

        agepf1 = 0;
        agepf2 = 0;
        agepf3 = 0;
        agepf4 = 0;
        agepf5 = 0;
        agepf6 = 0;
        agepf7 = 0;
        agepf8 = 0;
        agepf9 = 0;
        agepf10 = 0;

    }

}
