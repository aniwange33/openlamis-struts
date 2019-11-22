/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.parser.json;

import java.util.Date;
import org.fhi360.lamis.dao.hibernate.ChroniccareDAO;
import org.fhi360.lamis.dao.jdbc.ChroniccareJDBC;
import org.fhi360.lamis.model.Chroniccare;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.utility.DateUtil;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author user1
 */
public class ChroniccareJsonParser {

    public ChroniccareJsonParser() {
    }

    
    public void parserJson(String content) {
        try {
            JSONArray chroniccares = new JSONArray(content); 
            System.out.println("JSON: "+chroniccares);
            for (int i = 0; i < chroniccares.length(); i++) {
                JSONObject record = chroniccares.optJSONObject(i);
                long facilityId = record.getLong("facility_id");
                long patientId = record.getLong("patient_id");
                long communitypharmId = record.getLong("communitypharm_id");
                String dateVisit = record.getString("date_visit"); 
                String ipt = record.getString("ipt");
                String eligibleIpt = record.getString("eligible_ipt");
                String inh = record.getString("inh");
                String tbTreatment = record.getString("tb_treatment");
                String dateStartedTbTreatment = record.getString("date_started_tb_treatment");
                String tbReferred = record.getString("tb_referred");
                String tbValue1 = record.getString("tb_value1");
                String tbValue2 = record.getString("tb_value2");
                String tbValue3 = record.getString("tb_value3");
                String tbValue4 = record.getString("tb_value4");
                String tbValue5 = record.getString("tb_value5");
                String tbValues = tbValue1+"#"+tbValue2+"#"+tbValue3+"#"+tbValue4+"#"+tbValue5;
                String value = record.getString("body_weight");
                Double bodyWeight = value.isEmpty()? null : Double.parseDouble(value);
                value = record.getString("height");
                Double height = value.isEmpty()? null : Double.parseDouble(value);
                value = record.getString("bmi");
                Double bmi = value.isEmpty()? null : Double.parseDouble(value);
                String bmiCategory = record.getString("bmi_category");
                value = record.getString("muac");
                Double muac = value.isEmpty()? null : Double.parseDouble(value);
                String muacCategory = record.getString("muac_category");
                String supplementaryFood = record.getString("supplementary_food");
                String nutritionalStatusReferred = record.getString("nutritional_status_referred");
                String gbv1 = record.getString("gbv1");
                String gbv1Referred = record.getString("gbv1_referred");
                String gbv2 = record.getString("gbv2");
                String gbv2Referred = record.getString("gbv2_referred");
                String hypertensive = record.getString("hypertensive");
                String firstHypertensive = record.getString("first_hypertensive");
                String bp = record.getString("bp");
                String bpAbove = record.getString("bp_above");
                String bpReferred = record.getString("bp_referred");
                String diabetic = record.getString("diabetic");
                String firstDiabetic = record.getString("first_diabetic");
                String dmValue1 = record.getString("dm_value1");
                String dmValue2 = record.getString("dm_value2");
                String dmValues = dmValue1+"#"+dmValue2;
                String dmReferred = record.getString("dm_referred");
                String phdp1 = record.getString("phdp1");
                String phdp1ServicesProvided = record.getString("phdp1_services_provided");
                String phdp2 = record.getString("phdp2");
                String phdp3 = record.getString("phdp3");
                String phdp4 = record.getString("phdp4");
                String phdp4ServicesProvided = record.getString("phdp4_services_provided");
                String phdp5 = record.getString("phdp5");
                value = record.getString("phdp6");
                Integer phdp6 = value.isEmpty()? null : Integer.parseInt(value); 
                value = record.getString("phdp7");
                Integer phdp7 = value.isEmpty()? null : Integer.parseInt(value); 
                String phdp7ServicesProvided = record.getString("phdp7_services_provided");               
                String phdp8ServicesProvided = record.getString("phdp8_services_provided");
                String additionalServicesProvided = record.getString("additional_services_provided");
                String reproductiveIntentions1 = record.getString("reproductive_intentions1");
                String reproductiveIntentions1Referred = record.getString("reproductive_intentions1_referred");
                String reproductiveIntentions2 = record.getString("reproductive_intentions2");
                String reproductiveIntentions2Referred = record.getString("reproductive_intentions2_referred");
                String reproductiveIntentions3 = record.getString("reproductive_intentions3");
                String reproductiveIntentions3Referred = record.getString("reproductive_intentions3_referred");
                String malariaPrevention1 = record.getString("malaria_prevention1");
                String malariaPrevention1Referred = record.getString("malaria_prevention1_referred");
                String malariaPrevention2 = record.getString("malaria_prevention2");
                String malariaPrevention2Referred = record.getString("malaria_prevention2_referred");

                Chroniccare chroniccare = new Chroniccare();
                chroniccare.setFacilityId(facilityId);
                Patient patient = new Patient();                
                patient.setPatientId(patientId); 
                
                chroniccare.setPatient(patient);
                chroniccare.setCommunitypharmId(communitypharmId);
                chroniccare.setDateVisit(DateUtil.parseStringToDate(dateVisit, "yyyy-MM-dd"));
//                chroniccare.setClientType(clientType);
//                chroniccare.setCurrentStatus(currentStatus);
//                chroniccare.setPregnancyStatus(pregnancyStatus);
//                chroniccare.setClinicStage(clinicStage);
//                chroniccare.setLastCd4(lastCd4);
//                chroniccare.setDateLastCd4(DateUtil.parseStringToDate(dateVisit, "yyyy-MM-dd"));
//                chroniccare.setLastViralLoad(lastViralLoad);
//                chroniccare.setDateLastViralLoad(DateUtil.parseStringToDate(dateVisit, "yyyy-MM-dd"));
//                chroniccare.setEligibleViralLoad(eligibleViralLoad);
//                chroniccare.setEligibleCd4(eligibleCd4);
                chroniccare.setIpt(ipt);
                chroniccare.setInh(inh);
                chroniccare.setTbTreatment(tbTreatment);
                chroniccare.setTbReferred(tbReferred);
                chroniccare.setDateStartedTbTreatment(DateUtil.parseStringToDate(dateStartedTbTreatment, "yyyy-MM-dd"));
                chroniccare.setEligibleIpt(eligibleIpt);
                chroniccare.setTbValues(tbValues);
                if(bodyWeight != null) chroniccare.setBodyWeight(bodyWeight);
                if(height != null) chroniccare.setHeight(height);
                chroniccare.setMuac(muac);
                chroniccare.setSupplementaryFood(supplementaryFood);
                chroniccare.setNutritionalStatusReferred(nutritionalStatusReferred);
                chroniccare.setGbv1(gbv1);
                chroniccare.setGbv1Referred(gbv1Referred);
                chroniccare.setGbv2(gbv2);
                chroniccare.setGbv2Referred(gbv2Referred);
                chroniccare.setHypertensive(hypertensive);
                chroniccare.setFirstHypertensive(firstHypertensive);
                //chroniccare.setBp(bp);
                chroniccare.setBpAbove(bpAbove);
                chroniccare.setBpReferred(bpReferred);
                chroniccare.setDiabetic(diabetic);
                chroniccare.setFirstDiabetic(firstDiabetic);
                chroniccare.setDmReferred(dmReferred);
                chroniccare.setDmValues(dmValues);
                chroniccare.setPhdp1(phdp1);
                chroniccare.setPhdp1ServicesProvided(phdp1ServicesProvided);
                chroniccare.setPhdp2(phdp2);
                chroniccare.setPhdp4ServicesProvided(phdp4ServicesProvided);
                chroniccare.setPhdp3(phdp3);
                chroniccare.setPhdp4(phdp4);
                chroniccare.setPhdp5(phdp5);
                if(phdp6 != null) chroniccare.setPhdp6(phdp6);
                if(phdp7 != null) chroniccare.setPhdp7(phdp7);
                chroniccare.setPhdp7ServicesProvided(phdp7ServicesProvided);
                chroniccare.setPhdp8ServicesProvided(phdp8ServicesProvided);
                chroniccare.setPhdp9ServicesProvided(additionalServicesProvided);
                chroniccare.setReproductiveIntentions1(reproductiveIntentions1);
                chroniccare.setReproductiveIntentions1Referred(reproductiveIntentions1Referred);
                chroniccare.setReproductiveIntentions2(reproductiveIntentions2);
                chroniccare.setReproductiveIntentions2Referred(reproductiveIntentions2Referred);
                chroniccare.setReproductiveIntentions3(reproductiveIntentions3);
                chroniccare.setReproductiveIntentions3Referred(reproductiveIntentions3Referred);
                chroniccare.setMalariaPrevention1(malariaPrevention1);
                chroniccare.setMalariaPrevention2(malariaPrevention2);
                chroniccare.setMalariaPrevention1Referred(malariaPrevention1Referred);
                chroniccare.setMalariaPrevention2Referred(malariaPrevention2Referred);
                chroniccare.setTimeStamp(new java.sql.Timestamp(new Date().getTime()));
                
                long chroniccareId = new ChroniccareJDBC().getChroniccareId(facilityId, patientId, dateVisit);
                if(chroniccareId == 0L) {
                    ChroniccareDAO.save(chroniccare);
                }
                else {
                    chroniccare.setChroniccareId(chroniccareId);
                    ChroniccareDAO.update(chroniccare);
                }                                    
            }
        } 
        catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }        

    }
}
