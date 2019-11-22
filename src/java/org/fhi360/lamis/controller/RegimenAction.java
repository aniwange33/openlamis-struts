/**
 *
 * @author AALOZIE
 */
package org.fhi360.lamis.controller;

import java.util.*;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.builder.PharmacyListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;

public class RegimenAction extends ActionSupport implements Preparable {

    private Integer page;
    private Integer rows;
    private String sidx;
    private String sord;
    private Integer totalpages;
    private Integer currpage;
    private Integer totalrecords;

    private String regimentypeId;
    private String regimentype;
    private String regimenId;
    private String regimen;
    private String query;
    private JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private HttpServletRequest request;
    private HttpSession session;

    private Map<String, String> regimenTypeMap = new HashMap<>();
    private Map<String, String> regimenMap = new HashMap<>();
    private Map<String, String> regimenIdMap = new HashMap<>();
    private ArrayList<Map<String, String>> regimenPharmList = new ArrayList<>();

    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
    }

    public String retrieveRegimenTypeById() {
        try {
            query = "SELECT * FROM regimentype ORDER BY description";
            if (ServletActionContext.getRequest().getParameterMap().containsKey("first")) {
                query = "SELECT * FROM regimentype WHERE regimentype_id = 1 OR regimentype_id = 3 ORDER BY description";
            }
            if (ServletActionContext.getRequest().getParameterMap().containsKey("second")) {
                query = "SELECT * FROM regimentype WHERE regimentype_id = 2 OR regimentype_id = 4 ORDER BY description";
            }
            if (ServletActionContext.getRequest().getParameterMap().containsKey("commence")) {
                query = "SELECT * FROM regimentype WHERE regimentype_id <= 4 OR regimentype_id = 14 ORDER BY description";
            }

            jdbcTemplate.query(query, resultSet -> {
                // loop through resultSet for each row and put into Map
                while (resultSet.next()) {
                    String regimentypeId = Long.toString(resultSet.getLong("regimentype_id"));
                    String description = resultSet.getString("description");
                    regimenTypeMap.put(regimentypeId, description);

                }
                return null;
            });
        } catch (Exception exception) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String retrieveRegimenTypeByName() {
        try {

            query = "SELECT * FROM regimentype ORDER BY description";
            if (ServletActionContext.getRequest().getParameterMap().containsKey("first")) {
                query = "SELECT * FROM regimentype WHERE regimentype_id = 1 OR regimentype_id = 3 ORDER BY description";
            }
            if (ServletActionContext.getRequest().getParameterMap().containsKey("second")) {
                query = "SELECT * FROM regimentype WHERE regimentype_id = 2 OR regimentype_id = 4 ORDER BY description";
            }
            if (ServletActionContext.getRequest().getParameterMap().containsKey("commence")) {
                query = "SELECT * FROM regimentype WHERE regimentype_id <= 4 OR regimentype_id = 14 ORDER BY description";
            }
            jdbcTemplate.query(query, resultSet -> {
                // loop through resultSet for each row and put into Map
                while (resultSet.next()) {
                    String description = resultSet.getString("description");
                    regimenTypeMap.put(description, description);
                }
                return null;
            });
        } catch (Exception exception) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String retrieveRegimenById() {
        try {
            String selectedRegimen[] = {""};
            String regimentypeId = request.getParameter("regimentypeId");
            System.out.println("OYISCO " + regimentypeId);
            if (request.getParameterMap().containsKey("selectedRegimen")) {
                selectedRegimen[0] = request.getParameter("selectedRegimen");
            }

            query = "SELECT * FROM regimen WHERE regimentype_id = ? ORDER BY description";

            jdbcTemplate.query(query, resultSet -> {
                try {
                    // loop through resultSet for each row and put into Map
                    new PharmacyListBuilder().buildRegimenList(resultSet, selectedRegimen[0]);
                    regimenPharmList = new PharmacyListBuilder().retrieveRegimenList();
                } catch (Exception ex) {
                    //       Logger.getLogger(RegimenAction.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }, Long.parseLong(regimentypeId));

        } catch (Exception exception) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String retrieveAllRegimen() {
        try {

            query = "SELECT * FROM regimen ORDER BY description";
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String regimenId = Long.toString(resultSet.getInt("regimen_id"));  //Double.toString(double) if it is a double
                    String description = resultSet.getString("description");
                    regimenMap.put(regimenId, description);
                }
                return null;
            });
        } catch (Exception exception) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String retrieveRegimenByName() {
        try {

            query = "SELECT * FROM regimen WHERE regimentype_id = (SELECT DISTINCT regimentype_id FROM regimentype WHERE description = ?) ORDER BY description";

            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String description = resultSet.getString("description");
                    regimenMap.put(description, description);
                }
                return null;
            }, ServletActionContext.getRequest().getParameter("regimentype"));
        } catch (Exception exception) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String retrieveRegimenByIdMap() {
        try {
            String regimentypeId = request.getParameter("regimentypeId");

            query = "SELECT * FROM regimen WHERE regimentype_id =" + regimentypeId + " ORDER BY description";
            jdbcTemplate.query(query, resultSet -> {
                // loop through resultSet for each row and put into Map									   
                while (resultSet.next()) {
                    String regimenId = Long.toString(resultSet.getInt("regimen_id"));  //Double.toString(double) if it is a double
                    String description = resultSet.getString("description");
                    regimenIdMap.put(regimenId, description);

                }
                return null;
            });
        } catch (Exception exception) {
            return ERROR;
        }
        return SUCCESS;
    }

    /**
     * @param regimentypeId the regimentypeId to set
     */
    public void setRegimentypeId(String regimentypeId) {
        this.regimentypeId = regimentypeId;
    }

    /**
     * @return the regimentypeId
     */
    public String getRegimentypeId() {
        return regimentypeId;
    }

    /**
     * @param regimenId the regimenId to set
     */
    public void setRegimenId(String regimenId) {
        this.regimenId = regimenId;
    }

    /**
     * @return the regimenId
     */
    public String getRegimenId() {
        return regimenId;
    }

    /**
     * @return the regimentype
     */
    public String getRegimentype() {
        return regimentype;
    }

    /**
     * @return the regimen
     */
    public String getRegimen() {
        return regimen;
    }

    /**
     * @param regimentype the regimentype to set
     */
    public void setRegimentype(String regimentype) {
        this.regimentype = regimentype;
    }

    /**
     * @param regimen the regimen to set
     */
    public void setRegimen(String regimen) {
        this.regimen = regimen;
    }

    /**
     * @param regimenTypeMap the regimenTypeMap to set
     */
    public void setRegimenTypeMap(Map<String, String> regimenTypeMap) {
        this.regimenTypeMap = regimenTypeMap;
    }

    /**
     * @return the regimenTypeMap
     */
    public Map<String, String> getRegimenTypeMap() {
        return regimenTypeMap;
    }

    public Map<String, String> getRegimenIdMap() {
        return regimenIdMap;
    }

    public void setRegimenIdMap(Map<String, String> regimenIdMap) {
        this.regimenIdMap = regimenIdMap;
    }

    /**
     * @return the regimenMap
     */
    public Map<String, String> getRegimenMap() {
        return regimenMap;
    }

    /**
     * @param regimenMap the regimenMap to set
     */
    public void setRegimenMap(Map<String, String> regimenMap) {
        this.regimenMap = regimenMap;
    }

    public ArrayList<Map<String, String>> getRegimenPharmList() {
        return regimenPharmList;
    }

    public void setRegimenPharmList(ArrayList<Map<String, String>> regimenPharmList) {
        this.regimenPharmList = regimenPharmList;
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
