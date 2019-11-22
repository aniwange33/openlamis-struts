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
import org.fhi360.lamis.dao.hibernate.MhtcDAO;
import org.fhi360.lamis.dao.jdbc.MhtcJDBC;
import org.fhi360.lamis.model.Mhtc;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author user10
 */
public class MhtcJsonParser {

    public void  parserJson(String table, String content) {

        try {
            System.out.println("Mhtc JSON :"+content);
            JSONObject jsonObj = new JSONObject(content);              
            JSONArray jsonArray = jsonObj.optJSONArray(table);
            
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject record = jsonArray.optJSONObject(i);
                
                Mhtc mhtc = getObject(record.toString());
                mhtc.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
 
                long mhtcId = new MhtcJDBC().getMhtcId(mhtc.getCommunitypharmId(), mhtc.getMonth(), mhtc.getYear());
                if (mhtcId == 0L) {
                    MhtcDAO.save(mhtc);
                } else {
                    mhtc.setMhtcId(mhtcId);
                    MhtcDAO.update(mhtc);
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }

    }

    private static Mhtc getObject(String content) throws JAXBException, JsonParseException, JsonMappingException, IOException {
        Mhtc mhtc = new Mhtc();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
             objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
             objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            mhtc = objectMapper.readValue(content, Mhtc.class);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
        return mhtc;
    }

}
