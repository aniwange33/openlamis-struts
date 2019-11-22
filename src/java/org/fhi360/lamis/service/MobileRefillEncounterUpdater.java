/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service;

import org.fhi360.lamis.dao.hibernate.PharmacyDAO;
import org.fhi360.lamis.dao.jdbc.RegimenJDBC;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.model.Pharmacy;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author user10
 */
public class MobileRefillEncounterUpdater {

    private String query;

    private Long facilityId;
    private Long patientId;
    private Patient patient;
    private Long pharmacyId;
    private Long userId;
    private Pharmacy pharmacy;
    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    public void logDrug(long facilityId, long patientId, String dateVisit, String nextAppointment, String regimentype, String regimen, int duration) {
        long regimentypeId = RegimenJDBC.getRegimentypeId(regimentype);
        query = "SELECT regimen.regimen_id, regimen.regimentype_id, regimendrug.regimendrug_id, drug.morning, drug.afternoon, drug.evening FROM regimen "
                + " JOIN regimendrug ON regimen.regimen_id = regimendrug.regimen_id JOIN drug ON regimendrug.drug_id = drug.drug_id WHERE regimen.description = '" + regimen + "' AND regimen.regimentype_id = " + regimentypeId;
        jdbcTemplate.query(query, resultSet -> {
            while (resultSet.next()) {
                long regimenId = resultSet.getLong("regimen_id");
                long regimendrugId = resultSet.getLong("regimendrug_id");
                double morning = resultSet.getDouble("morning");
                double afternoon = resultSet.getDouble("afternoon");
                double evening = resultSet.getDouble("evening");

                pharmacy = new Pharmacy();
                patient.setPatientId(patientId);
                pharmacy.setPatient(patient);
                pharmacy.setFacilityId(facilityId);
                pharmacy.setRegimentypeId(regimentypeId);
                pharmacy.setRegimenId(regimenId);
                pharmacy.setRegimendrugId(regimendrugId);
                pharmacy.setMorning(morning);
                pharmacy.setAfternoon(afternoon);
                pharmacy.setEvening(evening);
                pharmacy.setDuration(duration);
                pharmacy.setUserId(1L);
                pharmacy.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));

                long[] id = {0};
                query = "SELECT pharmacy_id FROM pharmacy WHERE facility_id = "
                        + facilityId + " AND patient_id = " + patientId
                        + " AND date_visit = '" + dateVisit + "' AND regimendrug_id = "
                        + regimendrugId;
                jdbcTemplate.query(query, rs -> {
                    id[0] = rs.getLong("pharmacy_id");
                    pharmacy.setPharmacyId(id[0]);
                    PharmacyDAO.update(pharmacy);
                });
                if (id[0] == 0) {
                    PharmacyDAO.save(pharmacy);
                }
            }
            return null;
        });
    }

    public void logDrug(long facilityId, long patientId, String dateVisit, String nextAppointment, String regimen, int duration) {

        query = "SELECT regimen.regimen_id, regimen.regimentype_id, regimendrug.regimendrug_id, drug.morning, drug.afternoon, drug.evening FROM regimen "
                + " JOIN regimendrug ON regimen.regimen_id = regimendrug.regimen_id JOIN drug ON regimendrug.drug_id = drug.drug_id WHERE regimen.description = '" + regimen + "'";
        jdbcTemplate.query(query, resultSet -> {
            while (resultSet.next()) {
                long regimentypeId = resultSet.getLong("regimentype_id");
                long regimenId = resultSet.getLong("regimen_id");
                long regimendrugId = resultSet.getLong("regimendrug_id");
                double morning = resultSet.getDouble("morning");
                double afternoon = resultSet.getDouble("afternoon");
                double evening = resultSet.getDouble("evening");

                pharmacy = new Pharmacy();
                patient.setPatientId(patientId);
                pharmacy.setPatient(patient);
                pharmacy.setFacilityId(facilityId);
                pharmacy.setRegimentypeId(regimentypeId);
                pharmacy.setRegimenId(regimenId);
                pharmacy.setRegimendrugId(regimendrugId);
                pharmacy.setMorning(morning);
                pharmacy.setAfternoon(afternoon);
                pharmacy.setEvening(evening);
                pharmacy.setDuration(duration);
                pharmacy.setUserId(1L);
                pharmacy.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));

                long[] id = {0};
                query = "SELECT pharmacy_id FROM pharmacy WHERE facility_id = "
                        + facilityId + " AND patient_id = " + patientId
                        + " AND date_visit = '" + dateVisit + "' AND regimendrug_id = "
                        + regimendrugId;
                jdbcTemplate.query(query, rs -> {
                    id[0] = rs.getLong("pharmacy_id");
                    pharmacy.setPharmacyId(id[0]);
                    PharmacyDAO.update(pharmacy);
                });
                if (id[0] == 0) {
                    PharmacyDAO.save(pharmacy);
                }
            }
            return null;
        });
    }

    public void updateRefillAttribute(long facilityId, long patientId, String dateVisit, String nextRefill) {
        query = "UPDATE patient SET date_last_refill = '" + dateVisit + "', date_next_refill = '" + nextRefill + "', refill_setting = 'COMMUNITY', time_stamp = NOW() WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_last_refill < '" + dateVisit + "'";
    }
}
