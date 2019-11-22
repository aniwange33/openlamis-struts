/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author MEdor
 */
public class SyncUtilService {

    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    public static void syncFolder() {
        executeUpdate("DROP TABLE IF EXISTS sync");
        executeUpdate("CREATE TABLE sync (facility_id int, facility_name varchar(100), last_modified date)");
        String contextPath = ServletActionContext.getServletContext().getInitParameter("contextPath");
        final String name = contextPath + "exchange/sync/";
        File directory = new File(name);

        //get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isDirectory()) {

                String facilityId = file.getName();
                File[] tableList = file.listFiles();
                boolean lockFile = Arrays.stream(tableList)
                        .anyMatch(f -> f.getName().equals("lock.ser"));
                if (lockFile) {
                    String query = "SELECT name FROM facility WHERE facility_id = " + facilityId;
                    jdbcTemplate.query(query, resultSet -> {
                        while (resultSet.next()) {
                            String facilityName = resultSet.getString("name");

                            String q = "INSERT INTO sync (facility_id, facility_name, last_modified) VALUES("
                                    + facilityId + ", '" + facilityName + "', ?)";
                            transactionTemplate.execute(ts -> {
                                jdbcTemplate.update(q, new Date());
                                return null;
                            });
                        }
                        return null;
                    });
                    executeUpdate("call CSVWRITE('C:/lamis2/sync.csv', 'select * from sync')");
                }
            }
        }
    }

    private static void executeUpdate(String query) {
        transactionTemplate.execute(ts -> {
            jdbcTemplate.execute(query);
            return null;
        });
    }

}
