/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.ObjectSerializer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class SyncAnalyzer {

    private String query;
    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    private ObjectSerializer objectSerializer;
    private ArrayList<Map<String, String>> syncList;

    public SyncAnalyzer() {
        this.objectSerializer = ObjectSerializer.getInstance();
        this.syncList = new ArrayList<Map<String, String>>();
    }

    public void analyze() {
        //Get the facilities that sync/uploaded data to the server this current month
        query = "CREATE TEMPORARY TABLE sync AS SELECT DISTINCT facility_id FROM patient WHERE MONTH(time_stamp) = MONTH(CURDATE()) AND YEAR(time_stamp) = YEAR(CURDATE()) UNION SELECT DISTINCT facility_id FROM clinic WHERE MONTH(time_stamp) = MONTH(CURDATE()) AND YEAR(time_stamp) = YEAR(CURDATE()) UNION SELECT DISTINCT facility_id FROM pharmacy WHERE MONTH(time_stamp) = MONTH(CURDATE()) AND YEAR(time_stamp) = YEAR(CURDATE()) UNION SELECT DISTINCT facility_id FROM laboratory WHERE MONTH(time_stamp) = MONTH(CURDATE()) AND YEAR(time_stamp) = YEAR(CURDATE())";
        executeUpdate(query);
        final String[] state = {""};
        final String[] currentState = {""};
        query = "SELECT DISTINCT facility.facility_id, facility.name, state.name AS state FROM facility JOIN state ON facility.state_id = state.state_id WHERE facility.facility_id IN (SELECT DISTINCT facility_id FROM patient) ORDER BY state.name, facility.name";
        jdbcTemplate.query(query, resultSet -> {
            while (resultSet.next()) {
                long facilityId = resultSet.getLong("facility_id");
                if (currentState[0].equalsIgnoreCase(resultSet.getString("state"))) {
                    state[0] = "";
                } else {
                    state[0] = resultSet.getString("state");
                    currentState[0] = resultSet.getString("state");
                }

                String[] sync = {"0"};
                //check if this facility has sync this month
                query = "SELECT DISTINCT facility_id FROM sync WHERE facility_id = " + facilityId;
                jdbcTemplate.query(query, rs -> {
                    sync[0] = "1";
                });

                query = "SELECT MAX(lastsync) AS lastsync FROM (SELECT MAX(time_stamp) AS lastsync FROM patient WHERE facility_id = " + facilityId + " UNION SELECT MAX(time_stamp) AS lastsync FROM clinic WHERE facility_id = " + facilityId + " UNION SELECT MAX(time_stamp) AS lastsync FROM pharmacy WHERE facility_id = " + facilityId + " UNION SELECT MAX(time_stamp) AS lastsync FROM laboratory WHERE facility_id = " + facilityId + ")";
                String dateLastSync = getTime(query);

                // create an array from object properties 
                Map<String, String> map = new HashMap<>();
                map.put("facilityId", Long.toString(facilityId));
                map.put("facility", resultSet.getString("name"));
                map.put("state", state[0]);
                map.put("dateLastSync", dateLastSync);
                map.put("status", "");
                map.put("sync", sync[0]);
                syncList.add(map);
            }
            //Summarise the number facilities syncing data vs the number of facilities expected
            Map map = summarise();
            if (map != null) {
                syncList.add(map);
            }
            try {
                objectSerializer.serialize(syncList, "sync");
            } catch (IOException | ClassNotFoundException ex) {
            }
            return null;
        });
        executeUpdate("DROP TABLE IF EXISTS sync");
    }

    //Summary of facilities uploading data this current month
    public Map summarise() {
        try {
            query = "SELECT COUNT(DISTINCT facility_id) AS count FROM (SELECT DISTINCT facility_id FROM patient WHERE MONTH(time_stamp) = MONTH(CURDATE()) AND YEAR(time_stamp) = YEAR(CURDATE()) UNION SELECT DISTINCT facility_id FROM clinic WHERE MONTH(time_stamp) = MONTH(CURDATE()) AND YEAR(time_stamp) = YEAR(CURDATE()) UNION SELECT DISTINCT facility_id FROM pharmacy WHERE MONTH(time_stamp) = MONTH(CURDATE()) AND YEAR(time_stamp) = YEAR(CURDATE()) UNION SELECT DISTINCT facility_id FROM laboratory WHERE MONTH(time_stamp) = MONTH(CURDATE()) AND YEAR(time_stamp) = YEAR(CURDATE()))";
            String facilitySyncing = Integer.toString(getCount(query));

            query = "SELECT COUNT(DISTINCT facility_id) AS count FROM (SELECT DISTINCT facility_id FROM patient UNION SELECT DISTINCT facility_id FROM clinic UNION SELECT DISTINCT facility_id FROM pharmacy UNION SELECT DISTINCT facility_id FROM laboratory)";
            String facilityExpected = Integer.toString(getCount(query));

            Map<String, String> map = new HashMap<String, String>();
            map.put("facilityId", "0000");
            map.put("facilitySyncing", facilitySyncing);
            map.put("facilityExpected", facilityExpected);
            return map;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public ArrayList<Map<String, String>> getAnalysis() {
        try {
            //check facility with ID 0000 and remove
            syncList = (ArrayList<Map<String, String>>) objectSerializer.deserialize("sync");
            for (Iterator<Map<String, String>> iterator = syncList.iterator(); iterator.hasNext();) {
                Map map = iterator.next();
                if (((String) map.get("facilityId")).equals("0000")) {
                    iterator.remove();
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return syncList;
    }

    public Map getSummary() {
        try {
            syncList = (ArrayList<Map<String, String>>) objectSerializer.deserialize("sync");
            for (Map map : syncList) {
                if (((String) map.get("facilityId")).equals("0000")) {
                    return map;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public ArrayList<Map<String, String>> audit(long facilityId) {
        ArrayList<Map<String, String>> auditList = new ArrayList<Map<String, String>>();
        try {
            query = "SELECT COUNT(*) AS count FROM (SELECT DISTINCT facility_id, patient_id FROM patient WHERE MONTH(time_stamp) = MONTH(CURDATE()) AND YEAR(time_stamp) = YEAR(CURDATE()) AND facility_id = " + facilityId + ")";
            int newRecPatient = getCount(query);

            query = "SELECT COUNT(*) AS count FROM (SELECT DISTINCT facility_id, patient_id, date_visit FROM clinic WHERE MONTH(time_stamp) = MONTH(CURDATE()) AND YEAR(time_stamp) = YEAR(CURDATE()) AND facility_id = " + facilityId + ")";
            int newRecClinic = getCount(query);

            query = "SELECT COUNT(*) AS count FROM (SELECT DISTINCT facility_id, patient_id, date_visit FROM pharmacy WHERE MONTH(time_stamp) = MONTH(CURDATE()) AND YEAR(time_stamp) = YEAR(CURDATE()) AND facility_id = " + facilityId + ")";
            int newRecPharm = getCount(query);

            query = "SELECT COUNT(*) AS count FROM (SELECT DISTINCT facility_id, patient_id, date_reported FROM laboratory WHERE MONTH(time_stamp) = MONTH(CURDATE()) AND YEAR(time_stamp) = YEAR(CURDATE()) AND facility_id = " + facilityId + ")";
            int newRecLab = getCount(query);

            query = "SELECT COUNT(DISTINCT patient_id) AS count FROM patient WHERE facility_id = " + facilityId + " AND MONTH(date_registration) =  MONTH(CURDATE()) AND YEAR(date_registration) = YEAR(CURDATE())";
            String newEnrolled = Integer.toString(getCount(query));

            query = "SELECT COUNT(DISTINCT patient_id) AS count FROM patient WHERE facility_id = " + facilityId + " AND status_registration IN ('HIV+ non ART', 'Pre-ART Transfer In', 'ART Transfer In')";
            String everEnrolled = Integer.toString(getCount(query));

            query = "SELECT COUNT(DISTINCT patient_id) AS count FROM patient WHERE facility_id = " + facilityId + " AND current_status IN ('HIV+ non ART', 'Pre-ART Transfer In', 'ART Start', 'ART Restart', 'ART Transfer In')";
            String currentCare = Integer.toString(getCount(query));

            query = "SELECT COUNT(DISTINCT patient_id) AS count FROM patient WHERE facility_id = " + facilityId + " AND current_status IN ('ART Transfer In', 'ART Start', 'ART Restart')";
            String currentART = Integer.toString(getCount(query));

            // create an array from object properties 
            Map<String, String> map = new HashMap<String, String>();
            map.put("newRecPatient", Integer.toString(newRecPatient));
            map.put("newRecClinic", Integer.toString(newRecClinic));
            map.put("newRecPharm", Integer.toString(newRecPharm));
            map.put("newRecLab", Integer.toString(newRecLab));

            map.put("newEnrolled", newEnrolled);
            map.put("everEnrolled", everEnrolled);
            map.put("currentCare", currentCare);
            map.put("currentART", currentART);
            auditList.add(map);
        } catch (Exception exception) {  //disconnect from database
        }
        return auditList;
    }

    private void executeUpdate(String query) {
        transactionTemplate.execute(ts -> {
            jdbcTemplate.execute(query);
            return null;
        });
    }

    private int getCount(String query) {
        int[] count = {0};
        jdbcTemplate.query(query, rs -> {
            count[0] = rs.getInt("count");
        });
        return count[0];
    }

    private String getTime(String query) {
        String[] time = {""};
        jdbcTemplate.query(query, rs -> {
            time[0] = (rs.getDate("lastsync") == null) ? ""
                    : DateUtil.parseDateToString(rs.getDate("lastsync"), "dd-MM-yyyy");
        });
        return time[0];
    }
}
