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
import org.fhi360.lamis.dao.hibernate.AppointmentDAO;
import org.fhi360.lamis.dao.jdbc.AppointmentJDBC;
import org.fhi360.lamis.model.Appointment;
import org.fhi360.lamis.model.Patient;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author user10
 */
public class AppointmentJsonParser {

    public void parserJson(String table, String content) {

        try {
            System.out.println("Appointment JSON :" + content);
            JSONObject jsonObj = new JSONObject(content);
            JSONArray jsonArray = jsonObj.optJSONArray(table);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject record = jsonArray.optJSONObject(i);

                Appointment appointment = getObject(record.toString());
                appointment.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                //long patientId = new PatientJDBC().getPatientId(appointment.getFacilityId(), appointment.getPatient().getHospitalNum());
                Patient patient = new Patient();
                patient.setPatientId(appointment.getPatientId());
                appointment.setPatient(patient);

                long appointmentId = new AppointmentJDBC().getAppointmentId(appointment.getFacilityId(), appointment.getPatientId(), dateFormat.format(appointment.getDateTracked()));
                if (appointmentId == 0L) {
                    AppointmentDAO.save(appointment);
                } else {
                    appointment.setAppointmentId(appointmentId);
                    AppointmentDAO.update(appointment);
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }

    }

    private static Appointment getObject(String content) throws JAXBException, JsonParseException, JsonMappingException, IOException {
        Appointment appointment = new Appointment();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            appointment = objectMapper.readValue(content, Appointment.class);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
        return appointment;
    }

}
