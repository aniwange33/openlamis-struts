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
public class FacilitySyncChartService {

    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    public FacilitySyncChartService() {

    }

    public Map<String, Integer> chartData(int stateId, Date reportingDateBegin, Date reportingDateEnd) {

        Map<String, Integer> map = new HashMap<>();
        String strDate = DateUtil.parseDateToString(reportingDateBegin, "yyyy-MM-dd");
        java.sql.Date reportingDate = java.sql.Date.valueOf(strDate);
        strDate = DateUtil.parseDateToString(reportingDateEnd, "yyyy-MM-dd");
        java.sql.Date reportingDate2 = java.sql.Date.valueOf(strDate);

        //Number of expected uploads
        String query = "SELECT COUNT(*) AS count FROM facility WHERE state_id = " + stateId + " AND active";
        jdbcTemplate.query(query, resultSet -> {
            map.put("value1", resultSet.getInt("count"));
        });

        //Number of uploads
        query = "SELECT COUNT(DISTINCT facility_id) AS count FROM synchistory WHERE upload_date BETWEEN '" + reportingDate + "' AND  '" + reportingDate2 + "' AND facility_id IN (SELECT DISTINCT facility_id FROM facility WHERE state_id = " + stateId + " AND active)";
        jdbcTemplate.query(query, resultSet -> {
            map.put("value2", resultSet.getInt("count"));
        });
        return map;
    }

    public String getStateById(int stateId) {
        String query = "SELECT name FROM state WHERE state_id = " + stateId;
        return jdbcTemplate.queryForObject(query, String.class);
    }

}
