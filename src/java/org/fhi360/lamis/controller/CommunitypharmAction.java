/**
 *
 * @author AALOZIE
 */
package org.fhi360.lamis.controller;

import static com.opensymphony.xwork2.Action.SUCCESS;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.ModelDriven;
import org.fhi360.lamis.dao.hibernate.CommunitypharmDAO;
import org.fhi360.lamis.model.Communitypharm;
import org.fhi360.lamis.service.beans.ContextProvider;

import org.fhi360.lamis.utility.builder.CommunitypharmListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class CommunitypharmAction extends ActionSupport implements ModelDriven, Preparable {

    private Long communitypharmId;
    private Communitypharm communitypharm;
    private Set<Communitypharm> communitypharmes = new HashSet<>(0);
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private HttpServletRequest request;
    private HttpSession session;
    private String query;

    private ArrayList<Map<String, String>> pharmList = new ArrayList<>();
    private Map<String, String> pharmMap = new TreeMap<>();

    @Override
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
    }

    @Override
    public Object getModel() {
        communitypharm = new Communitypharm();
        return communitypharm;
    }

    public String saveCommunitypharm() {
        try {
            CommunitypharmDAO.save(communitypharm);
        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String updateCommunitypharm() {
        try {
            CommunitypharmDAO.update(communitypharm);
        } catch (Exception e) {
            
            return ERROR;
        }
        return SUCCESS;
    }

    public String deleteCommunitypharm() {
        try {
            CommunitypharmDAO.delete(communitypharmId);
        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String retrieveCommunitypharm() {

        long stateId = Long.parseLong(ServletActionContext.getRequest().getParameter("stateId"));
        long lgaId = Long.parseLong(ServletActionContext.getRequest().getParameter("lgaId"));
        query = "SELECT * FROM communitypharm WHERE state_id  = " + stateId + " AND lga_id = " + lgaId + " ORDER BY pharmacy";
        try {
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String communitypharmId = Long.toString(resultSet.getLong("communitypharm_id"));
                    String pharmacy = resultSet.getString("pharmacy");
                    pharmMap.put(communitypharmId, pharmacy);
                }
                return null; //To change body of generated lambdas, choose Tools | Templates.
            });

        } catch (Exception exception) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String retrieveCommunitypharmById() {
        query = "SELECT * FROM communitypharm WHERE communitypharm_id  = "
                + Long.parseLong(request.getParameter("communitypharmId"));
        try {
            jdbcTemplate.query(query, resultSet -> {
                new CommunitypharmListBuilder().buildCommunitypharmList(resultSet);
                pharmList = new CommunitypharmListBuilder().retrieveCommunitypharmList();
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR;
        }
        return SUCCESS;
    }

    // Retrieve a Communitypharm in database
    public String findCommunitypharm() {
        try {
            communitypharmId = Long.parseLong(request.getParameter("communitypharmId"));
            communitypharm = CommunitypharmDAO.find(communitypharmId);
        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    /**
     * @return the pharmMap
     */
    public Map<String, String> getPharmMap() {
        return pharmMap;
    }

    /**
     * @param pharmMap the pharmMap to set
     */
    public void setPharmMap(Map<String, String> pharmMap) {
        this.pharmMap = pharmMap;
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
