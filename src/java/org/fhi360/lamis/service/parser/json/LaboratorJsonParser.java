/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.parser.json;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.xml.bind.JAXBException;
import org.fhi360.lamis.dao.hibernate.LaboratoryDAO;
import org.fhi360.lamis.dao.jdbc.LaboratoryJDBC;
import org.fhi360.lamis.dao.jdbc.PatientJDBC;
import org.fhi360.lamis.model.Laboratory;
import org.fhi360.lamis.model.Patient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Idris
 */
public class LaboratorJsonParser {

    public void parserJson(String table, String content) {
        try {

            JSONObject jsonObj = new JSONObject(content);
            JSONArray jsonArray = jsonObj.optJSONArray(table);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject record = jsonArray.optJSONObject(i);
                Laboratory laboratory = getObject(record.toString());
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                 long patientId = new PatientJDBC().getPatientId(laboratory.getFacilityId(), laboratory.getPatient().getHospitalNum());
                Patient patient = new Patient();
                patient.setPatientId(patientId);

                laboratory.setPatient(patient);
                long laboratoryId = new LaboratoryJDBC().getLabouratoryId(patientId,
                         laboratory.getFacilityId(), dateFormat.format(laboratory.getDateReported()));
                if (laboratoryId == 0L) {
                    LaboratoryDAO.save(laboratory);
                } else {
                    laboratory.setLaboratoryId(laboratoryId);
                    LaboratoryDAO.update(laboratory);
                }

            }
        } catch (IOException | JAXBException | JSONException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static Laboratory getObject(String content) throws JAXBException, JsonParseException, JsonMappingException, IOException {
        Laboratory laboratory = new Laboratory();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            laboratory = objectMapper.readValue(content, Laboratory.class);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        return laboratory;
    }
}
