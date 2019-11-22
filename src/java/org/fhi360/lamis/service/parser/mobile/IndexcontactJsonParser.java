/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.parser.mobile;

import java.io.IOException;
import javax.xml.bind.JAXBException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.text.SimpleDateFormat;
import org.fhi360.lamis.dao.hibernate.IndexcontactDAO;
import org.fhi360.lamis.dao.jdbc.HtsJDBC;
import org.fhi360.lamis.dao.jdbc.IndexcontactJDBC;
import org.fhi360.lamis.model.Hts;
import org.fhi360.lamis.model.Indexcontact;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author user10
 */
public class IndexcontactJsonParser {

    public void parserJson(String table, String content) {
        try {
            System.out.println("Indexcontact JSON : " + content);
            JSONObject jsonObj = new JSONObject(content);
            JSONArray jsonArray = jsonObj.optJSONArray(table);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject record = jsonArray.optJSONObject(i);
                Indexcontact indexcontact = getObject(record.toString());
                indexcontact.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
                long htsId = new HtsJDBC().getHtsId(indexcontact.getIndexContactCode(), indexcontact.getFacilityId());
                Hts hts = new Hts();
                hts.setHtsId(htsId);
                System.out.println("HTS "+htsId);
                indexcontact.setHts(hts);
                long indexContactId = new IndexcontactJDBC().getIndexcontactId(indexcontact.getIndexContactCode(), indexcontact.getFacilityId());
                if (indexContactId == 0L) {
                    IndexcontactDAO.save(indexcontact);
                } else {
                    indexcontact.setIndexcontactId(indexContactId);
                    IndexcontactDAO.update(indexcontact);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }

    private static Indexcontact getObject(String content) throws JAXBException, JsonParseException, JsonMappingException, IOException {
        Indexcontact indexcontact = new Indexcontact();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            indexcontact = objectMapper.readValue(content, Indexcontact.class);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
        return indexcontact;
    }

}
