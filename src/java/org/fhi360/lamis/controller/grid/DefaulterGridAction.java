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

import org.fhi360.lamis.utility.builder.PatientListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class DefaulterGridAction extends ActionSupport implements Preparable {

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

    private ArrayList<Map<String, String>> defaulterList = new ArrayList<>();

    @Override
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
    }

    public String defaulterGrid() {
        //setTotalpages(1);
        //setCurrpage(1);
        //setTotalrecords(20);
        try {
            // obtain a JDBC connect and execute query

            query = "SELECT patient_id, hospital_num, surname, other_names, gender, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, address, phone, current_status, date_current_status, date_last_refill, date_last_clinic, date_tracked, outcome, agreed_date "
                    + " FROM patient WHERE facility_id = ? AND current_status NOT IN ('ART Transfer Out', 'Pre-ART Transfer Out', 'Lost to Follow Up', 'Stopped Treatment', 'Known Death') AND ((date_next_clinic IS NOT NULL AND DATEDIFF(DAY, date_next_clinic, CURDATE()) > 7) OR (date_next_refill IS NOT NULL AND DATEDIFF(DAY, date_next_refill, CURDATE()) > 7)) ORDER BY current_status";

            jdbcTemplate.query(query, resultSet -> {
                new PatientListBuilder().buildDefaulterList(resultSet);
                defaulterList = new PatientListBuilder().retrieveDefaulterList();
                return null;
            }, (Long) session.getAttribute("facilityId"));
        } catch (Exception exception) {

        }
        return SUCCESS;
    }

    public String updateDefaulterList() {
        // retrieve contact tracking information to be saved from request parameters 
        String patientId = request.getParameter("id");
        String dateTracked = request.getParameter("dateTracked");
        String outcome = request.getParameter("outcome");
        String agreedDate = request.getParameter("agreedDate");

        // retrieve the defaulter list stored as an attribute in session object
        defaulterList = new PatientListBuilder().retrieveDefaulterList();

        // find the target patient and update with values of request parameters
        for (int i = 0; i < defaulterList.size(); i++) {
            String id = (String) defaulterList.get(i).get("patientId"); // retrieve patient id from list
            if (id.equals(patientId)) {
                defaulterList.get(i).remove("dateTracked");
                defaulterList.get(i).put("dateTracked", dateTracked);
                defaulterList.get(i).remove("outcome");
                defaulterList.get(i).put("outcome", outcome);
                defaulterList.get(i).remove(agreedDate);
                defaulterList.get(i).put("agreedDate", agreedDate);
                defaulterList.get(i).remove("updated");
                defaulterList.get(i).put("updated", "true");
            }
        }
        // set contactList as a session-scoped attribute
        session.setAttribute("defaulterList", defaulterList);
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
     * @return the defaulterList
     */
    public ArrayList<Map<String, String>> getDefaulterList() {
        return defaulterList;
    }

    /**
     * @param defaulterList the defaulterList to set
     */
    public void setDefaulterList(ArrayList<Map<String, String>> defaulterList) {
        this.defaulterList = defaulterList;
    }
}
