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
public class IndicatorSummaryByCategoryChartService {

    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    public IndicatorSummaryByCategoryChartService() {
    }

    public Map<String, Integer> chartData(long ipId, long stateId, long lgaId, long facilityId, String dataElementId, int categoryFemaleId, int categoryMaleId, Date reportingDateBegin, Date reportingDateEnd) {

         Map<String, Integer> map = new HashMap<>();

        String query = "SELECT SUM(value) AS value, category_id FROM indicatorvalue WHERE data_element_id = " + Integer.parseInt(dataElementId) + " AND (category_id = " + categoryFemaleId + " OR category_id = " + categoryMaleId + ") AND report_date BETWEEN '" +  DateUtil.parseDateToString(reportingDateBegin, "yyyy-MM-dd") + "' AND '" + DateUtil.parseDateToString(reportingDateEnd, "yyyy-MM-dd") + "'";

        //If indicator is cummulative do not sum
        if (8 == Integer.valueOf(dataElementId.trim())) {
                query = "SELECT value, category_id FROM indicatorvalue WHERE data_element_id = " + Integer.parseInt(dataElementId) + " AND (category_id = " + categoryFemaleId + " OR category_id = " + categoryMaleId + ") AND report_date = '" + DateUtil.parseDateToString(reportingDateEnd, "yyyy-MM-dd") + "'";
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

        query = query + " GROUP BY category_id";

        jdbcTemplate.query(query, resultSet -> {
            while (resultSet.next()) {
                int id = resultSet.getInt("category_id");
                
                if (id == categoryMaleId) {
                    map.put("value1", resultSet.getInt("value"));                   
                } else {
                   map.put("value2", resultSet.getInt("value") * -1);                
                }
            }
            return null;
        });
        return map;
    }

}
