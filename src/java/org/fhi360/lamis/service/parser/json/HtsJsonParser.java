/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.parser.json;

import java.io.IOException;
import javax.xml.bind.JAXBException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.fhi360.lamis.dao.hibernate.HtsDAO;
import org.fhi360.lamis.dao.jdbc.HtsJDBC;
import org.fhi360.lamis.model.Hts;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author user10
 */
public class HtsJsonParser {

    public HtsJsonParser() {
    }
    
    
      public void parserJson(String content) {
        try {
            JSONArray htcs = new JSONArray(content); 
            System.out.println("........................ hts: "+htcs);
            for (int i = 0; i < htcs.length(); i++) {
                JSONObject record = htcs.optJSONObject(i);
                Hts hts = getObject(record.toString());
                hts.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
                                               
                long htsId = new HtsJDBC().getHtsId(hts.getClientCode(), hts.getFacilityId());
                if(htsId == 0L) {
                    HtsDAO.save(hts);
                }
                else {
                    hts.setHtsId(htsId);
                    HtsDAO.update(hts);
                }                                    
            }
        } 
        catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }        

    }
   
     private static Hts getObject(String content) throws JAXBException, JsonParseException, JsonMappingException, IOException {
        Hts hts = new Hts();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            hts = objectMapper.readValue(content, Hts.class);
        } catch(Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
        return hts;
    }
     
      
      
}

    

