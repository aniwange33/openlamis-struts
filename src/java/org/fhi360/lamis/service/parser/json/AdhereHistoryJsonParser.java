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
import org.fhi360.lamis.dao.hibernate.AdherehistoryDAO;
import org.fhi360.lamis.dao.jdbc.AdherehistoryJDBC;
import org.fhi360.lamis.dao.jdbc.PatientJDBC;
import org.fhi360.lamis.model.Adherehistory;
import org.fhi360.lamis.model.Patient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Idris
 */
public class AdhereHistoryJsonParser {

    public void parserJson(String table, String content) {
        try {
            JSONObject jsonObj = new JSONObject(content);
            JSONArray jsonArray = jsonObj.optJSONArray(table);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject record = jsonArray.optJSONObject(i);
                Adherehistory adherehistory = getObject(record.toString());
                adherehistory.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                long patientId = new PatientJDBC().getPatientId(adherehistory.getFacilityId(), adherehistory.getPatient().getHospitalNum());
                Patient patient = new Patient();
                patient.setPatientId(patientId);
                adherehistory.setPatient(patient);

                long adherHistorytId = new AdherehistoryJDBC().getAdherHistoryId(adherehistory.getFacilityId(), patientId, dateFormat.format(adherehistory.getDateVisit()));
                if (adherHistorytId == 0L) {
                    AdherehistoryDAO.save(adherehistory);
                } else {
                    adherehistory.setHistoryId(adherHistorytId);
                    AdherehistoryDAO.update(adherehistory);
                }
            }
        } catch (IOException | JAXBException | JSONException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static Adherehistory getObject(String content) throws JAXBException, JsonParseException, JsonMappingException, IOException {
        Adherehistory adhere = new Adherehistory();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            adhere = objectMapper.readValue(content, Adherehistory.class);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        return adhere;
    }

}
