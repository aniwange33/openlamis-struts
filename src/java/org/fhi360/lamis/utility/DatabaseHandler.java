/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.utility;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Date;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author user10
 */
public class DatabaseHandler {

    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    public DatabaseHandler() {
    }

    public static void executeUpdate(String query) {
        transactionTemplate.execute(st -> {
            jdbcTemplate.execute(query);
            return null;
        });
    }

    public static Long getFacilityUploadForToday(long facilityId, Date dateUpload) {
        String query1 = "SELECT facility_id FROM synchistory WHERE facility_id = " + facilityId + " AND upload_date = '" + dateUpload + "'";
        Long id = jdbcTemplate.queryForObject(query1, Long.class);
        return id != null ? id : 0L;
    }

    public static String getFilesUploadedToday(long facilityId, Date dateUpload) {
        String query1 = "SELECT tables_uploaded FROM synchistory WHERE facility_id = " + facilityId + " AND upload_date = '" + dateUpload + "'";
        String tables = jdbcTemplate.queryForObject(query1, String.class);
        return tables != null ? tables : "";
    }

}
