/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.parser.mobile;

import java.io.IOException;
import javax.xml.bind.JAXBException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.text.SimpleDateFormat;
import org.fhi360.lamis.dao.hibernate.ClinicDAO;
import org.fhi360.lamis.dao.jdbc.ClinicJDBC;
import org.fhi360.lamis.dao.jdbc.PatientJDBC;
import org.fhi360.lamis.model.Clinic;
import org.fhi360.lamis.model.Patient;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author user10
 */
public class ClinicJsonParser {

    public void parserJson(String table, String content) {
        try {
            System.out.println("Clinic JSON :" + content);
            JSONObject jsonObj = new JSONObject(content);
            JSONArray jsonArray = jsonObj.optJSONArray(table);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject record = jsonArray.optJSONObject(i);

                Clinic clinic = getObject(record.toString());
                clinic.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));

                long patientId = new PatientJDBC().getPatientId(clinic.getFacilityId(), clinic.getPatient().getHospitalNum());
                Patient patient = new Patient();
                patient.setPatientId(patientId);
                clinic.setPatient(patient);
                long clinicId = new ClinicJDBC().getClinicId(patientId, clinic.getDateVisit());
                if (clinicId == 0L) {
                    ClinicDAO.save(clinic);
                } else {
                    clinic.setClinicId(clinicId);
                    ClinicDAO.update(clinic);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }

    }

    private static Clinic getObject(String content) throws JAXBException, JsonParseException, JsonMappingException, IOException {
        Clinic clinic = new Clinic();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            clinic = objectMapper.readValue(content, Clinic.class);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
        return clinic;
    }

}
