/**
 *
 * @author AALOZIE
 */
package org.fhi360.lamis.controller.grid;

import java.util.*;
import com.opensymphony.xwork2.ActionSupport;
import org.fhi360.lamis.service.beans.ContextProvider;

import org.fhi360.lamis.utility.builder.ParticipantListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class ParticipantGridAction extends ActionSupport {

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

    private ArrayList<Map<String, String>> participantList = new ArrayList<>();

    public String participantGrid() {
        //setTotalpages(1);
        //setCurrpage(1);
        //setTotalrecords(20);
        try {
            query = "SELECT phone, MAX(time_stamp) AS time_stamp FROM conversation WHERE originator_id = 0 GROUP BY phone ORDER BY time_stamp DESC";
            jdbcTemplate.query(query, resultSet -> {
                new ParticipantListBuilder().buildList(resultSet);
                participantList = new ParticipantListBuilder().retrieveList();
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
     * @return the conversationList
     */
    public ArrayList<Map<String, String>> getParticipantList() {
        return participantList;
    }

    /**
     * @param conversationList the conversationList to set
     */
    public void setParticipantList(ArrayList<Map<String, String>> participantList) {
        this.participantList = participantList;
    }
}
