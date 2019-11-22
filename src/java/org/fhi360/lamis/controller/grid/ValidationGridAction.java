/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.controller.grid;

import static com.opensymphony.xwork2.Action.SUCCESS;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import java.util.ArrayList;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;

import org.fhi360.lamis.utility.builder.ValidationListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author user10
 */
public class ValidationGridAction extends ActionSupport implements Preparable {

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

    long facilityId;
    private ArrayList<Map<String, String>> validationList = new ArrayList<>();
    private ArrayList<Map<String, String>> entityList = new ArrayList<>();
    private String year, month;
    private int entity_id;

    @Override
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();

        //Get the parameters from the request.
        year = request.getParameter("year");
        month = request.getParameter("month");

        if (year == null) {
            year = (String) session.getAttribute("year");
        }
        if (month == null) {
            month = (String) session.getAttribute("month");
        }

        //Set the year and month in the session...
        if (session.getAttribute("year") != year) {
            session.setAttribute("year", year);
        }
        if (session.getAttribute("month") != month) {
            session.setAttribute("month", month);
        }
        facilityId = (Long) session.getAttribute("facilityId");

        //System.out.println("Year: "+year+" and month: "+month+" Facility ID: "+facilityId);
    }

    public String validationGrid() {
        try {
            facilityId = (Long) session.getAttribute("facilityId");
            String general_temp_drop = "DROP TABLE general_temp IF EXISTS";

            transactionTemplate.execute((ts) -> {
                jdbcTemplate.execute(general_temp_drop);
                return null; //To change body of generated lambdas, choose Tools | Templates.
            });

            String general_temp = "CREATE TEMPORARY TABLE IF NOT EXISTS general_temp (id INT, entity TEXT, entity_count INTEGER)";

            transactionTemplate.execute((ts) -> {
                jdbcTemplate.execute(general_temp);
                return null; //To change body of generated lambdas, choose Tools | Templates.
            });

            //ENROLMENTS                
            String query_temp_reg = "INSERT INTO general_temp(id, entity, entity_count) "
                    + "SELECT 1, 'Number of enrolment records this month', COUNT(DISTINCT patient.patient_id) AS reg_count FROM patient WHERE YEAR(patient.time_stamp) = '" + year + "' AND MONTH(patient.time_stamp) = '" + month + "' AND patient.facility_id = '" + facilityId + "'";

            transactionTemplate.execute((ts) -> {
                jdbcTemplate.execute(query_temp_reg);
                return null; //To change body of generated lambdas, choose Tools | Templates.
            });

            //CLINIC
            String query_temp_clinic = "INSERT INTO general_temp(id, entity, entity_count) "
                    + "SELECT 2, 'Number of clinic Records this month', COUNT(DISTINCT clinic.patient_id) AS reg_count FROM clinic WHERE YEAR(clinic.time_stamp) = '" + year + "' AND MONTH(clinic.time_stamp) = '" + month + "' AND clinic.facility_id = '" + facilityId + "'";

            transactionTemplate.execute((ts) -> {
                jdbcTemplate.execute(query_temp_clinic);
                return null; //To change body of generated lambdas, choose Tools | Templates.
            });

            //PHARMACY
            String query_temp_pharmacy = "INSERT INTO general_temp(id, entity, entity_count) "
                    + "SELECT 3, 'Number of pharmacy records this month', COUNT(DISTINCT CONCAT(pharmacy.date_visit, patient.patient_id)) AS reg_count FROM pharmacy JOIN patient ON pharmacy.patient_id = patient.patient_id WHERE YEAR(pharmacy.time_stamp) = '" + year + "' AND MONTH(pharmacy.time_stamp) = '" + month + "' AND pharmacy.facility_id = '" + facilityId + "'";

            transactionTemplate.execute((ts) -> {
                jdbcTemplate.execute(query_temp_pharmacy);
                return null; //To change body of generated lambdas, choose Tools | Templates.
            });

            //LABORATORY
            String query_temp_laboratory = "INSERT INTO general_temp(id, entity, entity_count) "
                    + "SELECT 4, 'Number of laboratory records this month', COUNT(DISTINCT CONCAT(laboratory.date_reported, patient.patient_id)) AS reg_count FROM laboratory JOIN patient ON laboratory.patient_id = patient.patient_id WHERE YEAR(laboratory.time_stamp) = '" + year + "' AND MONTH(laboratory.time_stamp) = '" + month + "' AND laboratory.facility_id = '" + facilityId + "'";

            transactionTemplate.execute((ts) -> {
                jdbcTemplate.execute(query_temp_laboratory);
                return null; //To change body of generated lambdas, choose Tools | Templates.
            });

            //The global table...
            query = "SELECT * from general_temp";

            jdbcTemplate.query(query, resultSet -> {
                new ValidationListBuilder().buildValidationList(resultSet);
                validationList = new ValidationListBuilder().retrieveValidationList();
                return null;
            });

        } catch (Exception exception) {

            exception.printStackTrace();
        }
        return SUCCESS;
    }

    public String entityGrid() {

        //Get the entity selected and sample from it.
        String entity_year = (String) session.getAttribute("year");
        String entity_month = (String) session.getAttribute("month");
        entity_id = Integer.parseInt(request.getParameter("entity"));

        if (session.getAttribute("entity_id") != null) {
            if ((int) session.getAttribute("entity_id") != entity_id) {
                session.setAttribute("entity_id", entity_id);
            }
        } else {
            session.setAttribute("entity_id", entity_id);
        }

        //System.out.println("Entity ID in session is: "+session.getAttribute("entity_id"));
        try {
            //Get the Enrolment Uploads...
            if (entity_id == 1) {
                query = "SELECT patient_id, surname, other_names, hospital_num, date_current_status AS report_date, current_status, fullname FROM patient JOIN user ON user.user_id = patient.user_id WHERE YEAR(patient.time_stamp) = '" + entity_year + "' AND MONTH(patient.time_stamp) = '" + entity_month + "' AND patient.facility_id = '" + facilityId + "'";
            } //Get the Clinic Uploads...
            else if (entity_id == 2) {
                query = "SELECT patient.surname AS surname, patient.patient_id AS patient_id, patient.other_names AS other_names, patient.hospital_num AS hospital_num, clinic.date_visit AS report_date, patient.current_status AS current_status, fullname FROM patient JOIN clinic ON patient.patient_id = clinic.patient_id JOIN user ON user.user_id = clinic.user_id WHERE YEAR(clinic.time_stamp) = '" + entity_year + "' AND MONTH(clinic.time_stamp) = '" + entity_month + "' AND clinic.facility_id = '" + facilityId + "'";
            } //Get the Pharmacy Uploads...
            else if (entity_id == 3) {
                query = "SELECT DISTINCT pharmacy.date_visit, patient.patient_id AS patient_id, patient.surname AS surname, patient.other_names AS other_names, patient.hospital_num AS hospital_num, pharmacy.date_visit AS report_date, patient.current_status AS current_status, fullname FROM patient JOIN pharmacy ON patient.patient_id = pharmacy.patient_id JOIN user ON user.user_id = pharmacy.user_id WHERE YEAR(pharmacy.time_stamp) = '" + entity_year + "' AND MONTH(pharmacy.time_stamp) = '" + entity_month + "' AND pharmacy.facility_id = '" + facilityId + "'";
            } //Get the Laboratory Uploads...
            else if (entity_id == 4) {
                query = "SELECT DISTINCT laboratory.date_reported, patient.patient_id AS patient_id, patient.surname AS surname, patient.other_names AS other_names, patient.hospital_num AS hospital_num, laboratory.date_reported AS report_date, patient.current_status AS current_status, fullname FROM patient JOIN laboratory ON patient.patient_id = laboratory.patient_id JOIN user ON user.user_id = laboratory.user_id WHERE YEAR(laboratory.time_stamp) = '" + entity_year + "' AND MONTH(laboratory.time_stamp) = '" + entity_month + "' AND laboratory.facility_id = '" + facilityId + "'";
            }

            jdbcTemplate.query(query, resultSet -> {
                new ValidationListBuilder().buildEntityList(resultSet);
                entityList = new ValidationListBuilder().retrieveEntityList();
                return null;
            });
        } catch (Exception exception) {

            exception.printStackTrace();
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
     * @return the statusList
     */
    public ArrayList<Map<String, String>> getValidationList() {
        return validationList;
    }

    /**
     * @param statusList the statusList to set
     */
    public void setValidationList(ArrayList<Map<String, String>> validationList) {
        this.validationList = validationList;
    }

    public ArrayList<Map<String, String>> getEntityList() {
        return entityList;
    }

    /**
     * @param statusList the statusList to set
     */
    public void setEntityList(ArrayList<Map<String, String>> entityList) {
        this.entityList = entityList;
    }
}
