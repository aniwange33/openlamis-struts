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
import org.fhi360.lamis.dao.hibernate.PharmacyDAO;
import org.fhi360.lamis.dao.hibernate.RegimenhistoryDAO;
import org.fhi360.lamis.dao.jdbc.PatientJDBC;
import org.fhi360.lamis.dao.jdbc.PharmacyJDBC;
import org.fhi360.lamis.dao.jdbc.RegimenHistoryJDBC;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.model.Pharmacy;
import org.fhi360.lamis.model.Regimen;
import org.fhi360.lamis.model.Regimenhistory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author user10
 */
public class RegimenHistoryJsonParser {
    
    public void parserJson(String table, String content) {
        try {

            JSONObject jsonObj = new JSONObject(content);
            JSONArray jsonArray = jsonObj.optJSONArray(table);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject record = jsonArray.optJSONObject(i);
                 Regimenhistory  regimenhistory = getObject(record.toString());
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                
                  long patientId = new PatientJDBC().getPatientId(regimenhistory.getFacilityId(), regimenhistory.getPatient().getHospitalNum());
                Patient patient = new Patient();
                patient.setPatientId(patientId);
                
                regimenhistory.setPatient(patient);
                
                long regimenhistoryId = new RegimenHistoryJDBC().getRegimenHistoryId(regimenhistory.getFacilityId(),
                         patientId, dateFormat.format(regimenhistory.getDateVisit()));
                if (regimenhistoryId == 0L) {
                    RegimenhistoryDAO.save(regimenhistory);
                } else {
                    regimenhistory.setHistoryId(regimenhistoryId);
                   RegimenhistoryDAO.update(regimenhistory);
                }

            }
        } catch (IOException | JAXBException | JSONException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static Regimenhistory getObject(String content) throws JAXBException, JsonParseException, JsonMappingException, IOException {
        Regimenhistory regimenhistory = new Regimenhistory();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            regimenhistory = objectMapper.readValue(content, Regimenhistory.class);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        return regimenhistory;
    }
}
