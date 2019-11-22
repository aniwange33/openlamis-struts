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
import java.text.SimpleDateFormat;
import javax.xml.bind.JAXBException;
import org.fhi360.lamis.dao.hibernate.ChildDAO;
import org.fhi360.lamis.dao.jdbc.ChildJDBC;
import org.fhi360.lamis.model.Child;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Idris
 */
public class ChildJsonParser {

    public void parserJson(String table, String content) {
        try {

            JSONObject jsonObj = new JSONObject(content);
            JSONArray jsonArray = jsonObj.optJSONArray(table);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject record = jsonArray.optJSONObject(i);
                Child child = getObject(record.toString());
                long childCareId = new ChildJDBC().getChildId(child.getFacilityId(), child.getPatientId(), child.getReferenceNum());
                if (childCareId == 0L) {
                    ChildDAO.save(child);
                } else {
                    child.setChildId(childCareId);
                    ChildDAO.update(child);
                }
            }
        } catch (IOException | JAXBException | JSONException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static Child getObject(String content) throws JAXBException, JsonParseException, JsonMappingException, IOException {
        Child child = new Child();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            child = objectMapper.readValue(content, Child.class);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        return child;
    }
}
