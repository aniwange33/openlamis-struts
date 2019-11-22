/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.utility;

import org.fhi360.lamis.dao.hibernate.FacilityDAO;
import org.fhi360.lamis.model.Facility;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author user1
 */
public class PatientNumberNormalizer {

    private static String query;
    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    public static String normalize(String hospitalNum, long facilityId) {
        if (FacilityDAO.find(facilityId).getPadHospitalNum() == 1) {
            return padNumber(hospitalNum);
        } else {
            return unpadNumber(hospitalNum);
        }
    }

    public static String padNumber(String hospitalNum) {
        hospitalNum = cleanNumber(hospitalNum);
        String zeros = "";
        int MAX_LENGTH = 7;
        if (hospitalNum.length() < MAX_LENGTH) {
            for (int i = 0; i < MAX_LENGTH - hospitalNum.length(); i++) {
                zeros = zeros + "0";
            }
        }
        return (zeros + hospitalNum).toUpperCase().trim();
    }

    public static String unpadNumber(String hospitalNum) {
        hospitalNum = cleanNumber(hospitalNum);
        char[] numbers = hospitalNum.toCharArray();
        for (int i = 0; i < numbers.length; i++) {
            String ch = Character.toString(numbers[i]);
            if (ch.equals("0")) {
                numbers[i] = ' ';
            } else {
                hospitalNum = String.valueOf(numbers);
                break;
            }
        }
        return hospitalNum.toUpperCase().trim();
    }

    public static String cleanNumber(String hospitalNum) {
        //remove special some characters from hospital number 
        hospitalNum = hospitalNum.replace("'", "");
        hospitalNum = hospitalNum.replace("&", "");
        hospitalNum = hospitalNum.replace("%", "");
        hospitalNum = hospitalNum.replace("?", "");
        hospitalNum = hospitalNum.replace(",", "");
        hospitalNum = hospitalNum.replace(" ", "");
        return hospitalNum;
    }

    public static void normalize(Facility facility) {
        int paddingStatus = FacilityDAO.find(facility.getFacilityId()).getPadHospitalNum();
        if (facility.getPadHospitalNum() != paddingStatus) {
            query = "SELECT patient_id, hospital_num FROM patient WHERE facility_id = ?";
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    Long patientId[] = {resultSet.getLong("patient_id")};
                    String hospitalNum[] = {resultSet.getString("hospital_num")};
                    if (facility.getPadHospitalNum() == 1) {
                        hospitalNum[0] = padNumber(hospitalNum[0]);
                    } else {
                        hospitalNum[0] = unpadNumber(hospitalNum[0]);
                    }
                    query = "UPDATE patient SET hospital_num = ? WHERE facility_id = ? AND patient_id = ?";
                    transactionTemplate.execute(ts -> {
                        jdbcTemplate.update(query, hospitalNum[0], facility.getFacilityId(), patientId[0]);
                        return null;
                    });
                }
                return null;
            }, facility.getFacilityId());
        }
    }
}

//JSONObject json = (JSONObject) JSONSerializer.toJSON(content);
//normalize((Integer) json.get("padHospitalNum") == 1? true : false, (Long) json.get("facilityId"));        
