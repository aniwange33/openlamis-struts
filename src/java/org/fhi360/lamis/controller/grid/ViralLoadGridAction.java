/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.controller.grid;

import static com.opensymphony.xwork2.Action.SUCCESS;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import java.util.ArrayList;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;

import org.fhi360.lamis.utility.builder.ViralLoadListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author user10
 */
public class ViralLoadGridAction extends ActionSupport implements Preparable {

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
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    private ArrayList<Map<String, String>> viralLoadList = new ArrayList<>();
    long facilityId;

    @Override
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
        facilityId = (Long) session.getAttribute("facilityId");

    }

    public String viralLoadGridRetrieve() {
        Map<String, Object> pagerParams = new PaginationUtil().paginateGrid(getPage().intValue(), getRows().intValue(), "patient");
        int start = (Integer) pagerParams.get("start");
        int numberOfRows = getRows().intValue();
        try {
            //The global table...
            query = "SELECT patient_id, facility_id, hospital_num, surname, other_names, phone, date_started FROM patient WHERE current_status IN('ART Start', 'ART Restart', 'ART Transfer In') AND date_started IS NOT NULL ORDER BY date_started DESC LIMIT " + start + ", " + numberOfRows;
            jdbcTemplate.query(query, resultSet -> {
                new ViralLoadListBuilder().buildViralLoadList(resultSet);
                viralLoadList = new ViralLoadListBuilder().retrieveViralLoadList();
                return null;
            });

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        page = (Integer) pagerParams.get("page");
        currpage = (Integer) pagerParams.get("page");
        totalpages = (Integer) pagerParams.get("totalpages");
        totalrecords = (Integer) pagerParams.get("totalrecords");
        //System.out.println("Current_page is: "+currpage+" and Page is: "+page);
        session.setAttribute("currpage", currpage);

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
     * @return the statusList
     */
    public ArrayList<Map<String, String>> getViralLoadList() {
        return viralLoadList;
    }

    /**
     * @param statusList the statusList to set
     */
    public void setViralLoadList(ArrayList<Map<String, String>> viralLoadList) {
        this.viralLoadList = viralLoadList;
    }

}
