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
import org.fhi360.lamis.dao.hibernate.ChildfollowupDAO;
import org.fhi360.lamis.dao.jdbc.ChildFollowUpJDBC;
import org.fhi360.lamis.model.Child;
import org.fhi360.lamis.model.Childfollowup;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Idris
 */
public class ChildFollowUpJsonParser {

    public void parserJson(String table, String content) {
        try {
            JSONObject jsonObj = new JSONObject(content);
            JSONArray jsonArray = jsonObj.optJSONArray(table);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject record = jsonArray.optJSONObject(i);
                Childfollowup childFollowUp = getObject(record.toString());
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Child child = new Child();
                child.setReferenceNum(childFollowUp.getReferenceNum());
                childFollowUp.setChild(child);
                long childFollowUpId = new ChildFollowUpJDBC().getChildFollowUpId(childFollowUp.getFacilityId(), childFollowUp.getReferenceNum(), dateFormat.format(childFollowUp.getDateVisit()));
                if (childFollowUpId == 0l) {
                    ChildfollowupDAO.save(childFollowUp);
                } else {
                    childFollowUp.setChildfollowupId(childFollowUpId);
                    ChildfollowupDAO.update(childFollowUp);
                }
            }
        } catch (IOException | JAXBException | JSONException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static Childfollowup getObject(String content) throws JAXBException, JsonParseException, JsonMappingException, IOException {
        Childfollowup childFollowUp = new Childfollowup();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            childFollowUp = objectMapper.readValue(content, Childfollowup.class);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        return childFollowUp;
    }
}
