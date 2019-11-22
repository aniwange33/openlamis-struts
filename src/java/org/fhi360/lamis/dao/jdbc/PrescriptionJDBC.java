/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.dao.jdbc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.model.dto.PrescriptionDTO;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.Constants;

import org.fhi360.lamis.utility.ResultSetMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author user10
 */
public class PrescriptionJDBC {

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    public PrescriptionJDBC() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
    }

    // Retrieve a pharmacy record in database
    public static List<PrescriptionDTO> findActivePrescriptions(long patientId, long facilityId) {
        ResultSetMapper<PrescriptionDTO> resultSetMapper = new ResultSetMapper<>();
        final List<PrescriptionDTO> prescriptionList = new ArrayList<>();
        try {
            String prescriptionType = "drug";

            String query = "SELECT * FROM prescription WHERE patient_id = ? AND facility_id = ? AND prescription_type = ? AND status = ?";

            jdbcTemplate.query(query, (resultSet) -> {

                prescriptionList.addAll(resultSetMapper.mapRersultSetToObject(resultSet, PrescriptionDTO.class));
                return null; //To change body of generated lambdas, choose Tools | Templates.
            }, patientId, facilityId, prescriptionType, Constants.Prescription.PRESCRIBED);

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return prescriptionList;
    }

    public static void changeDrugPrescriptionStatus(Long patientId, Long facilityId, int regimenId, int regimenTypeId, Integer oldStatus, Integer newStatus) {
        try {
            String prescriptionType = "drug";

            String query = "UPDATE prescription SET status = ?, time_stamp = ? WHERE patient_id = ? AND facility_id = ? AND prescription_type = ? AND status = ? AND regimen_id = ? AND regimentype_id = ?";

            transactionTemplate.execute((ts) -> {
                jdbcTemplate.update(query, newStatus, new Date(),
                        patientId, facilityId, prescriptionType, oldStatus, regimenId, regimenTypeId);
                return null; //To change body of generated lambdas, choose Tools | Templates.
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void changeLabtestPrescriptionStatus(Long patientId, Long facilityId, int labtestId, Integer oldStatus, Integer newStatus) {
        try {
            String prescriptionType = "labtest";

            String query = "UPDATE prescription SET status = ?, time_stamp = ? WHERE patient_id = ? AND facility_id = ? AND prescription_type = ? AND status = ? AND labtest_id = ?";

            transactionTemplate.execute((ts) -> {
                jdbcTemplate.update(query, newStatus, new Date(),
                        patientId, facilityId, prescriptionType, oldStatus, oldStatus, labtestId);
                return null; //To change body of generated lambdas, choose Tools | Templates.
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void changePrescriptionStatusUn(Long patientId, Long facilityId, Integer oldStatus, Integer newStatus, String prescriptionType) {
        try {
            //String prescriptionType = "drug";

            String query = "UPDATE prescription SET status = ?, time_stamp = ? WHERE patient_id = ? AND facility_id = ? AND prescription_type = ? AND status = ?";

            transactionTemplate.execute((ts) -> {
                jdbcTemplate.update(query, newStatus, new Date(),
                        patientId, facilityId, prescriptionType, oldStatus);
                return null; //To change body of generated lambdas, choose Tools | Templates.
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
