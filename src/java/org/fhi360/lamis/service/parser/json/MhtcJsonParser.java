/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.parser.json;

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

    public MhtcJsonParser() {
    }
    
    
    public void parserJson(String content) {
        try {
            JSONArray htcs = new JSONArray(content); 
            System.out.println("JSON: "+htcs);
            for (int i = 0; i < htcs.length(); i++) {
                JSONObject record = htcs.optJSONObject(i);
                long communitypharmId = record.getLong("communitypharm_id");
                String numTested = record.getString("num_tested"); 
                String numPositive = record.getString("num_positive"); 
                String numReferred = record.getString("num_referred"); 
                String numOnsiteVisit = record.getString("num_onsite_visit"); 
                String month = record.getString("month"); 
                String year = record.getString("year"); 

                Mhtc mhtc = new Mhtc();
                mhtc.setCommunitypharmId(communitypharmId);
                mhtc.setNumTested(Integer.parseInt(numTested));
                mhtc.setNumPositive(Integer.parseInt(numPositive));
                mhtc.setNumReferred(Integer.parseInt(numReferred));
                mhtc.setNumOnsiteVisit(Integer.parseInt(numOnsiteVisit));
                mhtc.setMonth(Integer.parseInt(month));
                mhtc.setYear(Integer.parseInt(year));
                mhtc.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
                               
                long mhtcId = new MhtcJDBC().getMhtcId(communitypharmId, Integer.parseInt(month), Integer.parseInt(year));
                if(mhtcId == 0L) {
                    MhtcDAO.save(mhtc);
                }
                else {
                    mhtc.setMhtcId(mhtcId);
                    MhtcDAO.update(mhtc);
                }                                    
            }
        } 
        catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }        

    }
        
}
