/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.parser;

import java.io.IOException;
import javax.xml.bind.JAXBException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.fhi360.lamis.dao.hibernate.IndexcontactDAO;
import org.fhi360.lamis.dao.jdbc.IndexcontactJDBC;
import org.fhi360.lamis.model.Indexcontact;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author user10
 */
public class IndexcontactJsonParser {

    public IndexcontactJsonParser() {
    }
    
    public void parserJson(String content) {
        try {
            JSONArray indexcontacts = new JSONArray(content); 
            
            for (int i = 0; i < indexcontacts.length(); i++) {
                JSONObject record = indexcontacts.optJSONObject(i);
                Indexcontact indexcontact = getObject(record.toString());
                indexcontact.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
                                               
                long indexcontactId = new IndexcontactJDBC().getIndexcontactId(indexcontact.getIndexContactCode(),
                        indexcontact.getFacilityId());
                if(indexcontactId == 0L) {
                    IndexcontactDAO.save(indexcontact);
                }
                else {
                    indexcontact.setIndexcontactId(indexcontactId);
                    IndexcontactDAO.update(indexcontact);
                }                                    
            }
        } 
        catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }        
    }
   
     private static Indexcontact getObject(String content) throws JAXBException, JsonParseException, JsonMappingException, IOException {
        Indexcontact indexcontact = new Indexcontact();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            indexcontact = objectMapper.readValue(content, Indexcontact.class);
        } catch(Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
        return indexcontact;
    }
     
}
