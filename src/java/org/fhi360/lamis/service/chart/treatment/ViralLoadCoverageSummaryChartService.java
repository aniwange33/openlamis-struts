/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.chart.treatment;

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
public class ViralLoadCoverageSummaryChartService {
    
    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    public ViralLoadCoverageSummaryChartService() {

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
                
                if (dataElementId == 41) {
                    map.put("value1", resultSet.getInt("value"));
                } else {
                    if (dataElementId == 16) {
                        map.put("value2", resultSet.getInt("value"));
                    }
                }
            }
            return null;
        });
        return map;
    }

    
}
