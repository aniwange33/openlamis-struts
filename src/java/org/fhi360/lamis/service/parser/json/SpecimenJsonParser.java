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
import org.fhi360.lamis.dao.hibernate.RegimenhistoryDAO;
import org.fhi360.lamis.dao.hibernate.SpecimenDAO;
import org.fhi360.lamis.dao.jdbc.RegimenHistoryJDBC;
import org.fhi360.lamis.dao.jdbc.SpecimenJDBC;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.model.Regimenhistory;
import org.fhi360.lamis.model.Specimen;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Idris
 */
public class SpecimenJsonParser {

    public void parserJson(String table, String content) {
        try {

            JSONObject jsonObj = new JSONObject(content);
            JSONArray jsonArray = jsonObj.optJSONArray(table);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject record = jsonArray.optJSONObject(i);
                Specimen specimen = getObject(record.toString());
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                long specimenId = new SpecimenJDBC().getRegimenId(specimen.getFacilityId(),
                        specimen.getLabno(), dateFormat.format(specimen.getDateReceived()));
                if (specimenId == 0L) {
                    SpecimenDAO.save(specimen);
                } else {
                    specimen.setSpecimenId(specimenId);
                    SpecimenDAO.update(specimen);
                }

            }
        } catch (IOException | JAXBException | JSONException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static Specimen getObject(String content) throws JAXBException, JsonParseException, JsonMappingException, IOException {
        Specimen specimen = new Specimen();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            specimen = objectMapper.readValue(content, Specimen.class);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        return specimen;
    }
}
