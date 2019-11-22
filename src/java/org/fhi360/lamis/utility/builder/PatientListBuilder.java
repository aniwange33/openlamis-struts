/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.utility.builder;

import java.util.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.utility.Scrambler;
import org.apache.commons.lang3.StringUtils;
import org.fhi360.lamis.dao.jdbc.PatientJDBC;
import org.fhi360.lamis.utility.DateUtil;

public class PatientListBuilder {

    private HttpServletRequest request;
    private HttpSession session;
    private Boolean viewIdentifier = false;
    private Scrambler scrambler;

    private ArrayList<Map<String, String>> patientList = new ArrayList<>();
    private ArrayList<Map<String, String>> defaulterList = new ArrayList<>();
    private Map<String, Map<String, String>> sortedMaps = new TreeMap<>();

    public PatientListBuilder() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
        this.scrambler = new Scrambler();
        if (ServletActionContext.getRequest().getSession().getAttribute("viewIdentifier") != null) {
            this.viewIdentifier = (Boolean) session.getAttribute("viewIdentifier");
        }
    }

    public void buildPatientList(Patient patient) {
        String patientId = Long.toString(patient.getPatientId());
        String facilityId = Long.toString(patient.getFacility().getFacilityId());
        String hospitalNum = patient.getHospitalNum();
        String uniqueId = patient.getUniqueId();
        String surname = (viewIdentifier) ? scrambler.unscrambleCharacters(patient.getSurname()) : patient.getSurname();
        surname = StringUtils.upperCase(surname);
        String otherNames = (viewIdentifier) ? scrambler.unscrambleCharacters(patient.getOtherNames()) : patient.getOtherNames();
        otherNames = StringUtils.capitalize(otherNames);
        String gender = patient.getGender() == null ? "" : patient.getGender();
        String maritalStatus = patient.getMaritalStatus() == null ? "" : patient.getMaritalStatus();
        String dateBirth = patient.getDateBirth() == null ? "" : DateUtil.parseDateToString(patient.getDateBirth(), "MM/dd/yyyy");
        String age = (patient.getAge() == null) ? "" : Integer.toString(patient.getAge());
        String ageUnit = patient.getAgeUnit() == null ? "" : patient.getAgeUnit();
        String address = (viewIdentifier) ? scrambler.unscrambleCharacters(patient.getAddress()) : patient.getAddress();
        address = StringUtils.capitalize(address);
        String phone = (viewIdentifier) ? scrambler.unscrambleNumbers(patient.getPhone()) : patient.getPhone();
        String education = patient.getEducation() == null ? "" : patient.getEducation();
        String occupation = patient.getOccupation() == null ? "" : patient.getOccupation();
        String state = patient.getState() == null ? "" : patient.getState();
        String lga = patient.getLga() == null ? "" : patient.getLga();
        String nextKin = (viewIdentifier) ? scrambler.unscrambleCharacters(patient.getNextKin()) : patient.getNextKin();
        nextKin = StringUtils.capitalize(nextKin);
        String addressKin = (viewIdentifier) ? scrambler.unscrambleCharacters(patient.getAddressKin()) : patient.getAddressKin();
        addressKin = StringUtils.capitalize(addressKin);
        String phoneKin = (viewIdentifier) ? scrambler.unscrambleNumbers(patient.getPhoneKin()) : patient.getPhoneKin();
        String relationKin = patient.getRelationKin() == null ? "" : patient.getRelationKin();
        String entryPoint = patient.getEntryPoint() == null ? "" : patient.getEntryPoint();
        String timeHivDiagnosis = patient.getTimeHivDiagnosis() == null ? "" : patient.getTimeHivDiagnosis();
        String dateConfirmedHiv = patient.getDateConfirmedHiv() == null ? "" : DateUtil.parseDateToString(patient.getDateConfirmedHiv(), "MM/dd/yyyy");
        String pregnant = (patient.getPregnant() == null) ? "" : Integer.toString(patient.getPregnant());
        String breastfeeding = (patient.getBreastfeeding() == null) ? "" : Integer.toString(patient.getBreastfeeding());
        String dateRegistration = patient.getDateRegistration() == null ? "" : DateUtil.parseDateToString(patient.getDateRegistration(), "MM/dd/yyyy");
        String statusRegistration = patient.getStatusRegistration() == null ? "" : patient.getStatusRegistration();
        String enrollmentSetting = patient.getEnrollmentSetting() == null ? "" : patient.getEnrollmentSetting();
        String currentStatus = patient.getCurrentStatus() == null ? "" : patient.getCurrentStatus();
        String sourceReferral = patient.getSourceReferral() == null ? "" : patient.getSourceReferral();
        String dateCurrentStatus = patient.getDateCurrentStatus() == null ? "" : DateUtil.parseDateToString(patient.getDateCurrentStatus(), "MM/dd/yyyy");
        String dateStarted = patient.getDateStarted() == null ? "" : DateUtil.parseDateToString(patient.getDateStarted(), "MM/dd/yyyy");
        String dateLastClinic = patient.getDateLastClinic() == null ? "" : DateUtil.parseDateToString(patient.getDateLastClinic(), "MM/dd/yyyy");
        String dateLastRefill = patient.getDateLastRefill() == null ? "" : DateUtil.parseDateToString(patient.getDateLastRefill(), "MM/dd/yyyy");
        String dateNextClinic = patient.getDateNextClinic() == null ? "" : DateUtil.parseDateToString(patient.getDateNextClinic(), "MM/dd/yyyy");
        String dateNextRefill = patient.getDateNextRefill() == null ? "" : DateUtil.parseDateToString(patient.getDateNextRefill(), "MM/dd/yyyy");
        String dateEnrolledPmtct = patient.getDateEnrolledPmtct() == null ? "" : DateUtil.parseDateToString(patient.getDateEnrolledPmtct(), "MM/dd/yyyy");
        String lastViralLoad = "";
        String dateLastViralLoad = patient.getDateLastViralLoad() == null ? "" : DateUtil.parseDateToString(patient.getDateLastViralLoad(), "MM/dd/yyyy");
        if (!dateLastViralLoad.equalsIgnoreCase("")) {
            lastViralLoad = patient.getLastViralLoad() == null ? "" : patient.getLastViralLoad() == 0.0 ? "0" : Double.toString(patient.getLastViralLoad());
        }
        String viralLoadType = patient.getViralLoadType() == null ? "" : patient.getViralLoadType();
        String lastCd4 = "";
        String lastCd4p = "";
        String dateLastCd4 = patient.getDateLastCd4() == null ? "" : DateUtil.parseDateToString(patient.getDateLastCd4(), "MM/dd/yyyy");
        if (!dateLastCd4.equalsIgnoreCase("")) {
            lastCd4 = patient.getLastCd4() == null ? "" : patient.getLastCd4() == 0.0 ? "0" : Double.toString(patient.getLastCd4());
            lastCd4p = patient.getLastCd4p() == null ? "" : patient.getLastCd4p() == 0.0 ? "0" : Double.toString(patient.getLastCd4p());
        }
        String lastClinicStage = patient.getLastClinicStage() == null ? "" : patient.getLastClinicStage();
        String regimentype = patient.getRegimentype() == null ? "" : patient.getRegimentype();
        String regimen = patient.getRegimen() == null ? "" : patient.getRegimen();
        String lastRefillSetting = patient.getLastRefillSetting() == null ? "" : patient.getLastRefillSetting();
        String lastRefillDuration = patient.getLastRefillDuration() == null ? "" : patient.getLastRefillDuration() == 0.0 ? "0" : Double.toString(patient.getLastRefillDuration());
        String sendMessage = patient.getSendMessage() == null ? "" : Integer.toString(patient.getSendMessage());
        String timeStamp = patient.getTimeStamp() == null ? "" : DateUtil.parseDateToString(patient.getTimeStamp(), "MM/dd/yyyy");
        String tbStatus = patient.getTbStatus() == null ? "" : patient.getTbStatus();
        String communitypharmId = (patient.getCommunitypharmId() == null) ? "" : Long.toString(patient.getCommunitypharmId());
        String casemanagerId = (patient.getCasemanagerId() == null) ? "" : Long.toString(patient.getCasemanagerId());

        // create an array from object properties 
        Map<String, String> map = new TreeMap<>();
        String pregnancyStatus = "1";
        if (pregnant.equals("1")) {
            pregnancyStatus = "2";
        }
        if (breastfeeding.equals("1")) {
            pregnancyStatus = "3";
        }
        map.put("pregnancyStatus", pregnancyStatus);
        map.put("patientId", patientId);
        map.put("facilityId", facilityId);
        map.put("hospitalNum", hospitalNum);
        map.put("uniqueId", uniqueId);
        map.put("surname", surname);
        map.put("otherNames", otherNames);
        map.put("name", surname + ' ' + otherNames);
        map.put("gender", gender);
        map.put("dateBirth", dateBirth);
        map.put("age", age);
        map.put("ageUnit", ageUnit);
        map.put("maritalStatus", maritalStatus);
        map.put("address", address);
        map.put("phone", phone);
        map.put("education", education);
        map.put("occupation", occupation);
        map.put("state", state.trim());
        map.put("lga", lga.trim());
        map.put("nextKin", nextKin);
        map.put("addressKin", addressKin);
        map.put("phoneKin", phoneKin);
        map.put("relationKin", relationKin);
        map.put("entryPoint", entryPoint);
        map.put("dateConfirmedHiv", dateConfirmedHiv);
        map.put("pregnant", pregnant);
        map.put("breastfeeding", breastfeeding);
        map.put("dateRegistration", dateRegistration);
        map.put("statusRegistration", statusRegistration);
        map.put("enrollmentSetting", enrollmentSetting);
        map.put("currentStatus", currentStatus);
        map.put("dateCurrentStatus", dateCurrentStatus);
        map.put("dateStarted", dateStarted);
        map.put("dateLastClinic", dateLastClinic);
        map.put("dateLastRefill", dateLastRefill);
        map.put("dateNextClinic", dateNextClinic);
        map.put("dateNextRefill", dateNextRefill);
        map.put("dateLastViralLoad", dateLastViralLoad);
        map.put("dateLastCd4", dateLastCd4);
        map.put("dateEnrolledPmtct", dateEnrolledPmtct);
        map.put("sourceReferral", sourceReferral);
        map.put("timeHivDiagnosis", timeHivDiagnosis);
        map.put("lastViralLoad", lastViralLoad);
        map.put("lastCd4", lastCd4);
        map.put("lastCd4", lastCd4p);
        map.put("lastClinicStage", lastClinicStage);
        map.put("regimentype", regimentype);
        map.put("regimen", regimen);
        map.put("lastRefillSetting", lastRefillSetting);
        map.put("lastRefillDuration", lastRefillDuration);
        map.put("sendMessage", sendMessage);
        map.put("timeStamp", timeStamp);
        map.put("tbStatus", tbStatus);

        map.put("communitypharmId", communitypharmId);
        map.put("casemanagerId", casemanagerId);
        //Check if this patient ARV refill has been devolved to a community pharmacy
        map.put("devolve", communitypharmId.isEmpty() ? "0" : "1");
        //Check if this patient ARV refill has been devolved to a community pharmacy
        boolean dueViralLoad = new PatientJDBC().dueViralLoad(patient.getPatientId());
        map.put("dueViralLoad", dueViralLoad ? "1" : "0");
        map.put("sel", "0");
        map.put("viralLoadType", viralLoadType);
        patientList.add(map);
        session.setAttribute("patientList", patientList);
    }

    public void buildPatientList(ResultSet resultSet, String option) throws SQLException {
        try {
            // loop through resultSet for each row and put into Map
            while (resultSet.next()) {
                String patientId = Long.toString(resultSet.getLong("patient_id"));
                String facilityId = Long.toString(resultSet.getLong("facility_id"));
                String motherId = "N/A";
                if (option.equals("female")) {
                    motherId = resultSet.getObject("motherinformation_id") == null ? "N/A" : Long.toString(resultSet.getLong("motherinformation_id"));
                }
                String hospitalNum = resultSet.getString("hospital_num");
                String uniqueId = resultSet.getObject("unique_id") == null ? "" : resultSet.getString("unique_id");
                String surname = resultSet.getObject("surname") == null ? "" : resultSet.getString("surname");
                surname = (viewIdentifier) ? scrambler.unscrambleCharacters(surname) : surname;
                surname = StringUtils.upperCase(surname);
                String otherNames = resultSet.getObject("other_names") == null ? "" : resultSet.getString("other_names");
                otherNames = (viewIdentifier) ? scrambler.unscrambleCharacters(otherNames) : otherNames;
                otherNames = StringUtils.capitalize(otherNames);
                String gender = resultSet.getObject("gender") == null ? "" : resultSet.getString("gender");
                String maritalStatus = resultSet.getObject("marital_status") == null ? "" : resultSet.getString("marital_status");
                String dateBirth = resultSet.getObject("date_birth") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_birth"), "MM/dd/yyyy");
                String age = resultSet.getObject("age") == null ? "" : resultSet.getInt("age") == 0 ? "" : Integer.toString(resultSet.getInt("age"));
                String timeHivDiagnosis = resultSet.getObject("time_hiv_diagnosis") == null ? "" : resultSet.getString("time_hiv_diagnosis");
                String ageUnit = resultSet.getObject("age_unit") == null ? "" : resultSet.getString("age_unit");
                String address = resultSet.getObject("address") == null ? "" : resultSet.getString("address");
                address = (viewIdentifier) ? scrambler.unscrambleCharacters(address) : address;
                address = StringUtils.capitalize(address);
                String phone = resultSet.getObject("phone") == null ? "" : resultSet.getString("phone");
                phone = (viewIdentifier) ? scrambler.unscrambleNumbers(phone) : phone;
                String education = resultSet.getObject("education") == null ? "" : resultSet.getString("education");
                String occupation = resultSet.getObject("occupation") == null ? "" : resultSet.getString("occupation");
                String state = resultSet.getObject("state") == null ? "" : resultSet.getString("state");
                String lga = resultSet.getObject("lga") == null ? "" : resultSet.getString("lga");
                String nextKin = resultSet.getObject("next_kin") == null ? "" : resultSet.getString("next_kin");
                nextKin = (viewIdentifier) ? scrambler.unscrambleCharacters(nextKin) : nextKin;
                nextKin = StringUtils.capitalize(nextKin);
                String addressKin = resultSet.getObject("address_kin") == null ? "" : resultSet.getString("address_kin");
                addressKin = (viewIdentifier) ? scrambler.unscrambleCharacters(addressKin) : addressKin;
                addressKin = StringUtils.capitalize(addressKin);
                String phoneKin = resultSet.getObject("phone_kin") == null ? "" : resultSet.getString("phone_kin");
                phoneKin = (viewIdentifier) ? scrambler.unscrambleNumbers(phoneKin) : phoneKin;
                String relationKin = resultSet.getObject("relation_kin") == null ? "" : resultSet.getString("relation_kin");
                String entryPoint = resultSet.getObject("entry_point") == null ? "" : resultSet.getString("entry_point");
                String dateConfirmedHiv = resultSet.getObject("date_confirmed_hiv") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_confirmed_hiv"), "MM/dd/yyyy");
                String pregnant = resultSet.getObject("pregnant") == null ? "" : Integer.toString(resultSet.getInt("pregnant"));
                String breastfeeding = resultSet.getObject("breastfeeding") == null ? "" : Integer.toString(resultSet.getInt("breastfeeding"));
                String dateRegistration = resultSet.getObject("date_registration") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_registration"), "MM/dd/yyyy");
                String statusRegistration = resultSet.getObject("status_registration") == null ? "" : resultSet.getString("status_registration");
                String enrollmentSetting = resultSet.getObject("enrollment_setting") == null ? "" : resultSet.getString("enrollment_setting");
                String currentStatus = resultSet.getObject("current_status") == null ? "" : resultSet.getString("current_status");
                String dateCurrentStatus = resultSet.getObject("date_current_status") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_current_status"), "MM/dd/yyyy");
                String sourceReferral = resultSet.getObject("source_referral") == null ? "" : resultSet.getString("source_referral");
                String dateStarted = resultSet.getObject("date_started") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_started"), "MM/dd/yyyy");
                String dateLastClinic = resultSet.getObject("date_last_clinic") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_last_clinic"), "MM/dd/yyyy");
                String dateLastRefill = resultSet.getObject("date_last_refill") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_last_refill"), "MM/dd/yyyy");
                String dateNextClinic = resultSet.getObject("date_next_clinic") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_next_clinic"), "MM/dd/yyyy");
                String dateNextRefill = resultSet.getObject("date_next_refill") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_next_refill"), "MM/dd/yyyy");
                String dateEnrolledPmtct = resultSet.getObject("date_enrolled_pmtct") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_enrolled_pmtct"), "MM/dd/yyyy");
                String tbStatus = resultSet.getObject("tb_status") == null ? "" : resultSet.getString("tb_status");
                String lastViralLoad = "";
                String dateLastViralLoad = resultSet.getObject("date_last_viral_load") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_last_viral_load"), "MM/dd/yyyy");
                if (!dateLastViralLoad.equalsIgnoreCase("")) {
                    lastViralLoad = resultSet.getObject("last_viral_load") == null ? "" : resultSet.getDouble("last_viral_load") == 0.0 ? "0" : Double.toString(resultSet.getDouble("last_viral_load"));
                }
                String lastCd4 = "";
                String lastCd4p = "";
                String dateLastCd4 = resultSet.getObject("date_last_cd4") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_last_cd4"), "MM/dd/yyyy");
                if (!dateLastCd4.equalsIgnoreCase("")) {
                    lastCd4 = resultSet.getObject("last_cd4") == null ? "" : resultSet.getDouble("last_cd4") == 0.0 ? "0" : Double.toString(resultSet.getDouble("last_cd4"));
                    lastCd4p = resultSet.getObject("last_cd4p") == null ? "" : resultSet.getDouble("last_cd4p") == 0.0 ? "0" : Double.toString(resultSet.getDouble("last_cd4p"));
                }
                String lastClinicStage = resultSet.getObject("last_clinic_stage") == null ? "" : resultSet.getString("last_clinic_stage");
                String regimentype = resultSet.getObject("regimentype") == null ? "" : resultSet.getString("regimentype");
                String viralLoadType = resultSet.getObject("viral_load_type") == null ? "" : resultSet.getString("viral_load_type");
                String regimen = resultSet.getObject("regimen") == null ? "" : resultSet.getString("regimen");
                String lastRefillSetting = resultSet.getObject("last_refill_setting") == null ? "" : resultSet.getString("last_refill_setting");
                String lastRefillDuration = resultSet.getObject("last_refill_duration") == null ? "" : resultSet.getDouble("last_refill_duration") == 0.0 ? "0" : Double.toString(resultSet.getDouble("last_refill_duration"));
                String sendMessage = resultSet.getObject("send_message") == null ? "" : Integer.toString(resultSet.getInt("send_message"));
                String timeStamp = resultSet.getObject("time_stamp") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("time_stamp"), "MM/dd/yyyy");

                String communitypharmId = resultSet.getObject("communitypharm_id") == null ? "" : Integer.toString(resultSet.getInt("communitypharm_id"));
                String casemanagerId = resultSet.getObject("casemanager_id") == null ? "" : Integer.toString(resultSet.getInt("casemanager_id"));
                Boolean biometric = resultSet.getObject("biometric") == null ? false : resultSet.getBoolean("biometric");

                // create an array from object properties 
                Map<String, String> map = new HashMap<>();
                map.put("biometric", Boolean.toString(biometric));
                map.put("patientId", patientId);
                map.put("facilityId", facilityId);
                map.put("motherId", motherId);
                map.put("hospitalNum", hospitalNum);
                map.put("uniqueId", uniqueId);
                map.put("surname", surname);
                map.put("otherNames", otherNames);
                map.put("name", surname + ' ' + otherNames);
                map.put("gender", gender);
                map.put("dateBirth", dateBirth);
                map.put("age", age);
                map.put("ageUnit", ageUnit);
                map.put("maritalStatus", maritalStatus);
                map.put("address", address);
                map.put("phone", phone);
                map.put("education", education);
                map.put("occupation", occupation);
                map.put("state", state.trim());
                map.put("lga", lga.trim());
                map.put("nextKin", nextKin);
                map.put("addressKin", addressKin);
                map.put("phoneKin", phoneKin);
                map.put("relationKin", relationKin);
                map.put("entryPoint", entryPoint);
                map.put("dateConfirmedHiv", dateConfirmedHiv);
                String pregnancyStatus = "1";
                if (pregnant.equals("1")) {
                    pregnancyStatus = "2";
                }
                if (breastfeeding.equals("1")) {
                    pregnancyStatus = "3";
                }
                map.put("pregnancyStatus", pregnancyStatus);
                map.put("pregnant", pregnant);
                map.put("breastfeeding", breastfeeding);
                map.put("dateRegistration", dateRegistration);
                map.put("statusRegistration", statusRegistration);
                map.put("enrollmentSetting", enrollmentSetting);
                map.put("currentStatus", currentStatus);
                map.put("dateCurrentStatus", dateCurrentStatus);
                map.put("dateStarted", dateStarted);
                map.put("dateLastClinic", dateLastClinic);
                map.put("dateLastRefill", dateLastRefill);
                map.put("dateNextClinic", dateNextClinic);
                map.put("dateNextRefill", dateNextRefill);
                map.put("dateLastCd4", dateLastCd4);
                map.put("dateEnrolledPmtct", dateEnrolledPmtct);
                map.put("sourceReferral", sourceReferral);
                map.put("timeHivDiagnosis", timeHivDiagnosis);
                map.put("dateLastViralLoad", dateLastViralLoad);
                map.put("lastViralLoad", lastViralLoad);
                map.put("lastCd4", lastCd4);
                map.put("lastCd4p", lastCd4p);
                map.put("lastClinicStage", lastClinicStage);
                map.put("regimentype", regimentype);
                map.put("regimen", regimen);
                map.put("lastRefillSetting", lastRefillSetting);
                map.put("lastRefillDuration", lastRefillDuration);
                map.put("sendMessage", sendMessage);
                map.put("timeStamp", timeStamp);
                map.put("tbStatus", tbStatus);

                map.put("communitypharmId", communitypharmId);
                map.put("casemanagerId", casemanagerId);
                //Check if this patient ARV refill has been devolved to a community pharmacy
                map.put("devolve", communitypharmId.isEmpty() ? "0" : "1");
                //Check if this patient ARV refill has been devolved to a community pharmacy
                boolean dueViralLoad = new PatientJDBC().dueViralLoad(resultSet.getLong("patient_id"));
                map.put("dueViralLoad", dueViralLoad ? "1" : "0");
                map.put("sel", "0");
                map.put("viralLoadType", viralLoadType);
                patientList.add(map);
            }
            session.setAttribute("patientList", patientList);
            patientList = null;
        } catch (SQLException sqlException) {
            throw sqlException;
        }
    }

    public void buildPatientListSorted(ResultSet resultSet) throws SQLException {
        try {
            // loop through resultSet for each row and put into Map
            while (resultSet.next()) {
                String patientId = Long.toString(resultSet.getLong("patient_id"));
                String facilityId = Long.toString(resultSet.getLong("facility_id"));
                String hospitalNum = resultSet.getString("hospital_num");
                String uniqueId = resultSet.getObject("unique_id") == null ? "" : resultSet.getString("unique_id");
                String surname = resultSet.getObject("surname") == null ? "" : resultSet.getString("surname");
                surname = (viewIdentifier) ? scrambler.unscrambleCharacters(surname) : surname;
                surname = StringUtils.upperCase(surname);
                String otherNames = resultSet.getObject("other_names") == null ? "" : resultSet.getString("other_names");
                otherNames = (viewIdentifier) ? scrambler.unscrambleCharacters(otherNames) : otherNames;
                otherNames = StringUtils.capitalize(otherNames);
                String gender = resultSet.getObject("gender") == null ? "" : resultSet.getString("gender");
                String timeHivDiagnosis = resultSet.getObject("time_hiv_diagnosis") == null ? "" : resultSet.getString("time_hiv_diagnosis");
                String maritalStatus = resultSet.getObject("marital_status") == null ? "" : resultSet.getString("marital_status");
                String dateBirth = resultSet.getObject("date_birth") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_birth"), "MM/dd/yyyy");
                String age = resultSet.getObject("age") == null ? "" : resultSet.getInt("age") == 0 ? "" : Integer.toString(resultSet.getInt("age"));
                String ageUnit = resultSet.getObject("age_unit") == null ? "" : resultSet.getString("age_unit");
                String address = resultSet.getObject("address") == null ? "" : resultSet.getString("address");
                address = (viewIdentifier) ? scrambler.unscrambleCharacters(address) : address;
                address = StringUtils.capitalize(address);
                String phone = resultSet.getObject("phone") == null ? "" : resultSet.getString("phone");
                phone = (viewIdentifier) ? scrambler.unscrambleNumbers(phone) : phone;
                String education = resultSet.getObject("education") == null ? "" : resultSet.getString("education");
                String occupation = resultSet.getObject("occupation") == null ? "" : resultSet.getString("occupation");
                String state = resultSet.getObject("state") == null ? "" : resultSet.getString("state");
                String lga = resultSet.getObject("lga") == null ? "" : resultSet.getString("lga");
                String nextKin = resultSet.getObject("next_kin") == null ? "" : resultSet.getString("next_kin");
                nextKin = (viewIdentifier) ? scrambler.unscrambleCharacters(nextKin) : nextKin;
                nextKin = StringUtils.capitalize(nextKin);
                String addressKin = resultSet.getObject("address_kin") == null ? "" : resultSet.getString("address_kin");
                addressKin = (viewIdentifier) ? scrambler.unscrambleCharacters(addressKin) : addressKin;
                addressKin = StringUtils.capitalize(addressKin);
                String phoneKin = resultSet.getObject("phone_kin") == null ? "" : resultSet.getString("phone_kin");
                phoneKin = (viewIdentifier) ? scrambler.unscrambleNumbers(phoneKin) : phoneKin;
                String relationKin = resultSet.getObject("relation_kin") == null ? "" : resultSet.getString("relation_kin");
                String entryPoint = resultSet.getObject("entry_point") == null ? "" : resultSet.getString("entry_point");
                String dateConfirmedHiv = resultSet.getObject("date_confirmed_hiv") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_confirmed_hiv"), "MM/dd/yyyy");
                String pregnant = resultSet.getObject("pregnant") == null ? "" : Integer.toString(resultSet.getInt("pregnant"));
                String breastfeeding = resultSet.getObject("breastfeeding") == null ? "" : Integer.toString(resultSet.getInt("breastfeeding"));
                String dateRegistration = resultSet.getObject("date_registration") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_registration"), "MM/dd/yyyy");
                String statusRegistration = resultSet.getObject("status_registration") == null ? "" : resultSet.getString("status_registration");
                String enrollmentSetting = resultSet.getObject("enrollment_setting") == null ? "" : resultSet.getString("enrollment_setting");
                String currentStatus = resultSet.getObject("current_status") == null ? "" : resultSet.getString("current_status");
                String sourceReferral = resultSet.getObject("source_referral") == null ? "" : resultSet.getString("source_referral");
                String tbStatus = resultSet.getObject("tb_status") == null ? "" : resultSet.getString("tb_status");
                String dateCurrentStatus = resultSet.getObject("date_current_status") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_current_status"), "MM/dd/yyyy");
                String dateStarted = resultSet.getObject("date_started") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_started"), "MM/dd/yyyy");
                String dateLastClinic = resultSet.getObject("date_last_clinic") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_last_clinic"), "MM/dd/yyyy");
                String dateLastRefill = resultSet.getObject("date_last_refill") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_last_refill"), "MM/dd/yyyy");
                String dateNextClinic = resultSet.getObject("date_next_clinic") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_next_clinic"), "MM/dd/yyyy");
                String dateNextRefill = resultSet.getObject("date_next_refill") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_next_refill"), "MM/dd/yyyy");
                String dateEnrolledPmtct = resultSet.getObject("date_enrolled_pmtct") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_enrolled_pmtct"), "MM/dd/yyyy");
                String lastViralLoad = "";
                String dateLastViralLoad = resultSet.getObject("date_last_viral_load") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_last_viral_load"), "MM/dd/yyyy");
                if (!dateLastViralLoad.equalsIgnoreCase("")) {
                    lastViralLoad = resultSet.getObject("last_viral_load") == null ? "" : resultSet.getDouble("last_viral_load") == 0.0 ? "0" : Double.toString(resultSet.getDouble("last_viral_load"));
                }
                String lastCd4 = "";
                String lastCd4p = "";
                String dateLastCd4 = resultSet.getObject("date_last_cd4") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_last_cd4"), "MM/dd/yyyy");
                if (!dateLastCd4.equalsIgnoreCase("")) {
                    lastCd4 = resultSet.getObject("last_cd4") == null ? "" : resultSet.getDouble("last_cd4") == 0.0 ? "0" : Double.toString(resultSet.getDouble("last_cd4"));
                    lastCd4p = resultSet.getObject("last_cd4p") == null ? "" : resultSet.getDouble("last_cd4p") == 0.0 ? "0" : Double.toString(resultSet.getDouble("last_cd4p"));
                }

                String lastClinicStage = resultSet.getObject("last_clinic_stage") == null ? "" : resultSet.getString("last_clinic_stage");
                String regimentype = resultSet.getObject("regimentype") == null ? "" : resultSet.getString("regimentype");
                String viralLoadType = resultSet.getObject("viral_load_type") == null ? "" : resultSet.getString("viral_load_type");
                String regimen = resultSet.getObject("regimen") == null ? "" : resultSet.getString("regimen");
                String lastRefillSetting = resultSet.getObject("last_refill_setting") == null ? "" : resultSet.getString("last_refill_setting");
                String lastRefillDuration = resultSet.getObject("last_refill_duration") == null ? "" : resultSet.getDouble("last_refill_duration") == 0.0 ? "0" : Double.toString(resultSet.getDouble("last_refill_duration"));
                String sendMessage = resultSet.getObject("send_message") == null ? "" : Integer.toString(resultSet.getInt("send_message"));
                String timeStamp = resultSet.getObject("time_stamp") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("time_stamp"), "MM/dd/yyyy");

                String communitypharmId = resultSet.getObject("communitypharm_id") == null ? "" : Integer.toString(resultSet.getInt("communitypharm_id"));
                String casemanagerId = resultSet.getObject("casemanager_id") == null ? "" : Integer.toString(resultSet.getInt("casemanager_id"));

                Boolean biometric = resultSet.getObject("biometric") == null ? false : resultSet.getBoolean("biometric");

                // create an array from object properties 
                Map<String, String> map = new HashMap<>();
                map.put("patientId", patientId);
                map.put("facilityId", facilityId);
                map.put("hospitalNum", hospitalNum);
                map.put("uniqueId", uniqueId);
                map.put("surname", surname);
                map.put("otherNames", otherNames);
                map.put("name", surname + ' ' + otherNames);
                map.put("gender", gender);
                map.put("dateBirth", dateBirth);
                map.put("age", age);
                map.put("ageUnit", ageUnit);
                map.put("maritalStatus", maritalStatus);
                map.put("address", address);
                map.put("phone", phone);
                map.put("education", education);
                map.put("occupation", occupation);
                map.put("state", state);
                map.put("lga", lga);
                map.put("nextKin", nextKin);
                map.put("addressKin", addressKin);
                map.put("phoneKin", phoneKin);
                map.put("relationKin", relationKin);
                map.put("entryPoint", entryPoint);
                map.put("dateConfirmedHiv", dateConfirmedHiv);
                map.put("pregnant", pregnant);
                map.put("breastfeeding", breastfeeding);
                map.put("dateRegistration", dateRegistration);
                map.put("statusRegistration", statusRegistration);
                map.put("enrollmentSetting", enrollmentSetting);
                map.put("currentStatus", currentStatus);
                map.put("dateCurrentStatus", dateCurrentStatus);
                map.put("dateEnrolledPmtct", dateEnrolledPmtct);
                map.put("sourceReferral", sourceReferral);
                map.put("timeHivDiagnosis", timeHivDiagnosis);
                map.put("dateStarted", dateStarted);
                map.put("dateLastClinic", dateLastClinic);
                map.put("dateLastRefill", dateLastRefill);
                map.put("dateNextClinic", dateNextClinic);
                map.put("dateNextRefill", dateNextRefill);
                map.put("dateLastCd4", dateLastCd4);
                map.put("dateLastViralLoad", dateLastViralLoad);
                map.put("lastViralLoad", lastViralLoad);
                map.put("lastCd4", lastCd4);
                map.put("lastCd4p", lastCd4p);
                map.put("lastClinicStage", lastClinicStage);
                map.put("regimentype", regimentype);
                map.put("regimen", regimen);
                map.put("lastRefillSetting", lastRefillSetting);
                map.put("lastRefillDuration", lastRefillDuration);
                map.put("sendMessage", sendMessage);
                map.put("timeStamp", timeStamp);
                map.put("tbStatus", tbStatus);

                map.put("biometric", biometric.toString());
                map.put("communitypharmId", communitypharmId);
                map.put("casemanagerId", casemanagerId);
                //Check if this patient ARV refill has been devolved to a community pharmacy
                map.put("devolve", communitypharmId.isEmpty() ? "0" : "1");

                //Check if this patient ARV refill has been devolved to a community pharmacy
                boolean dueViralLoad = new PatientJDBC().dueViralLoad(resultSet.getLong("patient_id"));
                map.put("dueViralLoad", dueViralLoad ? "1" : "0");
                map.put("sel", "0");
                map.put("viralLoadType", viralLoadType);
                sortedMaps.put(surname + otherNames, map);
            }
            for (Map.Entry<String, Map<String, String>> entry : sortedMaps.entrySet()) {
                patientList.add(entry.getValue());
            }
            session.setAttribute("patientList", patientList);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void buildDefaulterList(ResultSet resultSet) throws SQLException {
        try {
            int numRows = 0;
            while (resultSet.next()) {
                String patientId = Long.toString(resultSet.getLong("patient_id"));
                String hospitalNum = resultSet.getString("hospital_num");
                String surname = resultSet.getObject("surname") == null ? "" : resultSet.getString("surname");
                surname = (viewIdentifier) ? scrambler.unscrambleCharacters(surname) : surname;
                surname = StringUtils.upperCase(surname);
                String otherNames = resultSet.getObject("other_names") == null ? "" : resultSet.getString("other_names");
                otherNames = (viewIdentifier) ? scrambler.unscrambleCharacters(otherNames) : otherNames;
                otherNames = StringUtils.capitalize(otherNames);
                String name = surname + " " + otherNames;
                String gender = resultSet.getObject("gender") == null ? "" : resultSet.getString("gender");
                String sourceReferral = resultSet.getObject("source_referral") == null ? "" : resultSet.getString("source_referral");
                String timeHivDiagnosis = resultSet.getObject("time_hiv_diagnosis") == null ? "" : resultSet.getString("time_hiv_diagnosis");
                String age = resultSet.getObject("age") == null ? "" : resultSet.getInt("age") == 0 ? "" : Integer.toString(resultSet.getInt("age"));
                String address = resultSet.getObject("address") == null ? "" : resultSet.getString("address");
                address = (viewIdentifier) ? scrambler.unscrambleCharacters(address) : address;
                address = StringUtils.capitalize(address);
                String phone = resultSet.getObject("phone") == null ? "" : resultSet.getString("phone");
                phone = (viewIdentifier) ? scrambler.unscrambleNumbers(phone) : phone;
                String currentStatus = resultSet.getObject("current_status") == null ? "" : resultSet.getString("current_status");
                String dateCurrentStatus = resultSet.getObject("date_current_status") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_current_status"), "MM/dd/yyyy");
                String dateLastRefill = resultSet.getObject("date_last_refill") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_last_refill"), "MM/dd/yyyy");
                String dateLastClinic = resultSet.getObject("date_last_clinic") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_last_clinic"), "MM/dd/yyyy");
                String dateEnrolledPmtct = resultSet.getObject("date_enrolled_pmtct") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_enrolled_pmtct"), "MM/dd/yyyy");
                String dateTracked = resultSet.getObject("date_tracked") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_tracked"), "MM/dd/yyyy");
                String outcome = resultSet.getObject("outcome") == null ? "" : resultSet.getString("outcome");
                String agreedDate = resultSet.getObject("agreed_date") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("agreed_date"), "MM/dd/yyyy");
                String tbStatus = resultSet.getObject("tb_status") == null ? "" : resultSet.getString("tb_status");

                Map<String, String> map = new HashMap<>();
                map.put("patientId", patientId);
                map.put("hospitalNum", hospitalNum);
                map.put("name", name);
                map.put("gender", gender);
                map.put("age", age);
                map.put("address", address);
                map.put("phone", phone);
                map.put("currentStatus", currentStatus);
                map.put("dateCurrentStatus", dateCurrentStatus);
                map.put("dateLastRefill", dateLastRefill);
                map.put("dateLastClinic", dateLastClinic);
                map.put("dateTracked", dateTracked);
                map.put("dateEnrolledPmtct", dateEnrolledPmtct);
                map.put("sourceReferral", sourceReferral);
                map.put("timeHivDiagnosis", timeHivDiagnosis);
                map.put("outcome", outcome);
                map.put("agreedDate", agreedDate);
                map.put("updated", "false");
                map.put("tbStatus", tbStatus);
                sortedMaps.put(surname + otherNames, map);
                numRows++;
            } //end while
            for (Map.Entry<String, Map<String, String>> entry : sortedMaps.entrySet()) {
                defaulterList.add(entry.getValue());
            }
            session.setAttribute("defaulterList", defaulterList);
            defaulterList = null;
            sortedMaps = null;
        } catch (SQLException sqlException) {
            throw sqlException;
        }
    }

    public ArrayList<Map<String, String>> retrievePatientList() {
        // retrieve the patient record store in session attribute
        if (session.getAttribute("patientList") != null) {
            patientList = (ArrayList) session.getAttribute("patientList");
        }
        return patientList;
    }

    public ArrayList<Map<String, String>> retrieveDefaulterList() {
        // retrieve the patient record store in session attribute
        if (session.getAttribute("defaulterList") != null) {
            defaulterList = (ArrayList) session.getAttribute("defaulterList");
        }
        return defaulterList;
    }

    public void clearPatientList() {
        patientList = retrievePatientList();
        patientList.clear();
        session.setAttribute("patientList", patientList);
    }

}
