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
import org.fhi360.lamis.dao.hibernate.SpecimenDAO;
import org.fhi360.lamis.dao.hibernate.StatushistoryDAO;
import org.fhi360.lamis.dao.jdbc.PatientJDBC;
import org.fhi360.lamis.dao.jdbc.SpecimenJDBC;
import org.fhi360.lamis.dao.jdbc.StatusHistoryJDBC;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.model.Specimen;
import org.fhi360.lamis.model.Statushistory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author user10
 */
public class StatusHistoryJsonParser {
    
    public void parserJson(String table, String content) {
        try {

            JSONObject jsonObj = new JSONObject(content);
            JSONArray jsonArray = jsonObj.optJSONArray(table);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject record = jsonArray.optJSONObject(i);
                Statushistory statusHistory = getObject(record.toString());
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                 long patientId = new PatientJDBC().getPatientId(statusHistory.getFacilityId(), statusHistory.getPatient().getHospitalNum());
                Patient patient = new Patient();
                patient.setPatientId(patientId);  
              
                statusHistory.setPatient(patient);
                long statusId = new StatusHistoryJDBC().getStatusHistoryId(statusHistory.getFacilityId(),
                        patientId, dateFormat.format(statusHistory.getDateCurrentStatus()));
                if (statusId == 0L) {
                    StatushistoryDAO.save(statusHistory);
                } else {
                    statusHistory.setHistoryId(statusId);
                    StatushistoryDAO.update(statusHistory);
                }

            }
        } catch (IOException | JAXBException | JSONException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static Statushistory getObject(String content) throws JAXBException, JsonParseException, JsonMappingException, IOException {
        Statushistory statushistory = new Statushistory();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            statushistory = objectMapper.readValue(content, Statushistory.class);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        return statushistory;
    }
}
