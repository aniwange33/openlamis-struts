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
import org.fhi360.lamis.dao.hibernate.PartnerinformationDAO;
import org.fhi360.lamis.dao.jdbc.PartnerInformationJDBC;
import org.fhi360.lamis.dao.jdbc.PatientJDBC;
import org.fhi360.lamis.model.Partnerinformation;
import org.fhi360.lamis.model.Patient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Idris
 */
public class PartnerInformationJsonParser {
    
    public void parserJson(String table, String content) {
        try {

            JSONObject jsonObj = new JSONObject(content);
            JSONArray jsonArray = jsonObj.optJSONArray(table);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject record = jsonArray.optJSONObject(i);
                Partnerinformation partnerinformation = getObject(record.toString());
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
              
                 long patientId = new PatientJDBC().getPatientId(partnerinformation.getFacilityId(), partnerinformation.getPatient().getHospitalNum());
                Patient patient = new Patient();
                patient.setPatientId(patientId);
                
                partnerinformation.setPatient(patient);
                long opartnerinformationId = new PartnerInformationJDBC().getPartnerInformationId(partnerinformation.getFacilityId(),
                         patientId, dateFormat.format(partnerinformation.getDateVisit()));
                if (opartnerinformationId == 0L) {
                    PartnerinformationDAO.save(partnerinformation);
                } else {
                    partnerinformation.setPartnerinformationId(opartnerinformationId);
                   PartnerinformationDAO.update(partnerinformation);
                }

            }
        } catch (IOException | JAXBException | JSONException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static Partnerinformation getObject(String content) throws JAXBException, JsonParseException, JsonMappingException, IOException {
        Partnerinformation partnerInformation = new Partnerinformation();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            partnerInformation = objectMapper.readValue(content, Partnerinformation.class);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        return partnerInformation;
    }
}
