/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.parser.xml;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.fhi360.lamis.dao.hibernate.HtsDAO;
import org.fhi360.lamis.dao.jdbc.HtsJDBC;
import org.fhi360.lamis.model.Hts;
import org.fhi360.lamis.utility.DateUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author User10
 */
public class HtsXmlParser extends DefaultHandler {

    private long facilityId;
    private boolean skipRecord;
    private Hts hts;

    public HtsXmlParser() {
    }

    public void parseXml(String xmlFileName) {
        try {
            //obtain and configure a SAX based parser
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

            //obtain object for SAX hadler class
            SAXParser saxParser = saxParserFactory.newSAXParser();

            //default handler for SAX handler class
            // all three methods are written in handler's body
            DefaultHandler defaultHandler = new DefaultHandler() {
                String htsTag = "close";
                String historyIdTag = "close";
                String facilityIdTag = "close";
                String hospitalNumTag = "close";
                String dateVisitTag = "close";
                String timeStampTag = "close";
                String idUUIDTag = "close";
                String clientCodeTag = "close";
                String stateIDTag = "close";
                String lgaIDTag = "close";
                String facilityNameTag = "close";
                String referredFromTag = "close";
                String testingSettingTag = "close";
                String surnameTag = "close";
                String otherNamesTag = "close";
                String dateBirthTag = "close";
                String ageTag = "close";
                String ageUnitTag = "close";
                String phoneTag = "close";
                String addressTag = "close";
                String genderTag = "close";
                String firstTimeVisitTag = "close";
                String stateTag = "close";
                String lgaTag = "close";
                String maritalStatusTag = "close";
                String numChildrenTag = "close";
                String numWivesTag = "close";
                String typeCounselingTag = "close";
                String indexClientTag = "close";
                String indexTypeTag = "close";
                String indexClientCodeTag = "close";
                String knowledgeAssessment1Tag = "close";
                String knowledgeAssessment2Tag = "close";
                String knowledgeAssessment3Tag = "close";
                String knowledgeAssessment4Tag = "close";
                String knowledgeAssessment5Tag = "close";
                String knowledgeAssessment6Tag = "close";
                String knowledgeAssessment7Tag = "close";
                String riskAssessment1Tag = "close";
                String riskAssessment6Tag = "close";
                String riskAssessment2Tag = "close";
                String riskAssessment3Tag = "close";
                String riskAssessment4Tag = "close";
                String riskAssessment5Tag = "close";
                String tbScreening1Tag = "close";
                String tbScreening2Tag = "close";
                String tbScreening3Tag = "close";
                String tbScreening4Tag = "close";
                String stiScreening1Tag = "close";
                String stiScreening2Tag = "close";
                String stiScreening3Tag = "close";
                String stiScreening4Tag = "close";
                String stiScreening5Tag = "close";
                String hivTestResultTag = "close";
                String testedHivTag = "close";
                String postTest1Tag = "close";
                String postTest2Tag = "close";
                String postTest3Tag = "close";
                String postTest4Tag = "close";
                String postTest5Tag = "close";
                String postTest6Tag = "close";
                String postTest7Tag = "close";
                String postTest8Tag = "close";
                String postTest9Tag = "close";
                String postTest10Tag = "close";
                String postTest11Tag = "close";
                String postTest12Tag = "close";
                String postTest13Tag = "close";
                String postTest14Tag = "close";
                String syphilisTestResultTag = "close";
                String hepatitisbTestResultTag = "close";
                String hepatitiscTestResultTag = "close";
                String stiReferredTag = "close";
                String tbReferredTag = "close";
                String artReferredTag = "close";
                String partnerNotificationTag = "close";
                String notificationCounselingTag = "close";
                String numberPartnerTag = "close";
                String dateRegistrationTag = "close";
                String dateStartedTag = "close";
                String noteTag = "close";
                String longitudeTag = "close";
                String latitudeTag = "close";
                String deviceConfigIdTag = "close";
                String assessmentIdTag = "close";

                //this method is called every time the parser gets an open tag '<'
                //identifies which tag is being open at the time by assigning an open flag
                @Override
                public void startElement(String uri, String localName, String element, Attributes attributes) throws SAXException {
                    if (element.equalsIgnoreCase("hts")) {
                        htsTag = "open";
                        hts = new Hts();
                    }
                    if (element.equalsIgnoreCase("history_id")) {
                        historyIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("hospital_num")) {
                        hospitalNumTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_visit")) {
                        dateVisitTag = "open";
                    }
                    if (element.equalsIgnoreCase("time_stamp")) {
                        timeStampTag = "open";
                    }
                    if (element.equalsIgnoreCase("id_uuid") || element.equalsIgnoreCase("uuid")) {
                        idUUIDTag = "open";
                    }
                    if (element.equalsIgnoreCase("state_id")) {
                        stateIDTag = "open";
                    }
                    if (element.equalsIgnoreCase("lga_id")) {
                        lgaIDTag = "open";
                    }
                    if (element.equalsIgnoreCase("state")) {
                        stateTag = "open";
                    }
                    if (element.equalsIgnoreCase("lga")) {
                        lgaTag = "open";
                    }
                    if (element.equalsIgnoreCase("facility_name")) {
                        facilityNameTag = "open";
                    }
                    if (element.equalsIgnoreCase("surname")) {
                        surnameTag = "open";
                    }
                    if (element.equalsIgnoreCase("other_names")) {
                        otherNamesTag = "open";
                    }
                    if (element.equalsIgnoreCase("testing_setting")) {
                        testingSettingTag = "open";
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
                    if (element.equalsIgnoreCase("date_registration")) {
                        dateRegistrationTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_started")) {
                        dateStartedTag = "open";
                    }
                    if (element.equalsIgnoreCase("referred_from")) {
                        referredFromTag = "open";
                    }
                    if (element.equalsIgnoreCase("client_code")) {
                        clientCodeTag = "open";
                    }
                    if (element.equalsIgnoreCase("deviceconfig_id")) {
                        deviceConfigIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("phone")) {
                        phoneTag = "open";
                    }
                    if (element.equalsIgnoreCase("address")) {
                        addressTag = "open";
                    }
                    if (element.equalsIgnoreCase("gender")) {
                        genderTag = "open";
                    }
                    if (element.equalsIgnoreCase("first_time_visit")) {
                        firstTimeVisitTag = "open";
                    }
                    if (element.equalsIgnoreCase("marital_status")) {
                        maritalStatusTag = "open";
                    }
                    if (element.equalsIgnoreCase("num_children")) {
                        numChildrenTag = "open";
                    }
                    if (element.equalsIgnoreCase("num_wives")) {
                        numWivesTag = "open";
                    }
                    if (element.equalsIgnoreCase("type_counseling")) {
                        typeCounselingTag = "open";
                    }
                    if (element.equalsIgnoreCase("index_client")) {
                        indexClientTag = "open";
                    }
                    if (element.equalsIgnoreCase("index_type")) {
                        indexTypeTag = "open";
                    }
                    if (element.equalsIgnoreCase("index_client_code")) {
                        indexClientCodeTag = "open";
                    }
                    if (element.equalsIgnoreCase("knowledge_assessment1")) {
                        knowledgeAssessment1Tag = "open";
                    }
                    if (element.equalsIgnoreCase("knowledge_assessment2")) {
                        knowledgeAssessment2Tag = "open";
                    }
                    if (element.equalsIgnoreCase("knowledge_assessment3")) {
                        knowledgeAssessment3Tag = "open";
                    }
                    if (element.equalsIgnoreCase("knowledge_assessment4")) {
                        knowledgeAssessment4Tag = "open";
                    }
                    if (element.equalsIgnoreCase("knowledge_assessment5")) {
                        knowledgeAssessment5Tag = "open";
                    }
                    if (element.equalsIgnoreCase("knowledge_assessment6")) {
                        knowledgeAssessment6Tag = "open";
                    }
                    if (element.equalsIgnoreCase("knowledge_assessment7")) {
                        knowledgeAssessment7Tag = "open";
                    }
                    if (element.equalsIgnoreCase("risk_assessment1")) {
                        riskAssessment1Tag = "open";
                    }
                    if (element.equalsIgnoreCase("risk_assessment1")) {
                        riskAssessment1Tag = "open";
                    }
                    if (element.equalsIgnoreCase("risk_assessment2")) {
                        riskAssessment2Tag = "open";
                    }
                    if (element.equalsIgnoreCase("risk_assessment3")) {
                        riskAssessment3Tag = "open";
                    }
                    if (element.equalsIgnoreCase("risk_assessment4")) {
                        riskAssessment4Tag = "open";
                    }
                    if (element.equalsIgnoreCase("risk_assessment5")) {
                        riskAssessment5Tag = "open";
                    }
                    if (element.equalsIgnoreCase("risk_assessment6")) {
                        riskAssessment6Tag = "open";
                    }
                    if (element.equalsIgnoreCase("tb_screening1")) {
                        tbScreening1Tag = "open";
                    }
                    if (element.equalsIgnoreCase("tb_screening2")) {
                        tbScreening2Tag = "open";
                    }
                    if (element.equalsIgnoreCase("tb_screening3")) {
                        tbScreening3Tag = "open";
                    }
                    if (element.equalsIgnoreCase("tb_screening4")) {
                        tbScreening4Tag = "open";
                    }
                    if (element.equalsIgnoreCase("sti_screening1")) {
                        stiScreening1Tag = "open";
                    }
                    if (element.equalsIgnoreCase("sti_screening2")) {
                        stiScreening2Tag = "open";
                    }
                    if (element.equalsIgnoreCase("sti_screening3")) {
                        stiScreening3Tag = "open";
                    }
                    if (element.equalsIgnoreCase("sti_screening4")) {
                        stiScreening4Tag = "open";
                    }
                    if (element.equalsIgnoreCase("sti_screening5")) {
                        stiScreening5Tag = "open";
                    }
                    if (element.equalsIgnoreCase("hiv_test_result")) {
                        hivTestResultTag = "open";
                    }
                    if (element.equalsIgnoreCase("tested_hiv")) {
                        testedHivTag = "open";
                    }
                    if (element.equalsIgnoreCase("post_test1")) {
                        postTest1Tag = "open";
                    }
                    if (element.equalsIgnoreCase("post_test2")) {
                        postTest2Tag = "open";
                    }
                    if (element.equalsIgnoreCase("post_test3")) {
                        postTest3Tag = "open";
                    }
                    if (element.equalsIgnoreCase("post_test4")) {
                        postTest4Tag = "open";
                    }
                    if (element.equalsIgnoreCase("post_test5")) {
                        postTest5Tag = "open";
                    }
                    if (element.equalsIgnoreCase("post_test6")) {
                        postTest6Tag = "open";
                    }
                    if (element.equalsIgnoreCase("post_test7")) {
                        postTest7Tag = "open";
                    }
                    if (element.equalsIgnoreCase("post_test8")) {
                        postTest8Tag = "open";
                    }
                    if (element.equalsIgnoreCase("post_test9")) {
                        postTest9Tag = "open";
                    }
                    if (element.equalsIgnoreCase("post_test10")) {
                        postTest10Tag = "open";
                    }
                    if (element.equalsIgnoreCase("post_test11")) {
                        postTest11Tag = "open";
                    }
                    if (element.equalsIgnoreCase("post_test12")) {
                        postTest12Tag = "open";
                    }
                    if (element.equalsIgnoreCase("post_test13")) {
                        postTest13Tag = "open";
                    }
                    if (element.equalsIgnoreCase("post_test14")) {
                        postTest14Tag = "open";
                    }
                    if (element.equalsIgnoreCase("syphilis_test_result")) {
                        syphilisTestResultTag = "open";
                    }
                    if (element.equalsIgnoreCase("hepatitisb_test_result")) {
                        hepatitisbTestResultTag = "open";
                    }
                    if (element.equalsIgnoreCase("hepatitisc_test_result")) {
                        hepatitiscTestResultTag = "open";
                    }
                    if (element.equalsIgnoreCase("sti_referred")) {
                        stiReferredTag = "open";
                    }
                    if (element.equalsIgnoreCase("tb_referred")) {
                        tbReferredTag = "open";
                    }
                    if (element.equalsIgnoreCase("art_referred")) {
                        artReferredTag = "open";
                    }
                    if (element.equalsIgnoreCase("partner_notification")) {
                        partnerNotificationTag = "open";
                    }
                    if (element.equalsIgnoreCase("notification_counseling")) {
                        notificationCounselingTag = "open";
                    }
                    if (element.equalsIgnoreCase("number_partner")) {
                        numberPartnerTag = "open";
                    }
                    if (element.equalsIgnoreCase("note")) {
                        noteTag = "open";
                    }
                    if (element.equalsIgnoreCase("longitude")) {
                        longitudeTag = "open";
                    }
                    if (element.equalsIgnoreCase("latitude")) {
                        latitudeTag = "open";
                    }
                    if (element.equalsIgnoreCase("deviceconfig_id")) {
                        deviceConfigIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("assessment_id")) {
                        assessmentIdTag = "open";
                    }
                }

                //store data store in between '<' and '>' tags                     
                @Override
                public void characters(char[] chars, int start, int length) throws SAXException {
                    String content = new String(chars, start, length);
                    if (facilityIdTag.equals("open")) {
                        facilityId = Long.parseLong(content);
                        hts.setFacilityId(facilityId);
                    }
                    if (hospitalNumTag.equals("open")) {
                        hts.setHospitalNum(content);
                    }
                    if (clientCodeTag.equals("open")) {
                        hts.setClientCode(content);
                    }
                    if (indexClientCodeTag.equals("open")) {
                        hts.setIndexClientCode(content);
                    }
                    if (indexClientTag.equals("open")) {
                        hts.setIndexClient(content);
                    }
                    if (indexTypeTag.equals("open")) {
                        hts.setTypeIndex(content);
                    }
                    if (dateVisitTag.equals("open")) {
                        if (!content.trim().isEmpty()) {
                            hts.setDateVisit(DateUtil.parseStringToDate(content, "yyyy-MM-dd"));
                        } else {
                            skipRecord = true;
                        }
                    }
                    if (dateBirthTag.equals("open")) {
                        if (!content.trim().isEmpty()) {
                            hts.setDateBirth(DateUtil.parseStringToDate(content, "yyyy-MM-dd"));
                        }
                    }
                    if (dateRegistrationTag.equals("open")) {
                        if (!content.trim().isEmpty()) {
                            hts.setDateRegistration(DateUtil.parseStringToDate(content, "yyyy-MM-dd"));
                        }
                    }
                    if (dateStartedTag.equals("open")) {
                        if (!content.trim().isEmpty()) {
                            hts.setDateStarted(DateUtil.parseStringToDate(content, "yyyy-MM-dd"));
                        }
                    }
                    if (idUUIDTag.equals("open")) {
                        hts.setUuid(new String(chars, start, length));
                    }
                    if (timeStampTag.equals("open")) {
                        hts.setTimeStamp(new java.sql.Date(new java.util.Date().getTime()));
                    }
                    if (stateIDTag.equals("open")) {
                        try {
                            hts.setStateId(Long.parseLong(content));
                        } catch (Exception e) {
                        }
                    }
                    if (stateTag.equals("open")) {
                        hts.setState(content);
                    }
                    if (lgaIDTag.equals("open")) {
                        try {
                            hts.setLgaId(Long.parseLong(content));
                        } catch (Exception e) {
                        }
                    }
                    if (lgaTag.equals("open")) {
                        hts.setLga(content);
                    }
                    if (facilityNameTag.equals("open")) {
                        hts.setFacilityName(content);
                    }
                    if (firstTimeVisitTag.equals("open")) {
                        hts.setFirstTimeVisit(content);
                    }
                    if (longitudeTag.equals("open")) {
                        try {
                            hts.setLongitude(Double.parseDouble(content));
                        } catch (Exception e) {
                        }
                    }
                    if (latitudeTag.equals("open")) {
                        try {
                            hts.setLatitude(Double.parseDouble(content));
                        } catch (Exception e) {
                        }
                    }
                    if (surnameTag.equals("open")) {
                        hts.setSurname(content);
                    }
                    if (otherNamesTag.equals("open")) {
                        hts.setOtherNames(content);
                    }
                    if (genderTag.equals("open")) {
                        hts.setGender(content);
                    }
                    if (ageTag.equals("open")) {
                        try {
                            hts.setAge(Integer.parseInt(content));
                        } catch (Exception e) {
                        }
                    }
                    if (ageUnitTag.equals("open")) {
                        hts.setAgeUnit(content);
                    }
                    if (phoneTag.equals("open")) {
                        hts.setPhone(content);
                    }
                    if (maritalStatusTag.equals("open")) {
                        hts.setMaritalStatus(content);
                    }
                    if (addressTag.equals("open")) {
                        hts.setAddress(content);
                    }
                    if (referredFromTag.equals("open")) {
                        hts.setReferredFrom(content);
                    }
                    if (testingSettingTag.equals("open")) {
                        hts.setTestingSetting(content);
                    }
                    if (hivTestResultTag.equals("open")) {
                        hts.setHivTestResult(content);
                    }
                    if (noteTag.equals("open")) {
                        hts.setNote(content);
                    }
                    if (testedHivTag.equals("open")) {
                        hts.setTestedHiv(content);
                    }
                    if (noteTag.equals("open")) {
                        hts.setNote(content);
                    }
                    if (numChildrenTag.equals("open")) {
                        try {
                            hts.setNumChildren(Integer.parseInt(content));
                        } catch (Exception e) {
                        }
                    }
                    if (numWivesTag.equals("open")) {
                        try {
                            hts.setNumWives(Integer.parseInt(content));
                        } catch (Exception e) {
                        }
                    }
                    if (numberPartnerTag.equals("open")) {
                        try {
                            hts.setNumberPartner(Integer.parseInt(content));
                        } catch (Exception e) {
                        }
                    }
                    if (partnerNotificationTag.equals("open")) {
                        hts.setPartnerNotification(content);
                    }
                    if (notificationCounselingTag.equals("open")) {
                        hts.setNotificationCounseling(content);
                    }
                    if (artReferredTag.equals("open")) {
                        hts.setArtReferred(content);
                    }
                    if (stiReferredTag.equals("open")) {
                        hts.setStiReferred(content);
                    }
                    if (tbReferredTag.equals("open")) {
                        hts.setTbReferred(content);
                    }
                    if (hepatitisbTestResultTag.equals("open")) {
                        hts.setHepatitisbTestResult(content);
                    }
                    if (hepatitiscTestResultTag.equals("open")) {
                        hts.setHepatitiscTestResult(content);
                    }
                    if (syphilisTestResultTag.equals("open")) {
                        hts.setSyphilisTestResult(content);
                    }
                    Integer intContent = 0;
                    try {
                        intContent = Integer.parseInt(content);
                    } catch (Exception e) {
                    }
                    if (knowledgeAssessment1Tag.equals("open")) {
                        hts.setKnowledgeAssessment1(intContent);
                    }
                    if (knowledgeAssessment2Tag.equals("open")) {
                        hts.setKnowledgeAssessment2(intContent);
                    }
                    if (knowledgeAssessment3Tag.equals("open")) {
                        hts.setKnowledgeAssessment3(intContent);
                    }
                    if (knowledgeAssessment4Tag.equals("open")) {
                        hts.setKnowledgeAssessment4(intContent);
                    }
                    if (knowledgeAssessment5Tag.equals("open")) {
                        hts.setKnowledgeAssessment5(intContent);
                    }
                    if (knowledgeAssessment6Tag.equals("open")) {
                        hts.setKnowledgeAssessment6(intContent);
                    }
                    if (knowledgeAssessment7Tag.equals("open")) {
                        hts.setKnowledgeAssessment7(intContent);
                    }
                    if (riskAssessment1Tag.equals("open")) {
                        hts.setRiskAssessment1(intContent);
                    }
                    if (riskAssessment6Tag.equals("open")) {
                        hts.setRiskAssessment6(intContent);
                    }
                    if (riskAssessment2Tag.equals("open")) {
                        hts.setRiskAssessment2(intContent);
                    }
                    if (riskAssessment3Tag.equals("open")) {
                        hts.setRiskAssessment3(intContent);
                    }
                    if (riskAssessment4Tag.equals("open")) {
                        hts.setRiskAssessment4(intContent);
                    }
                    if (riskAssessment5Tag.equals("open")) {
                        hts.setRiskAssessment5(intContent);
                    }
                    if (tbScreening1Tag.equals("open")) {
                        hts.setTbScreening1(intContent);
                    }
                    if (tbScreening2Tag.equals("open")) {
                        hts.setTbScreening2(intContent);
                    }
                    if (tbScreening3Tag.equals("open")) {
                        hts.setTbScreening3(intContent);
                    }
                    if (tbScreening4Tag.equals("open")) {
                        hts.setTbScreening4(intContent);
                    }
                    if (stiScreening1Tag.equals("open")) {
                        hts.setStiScreening1(intContent);
                    }
                    if (stiScreening2Tag.equals("open")) {
                        hts.setStiScreening2(intContent);
                    }
                    if (stiScreening3Tag.equals("open")) {
                        hts.setStiScreening3(intContent);
                    }
                    if (stiScreening4Tag.equals("open")) {
                        hts.setStiScreening4(intContent);
                    }
                    if (stiScreening5Tag.equals("open")) {
                        hts.setStiScreening5(intContent);
                    }
                    if (postTest1Tag.equals("open")) {
                        hts.setPostTest1(intContent);
                    }
                    if (postTest2Tag.equals("open")) {
                        hts.setPostTest2(intContent);
                    }
                    if (postTest3Tag.equals("open")) {
                        hts.setPostTest3(intContent);
                    }
                    if (postTest4Tag.equals("open")) {
                        hts.setPostTest4(intContent);
                    }
                    if (postTest5Tag.equals("open")) {
                        hts.setPostTest5(intContent);
                    }
                    if (postTest6Tag.equals("open")) {
                        hts.setPostTest6(intContent);
                    }
                    if (postTest7Tag.equals("open")) {
                        hts.setPostTest7(intContent);
                    }
                    if (postTest8Tag.equals("open")) {
                        hts.setPostTest8(intContent);
                    }
                    if (postTest9Tag.equals("open")) {
                        hts.setPostTest9(intContent);
                    }
                    if (postTest10Tag.equals("open")) {
                        hts.setPostTest10(intContent);
                    }
                    if (postTest11Tag.equals("open")) {
                        hts.setPostTest11(intContent);
                    }
                    if (postTest12Tag.equals("open")) {
                        hts.setPostTest12(intContent);
                    }
                    if (postTest13Tag.equals("open")) {
                        hts.setPostTest13(intContent);
                    }
                    if (postTest14Tag.equals("open")) {
                        hts.setPostTest14(intContent);
                    }
                }

                @Override
                public void endElement(String uri, String localName, String element) throws SAXException {
                    if (element.equalsIgnoreCase("history_id")) {
                        historyIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("hospital_num")) {
                        hospitalNumTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_visit")) {
                        dateVisitTag = "close";
                    }
                    if (element.equalsIgnoreCase("time_stamp")) {
                        timeStampTag = "close";
                    }
                    if (element.equalsIgnoreCase("id_uuid") || element.equalsIgnoreCase("uuid")) {
                        idUUIDTag = "close";
                    }
                    if (element.equalsIgnoreCase("state_id")) {
                        stateIDTag = "close";
                    }
                    if (element.equalsIgnoreCase("lga_id")) {
                        lgaIDTag = "close";
                    }
                    if (element.equalsIgnoreCase("state")) {
                        stateTag = "close";
                    }
                    if (element.equalsIgnoreCase("lga")) {
                        lgaTag = "close";
                    }
                    if (element.equalsIgnoreCase("facility_name")) {
                        facilityNameTag = "close";
                    }
                    if (element.equalsIgnoreCase("surname")) {
                        surnameTag = "close";
                    }
                    if (element.equalsIgnoreCase("other_names")) {
                        otherNamesTag = "close";
                    }
                    if (element.equalsIgnoreCase("testing_setting")) {
                        testingSettingTag = "close";
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
                    if (element.equalsIgnoreCase("date_registration")) {
                        dateRegistrationTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_started")) {
                        dateStartedTag = "close";
                    }
                    if (element.equalsIgnoreCase("referred_from")) {
                        referredFromTag = "close";
                    }
                    if (element.equalsIgnoreCase("client_code")) {
                        clientCodeTag = "close";
                    }
                    if (element.equalsIgnoreCase("deviceconfig_id")) {
                        deviceConfigIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("phone")) {
                        phoneTag = "close";
                    }
                    if (element.equalsIgnoreCase("address")) {
                        addressTag = "close";
                    }
                    if (element.equalsIgnoreCase("gender")) {
                        genderTag = "close";
                    }
                    if (element.equalsIgnoreCase("first_time_visit")) {
                        firstTimeVisitTag = "close";
                    }
                    if (element.equalsIgnoreCase("marital_status")) {
                        maritalStatusTag = "close";
                    }
                    if (element.equalsIgnoreCase("num_children")) {
                        numChildrenTag = "close";
                    }
                    if (element.equalsIgnoreCase("num_wives")) {
                        numWivesTag = "close";
                    }
                    if (element.equalsIgnoreCase("type_counseling")) {
                        typeCounselingTag = "close";
                    }
                    if (element.equalsIgnoreCase("index_client")) {
                        indexClientTag = "close";
                    }
                    if (element.equalsIgnoreCase("type_index")) {
                        indexTypeTag = "close";
                    }
                    if (element.equalsIgnoreCase("index_client_code")) {
                        indexClientCodeTag = "close";
                    }
                    if (element.equalsIgnoreCase("knowledge_assessment1")) {
                        knowledgeAssessment1Tag = "close";
                    }
                    if (element.equalsIgnoreCase("knowledge_assessment2")) {
                        knowledgeAssessment2Tag = "close";
                    }
                    if (element.equalsIgnoreCase("knowledge_assessment3")) {
                        knowledgeAssessment3Tag = "close";
                    }
                    if (element.equalsIgnoreCase("knowledge_assessment4")) {
                        knowledgeAssessment4Tag = "close";
                    }
                    if (element.equalsIgnoreCase("knowledge_assessment5")) {
                        knowledgeAssessment5Tag = "close";
                    }
                    if (element.equalsIgnoreCase("knowledge_assessment6")) {
                        knowledgeAssessment6Tag = "close";
                    }
                    if (element.equalsIgnoreCase("knowledge_assessment7")) {
                        knowledgeAssessment7Tag = "close";
                    }
                    if (element.equalsIgnoreCase("risk_assessment1")) {
                        riskAssessment1Tag = "close";
                    }
                    if (element.equalsIgnoreCase("risk_assessment1")) {
                        riskAssessment1Tag = "close";
                    }
                    if (element.equalsIgnoreCase("risk_assessment2")) {
                        riskAssessment2Tag = "close";
                    }
                    if (element.equalsIgnoreCase("risk_assessment3")) {
                        riskAssessment3Tag = "close";
                    }
                    if (element.equalsIgnoreCase("risk_assessment4")) {
                        riskAssessment4Tag = "close";
                    }
                    if (element.equalsIgnoreCase("risk_assessment5")) {
                        riskAssessment5Tag = "close";
                    }
                    if (element.equalsIgnoreCase("risk_assessment6")) {
                        riskAssessment6Tag = "close";
                    }
                    if (element.equalsIgnoreCase("tb_screening1")) {
                        tbScreening1Tag = "close";
                    }
                    if (element.equalsIgnoreCase("tb_screening2")) {
                        tbScreening2Tag = "close";
                    }
                    if (element.equalsIgnoreCase("tb_screening3")) {
                        tbScreening3Tag = "close";
                    }
                    if (element.equalsIgnoreCase("tb_screening4")) {
                        tbScreening4Tag = "close";
                    }
                    if (element.equalsIgnoreCase("sti_screening1")) {
                        stiScreening1Tag = "close";
                    }
                    if (element.equalsIgnoreCase("sti_screening2")) {
                        stiScreening2Tag = "close";
                    }
                    if (element.equalsIgnoreCase("sti_screening3")) {
                        stiScreening3Tag = "close";
                    }
                    if (element.equalsIgnoreCase("sti_screening4")) {
                        stiScreening4Tag = "close";
                    }
                    if (element.equalsIgnoreCase("sti_screening5")) {
                        stiScreening5Tag = "close";
                    }
                    if (element.equalsIgnoreCase("hiv_test_result")) {
                        hivTestResultTag = "close";
                    }
                    if (element.equalsIgnoreCase("tested_hiv")) {
                        testedHivTag = "close";
                    }
                    if (element.equalsIgnoreCase("post_test1")) {
                        postTest1Tag = "close";
                    }
                    if (element.equalsIgnoreCase("post_test2")) {
                        postTest2Tag = "close";
                    }
                    if (element.equalsIgnoreCase("post_test3")) {
                        postTest3Tag = "close";
                    }
                    if (element.equalsIgnoreCase("post_test4")) {
                        postTest4Tag = "close";
                    }
                    if (element.equalsIgnoreCase("post_test5")) {
                        postTest5Tag = "close";
                    }
                    if (element.equalsIgnoreCase("post_test6")) {
                        postTest6Tag = "close";
                    }
                    if (element.equalsIgnoreCase("post_test7")) {
                        postTest7Tag = "close";
                    }
                    if (element.equalsIgnoreCase("post_test8")) {
                        postTest8Tag = "close";
                    }
                    if (element.equalsIgnoreCase("post_test9")) {
                        postTest9Tag = "close";
                    }
                    if (element.equalsIgnoreCase("post_test10")) {
                        postTest10Tag = "close";
                    }
                    if (element.equalsIgnoreCase("post_test11")) {
                        postTest11Tag = "close";
                    }
                    if (element.equalsIgnoreCase("post_test12")) {
                        postTest12Tag = "close";
                    }
                    if (element.equalsIgnoreCase("post_test13")) {
                        postTest13Tag = "close";
                    }
                    if (element.equalsIgnoreCase("post_test14")) {
                        postTest14Tag = "close";
                    }
                    if (element.equalsIgnoreCase("syphilis_test_result")) {
                        syphilisTestResultTag = "close";
                    }
                    if (element.equalsIgnoreCase("hepatitisb_test_result")) {
                        hepatitisbTestResultTag = "close";
                    }
                    if (element.equalsIgnoreCase("hepatitisc_test_result")) {
                        hepatitiscTestResultTag = "close";
                    }
                    if (element.equalsIgnoreCase("sti_referred")) {
                        stiReferredTag = "close";
                    }
                    if (element.equalsIgnoreCase("tb_referred")) {
                        tbReferredTag = "close";
                    }
                    if (element.equalsIgnoreCase("art_referred")) {
                        artReferredTag = "close";
                    }
                    if (element.equalsIgnoreCase("partner_notification")) {
                        partnerNotificationTag = "close";
                    }
                    if (element.equalsIgnoreCase("notification_counseling")) {
                        notificationCounselingTag = "close";
                    }
                    if (element.equalsIgnoreCase("number_partner")) {
                        numberPartnerTag = "close";
                    }
                    if (element.equalsIgnoreCase("note")) {
                        noteTag = "close";
                    }
                    if (element.equalsIgnoreCase("longitude")) {
                        longitudeTag = "close";
                    }
                    if (element.equalsIgnoreCase("latitude")) {
                        latitudeTag = "close";
                    }
                    if (element.equalsIgnoreCase("deviceconfig_id")) {
                        deviceConfigIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("assessment_id")) {
                        assessmentIdTag = "close";
                    }

                    //if this is the closing tag of a adherehistory element save the record
                    if (element.equalsIgnoreCase("hts")) {
                        htsTag = "close";
                        if (!skipRecord) {

                            long htsId = new HtsJDBC().getHtsId(hts.getClientCode(), hts.getFacilityId());
                            if (htsId == 0L) {
                                HtsDAO.save(hts);
                            } else {
                                hts.setHtsId(htsId);
                                HtsDAO.update(hts);
                            }
                        }
                    }
                }
            };

            //parse the XML specified in the given path and uses supplied handler to parse the document
            //this calls startElement(), endElement(), and character() methods accordingly
            saxParser.parse(xmlFileName, defaultHandler);
            //new CleanupService().cleanNullFields("adherehistory", facilityId);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }

}
