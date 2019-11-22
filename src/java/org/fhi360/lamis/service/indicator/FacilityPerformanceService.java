/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.indicator;

import org.apache.commons.lang3.RandomStringUtils;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author user10
 */
//@Component
public class FacilityPerformanceService {

    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private static ExecutorService executorService = Executors.newFixedThreadPool(50);

    static {
        ContextProvider.getBean(TransactionTemplate.class).execute(status -> {
            jdbcTemplate.execute("update facility set processing = 0");
            return null;
        });

        jdbcTemplate.query("select table_name from information_schema.tables where table_type "
                + "like 'VIEW' and table_schema like 'LAMIS'",
                rs -> {
                    while (rs.next()) {
                        String view = rs.getString("table_name");
                        ContextProvider.getBean(TransactionTemplate.class).execute(status -> {
                            jdbcTemplate.execute(String.format("drop view if exists %s", view));
                            return null;
                        });
                    }
                    return null;
                });
    }

    private IndicatorService indicatorService;

    public FacilityPerformanceService(IndicatorService indicatorService) {
        this.indicatorService = indicatorService;
    }

    public void process() {
        //Select all facilityId that have uploaded data in the last 14 days and run performance analysis
        String query = "SELECT DISTINCT facility_id FROM patient WHERE time_stamp BETWEEN DATE_ADD(CURDATE(), "
                + "INTERVAL -14 DAY) AND CURDATE() UNION SELECT DISTINCT facility_id FROM clinic WHERE "
                + "time_stamp BETWEEN DATE_ADD(CURDATE(), INTERVAL -14 DAY) AND CURDATE() UNION SELECT "
                + "DISTINCT facility_id FROM pharmacy WHERE time_stamp BETWEEN DATE_ADD(CURDATE(), INTERVAL -14 DAY) "
                + "AND CURDATE() UNION SELECT DISTINCT facility_id FROM laboratory WHERE time_stamp BETWEEN "
                + "DATE_ADD(CURDATE(), INTERVAL -14 DAY) AND CURDATE() UNION SELECT DISTINCT facility_id FROM "
                + "statushistory WHERE time_stamp BETWEEN DATE_ADD(CURDATE(), INTERVAL -14 DAY) AND CURDATE() "
                + "UNION SELECT DISTINCT facility_id FROM hts WHERE time_stamp BETWEEN DATE_ADD(CURDATE(), "
                + "INTERVAL -14 DAY) AND CURDATE()";
        /*
        String query = "SELECT DISTINCT facility_id FROM patient WHERE time_stamp BETWEEN DATE_ADD(CURDATE(), " +
                "INTERVAL -14 DAY) AND CURDATE() and FACILITY_ID in (select FACILITY_ID from FACILITY where STATE_ID = 3)" +
                " UNION SELECT DISTINCT facility_id FROM clinic WHERE " +
                "time_stamp BETWEEN DATE_ADD(CURDATE(), INTERVAL -14 DAY) AND CURDATE() and FACILITY_ID in (select " +
                "FACILITY_ID from FACILITY where STATE_ID = 3) UNION SELECT " +
                "DISTINCT facility_id FROM pharmacy WHERE time_stamp BETWEEN DATE_ADD(CURDATE(), INTERVAL -14 DAY) " +
                "AND CURDATE() and FACILITY_ID in (select FACILITY_ID from FACILITY where STATE_ID = 3) " +
                "UNION SELECT DISTINCT facility_id FROM laboratory WHERE time_stamp BETWEEN " +
                "DATE_ADD(CURDATE(), INTERVAL -14 DAY) AND CURDATE() and FACILITY_ID in (select FACILITY_ID from FACILITY where STATE_ID = 3)" +
                " UNION SELECT DISTINCT facility_id FROM " +
                "statushistory WHERE time_stamp BETWEEN DATE_ADD(CURDATE(), INTERVAL -14 DAY) AND CURDATE() and " +
                "FACILITY_ID in (select FACILITY_ID from FACILITY where STATE_ID = 3)";
            String query = "SELECT FACILITY_ID FROM facility WHERE state_id = 3 and active = 1";       
         */

 /*        
        String query = "SELECT FACILITY_ID FROM facility WHERE active = 1";       
        if(indicatorService instanceof HtsIndicatorService) {
            query = "SELECT FACILITY_ID FROM facility WHERE state_id = 3 and active = 1";       
        } 
         */
        List<Long> facilityIds = jdbcTemplate.queryForList(query, Long.class);
        for (Long facilityId : facilityIds) {
            //For every facility generate a db suffix for temporary tables
            String dbSuffix = RandomStringUtils.randomAlphabetic(6);
            dbSuffix = dbSuffix + "_" + facilityId;

            ProcessorThread processorThread = new ProcessorThread(facilityId, dbSuffix);
            executorService.execute(processorThread);
        }
    }

    public void process(long facilityId, Date reportingDate, int dataElementId, String dbSuffix) {
        try {
            System.out.println("Performance Report for @ " + reportingDate + " for facility --- " + facilityId);
            indicatorService.process(facilityId, reportingDate, dataElementId, dbSuffix);
            System.out.println("Performance Report Completed @ " + new Date() + " for facility --- " + facilityId);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    private Boolean facilityProcessing(Long facilityId) {
        return jdbcTemplate.queryForObject("select processing from facility where facility_id = ?",
                Boolean.class, facilityId);
    }

    private void processFacility(Long facilityId, boolean processing) {
        ContextProvider.getBean(TransactionTemplate.class).execute(status -> {
            jdbcTemplate.update("update facility set processing = ? where facility_id = ?",
                    processing, facilityId);
            return null;
        });
    }

    public Date convertToDate(LocalDate dateToConvert) {
        return java.util.Date.from(dateToConvert.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    public LocalDate convertToLocalDate(Date dateToConvert) {
        return Instant.ofEpochMilli(dateToConvert.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    class ProcessorThread implements Runnable {

        private Long facilityId;
        private String dbSuffix;

        ProcessorThread(Long facilityId, String dbSuffix) {
            this.facilityId = facilityId;
            this.dbSuffix = dbSuffix;
        }

        @Override
        public void run() {
            for (int dataElementId : indicatorService.dataElementIds()) {
                LocalDate end = getDateLastProcessed(facilityId, dataElementId).minusDays(7);
                System.out.println("End date: " + end);
                LocalDate start = LocalDate.now();
                System.out.printf("Facility %s starting from %s\n", facilityId, start);
                boolean processing = facilityProcessing(facilityId);
                if (!processing) {
                    processFacility(facilityId, true);
                    while (!end.isAfter(start)) {
                        process(facilityId, convertToDate(start), dataElementId, dbSuffix);
                        //start = DateUtil.addDay(start, -1);
                        start = start.minusDays(1);
                    }
                    processFacility(facilityId, false);
                }
            }
        }
    }

    private LocalDate getDateLastProcessed(Long facilityId, int dataElementId) {
        //LocalDate END = LocalDate.of(2019, 3, 1);
        String END = "2019-03-01";

        String query = "SELECT COALESCE(MAX(report_date), ?) AS report_date FROM indicatorvalue "
                + "where facility_id = ? AND data_element_id = ?";
        return jdbcTemplate.query(query, rs -> {
            rs.next();
            return convertToLocalDate(rs.getDate(1));
        }, END, facilityId, dataElementId);

    }
}
