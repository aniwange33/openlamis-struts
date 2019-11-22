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
import org.fhi360.lamis.dao.hibernate.AdrhistoryDAO;
import org.fhi360.lamis.dao.jdbc.AdrhistoryJDBC;
import org.fhi360.lamis.dao.jdbc.PatientJDBC;
import org.fhi360.lamis.model.Adrhistory;
import org.fhi360.lamis.model.Patient;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Idris
 */
public class AdrhistoryJsonParser {

    public void parserJson(String table, String content) {
        try {

            JSONObject jsonObj = new JSONObject(content);
            JSONArray jsonArray = jsonObj.optJSONArray(table);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject record = jsonArray.optJSONObject(i);
                Adrhistory adrhistory = getObject(record.toString());
                adrhistory.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                long patientId = new PatientJDBC().getPatientId(adrhistory.getFacilityId(), adrhistory.getPatient().getHospitalNum());
                Patient patient = new Patient();
                patient.setPatientId(patientId);
                adrhistory.setPatient(patient);
                long adrHistorytId = new AdrhistoryJDBC().getAdrHistoryId(adrhistory.getFacilityId(), patientId, dateFormat.format(adrhistory.getDateVisit()));
                if (adrHistorytId == 0L) {
                    AdrhistoryDAO.save(adrhistory);
                } else {
                    adrhistory.setHistoryId(adrHistorytId);
                    AdrhistoryDAO.update(adrhistory);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }

    private static Adrhistory getObject(String content) throws JAXBException, JsonParseException, JsonMappingException, IOException {
        Adrhistory adrhistory = new Adrhistory();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            adrhistory = objectMapper.readValue(content, Adrhistory.class);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
        return adrhistory;
    }

}
