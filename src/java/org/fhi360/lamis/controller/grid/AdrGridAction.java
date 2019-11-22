/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.controller.grid;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.ActionSupport;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class AdrGridAction extends ActionSupport implements Preparable {

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

    private ArrayList<Map<String, String>> adrList = new ArrayList<>();

    @Override
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
    }

    public String adrGrid() {
        //setTotalpages(1);
        //setCurrpage(1);
        //setTotalrecords(20);
        try {
            // obtain a JDBC connect and execute query

            query = "SELECT * FROM adr";
            jdbcTemplate.query(query, (resultSet) -> {

                // loop through resultSet for each row and put into Map
                while (resultSet.next()) {
                    String adrId = Long.toString(resultSet.getLong("adr_id"));
                    String description = resultSet.getString("description");
                    String severity = "";

                    Map<String, String> map = new HashMap<>();
                    map.put("adrId", adrId);
                    map.put("description", description);
                    map.put("severity", severity);
                    adrList.add(map);
                }
                session.setAttribute("adrList", adrList);
                return null;
            });
        } catch (Exception exception) {

        }
        return SUCCESS;
    }

    public String updateAdrList() {
        // retrieve adr information to be saved from request parameters 
        String adrId = request.getParameter("id");
        String severity = request.getParameter("severity");

        // retrieve the list stored as an attribute in session object
        if (session.getAttribute("adrList") != null) {
            adrList = (ArrayList) session.getAttribute("adrList");
        }

        // find the target element and update with values of request parameters
        for (int i = 0; i < adrList.size(); i++) {
            String id = (String) adrList.get(i).get("adrId"); // retrieve adr id from list
            if (id.equals(adrId)) {
                adrList.get(i).remove("severity");
                adrList.get(i).put("severity", severity);
            }
        }
        // set list as a session-scoped attribute
        session.setAttribute("adrList", adrList);
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
     * @return the adrList
     */
    public ArrayList<Map<String, String>> getAdrList() {
        return adrList;
    }

    /**
     * @param adrList the adrList to set
     */
    public void setAdrList(ArrayList<Map<String, String>> adrList) {
        this.adrList = adrList;
    }
}
