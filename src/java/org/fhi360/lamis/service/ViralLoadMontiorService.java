/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service;

import java.util.ArrayList;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.Constants;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.StringUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author user10
 */
public class ViralLoadMontiorService {

    private String query;
    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    //Update Viral Load Due.
    public void updateViralLoadDue(long patientId) {
        query = "SELECT date_started FROM patient WHERE patient_id = " + patientId + " AND date_started IS NOT NULL";
        jdbcTemplate.query(query, resultSet -> {
            updateViralLoadDue(patientId, DateUtil.parseDateToString(resultSet.getDate("date_started"), "yyyy-MM-dd"));
        });
    }

    public void updateViralLoadDue(long patientId, String dateStarted) {
        ArrayList<String> viralLoads;
        ArrayList<String> viralLoadDates;
        final String[] viralLoadType = {null};
        final String[] updateQuery = {null};
        viralLoads = new ArrayList<>();
        viralLoadDates = new ArrayList<>();
        viralLoadType[0] = Constants.TypeVL.VL_BASELINE;
        updateQuery[0] = "UPDATE patient SET viral_load_due_date = DATEADD('MONTH', 6, '" + dateStarted + "'), viral_load_type = '" + viralLoadType[0] + "', time_stamp = NOW() WHERE patient_id = " + patientId;

        query = "SELECT resultab, resultpc, date_reported FROM laboratory WHERE patient_id = " + patientId + " AND labtest_id = 16 AND DATEDIFF(MONTH, '" + dateStarted + "', date_reported) >= 6 ORDER BY date_reported DESC";
        jdbcTemplate.query(query, resultSet -> {
            //int i = 0;
            while (resultSet.next()) {
                //i++;
                //System.out.println("The Record Count is: "+i);
                String resultab = resultSet.getString("resultab");
                String resultpc = resultSet.getString("resultpc");
                String vlDate = (resultSet.getDate("date_reported") == null) ? "" : DateUtil.parseDateToString(resultSet.getDate("date_reported"), "yyyy-MM-dd");

                if (resultab != null && resultab != "") {
                    if (!StringUtil.isInteger(resultab)) {
                        resultab = "0";
                    }
                    viralLoads.add(resultab);
                    viralLoadDates.add(vlDate);
                } else {
                    if (resultpc != null && resultpc != "") {
                        if (!StringUtil.isInteger(resultpc)) {
                            resultpc = "0";
                        }
                        viralLoads.add(resultpc);
                        viralLoadDates.add(vlDate);
                    }
                }

            }
            Integer vlSize = viralLoads.size();

            if (vlSize == 1) {
                //This is the baseline...set the date to 6 months or three months  
                if (Double.parseDouble(viralLoads.get(0)) < 1000) {
                    viralLoadType[0] = Constants.TypeVL.VL_SECOND;
                    updateQuery[0] = "UPDATE patient SET viral_load_due_date = DATEADD('MONTH', 6, '" + viralLoadDates.get(0) + "'), viral_load_type = '" + viralLoadType[0] + "', time_stamp = NOW() WHERE patient_id = " + patientId;
                } else {
                    viralLoadType[0] = Constants.TypeVL.VL_REPEAT;
                    updateQuery[0] = "UPDATE patient SET viral_load_due_date = DATEADD('MONTH', 3, '" + viralLoadDates.get(0) + "'), viral_load_type = '" + viralLoadType[0] + "', time_stamp = NOW() WHERE patient_id = " + patientId;
                }
            } else if (vlSize == 2) {
                if (Double.parseDouble(viralLoads.get(0)) < 1000) {
                    viralLoadType[0] = Constants.TypeVL.VL_ROUTINE;
                    updateQuery[0] = "UPDATE patient SET viral_load_due_date = DATEADD('MONTH', 12, '" + viralLoadDates.get(0) + "'), viral_load_type = '" + viralLoadType[0] + "', time_stamp = NOW() WHERE patient_id = " + patientId;
                } else {
                    viralLoadType[0] = Constants.TypeVL.VL_REPEAT;
                    updateQuery[0] = "UPDATE patient SET viral_load_due_date = DATEADD('MONTH', 3, '" + viralLoadDates.get(0) + "'), viral_load_type = '" + viralLoadType[0] + "', time_stamp = NOW() WHERE patient_id = " + patientId;
                }
            } else if (vlSize > 2) {

                if (Double.parseDouble(viralLoads.get(0)) < 1000) {
                    viralLoadType[0] = Constants.TypeVL.VL_ROUTINE;
                    updateQuery[0] = "UPDATE patient SET viral_load_due_date = DATEADD('MONTH', 12, '" + viralLoadDates.get(0) + "'), viral_load_type = '" + viralLoadType[0] + "', time_stamp = NOW() WHERE patient_id = " + patientId;
                } else {
                    viralLoadType[0] = Constants.TypeVL.VL_REPEAT;
                    updateQuery[0] = "UPDATE patient SET viral_load_due_date = DATEADD('MONTH', 3, '" + viralLoadDates.get(0) + "'), viral_load_type = '" + viralLoadType[0] + "', time_stamp = NOW() WHERE patient_id = " + patientId;
                }
            }
            executeUpdate(updateQuery[0]);
            return null;
        });
    }

    private void executeUpdate(String query) {
        transactionTemplate.execute(ts -> {
            jdbcTemplate.execute(query);
            return null;
        });
    }

    private boolean isInteger(String s) {
        int radix = 10;
        if (s.isEmpty()) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) {
                    return false;
                } else {
                    continue;
                }
            }
            if (Character.digit(s.charAt(i), radix) < 0) {
                return false;
            }
        }
        return true;
    }

}
