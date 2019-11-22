/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service;

import java.util.Date;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author user10
 */
public class TreatmentCurrentService {

    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    public TreatmentCurrentService() {
    }

    public static boolean isPatientActive(long patientId, int daysSinceLastRefill, Date reportingDate, String dbSuffix) {
        String query = "SELECT m.date_visit, m.duration, m.regimentype_id, m.regimen_id, l.description AS regimentype, r.description AS regimen FROM pharmacy_" + dbSuffix + "  m JOIN regimentype l ON m.regimentype_id = l.regimentype_id JOIN regimen r ON m.regimen_id = r.regimen_id WHERE m.regimentype_id IN (1, 2, 3, 4, 14) AND m.patient_id = " + patientId + " ORDER BY m.date_visit DESC LIMIT 1";
        return jdbcTemplate.query(query, rs -> {
            while (rs.next()) {
                Date dateLastRefill = rs.getDate("date_visit");
                int duration = rs.getInt("duration");
                int monthRefill = duration / 30;
                if (monthRefill <= 0) {
                    monthRefill = 1;
                }
                if (dateLastRefill != null) {
                    //If the last refill date plus refill duration plus days since last refill  in days is before the last day of the reporting date this patient is Active   
                    //or in other words if your 28 days is not after the reporting date your are LTFU
                    if (DateUtil.addYearMonthDay(dateLastRefill, duration + daysSinceLastRefill, "DAY").before(reportingDate)) {
                        return false;
                    } else {
                        return true;
                    }
                }
            }
            return false;
        });
    }

    public static boolean isPatientActiveTLD(long patientId, int daysSinceLastRefill, Date reportingDate, String dbSuffix) {

        String query = "SELECT m.date_visit, m.duration, m.regimentype_id, m.regimen_id, l.description AS regimentype, r.description AS regimen FROM pharmacy_" + dbSuffix + "  m JOIN regimentype l ON m.regimentype_id = l.regimentype_id JOIN regimen r ON m.regimen_id = r.regimen_id WHERE m.regimentype_id IN (1, 2, 3, 4, 14) AND m.patient_id = " + patientId + " ORDER BY m.date_visit DESC LIMIT 1";
        return jdbcTemplate.query(query, rs -> {
            Date dateLastRefill = rs.getDate("date_visit");
            int duration = rs.getInt("duration");
            String regimen = rs.getString("regimen");
            int monthRefill = duration / 30;
            if (monthRefill <= 0) {
                monthRefill = 1;
            }
            if (dateLastRefill != null) {
                //If the last refill date plus refill duration plus days since last refill  in days is before the last day of the reporting date this patient is Active   
                //or in other words if your 28 days is not after the reporting date your are LTFU
                if (DateUtil.addYearMonthDay(dateLastRefill, duration + daysSinceLastRefill, "DAY").before(reportingDate)) {
                    return false;
                } else if (regimen.contains("DTG")) {
                    return true;
                }
            }
            return false;
        });
    }

}
