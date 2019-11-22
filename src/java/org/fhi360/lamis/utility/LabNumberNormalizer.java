/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.utility;

import java.util.Calendar;
import java.util.GregorianCalendar;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author user1
 */
public class LabNumberNormalizer {

    private static String query;
    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    public static String getLabno() {
        long facilityId = (Long) ServletActionContext.getRequest().getSession().getAttribute("facilityId");
        final String labno[] = {""};
        String zeros[] = {""};
        String lastno[] = {""};
        final String year[] = {""};
        int MAX_LENGTH = 5;
        boolean found[] = {false};
        query = "SELECT * FROM labno WHERE facility_id = " + facilityId + " AND year = YEAR(CURDATE())";
        jdbcTemplate.query(query, resultSet -> {
            found[0] = true;
            lastno[0] = Integer.toString(resultSet.getInt("lastno") + 1);
            year[0] = Integer.toString(resultSet.getInt("year"));
            if (lastno[0].length() < MAX_LENGTH) {
                for (int i = 0; i < MAX_LENGTH - lastno[0].length(); i++) {
                    zeros[0] = zeros[0] + "0";
                }
            }
            labno[0] = zeros[0] + lastno[0] + "/" + year[0].substring(2, 4);
        });
        if (!found[0]) {
            query = "INSERT INTO labno(facility_id, year, lastno, time_stamp) VALUES(" + facilityId + ", YEAR(CURDATE()), 0, NOW())";
            transactionTemplate.execute(st -> {
                jdbcTemplate.execute(query);
                year[0] = Integer.toString(new GregorianCalendar().get(Calendar.YEAR));
                labno[0] = "00001/" + year[0].substring(2, 4);
                return null;
            });
        }
        return labno[0];
    }

    public static void updateLabno() {
        long facilityId = (Long) ServletActionContext.getRequest().getSession().getAttribute("facilityId");
        query = "UPDATE labno SET lastno = lastno+1, time_stamp = NOW() WHERE facility_id = " + facilityId + " AND year = YEAR(CURDATE())";
        transactionTemplate.execute(ts -> {
            jdbcTemplate.execute(query);
            return null;
        });
    }

    public static void updateLabno(long facilityId, int year, int lastno) {

        query = "SELECT * FROM labno WHERE facility_id = " + facilityId + " AND year = " + year;
        String query1[] = {"INSERT INTO labno(facility_id, year, lastno, time_stamp) VALUES(" + facilityId + ", " + year + "," + lastno + ", NOW())"};

        jdbcTemplate.query(query, rs -> {
            query1[0] = "UPDATE labno SET lastno = " + lastno + ", time_stamp = NOW() WHERE facility_id = " + facilityId + " AND year = " + year;
        });
        transactionTemplate.execute(ts -> {
            jdbcTemplate.execute(query1[0]);
            return null;
        });
    }

    public static String normalize(String labno) {
        String zeros = "";
        int MAX_LENGTH = 8;
        if (labno.length() < MAX_LENGTH) {
            for (int i = 0; i < MAX_LENGTH - labno.length(); i++) {
                zeros = zeros + "0";
            }
        }
        labno = labno.replace("O", "0");
        labno = labno.replace("o", "0");
        labno = labno.replace("\\", "/");
        labno = labno.replace("-", "/");
        labno = labno.replace("_", "/");
        return zeros + labno;
    }

}
