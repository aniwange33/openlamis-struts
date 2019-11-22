/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.parser.json;

import org.fhi360.lamis.dao.hibernate.EncounterDAO;
import org.fhi360.lamis.dao.jdbc.EncounterJDBC;
import org.fhi360.lamis.model.Encounter;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.service.MobileRefillEncounterUpdater;
import org.fhi360.lamis.utility.DateUtil;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author user1
 */
public class EncounterJsonParser {
    private long facilityId;
    private long patientId;
    private String dateVisit;
    private String regimentype;
    private String regimen1;
    private String regimen2;
    private String regimen3;
    private String regimen4;
    private Integer duration1;
    private Integer duration2;
    private Integer duration3;
    private Integer duration4;
    private String nextRefill;

    public EncounterJsonParser() {
    }
    
    public void parserJson(String content) {
        try {
            JSONArray encounters = new JSONArray(content); 
            System.out.println("JSON: "+encounters);
            for (int i = 0; i < encounters.length(); i++) {
                JSONObject record = encounters.optJSONObject(i);
                facilityId = record.getLong("facility_id");
                patientId = record.getLong("patient_id");
                long communitypharmId = record.getLong("communitypharm_id");
                dateVisit = record.getString("date_visit"); 
                String question1 = record.getString("question1");
                String question2 = record.getString("question2");
                String question3 = record.getString("question3");
                String question4 = record.getString("question4");
                String question5 = record.getString("question5");
                String question6 = record.getString("question6");
                String question7 = record.getString("question7");
                String question8 = record.getString("question8");
                String question9 = record.getString("question9");
                regimen1 = record.getString("regimen1");
                regimen2 = record.getString("regimen2");
                regimen3 = record.getString("regimen3");
                regimen4 = record.getString("regimen4");
                String value = record.getString("duration1");
                duration1 = value.isEmpty()? null : Integer.parseInt(value);
                value = record.getString("duration2");
                duration2 = value.isEmpty()? null : Integer.parseInt(value);                
                value = record.getString("duration3");
                duration3 = value.isEmpty()? null : Integer.parseInt(value);                
                value = record.getString("duration4");
                duration4 = value.isEmpty()? null : Integer.parseInt(value);                
                value = record.getString("prescribed1");
                Integer prescribed1 = value.isEmpty()? null : Integer.parseInt(value);                
                value = record.getString("prescribed2");
                Integer prescribed2 = value.isEmpty()? null : Integer.parseInt(value);                
                value = record.getString("prescribed3");
                Integer prescribed3 = value.isEmpty()? null : Integer.parseInt(value);                
                value = record.getString("prescribed4");
                Integer prescribed4 = value.isEmpty()? null : Integer.parseInt(value);                
                value = record.getString("dispensed1");
                Integer dispensed1 = value.isEmpty()? null : Integer.parseInt(value);                
                value = record.getString("dispensed2");
                Integer dispensed2 = value.isEmpty()? null : Integer.parseInt(value);                
                value = record.getString("dispensed3");
                Integer dispensed3 = value.isEmpty()? null : Integer.parseInt(value);                
                value = record.getString("dispensed4");
                Integer dispensed4 = value.isEmpty()? null : Integer.parseInt(value);                
                String notes = record.getString("notes");
                nextRefill = record.getString("next_refill");
                regimentype = record.getString("regimentype");

                Encounter encounter = new Encounter();
                encounter.setFacilityId(facilityId);
                Patient patient = new Patient();
                patient.setPatientId(patientId);
                encounter.setPatient(patient);
                encounter.setCommunitypharmId(communitypharmId);
                encounter.setDateVisit(DateUtil.parseStringToDate(dateVisit, "yyyy-MM-dd"));
                encounter.setQuestion1(question1);
                encounter.setQuestion2(question2);
                encounter.setQuestion3(question3);
                encounter.setQuestion4(question4);
                encounter.setQuestion5(question5);
                encounter.setQuestion6(question6);
                encounter.setQuestion7(question7);
                encounter.setQuestion8(question8);
                encounter.setQuestion9(question9);
                encounter.setRegimen1(regimen1);
                encounter.setRegimen2(regimen2);
                encounter.setRegimen3(regimen3);
                encounter.setRegimen4(regimen4);
                if(duration1 != null) encounter.setDuration1(duration1);
                if(duration2 != null) encounter.setDuration2(duration2);
                if(duration3 != null) encounter.setDuration3(duration3);
                if(duration4 != null) encounter.setDuration4(duration4);
                if(prescribed1 != null) encounter.setPrescribed1(prescribed1);
                if(prescribed2 != null) encounter.setPrescribed2(prescribed2);
                if(prescribed3 != null) encounter.setPrescribed3(prescribed3);
                if(prescribed4 != null) encounter.setPrescribed4(prescribed4);
                if(dispensed1 != null) encounter.setDispensed1(dispensed1);
                if(dispensed2 != null) encounter.setDispensed2(dispensed2);
                if(dispensed3 != null) encounter.setDispensed3(dispensed3);
                if(dispensed4 != null) encounter.setDispensed4(dispensed4);
                encounter.setNotes(notes);
                encounter.setNextRefill(DateUtil.parseStringToDate(nextRefill, "yyyy-MM-dd"));
                encounter.setRegimentype(regimentype);
                encounter.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
                
                long encounterId = new EncounterJDBC().getEncounterId(facilityId, patientId, dateVisit);               
                if(encounterId == 0L) {
                    EncounterDAO.save(encounter);
                }
                else {
                    encounter.setEncounterId(encounterId);
                    EncounterDAO.update(encounter);
                }
                logDrugDispensed();
            }
        } 
        catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
        

    }
    
    private void logDrugDispensed() {
       MobileRefillEncounterUpdater communityRefillUpdater = new MobileRefillEncounterUpdater(); 
       if(!regimen1.trim().isEmpty()) communityRefillUpdater.logDrug(facilityId, patientId, dateVisit, nextRefill, regimentype, regimen1, duration1);        
       if(!regimen2.trim().isEmpty()) communityRefillUpdater.logDrug(facilityId, patientId, dateVisit, nextRefill, regimen2, duration2);        
       if(!regimen3.trim().isEmpty()) communityRefillUpdater.logDrug(facilityId, patientId, dateVisit, nextRefill, regimen3, duration3);        
       if(!regimen4.trim().isEmpty()) communityRefillUpdater.logDrug(facilityId, patientId, dateVisit, nextRefill, regimen4, duration4);
       communityRefillUpdater.updateRefillAttribute(facilityId, patientId, dateVisit, nextRefill);
    }
    
}

