/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.dao.jdbc;

import java.util.Date;
import org.fhi360.lamis.model.Regimenhistory;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author user1
 */
public class RegimenIntrospector {

    private static final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    public RegimenIntrospector() {
    }

    public static boolean substitutedOrSwitched(String regimen1, String regimen2) {
        boolean[] changed = {false};
        try {
            String query = "SELECT DISTINCT composition, description FROM regimen WHERE description = '" + regimen1 + "' OR description = '" + regimen2 + "'";
            jdbcTemplate.query(query, rs -> {
                String composition1 = "";
                String composition2 = "";
                while (rs.next()) {
                    if (rs.getString("description").equals(regimen1)) {
                        composition1 = rs.getString("composition");
                    }
                    if (rs.getString("description").equals(regimen2)) {
                        composition2 = rs.getString("composition");
                    }
                }

                if (!composition1.equals(composition2)) {
                    changed[0] = true;
                }
                return null;
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return changed[0];
    }

    public static boolean substitutedOrSwitched(long regimenId1, long regimenId2) {
        boolean[] changed = {false};
        try {
            String query = "SELECT DISTINCT composition, regimen_id FROM regimen WHERE regimen_id = " + regimenId1 + " OR regimen_id = " + regimenId2;
            jdbcTemplate.query(query, rs -> {
                String composition1 = "";
                String composition2 = "";
                while (rs.next()) {
                    if (rs.getLong("regimen_id") == regimenId1) {
                        composition1 = rs.getString("composition");
                    }
                    if (rs.getLong("regimen_id") == regimenId2) {
                        composition2 = rs.getString("composition");
                    }
                }
                if (!composition1.equals(composition2)) {
                    changed[0] = true;
                }
                return null;
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return changed[0];
    }

    //This method returns the details of the first time this regimen was dispensed to the patient
    public static Regimenhistory getRegimenHistory(long patientId, long regimentypeId, long regimenId) {
        String regimentype = RegimenJDBC.getRegimentype(regimentypeId);
        String regimen = RegimenJDBC.getRegimen(regimenId);
        return getRegimenHistory(patientId, regimentype, regimen);
    }

    public static Regimenhistory getRegimenHistory(long patientId, String regimentype, String regimen) {
        Regimenhistory regimenhistory = new Regimenhistory();
        try {
            String query = "SELECT * FROM regimenhistory WHERE patient_id = " + patientId + " AND regimentype = '" + regimentype + "' AND regimen = '" + regimen + "' ORDER BY date_visit ASC LIMIT 1";
            jdbcTemplate.query(query, rs -> {
                while (rs.next()) {
                    regimenhistory.setDateVisit(rs.getDate("date_visit"));
                    regimenhistory.setReasonSwitchedSubs(rs.getString("reason_switched_subs"));
                }
                return null;
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return regimenhistory;
    }

    public static Date getDateRegimenEnded(long patientId, long regimenId) {
        Date[] dateRegimenEnded = {null};
        try {
            String query = "SELECT date_visit, duration FROM pharmacy WHERE patient_id = " + patientId + " AND regimen_id = " + regimenId + " ORDER BY date_visit DESC LIMIT 1";
            jdbcTemplate.query(query, rs -> {
                while (rs.next()) {
                    dateRegimenEnded[0] = DateUtil.addDay(rs.getDate("date_visit"), (int) rs.getDouble("duration"));
                }
                return null;
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return dateRegimenEnded[0];
    }

    public static Date getDateRegimenStarted(long patientId, long regimenId) {
        Date[] dateRegimenEnded = {null};
        try {
            String query = "SELECT date_visit, duration FROM pharmacy WHERE patient_id = " + patientId + " AND regimen_id = " + regimenId + " ORDER BY date_visit ASC LIMIT 1";
            jdbcTemplate.query(query, rs -> {
                while (rs.next()) {
                    dateRegimenEnded[0] = rs.getDate("date_visit");
                }
                return null;
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return dateRegimenEnded[0];
    }

    public static String resolveRegimen(String regimensys) {
        String[] regimen = {""};
        try {
            String query = "SELECT regimen FROM regimenresolver WHERE regimensys = '" + regimensys + "'";
            jdbcTemplate.query(query, rs -> {
                while (rs.next()) {
                    regimen[0] = rs.getString("regimen");
                }
                return null;
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return regimen[0];
    }

    public static boolean isARV(String id) {
        if (id.equals("1") || id.equals("2") || id.equals("3") || id.equals("4") || id.equals("14")) {
            return true;
        }
        return false;
    }

    public static boolean isARV(int id) {
        if ((id >= 1 && id <= 4) || id == 14) {
            return true;
        }
        return false;
    }

}
