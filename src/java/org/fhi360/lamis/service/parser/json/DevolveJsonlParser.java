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
import org.fhi360.lamis.dao.hibernate.DevolveDAO;
import org.fhi360.lamis.dao.jdbc.DevolveJDBC;
import org.fhi360.lamis.dao.jdbc.PatientJDBC;
import org.fhi360.lamis.model.Devolve;
import org.fhi360.lamis.model.Patient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Idris
 */
public class DevolveJsonlParser {
    
    public void parserJson(String table, String content) {
        try {

            JSONObject jsonObj = new JSONObject(content);
            JSONArray jsonArray = jsonObj.optJSONArray(table);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject record = jsonArray.optJSONObject(i);
                Devolve devolve = getObject(record.toString());
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                 long patientId = new PatientJDBC().getPatientId(devolve.getFacilityId(), devolve.getPatient().getHospitalNum());
                Patient patient = new Patient();
                patient.setPatientId(patientId);
                
                devolve.setPatient(patient);

                long devolveId = new DevolveJDBC().getDevolvedId(devolve.getFacilityId(), patientId, dateFormat.format(devolve.getDateDevolved()));
                if (devolveId == 0L) {
                    DevolveDAO.save(devolve);
                } else {
                    devolve.setDevolveId(devolveId);
                    DevolveDAO.update(devolve);
                }

            }
        } catch (IOException | JAXBException | JSONException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static Devolve getObject(String content) throws JAXBException, JsonParseException, JsonMappingException, IOException {
        Devolve devolve = new Devolve();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            devolve = objectMapper.readValue(content, Devolve.class);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        return devolve;
    }
}
