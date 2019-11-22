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
import org.fhi360.lamis.dao.hibernate.DeliveryDAO;
import org.fhi360.lamis.dao.jdbc.DeliveryJDBC;
import org.fhi360.lamis.dao.jdbc.PatientJDBC;
import org.fhi360.lamis.model.Delivery;
import org.fhi360.lamis.model.Patient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Idris
 */
public class DeliveryJsonParser {
    
    public void parserJson(String table, String content) {
        try {

            JSONObject jsonObj = new JSONObject(content);
            JSONArray jsonArray = jsonObj.optJSONArray(table);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject record = jsonArray.optJSONObject(i);
                Delivery delivery = getObject(record.toString());
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                  long patientId = new PatientJDBC().getPatientId(delivery.getFacilityId(), delivery.getPatient().getHospitalNum());
                Patient patient = new Patient();
                patient.setPatientId(patientId);
                delivery.setPatient(patient);

                long deliveryId = new DeliveryJDBC().getDeliveryId(delivery.getFacilityId(), patientId, dateFormat.format(delivery.getDateDelivery()));
                if (deliveryId == 0L) {
                    DeliveryDAO.save(delivery);
                } else {
                    delivery.setDeliveryId(deliveryId);
                    DeliveryDAO.update(delivery);
                }

            }
        } catch (IOException | JAXBException | JSONException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static Delivery getObject(String content) throws JAXBException, JsonParseException, JsonMappingException, IOException {
        Delivery delivery = new Delivery();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            delivery = objectMapper.readValue(content, Delivery.class);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        return delivery;
    }
}
