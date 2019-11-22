/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.parser.json;

import org.fhi360.lamis.dao.hibernate.AppointmentDAO;
import org.fhi360.lamis.dao.jdbc.AppointmentJDBC;
import org.fhi360.lamis.model.Appointment;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.utility.DateUtil;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author user10
 */
public class AppointmentJsonParser {

    public AppointmentJsonParser() {
    }
    
    
    public void parserJson(String content) {
        try {
            JSONArray appointments = new JSONArray(content); 
            System.out.println("JSON: "+appointments);
            for (int i = 0; i < appointments.length(); i++) {
                JSONObject record = appointments.optJSONObject(i);
                long facilityId = record.getLong("facility_id");
                long patientId = record.getLong("patient_id");
                long communitypharmId = record.getLong("communitypharm_id");
                String dateTracked = record.getString("date_tracked"); 
                String typeTracking = record.getString("type_tracking");
                String trackingOutcome = record.getString("tracking_outcome");
                String dateLastVisit = record.getString("date_last_visit"); 
                String dateNextVisit = record.getString("date_next_visit"); 
                String dateAgreed = record.getString("date_agreed");

                Appointment appointment = new Appointment();
                appointment.setFacilityId(facilityId);
                Patient patient = new Patient();
                patient.setPatientId(patientId);
                appointment.setPatient(patient);
                appointment.setCommunitypharmId(communitypharmId);
                appointment.setDateTracked(DateUtil.parseStringToDate(dateTracked, "yyyy-MM-dd"));
                appointment.setTypeTracking(typeTracking);
                //appointment.setTrackingOutcome(trackingOutcome);
                appointment.setDateLastVisit(DateUtil.parseStringToDate(dateLastVisit, "yyyy-MM-dd"));
                appointment.setDateNextVisit(DateUtil.parseStringToDate(dateNextVisit, "yyyy-MM-dd"));
                appointment.setDateAgreed(DateUtil.parseStringToDate(dateAgreed, "yyyy-MM-dd"));
                appointment.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));  
                
                long appointmentId = new AppointmentJDBC().getAppointmentId(facilityId, patientId, dateTracked);
                if(appointmentId == 0L) {
                    AppointmentDAO.save(appointment);
                }
                else {
                    appointment.setAppointmentId(appointmentId);
                    AppointmentDAO.update(appointment);
                }                                    
            }
        } 
        catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }        

    }
    
}
