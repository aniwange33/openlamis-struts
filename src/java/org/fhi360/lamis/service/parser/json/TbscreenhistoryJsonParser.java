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
import org.fhi360.lamis.dao.hibernate.TbscreenhistoryDAO;
import org.fhi360.lamis.dao.jdbc.PatientJDBC;
import org.fhi360.lamis.dao.jdbc.TbscreenhistoryJDBC;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.model.Tbscreenhistory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Idris
 */
public class TbscreenhistoryJsonParser {

    public void parserJson(String table, String content) {
        try {

            JSONObject jsonObj = new JSONObject(content);
            JSONArray jsonArray = jsonObj.optJSONArray(table);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject record = jsonArray.optJSONObject(i);
                Tbscreenhistory tbscreenhistory = getObject(record.toString());
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                long patientId = new PatientJDBC().getPatientId(tbscreenhistory.getFacilityId(), tbscreenhistory.getPatient().getHospitalNum());
                Patient patient = new Patient();
                patient.setPatientId(patientId);
                tbscreenhistory.setPatient(patient);
                long statusId = new TbscreenhistoryJDBC().getTbscreenhistoryId(tbscreenhistory.getFacilityId(),
                        patientId, dateFormat.format(tbscreenhistory.getDateVisit()));
                if (statusId == 0L) {
                    TbscreenhistoryDAO.save(tbscreenhistory);
                } else {
                    tbscreenhistory.setHistoryId(statusId);
                    TbscreenhistoryDAO.update(tbscreenhistory);
                }

            }
        } catch (IOException | JAXBException | JSONException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static Tbscreenhistory getObject(String content) throws JAXBException, JsonParseException, JsonMappingException, IOException {
        Tbscreenhistory tbscreenhistory = new Tbscreenhistory();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            tbscreenhistory = objectMapper.readValue(content, Tbscreenhistory.class);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        return tbscreenhistory;
    }
}
