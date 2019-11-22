/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.parser.json;

import org.fhi360.lamis.dao.hibernate.DrugtherapyDAO;
import org.fhi360.lamis.dao.jdbc.DrugtherapyJDBC;
import org.fhi360.lamis.model.Drugtherapy;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.utility.DateUtil;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author user10
 */
public class DrugtherapyJsonParser {
    public void parserJson(String content) {
        try {
            JSONArray drugtherapies = new JSONArray(content); 
            System.out.println("JSON: "+drugtherapies);
            for (int i = 0; i < drugtherapies.length(); i++) {
                JSONObject record = drugtherapies.optJSONObject(i);
                long facilityId = record.getLong("facility_id");
                long patientId = record.getLong("patient_id");
                long communitypharmId = record.getLong("communitypharm_id");
                String dateVisit = record.getString("date_visit"); 
                String ois = record.getString("ois"); 
                String therapyProblemScreened = record.getString("therapy_problem_screened"); 
                String adherenceIssues = record.getString("adherence_issues"); 
                String medicationError = record.getString("medication_error"); 
                String adrs = record.getString("adrs"); 
                String severity = record.getString("severity"); 
                String icsrForm = record.getString("icsr_form"); 
                String adrReferred = record.getString("adr_referred"); 

                Drugtherapy drugtherapy = new Drugtherapy();
                drugtherapy.setFacilityId(facilityId);
                Patient patient = new Patient();
                patient.setPatientId(patientId);
                drugtherapy.setPatient(patient);
                drugtherapy.setCommunitypharmId(communitypharmId);
                drugtherapy.setDateVisit(DateUtil.parseStringToDate(dateVisit, "yyyy-MM-dd"));
                drugtherapy.setOis(ois);
                drugtherapy.setTherapyProblemScreened(therapyProblemScreened);
                drugtherapy.setAdherenceIssues(adherenceIssues);
                drugtherapy.setMedicationError(medicationError);
                drugtherapy.setAdrs(adrs);
                drugtherapy.setSeverity(severity);
                drugtherapy.setIcsrForm(icsrForm);
                drugtherapy.setAdrReferred(adrReferred);
                drugtherapy.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
                
                long drugtherapyId = new DrugtherapyJDBC().getDrugtherapyId(facilityId, patientId, dateVisit);
                if(drugtherapyId == 0L) {
                    DrugtherapyDAO.save(drugtherapy);
                }
                else {
                    drugtherapy.setDrugtherapyId(drugtherapyId);
                    DrugtherapyDAO.update(drugtherapy);
                }                                    
            }
        } 
        catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }        

    }
    
}
