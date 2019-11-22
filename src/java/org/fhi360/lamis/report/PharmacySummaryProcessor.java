/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.report;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;

public class PharmacySummaryProcessor {

    private int reportingMonth;
    private int reportingYear;

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    private ArrayList<Map<String, Object>> reportList;
    private HashMap parameterMap;
    private long facilityId;

    public PharmacySummaryProcessor() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    public synchronized ArrayList<Map<String, Object>> process() {
        reportList = new ArrayList<Map<String, Object>>();
        reportingMonth = DateUtil.getMonth(request.getParameter("reportingMonth"));
        reportingYear = Integer.parseInt(request.getParameter("reportingYear"));
        facilityId = (Long) session.getAttribute("facilityId");
        try {
            executeUpdate("DROP TABLE IF EXISTS refill");
            query = "CREATE TEMPORARY TABLE refill AS SELECT pharmacy.pharmacy_id, pharmacy.patient_id, pharmacy.morning+pharmacy.afternoon+pharmacy.evening AS quantity, pharmacy.regimentype_id, pharmacy.regimendrug_id, regimendrug.regimen_id, regimendrug.drug_id, drug.name, drug.strength "
                    + " FROM pharmacy JOIN regimendrug ON pharmacy.regimendrug_id = regimendrug.regimendrug_id JOIN drug ON regimendrug.drug_id = drug.drug_id WHERE pharmacy.facility_id = " + facilityId + " AND YEAR(pharmacy.date_visit) = " + reportingYear + " AND MONTH(pharmacy.date_visit) = " + reportingMonth;
            jdbcTemplate.execute(query);

            query = "SELECT refill.name AS description, SUM(refill.quantity) AS quantity, COUNT(DISTINCT refill.patient_id) AS num FROM refill GROUP BY refill.name";
            jdbcTemplate.query(query, resultSet -> {
                int i = 0;
                while (resultSet.next()) {
                    String description = resultSet.getString("description");
                    double quantity = resultSet.getDouble("quantity");
                    int num = resultSet.getInt("num");

                    // create map of values 
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("sno", Integer.toString(i++));
                    map.put("description", description);
                    map.put("quantity", Double.toString(quantity));
                    map.put("num", Integer.toString(num));
                    reportList.add(map);
                }
                return null;
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return reportList;
    }

    public HashMap getReportParameters() {
        parameterMap = new HashMap();
        parameterMap.put("reportingMonth", request.getParameter("reportingMonth"));
        parameterMap.put("reportingYear", request.getParameter("reportingYear"));
        facilityId = (Long) session.getAttribute("facilityId");

        try {
            // fetch the required records from the database
            query = "SELECT DISTINCT facility.name, facility.address1, facility.address2, facility.phone1, facility.phone2, facility.email, lga.name AS lga, state.name AS state FROM facility JOIN lga ON facility.lga_id = lga.lga_id JOIN state ON facility.state_id = state.state_id WHERE facility_id = " + facilityId;
            jdbcTemplate.query(query, resultSet -> {
                if (resultSet.next()) {
                    parameterMap.put("facilityName", resultSet.getString("name"));
                    parameterMap.put("lga", resultSet.getString("lga"));
                    parameterMap.put("state", resultSet.getString("state"));
                }
                return null;
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return parameterMap;
    }

    private void executeUpdate(String query) {
        try {
            jdbcTemplate.execute(query);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
