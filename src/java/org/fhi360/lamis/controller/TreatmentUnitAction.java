/**
 *
 * @author AALOZIE
 */
package org.fhi360.lamis.controller;

import java.util.*;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.model.Specimen;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class TreatmentUnitAction extends ActionSupport {

    private String stateId;
    private String lgaId;
    private String treatmentUnitId;
    private String query;
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    private Map<String, String> lgaMap = new TreeMap<>();
    private Map<String, String> facilityMap = new TreeMap<>();

    public String retrieveTreatmentUnit() {
        query = "SELECT DISTINCT specimen.treatment_unit_id, facility.name FROM specimen JOIN facility ON specimen.treatment_unit_id = facility.facility_id ORDER BY facility.name";
        try {
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String facilityId = Long.toString(resultSet.getLong("treatment_unit_id"));
                    String name = resultSet.getString("name");
                    facilityMap.put(facilityId, name);
                }
                return null; //To change body of generated lambdas, choose Tools | Templates.
            });

            // loop through resultSet for each row and put into Map
        } catch (Exception exception) {

        }
        return SUCCESS;
    }

    public String retrieveCurrentTreatmentUnit() {
        //State, LGA and treatmentunit is preserved so that the specimen page can reload 
        if (ServletActionContext.getRequest().getParameterMap().containsKey("stateId")) {
            stateId = ServletActionContext.getRequest().getParameter("stateId");
            lgaId = ServletActionContext.getRequest().getParameter("lgaId");
        } else {
            if (ServletActionContext.getRequest().getSession().getAttribute("specimenObjSession") != null) {
                Specimen specimenObjSession = (Specimen) ServletActionContext.getRequest().getSession().getAttribute("specimenObjSession");
                stateId = Long.toString(specimenObjSession.getStateId());
                lgaId = Long.toString(specimenObjSession.getLgaId());
                treatmentUnitId = Long.toString(specimenObjSession.getTreatmentUnitId());
            }
        }
        retrieveLgaById();
        retrieveFacilityById();
        return SUCCESS;
    }

    private void retrieveLgaById() {
        query = "SELECT * FROM lga WHERE state_id  = '" + stateId + "' ORDER BY name";
        try {
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String id = Long.toString(resultSet.getLong("lga_id"));
                    String name = resultSet.getString("name");
                    lgaMap.put(id, name);
                }
                return null; //To change body of generated lambdas, choose Tools | Templates.
            });
            // loop through resultSet for each row and put into Map

        } catch (Exception exception) {

        }
    }

    private void retrieveFacilityById() {
        query = "SELECT * FROM facility WHERE state_id  = '" + stateId + "' AND lga_id = '" + lgaId + "' ORDER BY name";
        try {
            jdbcTemplate.query(query, resultSet -> {
                // loop through resultSet for each row and put into Map
                while (resultSet.next()) {
                    String id = Long.toString(resultSet.getLong("facility_id"));
                    String name = resultSet.getString("name");
                    facilityMap.put(id, name);
                }
                return null;
            });
        } catch (Exception exception) {

        }
    }

    /**
     * @return the stateId
     */
    public String getStateId() {
        return stateId;
    }

    /**
     * @param stateId the stateId to set
     */
    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    /**
     * @return the lgaId
     */
    public String getLgaId() {
        return lgaId;
    }

    /**
     * @param lgaId the lgaId to set
     */
    public void setLgaId(String lgaId) {
        this.lgaId = lgaId;
    }

    /**
     * @return the treatmentUnitId
     */
    public String getTreatmentUnitId() {
        return treatmentUnitId;
    }

    /**
     * @param treatmentUnitId the treatmentUnitId to set
     */
    public void setTreatmentUnitId(String treatmentUnitId) {
        this.treatmentUnitId = treatmentUnitId;
    }

    /**
     * @return the lgaMap
     */
    public Map<String, String> getLgaMap() {
        return lgaMap;
    }

    /**
     * @param lgaMap the lgaMap to set
     */
    public void setLgaMap(Map<String, String> lgaMap) {
        this.lgaMap = lgaMap;
    }

    /**
     * @return the facilityMap
     */
    public Map<String, String> getFacilityMap() {
        return facilityMap;
    }

    /**
     * @param stateMap the facilityMap to set
     */
    public void setFacilityMap(Map<String, String> facilityMap) {
        this.facilityMap = facilityMap;
    }

}
