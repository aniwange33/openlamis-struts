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
import org.fhi360.lamis.dao.hibernate.OihistoryDAO;
import org.fhi360.lamis.dao.jdbc.OIHistoryJDBC;
import org.fhi360.lamis.dao.jdbc.PatientJDBC;
import org.fhi360.lamis.model.Oihistory;
import org.fhi360.lamis.model.Patient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Idris
 */
public class OiHistoryJsonParser {

    public void parserJson(String table, String content) {
        try {

            JSONObject jsonObj = new JSONObject(content);
            JSONArray jsonArray = jsonObj.optJSONArray(table);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject record = jsonArray.optJSONObject(i);
                Oihistory oihistory = getObject(record.toString());
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                long patientId = new PatientJDBC().getPatientId(oihistory.getFacilityId(), oihistory.getPatient().getHospitalNum());
                Patient patient = new Patient();
                patient.setPatientId(patientId);

                oihistory.setPatient(patient);
                long oihistoryId = new OIHistoryJDBC().getOIHistoryId(oihistory.getFacilityId(),
                        patientId, dateFormat.format(oihistory.getDateVisit()));
                if (oihistoryId == 0L) {
                    OihistoryDAO.save(oihistory);
                } else {
                    oihistory.setHistoryId(oihistoryId);
                    OihistoryDAO.update(oihistory);
                }

            }
        } catch (IOException | JAXBException | JSONException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static Oihistory getObject(String content) throws JAXBException, JsonParseException, JsonMappingException, IOException {
        Oihistory oihistory = new Oihistory();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            oihistory = objectMapper.readValue(content, Oihistory.class);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        return oihistory;
    }
}
