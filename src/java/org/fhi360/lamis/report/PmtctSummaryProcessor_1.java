/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.report;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.JDBCUtil;

/**
 *
 * @author user10
 */
public class PmtctSummaryProcessor_1 {
    
    private int reportingMonth;
    private int reportingYear;
    private String reportingDateBegin;
    private String reportingDateEnd;
    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private JDBCUtil jdbcUtil;
    private PreparedStatement preparedStatement;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private ArrayList<Map<String, String>> reportList;
    private HashMap parameterMap;
    private long facilityId;

    public PmtctSummaryProcessor_1() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    public synchronized HashMap getReportParameters() {
        reportList = new ArrayList<Map<String, String>>();
        reportingMonth = DateUtil.getMonth(request.getParameter("reportingMonth"));
        reportingYear = Integer.parseInt(request.getParameter("reportingYear"));
        reportingDateBegin = dateFormat.format(DateUtil.getFirstDateOfMonth(reportingYear, reportingMonth));
        reportingDateEnd = dateFormat.format(DateUtil.getLastDateOfMonth(reportingYear, reportingMonth));
        facilityId = (Long) session.getAttribute("facilityId");
        parameterMap = new HashMap();
        int total = 0;
        int count = 0;

        try {
            jdbcUtil = new JDBCUtil();
            //Number of  new ANC Clients
            query = "SELECT COUNT(DISTINCT anc.anc_id) AS count FROM anc WHERE anc.facility_id = " + facilityId + " "
                    + "AND anc.date_visit >= '" + reportingDateBegin + "' AND anc.date_visit <= '" + reportingDateEnd + "'";
            parameterMap.put("I1", Integer.toString(executeQuery(query)));

            //Number of new ANC Clients tested for syphilis
            query = "SELECT COUNT(DISTINCT anc.anc_id) AS count FROM anc JOIN maternalfollowup ON anc.anc_id = maternalfollowup.anc_id "
                    + "WHERE anc.facility_id = " + facilityId + " AND anc.date_visit >= '" + reportingDateBegin + "' AND anc.date_visit <= '" + reportingDateEnd + "' "
                    + "AND maternalfollowup.syphilis_tested = 'Yes'";
            parameterMap.put("I2", Integer.toString(executeQuery(query)));

            //Number of new ANC Clients who tested positive for Syphilis
            query = "SELECT COUNT(DISTINCT anc.anc_id) AS count FROM anc JOIN maternalfollowup ON anc.anc_id = maternalfollowup.anc_id "
                    + "WHERE anc.facility_id = " + facilityId + " AND anc.date_visit >= '" + reportingDateBegin + "' AND anc.date_visit <= '" + reportingDateEnd + "' "
                    + "AND maternalfollowup.syphilis_test_result = 'P - Positive'";
            parameterMap.put("I3", Integer.toString(executeQuery(query)));

            //Number of the ANC Clients treated for Syphilis
            query = "SELECT COUNT(DISTINCT anc.anc_id) AS count FROM anc JOIN maternalfollowup ON anc.anc_id = maternalfollowup.anc_id "
                    + "WHERE anc.facility_id = " + facilityId + " AND anc.date_visit >= '" + reportingDateBegin + "' AND anc.date_visit <= '" + reportingDateEnd + "' "
                    + "AND maternalfollowup.syphilis_treated = 'Yes'";
            parameterMap.put("I4", Integer.toString(executeQuery(query)));

            //No. of pregnant women with previously known HIV +ve infection at ANC
            query = "SELECT COUNT(DISTINCT anc.anc_id) AS count FROM anc WHERE anc.facility_id = " + facilityId + " "
                    + "AND anc.date_visit >= '" + reportingDateBegin + "' AND anc.date_visit <= '" + reportingDateEnd + "' "
                    + "AND time_hiv_diagnosis = 'Previously known'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I5a", Integer.toString(count));

            //No. of pregnant women with previously known HIV +ve infection at Labour
            query = "SELECT COUNT(DISTINCT delivery.delivery_id) AS count FROM delivery WHERE delivery.facility_id = " + facilityId + " "
                    + "AND delivery.date_delivery >= '" + reportingDateBegin + "' AND delivery.date_delivery <= '" + reportingDateEnd + "' "
                    + "AND delivery.time_hiv_diagnosis = 'Previously known' AND delivery.anc_id = 0 AND delivery.screen_post_partum = 0";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I5b", Integer.toString(count));

            //No. of pregnant women with previously known HIV +ve infection at Post partum
            query = "SELECT COUNT(DISTINCT delivery.delivery_id) AS count FROM delivery WHERE delivery.facility_id = " + facilityId + " "
                    + "AND delivery.date_delivery >= '" + reportingDateBegin + "' AND delivery.date_delivery <= '" + reportingDateEnd + "' "
                    + "AND delivery.time_hiv_diagnosis = 'Previously known' AND delivery.anc_id = 0 AND delivery.screen_post_partum = 1";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I5c", Integer.toString(count));

            //No. of pregnant women with previously known HIV +ve infection total
            parameterMap.put("I5d", Integer.toString(total));
            total = 0;

            //No. of Pregnant women tested for HIV at ANC (Proxy)
            query = "SELECT COUNT(DISTINCT anc.anc_id) AS count FROM anc WHERE anc.facility_id = " + facilityId + " "
                    + "AND anc.date_visit >= '" + reportingDateBegin + "' AND anc.date_visit <= '" + reportingDateEnd + "'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I6a", Integer.toString(count));

            //No. of Pregnant women tested for HIV at Labour (Proxy)
            query = "SELECT COUNT(DISTINCT delivery.delivery_id) AS count FROM delivery WHERE delivery.facility_id = " + facilityId + " "
                    + "AND delivery.date_delivery >= '" + reportingDateBegin + "' AND delivery.date_delivery <= '" + reportingDateEnd + "' "
                    + "AND delivery.screen_post_partum = 0";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I6b", Integer.toString(count));

            //No. of Pregnant women tested for HIV at Post partum (Proxy)
            query = "SELECT COUNT(DISTINCT delivery.delivery_id) AS count FROM delivery WHERE delivery.facility_id = " + facilityId + " "
                    + "AND delivery.date_delivery >= '" + reportingDateBegin + "' AND delivery.date_delivery <= '" + reportingDateEnd + "' "
                    + "AND delivery.screen_post_partum = 1";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I6c", Integer.toString(count));

            //No. of Pregnant women tested for HIV total (Proxy)
            parameterMap.put("I6d", Integer.toString(total));
            total = 0;

            //create a temporary table of positives at the end of the review period
            executeUpdate("DROP TABLE IF EXISTS positives");
            query = "CREATE TEMPORARY TABLE positives AS SELECT DISTINCT patient_id, MAX(date_current_status) FROM statushistory "
                    + "WHERE facility_id = " + facilityId + " AND date_current_status <= '" + reportingDateEnd + "' "
                    + "AND current_status IN ('HIV+ non ART', 'ART Transfer In', 'Pre-ART Transfer In', 'ART Start') "
                    + "GROUP BY patient_id";
            executeUpdate(query);

