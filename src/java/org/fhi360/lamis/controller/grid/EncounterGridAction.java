/**
 *
 * @author AALOZIE
 */
package org.fhi360.lamis.controller.grid;

import java.util.*;
import java.sql.ResultSet;
import com.opensymphony.xwork2.ActionSupport;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.utility.builder.EncounterListBuilder;
import static com.opensymphony.xwork2.Action.SUCCESS;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class EncounterGridAction extends ActionSupport {

    private Integer page;
    private Integer rows;
    private String sidx;
    private String sord;
    private Integer totalpages;
    private Integer currpage;
    private Integer totalrecords;

    private String query;
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    private ResultSet resultSet;
    private HttpServletRequest request;

    private ArrayList<Map<String, String>> encounterList = new ArrayList<>();

    public String encounterGrid() {
        //setTotalpages(1);
        //setCurrpage(1);
        //setTotalrecords(20);
        request = ServletActionContext.getRequest();
        try {
            query = "SELECT * FROM encounter";
            if (request.getParameterMap().containsKey("stateId")) {
                query = "SELECT * FROM encounter WHERE facility_id IN (SELECT facility_id FROM facility WHERE state_id = " + Long.toString(resultSet.getLong("stateId")) + ")";
            }
            if (request.getParameterMap().containsKey("lgaId")) {
                query = "SELECT * FROM encounter WHERE facility_id IN (SELECT facility_id FROM facility WHERE lga_id = " + Long.toString(resultSet.getLong("lgaId")) + ")";
            }
            if (request.getParameterMap().containsKey("facilityId")) {
                query = "SELECT * FROM encounter WHERE facility_id = " + Long.toString(resultSet.getLong("facilityId")) + ")";
            }

            jdbcTemplate.query(query, resultSet -> {
                new EncounterListBuilder().buildEncounterList(resultSet);
                encounterList = new EncounterListBuilder().retrieveEncounterList();
                return null;
            });

        } catch (Exception exception) {
           
          
        }
        return SUCCESS;
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
     * @return the encounterList
     */
    public ArrayList<Map<String, String>> getEncounterList() {
        return encounterList;
    }

    /**
     * @param encounterList the encounterListt to set
     */
    public void setEncounterList(ArrayList<Map<String, String>> encounterList) {
        this.encounterList = encounterList;
    }
}
