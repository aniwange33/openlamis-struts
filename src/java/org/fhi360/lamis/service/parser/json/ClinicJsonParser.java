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
import org.fhi360.lamis.dao.hibernate.ClinicDAO;
import org.fhi360.lamis.dao.jdbc.ClinicJDBC;
import org.fhi360.lamis.dao.jdbc.PatientJDBC;
import org.fhi360.lamis.model.Clinic;
import org.fhi360.lamis.model.Patient;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author user10
 */
public class ClinicJsonParser {

    public ClinicJsonParser() {
    }
    
    
      public void parserJson(String content) {
        try {
            JSONArray clinics = new JSONArray(content); 
            System.out.println("........................clinic: "+clinics);            
            for (int i = 0; i < clinics.length(); i++) {
                JSONObject record = clinics.optJSONObject(i);
                Clinic clinic = getObject(record.toString());
                clinic.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
                                               
                long patientId = new PatientJDBC().getPatientId(clinic.getFacilityId(), clinic.getPatient().getHospitalNum());
                long clinicId = new ClinicJDBC().getClinicId(patientId, clinic.getDateVisit());
                Patient patient = new Patient();
                patient.setPatientId(patientId);
                clinic.setPatient(patient);
                if(clinicId == 0L) {
                    ClinicDAO.save(clinic);
                }
                else {
                    clinic.setClinicId(clinicId);
                    ClinicDAO.update(clinic);
                }                                    
            }
        } 
        catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }        

    }
 
     private static Clinic getObject(String content) throws JAXBException, JsonParseException, JsonMappingException, IOException {
        Clinic clinic = new Clinic();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            clinic = objectMapper.readValue(content, Clinic.class);
        } catch(Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
        return clinic;
    }
      
}
