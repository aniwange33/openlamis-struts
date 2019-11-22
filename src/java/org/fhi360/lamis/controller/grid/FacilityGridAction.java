/**
 *
 * @author AALOZIE
 */
package org.fhi360.lamis.controller.grid;

import java.util.*;
import com.opensymphony.xwork2.ActionSupport;
import org.fhi360.lamis.utility.builder.FacilityListBuilder;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class FacilityGridAction extends ActionSupport {

    private Integer page;
    private Integer rows;
    private String sidx;
    private String sord;
    private Integer totalpages;
    private Integer currpage;
    private Integer totalrecords;

    private String query;
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private HttpServletRequest request;

    private ArrayList<Map<String, String>> facilityList = new ArrayList<>();

    public String facilityGrid() {
        //setTotalpages(1);
        //setCurrpage(1);
        //setTotalrecords(20);
        int level[] = {0};
        Long stateId[] = {null};
        Long lgaId[] = {null};
        request = ServletActionContext.getRequest();
        try {
            // obtain a JDBC connect and execute query
            query = "SELECT * FROM facility";
            if (request.getParameterMap().containsKey("stateId")) {
                if (request.getParameterMap().containsKey("lgaId")) {
                    level[0] = 2;
                    query = "SELECT * FROM facility WHERE state_id = ? AND lga_id = ?";
                } else {
                    level[0] = 1;
                    query = "SELECT * FROM facility WHERE state_id = ?";
                }
            }

            if (level[0] == 1) {
                stateId[0] = Long.parseLong(request.getParameter("stateId"));
            }
            if (level[0] == 2) {
                stateId[0] = Long.parseLong(request.getParameter("stateId"));
                lgaId[0] = Long.parseLong(request.getParameter("lgaId"));
            }
            jdbcTemplate.query(query, resultSet -> {
                new FacilityListBuilder().buildFacilityList(resultSet);
                facilityList = new FacilityListBuilder().retrieveFacilityList();
                return null;
            }, stateId[0], lgaId[0]);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return SUCCESS;
    }

    //This method controls the NDR page.
    public String facilitySelGrid() {
        //setTotalpages(1);
        //setCurrpage(1);
        //setTotalrecords(20);
        request = ServletActionContext.getRequest();
        try {
            executeUpdate("DROP TABLE IF EXISTS actives");
            query = "CREATE TEMPORARY TABLE actives AS SELECT facility_id, COUNT(patient_id) AS count FROM patient WHERE current_status IN ('ART Start', 'ART Restart', 'ART Transfer In') AND DATEDIFF(DAY, date_last_refill + last_refill_duration, CURDATE()) <= " + Constants.LTFU.PEPFAR + " AND date_started IS NOT NULL GROUP BY facility_id";
            executeUpdate(query);

            executeUpdate("DROP TABLE IF EXISTS counts");
            query = "CREATE TEMPORARY TABLE counts AS SELECT facility_id, COUNT(patient_id) AS count FROM patient GROUP BY facility_id";
            executeUpdate(query);

            query = "SELECT facility_id, name, datim_id FROM facility WHERE facility_id IN (SELECT DISTINCT facility_id FROM patient) AND state_id = ? ORDER BY name ASC";
            jdbcTemplate.query(query, resultSet -> {
                int i = 1;
                while (resultSet.next()) {
                    String facilityId = Long.toString(resultSet.getLong("facility_id"));
                    String name = resultSet.getString("name");
                    Long facId = resultSet.getLong("facility_id");
                    String count = getFacilityPatientCount(facId);
                    String active = getActiveClientsForFacility(facId);
                    String datimId = resultSet.getString("datim_id");

                    Map<String, String> map = new HashMap<>();
                    map.put("facilityId", facilityId);
                    map.put("name", name);
                    map.put("count", count);
                    map.put("active", active);
                    map.put("datimId", datimId);
                    map.put("sn", "" + i);
                    map.put("sel", "0");
                    facilityList.add(map);
                    i++;
                }
                return null;
            }, Long.parseLong(request.getParameter("stateId")));

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return SUCCESS;
    }

    private String getFacilityPatientCount(long facId) {
        String query = "SELECT count FROM counts WHERE facility_id = ?";
        try {
            return jdbcTemplate.queryForObject(query, Long.class, facId).toString();
        } catch (Exception e) {
            return "0";
        }
    }

    public String getActiveClientsForFacility(long facId) {
        String query = "SELECT count FROM actives WHERE facility_id = ?";
        try {
            return jdbcTemplate.queryForObject(query, Long.class, facId).toString();
        } catch (Exception e) {
            return "0";
        }
    }

    private void executeUpdate(String query) {
        transactionTemplate.execute((ts) -> {
            jdbcTemplate.execute(query);
            return null;
        });
    }

    /**
     * @return the page
     */
    public Integer getPage() {
        return page;
    }

    /**
     * @param page the page to set
     */
    public void setPage(Integer page) {
        this.page = page;
    }

    /**
     * @return the limit
     */
    public Integer getRows() {
        return rows;
    }

    /**
     * @param limit the limit to set
     */
    public void setRows(Integer rows) {
        this.rows = rows;
    }

    /**
     * @return the sidx
     */
    public String getSidx() {
        return sidx;
    }

    /**
     * @param sidx the sidx to set
     */
    public void setSidx(String sidx) {
        this.sidx = sidx;
    }

    /**
     * @return the sord
     */
    public String getSord() {
        return sord;
    }

    /**
     * @param sord the sord to set
     */
    public void setSord(String sord) {
        this.sord = sord;
    }

    /**
     * @return the totalpages
     */
    public Integer getTotalpages() {
        return totalpages;
    }

    /**
     * @param totalpages the totalpages to set
     */
    public void setTotalpages(Integer totalpages) {
        this.totalpages = totalpages;
    }

    /**
     * @return the currpage
     */
    public Integer getCurrpage() {
        return currpage;
    }

    /**
     * @param currpage the currpage to set
     */
    public void setCurrpage(Integer currpage) {
        this.currpage = currpage;
    }

    /**
     * @return the totalrecords
     */
    public Integer getTotalrecords() {
        return totalrecords;
    }

    /**
     * @param totalrecords the totalrecords to set
     */
    public void setTotalrecords(Integer totalrecords) {
        this.totalrecords = totalrecords;
    }

    /**
     * @return the facilityList
     */
    public ArrayList<Map<String, String>> getFacilityList() {
        return facilityList;
    }

    /**
     * @param facilityList the facilityList to set
     */
    public void setFacilityList(ArrayList<Map<String, String>> facilityList) {
        this.facilityList = facilityList;
    }
}
