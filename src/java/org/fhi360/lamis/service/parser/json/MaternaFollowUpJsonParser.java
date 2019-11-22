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
import org.fhi360.lamis.dao.hibernate.MaternalfollowupDAO;
import org.fhi360.lamis.dao.jdbc.MaternaFollowUpJDBC;
import org.fhi360.lamis.dao.jdbc.PatientJDBC;
import org.fhi360.lamis.model.Maternalfollowup;
import org.fhi360.lamis.model.Patient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Idris
 */
public class MaternaFollowUpJsonParser {

    public void parserJson(String table, String content) {
        try {

            JSONObject jsonObj = new JSONObject(content);
            JSONArray jsonArray = jsonObj.optJSONArray(table);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject record = jsonArray.optJSONObject(i);
                Maternalfollowup maternalfollowup = getObject(record.toString());
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                long patientId = new PatientJDBC().getPatientId(maternalfollowup.getFacilityId(), maternalfollowup.getPatient().getHospitalNum());
                Patient patient = new Patient();
                patient.setPatientId(patientId);

                maternalfollowup.setPatient(patient);
                long maternaFollowUpId = new MaternaFollowUpJDBC().getMaternaFollowUpId(maternalfollowup.getFacilityId(),
                        maternalfollowup.getPatientId(), dateFormat.format(maternalfollowup.getDateVisit()));
                if (maternaFollowUpId == 0L) {
                    MaternalfollowupDAO.save(maternalfollowup);
                } else {
                    maternalfollowup.setMaternalfollowupId(maternaFollowUpId);
                    MaternalfollowupDAO.update(maternalfollowup);
                }

            }
        } catch (IOException | JAXBException | JSONException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static Maternalfollowup getObject(String content) throws JAXBException, JsonParseException, JsonMappingException, IOException {
        Maternalfollowup maternalfollowup = new Maternalfollowup();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            maternalfollowup = objectMapper.readValue(content, Maternalfollowup.class);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        return maternalfollowup;
    }
}
