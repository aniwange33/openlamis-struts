/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.exchange.nigqual;

/**
 *
 * @author user1
 */

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.JDBCUtil;
import org.fhi360.lamis.utility.Scrambler;

public class NigQualGridBuilder {
    private String query;
    private JDBCUtil jdbcUtil;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private Boolean viewIdentifier;
    private Scrambler scrambler;
    
    public NigQualGridBuilder() {
        this.scrambler = new Scrambler();
        if(ServletActionContext.getRequest().getSession().getAttribute("viewIdentifier") != null) {
            this.viewIdentifier = (Boolean) ServletActionContext.getRequest().getSession().getAttribute("viewIdentifier");                        
        }        
    }
    
    public ArrayList<Map<String, String>> nigQualList() {
        long facilityId = (Long) ServletActionContext.getRequest().getSession().getAttribute("facilityId");
        ArrayList<Map<String, String>> nigqualList = new ArrayList<Map<String, String>>();
        try {
            jdbcUtil = new JDBCUtil();
            query = "SELECT DISTINCT facility_id, portal_id, reporting_date_begin, reporting_date_end, sample_size, population, thermatic_area, review_period_id FROM nigqual WHERE facility_id = " + facilityId + " GROUP BY review_period_id, thermatic_area ORDER BY review_period_id, thermatic_area";
            preparedStatement = jdbcUtil.getStatement(query);
            resultSet = preparedStatement.executeQuery();

            int id = 0;
            while (resultSet.next()) { 
                String reportingDateBegin = resultSet.getDate("reporting_date_begin") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("reporting_date_begin"), "MM/dd/yyyy");
                String reportingDateEnd = resultSet.getDate("reporting_date_end") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("reporting_date_end"), "MM/dd/yyyy");
                String reviewPeriodId = Integer.toString(resultSet.getInt("review_period_id"));
                String reviewPeriod = DateUtil.parseDateToString(resultSet.getDate("reporting_date_begin"), "MMMMM yyyy") + " to " + DateUtil.parseDateToString(resultSet.getDate("reporting_date_end"), "MMMMM yyyy");
                String portalId = Integer.toString(resultSet.getInt("portal_id"));                                    
                String population = Integer.toString(resultSet.getInt("population"));
                String sampleSize = Integer.toString(resultSet.getInt("sample_size"));
                String thermaticArea = resultSet.getString("thermatic_area");
                String description = thermaticArea.equalsIgnoreCase("AD")? "Adult" : thermaticArea.equalsIgnoreCase("PD")? "Pediatric" : "PMTCT"; 
                
                Map<String, String> map = new HashMap<String, String>();
                map.put("id", Integer.toString(id++));
                map.put("reviewPeriod", reviewPeriod);
                map.put("reviewPeriodId", reviewPeriodId);
                map.put("reportingDateBegin", reportingDateBegin);
                map.put("reportingDateEnd", reportingDateEnd);
                map.put("portalId", portalId);
                map.put("population", population);
                map.put("sampleSize", sampleSize);
                map.put("thermaticArea", thermaticArea);
                map.put("description", description);
                nigqualList.add(map);                                    
            }
            resultSet = null;
        }
        catch (Exception exception) {
            resultSet = null;
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }
        return nigqualList;
    }
    
    public ArrayList<Map<String, String>> cohortList() {
        long facilityId = (Long) ServletActionContext.getRequest().getSession().getAttribute("facilityId");
        long reviewPeriodId = Long.parseLong(ServletActionContext.getRequest().getParameter("reviewPeriodId"));
        long portalId = Long.parseLong(ServletActionContext.getRequest().getParameter("portalId"));
        String thermaticArea = ServletActionContext.getRequest().getParameter("thermaticArea");

        ArrayList<Map<String, String>> cohortList = new ArrayList<Map<String, String>>();
        try {
            jdbcUtil = new JDBCUtil();
            query = "SELECT DISTINCT patient.patient_id, patient.unique_id, patient.hospital_num, patient.surname, patient.other_names, patient.gender, patient.age, patient.age_unit, patient.date_registration FROM patient "
                    + " JOIN nigqual ON patient.facility_id = nigqual.facility_id AND patient.patient_id = nigqual.patient_id WHERE patient.facility_id = " + facilityId + " AND nigqual.portal_id = " + portalId + " AND nigqual.review_period_id = " + reviewPeriodId + " AND nigqual.thermatic_area = '" + thermaticArea + "'"; 
            preparedStatement = jdbcUtil.getStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) { 
                String patientId = Long.toString(resultSet.getLong("patient_id")); 
                String hospitalNum = resultSet.getString("hospital_num");
                String uniqueId = resultSet.getString("unique_id") == null ? "" : resultSet.getString("unique_id");
                String surname = resultSet.getString("surname") == null ? "" : resultSet.getString("surname");
                surname = (viewIdentifier)? scrambler.unscrambleCharacters(surname) : surname;
                surname = StringUtils.upperCase(surname);                
                String otherNames = resultSet.getString("other_names") == null ? "" : resultSet.getString("other_names");
                otherNames = (viewIdentifier)? scrambler.unscrambleCharacters(otherNames) : otherNames;
                otherNames = StringUtils.capitalize(otherNames);
                String gender = resultSet.getString("gender") == null ? "" : resultSet.getString("gender");                
                String age = (resultSet.getInt("age") == 0)? "" : Integer.toString(resultSet.getInt("age"));
                String ageUnit = resultSet.getString("age_unit") == null ? "" : resultSet.getString("age_unit");
                String dateRegistration = (resultSet.getDate("date_registration") == null)? "" : DateUtil.parseDateToString(resultSet.getDate("date_registration"), "MM/dd/yyyy");                
                if(!age.trim().isEmpty()) age = age + " - " + ageUnit ;

                // create an array from object properties 
                Map<String, String> map = new HashMap<String, String>();
                map.put("patientId", patientId);
                map.put("facilityId", Long.toString(facilityId));
                map.put("hospitalNum", hospitalNum);
                map.put("uniqueId", uniqueId);
                map.put("name", surname + ' ' + otherNames);
                map.put("gender", gender);
                map.put("age", age);
                map.put("ageUnit", ageUnit);
                map.put("dateRegistration", dateRegistration);
                cohortList.add(map);
            }
            resultSet = null;
        }
        catch (Exception exception) {
            resultSet = null;
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }
        return cohortList;
    }
    
    private void executeUpdate(String query) {
        try {
            preparedStatement = jdbcUtil.getStatement(query);
            preparedStatement.executeUpdate();
        }
        catch (Exception exception) {
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }        
    }        

}
