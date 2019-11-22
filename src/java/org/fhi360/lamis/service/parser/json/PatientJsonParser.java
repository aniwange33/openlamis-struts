/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.parser.json;

import java.io.IOException;
import javax.xml.bind.JAXBException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
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

    public PatientJsonParser() {
    }
    
      public void parserJson(String content) {
        try {
            JSONArray patients = new JSONArray(content); 
            System.out.println(".....................Patient: "+patients);            
            
            for (int i = 0; i < patients.length(); i++) {
                JSONObject record = patients.optJSONObject(i);
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
            patient = objectMapper.readValue(content, Patient.class);
        } catch(Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
        return patient;
    }
           
}
