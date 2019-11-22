/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.controller.report;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author user10
 */
public class ReportPeriodAction extends ActionSupport {
//    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private Map<String, String> periodMap = new HashMap<>();
//
   public String retrieve() {
//        String reportType = ServletActionContext.getRequest().getParameter("reportType");
//        System.out.println("........... reporting DHIS: " + reportType);
//        int length = 8;
//        switch (reportType) {
//            case "WR":
//                length = 7;
//                break;
//            case "MR":
//                length = 6;
//        }
//        jdbcTemplate.queryForList("select distinct period from dhisvalue where length(period) = ?",
//                String.class, length)
//                .forEach(period -> periodMap.put(period, period));
        return SUCCESS;
    }

    /**
     * @return the periodMap
     */
    public Map<String, String> getPeriodMap() {
        return periodMap;
    }

    /**
     * @param periodMap the periodMap to set
     */
    public void setPeriodMap(Map<String, String> periodMap) {
        this.periodMap = periodMap;
    }
}
