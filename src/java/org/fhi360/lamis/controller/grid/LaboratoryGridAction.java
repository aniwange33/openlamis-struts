/**
 *
 * @author AALOZIE
 */
package org.fhi360.lamis.controller.grid;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.ActionSupport;
import org.fhi360.lamis.service.beans.ContextProvider;

import org.fhi360.lamis.utility.builder.LaboratoryListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;

public class LaboratoryGridAction extends ActionSupport implements Preparable {

    private Integer page;
    private Integer rows;
    private String sidx;
    private String sord;
    private Integer totalpages;
    private Integer currpage;
    private Integer totalrecords;

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private ArrayList<Map<String, String>> laboratoryList = new ArrayList<>();

    @Override
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
    }

    public String laboratoryGrid() {
        //setTotalpages(1);
        //setCurrpage(1);
        //setTotalrecords(20);
        try {
            // obtain a JDBC connect and execute query

            // fetch the required records from the database
            query = "SELECT laboratory.laboratory_id, laboratory.patient_id, laboratory.facility_id, laboratory.date_collected, laboratory.date_reported, laboratory.labno, laboratory.resultab, laboratory.resultpc, laboratory.comment, laboratory.labtest_id, labtest.description "
                    + " FROM laboratory JOIN labtest ON laboratory.labtest_id = labtest.labtest_id WHERE laboratory.facility_id = ? AND laboratory.patient_id = ? ORDER BY laboratory.date_reported DESC";

            jdbcTemplate.query(query, resultSet -> {
                new LaboratoryListBuilder().buildLaboratoryList(resultSet);
                laboratoryList = new LaboratoryListBuilder().retrieveLaboratoryList();
                return null;
            }, (Long) session.getAttribute("facilityId"), Long.parseLong(request.getParameter("patientId")));
        } catch (Exception exception) {

        }
        return SUCCESS;
    }

    /*
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
     * @return the laboratoryList
     */
    public ArrayList<Map<String, String>> getLaboratoryList() {
        return laboratoryList;
    }

    /**
     * @param laboratoryList the laboratoryList to set
     */
    public void setLaboratoryList(ArrayList<Map<String, String>> laboratoryList) {
        this.laboratoryList = laboratoryList;
    }
}
