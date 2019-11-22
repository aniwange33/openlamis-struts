/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.chart.hts;

import java.util.ArrayList;
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
public class HtsSummaryChartService {

    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    public HtsSummaryChartService() {

    }

    public ArrayList<Map<String, Object>> chartData(long ipId, long stateId, long lgaId, long facilityId, String dataElementIds, Date reportingDateBegin, Date reportingDateEnd) {
        
        ArrayList<Map<String, Object>> analysisList = new ArrayList<>();

        String[] ids = dataElementIds.split(",");
        
        for (String dataElementId : ids) {
            Map<String, Object> map = new HashMap<>();
            String query = "SELECT SUM(value) AS value, data_element_id FROM indicatorvalue WHERE data_element_id IN ( " + dataElementIds + ") AND report_date BETWEEN '" +  DateUtil.parseDateToString(reportingDateBegin, "yyyy-MM-dd") + "' AND '" + DateUtil.parseDateToString(reportingDateEnd, "yyyy-MM-dd") + "'";

            //If indicator is cummulative do not sum
           if (11 == Integer.valueOf(dataElementId.trim())) {
                   query = "SELECT  SUM(value) AS value, data_element_id FROM indicatorvalue WHERE data_element_id IN ( " + dataElementIds + ") AND report_date = '" + DateUtil.parseDateToString(reportingDateEnd, "yyyy-MM-dd") + "'";
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

            query = query + " GROUP BY data_element_id";

            int[] value1 = {0};
            int[] value2 = {0};
            int[] value3 = {0};

            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    int dataElement = resultSet.getInt("data_element_id");

                    if (dataElement == Integer.valueOf(ids[0].trim())) {
                        value1[0] = resultSet.getInt("value");
                    } else {
                        if (dataElement == Integer.valueOf(ids[1].trim())) {
                            value2[0] = resultSet.getInt("value");
                        } else {
                            value3[0] = resultSet.getInt("value");
                        }
                    }
                }
                return null;
            });

            map.put("value1", value1[0]);
            map.put("value2", value2[0]);
            map.put("value3", value3[0] );
            analysisList.add(map);
        }
        return analysisList;
    }
    
}
