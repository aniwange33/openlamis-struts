/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.parser.mobile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.fhi360.lamis.dao.hibernate.HtsDAO;
import org.fhi360.lamis.dao.jdbc.HtsJDBC;
import org.fhi360.lamis.model.Hts;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author user10
 */
public class HtsJsonParser {

    public void parserJson(String table, String content) {
        try {
            System.out.println("Hts JSON :" + content);
            JSONObject jsonObj = new JSONObject(content);
            JSONArray jsonArray = jsonObj.optJSONArray(table);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject record = jsonArray.optJSONObject(i);

                Hts hts = getObject(record.toString());
                hts.setTimeStamp(new java.sql.Timestamp(new Date().getTime()));


                long htsId = new HtsJDBC().getHtsId(hts.getClientCode(), hts.getFacilityId());
                if (htsId == 0L) {
                    HtsDAO.save(hts);
                } else {
                    hts.setHtsId(htsId);
                    HtsDAO.update(hts);
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }

    }

    private static Hts getObject(String content) throws JAXBException, JsonParseException, JsonMappingException, IOException {
        Hts hts = new Hts();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            hts = objectMapper.readValue(content, Hts.class);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
        return hts;
    }


}

    

