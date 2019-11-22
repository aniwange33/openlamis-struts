/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.chart;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author user10
 */
public class IndicatorByGenderChartService {
    
    
    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    public IndicatorByGenderChartService() {

    }

    public Map<String, Integer> chartData(long ipId, long stateId, long lgaId, long facilityId, String dataElementId, String categoryFemaleIds, String categoryMaleIds, Date reportingDateBegin, Date reportingDateEnd) {

        Map<String, Integer> map = new HashMap<>();

        String query = "SELECT SUM(value) AS value FROM indicatorvalue WHERE data_element_id = " + Integer.parseInt(dataElementId) + " AND category_id IN (" + categoryFemaleIds + ") AND report_date BETWEEN '" +  DateUtil.parseDateToString(reportingDateBegin, "yyyy-MM-dd") + "' AND '" + DateUtil.parseDateToString(reportingDateEnd, "yyyy-MM-dd") + "'";
        
        //If indicator is cummulative do not sum
        if (8 == Integer.valueOf(dataElementId.trim())) {
                query = "SELECT value FROM indicatorvalue WHERE data_element_id = " + Integer.parseInt(dataElementId) + " AND category_id IN (" + categoryFemaleIds + ") AND report_date = '" + DateUtil.parseDateToString(reportingDateEnd, "yyyy-MM-dd") + "'";
         }
         
        if (stateId != 0) {
            query = query + " AND state_id = " + stateId;
        }
        if (lgaId != 0) {
            query = query + " AND lga_id = " + lgaId;
        }
        if (facilityId != 0) {
            query = query + " AND facility_id = " + facilityId;
        }

        int[] female = {0};

        jdbcTemplate.query(query, resultSet -> {
            female[0] = resultSet.getInt("value");
        });
        map.put("female", female[0]);

        query = "SELECT SUM(value) AS value FROM indicatorvalue WHERE data_element_id = " + Integer.parseInt(dataElementId) + " AND category_id IN (" + categoryMaleIds + ") AND report_date BETWEEN '" +  DateUtil.parseDateToString(reportingDateBegin, "yyyy-MM-dd") + "' AND '" + DateUtil.parseDateToString(reportingDateEnd, "yyyy-MM-dd") + "'";
        
        //If indicator is cummulative do not sum
        if (8 == Integer.valueOf(dataElementId.trim())) {
                query = "SELECT value FROM indicatorvalue WHERE data_element_id = " + Integer.parseInt(dataElementId) + " AND category_id IN (" + categoryMaleIds + ") AND report_date = '" + DateUtil.parseDateToString(reportingDateEnd, "yyyy-MM-dd") + "'";
         }
        
        if (stateId != 0) {
            query = query + " AND state_id = " + stateId;
        }
        if (lgaId != 0) {
            query = query + " AND lga_id = " + lgaId;
        }
        if (facilityId != 0) {
            query = query + " AND facility_id = " + facilityId;
        }

        int male[] = {0};

        jdbcTemplate.query(query, resultSet -> {
            male[0] = resultSet.getInt("value");
        });
        map.put("male", male[0]);
        return map;
    }

}
