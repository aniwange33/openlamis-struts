/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.parser.mobile;

import java.io.IOException;
import javax.xml.bind.JAXBException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.text.SimpleDateFormat;
import org.fhi360.lamis.dao.hibernate.PatientDAO;
import org.fhi360.lamis.dao.jdbc.PatientJDBC;
import org.fhi360.lamis.model.Patient;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author user10
 */
public class PatientJsonParser {
    
      public void  parserJson(String table, String content) {
        try {
            System.out.println("Hts JSON :"+content);
            JSONObject jsonObj = new JSONObject(content);              
            JSONArray jsonArray = jsonObj.optJSONArray(table);
            
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject record = jsonArray.optJSONObject(i);
                Patient patient = getObject(record.toString());
                patient.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
                                               
                long patientId = new PatientJDBC().getPatientId(patient.getFacility().getFacilityId(), patient.getHospitalNum());
                if(patientId == 0L) {
                    PatientDAO.save(patient);
                }
                else {
                    patient.setPatientId(patientId);
                    PatientDAO.update(patient);
                }                                    
            }
        } 
        catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }        

      }
      
     private static Patient getObject(String content) throws JAXBException, JsonParseException, JsonMappingException, IOException {
        Patient patient = new Patient();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
             objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
             objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            patient = objectMapper.readValue(content, Patient.class);
        } catch(Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
        return patient;
    }
           
}
