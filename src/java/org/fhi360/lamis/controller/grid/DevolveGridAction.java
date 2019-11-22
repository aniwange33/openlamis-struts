/**
 *
 * @author AALOZIE
 */
package org.fhi360.lamis.controller.grid;

import static com.opensymphony.xwork2.Action.SUCCESS;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.ActionSupport;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.builder.DevolveListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;

public class DevolveGridAction extends ActionSupport implements Preparable {

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

    private ArrayList<Map<String, String>> devolveList = new ArrayList<>();

    @Override
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
    }

    public String devolveGrid() {
        //setTotalpages(1);
        //setCurrpage(1);
        //setTotalrecords(20);
        try {
            // fetch the required records from the database
            System.out.println("");
            query = "SELECT devolve.*, communitypharm.* FROM devolve LEFT JOIN communitypharm ON devolve.communitypharm_id = communitypharm.communitypharm_id WHERE facility_id = ? AND patient_id = ? ORDER BY date_devolved DESC";
            jdbcTemplate.query(query, resultSet -> {
                new DevolveListBuilder().buildDevolveList(resultSet);
                devolveList = new DevolveListBuilder().retrieveDevolveList();
                return null;
            }, (Long) session.getAttribute("facilityId"), Long.parseLong(request.getParameter("patientId")));

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
     * @return the devolveList
     */
    public ArrayList<Map<String, String>> getDevolveList() {
        return devolveList;
    }

    /**
     * @param devolveList the devolveList to set
     */
    public void setDevolveList(ArrayList<Map<String, String>> devolveList) {
        this.devolveList = devolveList;
    }
}
