/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service;

import java.util.UUID;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author User10
 */
public class TableUUIDGenerator {

    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    private static final Logger LOG = LoggerFactory.getLogger(TableUUIDGenerator.class);
    private final String[] tables = {"patient", "user", "casemanager", "communitypharm", "monitor", "clinic", "pharmacy", "laboratory", "adrhistory", "oihistory", "adherehistory", "statushistory", "regimenhistory", "chroniccare", "dmscreenhistory", "tbscreenhistory", "anc", "delivery", "child", "childfollowup", "maternalfollowup", "partnerinformation", "specimen", "eid", "labno", "nigqual", "devolve", "patientcasemanager", "eac"};

    public void init() {
        LOG.info("Executing....");

        for (String table : tables) {
            LOG.info("Selecting from {} table", table);
            String query = "select count(*) from information_schema.columns "
                    + "where table_name = ? and column_name = 'uuid'";
            jdbcTemplate.query(query, rs -> {
                while (rs.next()) {
                    int count = rs.getInt(1);
                    if (count == 0) {
                        String q = "alter table " + table + " add uuid varchar(36)";
                        transactionTemplate.execute(ts -> {
                            jdbcTemplate.execute(q);
                            return null;
                        });
                    } else {
                        LOG.info("Updating table {}", table);
                        final String[] idColumn = {table + "_id"};
                        if (table.contains("history")) {
                            idColumn[0] = "history_id";
                        }
                        String query1 = "select " + idColumn[0] + " as id from " + table
                                + " where id_uuid is null limit 1000";
                        jdbcTemplate.query(query1, rs1 -> {

                            while (rs1.next()) {
                                Long id = rs.getLong("id");
                                LOG.info("Updating {} id {}", table, id);
                                String query2 = String.format("update %s set id_uuid = '%s' where %s = %s",
                                        table, UUID.randomUUID().toString(), idColumn[0], id);
                                transactionTemplate.execute(ts -> {
                                    jdbcTemplate.execute(query2);
                                    return null;
                                });
                            }
                            return null;
                        });
                    }
                }
                return null;
            });
        }
    }
}
