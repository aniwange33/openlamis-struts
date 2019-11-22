/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.chart.hts;

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
public class HtsReferralSummaryChartService {
    
    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    public HtsReferralSummaryChartService() {

    }

    public Map<String, Integer> chartData(long ipId, long stateId, long lgaId, long facilityId, String dataElementIds, Date reportingDateBegin, Date reportingDateEnd) {

        Map<String, Integer> map = new HashMap<>();

        String query = "SELECT SUM(value) AS value, data_element_id  FROM indicatorvalue WHERE data_element_id IN ( " + dataElementIds + ") AND report_date BETWEEN '" +  DateUtil.parseDateToString(reportingDateBegin, "yyyy-MM-dd") + "' AND '" + DateUtil.parseDateToString(reportingDateEnd, "yyyy-MM-dd") + "'";
        if (stateId != 0) {
            query = query + " AND state_id = " + stateId;
        }
        if (lgaId != 0) {
            query = query + " AND lga_id = " + lgaId;
        }
        if (facilityId != 0) {
            query = query + " AND facility_id = " + facilityId;
        }
        query = query + "  GROUP BY data_element_id";
        
        jdbcTemplate.query(query, resultSet -> {
            while (resultSet.next()) {
                int dataElementId = resultSet.getInt("data_element_id");

                if (dataElementId == 112) {
                    map.put("value1", resultSet.getInt("value"));  //self
                }
                if (dataElementId == 113) {
                    map.put("value2", resultSet.getInt("value"));  //TB
                }
                if (dataElementId == 114) {
                    map.put("value3", resultSet.getInt("value"));  //STI
                }
                if (dataElementId == 115) {
                    map.put("value4", resultSet.getInt("value"));  //FP
                }
                if (dataElementId == 116) {
                    map.put("value5", resultSet.getInt("value"));  //OPD
                }
                if (dataElementId == 117) {
                    map.put("value6", resultSet.getInt("value"));  //Ward
                }
                if (dataElementId == 118) {
                    map.put("value7", resultSet.getInt("value"));   //Blood
                }
                if (dataElementId == 119) {
                    map.put("value8", resultSet.getInt("value"));   //Others
                }
            }
            return null;
        });
        return map;
    }

}
