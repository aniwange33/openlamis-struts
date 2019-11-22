/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.controller.grid;

import static com.opensymphony.xwork2.Action.SUCCESS;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.utility.Constants;
import org.fhi360.lamis.utility.JDBCUtil;
import org.fhi360.lamis.utility.PatientNumberNormalizer;
import org.fhi360.lamis.utility.Scrambler;
import org.fhi360.lamis.utility.builder.MotherInformationListBuilder;
import org.fhi360.lamis.utility.builder.PatientListBuilder;

/**
 *
 * @author user10
 */
public class MotherInformationGridAction extends ActionSupport implements Preparable {
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
    private JDBCUtil jdbcUtil;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    
    private ArrayList<Map<String, String>> motherList = new ArrayList<>();
    
    @Override    
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
    }
    
    public String motherGrid() {
        long facilityId = (Long) session.getAttribute("facilityId");
        Scrambler scrambler = new Scrambler();
        Map<String, Object> pagerParams = new PaginationUtil().paginateGrid(getPage().intValue(), getRows().intValue(), "patient");
        int start = (Integer) pagerParams.get("start");
        int numberOfRows = getRows().intValue();
        try {           
            jdbcUtil = new JDBCUtil();
            if(request.getParameterMap().containsKey("name")) {
                String name = scrambler.scrambleCharacters(request.getParameter("name"));
                name = name.toUpperCase();
                if(name == null || name.isEmpty()) {
                    query = "SELECT * FROM motherinformation WHERE facility_id = " + facilityId + " ORDER BY surname ASC LIMIT " + start + " , " + numberOfRows;                                                         
                }
                else {
                    query = "SELECT * FROM motherinformation WHERE facility_id = " + facilityId + " AND UPPER(surname) LIKE '" + name + "%' OR UPPER(other_names) LIKE '" + name + "%' OR UPPER(CONCAT(surname, ' ', other_names)) LIKE '" + name + "%'  OR UPPER(CONCAT(other_names, ' ', surname)) LIKE '" + name + "%' ORDER BY surname ASC LIMIT " + start + " , " + numberOfRows;  
                }
            }
            else {
                query = "SELECT * FROM motherinformation WHERE facility_id = " + facilityId + " ORDER BY surname ASC LIMIT " + start + " , " + numberOfRows;                 
                System.out.println("....."+query);
            }
            preparedStatement = jdbcUtil.getStatement(query);
            resultSet = preparedStatement.executeQuery();
            
            new MotherInformationListBuilder().buildMotherListSorted(resultSet);
            motherList = new MotherInformationListBuilder().retrieveMotherList();
            System.out.println("MotherList is: "+motherList);
            resultSet = null;
        }
        catch (Exception exception) {
            resultSet = null;
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }
        
        page = (Integer) pagerParams.get("page");
        currpage = (Integer) pagerParams.get("page");
        totalpages = (Integer) pagerParams.get("totalpages");
        totalrecords = (Integer) pagerParams.get("totalrecords");
        return SUCCESS;
    }
    
    public String motherGridAnc() {
        long facilityId = (Long) session.getAttribute("facilityId");
        Scrambler scrambler = new Scrambler();
        Map<String, Object> pagerParams = new PaginationUtil().paginateGrid(getPage().intValue(), getRows().intValue(), "patient");
        int start = (Integer) pagerParams.get("start");
        int numberOfRows = getRows().intValue();
        try {           
            jdbcUtil = new JDBCUtil();
            if(request.getParameterMap().containsKey("name")) {
                String name = scrambler.scrambleCharacters(request.getParameter("name"));
                name = name.toUpperCase();
                if(name == null || name.isEmpty()) {
                    query = "SELECT * FROM motherinformation WHERE facility_id = " + facilityId + " AND in_facility = 'Yes' ORDER BY surname ASC LIMIT " + start + " , " + numberOfRows;                                                         
                }
                else {
                    query = "SELECT * FROM motherinformation WHERE facility_id = " + facilityId + " AND in_facility = 'Yes' AND UPPER(surname) LIKE '" + name + "%' OR UPPER(other_names) LIKE '" + name + "%' OR UPPER(CONCAT(surname, ' ', other_names)) LIKE '" + name + "%'  OR UPPER(CONCAT(other_names, ' ', surname)) LIKE '" + name + "%' ORDER BY surname ASC LIMIT " + start + " , " + numberOfRows;  
                }
            }
            else {
                query = "SELECT * FROM motherinformation WHERE facility_id = " + facilityId + " AND in_facility = 'Yes' ORDER BY surname ASC LIMIT " + start + " , " + numberOfRows;                 
                System.out.println("....."+query);
            }
            preparedStatement = jdbcUtil.getStatement(query);
            resultSet = preparedStatement.executeQuery();
            
            new MotherInformationListBuilder().buildMotherListSorted(resultSet);
            motherList = new MotherInformationListBuilder().retrieveMotherList();
            System.out.println("MotherList is: "+motherList);
            resultSet = null;
        }
        catch (Exception exception) {
            resultSet = null;
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }
        
        page = (Integer) pagerParams.get("page");
        currpage = (Integer) pagerParams.get("page");
        totalpages = (Integer) pagerParams.get("totalpages");
        totalrecords = (Integer) pagerParams.get("totalrecords");
        return SUCCESS;
    }
    
    public String motherGridByNumber(){
        long facilityId = (Long) session.getAttribute("facilityId");
        String hospitalNum = request.getParameter("hospitalNum");

        Map<String, Object> pagerParams = new PaginationUtil().paginateGrid(getPage().intValue(), getRows().intValue(), "patient");
        int start = (Integer) pagerParams.get("start");
        int numberOfRows = getRows().intValue();
        try {
            // fetch the required records from the database
            jdbcUtil = new JDBCUtil();
            if(hospitalNum == null || hospitalNum.isEmpty()) {
                query = "SELECT * FROM motherinformation WHERE facility_id = " + facilityId + " ORDER BY current_status ASC LIMIT " + start + " , " + numberOfRows;                                                         
            }
            else {
                query = "SELECT * FROM motherinformation WHERE facility_id = " + facilityId + " AND TRIM(LEADING '0' FROM hospital_num) LIKE '" + PatientNumberNormalizer.unpadNumber(hospitalNum) + "%' ORDER BY current_status ASC LIMIT " + start + " , " + numberOfRows; 
            }
            preparedStatement = jdbcUtil.getStatement(query);
            resultSet = preparedStatement.executeQuery();
            
            new MotherInformationListBuilder().buildMotherListSorted(resultSet);
            motherList = new MotherInformationListBuilder().retrieveMotherList();
            resultSet = null;
        }
        catch (Exception exception) {
            resultSet = null;
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }        
        page = (Integer) pagerParams.get("page");
        currpage = (Integer) pagerParams.get("page");
        totalpages = (Integer) pagerParams.get("totalpages");
        totalrecords = (Integer) pagerParams.get("totalrecords");
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
     * @return the patientList
     */
    public ArrayList<Map<String, String>> getMotherList() {
        return motherList;
    }

    /**
     * @param motherList the patientList to set
     */
    public void setMotherList(ArrayList<Map<String, String>> motherList) {
        this.motherList = motherList;
    }
    
}
