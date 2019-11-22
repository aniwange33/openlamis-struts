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
import org.fhi360.lamis.utility.Constants;										  
import org.fhi360.lamis.utility.PatientNumberNormalizer;
import org.fhi360.lamis.utility.builder.PatientListBuilder;
import org.fhi360.lamis.utility.Scrambler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class PatientGridAction extends ActionSupport implements Preparable {
    private static final Logger LOG = LoggerFactory.getLogger(PatientGridAction.class);
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
   
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private ArrayList<Map<String, String>> patientList = new ArrayList<>(); 
    
    @Override    
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
    }
    
    public String patientGrid() {
        long facilityId = (Long) session.getAttribute("facilityId");
        Scrambler scrambler = new Scrambler();
        Map<String, Object> pagerParams = new PaginationUtil().paginateGrid(getPage().intValue(), getRows().intValue(), "patient");
        int start = (Integer) pagerParams.get("start");
        int numberOfRows = getRows().intValue();
        try {           
           
            if(request.getParameterMap().containsKey("name")) {
                String name = scrambler.scrambleCharacters(request.getParameter("name"));
                name = name.toUpperCase();
                if(name == null || name.isEmpty()) {
                    query = "SELECT p.*, (select case when count(b.*) > 0 then true else false end from biometric b inner join patient x on x.id_uuid = b.patient_id  where x.patient_id = p.patient_id and x.facility_id = p.facility_id) as biometric"
                            + " FROM patient p WHERE facility_id = " + facilityId + " ORDER BY surname ASC LIMIT " + start + " , " + numberOfRows;                                                         
                    if(request.getParameterMap().containsKey("female")) query = "SELECT p.*, (select case when count(b.*) > 0 then true else false end from biometric b inner join patient x on x.id_uuid = b.patient_id  where x.patient_id = p.patient_id and x.facility_id = p.facility_id) as biometric FROM patient p WHERE facility_id = " + facilityId + " AND gender = 'Female' ORDER BY surname ASC LIMIT " + start + " , " + numberOfRows;                                                         
                }
                else {
                    query = "SELECT p.*, (select case when count(b.*) > 0 then true else false end from biometric b inner join patient x on x.id_uuid = b.patient_id  where x.patient_id = p.patient_id and x.facility_id = p.facility_id) as biometric FROM patient p WHERE facility_id = " + facilityId + " AND UPPER(surname) LIKE '" + name + "%' OR UPPER(other_names) LIKE '" + name + "%' OR UPPER(CONCAT(surname, ' ', other_names)) LIKE '" + name + "%'  OR UPPER(CONCAT(other_names, ' ', surname)) LIKE '" + name + "%'  ORDER BY surname ASC LIMIT " + start + " , " + numberOfRows;                                     
                    if(request.getParameterMap().containsKey("female")) query = "SELECT p.*, (select case when count(b.*) > 0 then true else false end from biometric b inner join patient x on x.id_uuid = b.patient_id  where x.patient_id = p.patient_id and x.facility_id = p.facility_id) as biometric FROM patient p WHERE facility_id = " + facilityId + " AND gender = 'Female' AND UPPER(surname) LIKE '" + name + "%' OR UPPER(other_names) LIKE '" + name + "%' OR UPPER(CONCAT(surname, ' ', other_names)) LIKE '" + name + "%'  OR UPPER(CONCAT(other_names, ' ', surname)) LIKE '" + name + "%' ORDER BY surname ASC LIMIT " + start + " , " + numberOfRows;  
                    if(request.getParameterMap().containsKey("unsuppressed")) query = "SELECT p.*, (select case when count(b.*) > 0 then true else false end from biometric b inner join patient x on x.id_uuid = b.patient_id  where x.patient_id = p.patient_id and x.facility_id = p.facility_id) as biometric FROM patient p WHERE facility_id = " + facilityId + " AND current_status IN ('ART Start', 'ART Restart', 'ART Transfer In') AND last_viral_load >=1000 AND UPPER(surname) LIKE '" + name + "%' OR UPPER(other_names) LIKE '" + name + "%' OR UPPER(CONCAT(surname, ' ', other_names)) LIKE '" + name + "%'  OR UPPER(CONCAT(other_names, ' ', surname)) LIKE '" + name + "%' ORDER BY surname ASC LIMIT " + start + " , " + numberOfRows;  
                }

            }
            else {
                query = "SELECT p.*,(select case when count(b.*) > 0 then true else false end from biometric b inner join patient x on x.id_uuid = b.patient_id  where x.patient_id = p.patient_id and x.facility_id = p.facility_id) as biometric FROM patient p WHERE facility_id = " + facilityId + " ORDER BY surname ASC LIMIT " + start + " , " + numberOfRows;                 
                if(request.getParameterMap().containsKey("female")) query = "SELECT p.*,(select case when count(b.*) > 0 then true else false end from biometric b inner join patient x on x.id_uuid = b.patient_id  where x.patient_id = p.patient_id and x.facility_id = p.facility_id) as biometric FROM patient p WHERE facility_id = " + facilityId + " AND gender = 'Female' ORDER BY surname ASC LIMIT " + start + " , " + numberOfRows;                 
                if(request.getParameterMap().containsKey("unsuppressed")) query = "SELECT p.*,(select case when count(b.*) > 0 then true else false end from biometric b inner join patient x on x.id_uuid = b.patient_id  where x.patient_id = p.patient_id and x.facility_id = p.facility_id) as biometric FROM patient p WHERE facility_id = " + facilityId + " AND current_status IN ('ART Start', 'ART Restart', 'ART Transfer In') AND last_viral_load >=1000 ORDER BY surname ASC LIMIT " + start + " , " + numberOfRows;
            }
            LOG.info("Query 1: {}", query);
             jdbcTemplate.query(query, resultSet -> {
            
                   new PatientListBuilder().buildPatientListSorted(resultSet);
                    patientList = new PatientListBuilder().retrievePatientList();
              return null;
              });
            
           
        }
        catch (Exception exception) {
            exception.printStackTrace();
          
        }
        
        page = (Integer) pagerParams.get("page");
        currpage = (Integer) pagerParams.get("page");
        totalpages = (Integer) pagerParams.get("totalpages");
        totalrecords = (Integer) pagerParams.get("totalrecords");
        return SUCCESS;
    }
    
	public String patientDrugDispenseGrid() {
        long facilityId = (Long) session.getAttribute("facilityId");
        Scrambler scrambler = new Scrambler();
        Map<String, Object> pagerParams = new PaginationUtil().paginateGrid(getPage().intValue(), getRows().intValue(), "patient");
        int start = (Integer) pagerParams.get("start");
        int numberOfRows = getRows().intValue();
        try {           
            
            if(request.getParameterMap().containsKey("name")) {
                String name = scrambler.scrambleCharacters(request.getParameter("name"));
                if(name == null || name.isEmpty()) {
                    query = "SELECT p.*, (select case when count(b.*) > 0 then true else false end from biometric b inner join patient x on x.id_uuid = b.patient_id  where x.patient_id = p.patient_id and x.facility_id = p.facility_id) as biometric FROM patient p JOIN prescription pr ON p.patient_id = pr.patient_id WHERE p.facility_id = " + facilityId + " AND pr.status = " + Constants.Prescription.PRESCRIBED + " AND prescription_type = 'drug' ORDER BY p.surname ASC LIMIT " + start + " , " + numberOfRows;                                                         
                }
                else {
                    query = "SELECT p.*, (select case when count(b.*) > 0 then true else false end from biometric b inner join patient x on x.id_uuid = b.patient_id  where x.patient_id = p.patient_id and x.facility_id = p.facility_id) as biometric FROM patient p JOIN prescription pr ON p.patient_id = pr.patient_id WHERE p.facility_id = " + facilityId + " AND p.surname LIKE '"+name+"%' OR p.other_names LIKE '"+name+"%' AND pr.status = " + Constants.Prescription.PRESCRIBED + " AND prescription_type = 'drug' ORDER BY p.surname ASC LIMIT " + start + " , " + numberOfRows;                                     
                }
            }
            else {
                query = "SELECT p.*, (select case when count(b.*) > 0 then true else false end from biometric b inner join patient x on x.id_uuid = b.patient_id  where x.patient_id = p.patient_id and x.facility_id = p.facility_id) as biometric FROM patient p JOIN prescription pr ON p.patient_id = pr.patient_id WHERE p.facility_id = " + facilityId + " AND pr.status = " + Constants.Prescription.PRESCRIBED + " AND prescription_type = 'drug' ORDER BY p.surname ASC LIMIT " + start + " , " + numberOfRows;                 
            }
        
              jdbcTemplate.query(query, resultSet -> {
                  new PatientListBuilder().buildPatientListSorted(resultSet);
            patientList = new PatientListBuilder().retrievePatientList();
            return null;
              });
            
           
        }
        catch (Exception exception) {
        
        }
        
        page = (Integer) pagerParams.get("page");
        currpage = (Integer) pagerParams.get("page");
        totalpages = (Integer) pagerParams.get("totalpages");
        totalrecords = (Integer) pagerParams.get("totalrecords");
        return SUCCESS;
    }
    
    public String patientLabtestDispenseGrid() {
        long facilityId = (Long) session.getAttribute("facilityId");
        Scrambler scrambler = new Scrambler();
        Map<String, Object> pagerParams = new PaginationUtil().paginateGrid(getPage().intValue(), getRows().intValue(), "patient");
        int start = (Integer) pagerParams.get("start");
        int numberOfRows = getRows().intValue();
        try {              
            if(request.getParameterMap().containsKey("name")) {
                String name = scrambler.scrambleCharacters(request.getParameter("name"));
                if(name == null || name.isEmpty()) {
                    query = "SELECT p.*, (select case when count(b.*) > 0 then true else false end from biometric b inner join patient x on x.id_uuid = b.patient_id  where x.patient_id = p.patient_id and x.facility_id = p.facility_id) as biometric FROM patient p JOIN prescription pr ON p.patient_id = pr.patient_id WHERE p.facility_id = " + facilityId + " AND pr.status = " + Constants.Prescription.PRESCRIBED + " AND prescription_type = 'labtest' ORDER BY p.surname ASC LIMIT " + start + " , " + numberOfRows;                                                         
                }
                else {
                    query = "SELECT p.*, (select case when count(b.*) > 0 then true else false end from biometric b inner join patient x on x.id_uuid = b.patient_id  where x.patient_id = p.patient_id and x.facility_id = p.facility_id) as biometric FROM patient p JOIN prescription pr ON p.patient_id = pr.patient_id WHERE p.facility_id = " + facilityId + " AND p.surname LIKE '"+name+"%' OR p.other_names LIKE '"+name+"%' AND pr.status = " + Constants.Prescription.PRESCRIBED + " AND prescription_type = 'labtest' ORDER BY p.surname ASC LIMIT " + start + " , " + numberOfRows;                                     
                }
            }
            else {
                query = "SELECT p.*, (select case when count(b.*) > 0 then true else false end from biometric b inner join patient x on x.id_uuid = b.patient_id  where x.patient_id = p.patient_id and x.facility_id = p.facility_id) as biometric FROM patient p JOIN prescription pr ON p.patient_id = pr.patient_id WHERE p.facility_id = " + facilityId + " AND pr.status = " + Constants.Prescription.PRESCRIBED + " AND prescription_type = 'labtest' ORDER BY p.surname ASC LIMIT " + start + " , " + numberOfRows;                 
            }
      
                 jdbcTemplate.query(query, resultSet -> {
                      new PatientListBuilder().buildPatientListSorted(resultSet);
            patientList = new PatientListBuilder().retrievePatientList();
            return null;
                 });
           
        }
        catch (Exception exception) {
           
        }
        
        page = (Integer) pagerParams.get("page");
        currpage = (Integer) pagerParams.get("page");
        totalpages = (Integer) pagerParams.get("totalpages");
        totalrecords = (Integer) pagerParams.get("totalrecords");
        return SUCCESS;
    }
    
    public String patientGridSearch() {
        long facilityId = (Long) session.getAttribute("facilityId");
        Scrambler scrambler = new Scrambler();
       
        Map<String, Object> pagerParams = new PaginationUtil().paginateGrid(getPage().intValue(), getRows().intValue(), "patient");
        int start = (Integer) pagerParams.get("start");
        int numberOfRows = getRows().intValue();
        try {           
           
            if(request.getParameterMap().containsKey("value")) {
                String otherNames = scrambler.scrambleCharacters(request.getParameter("value"));
                String surname = otherNames.toUpperCase();
                String hospitalNum =  request.getParameter("value");
                
                if(request.getParameter("value") == null || request.getParameter("value").isEmpty()) {
                    query = "SELECT p.*, (select case when count(b.*) > 0 then true else false end from biometric b inner join patient x on x.id_uuid = b.patient_id  where x.patient_id = p.patient_id and x.facility_id = p.facility_id) as biometric FROM patient p WHERE facility_id = " + facilityId + " ORDER BY time_stamp DESC LIMIT " + start + " , " + numberOfRows;                                                         
                    if(request.getParameterMap().containsKey("female")) query = "SELECT p.*, (select case when count(b.*) > 0 then true else false end from biometric b inner join patient x on x.id_uuid = b.patient_id  where x.patient_id = p.patient_id and x.facility_id = p.facility_id) as biometric FROM patient p WHERE facility_id = " + facilityId + " AND gender = 'Female' ORDER BY time_stamp DESC LIMIT " + start + " , " + numberOfRows;                                                         
                }
                else {
                    query = "SELECT p.*, (select case when count(b.*) > 0 then true else false end from biometric b inner join patient x on x.id_uuid = b.patient_id  where x.patient_id = p.patient_id and x.facility_id = p.facility_id) as biometric FROM patient p WHERE facility_id = " + facilityId + " AND surname LIKE '"+surname+"%' OR other_names LIKE '"+otherNames+"%' OR TRIM(LEADING '0' FROM hospital_num) LIKE '" + PatientNumberNormalizer.unpadNumber(hospitalNum) + "%'  ORDER BY current_status LIMIT " + start + " , " + numberOfRows;                                     
                    if(request.getParameterMap().containsKey("female")) query = "SELECT p.*, (select case when count(b.*) > 0 then true else false end from biometric b inner join patient x on x.id_uuid = b.patient_id  where x.patient_id = p.patient_id and x.facility_id = p.facility_id) as biometric FROM patient p WHERE facility_id = " + facilityId + " AND gender = 'Female' AND surname LIKE '"+surname+"%' OR other_names LIKE '"+otherNames+"%' OR TRIM(LEADING '0' FROM hospital_num) LIKE '" + PatientNumberNormalizer.unpadNumber(hospitalNum) + "%'  ORDER BY current_status LIMIT " + start + " , " + numberOfRows;                                     
                }

            }
            else {
                query = "SELECT p.*, (select case when count(b.*) > 0 then true else false end from biometric b inner join patient x on x.id_uuid = b.patient_id  where x.patient_id = p.patient_id and x.facility_id = p.facility_id) as biometric FROM patient p WHERE facility_id = " + facilityId + " ORDER BY time_stamp DESC LIMIT " + start + " , " + numberOfRows;                 
                if(request.getParameterMap().containsKey("female")) query = "SELECT p.*, (select case when count(b.*) > 0 then true else false end from biometric b inner join patient x on x.id_uuid = b.patient_id  where x.patient_id = p.patient_id and x.facility_id = p.facility_id) as biometric FROM patient p WHERE facility_id = " + facilityId + " AND gender = 'Female' ORDER BY time_stamp DESC LIMIT " + start + " , " + numberOfRows;                 
            }
            LOG.info("Query: {}", query);
      
               jdbcTemplate.query(query, resultSet -> {
                        new PatientListBuilder().buildPatientList(resultSet, "");
            patientList = new PatientListBuilder().retrievePatientList();
            return null;
               });
       
        }
        catch (Exception exception) {
            LOG.info("Exception: {}", exception);
         
            exception.printStackTrace();
        }
        
        page = (Integer) pagerParams.get("page");
        currpage = (Integer) pagerParams.get("page");
        totalpages = (Integer) pagerParams.get("totalpages");
        totalrecords = (Integer) pagerParams.get("totalrecords");
        return SUCCESS;
    }

    public String patientGridByNumber(){
        long facilityId = (Long) session.getAttribute("facilityId");
        String hospitalNum = request.getParameter("hospitalNum");

        Map<String, Object> pagerParams = new PaginationUtil().paginateGrid(getPage().intValue(), getRows().intValue(), "patient");
        int start = (Integer) pagerParams.get("start");
        int numberOfRows = getRows().intValue();
        try {
            // fetch the required records from the database
          
            if(hospitalNum == null || hospitalNum.isEmpty()) {
                query = "SELECT p.*, (select case when count(b.*) > 0 then true else false end from biometric b inner join patient x on x.id_uuid = b.patient_id  where x.patient_id = p.patient_id and x.facility_id = p.facility_id) as biometric FROM patient p WHERE facility_id = " + facilityId + " ORDER BY current_status ASC LIMIT " + start + " , " + numberOfRows;                                                         
            }
            else {
                query = "SELECT p.*, (select case when count(b.*) > 0 then true else false end from biometric b inner join patient x on x.id_uuid = b.patient_id  where x.patient_id = p.patient_id and x.facility_id = p.facility_id) as biometric FROM patient p WHERE facility_id = " + facilityId + " AND TRIM(LEADING '0' FROM hospital_num) LIKE '" + PatientNumberNormalizer.unpadNumber(hospitalNum) + "%' ORDER BY current_status ASC LIMIT " + start + " , " + numberOfRows; 
            }
       
             jdbcTemplate.query(query, resultSet -> {
                 new PatientListBuilder().buildPatientListSorted(resultSet);
            patientList = new PatientListBuilder().retrievePatientList();
                 return null;
             });
        }
        catch (Exception exception) {
           
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

    /**`nmkl;'
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
    public ArrayList<Map<String, String>> getPatientList() {
        return patientList;
    }

    /**
     * @param patientList the patientList to set
     */
    public void setPatientList(ArrayList<Map<String, String>> patientList) {
        this.patientList = patientList;
    }
}

//                    query = "SELECT * FROM patient WHERE facility_id = " + facilityId + " AND surname LIKE '"+name+"%' UNION ALL SELECT * FROM patient WHERE facility_id = " + facilityId + " AND UPPER(other_names) LIKE '"+name+"%' ORDER BY current_status LIMIT " + start + " , " + numberOfRows;                                     