            //No. tested HIV positive at ANC
            query = "SELECT COUNT(DISTINCT anc.anc_id) AS count FROM anc JOIN positives ON anc.patient_id = positives.patient_id WHERE anc.facility_id = " + facilityId + " "
                    + "AND anc.date_visit >= '" + reportingDateBegin + "' AND anc.date_visit <= '" + reportingDateEnd + "'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I7a", Integer.toString(count));

            //No. tested HIV positive at Labour
            query = "SELECT COUNT(DISTINCT delivery.delivery_id) AS count FROM delivery JOIN positives ON delivery.patient_id = positives.patient_id WHERE delivery.facility_id = " + facilityId + " "
                    + "AND delivery.date_delivery >= '" + reportingDateBegin + "' AND delivery.date_delivery <= '" + reportingDateEnd + "' AND delivery.screen_post_partum = 0";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I7b", Integer.toString(count));

            //No. tested HIV positive at Post partum
            query = "SELECT COUNT(DISTINCT delivery.delivery_id) AS count FROM delivery JOIN positives ON delivery.patient_id = positives.patient_id WHERE delivery.facility_id = " + facilityId + " "
                    + "AND delivery.date_delivery >= '" + reportingDateBegin + "' AND delivery.date_delivery <= '" + reportingDateEnd + "' AND delivery.screen_post_partum = 1";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I7c", Integer.toString(count));

            //No. tested HIV positive total
            parameterMap.put("I7d", Integer.toString(total));
            total = 0;

            //No. of  pregnant women HIV tested, counseled and received results at ANC (Proxy)
            query = "SELECT COUNT(DISTINCT anc.anc_id) AS count FROM anc WHERE anc.facility_id = " + facilityId + " "
                    + "AND anc.date_visit >= '" + reportingDateBegin + "' AND anc.date_visit <= '" + reportingDateEnd + "'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I8a", Integer.toString(count));

            //No. of  pregnant women HIV tested, counseled and received results at Labour (Proxy)
            query = "SELECT COUNT(DISTINCT delivery.delivery_id) AS count FROM delivery WHERE delivery.facility_id = " + facilityId + " "
                    + "AND delivery.date_delivery >= '" + reportingDateBegin + "' AND delivery.date_delivery <= '" + reportingDateEnd + "' AND delivery.screen_post_partum = 0";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I8b", Integer.toString(count));

            //No. of  pregnant women HIV tested, counseled and received results at Post partum (Proxy)
            query = "SELECT COUNT(DISTINCT delivery.delivery_id) AS count FROM delivery WHERE delivery.facility_id = " + facilityId + " "
                    + "AND delivery.date_delivery >= '" + reportingDateBegin + "' AND delivery.date_delivery <= '" + reportingDateEnd + "' AND delivery.screen_post_partum = 1";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I8c", Integer.toString(count));

            //No. of  pregnant women HIV tested, counseled and received results total (Proxy)
            parameterMap.put("I8d", Integer.toString(total));
            total = 0;

            //No. of HIV positive pregnant women who received family planning counselling
            query = "SELECT COUNT(DISTINCT maternalfollowup.patient_id) AS count FROM maternalfollowup JOIN positives ON maternalfollowup.patient_id = positives.patient_id WHERE maternalfollowup.facility_id = " + facilityId + " "
                    + "AND maternalfollowup.date_visit >= '" + reportingDateBegin + "' AND maternalfollowup.date_visit <= '" + reportingDateEnd + "' AND maternalfollowup.counsel_family_planning = 1";
            parameterMap.put("I9", Integer.toString(executeQuery(query)));

            //create a temporary table of negatives at the end of the review period
            executeUpdate("DROP TABLE IF EXISTS negatives");
            query = "CREATE TEMPORARY TABLE negatives AS SELECT DISTINCT patient_id, MAX(date_current_status) FROM statushistory "
                    + "WHERE facility_id = " + facilityId + " AND date_current_status <= '" + reportingDateEnd + "' "
                    + "AND current_status = 'HIV negative' "
                    + "GROUP BY patient_id";
            executeUpdate(query);

            //No. of partners of HIV neg. pregnant women tested HIV negative
            query = "SELECT COUNT(DISTINCT partnerinformation.patient_id) AS count FROM partnerinformation JOIN negatives ON partnerinformation.patient_id = negatives.patient_id WHERE partnerinformation.facility_id = " + facilityId + " "
                    + "AND partnerinformation.date_visit  >= '" + reportingDateBegin + "' AND partnerinformation.date_visit <= '" + reportingDateEnd + "' AND partnerinformation.partner_hiv_status = 'Negative'";
            parameterMap.put("I10", Integer.toString(executeQuery(query)));

            //No. of partners of HIV neg. pregnant women tested HIV positive
            query = "SELECT COUNT(DISTINCT partnerinformation.patient_id) AS count FROM partnerinformation JOIN negatives ON partnerinformation.patient_id = negatives.patient_id WHERE partnerinformation.facility_id = " + facilityId + " "
                    + "AND partnerinformation.date_visit  >= '" + reportingDateBegin + "' AND partnerinformation.date_visit <= '" + reportingDateEnd + "' AND partnerinformation.partner_hiv_status = 'Positive'";
            parameterMap.put("I11", Integer.toString(executeQuery(query)));

            //No. of partners of HIV pos. pregnant women  tested HIV negative
            query = "SELECT COUNT(DISTINCT partnerinformation.patient_id) AS count FROM partnerinformation JOIN positives ON partnerinformation.patient_id = positives.patient_id WHERE partnerinformation.facility_id = " + facilityId + " "
                    + "AND partnerinformation.date_visit  >= '" + reportingDateBegin + "' AND partnerinformation.date_visit <= '" + reportingDateEnd + "' AND partnerinformation.partner_hiv_status = 'Negative'";
            parameterMap.put("I12", Integer.toString(executeQuery(query)));

            //No. of partners of HIV pos. pregnant women tested HIV positive
            query = "SELECT COUNT(DISTINCT partnerinformation.patient_id) AS count FROM partnerinformation JOIN negatives ON partnerinformation.patient_id = negatives.patient_id WHERE partnerinformation.facility_id = " + facilityId + " "
                    + "AND partnerinformation.date_visit  >= '" + reportingDateBegin + "' AND partnerinformation.date_visit <= '" + reportingDateEnd + "' AND partnerinformation.partner_hiv_status = 'Positive'";
            parameterMap.put("I13", Integer.toString(executeQuery(query)));

            //No. of HIV+ pregnant women assessed for ART eligibility by Clinical Stage at ANC
            query = "SELECT COUNT(DISTINCT anc.anc_id) AS count FROM anc JOIN positives ON anc.patient_id = positives.patient_id WHERE anc.facility_id = " + facilityId + " "
                    + "AND anc.date_visit >= '" + reportingDateBegin + "' AND anc.date_visit <= '" + reportingDateEnd + "' "
                    + "AND anc.arv_regimen_current = 'ART' AND anc.clinic_stage IN ('Stage I', 'Stage II', 'Stage III', 'Stage IV') "
                    + "AND (anc.cd4 IS NULL OR anc.cd4 = 0)";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I14a", Integer.toString(count));

            //No. of HIV+ pregnant women assessed for ART eligibility by Clinical Stage at Post partum
            query = "SELECT COUNT(DISTINCT delivery.delivery_id) AS count FROM delivery JOIN positives ON delivery.patient_id = positives.patient_id WHERE delivery.facility_id = " + facilityId + " "
                    + "AND delivery.date_delivery >= '" + reportingDateBegin + "' AND delivery.date_delivery <= '" + reportingDateEnd + "' "
                    + "AND delivery.screen_post_partum = 1 AND delivery.arv_regimen_current = 'ART' "
                    + "AND delivery.clinic_stage IN ('Stage I', 'Stage II', 'Stage III', 'Stage IV') AND (delivery.cd4 IS NULL OR delivery.cd4 = 0)";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I14b", Integer.toString(count));

            //No. of HIV+ pregnant women assessed for ART eligibility by Clinical Stage total
            parameterMap.put("I14c", Integer.toString(total));
            total = 0;

            //No. of HIV+ pregnant women assessed for ART eligibility by CD4 at ANC
            query = "SELECT COUNT(DISTINCT anc.anc_id) AS count FROM anc JOIN positives ON anc.patient_id = positives.patient_id WHERE anc.facility_id = " + facilityId + " "
                    + "AND anc.date_visit >= '" + reportingDateBegin + "' AND anc.date_visit <= '" + reportingDateEnd + "' "
                    + "AND anc.arv_regimen_current = 'ART' AND (anc.cd4 IS NOT NULL OR anc.cd4 != 0) "
                    + "AND anc.clinic_stage NOT IN ('Stage I', 'Stage II', 'Stage III', 'Stage IV')";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I15a", Integer.toString(count));

            //No. of HIV+ pregnant women assessed for ART eligibility by CD4 at Post partum
            query = "SELECT COUNT(DISTINCT delivery.delivery_id) AS count FROM delivery JOIN positives ON delivery.patient_id = positives.patient_id WHERE delivery.facility_id = " + facilityId + " "
                    + "AND delivery.date_delivery >= '" + reportingDateBegin + "' AND date_delivery <= '" + reportingDateEnd + "' "
                    + "AND delivery.screen_post_partum = 1 AND delivery.arv_regimen_current = 'ART' "
                    + "AND (delivery.cd4 IS NOT NULL OR delivery.cd4 != 0) "
                    + "AND delivery.clinic_stage NOT IN ('Stage I', 'Stage II', 'Stage III', 'Stage IV')";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I15b", Integer.toString(count));

            //No. of HIV+ pregnant women assessed for ART eligibility by CD4 total
            parameterMap.put("I15c", Integer.toString(total));
            total = 0;

            //No. of HIV+ pregnant women assessed for ART eligibility by either Clinical Stage or CD4 at ANC		
            query = "SELECT COUNT(DISTINCT anc.anc_id) AS count FROM anc JOIN positives ON anc.patient_id = positives.patient_id WHERE anc.facility_id = " + facilityId + " "
                    + "AND anc.date_visit >= '" + reportingDateBegin + "' AND anc.date_visit <= '" + reportingDateEnd + "' "
                    + "AND anc.arv_regimen_current = 'ART' "
                    + "AND (anc.clinic_stage IN ('Stage I', 'Stage II', 'Stage III', 'Stage IV') OR (anc.cd4 IS NOT NULL OR anc.cd4 != 0))";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I16a", Integer.toString(count));

            //No. of HIV+ pregnant women assessed for ART eligibility by either Clinical Stage or CD4 at Post partum
            query = "SELECT COUNT(DISTINCT delivery.delivery_id) AS count FROM delivery JOIN positives ON delivery.patient_id = positives.patient_id WHERE delivery.facility_id = " + facilityId + " "
                    + "AND delivery.date_delivery >= '" + reportingDateBegin + "' AND delivery.date_delivery <= '" + reportingDateEnd + "' "
                    + "AND delivery.screen_post_partum = 1 AND delivery.arv_regimen_current = 'ART' "
                    + "AND (delivery.clinic_stage IN ('Stage I', 'Stage II', 'Stage III', 'Stage IV') OR (delivery.cd4 IS NOT NULL OR delivery.cd4 != 0))";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I16b", Integer.toString(count));

            //No. of HIV+ pregnant women assessed for ART eligibility by either Clinical Stage or CD4 total
            parameterMap.put("I16c", Integer.toString(total));
            total = 0;

            //(a) No. of HIV+ pregnant women started  on ARV prophylaxis  AZT 
            int total1 = 0;
            query = "SELECT COUNT(DISTINCT anc.anc_id) AS count FROM anc JOIN positives ON anc.patient_id = positives.patient_id WHERE anc.facility_id = " + facilityId + " "
                    + "AND anc.date_visit >= '" + reportingDateBegin + "' AND anc.date_visit <= '" + reportingDateEnd + "' "
                    + "AND anc.arv_regimen_current = 'Prophylaxis (AZT)'";
            count = executeQuery(query);
            total += count;

            query = "SELECT COUNT(DISTINCT delivery.delivery_id) AS count FROM delivery JOIN positives ON delivery.patient_id = positives.patient_id WHERE delivery.facility_id = " + facilityId + " "
                    + "AND delivery.date_delivery >= '" + reportingDateBegin + "' AND delivery.date_delivery <= '" + reportingDateEnd + "' "
                    + "AND (delivery.screen_post_partum = 1 OR delivery.screen_post_partum = 0) AND delivery.arv_regimen_current = 'Prophylaxis (AZT)'";
            count = executeQuery(query);
            total += count;
            total1 += total;

            parameterMap.put("I17", Integer.toString(total));
            total = 0;

            //(b) No. of HIV+ pregnant women started on  ARV prophylaxis Triple regimen
            query = "SELECT COUNT(DISTINCT anc.anc_id) AS count FROM anc JOIN positives ON anc.patient_id = positives.patient_id WHERE anc.facility_id = " + facilityId + " "
                    + "AND anc.date_visit >= '" + reportingDateBegin + "' AND anc.date_visit <= '" + reportingDateEnd + "' "
                    + "AND anc.arv_regimen_current = 'Prophylaxis (Triple)'";
            count = executeQuery(query);
            total += count;

            query = "SELECT COUNT(DISTINCT delivery.delivery_id) AS count FROM delivery JOIN positives ON delivery.patient_id = positives.patient_id WHERE delivery.facility_id = " + facilityId + " "
                    + "AND delivery.date_delivery >= '" + reportingDateBegin + "' AND delivery.date_delivery <= '" + reportingDateEnd + "' "
                    + "AND (delivery.screen_post_partum = 1 OR delivery.screen_post_partum = 0) AND delivery.arv_regimen_past = 'Prophylaxis (Triple)'";
            count = executeQuery(query);
            total += count;
            total1 += total;

            parameterMap.put("I18", Integer.toString(total));
            total = 0;

            //(c) No. of HIV+ pregnant women started on ARV prophylaxis regimen: sdNVP only in labour 
            query = "SELECT COUNT(DISTINCT delivery.delivery_id) AS count FROM delivery JOIN positives ON delivery.patient_id = positives.patient_id WHERE delivery.facility_id = " + facilityId + " "
                    + "AND delivery.date_delivery >= '" + reportingDateBegin + "' AND delivery.date_delivery <= '" + reportingDateEnd + "' "
                    + "AND delivery.screen_post_partum = 0 AND delivery.arv_regimen_current = 'Prophylaxis (sdNVP)'";
            count = executeQuery(query);
            total1 += count;
            parameterMap.put("I19", Integer.toString(count));

            //(d) No. of HIV+ pregnant women received  ARV prophylaxis  sdNVP + (AZT+3TC) in labour
            query = "SELECT COUNT(DISTINCT delivery.delivery_id) AS count FROM delivery JOIN positives ON delivery.patient_id = positives.patient_id WHERE delivery.facility_id = " + facilityId + " "
                    + "AND delivery.date_delivery >= '" + reportingDateBegin + "' AND delivery.date_delivery <= '" + reportingDateEnd + "' "
                    + "AND delivery.screen_post_partum = 0 AND delivery.arv_regimen_current = 'Prophylaxis (sdNVP+(AZT+3TC))'";
            count = executeQuery(query);
            total1 += count;
            parameterMap.put("I20", Integer.toString(count));

            //(e) No. of HIV+ pregnant women on ART for their own health prior to this pregnancy
            query = "SELECT COUNT(DISTINCT anc.anc_id) AS count FROM anc JOIN positives ON anc.patient_id = positives.patient_id WHERE anc.facility_id = " + facilityId + " "
                    + "AND anc.date_visit >= '" + reportingDateBegin + "' AND anc.date_visit <= '" + reportingDateEnd + "' "
                    + "AND anc.arv_regimen_past = 'ART'";
            count = executeQuery(query);
            total += count;

            query = "SELECT COUNT(DISTINCT delivery.delivery_id) AS count FROM delivery JOIN positives ON delivery.patient_id = positives.patient_id WHERE delivery.facility_id = " + facilityId + " "
                    + "AND delivery.date_delivery >= '" + reportingDateBegin + "' AND delivery.date_delivery <= '" + reportingDateEnd + "' "
                    + "AND (delivery.screen_post_partum = 1 OR delivery.screen_post_partum = 0) AND delivery.arv_regimen_past = 'ART'";
            count = executeQuery(query);
            total += count;
            total1 += total;

            parameterMap.put("I21", Integer.toString(total));
            total = 0;

            //(f) No. of HIV+ pregnant women newly intiated on ART for their own health (New)
            query = "SELECT COUNT(DISTINCT anc.anc_id) AS count FROM anc JOIN positives ON anc.patient_id = positives.patient_id WHERE anc.facility_id = " + facilityId + " "
                    + "AND anc.date_visit >= '" + reportingDateBegin + "' AND anc.date_visit <= '" + reportingDateEnd + "' "
                    + "AND anc.arv_regimen_current = 'ART'";
            count = executeQuery(query);
            total += count;

            query = "SELECT COUNT(DISTINCT delivery.delivery_id) AS count FROM delivery JOIN positives ON delivery.patient_id = positives.patient_id WHERE delivery.facility_id = " + facilityId + " "
                    + "AND delivery.date_delivery >= '" + reportingDateBegin + "' AND delivery.date_delivery <= '" + reportingDateEnd + "' "
                    + "AND (delivery.screen_post_partum = 1 OR delivery.screen_post_partum = 0) AND delivery.arv_regimen_current = 'ART'";
            count = executeQuery(query);
            total += count;
            total1 += total;

            parameterMap.put("I22", Integer.toString(total));
            total = 0;

            //(g) Total no. of HIV positive pregnant women who received ARV prophylaxis for PMTCT =  (a) + (b) + (c) +(d) + (e)+ (f)
            parameterMap.put("I23", Integer.toString(total1));
            total1 = 0;

            //Total deliveries at facility (booked and unbooked pregnant women)
            query = "SELECT COUNT(DISTINCT delivery.delivery_id) AS count FROM delivery WHERE delivery.facility_id = " + facilityId + " "
                    + "AND delivery.date_delivery >= '" + reportingDateBegin + "' AND delivery.date_delivery <= '" + reportingDateEnd + "'";
            parameterMap.put("I24", Integer.toString(executeQuery(query)));

            //No. of  HIV+ women not previously booked at ANC but delivered at facility
            query = "SELECT COUNT(DISTINCT delivery.delivery_id) AS count FROM delivery JOIN positives ON delivery.patient_id = positives.patient_id WHERE delivery.facility_id = " + facilityId + " "
                    + "AND delivery.date_delivery >= '" + reportingDateBegin + "' AND delivery.date_delivery <= '" + reportingDateEnd + "' "
                    + "AND delivery.booking_status = 0";
            parameterMap.put("I25", Integer.toString(executeQuery(query)));

            //No. of deliveries by HIV+ women
            query = "SELECT COUNT(DISTINCT delivery.delivery_id) AS count FROM delivery JOIN positives ON delivery.patient_id = positives.patient_id WHERE delivery.facility_id = " + facilityId + " "
                    + "AND delivery.date_delivery >= '" + reportingDateBegin + "' AND delivery.date_delivery <= '" + reportingDateEnd + "'";
            parameterMap.put("I26", Integer.toString(executeQuery(query)));

            //No. of live births by  HIV+ women (Male)
            query = "SELECT COUNT(DISTINCT child.child_id) AS count FROM child JOIN positives ON child.patient_id_mother = positives.patient_id WHERE child.facility_id = " + facilityId + " "
                    + "AND child.date_birth >= '" + reportingDateBegin + "' AND child.date_birth <= '" + reportingDateEnd + "' "
                    + "AND child.status = 'A - Alive' AND child.gender = 'Male'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I27a", Integer.toString(count));

            //No. of live births by  HIV+ women (Female)
            query = "SELECT COUNT(DISTINCT child.child_id) AS count FROM child JOIN positives ON child.patient_id_mother = positives.patient_id WHERE child.facility_id = " + facilityId + " "
                    + "AND child.date_birth >= '" + reportingDateBegin + "' AND child.date_birth <= '" + reportingDateEnd + "' "
                    + "AND child.status = 'A - Alive' AND child.gender = 'Female'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I27b", Integer.toString(count));

            //No. of live births by  HIV+ women total
            parameterMap.put("I27c", Integer.toString(total));
            total = 0;

            //No. of HIV exposed infants who received first dose of NVP (Male)
            query = "SELECT COUNT(DISTINCT childfollowup.child_id) AS count FROM childfollowup JOIN child ON childfollowup.child_id = child.child_id "
                    + "JOIN positives ON child.patient_id_mother = positives.patient_id WHERE child.facility_id = " + facilityId + " "
                    + "AND childfollowup.date_nvp_initiated >= '" + reportingDateBegin + "' AND childfollowup.date_nvp_initiated <= '" + reportingDateEnd + "' "
                    + "AND child.gender = 'Male'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I28a", Integer.toString(count));

            //No. of HIV exposed infants who received first dose of NVP (Female)
            query = "SELECT COUNT(DISTINCT childfollowup.child_id) AS count FROM childfollowup JOIN child ON childfollowup.child_id = child.child_id "
                    + "JOIN positives ON child.patient_id_mother = positives.patient_id WHERE child.facility_id = " + facilityId + " "
                    + "AND childfollowup.date_nvp_initiated >= '" + reportingDateBegin + "' AND childfollowup.date_nvp_initiated <= '" + reportingDateEnd + "' "
                    + "AND child.gender = 'Female'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I28b", Integer.toString(count));

            //No. of HIV exposed infants who received first dose of NVP total
            parameterMap.put("I28c", Integer.toString(total));
            total = 0;

            //No. of HIV exposed infants who are breast feeding and receiving ARV prophylaxis for PMTCT (New) (Male)
            query = "SELECT COUNT(DISTINCT childfollowup.child_id) AS count FROM childfollowup JOIN child ON childfollowup.child_id = child.child_id "
                    + "JOIN positives ON child.patient_id_mother = positives.patient_id WHERE child.facility_id = " + facilityId + " "
                    + "AND childfollowup.date_visit >= '" + reportingDateBegin + "' AND childfollowup.date_visit <= '" + reportingDateEnd + "' "
                    + "AND child.date_birth >= '" + reportingDateBegin + "' AND child.date_birth <= '" + reportingDateEnd + "' "
                    + "AND childfollowup.feeding = '1 - Exclusive Breast Feeding (EBF)' AND childfollowup.arv = 'Yes' AND child.gender = 'Male'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I29a", Integer.toString(count));

            //No. of HIV exposed infants who are breast feeding and receiving ARV prophylaxis for PMTCT (New) (Female)
            query = "SELECT COUNT(DISTINCT childfollowup.child_id) AS count FROM childfollowup JOIN child ON childfollowup.child_id = child.child_id "
                    + "JOIN positives ON child.patient_id_mother = positives.patient_id WHERE child.facility_id = " + facilityId + " "
                    + "AND childfollowup.date_visit >= '" + reportingDateBegin + "' AND childfollowup.date_visit <= '" + reportingDateEnd + "' "
                    + "AND child.date_birth >= '" + reportingDateBegin + "' AND child.date_birth <= '" + reportingDateEnd + "' "
                    + "AND childfollowup.feeding = '1 - Exclusive Breast Feeding (EBF)' AND childfollowup.arv = 'Yes' AND child.gender = 'Female'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I29b", Integer.toString(count));

            //No. of HIV exposed infants who are breast feeding and receiving ARV prophylaxis for PMTCT (New) total
            parameterMap.put("I29c", Integer.toString(total));
            total = 0;

            //Total no. of infants born to HIV infected women started on CTX prophylaxis (Male)
            query = "SELECT COUNT(DISTINCT childfollowup.child_id) AS count FROM childfollowup JOIN child ON childfollowup.child_id = child.child_id "
                    + "JOIN positives ON child.patient_id_mother = positives.patient_id WHERE child.facility_id = " + facilityId + " "
                    + "AND childfollowup.date_cotrim_initiated >= '" + reportingDateBegin + "' AND childfollowup.date_cotrim_initiated <= '" + reportingDateEnd + "' AND child.gender = 'Male'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I30a", Integer.toString(count));

            //Total no. of infants born to HIV infected women started on CTX prophylaxis (Female)
            query = "SELECT COUNT(DISTINCT childfollowup.child_id) AS count FROM childfollowup JOIN child ON childfollowup.child_id = child.child_id "
                    + "JOIN positives ON child.patient_id_mother = positives.patient_id WHERE child.facility_id = " + facilityId + " "
                    + "AND childfollowup.date_cotrim_initiated >= '" + reportingDateBegin + "' AND childfollowup.date_cotrim_initiated <= '" + reportingDateEnd + "' AND child.gender = 'Female'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I30b", Integer.toString(count));

            //Total no. of infants born to HIV infected women started on CTX prophylaxis total
            parameterMap.put("I30c", Integer.toString(total));
            total = 0;

            //No. of infants born to HIV infected women started on CTX prophylaxis within two months of birth (subset of 30 above) (Male)
            query = "SELECT COUNT(DISTINCT childfollowup.child_id) AS count FROM childfollowup JOIN child ON childfollowup.child_id = child.child_id "
                    + "JOIN positives ON child.patient_id_mother = positives.patient_id WHERE child.facility_id = " + facilityId + " "
                    + "AND childfollowup.date_cotrim_initiated >= '" + reportingDateBegin + "' AND childfollowup.date_cotrim_initiated <= '" + reportingDateEnd + "' "
                    + "AND (TIMESTAMPDIFF(DAY, child.date_birth, '" + reportingDateBegin + "') / 30) <= 2 AND child.gender = 'Male'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I31a", Integer.toString(count));

            //No. of infants born to HIV infected women started on CTX prophylaxis within two months of birth (subset of 30 above) (Female)
            query = "SELECT COUNT(DISTINCT childfollowup.child_id) AS count FROM childfollowup JOIN child ON childfollowup.child_id = child.child_id "
                    + "JOIN positives ON child.patient_id_mother = positives.patient_id WHERE child.facility_id = " + facilityId + " "
                    + "AND childfollowup.date_cotrim_initiated >= '" + reportingDateBegin + "' AND childfollowup.date_cotrim_initiated <= '" + reportingDateEnd + "' "
                    + "AND (TIMESTAMPDIFF(DAY, child.date_birth, '" + reportingDateBegin + "') / 30) <= 2 AND child.gender = 'Female'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I31b", Integer.toString(count));

            //No. of infants born to HIV infected women started on CTX prophylaxis within two months of birth (subset of 30 above) total
            parameterMap.put("I31c", Integer.toString(total));
            total = 0;

            //No. of  infants born to HIV infected women who received an HIV test at age < 9 months  (Rapid test) (Male)
            query = "SELECT COUNT(DISTINCT childfollowup.child_id) AS count FROM childfollowup JOIN child ON childfollowup.child_id = child.child_id "
                    + "JOIN positives ON child.patient_id_mother = positives.patient_id WHERE child.facility_id = " + facilityId + " "
                    + "AND childfollowup.date_visit >= '" + reportingDateBegin + "' AND childfollowup.date_visit <= '" + reportingDateEnd + "' "
                    + "AND childfollowup.rapid_test = 'Yes' AND childfollowup.rapid_test_result IN ('Positive', 'Negative') "
                    + "AND (TIMESTAMPDIFF(DAY, child.date_birth, '" + reportingDateBegin + "') / 30) < 9 AND child.gender = 'Male'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I32a", Integer.toString(count));

            //No. of  infants born to HIV infected women who received an HIV test at age < 9 months  (Rapid test) (Female)
            query = "SELECT COUNT(DISTINCT childfollowup.child_id) AS count FROM childfollowup JOIN child ON childfollowup.child_id = child.child_id "
                    + "JOIN positives ON child.patient_id_mother = positives.patient_id WHERE child.facility_id = " + facilityId + " "
                    + "AND childfollowup.date_visit >= '" + reportingDateBegin + "' AND childfollowup.date_visit <= '" + reportingDateEnd + "' "
                    + "AND childfollowup.rapid_test = 'Yes' AND childfollowup.rapid_test_result IN ('Positive', 'Negative') "
                    + "AND (TIMESTAMPDIFF(DAY, child.date_birth, '" + reportingDateBegin + "') / 30) < 9 AND child.gender = 'Female'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I32b", Integer.toString(count));

            //No. of  infants born to HIV infected women who received an HIV test at age < 9 months  (Rapid test) total
            parameterMap.put("I32c", Integer.toString(total));
            total = 0;

            //No. of  infants born to HIV infected women who received an HIV test at age 9 - 12months  (Rapid test) (Male)
            query = "SELECT COUNT(DISTINCT childfollowup.child_id) AS count FROM childfollowup JOIN child ON childfollowup.child_id = child.child_id "
                    + "JOIN positives ON child.patient_id_mother = positives.patient_id WHERE child.facility_id = " + facilityId + " "
                    + "AND childfollowup.date_visit >= '" + reportingDateBegin + "' AND childfollowup.date_visit <= '" + reportingDateEnd + "' "
                    + "AND childfollowup.rapid_test = 'Yes' AND childfollowup.rapid_test_result IN ('Positive', 'Negative') "
                    + "AND (TIMESTAMPDIFF(DAY, child.date_birth, '" + reportingDateBegin + "') / 30) >= 9 "
                    + "AND (TIMESTAMPDIFF(DAY, child.date_birth, '" + reportingDateBegin + "') / 30) <= 12 AND child.gender = 'Male'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I33a", Integer.toString(count));

            //No. of  infants born to HIV infected women who received an HIV test at age 9 - 12months  (Rapid test) (Female)
            query = "SELECT COUNT(DISTINCT childfollowup.child_id) AS count FROM childfollowup JOIN child ON childfollowup.child_id = child.child_id "
                    + "JOIN positives ON child.patient_id_mother = positives.patient_id WHERE child.facility_id = " + facilityId + " "
                    + "AND childfollowup.date_visit >= '" + reportingDateBegin + "' AND childfollowup.date_visit <= '" + reportingDateEnd + "' "
                    + "AND childfollowup.rapid_test = 'Yes' AND childfollowup.rapid_test_result IN ('Positive', 'Negative') "
                    + "AND (TIMESTAMPDIFF(DAY, child.date_birth, '" + reportingDateBegin + "') / 30) >= 9 "
                    + "AND (TIMESTAMPDIFF(DAY, child.date_birth, '" + reportingDateBegin + "') / 30) <= 12 AND child.gender = 'Female'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I33b", Integer.toString(count));

            //No. of  infants born to HIV infected women who received an HIV test at age 9 - 12months  (Rapid test) total
            parameterMap.put("I33c", Integer.toString(total));
            total = 0;

            //No. of  infants born to HIV infected women who received an HIV test at age 13 - 18 months  (Rapid test) (Male)
            query = "SELECT COUNT(DISTINCT childfollowup.child_id) AS count FROM childfollowup JOIN child ON childfollowup.child_id = child.child_id "
                    + "JOIN positives ON child.patient_id_mother = positives.patient_id WHERE child.facility_id = " + facilityId + " "
                    + "AND childfollowup.date_visit >= '" + reportingDateBegin + "' AND childfollowup.date_visit <= '" + reportingDateEnd + "' "
                    + "AND childfollowup.rapid_test = 'Yes' AND childfollowup.rapid_test_result IN ('Positive', 'Negative') "
                    + "AND (TIMESTAMPDIFF(DAY, child.date_birth, '" + reportingDateBegin + "') / 30) >= 13 "
                    + "AND (TIMESTAMPDIFF(DAY, child.date_birth, '" + reportingDateBegin + "') / 30) <= 18 AND child.gender = 'Male'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I34a", Integer.toString(count));

            //No. of  infants born to HIV infected women who received an HIV test at age 13 - 18 months  (Rapid test) (Female)
            query = "SELECT COUNT(DISTINCT childfollowup.child_id) AS count FROM childfollowup JOIN child ON childfollowup.child_id = child.child_id "
                    + "JOIN positives ON child.patient_id_mother = positives.patient_id WHERE child.facility_id = " + facilityId + " "
                    + "AND childfollowup.date_visit >= '" + reportingDateBegin + "' AND childfollowup.date_visit <= '" + reportingDateEnd + "' "
                    + "AND childfollowup.rapid_test = 'Yes' AND childfollowup.rapid_test_result IN ('Positive', 'Negative') "
                    + "AND (TIMESTAMPDIFF(DAY, child.date_birth, '" + reportingDateBegin + "') / 30) >= 13 "
                    + "AND (TIMESTAMPDIFF(DAY, child.date_birth, '" + reportingDateBegin + "') / 30) <= 18 AND child.gender = 'Female'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I34b", Integer.toString(count));

            //No. of  infants born to HIV infected women who received an HIV test at age 13 - 18 months  (Rapid test) total
            parameterMap.put("I34c", Integer.toString(total));
            total = 0;

            //No. of  infants born to HIV infected women who received an HIV test within 18 months who tested positive (Male)
            query = "SELECT COUNT(DISTINCT childfollowup.child_id) AS count FROM childfollowup JOIN child ON childfollowup.child_id = child.child_id "
                    + "JOIN positives ON child.patient_id_mother = positives.patient_id WHERE child.facility_id = " + facilityId + " "
                    + "AND childfollowup.date_visit >= '" + reportingDateBegin + "' AND childfollowup.date_visit <= '" + reportingDateEnd + "' "
                    + "AND (childfollowup.rapid_test_result = 'Positive' OR pcr_result = 'P - Positive') "
                    + "AND (TIMESTAMPDIFF(DAY, child.date_birth, '" + reportingDateBegin + "') / 30) <= 18 AND child.gender = 'Male'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I35a", Integer.toString(count));

            //No. of  infants born to HIV infected women who received an HIV test within 18 months who tested positive (Female)
            query = "SELECT COUNT(DISTINCT childfollowup.child_id) AS count FROM childfollowup JOIN child ON childfollowup.child_id = child.child_id "
                    + "JOIN positives ON child.patient_id_mother = positives.patient_id WHERE child.facility_id = " + facilityId + " "
                    + "AND childfollowup.date_visit >= '" + reportingDateBegin + "' AND childfollowup.date_visit <= '" + reportingDateEnd + "' "
                    + "AND (childfollowup.rapid_test_result = 'Positive' OR pcr_result = 'P - Positive') "
                    + "AND (TIMESTAMPDIFF(DAY, child.date_birth, '" + reportingDateBegin + "') / 30) <= 18 AND child.gender = 'Female'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I35b", Integer.toString(count));

            //No. of  infants born to HIV infected women who received an HIV test within 18 months who tested positive total
            parameterMap.put("I35c", Integer.toString(total));
            total = 0;

            //No. of Infants born to HIV+ women whose blood samples were taken for DNA PCR test within 2 months of birth (Male)
            query = "SELECT COUNT(DISTINCT childfollowup.child_id) AS count FROM childfollowup JOIN child ON childfollowup.child_id = child.child_id "
                    + "JOIN positives ON child.patient_id_mother = positives.patient_id WHERE child.facility_id = " + facilityId + " "
                    + "AND childfollowup.date_visit >= '" + reportingDateBegin + "' AND childfollowup.date_visit <= '" + reportingDateEnd + "' "
                    + "AND (TIMESTAMPDIFF(DAY, child.date_birth, childfollowup.date_sample_collected) / 30) <= 2 AND child.gender = 'Male'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I36a", Integer.toString(count));

            //No. of Infants born to HIV+ women whose blood samples were taken for DNA PCR test within 2 months of birth (Female)
            query = "SELECT COUNT(DISTINCT childfollowup.child_id) AS count FROM childfollowup JOIN child ON childfollowup.child_id = child.child_id "
                    + "JOIN positives ON child.patient_id_mother = positives.patient_id WHERE child.facility_id = " + facilityId + " "
                    + "AND childfollowup.date_visit >= '" + reportingDateBegin + "' AND childfollowup.date_visit <= '" + reportingDateEnd + "' "
                    + "AND (TIMESTAMPDIFF(DAY, child.date_birth, childfollowup.date_sample_collected) / 30) <= 2 AND child.gender = 'Female'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I36b", Integer.toString(count));

            //No. of Infants born to HIV+ women whose blood samples were taken for DNA PCR test within 2 months of birth total
            parameterMap.put("I36c", Integer.toString(total));
            total = 0;

            //No. of Infants born to HIV+ women whose blood samples were taken for DNA PCR test within 12 months of birth (Male)
            query = "SELECT COUNT(DISTINCT childfollowup.child_id) AS count FROM childfollowup JOIN child ON childfollowup.child_id = child.child_id "
                    + "JOIN positives ON child.patient_id_mother = positives.patient_id WHERE child.facility_id = " + facilityId + " "
                    + "AND childfollowup.date_visit >= '" + reportingDateBegin + "' AND childfollowup.date_visit <= '" + reportingDateEnd + "' "
                    + "AND (TIMESTAMPDIFF(DAY, child.date_birth, childfollowup.date_sample_collected) / 30) <= 12 AND child.gender = 'Male'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I37a", Integer.toString(count));

            //No. of Infants born to HIV+ women whose blood samples were taken for DNA PCR test within 12 months of birth (Female)
            query = "SELECT COUNT(DISTINCT childfollowup.child_id) AS count FROM childfollowup JOIN child ON childfollowup.child_id = child.child_id "
                    + "JOIN positives ON child.patient_id_mother = positives.patient_id WHERE child.facility_id = " + facilityId + " "
                    + "AND childfollowup.date_visit >= '" + reportingDateBegin + "' AND childfollowup.date_visit <= '" + reportingDateEnd + "' "
                    + "AND (TIMESTAMPDIFF(DAY, child.date_birth, childfollowup.date_sample_collected) / 30) <= 12 AND child.gender = 'Female'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I37b", Integer.toString(count));

            //No. of Infants born to HIV+ women whose blood samples were taken for DNA PCR test within 12 months of birth total
            parameterMap.put("I37c", Integer.toString(total));
            total = 0;

            //No. of HIV PCR  results received for babies born to HIV+ women whose blood samples were taken within 2 months of birth tested positive (Male)
            query = "SELECT COUNT(DISTINCT childfollowup.child_id) AS count FROM childfollowup JOIN child ON childfollowup.child_id = child.child_id "
                    + "JOIN positives ON child.patient_id_mother = positives.patient_id WHERE child.facility_id = " + facilityId + " "
                    + "AND childfollowup.date_visit >= '" + reportingDateBegin + "' AND childfollowup.date_visit <= '" + reportingDateEnd + "' "
                    + "AND (TIMESTAMPDIFF(DAY, child.date_birth, childfollowup.date_sample_collected) / 30) <= 2 "
                    + "AND childfollowup.pcr_result = 'P - Positive' AND child.gender = 'Male'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I38a", Integer.toString(count));

            //No. of HIV PCR  results received for babies born to HIV+ women whose blood samples were taken within 2 months of birth tested positive (Female)
            query = "SELECT COUNT(DISTINCT childfollowup.child_id) AS count FROM childfollowup JOIN child ON childfollowup.child_id = child.child_id "
                    + "JOIN positives ON child.patient_id_mother = positives.patient_id WHERE child.facility_id = " + facilityId + " "
                    + "AND childfollowup.date_visit >= '" + reportingDateBegin + "' AND childfollowup.date_visit <= '" + reportingDateEnd + "' "
                    + "AND (TIMESTAMPDIFF(DAY, child.date_birth, childfollowup.date_sample_collected) / 30) <= 2 "
                    + "AND childfollowup.pcr_result = 'P - Positive' AND child.gender = 'Female'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I38b", Integer.toString(count));

            //No. of HIV PCR  results received for babies born to HIV+ women whose blood samples were taken within 2 months of birth tested positive total
            parameterMap.put("I38c", Integer.toString(total));
            total = 0;

            //No. of HIV PCR  results received for babies born to HIV+ women whose blood samples were taken within 2 months of birth tested negative (Male)
            query = "SELECT COUNT(DISTINCT childfollowup.child_id) AS count FROM childfollowup JOIN child ON childfollowup.child_id = child.child_id "
                    + "JOIN positives ON child.patient_id_mother = positives.patient_id WHERE child.facility_id = " + facilityId + " "
                    + "AND childfollowup.date_visit >= '" + reportingDateBegin + "' AND childfollowup.date_visit <= '" + reportingDateEnd + "' "
                    + "AND (TIMESTAMPDIFF(DAY, child.date_birth, childfollowup.date_sample_collected) / 30) <= 2 "
                    + "AND childfollowup.pcr_result = 'N - Negative' AND child.gender = 'Male'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I39a", Integer.toString(count));

            //No. of HIV PCR  results received for babies born to HIV+ women whose blood samples were taken within 2 months of birth tested negative (Female)
            query = "SELECT COUNT(DISTINCT childfollowup.child_id) AS count FROM childfollowup JOIN child ON childfollowup.child_id = child.child_id "
                    + "JOIN positives ON child.patient_id_mother = positives.patient_id WHERE child.facility_id = " + facilityId + " "
                    + "AND childfollowup.date_visit >= '" + reportingDateBegin + "' AND childfollowup.date_visit <= '" + reportingDateEnd + "' "
                    + "AND (TIMESTAMPDIFF(DAY, child.date_birth, childfollowup.date_sample_collected) / 30) <= 2 "
                    + "AND childfollowup.pcr_result = 'N - Negative' AND child.gender = 'Female'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I39b", Integer.toString(count));

            //No. of HIV PCR  results received for babies born to HIV+ women whose blood samples were taken within 2 months of birth tested negative total
            parameterMap.put("I39c", Integer.toString(total));
            total = 0;

            //No. of HIV PCR  results received for babies born to HIV+ women (all ages) (Male)
            query = "SELECT COUNT(DISTINCT childfollowup.child_id) AS count FROM childfollowup JOIN child ON childfollowup.child_id = child.child_id "
                    + "JOIN positives ON child.patient_id_mother = positives.patient_id WHERE child.facility_id = " + facilityId + " "
                    + "AND childfollowup.date_visit >= '" + reportingDateBegin + "' AND childfollowup.date_visit <= '" + reportingDateEnd + "' "
                    + "AND childfollowup.pcr_result IN ('P - Positive', 'N - Negative', 'I - Indeterminate') AND child.gender = 'Male'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I40a", Integer.toString(count));

            //No. of HIV PCR  results received for babies born to HIV+ women (all ages) (Female)
            query = "SELECT COUNT(DISTINCT childfollowup.child_id) AS count FROM childfollowup JOIN child ON childfollowup.child_id = child.child_id "
                    + "JOIN positives ON child.patient_id_mother = positives.patient_id WHERE child.facility_id = " + facilityId + " "
                    + "AND childfollowup.date_visit >= '" + reportingDateBegin + "' AND childfollowup.date_visit <= '" + reportingDateEnd + "' "
                    + "AND childfollowup.pcr_result IN ('P - Positive', 'N - Negative', 'I - Indeterminate') AND child.gender = 'Female'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I40b", Integer.toString(count));

            //No. of HIV PCR  results received for babies born to HIV+ women (all ages) total
            parameterMap.put("I40c", Integer.toString(total));
            total = 0;

            //No. of HIV PCR  results received for babies born to HIV+ women tested positive (subset of 40 above) (Male)
            query = "SELECT COUNT(DISTINCT childfollowup.child_id) AS count FROM childfollowup JOIN child ON childfollowup.child_id = child.child_id "
                    + "JOIN positives ON child.patient_id_mother = positives.patient_id WHERE child.facility_id = " + facilityId + " "
                    + "AND childfollowup.date_visit >= '" + reportingDateBegin + "' AND childfollowup.date_visit <= '" + reportingDateEnd + "' "
                    + "AND childfollowup.pcr_result = 'P - Positive' AND child.gender = 'Male'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I41a", Integer.toString(count));

            //No. of HIV PCR  results received for babies born to HIV+ women tested positive (subset of 40 above) (Female)
            query = "SELECT COUNT(DISTINCT childfollowup.child_id) AS count FROM childfollowup JOIN child ON childfollowup.child_id = child.child_id "
                    + "JOIN positives ON child.patient_id_mother = positives.patient_id WHERE child.facility_id = " + facilityId + " "
                    + "AND childfollowup.date_visit >= '" + reportingDateBegin + "' AND childfollowup.date_visit <= '" + reportingDateEnd + "' "
                    + "AND childfollowup.pcr_result = 'P - Positive' AND child.gender = 'Male'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I41b", Integer.toString(count));

            //No. of HIV PCR  results received for babies born to HIV+ women tested positive (subset of 40 above) total
            parameterMap.put("I41c", Integer.toString(total));
            total = 0;

            //No. of infants born to HIV infected women exclusively breast fed at 3 months (Male)
            query = "SELECT COUNT(DISTINCT childfollowup.child_id) AS count FROM childfollowup JOIN child ON childfollowup.child_id = child.child_id "
                    + "JOIN positives ON child.patient_id_mother = positives.patient_id WHERE child.facility_id = " + facilityId + " "
                    + "AND childfollowup.date_visit >= '" + reportingDateBegin + "' AND childfollowup.date_visit <= '" + reportingDateEnd + "' "
                    + "AND childfollowup.feeding = '1 - Exclusive Breast Feeding (EBF)' AND (TIMESTAMPDIFF(DAY, child.date_birth, '" + reportingDateBegin + "') / 30) = 3 AND child.gender = 'Male'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I42a", Integer.toString(count));

            //No. of infants born to HIV infected women exclusively breast fed at 3 months (Female)
            query = "SELECT COUNT(DISTINCT childfollowup.child_id) AS count FROM childfollowup JOIN child ON childfollowup.child_id = child.child_id "
                    + "JOIN positives ON child.patient_id_mother = positives.patient_id WHERE child.facility_id = " + facilityId + " "
                    + "AND childfollowup.date_visit >= '" + reportingDateBegin + "' AND childfollowup.date_visit <= '" + reportingDateEnd + "' "
                    + "AND childfollowup.feeding = '1 - Exclusive Breast Feeding (EBF)' AND (TIMESTAMPDIFF(DAY, child.date_birth, '" + reportingDateBegin + "') / 30) = 3 AND child.gender = 'Female'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I42b", Integer.toString(count));

            //No. of infants born to HIV infected women exclusively breast fed at 3 months total
            parameterMap.put("I42c", Integer.toString(total));
            total = 0;

            //No. of infants born to HIV infected women on commercial Infant Formula at 3 months (Male)
            query = "SELECT COUNT(DISTINCT childfollowup.child_id) AS count FROM childfollowup JOIN child ON childfollowup.child_id = child.child_id "
                    + "JOIN positives ON child.patient_id_mother = positives.patient_id WHERE child.facility_id = " + facilityId + " "
                    + "AND childfollowup.date_visit >= '" + reportingDateBegin + "' AND childfollowup.date_visit <= '" + reportingDateEnd + "' "
                    + "AND childfollowup.feeding = '2 - Commercial Infant Formula/Exclusive Replacement Feeding' AND (TIMESTAMPDIFF(DAY, child.date_birth, '" + reportingDateBegin + "') / 30) = 3 AND child.gender = 'Male'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I43a", Integer.toString(count));

            //No. of infants born to HIV infected women on commercial Infant Formula at 3 months (Female)
            query = "SELECT COUNT(DISTINCT childfollowup.child_id) AS count FROM childfollowup JOIN child ON childfollowup.child_id = child.child_id "
                    + "JOIN positives ON child.patient_id_mother = positives.patient_id WHERE child.facility_id = " + facilityId + " "
                    + "AND childfollowup.date_visit >= '" + reportingDateBegin + "' AND childfollowup.date_visit <= '" + reportingDateEnd + "' "
                    + "AND childfollowup.feeding = '2 - Commercial Infant Formula/Exclusive Replacement Feeding' AND (TIMESTAMPDIFF(DAY, child.date_birth, '" + reportingDateBegin + "') / 30) = 3 AND child.gender = 'Female'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I43b", Integer.toString(count));

            //No. of infants born to HIV infected women on commercial Infant Formula at 3 months total
            parameterMap.put("I43c", Integer.toString(total));
            total = 0;

            //No. of infants born to HIV infected women on mixed feeding at 3 months (Male)
            query = "SELECT COUNT(DISTINCT childfollowup.child_id) AS count FROM childfollowup JOIN child ON childfollowup.child_id = child.child_id "
                    + "JOIN positives ON child.patient_id_mother = positives.patient_id WHERE child.facility_id = " + facilityId + " "
                    + "AND childfollowup.date_visit >= '" + reportingDateBegin + "' AND childfollowup.date_visit <= '" + reportingDateEnd + "' "
                    + "AND childfollowup.feeding = '3 - Mixed Feeding (MF)' AND (TIMESTAMPDIFF(DAY, child.date_birth, '" + reportingDateBegin + "') / 30) = 3 AND child.gender = 'Male'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I44a", Integer.toString(count));

            //No. of infants born to HIV infected women on mixed feeding at 3 months (Female)
            query = "SELECT COUNT(DISTINCT childfollowup.child_id) AS count FROM childfollowup JOIN child ON childfollowup.child_id = child.child_id "
                    + "JOIN positives ON child.patient_id_mother = positives.patient_id WHERE child.facility_id = " + facilityId + " "
                    + "AND childfollowup.date_visit >= '" + reportingDateBegin + "' AND childfollowup.date_visit <= '" + reportingDateEnd + "' "
                    + "AND childfollowup.feeding = '3 - Mixed Feeding (MF)' AND (TIMESTAMPDIFF(DAY, child.date_birth, '" + reportingDateBegin + "') / 30) = 3 AND child.gender = 'Female'";
            count = executeQuery(query);
            total += count;
            parameterMap.put("I44b", Integer.toString(count));

            //No. of infants born to HIV infected women on mixed feeding at 3 months total
            parameterMap.put("I44c", Integer.toString(total));
            total = 0;


            System.out.println("...after end");




            parameterMap.put("reportingMonth", request.getParameter("reportingMonth"));
            parameterMap.put("reportingYear", request.getParameter("reportingYear"));

            // fetch the required records from the database   
            query = "SELECT DISTINCT facility.name, facility.address1, facility.address2, facility.phone1, facility.phone2, facility.email, lga.name AS lga, state.name AS state FROM facility JOIN lga ON facility.lga_id = lga.lga_id JOIN state ON facility.state_id = state.state_id WHERE facility_id = " + facilityId;
            preparedStatement = jdbcUtil.getStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                parameterMap.put("facilityName", resultSet.getString("name"));
                parameterMap.put("lga", resultSet.getString("lga"));
                parameterMap.put("state", resultSet.getString("state"));
            }
        } catch (Exception exception) {
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }

        System.out.println("...Before returning parameter map");

        return parameterMap;
    }

    private int executeQuery(String query) {
        int count = 0;
        try {
            preparedStatement = jdbcUtil.getStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                count = resultSet.getInt("count");
            }
        } catch (Exception exception) {


            System.out.println("...ExcepCount - " + exception);

            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }
        return count;
    }

    private void executeUpdate(String query) {
        try {
            preparedStatement = jdbcUtil.getStatement(query);
            preparedStatement.executeUpdate();
        } catch (Exception exception) {


            System.out.println("...ExcepUpdate - " + exception);

            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }
    }
}
