/**
 *
 * @author AALOZIE
 */
package org.fhi360.lamis.controller.grid;

import static com.opensymphony.xwork2.Action.SUCCESS;
import java.util.*;
import com.opensymphony.xwork2.ActionSupport;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.builder.CommunitypharmListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;

public class CommunitypharmGridAction extends ActionSupport {

    private Integer page;
    private Integer rows;
    private String sidx;
    private String sord;
    private Integer totalpages;
    private Integer currpage;
    private Integer totalrecords;

    private String query;
    private JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private HttpServletRequest request;

    private ArrayList<Map<String, String>> pharmList = new ArrayList<Map<String, String>>();

    public String communitypharmGrid() {
        //setTotalpages(1);
        //setCurrpage(1);
        //setTotalrecords(20);
        int level[] = {0};

        request = ServletActionContext.getRequest();
        try {
            query = "SELECT * FROM communitypharm";
            if (request.getParameterMap().containsKey("stateId")) {
                if (request.getParameterMap().containsKey("lgaId")) {
                    level[0] = 2;
                    query = "SELECT * FROM communitypharm WHERE state_id = ? AND lga_id = ?";
                } else {
                    level[0] = 1;
                    query = "SELECT * FROM communitypharm WHERE state_id = ?";
                }
            }
            Long stateId[] = {0L};
            Long lgaId[] = {0L};
            jdbcTemplate.query(query, resultSet -> {

                if (level[0] == 1) {
                    stateId[0] = Long.parseLong(request.getParameter("stateId"));
                }
                if (level[0] == 2) {
                    stateId[0] = Long.parseLong(request.getParameter("stateId"));
                    lgaId[0] = Long.parseLong(request.getParameter("lgaId"));
                }

                new CommunitypharmListBuilder().buildCommunitypharmList(resultSet);
                pharmList = new CommunitypharmListBuilder().retrieveCommunitypharmList();
                return null;
            }, stateId[0], lgaId[0]);

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
     * @return the pharmList
     */
    public ArrayList<Map<String, String>> getPharmList() {
        return pharmList;
    }

    /**
     * @param pharmList the pharmList to set
     */
    public void setPharmList(ArrayList<Map<String, String>> pharmList) {
        this.pharmList = pharmList;
    }
}
