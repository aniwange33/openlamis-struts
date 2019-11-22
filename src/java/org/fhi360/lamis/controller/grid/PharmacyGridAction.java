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
import org.fhi360.lamis.utility.DateUtil;
import org.springframework.jdbc.core.JdbcTemplate;

public class PharmacyGridAction extends ActionSupport implements Preparable {

    private Integer page;
    private Integer rows;
    private String sidx;
    private String sord;
    private Integer totalpages;
    private Integer currpage;
    private Integer totalrecords;
    private JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
   
    private HttpServletRequest request;
    private HttpSession session;
    private String query;
 
    private ArrayList<Map<String, String>> pharmacyList = new ArrayList<>();

    @Override
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
    }

    public String pharmacyGrid() {
        //setTotalpages(1);
        //setCurrpage(1);
        //setTotalrecords(20);
        try {
            // fetch the required records from the database

            query = "SELECT pharmacy.pharmacy_id, pharmacy.patient_id, pharmacy.facility_id, pharmacy.date_visit, pharmacy.duration, pharmacy.morning, pharmacy.afternoon, pharmacy.evening, pharmacy.next_appointment, pharmacy.regimentype_id, pharmacy.regimendrug_id, regimendrug.regimen_id, regimendrug.drug_id, drug.name, drug.strength "
                    + " FROM pharmacy JOIN regimendrug ON pharmacy.regimendrug_id = regimendrug.regimendrug_id JOIN drug ON regimendrug.drug_id = drug.drug_id WHERE pharmacy.facility_id = ? AND pharmacy.patient_id = ? ORDER BY pharmacy.date_visit DESC";
           
            jdbcTemplate.query(query, resultSet -> {
               
              while (resultSet.next()){
                    String pharmacyId = Long.toString(resultSet.getLong("pharmacy_id"));
                    String patientId = Long.toString(resultSet.getLong("patient_id"));
                    String facilityId = Long.toString(resultSet.getLong("facility_id"));
                    String dateVisit = DateUtil.parseDateToString(resultSet.getDate("date_visit"), "MM/dd/yyyy");
                    String description = resultSet.getString("name") + " " + resultSet.getString("strength");
                    String duration = Integer.toString(resultSet.getInt("duration"));
                    String nextAppointment = (resultSet.getDate("next_appointment") == null) ? "" : DateUtil.parseDateToString(resultSet.getDate("next_appointment"), "MM/dd/yyyy");

                    Map<String, String> map = new HashMap<>();
                    map.put("pharmacyId", pharmacyId);
                    map.put("patientId", patientId);
                    map.put("facilityId", facilityId);
                    map.put("dateVisit", dateVisit);
                    map.put("description", description);
                    map.put("duration", duration);
                    map.put("nextAppointment", nextAppointment);
                    pharmacyList.add(map);
                }
                return null;
            },(Long) session.getAttribute("facilityId"),Long.parseLong(request.getParameter("patientId")));

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
     * @return the pharmacyList
     */
    public ArrayList<Map<String, String>> getPharmacyList() {
        return pharmacyList;
    }

    /**
     * @param pharmacyList the pharmacyList to set
     */
    public void setPharmacyList(ArrayList<Map<String, String>> pharmacyList) {
        this.pharmacyList = pharmacyList;
    }
}
