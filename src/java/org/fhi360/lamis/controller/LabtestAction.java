/**
 *
 * @author AALOZIE
 */
package org.fhi360.lamis.controller;

import java.util.*;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.builder.LaboratoryListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;

public class LabtestAction extends ActionSupport implements Preparable {

    private Integer page;
    private Integer rows;
    private String sidx;
    private String sord;
    private Integer totalpages;
    private Integer currpage;
    private Integer totalrecords;

    private HttpServletRequest request;
    private HttpSession session;
    private JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    private Map<String, String> labtestMap = new HashMap<>();
    private ArrayList<Map<String, String>> labtestList = new ArrayList<>();

    @Override
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
    }

    public String retrieveLabtest() {
        try {
            String query = "SELECT * FROM labtest ORDER BY description";
            String selectedTest[] = {""};
            if (request.getParameterMap().containsKey("selectedLabtest")) {
                selectedTest[0] = request.getParameter("selectedLabtest");
            }
            jdbcTemplate.query(query, resultSet -> {
                try {
                    new LaboratoryListBuilder().buildLabTestList(resultSet, selectedTest[0]);
                    labtestList = new LaboratoryListBuilder().retrieveLabTestList();
                } catch (Exception ex) {
                    Logger.getLogger(LabtestAction.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            });

        } catch (Exception exception) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String retrieveLabtestMap() {
        try {
            String query = "SELECT * FROM labtest ORDER BY description";
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String labtestId = Long.toString(resultSet.getLong("labtest_id"));  //Double.toString(double) if it is a double
                    String description = resultSet.getString("description");
                    labtestMap.put(labtestId, description);
                }

                setLabtestMap(labtestMap);
                return null;
            });

        } catch (Exception exception) {
            exception.printStackTrace();
            return ERROR;
        }
        return SUCCESS;
    }

    /**
     * @return the labtestMap
     */
    public Map<String, String> getLabtestMap() {
        return labtestMap;
    }

    /**
     * @param labtestMap the labtestMap to set
     */
    public void setLabtestMap(Map<String, String> labtestMap) {
        this.labtestMap = labtestMap;
    }

    public ArrayList<Map<String, String>> getLabtestList() {
        return labtestList;
    }

    public void setLabtestList(ArrayList<Map<String, String>> labtestList) {
        this.labtestList = labtestList;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public String getSidx() {
        return sidx;
    }

    public void setSidx(String sidx) {
        this.sidx = sidx;
    }

    public String getSord() {
        return sord;
    }

    public void setSord(String sord) {
        this.sord = sord;
    }

    public Integer getTotalpages() {
        return totalpages;
    }

    public void setTotalpages(Integer totalpages) {
        this.totalpages = totalpages;
    }

    public Integer getCurrpage() {
        return currpage;
    }

    public void setCurrpage(Integer currpage) {
        this.currpage = currpage;
    }

    public Integer getTotalrecords() {
        return totalrecords;
    }

    public void setTotalrecords(Integer totalrecords) {
        this.totalrecords = totalrecords;
    }

}
