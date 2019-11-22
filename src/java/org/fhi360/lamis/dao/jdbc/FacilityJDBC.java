/**
 *
 * @author user1
 */
package org.fhi360.lamis.dao.jdbc;

import org.fhi360.lamis.model.Facility;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class FacilityJDBC {

    public FacilityJDBC() {
    }
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    public static void save(Facility facility) {
        long facilityId = facility.getFacilityId();
        long stateId = facility.getStateId();
        long lgaId = facility.getLga().getLgaId();
        String name = facility.getName();
        String facilityType = facility.getFacilityType();
        String address1 = facility.getAddress1();
        String address2 = facility.getAddress2();
        String phone1 = facility.getPhone1();
        String phone2 = facility.getPhone2();
        String email = facility.getEmail();
        int padHospitalNum = facility.getPadHospitalNum();

        try {
            String query[] = {"SELECT facility_id FROM facility WHERE facility_id = " + facilityId};

            jdbcTemplate.query(query[0], resultSet -> {
                if (resultSet.next()) {
                    query[0] = "UPDATE facility SET state_id = " + stateId + ", lga_id = " + lgaId + ", name = '" + name + "', facility_type = '" + facilityType + "', address1 = '" + address1 + "', address2 = '" + address2 + "', phone1 = '" + phone1 + "', phone2 = '" + phone2 + "', email = '" + email + "', pad_hospital_num = " + padHospitalNum + " WHERE facility_id = " + facilityId;
                    transactionTemplate.execute((ts) -> {
                        jdbcTemplate.execute(query[0]);
                        return null; //To change body of generated lambdas, choose Tools | Templates.
                    });
                } else {
                    query[0] = "INSERT INTO facility(facility_id, state_id, lga_id, name, facility_type, address1, address2, phone1, phone2, email, pad_hospital_num) VALUES(" + facilityId + "," + stateId + "," + lgaId + ",'" + name + "','" + facilityType + "','" + address1 + "','" + address2 + "','" + phone1 + "','" + phone2 + "','" + email + "'," + padHospitalNum + ")";
                    transactionTemplate.execute((ts) -> {
                        jdbcTemplate.execute(query[0]);
                        return null; //To change body of generated lambdas, choose Tools | Templates.
                    });
                    updateExchange(facilityId);
                }
                return null; //To change body of generated lambdas, choose Tools | Templates.
            });

        } catch (Exception exception) {

        }
    }

    public static String getSMSPrinter(long facilityId) {

        String query = "SELECT phone2 FROM facility WHERE facility_id = " + facilityId;
        String phone = jdbcTemplate.queryForObject(query, String.class);
        return phone != null ? phone : "";
    }

    public static void setSMSPrinter(long facilityId, String phone) {

        try {
            String query = "Update facility SET phone2 = '" + phone + "' WHERE facility_id = " + facilityId;
            transactionTemplate.execute((ts) -> {
                jdbcTemplate.execute(query);
                return null; //To change body of generated lambdas, choose Tools | Templates.
            });
        } catch (Exception exception) {

        }
    }

    public static String getFacilityName(long facilityId) {

        String query = "SELECT name FROM facility WHERE facility_id = " + facilityId;
        String name = jdbcTemplate.queryForObject(query, String.class);
        return name != null ? name : "";
    }

    public static String getStateName(long stateId) {

        String query = "SELECT name FROM state WHERE state_id = " + stateId;
        String name = jdbcTemplate.queryForObject(query, String.class);
        return name != null ? name : "";
    }

    public static String getStateNameForFacility(long facilityId) {

        String query = "SELECT s.name AS state_name FROM state s JOIN facility f ON s.state_id = f.state_id WHERE f.facility_id = " + facilityId;
        String name = jdbcTemplate.queryForObject(query, String.class);
        return name != null ? name : "";
    }

    public static String getLgaNameForFacility(long facilityId) {

        String query = "SELECT l.name AS lga_name FROM lga l JOIN facility f ON l.lga_id = f.lga_id WHERE f.facility_id = " + facilityId;
        String name = jdbcTemplate.queryForObject(query, String.class);
        return name != null ? name : "";
    }

    public static List<Long> getFacilitiesInState(long stateId) {
        List<Long> facilityIds = new ArrayList<>();
        String query = "SELECT facility_id FROM facility WHERE facility_id IN (SELECT DISTINCT facility_id FROM patient) AND state_id = " + stateId + " ORDER BY facility_id ASC";
        Long facilityId = jdbcTemplate.queryForObject(query, Long.class);
        facilityIds.add(facilityId);
        return facilityIds;
    }

    public static void updateExchange(long facilityId) {

        try {

            String query = "INSERT INTO exchange (facility_id, patient, clinic, "
                    + "pharmacy, laboratory, adrhistory, oihistory, adherehistory, "
                    + "statushistory, regimenhistory, chroniccare, dmscreenhistory, "
                    + "tbscreenhistory, specimen, eid, labno, monitor) "
                    + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            transactionTemplate.execute((ts) -> {
                jdbcTemplate.update(query, facilityId,
                        new Date(), new Date(), new Date(), new Date(), new Date(),
                        new Date(), new Date(), new Date(), new Date(), new Date(),
                        new Date(), new Date(), new Date(), new Date(), new Date(),
                        new Date()
                );
                return null; //To change body of generated lambdas, choose Tools | Templates.
            });
        } catch (Exception exception) {

        }
    }
}
