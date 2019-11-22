/**
 *
 * @author aalozie
 */

package org.fhi360.lamis.utility.builder;

import org.fhi360.lamis.model.Specimen;
import org.fhi360.lamis.utility.Scrambler;
import java.util.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.utility.DateUtil;

public class SpecimenListBuilder {
    private HttpServletRequest request;
    private HttpSession session;
    private Boolean viewIdentifier;
    private Scrambler scrambler;

    private DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");    
    private ArrayList<Map<String, String>> specimenList = new ArrayList<>();
    
    public SpecimenListBuilder() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
        this.scrambler = new Scrambler();
        if(ServletActionContext.getRequest().getSession().getAttribute("viewIdentifier") != null) {
            this.viewIdentifier = (Boolean) session.getAttribute("viewIdentifier");                        
        }
    }
   
    public void buildSpecimenList(Specimen specimen) {
        String specimenId = Long.toString(specimen.getSpecimenId());
        String specimenType = specimen.getSpecimenType();
        String labno = specimen.getLabno();
        String barcode = specimen.getBarcode();
        String facilityId = Long.toString(specimen.getFacilityId());
        String stateId = Long.toString(specimen.getStateId()); 
        String lgaId = Long.toString(specimen.getLgaId()); 
        String treatmentUnitId =  Long.toString(specimen.getTreatmentUnitId());
        String dateReceived = specimen.getDateReceived() == null ? "" : DateUtil.parseDateToString(specimen.getDateReceived(), "MM/dd/yyyy");  
        String dateCollected = specimen.getDateCollected() == null ? "" : DateUtil.parseDateToString(specimen.getDateCollected(), "MM/dd/yyyy"); 
        String dateAssay = specimen.getDateAssay() == null ? "" : DateUtil.parseDateToString(specimen.getDateAssay(), "MM/dd/yyyy"); 
        String dateReported = specimen.getDateReported() == null ? "" : DateUtil.parseDateToString(specimen.getDateReported(), "MM/dd/yyyy"); 
        String dateDispatched = specimen.getDateDispatched() == null ? "" : DateUtil.parseDateToString(specimen.getDateDispatched(), "MM/dd/yyyy"); 
        String qualityCntrl = specimen.getQualityCntrl() == null ? "" : Integer.toString(specimen.getQualityCntrl());
        String result = specimen.getResult() == null ? "" : specimen.getResult();         
        String reasonNoTest = specimen.getReasonNoTest() == null ? "" : specimen.getReasonNoTest();         
        String hospitalNum = specimen.getHospitalNum() == null ? "" : specimen.getHospitalNum();
        String surname = specimen.getSurname() == null ? "" : specimen.getSurname();
        surname = (viewIdentifier)? scrambler.unscrambleCharacters(surname) : surname;
        surname = StringUtils.upperCase(surname);                
        String otherNames = (viewIdentifier)? scrambler.unscrambleCharacters(specimen.getOtherNames()) : specimen.getOtherNames();
        otherNames = StringUtils.capitalize(otherNames);
        String gender = specimen.getGender() == null ? "" : specimen.getGender();
        String dateBirth =  specimen.getDateBirth() == null ? "" : dateFormat.format(specimen.getDateBirth());
        String age = specimen.getAge() == null ? "" : Integer.toString(specimen.getAge());
        String ageUnit = specimen.getAgeUnit() == null ? "" : specimen.getAgeUnit();
        String address = specimen.getAddress() == null ? "" : specimen.getAddress();
        address = (viewIdentifier)? scrambler.unscrambleCharacters(address) : address;
        address = StringUtils.capitalize(address); 
        String phone = specimen.getPhone() == null ? "" : specimen.getPhone();
        phone = (viewIdentifier)? scrambler.unscrambleNumbers(phone) : phone;
                
        // create an array from object properties 
        Map<String, String> map = new TreeMap<String, String>();
        map.put("specimenId", specimenId);
        map.put("specimenType", specimenType);
        map.put("labno", labno);
        map.put("barcode", barcode);
        map.put("facilityId", facilityId);
        map.put("stateId", stateId);
        map.put("lgaId", lgaId);
        map.put("treatmentUnitId", treatmentUnitId);
        map.put("dateReceived", dateReceived);
        map.put("dateCollected", dateCollected);
        map.put("dateAssay", dateAssay);
        map.put("dateReported", dateReported);
        map.put("dateDispatched", dateDispatched);
        map.put("qualityCntrl", qualityCntrl);
        map.put("result", result);
        map.put("reasonNoTest", reasonNoTest); 
        map.put("hospitalNum", hospitalNum);
        map.put("surname", surname);
        map.put("otherNames", otherNames);
        map.put("name", surname + ' ' + otherNames);
        map.put("gender", gender);
        map.put("dateBirth", dateBirth);
        map.put("age", age);
        map.put("ageUnit", ageUnit);
        map.put("address", address);
        map.put("phone", phone);
        specimenList.add(map);
        session.setAttribute("specimenList", specimenList);        
   }
    
    
    public void buildSpecimenList(ResultSet resultSet) throws SQLException{
        try {
            while (resultSet.next()) {
                String specimenId = Long.toString(resultSet.getLong("specimen_id")); 
                String specimenType = resultSet.getString("specimen_type"); 
                String labno = resultSet.getString("labno");
                String barcode = (resultSet.getString("barcode") == null)? "" : resultSet.getString("barcode");
                String facilityId = Long.toString(resultSet.getLong("facility_id")); 
                String facilityName = (resultSet.getString("name") == null)? "" : resultSet.getString("name");
                String stateId = Long.toString(resultSet.getLong("state_id")); 
                String lgaId = Long.toString(resultSet.getLong("lga_id")); 
                String treatmentUnitId =  Long.toString(resultSet.getLong("treatment_unit_id"));
                String dateReceived = (resultSet.getDate("date_received") == null)? "" : dateFormat.format(resultSet.getDate("date_received"));
                String dateCollected = (resultSet.getDate("date_collected") == null)? "" : dateFormat.format(resultSet.getDate("date_collected"));
                String dateAssay = (resultSet.getDate("date_assay") == null)? "" : dateFormat.format(resultSet.getDate("date_assay"));
                String dateReported = (resultSet.getDate("date_reported") == null)? "" : dateFormat.format(resultSet.getDate("date_reported"));
                String dateDispatched = (resultSet.getDate("date_dispatched") == null)? "" : dateFormat.format(resultSet.getDate("date_dispatched"));
                String qualityCntrl = (resultSet.getInt("quality_cntrl") == 0)? "" : Integer.toString(resultSet.getInt("quality_cntrl"));
                String hospitalNum = (resultSet.getDate("hospital_num") == null)? "" : resultSet.getString("hospital_num");
                String result = (resultSet.getDate("result") == null)? "" : resultSet.getString("result");                
                String reasonNoTest = (resultSet.getDate("reason_no_test") == null)? "" : resultSet.getString("reason_no_test");       
                String surname = resultSet.getString("surname") == null ? "" : resultSet.getString("surname");
                surname = (viewIdentifier)? scrambler.unscrambleCharacters(surname) : surname;
                surname = StringUtils.upperCase(surname);                
                String otherNames = resultSet.getString("other_names") == null ? "" : resultSet.getString("other_names");
                otherNames = (viewIdentifier)? scrambler.unscrambleCharacters(otherNames) : otherNames;
                otherNames = StringUtils.capitalize(otherNames);
                String gender = resultSet.getString("gender") == null ? "" : resultSet.getString("gender");                
                String dateBirth = (resultSet.getDate("date_birth") == null)? "" : dateFormat.format(resultSet.getDate("date_birth"));
                String age = (resultSet.getInt("age") == 0)? "" : Integer.toString(resultSet.getInt("age"));
                String ageUnit = resultSet.getString("age_unit") == null ? "" : resultSet.getString("age_unit");
                String address = resultSet.getString("address") == null ? "" : resultSet.getString("address");
                address = (viewIdentifier)? scrambler.unscrambleCharacters(address) : address;
                address = StringUtils.capitalize(address);                
                String phone = resultSet.getString("phone") == null ? ""  : resultSet.getString("phone");
                phone = (viewIdentifier)? scrambler.unscrambleNumbers(phone) : phone;
                String timeStamp = (resultSet.getDate("time_stamp") == null)? "" : dateFormat.format(resultSet.getDate("time_stamp"));                
                
                Map<String, String> map = new HashMap<String, String>();                
                map.put("specimenId", specimenId);
                map.put("specimenType", specimenType);
                map.put("labno", labno);
                map.put("barcode", barcode);
                map.put("facilityId", facilityId);
                map.put("facilityName", facilityName);
                map.put("stateId", stateId);
                map.put("lgaId", lgaId);
                map.put("treatmentUnitId", treatmentUnitId);
                map.put("dateReceived", dateReceived);
                map.put("dateCollected", dateCollected);
                map.put("dateAssay", dateAssay);
                map.put("dateReported", dateReported);
                map.put("dateDispatched", dateDispatched);
                map.put("qualityCntrl", qualityCntrl);
                map.put("result", result);
                map.put("reasonNoTest", reasonNoTest); 
                map.put("hospitalNum", hospitalNum);
                map.put("surname", surname);
                map.put("otherNames", otherNames);
                map.put("name", surname + ' ' + otherNames);
                map.put("gender", gender);
                map.put("dateBirth", dateBirth);
                map.put("age", age);
                map.put("ageUnit", ageUnit);
                map.put("address", address);
                map.put("phone", phone);
                map.put("timeStamp", timeStamp); 
                specimenList.add(map);
            }            
            session.setAttribute("specimenList", specimenList);   

            resultSet = null;
            specimenList = null;
        }
        catch (SQLException sqlException) {
            resultSet = null;
            throw sqlException;  
        }
    }

    public void buildResultList(ResultSet resultSet) throws SQLException{
        try {
            while (resultSet.next()) {
                String specimenId = Long.toString(resultSet.getLong("specimen_id")); 
                String labno = resultSet.getString("labno");
                String dateReceived = (resultSet.getDate("date_received") == null)? "" : dateFormat.format(resultSet.getDate("date_received"));
                String hospitalNum = (resultSet.getString("hospital_num") == null)? "" : resultSet.getString("hospital_num");
                String facilityName = (resultSet.getString("name") == null)? "" : resultSet.getString("name");
                String result = (resultSet.getString("result") == null)? "" : resultSet.getString("result");                
                String surname = resultSet.getString("surname") == null ? "" : resultSet.getString("surname");
                surname = (viewIdentifier)? scrambler.unscrambleCharacters(surname) : surname;
                surname = StringUtils.upperCase(surname);                
                String otherNames = resultSet.getString("other_names") == null ? "" : resultSet.getString("other_names");
                otherNames = (viewIdentifier)? scrambler.unscrambleCharacters(otherNames) : otherNames;
                otherNames = StringUtils.capitalize(otherNames);
                
                Map<String, String> map = new HashMap<String, String>();                
                map.put("specimenId", specimenId);
                map.put("labno", labno);
                map.put("dateReceived", dateReceived);
                map.put("result", result);
                map.put("hospitalNum", hospitalNum);
                map.put("facilityName", facilityName);
                map.put("surname", surname);
                map.put("otherNames", otherNames);
                map.put("name", surname + ' ' + otherNames);
                map.put("sel", "1");
                specimenList.add(map);
            }            
            session.setAttribute("specimenList", specimenList);   
            resultSet = null;
            specimenList = null;
        }
        catch (SQLException sqlException) {
            resultSet = null;
            throw sqlException;  
        }
    }
    
    public ArrayList<Map<String, String>> retrieveSpecimenList() {
        if(session.getAttribute("specimenList") != null) {
            specimenList = (ArrayList) session.getAttribute("specimenList");                        
        }
        return specimenList;
    }            

    public void clearSpecimenList() {
        specimenList = retrieveSpecimenList();
        specimenList.clear();
        session.setAttribute("specimenList", specimenList); 
    } 
    
}
