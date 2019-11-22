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
import org.fhi360.lamis.dao.jdbc.PatientJDBC;
import org.fhi360.lamis.dao.jdbc.PharmacyJDBC;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.model.Pharmacy;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Idris
 */
public class PharmacyJsonParser {
    
    public void parserJson(String table, String content) {
        try {

            JSONObject jsonObj = new JSONObject(content);
            JSONArray jsonArray = jsonObj.optJSONArray(table);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject record = jsonArray.optJSONObject(i);
                Pharmacy pharmacy = getObject(record.toString());
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
              
                   long patientId = new PatientJDBC().getPatientId(pharmacy.getFacilityId(), pharmacy.getPatient().getHospitalNum());
                Patient patient = new Patient();
                patient.setPatientId(patientId);
                
                pharmacy.setPatient(patient);
                long pharmacyId = new PharmacyJDBC().getPharmacyManagerId(pharmacy.getFacilityId(),
                         patientId, dateFormat.format(pharmacy.getDateVisit()));
                if (pharmacyId == 0L) {
                    PharmacyDAO.save(pharmacy);
                } else {
                    pharmacy.setPharmacyId(pharmacyId);
                   PharmacyDAO.update(pharmacy);
                }

            }
        } catch (IOException | JAXBException | JSONException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static Pharmacy getObject(String content) throws JAXBException, JsonParseException, JsonMappingException, IOException {
        Pharmacy pharmacy = new Pharmacy();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            pharmacy = objectMapper.readValue(content, Pharmacy.class);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        return pharmacy;
    }
}
