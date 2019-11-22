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
import org.fhi360.lamis.dao.hibernate.AssessmentDao;
import org.fhi360.lamis.dao.jdbc.AssessmentJDBC;
import org.fhi360.lamis.model.Assessment;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author user10
 */
public class AssessmentJsonParser {
    
    public void parserJson(String table, String content) {
        try {
            System.out.println("Assessment JSON :"+content);
            JSONObject jsonObj = new JSONObject(content);              
           JSONArray jsonArray = jsonObj.optJSONArray(table);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject record = jsonArray.optJSONObject(i);
                 System.out.println("OYISCO JSON :"+record.toString());
                Assessment assessment = getObject(record.toString());
                assessment.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
                             
                long assessmentId = new AssessmentJDBC().getHtsAssessment(assessment.getClientCode(), assessment.getFacilityId());
                if(assessmentId == 0L) {
                     System.out.println("OYISCO JSON :"+assessment.getQuestion1());
                    AssessmentDao.save(assessment);
                }
                else {
                    assessment.setAssessmentId(assessmentId);
                    AssessmentDao.update(assessment);
                }                                    
            }
        } 
        catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }        

    }
   
     private static Assessment getObject(String content) throws JAXBException, JsonParseException, JsonMappingException, IOException {
        Assessment assessment = new Assessment();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
             objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
             objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            assessment = objectMapper.readValue(content, Assessment.class);
            System.out.println(" assessment "+assessment.getFacilityId());
        } catch(Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
        return assessment;
    }
}
