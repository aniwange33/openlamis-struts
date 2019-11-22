/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.Scrambler;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author user10
 */
public class Deduplicator {

    private String query;
    private long facilityId;
    private Boolean viewIdentifier;
    private Scrambler scrambler;
    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    private ArrayList<Map<String, String>> duplicateList = new ArrayList<>();

    public Deduplicator() {
        this.scrambler = new Scrambler();
        if (ServletActionContext.getRequest().getSession().getAttribute("viewIdentifier") != null) {
            this.viewIdentifier = (Boolean) ServletActionContext.getRequest().getSession().getAttribute("viewIdentifier");
        }
    }

    public void duplicates() {
        facilityId = (Long) ServletActionContext.getRequest().getSession().getAttribute("facilityId");
        removeNullHospitalNum();
        executeUpdate("DROP TABLE IF EXISTS duplicate");
        executeUpdate("CREATE TEMPORARY TABLE duplicate (patient_id bigint, hospital_num varchar(25), unique_id varchar(25), surname varchar(45), other_names varchar(75), gender varchar(7), date_birth date, address varchar(100), current_status varchar(75))");

        query = "SELECT hospital_num, COUNT(hospital_num) AS num FROM patient WHERE facility_id = " + facilityId + " GROUP BY hospital_num";
        jdbcTemplate.query(query, resultSet -> {
            while (resultSet.next()) {
                String hospitalNum = resultSet.getString("hospital_num");
                int num = resultSet.getInt("num");
                if (num > 1) {
                    // System.out.println("Duplicate...."+hospitalNum);
                    executeUpdate("INSERT INTO duplicate (patient_id, hospital_num, unique_id, surname, other_names, gender, date_birth, address, current_status) SELECT patient_id, hospital_num, unique_id, surname, other_names, gender, date_birth, address, current_status FROM patient WHERE facility_id = " + facilityId + " AND hospital_num = '" + hospitalNum + "'");
                }
            }
            return null;
        });
    }

    public void updateDuplicates(long patientId, String hospitalNum, String uniqueId, long facilityId) {

        if (!hospitalNum.isEmpty() && (hospitalNum.length() >= 7)) {
            String query = "";
            if (!uniqueId.isEmpty()) {
                query = "UPDATE patient SET hospital_num = '" + hospitalNum + "', unique_id = '" + uniqueId + "' WHERE facility_id =  " + facilityId + " AND  patient_id = " + patientId;
            } else {
                query = "UPDATE patient SET hospital_num = '" + hospitalNum + "' WHERE facility_id =  " + facilityId + " AND  patient_id = " + patientId;
            }

            executeUpdate(query);
        }
        System.out.println("Record updated......" + patientId + " Hospital Number: ....." + hospitalNum);

    }

    public void deleteDuplicates(long patientId, long facilityId) {
        String query = "DELETE FROM patient WHERE facility_id =  " + facilityId + " AND  patient_id = " + patientId;
        executeUpdate(query);
        System.out.println("Record deleted......" + patientId);
    }

//    public ArrayList<Map<String, String>> getDuplicates() { 
//        try {
//            query = "SELECT * FROM patient WHERE patient_id IN (SELECT patient_id FROM duplicate) ORDER BY hospital_num";
//            resultSet = executeQuery(query);
//            new PatientListBuilder().buildPatientList(resultSet);        
//            duplicateList = new PatientListBuilder().retrievePatientList(); 
//        }
//        catch (Exception exception) {
//            jdbcUtil.disconnectFromDatabase();  //disconnect from database
//        }
//        return duplicateList;           
//    }
    private void removeNullHospitalNum() {
        String query = "SELECT patient_id FROM patient WHERE (hospital_num IS "
                + "NULL OR hospital_num = '') AND facility_id = " + facilityId;
        jdbcTemplate.query(query, resultSet -> {
            while (resultSet.next()) {
                long patientId = resultSet.getLong("patient_id");
                new DeleteService().deletePatient(facilityId, patientId);
            }
            return null;
        });
    }

    public ArrayList<Map<String, String>> getDuplicates() {
        String query = "SELECT * FROM duplicate ORDER BY hospital_num";
        jdbcTemplate.query(query, resultSet -> {
            while (resultSet.next()) {
                String patientId = Long.toString(resultSet.getLong("patient_id"));
                String hospitalNum = resultSet.getString("hospital_num");
                String uniqueId = resultSet.getString("unique_id");
                String surname = resultSet.getObject("surname") == null ? "" : resultSet.getString("surname");
                surname = (viewIdentifier) ? scrambler.unscrambleCharacters(surname) : surname;
                surname = StringUtils.upperCase(surname);
                String otherNames = resultSet.getObject("other_names") == null ? "" : resultSet.getString("other_names");
                otherNames = (viewIdentifier) ? scrambler.unscrambleCharacters(otherNames) : otherNames;
                otherNames = StringUtils.capitalize(otherNames);
                String gender = resultSet.getObject("gender") == null ? "" : resultSet.getString("gender");
                String dateBirth = resultSet.getObject("date_birth") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_birth"), "MM/dd/yyyy");
                String address = resultSet.getObject("address") == null ? "" : resultSet.getString("address");
                address = (viewIdentifier) ? scrambler.unscrambleCharacters(address) : address;
                address = StringUtils.capitalize(address);
                String currentStatus = resultSet.getObject("current_status") == null ? "" : resultSet.getString("current_status");

                // create an array from object properties 
                Map<String, String> map = new TreeMap<String, String>();
                map.put("patientId", patientId);
                map.put("hospitalNum", hospitalNum);
                map.put("uniqueId", uniqueId);
                map.put("surname", surname);
                map.put("otherNames", otherNames);
                map.put("name", surname + ' ' + otherNames);
                map.put("gender", gender);
                map.put("dateBirth", dateBirth);
                map.put("address", address);
                map.put("currentStatus", currentStatus);

                final int[] count = {0};
                String q = "SELECT COUNT(DISTINCT date_visit) AS count FROM clinic "
                        + "WHERE patient_id = " + resultSet.getLong("patient_id");
                jdbcTemplate.query(q, rs -> {
                    count[0] = count[0] + rs.getInt("count");
                });
                q = "SELECT COUNT(DISTINCT date_visit) AS count FROM pharmacy "
                        + "WHERE patient_id = " + resultSet.getLong("patient_id");
                jdbcTemplate.query(q, rs -> {
                    count[0] = count[0] + rs.getInt("count");
                });
                q = "SELECT COUNT(DISTINCT date_reported) AS count FROM laboratory "
                        + "WHERE patient_id = " + resultSet.getLong("patient_id");
                jdbcTemplate.query(q, rs -> {
                    count[0] = count[0] + rs.getInt("count");
                });
                map.put("count", Integer.toString(count[0]));
                map.put("sel", "0");
                duplicateList.add(map);
            }
            return null;
        });
        return duplicateList;
    }

    private void executeUpdate(String query) {
        transactionTemplate.execute(ts -> {
            jdbcTemplate.execute(query);
            return null;
        });
    }
}
