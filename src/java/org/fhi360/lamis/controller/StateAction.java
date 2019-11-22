/**
 *
 * @author AALOZIE
 */
package org.fhi360.lamis.controller;

import java.util.*;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.builder.UserListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class StateAction extends ActionSupport implements Preparable {

    private String stateId;
    private String state;
    private String lgaId;
    private String lga;
    private String query;

    private Integer page;
    private Integer rows;
    private String sidx;
    private String sord;
    private Integer totalpages;
    private Integer currpage;
    private Integer totalrecords;
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private HttpServletRequest request;
    private HttpSession session;

    private Map<String, String> lgaMap = new TreeMap<>();
    private Map<String, String> stateMap = new TreeMap<>();
    private Map<String, String> stateMapCustom = new TreeMap<>();
    private Map<String, String> facilityMap = new TreeMap<>();
    private ArrayList<Map<String, String>> statesList = new ArrayList<>();

    @Override
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
    }

    public String retrieveState() {
        try {

            query = "SELECT * FROM state ORDER BY name";

            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    stateMap.put(name, name);
                }
                return null; //To change body of generated lambdas, choose Tools | Templates.
            });
            // loop through resultSet for each row and put into Map

        } catch (Exception exception) {

        }
        return SUCCESS;
    }

    public String retrieveStateForUsers() {
        try {

            query = "SELECT * FROM state ORDER BY name";
            jdbcTemplate.query(query, resultSet -> {
                new UserListBuilder().buildStatesList(resultSet);
                statesList = new UserListBuilder().retrieveStatesList();
                return null;
            });

        } catch (Exception exception) {

        }
        return SUCCESS;
    }

    public String retrieveStateById() {
        try {

            query = "SELECT * FROM state ORDER BY name";
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String stateId = Long.toString(resultSet.getLong("state_id"));
                    String name = resultSet.getString("name");
                    stateMap.put(stateId, name);
                }
                return null;
            });
            // loop through resultSet for each row and put into Map

        } catch (Exception exception) {

        }
        return SUCCESS;
    }

    public String retrieveStateByIdCustom() {
        try {

            query = "SELECT DISTINCT st.name AS name, st.state_id AS state_id FROM PATIENT AS pt JOIN FACILITY AS ft ON pt.facility_id = ft.facility_id JOIN state AS st ON ft.state_id = st.state_id";
            jdbcTemplate.query(query, (resultSet) -> {
                while (resultSet.next()) {
                    String stateId = Long.toString(resultSet.getLong("state_id"));
                    String name = resultSet.getString("name");
                    stateMapCustom.put(stateId, name);
                }
                return null; //To change body of generated lambdas, choose Tools | Templates.
            });
            // loop through resultSet for each row and put into Map

        } catch (Exception exception) {

        }
        return SUCCESS;
    }

    public String retrieveLga() {
        try {

            query = "SELECT * FROM lga WHERE state_id = (SELECT DISTINCT state_id FROM state WHERE TRIM(BOTH ' ' FROM name) = ?) ORDER BY name";

            jdbcTemplate.query(query, (resultSet) -> {
                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    lgaMap.put(name, name);
                }
                return null; //To change body of generated lambdas, choose Tools | Templates.
            }, ServletActionContext.getRequest().getParameter("state"));
            // loop through resultSet for each row and put into Map

        } catch (Exception exception) {

        }
        return SUCCESS;
    }

    public String retrieveLgaById() {
        try {

            query = "SELECT * FROM lga WHERE state_id  = ? ORDER BY name";

            jdbcTemplate.query(query, (resultSet) -> {
                while (resultSet.next()) {
                    String lgaId = Long.toString(resultSet.getLong("lga_id"));
                    String name = resultSet.getString("name");
                    lgaMap.put(lgaId, name);
                }
                return null;
            }, ServletActionContext.getRequest().getParameter("stateId"));
            // loop through resultSet for each row and put into Map

        } catch (Exception exception) {

        }
        return SUCCESS;
    }

    public String retrieveFacility() {
        boolean active = false;
        stateId = ServletActionContext.getRequest().getParameter("stateId");

        if (ServletActionContext.getRequest().getParameterMap().containsKey("active")) {
            active = true;
        }

        if (ServletActionContext.getRequest().getParameterMap().containsKey("lgaId")) {
            lgaId = ServletActionContext.getRequest().getParameter("lgaId");
            query = active ? "SELECT DISTINCT pt.facility_id, ft.name FROM patient AS pt JOIN facility AS ft ON pt.facility_id = ft.facility_id WHERE ft.state_id  = " + Long.parseLong(stateId) + " AND ft.lga_id = " + Long.parseLong(lgaId) + " ORDER BY ft.name" : "SELECT * FROM facility WHERE state_id  = " + Long.parseLong(stateId) + " AND lga_id = " + Long.parseLong(lgaId) + " ORDER BY name";
        } else {
            query = active ? "SELECT DISTINCT pt.facility_id, ft.name FROM patient AS pt JOIN facility AS ft ON pt.facility_id = ft.facility_id WHERE ft.state_id  = " + Long.parseLong(stateId) + " ORDER BY ft.name" : "SELECT * FROM facility WHERE state_id  = " + Long.parseLong(stateId) + " ORDER BY name";
        }
        try {
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String facilityId = Long.toString(resultSet.getLong("facility_id"));
                    String name = resultSet.getString("name");
                    facilityMap.put(facilityId, name);
                }
                return null;
            });
            // loop through resultSet for each row and put into Map

        } catch (Exception exception) {
            exception.printStackTrace();

        }
        return SUCCESS;
    }

    public String retrieveTreatmentUnit() {
        query = "SELECT DISTINCT specimen.treatment_unit_id, facility.name FROM specimen JOIN facility ON specimen.treatment_unit_id = facility.facility_id ORDER BY facility.name";
        try {
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String facilityId = Long.toString(resultSet.getLong("treatment_unit_id"));
                    String name = resultSet.getString("name");
                    facilityMap.put(facilityId, name);
                }
                return null; //To change body of generated lambdas, choose Tools | Templates.
            });

            // loop through resultSet for each row and put into Map
        } catch (Exception exception) {

        }
        return SUCCESS;
    }

    /**
     * @return the stateId
     */
    public String getStateId() {
        return stateId;
    }

    /**
     * @param stateId the stateId to set
     */
    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    /**
     * @return the lgaId
     */
    public String getLgaId() {
        return lgaId;
    }

    /**
     * @param lgaId the lgaId to set
     */
    public void setLgaId(String lgaId) {
        this.lgaId = lgaId;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the lga
     */
    public String getLga() {
        return lga;
    }

    /**
     * @param lga the lga to set
     */
    public void setLga(String lga) {
        this.lga = lga;
    }

    /**
     * @return the lgaMap
     */
    public Map<String, String> getLgaMap() {
        return lgaMap;
    }

    /**
     * @param lgaMap the lgaMap to set
     */
    public void setLgaMap(Map<String, String> lgaMap) {
        this.lgaMap = lgaMap;
    }

    /**
     * @return the stateMap
     */
    public Map<String, String> getStateMap() {
        return stateMap;
    }

    /**
     * @param stateMap the stateMap to set
     */
    public void setStateMap(Map<String, String> stateMap) {
        this.stateMap = stateMap;
    }

    /**
     * @return the stateMap
     */
    public Map<String, String> getFacilityMap() {
        return facilityMap;
    }

    public Map<String, String> getStateMapCustom() {
        return stateMapCustom;
    }

    public void setStateMapCustom(Map<String, String> stateMapCustom) {
        this.stateMapCustom = stateMapCustom;
    }

    /**
     * @param stateMap the stateMap to set
     */
    public void setFacilityMap(Map<String, String> facilityMap) {
        this.facilityMap = facilityMap;
    }

    public ArrayList<Map<String, String>> getStatesList() {
        return statesList;
    }

    public void setStatesList(ArrayList<Map<String, String>> statesList) {
        this.statesList = statesList;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public String getSidx() {
        return sidx;
    }

    public void setSidx(String sidx) {
        this.sidx = sidx;
    }

    public String getSord() {
        return sord;
    }

    public void setSord(String sord) {
        this.sord = sord;
    }

    public Integer getTotalpages() {
        return totalpages;
    }

    public void setTotalpages(Integer totalpages) {
        this.totalpages = totalpages;
    }

    public Integer getCurrpage() {
        return currpage;
    }

    public void setCurrpage(Integer currpage) {
        this.currpage = currpage;
    }

    public Integer getTotalrecords() {
        return totalrecords;
    }

    public void setTotalrecords(Integer totalrecords) {
        this.totalrecords = totalrecords;
    }

}
