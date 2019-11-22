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
import org.fhi360.lamis.dao.hibernate.AncDAO;
import org.fhi360.lamis.dao.jdbc.AncJDBC;
import org.fhi360.lamis.dao.jdbc.PatientJDBC;
import org.fhi360.lamis.model.Anc;
import org.fhi360.lamis.model.Patient;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Idris
 */
public class AncJsonParser {

    public void parserJson(String table, String content) {
        try {

            JSONObject jsonObj = new JSONObject(content);
            JSONArray jsonArray = jsonObj.optJSONArray(table);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject record = jsonArray.optJSONObject(i);
                Anc anc = getObject(record.toString());
                anc.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                long patientId = new PatientJDBC().getPatientId(anc.getFacilityId(), anc.getPatient().getHospitalNum());
                Patient patient = new Patient();
                patient.setPatientId(patientId);
                anc.setPatient(patient);
                long ancId = new AncJDBC().findAnc(anc.getFacilityId(), patientId, dateFormat.format(anc.getDateVisit()));
                if (ancId == 0L) {
                    AncDAO.save(anc);
                } else {
                    anc.setAncId(ancId);
                    AncDAO.update(anc);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }

    private static Anc getObject(String content) throws JAXBException, JsonParseException, JsonMappingException, IOException {
        Anc anc = new Anc();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            anc = objectMapper.readValue(content, Anc.class);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
        return anc;
    }

}
