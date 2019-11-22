/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.parser.mobile;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.xml.bind.JAXBException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.fhi360.lamis.dao.hibernate.EncounterDAO;
import org.fhi360.lamis.dao.jdbc.EncounterJDBC;
import org.fhi360.lamis.dao.jdbc.PatientJDBC;
import org.fhi360.lamis.model.Encounter;
import org.fhi360.lamis.model.Patient;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author user1
 */
public class EncounterJsonParser {

    public void  parserJson(String table, String content) {

        try {
            System.out.println("Encounter JSON :"+content);
            JSONObject jsonObj = new JSONObject(content);              
            JSONArray jsonArray = jsonObj.optJSONArray(table);
            
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject record = jsonArray.optJSONObject(i);
                
                Encounter encounter = getObject(record.toString());
                encounter.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
                
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
               long patientId = new PatientJDBC().getPatientId(encounter.getFacilityId(), encounter.getPatient().getHospitalNum());
                Patient patient = new Patient();
                patient.setPatientId(patientId);
                encounter.setPatient(patient);
                long encounterId = new EncounterJDBC().getEncounterId(encounter.getFacilityId(),
                        encounter.getPatient().getPatientId(), dateFormat.format(encounter.getDateVisit()));
                if (encounterId == 0L) {
                    EncounterDAO.save(encounter);
                } else {
                    encounter.setEncounterId(encounterId);
                    EncounterDAO.update(encounter);
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }

    }

    private static Encounter getObject(String content) throws JAXBException, JsonParseException, JsonMappingException, IOException {
        Encounter encounter = new Encounter();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
             objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
             objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            encounter = objectMapper.readValue(content, Encounter.class);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
        return encounter;
    }

}
