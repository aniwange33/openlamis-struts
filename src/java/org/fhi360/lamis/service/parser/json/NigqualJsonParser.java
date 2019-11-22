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
import org.fhi360.lamis.dao.hibernate.NigqualDAO;
import org.fhi360.lamis.dao.jdbc.NigqualJDBC;
import org.fhi360.lamis.dao.jdbc.PatientJDBC;
import org.fhi360.lamis.model.Nigqual;
import org.fhi360.lamis.model.Patient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Idris
 */
public class NigqualJsonParser {
    
    public void parserJson(String table, String content) {
        try {

            JSONObject jsonObj = new JSONObject(content);
            JSONArray jsonArray = jsonObj.optJSONArray(table);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject record = jsonArray.optJSONObject(i);
                Nigqual nigqual = getObject(record.toString());
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
               
                
                long patientId = new PatientJDBC().getPatientId(nigqual.getFacilityId(), nigqual.getPatient().getHospitalNum());
                Patient patient = new Patient();
                patient.setPatientId(patientId);
                
                nigqual.setPatient(patient);
                long nigqualId = new NigqualJDBC().getNigqualId(nigqual.getFacilityId(),
                         patientId, nigqual.getReviewPeriodId());
                if (nigqualId == 0L) {
                    NigqualDAO.save(nigqual);
                } else {
                    nigqual.setNigqualId(nigqualId);
                    NigqualDAO.update(nigqual);
                }

            }
        } catch (IOException | JAXBException | JSONException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static Nigqual getObject(String content) throws JAXBException, JsonParseException, JsonMappingException, IOException {
        Nigqual nigqual = new Nigqual();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            nigqual = objectMapper.readValue(content, Nigqual.class);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        return nigqual;
    }
}
