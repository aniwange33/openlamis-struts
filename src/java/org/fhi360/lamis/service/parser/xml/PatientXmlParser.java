/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.service.parser.xml;

import org.fhi360.lamis.dao.hibernate.PatientDAO;
import org.fhi360.lamis.model.Facility;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.utility.Scrambler;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.fhi360.lamis.service.ServerIDProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PatientXmlParser extends DefaultHandler {

    private long facilityId;
    private Long idOnServer, casemanagerIdLocal;
    private String hospitalNum;
    private boolean populated;
    private Patient patient;
    private Facility facility = new Facility();
    private Scrambler scrambler = new Scrambler();

    public PatientXmlParser() {
    }

    
    public void parseXml(String xmlFileName) {
        populated = false;
        try {
            //obtain and configure a SAX based parser
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

            //obtain object for SAX hadler class
            SAXParser saxParser = saxParserFactory.newSAXParser();

            //default handler for SAX handler class
            // all three methods are written in handler's body
            DefaultHandler defaultHandler = new DefaultHandler() {
                String patientTag = "close";
                String patientIdTag = "close";
                String facilityIdTag = "close";
                String hospitalNumTag = "close";
                String uniqueIdTag = "close";
                String surnameTag = "close";
                String otherNamesTag = "close";
                String genderTag = "close";
                String dateBirthTag = "close";
                String ageTag = "close";
                String ageUnitTag = "close";
                String maritalStatusTag = "close";
                String educationTag = "close";
                String occupationTag = "close";
                String addressTag = "close";
                String phoneTag = "close";
                String stateTag = "close";
                String lgaTag = "close";
                String nextKinTag = "close";
                String addressKinTag = "close";
                String phoneKinTag = "close";
                String relationKinTag = "close";
                String entryPointTag = "close";
                String targetGroupTag = "close";
                String dateConfirmedHivTag = "close";
                String tbStatusTag = "close";
                String pregnantTag = "close";
                String breastfeedingTag = "close";
                String lmpTag = "close";
                String dateRegistrationTag = "close";
                String statusRegistrationTag = "close";
                String enrollmentSettingTag = "close";
                String dateStartedTag = "close";
                String currentStatusTag = "close";
                String dateCurrentStatusTag = "close";
                String regimentypeTag = "close";
                String regimenTag = "close";
                String lastClinicStageTag = "close";
                String lastViralLoadTag = "close";
                String lastCd4Tag = "close";
                String lastCd4pTag = "close";
                String dateLastCd4Tag = "close";
                String dateLastViralLoadTag = "close";
                String viralLoadDueDateTag = "close";
                String viralLoadTypeTag = "close";
                String dateLastRefillTag = "close";
                String dateNextRefillTag = "close";
                String lastRefillDurationTag = "close";
                String lastRefillSettingTag = "close";
                String dateLastClinicTag = "close";
                String dateNextClinicTag = "close";
                String dateTrackedTag = "close";
                String outcomeTag = "close";
                String agreedDateTag = "close";
                String causeDeathTag = "close";
                String sendMessageTag = "close";
                String timeStampTag = "close";
                String caseManagerIdTag = "close";
                String communitypharmIdTag = "close";
                String userIdTag = "close";
                String idUUIDTag = "close";

                //this method is called every time the parser gets an open tag '<'
                //identifies which element is being open at the time by assigning an open flag
                @Override
                public void startElement(String uri, String localName, String element, Attributes attributes) throws SAXException {
                    if (element.equalsIgnoreCase("patient")) {
                        patientTag = "open";
                        patient = new Patient();
                    }
                    if (element.equalsIgnoreCase("patient_id")) {
                        patientIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("hospital_num")) {
                        hospitalNumTag = "open";
                    }
                    if (element.equalsIgnoreCase("unique_id")) {
                        uniqueIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("surname")) {
                        surnameTag = "open";
                    }
                    if (element.equalsIgnoreCase("other_names")) {
                        otherNamesTag = "open";
                    }
                    if (element.equalsIgnoreCase("gender")) {
                        genderTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_birth")) {
                        dateBirthTag = "open";
                    }
                    if (element.equalsIgnoreCase("age")) {
                        ageTag = "open";
                    }
                    if (element.equalsIgnoreCase("age_unit")) {
                        ageUnitTag = "open";
                    }
                    if (element.equalsIgnoreCase("marital_status")) {
                        maritalStatusTag = "open";
                    }
                    if (element.equalsIgnoreCase("education")) {
                        educationTag = "open";
                    }
                    if (element.equalsIgnoreCase("occupation")) {
                        occupationTag = "open";
                    }
                    if (element.equalsIgnoreCase("address")) {
                        addressTag = "open";
                    }
                    if (element.equalsIgnoreCase("phone")) {
                        phoneTag = "open";
                    }
                    if (element.equalsIgnoreCase("state")) {
                        stateTag = "open";
                    }
                    if (element.equalsIgnoreCase("lga")) {
                        lgaTag = "open";
                    }
                    if (element.equalsIgnoreCase("next_kin")) {
                        nextKinTag = "open";
                    }
                    if (element.equalsIgnoreCase("address_kin")) {
                        addressKinTag = "open";
                    }
                    if (element.equalsIgnoreCase("phone_kin")) {
                        phoneKinTag = "open";
                    }
                    if (element.equalsIgnoreCase("relation_kin")) {
                        relationKinTag = "open";
                    }
                    if (element.equalsIgnoreCase("entry_point")) {
                        entryPointTag = "open";
                    }
                    if (element.equalsIgnoreCase("target_group")) {
                        targetGroupTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_confirmed_hiv")) {
                        dateConfirmedHivTag = "open";
                    }
                    if (element.equalsIgnoreCase("tb_status")) {
                        tbStatusTag = "open";
                    }
                    if (element.equalsIgnoreCase("pregnant")) {
                        pregnantTag = "open";
                    }
                    if (element.equalsIgnoreCase("breastfeeding")) {
                        breastfeedingTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_registration")) {
                        dateRegistrationTag = "open";
                    }
                    if (element.equalsIgnoreCase("status_registration")) {
                        statusRegistrationTag = "open";
                    }
                    if (element.equalsIgnoreCase("enrollment_setting")) {
                        enrollmentSettingTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_started")) {
                        dateStartedTag = "open";
                    }
                    if (element.equalsIgnoreCase("current_status")) {
                        currentStatusTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_current_status")) {
                        dateCurrentStatusTag = "open";
                    }
                    if (element.equalsIgnoreCase("regimentype")) {
                        regimentypeTag = "open";
                    }
                    if (element.equalsIgnoreCase("regimen")) {
                        regimenTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_last_refill")) {
                        dateLastRefillTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_next_refill")) {
                        dateNextRefillTag = "open";
                    }
                    if (element.equalsIgnoreCase("last_refill_duration")) {
                        lastRefillDurationTag = "open";
                    }
                    if (element.equalsIgnoreCase("last_refill_setting")) {
                        lastRefillSettingTag = "open";
                    }

                    if (element.equalsIgnoreCase("date_last_clinic")) {
                        dateLastClinicTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_next_clinic")) {
                        dateNextClinicTag = "open";
                    }
                    if (element.equalsIgnoreCase("last_clinic_stage")) {
                        lastClinicStageTag = "open";
                    }
                    if (element.equalsIgnoreCase("last_cd4")) {
                        lastCd4Tag = "open";
                    }
                    if (element.equalsIgnoreCase("last_cd4p")) {
                        lastCd4pTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_last_cd4")) {
                        dateLastCd4Tag = "open";
                    }
                    if (element.equalsIgnoreCase("last_viral_load")) {
                        lastViralLoadTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_last_viral_load")) {
                        dateLastViralLoadTag = "open";
                    }
                    if (element.equalsIgnoreCase("viral_load_due_date")) {
                        viralLoadDueDateTag = "open";
                    }
                    if (element.equalsIgnoreCase("viral_load_type")) {
                        viralLoadTypeTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_tracked")) {
                        dateTrackedTag = "open";
                    }
                    if (element.equalsIgnoreCase("outcome")) {
                        outcomeTag = "open";
                    }
                    if (element.equalsIgnoreCase("agreed_date")) {
                        agreedDateTag = "open";
                    }
                    if (element.equalsIgnoreCase("cause_death")) {
                        causeDeathTag = "open";
                    }
                    if (element.equalsIgnoreCase("send_message")) {
                        sendMessageTag = "open";
                    }
                    if (element.equalsIgnoreCase("time_stamp")) {
                        timeStampTag = "open";
                    }
                    if (element.equalsIgnoreCase("casemanager_id")) {
                        caseManagerIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("communitypharm_id")) {
                        communitypharmIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("user_id")) {
                        userIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("id_uuid") || element.equalsIgnoreCase("uuid")) {
                        idUUIDTag = "open";
                    }
                }

                //store data store in between '<' and '>' tags                     
                @Override
                public void characters(char[] chars, int start, int length) throws SAXException {
                    String statusRegistration = "";
                    String dateRegistration = "";

                    if (facilityIdTag.equals("open")) {
                        facilityId = Long.parseLong(new String(chars, start, length));
                        facility.setFacilityId(facilityId);
                        patient.setFacility(facility);
                    }
                    if (hospitalNumTag.equals("open")) {
                        hospitalNum = new String(chars, start, length);
                        patient.setHospitalNum(hospitalNum);
                    }
                    if (uniqueIdTag.equals("open")) {
                        patient.setUniqueId(new String(chars, start, length));
                    }
                    if (surnameTag.equals("open")) {
                        patient.setSurname(scrambler.scrambleCharacters(new String(chars, start, length)));
                    }
                    if (otherNamesTag.equals("open")) {
                        patient.setOtherNames(scrambler.scrambleCharacters(new String(chars, start, length)));
                    }
                    if (genderTag.equals("open")) {
                        patient.setGender(new String(chars, start, length));
                    }
                    if (dateBirthTag.equals("open")) {
                        String dateBirth = new String(chars, start, length);
                        if (!dateBirth.trim().isEmpty()) {
                            patient.setDateBirth(DateUtil.parseStringToDate(dateBirth, "yyyy-MM-dd"));
                        }
                    }
                    if (ageTag.equals("open")) {
                        String age = new String(chars, start, length);
                        if (!age.trim().isEmpty()) {
                            patient.setAge(Integer.parseInt(age));
                        }
                    }
                    if (ageUnitTag.equals("open")) {
                        patient.setAgeUnit(new String(chars, start, length));
                    }
                    if (maritalStatusTag.equals("open")) {
                        patient.setMaritalStatus(new String(chars, start, length));
                    }
                    if (educationTag.equals("open")) {
                        patient.setEducation(new String(chars, start, length));
                    }
                    if (occupationTag.equals("open")) {
                        patient.setOccupation(new String(chars, start, length));
                    }
                    if (addressTag.equals("open")) {
                        patient.setAddress(scrambler.scrambleCharacters(new String(chars, start, length)));
                    }
                    if (phoneTag.equals("open")) {
                        patient.setPhone(scrambler.scrambleNumbers(new String(chars, start, length)));
                    }
                    if (stateTag.equals("open")) {
                        patient.setState(new String(chars, start, length));
                    }
                    if (lgaTag.equals("open")) {
                        patient.setLga(new String(chars, start, length));
                    }
                    if (nextKinTag.equals("open")) {
                        patient.setNextKin(scrambler.scrambleCharacters(new String(chars, start, length)));
                    }
                    if (addressKinTag.equals("open")) {
                        patient.setAddressKin(scrambler.scrambleCharacters(new String(chars, start, length)));
                    }
                    if (phoneKinTag.equals("open")) {
                        patient.setPhoneKin(scrambler.scrambleNumbers(new String(chars, start, length)));
                    }
                    if (relationKinTag.equals("open")) {
                        patient.setRelationKin(new String(chars, start, length));
                    }
                    if (entryPointTag.equals("open")) {
                        patient.setEntryPoint(new String(chars, start, length));
                    }
                    if (targetGroupTag.equals("open")) {
                        //patient.setTargetGroup(new String(chars, start, length));
                    }
                    if (dateConfirmedHivTag.equals("open")) {
                        String dateConfirmedHiv = new String(chars, start, length);
                        if (!dateConfirmedHiv.trim().isEmpty()) {
                            patient.setDateConfirmedHiv(DateUtil.parseStringToDate(dateConfirmedHiv, "yyyy-MM-dd"));
                        }
                    }
                    if (tbStatusTag.equals("open")) {
                        patient.setTbStatus(new String(chars, start, length));
                    }
                    if (pregnantTag.equals("open")) {
                        String pregnant = new String(chars, start, length);
                        if (!pregnant.trim().isEmpty()) {
                            patient.setPregnant(Integer.parseInt(pregnant));
                        }
                    }
                    if (breastfeedingTag.equals("open")) {
                        String breastfeeding = new String(chars, start, length);
                        if (!breastfeeding.trim().isEmpty()) {
                            patient.setBreastfeeding(Integer.parseInt(breastfeeding));
                        }
                    }
                    if (dateRegistrationTag.equals("open")) {
                        dateRegistration = new String(chars, start, length);
                        if (!dateRegistration.trim().isEmpty()) {
                            patient.setDateRegistration(DateUtil.parseStringToDate(dateRegistration, "yyyy-MM-dd"));
                        }
                    }
                    if (statusRegistrationTag.equals("open")) {
                        statusRegistration = new String(chars, start, length);
                        patient.setStatusRegistration(statusRegistration);
                    }
                    if (enrollmentSettingTag.equals("open")) {
                        String enrollmentSetting = new String(chars, start, length);
                        patient.setEnrollmentSetting(enrollmentSetting);
                    }
                    if (dateStartedTag.equals("open")) {
                        String dateStarted = new String(chars, start, length);
                        if (!dateStarted.trim().isEmpty()) {
                            patient.setDateStarted(DateUtil.parseStringToDate(dateStarted, "yyyy-MM-dd"));
                        }
                    }
                    if (currentStatusTag.equals("open")) {
                        String currentStatus = new String(chars, start, length);
                        if (!currentStatus.trim().isEmpty()) {
                            patient.setCurrentStatus(currentStatus);
                        } else {
                            patient.setCurrentStatus(statusRegistration);
                        }
                    }
                    if (dateCurrentStatusTag.equals("open")) {
                        String dateCurrentStatus = new String(chars, start, length);
                        if (!dateCurrentStatus.trim().isEmpty()) {
                            patient.setDateCurrentStatus(DateUtil.parseStringToDate(dateCurrentStatus, "yyyy-MM-dd"));
                        } else {
                            patient.setDateCurrentStatus(DateUtil.parseStringToDate(dateRegistration, "yyyy-MM-dd"));
                        }
                    }
                    if (regimentypeTag.equals("open")) {
                        patient.setRegimentype(new String(chars, start, length));
                    }
                    if (regimenTag.equals("open")) {
                        patient.setRegimen(new String(chars, start, length));
                    }
                    if (dateLastRefillTag.equals("open")) {
                        String dateLastRefill = new String(chars, start, length);
                        if (!dateLastRefill.trim().isEmpty()) {
                            patient.setDateLastRefill(DateUtil.parseStringToDate(dateLastRefill, "yyyy-MM-dd"));
                        }
                    }
                    if (dateNextRefillTag.equals("open")) {
                        String dateNextRefill = new String(chars, start, length);
                        if (!dateNextRefill.trim().isEmpty()) {
                            patient.setDateNextRefill(DateUtil.parseStringToDate(dateNextRefill, "yyyy-MM-dd"));
                        }
                    }
                    if (lastRefillDurationTag.equals("open")) {
                        String lastRefillDuration = new String(chars, start, length);
                        if (!lastRefillDuration.trim().isEmpty()) {
                            patient.setLastRefillDuration(Integer.parseInt(lastRefillDuration));
                        }
                    }
                    if (lastRefillSettingTag.equals("open")) {
                        patient.setLastRefillSetting(new String(chars, start, length));
                    }

                    if (dateLastClinicTag.equals("open")) {
                        String dateLastClinic = new String(chars, start, length);
                        if (!dateLastClinic.trim().isEmpty()) {
                            patient.setDateLastClinic(DateUtil.parseStringToDate(dateLastClinic, "yyyy-MM-dd"));
                        }
                    }
                    if (dateNextClinicTag.equals("open")) {
                        String dateNextClinic = new String(chars, start, length);
                        if (!dateNextClinic.trim().isEmpty()) {
                            patient.setDateNextClinic(DateUtil.parseStringToDate(dateNextClinic, "yyyy-MM-dd"));
                        }
                    }
                    if (lastCd4Tag.equals("open")) {
                        String lastCd4 = new String(chars, start, length);
                        if (!lastCd4.trim().isEmpty()) {
                            patient.setLastCd4(Double.parseDouble(lastCd4));
                        }
                    }
                    if (lastCd4pTag.equals("open")) {
                        String lastCd4p = new String(chars, start, length);
                        if (!lastCd4p.trim().isEmpty()) {
                            patient.setLastCd4p(Double.parseDouble(lastCd4p));
                        }
                    }
                    if (dateLastCd4Tag.equals("open")) {
                        String dateLastCd4 = new String(chars, start, length);
                        if (!dateLastCd4.trim().isEmpty()) {
                            patient.setDateLastCd4(DateUtil.parseStringToDate(dateLastCd4, "yyyy-MM-dd"));
                        }
                    }
                    if (lastClinicStageTag.equals("open")) {
                        String lastClinicStage = new String(chars, start, length) == null ? "" : new String(chars, start, length);
                        patient.setLastClinicStage(lastClinicStage);
                    }
                    if (dateTrackedTag.equals("open")) {
                        String dateTracked = new String(chars, start, length);
                        if (!dateTracked.trim().isEmpty()) {
                            patient.setDateTracked(DateUtil.parseStringToDate(dateTracked, "yyyy-MM-dd"));
                        }
                    }
                    if (outcomeTag.equals("open")) {
                        patient.setOutcome(new String(chars, start, length));
                    }
                    if (causeDeathTag.equals("open")) {
                        patient.setCauseDeath(new String(chars, start, length));
                    }
                    if (agreedDateTag.equals("open")) {
                        String agreedDate = new String(chars, start, length);
                        if (!agreedDate.trim().isEmpty()) {
                            patient.setAgreedDate(DateUtil.parseStringToDate(agreedDate, "yyyy-MM-dd"));
                        }
                    }
                    if (lastViralLoadTag.equals("open")) {
                        String lastViralLoad = new String(chars, start, length);
                        if (!lastViralLoad.trim().isEmpty()) {
                            patient.setLastViralLoad(Double.parseDouble(lastViralLoad));
                        }
                    }
                    if (dateLastViralLoadTag.equals("open")) {
                        String dateLastViralLoad = new String(chars, start, length);
                        if (!dateLastViralLoad.trim().isEmpty()) {
                            patient.setDateLastViralLoad(DateUtil.parseStringToDate(dateLastViralLoad, "yyyy-MM-dd"));
                        }
                    }
                    if (viralLoadDueDateTag.equals("open")) {
                        String viralLoadDueDate = new String(chars, start, length);
                        if (!viralLoadDueDateTag.trim().isEmpty()) {
                            patient.setViralLoadDueDate(DateUtil.parseStringToDate(viralLoadDueDate, "yyyy-MM-dd"));
                        }
                    }
                    if (viralLoadTypeTag.equals("open")) {
                        patient.setViralLoadType(new String(chars, start, length));
                    }
                    if (sendMessageTag.equals("open")) {
                        String sendMessage = new String(chars, start, length);
                        if (!sendMessage.trim().isEmpty()) {
                            patient.setSendMessage(Integer.parseInt(sendMessage));
                        }
                    }
                    if (timeStampTag.equals("open")) {
                        patient.setTimeStamp(new java.sql.Date(new java.util.Date().getTime()));
                    }
                    if (communitypharmIdTag.equals("open")) {
                        String communitypharmId = new String(chars, start, length);
                        if (!communitypharmId.trim().isEmpty()) {
                            patient.setCommunitypharmId(Long.parseLong(communitypharmId));
                        }
                    }
                    if (caseManagerIdTag.equals("open")) {
                        String caseManagerId = new String(chars, start, length);
                        if (!caseManagerId.trim().isEmpty()) {
                            patient.setCasemanagerId(ServerIDProvider
                                    .getCaseManagerId(Long.parseLong(caseManagerId), facilityId));
                        }
                    }
                    if (userIdTag.equals("open")) {
                        String userId = new String(chars, start, length);

                    }
                    if (idUUIDTag.equals("open")) {
                        patient.setUuid(new String(chars, start, length));
                    }
                }

                @Override
                public void endElement(String uri, String localName, String element) throws SAXException {
                    if (element.equalsIgnoreCase("patient_id")) {
                        patientIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("hospital_num")) {
                        hospitalNumTag = "close";
                    }
                    if (element.equalsIgnoreCase("unique_id")) {
                        uniqueIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("surname")) {
                        surnameTag = "close";
                    }
                    if (element.equalsIgnoreCase("other_names")) {
                        otherNamesTag = "close";
                    }
                    if (element.equalsIgnoreCase("gender")) {
                        genderTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_birth")) {
                        dateBirthTag = "close";
                    }
                    if (element.equalsIgnoreCase("age")) {
                        ageTag = "close";
                    }
                    if (element.equalsIgnoreCase("age_unit")) {
                        ageUnitTag = "close";
                    }
                    if (element.equalsIgnoreCase("marital_status")) {
                        maritalStatusTag = "close";
                    }
                    if (element.equalsIgnoreCase("education")) {
                        educationTag = "close";
                    }
                    if (element.equalsIgnoreCase("occupation")) {
                        occupationTag = "close";
                    }
                    if (element.equalsIgnoreCase("address")) {
                        addressTag = "close";
                    }
                    if (element.equalsIgnoreCase("phone")) {
                        phoneTag = "close";
                    }
                    if (element.equalsIgnoreCase("state")) {
                        stateTag = "close";
                    }
                    if (element.equalsIgnoreCase("lga")) {
                        lgaTag = "close";
                    }
                    if (element.equalsIgnoreCase("next_kin")) {
                        nextKinTag = "close";
                    }
                    if (element.equalsIgnoreCase("address_kin")) {
                        addressKinTag = "close";
                    }
                    if (element.equalsIgnoreCase("phone_kin")) {
                        phoneKinTag = "close";
                    }
                    if (element.equalsIgnoreCase("relation_kin")) {
                        relationKinTag = "close";
                    }
                    if (element.equalsIgnoreCase("target_group")) {
                        targetGroupTag = "close";
                    }
                    if (element.equalsIgnoreCase("entry_point")) {
                        entryPointTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_confirmed_hiv")) {
                        dateConfirmedHivTag = "close";
                    }
                    if (element.equalsIgnoreCase("tb_status")) {
                        tbStatusTag = "close";
                    }
                    if (element.equalsIgnoreCase("pregnant")) {
                        pregnantTag = "close";
                    }
                    if (element.equalsIgnoreCase("breastfeeding")) {
                        breastfeedingTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_registration")) {
                        dateRegistrationTag = "close";
                    }
                    if (element.equalsIgnoreCase("status_registration")) {
                        statusRegistrationTag = "close";
                    }
                    if (element.equalsIgnoreCase("enrollment_setting")) {
                        enrollmentSettingTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_started")) {
                        dateStartedTag = "close";
                    }
                    if (element.equalsIgnoreCase("current_status")) {
                        currentStatusTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_current_status")) {
                        dateCurrentStatusTag = "close";
                    }
                    if (element.equalsIgnoreCase("regimentype")) {
                        regimentypeTag = "close";
                    }
                    if (element.equalsIgnoreCase("regimen")) {
                        regimenTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_last_refill")) {
                        dateLastRefillTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_next_refill")) {
                        dateNextRefillTag = "close";
                    }

                    if (element.equalsIgnoreCase("last_refill_duration")) {
                        lastRefillDurationTag = "close";
                    }
                    if (element.equalsIgnoreCase("last_refill_setting")) {
                        lastRefillSettingTag = "close";
                    }

                    if (element.equalsIgnoreCase("date_last_clinic")) {
                        dateLastClinicTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_next_clinic")) {
                        dateNextClinicTag = "close";
                    }
                    if (element.equalsIgnoreCase("last_clinic_stage")) {
                        lastClinicStageTag = "close";
                    }
                    if (element.equalsIgnoreCase("last_cd4")) {
                        lastCd4Tag = "close";
                    }
                    if (element.equalsIgnoreCase("last_cd4p")) {
                        lastCd4pTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_last_cd4")) {
                        dateLastCd4Tag = "close";
                    }
                    if (element.equalsIgnoreCase("last_viral_load")) {
                        lastViralLoadTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_last_viral_load")) {
                        dateLastViralLoadTag = "close";
                    }
                    if (element.equalsIgnoreCase("viral_load_due_date")) {
                        viralLoadDueDateTag = "close";
                    }
                    if (element.equalsIgnoreCase("viral_load_type")) {
                        viralLoadTypeTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_tracked")) {
                        dateTrackedTag = "close";
                    }
                    if (element.equalsIgnoreCase("outcome")) {
                        outcomeTag = "close";
                    }
                    if (element.equalsIgnoreCase("agreed_date")) {
                        agreedDateTag = "close";
                    }
                    if (element.equalsIgnoreCase("cause_death")) {
                        causeDeathTag = "close";
                    }
                    if (element.equalsIgnoreCase("send_message")) {
                        sendMessageTag = "close";
                    }
                    if (element.equalsIgnoreCase("time_stamp")) {
                        timeStampTag = "close";
                    }
                    if (element.equalsIgnoreCase("casemanager_id")) {
                        caseManagerIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("communitypharm_id")) {
                        communitypharmIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("user_id")) {
                        userIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("id_uuid") || element.equalsIgnoreCase("uuid")) {
                        idUUIDTag = "close";
                    }

                    //if this is the closing tag of a patient element save the record
                    if (element.equalsIgnoreCase("patient")) {
                        patientTag = "close";
                        Long patientId = ServerIDProvider.getPatientServerId(
                                patient.getHospitalNum(), facilityId
                        );
                        if (patientId != null) {
                            patient.setPatientId(patientId);
                            PatientDAO.update(patient);
                        } else {
                            try {
                                PatientDAO.save(patient);
                            } catch (Exception ignored) {
                            }
                        }
                    }
                }
            };
            //parse the XML specified in the given path and uses supplied handler to parse the document
            //this calls startElement(), endElement(), and character() methods accordingly
            saxParser.parse(xmlFileName, defaultHandler);
            // new CleanupService().cleanNullFields("patient", facilityId);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }

}
