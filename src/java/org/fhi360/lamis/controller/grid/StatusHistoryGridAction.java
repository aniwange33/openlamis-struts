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
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.service.beans.ContextProvider;

import org.fhi360.lamis.utility.builder.StatusHistoryBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class StatusHistoryGridAction extends ActionSupport implements Preparable {

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

    private Patient patientObjSession;
    private ArrayList<Map<String, String>> statusList = new ArrayList<>();

    @Override
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
    }

    public String statusGrid() {

        try {
            query = "SELECT DISTINCT patient.facility_id, patient.patient_id, patient.date_registration, "
                    + " patient.status_registration, patient.date_started, "
                    + " statushistory.history_id, statushistory.current_status, statushistory.date_current_status, "
                    + " statushistory.outcome, statushistory.date_tracked "
                    + " FROM patient JOIN statushistory ON patient.patient_id = statushistory.patient_id "
                    + " WHERE patient.facility_id = ? AND statushistory.facility_id = ?"
                    + " AND patient.patient_id = ? ORDER BY statushistory.date_current_status DESC";

            jdbcTemplate.query(query, resultSet -> {
                new StatusHistoryBuilder().buildStatusList(resultSet);
                statusList = new StatusHistoryBuilder().retrieveStatusList();
                return null;
            }, (Long) session.getAttribute("facilityId"), (Long) session.getAttribute("facilityId"), Long.parseLong(request.getParameter("patientId")));

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
     * @return the statusList
     */
    public ArrayList<Map<String, String>> getStatusList() {
        return statusList;
    }

    /**
     * @param statusList the statusList to set
     */
    public void setStatusList(ArrayList<Map<String, String>> statusList) {
        this.statusList = statusList;
    }
}
