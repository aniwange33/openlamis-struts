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
import org.fhi360.lamis.dao.hibernate.DrugtherapyDAO;
import org.fhi360.lamis.dao.jdbc.DrugtherapyJDBC;
import org.fhi360.lamis.dao.jdbc.PatientJDBC;
import org.fhi360.lamis.model.Drugtherapy;
import org.fhi360.lamis.model.Patient;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author user10
 */
public class DrugtherapyJsonParser {

    public void parserJson(String table, String content) {

        try {
            System.out.println("Hts JSON :" + content);
            JSONObject jsonObj = new JSONObject(content);
            JSONArray jsonArray = jsonObj.optJSONArray(table);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject record = jsonArray.optJSONObject(i);

                Drugtherapy drugtherapy = getObject(record.toString());
                drugtherapy.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

               long patientId = new PatientJDBC().getPatientId(drugtherapy.getFacilityId(), drugtherapy.getPatient().getHospitalNum());
                Patient patient = new Patient();
                patient.setPatientId(patientId);
                drugtherapy.setPatient(patient);

                long drugtherapyId = new DrugtherapyJDBC().getDrugtherapyId(drugtherapy.getFacilityId(), drugtherapy.getPatientId(), dateFormat.format(drugtherapy.getDateVisit()));
                if (drugtherapyId == 0L) {
                    DrugtherapyDAO.save(drugtherapy);
                } else {
                    drugtherapy.setDrugtherapyId(drugtherapyId);
                    DrugtherapyDAO.update(drugtherapy);
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }

    }

    private static Drugtherapy getObject(String content) throws JAXBException, JsonParseException, JsonMappingException, IOException {
        Drugtherapy drugtherapy = new Drugtherapy();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            drugtherapy = objectMapper.readValue(content, Drugtherapy.class);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
        return drugtherapy;
    }

}
