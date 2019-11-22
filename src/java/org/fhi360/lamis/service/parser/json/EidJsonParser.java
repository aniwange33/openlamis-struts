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
import org.fhi360.lamis.dao.hibernate.EidDAO;
import org.fhi360.lamis.dao.jdbc.EidJDBC;
import org.fhi360.lamis.model.Eid;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Idris
 */
public class EidJsonParser {
     
    public void parserJson(String table, String content) {
        try {

            JSONObject jsonObj = new JSONObject(content);
            JSONArray jsonArray = jsonObj.optJSONArray(table);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject record = jsonArray.optJSONObject(i);
                Eid eid = getObject(record.toString());
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
              
                long eidId = new EidJDBC().getEidId(eid.getFacilityId(), eid.getLabno());
                if (eidId == 0L) {
                   EidDAO.save(eid);
                } else {
                    eid.setEidId(eidId);
                    EidDAO.update(eid);
                }

            }
        } catch (IOException | JAXBException | JSONException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static Eid getObject(String content) throws JAXBException, JsonParseException, JsonMappingException, IOException {
        Eid eid = new Eid();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            eid = objectMapper.readValue(content, Eid.class);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        return eid;
    }
}
