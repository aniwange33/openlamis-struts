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
import org.fhi360.lamis.dao.hibernate.TbscreenhistoryDAO;
import org.fhi360.lamis.dao.hibernate.UserDAO;
import org.fhi360.lamis.dao.jdbc.TbscreenhistoryJDBC;
import org.fhi360.lamis.dao.jdbc.UserJDBC;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.model.Tbscreenhistory;
import org.fhi360.lamis.model.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author user10
 */
public class UserJsonParser {
    
    public void parserJson(String table, String content) {
        try {

            JSONObject jsonObj = new JSONObject(content);
            JSONArray jsonArray = jsonObj.optJSONArray(table);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject record = jsonArray.optJSONObject(i);
                User user = getObject(record.toString());
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
              
                long userId = new UserJDBC().getUserId(user.getFacilityId(),
                        user.getUsername(), user.getPassword());
                if (userId == 0L) {
                    UserDAO.save(user);
                } else {
                    user.setUserId(userId);
                    UserDAO.update(user);
                }

            }
        } catch (IOException | JAXBException | JSONException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static User getObject(String content) throws JAXBException, JsonParseException, JsonMappingException, IOException {
        User user = new User();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            user = objectMapper.readValue(content, User.class);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        return user;
    }
}
