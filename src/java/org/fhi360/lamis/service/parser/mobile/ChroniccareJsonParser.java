/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.parser.mobile;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.xml.bind.JAXBException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.fhi360.lamis.dao.hibernate.ChroniccareDAO;
import org.fhi360.lamis.dao.jdbc.ChroniccareJDBC;
import org.fhi360.lamis.dao.jdbc.PatientJDBC;
import org.fhi360.lamis.model.Chroniccare;
import org.fhi360.lamis.model.Patient;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author user1
 */
public class ChroniccareJsonParser {
    
    public void parserJson(String table, String content) {
        
        try {
            System.out.println("Chroniccare JSON :" + content);
            JSONObject jsonObj = new JSONObject(content);            
            JSONArray jsonArray = jsonObj.optJSONArray(table);
            
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject record = jsonArray.optJSONObject(i);
                
                Chroniccare chroniccare = getObject(record.toString());
                chroniccare.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
                
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                long patientId = new PatientJDBC().getPatientId(chroniccare.getFacilityId(), chroniccare.getPatient().getHospitalNum());
                Patient patient = new Patient();
                patient.setPatientId(patientId);
                chroniccare.setPatient(patient);
                
                long chroniccareId = new ChroniccareJDBC().getChroniccareId(chroniccare.getFacilityId(), patientId, dateFormat.format(chroniccare.getDateVisit()));
                if (chroniccareId == 0L) {
                    ChroniccareDAO.save(chroniccare);
                } else {
                    chroniccare.setChroniccareId(chroniccareId);
                    ChroniccareDAO.update(chroniccare);
                }
            }
            
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
        
    }
    
    private static Chroniccare getObject(String content) throws JAXBException, JsonParseException, JsonMappingException, IOException {
        Chroniccare chroniccare = new Chroniccare();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            chroniccare = objectMapper.readValue(content, Chroniccare.class);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
        return chroniccare;
    }
}
