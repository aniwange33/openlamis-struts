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

import org.fhi360.lamis.utility.builder.MaternalfollowupListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class MaternalfollowupGridAction extends ActionSupport implements Preparable {

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

    private ArrayList<Map<String, String>> maternalfollowupList = new ArrayList<>();

    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
    }

    public String maternalfollowupGrid() {
        //setTotalpages(1);
        //setCurrpage(1);
        //setTotalrecords(20);
        try {
            // fetch the required records from the database

            query = "SELECT maternalfollowup.maternalfollowup_id, maternalfollowup.date_visit, maternalfollowup.type_of_visit, maternalfollowup.body_weight, maternalfollowup.gestational_age, maternalfollowup.bp, maternalfollowup.fundal_height, maternalfollowup.fetal_presentation, maternalfollowup.viral_load_collected, maternalfollowup.date_sample_collected, maternalfollowup.date_confirmed_hiv, maternalfollowup.time_hiv_diagnosis, maternalfollowup.arv_regimen_past, maternalfollowup.arv_regimen_current, maternalfollowup.date_arv_regimen_current, maternalfollowup.screen_post_partum, maternalfollowup.syphilis_tested, maternalfollowup.syphilis_test_result, maternalfollowup.syphilis_treated, maternalfollowup.cd4_ordered, maternalfollowup.cd4, maternalfollowup.counsel_nutrition, maternalfollowup.counsel_feeding, maternalfollowup.counsel_family_planning, maternalfollowup.tb_status, maternalfollowup.visit_status, maternalfollowup.family_planning_method, maternalfollowup.referred, maternalfollowup.date_next_visit, maternalfollowup.anc_id, maternalfollowup.facility_id, maternalfollowup.patient_id, partnerinformation.partner_hiv_status FROM maternalfollowup JOIN partnerinformation ON maternalfollowup.patient_id = partnerinformation.patient_id WHERE maternalfollowup.facility_id = ? AND maternalfollowup.patient_id = ?";

            jdbcTemplate.query(query, resultSet -> {
                new MaternalfollowupListBuilder().buildMaternalfollowupList(resultSet);
                maternalfollowupList = new MaternalfollowupListBuilder().retrieveMaternalfollowupList();
                return null;
            }, (Long) session.getAttribute("facilityId"), Long.parseLong(request.getParameter("patientId")));
        } catch (Exception exception) {
            exception.printStackTrace();

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
     * @return the maternalfollowupList
     */
    public ArrayList<Map<String, String>> getMaternalfollowupList() {
        return maternalfollowupList;
    }

    /**
     * @param maternalfollowupList the maternalfollowupList to set
     */
    public void setMaternalfollowupList(ArrayList<Map<String, String>> maternalfollowupList) {
        this.maternalfollowupList = maternalfollowupList;
    }
}
