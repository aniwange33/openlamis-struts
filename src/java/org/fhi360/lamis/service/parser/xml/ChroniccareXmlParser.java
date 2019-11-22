/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.service.parser.xml;

import org.fhi360.lamis.model.Patient;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.fhi360.lamis.dao.hibernate.ChroniccareDAO;
import org.fhi360.lamis.model.Chroniccare;
import org.fhi360.lamis.service.ServerIDProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ChroniccareXmlParser extends DefaultHandler {

    private long facilityId;
    private String hospitalNum;
    private String dateVisit;
    private Boolean skipRecord;
    private Chroniccare chroniccare;

    public ChroniccareXmlParser() {
    }

    public void parseXml(String xmlFileName) {
        skipRecord = false;
        try {
            //obtain and configure a SAX based parser
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

            //obtain object for SAX hadler class
            SAXParser saxParser = saxParserFactory.newSAXParser();

            //default handler for SAX handler class
            // all three methods are written in handler's body
            DefaultHandler defaultHandler = new DefaultHandler() {
                String chroniccareTag = "close";
                String chroniccareIdTag = "close";
                String facilityIdTag = "close";
                String hospitalNumTag = "close";
                String dateVisitTag = "close";
                String clientTypeTag = "close";
                String currentStatusTag = "close";
                String pregnancyStatusTag = "close";
                String clinicStageTag = "close";
                String lastCd4Tag = "close";
                String dateLastCd4Tag = "close";
                String lastViralLoadTag = "close";
                String dateLastViralLoadTag = "close";
                String eligibleCd4Tag = "close";
                String eligibleViralLoadTag = "close";
                String cotrimEligibility1Tag = "close";
                String cotrimEligibility2Tag = "close";
                String cotrimEligibility3Tag = "close";
                String cotrimEligibility4Tag = "close";
                String cotrimEligibility5Tag = "close";
                String iptTag = "close";
                String inhTag = "close";
                String tbTreatmentTag = "close";
                String dateStartedTbTreatmentTag = "close";
                String tbReferredTag = "close";
                String eligibleIptTag = "close";
                String tbValuesTag = "close";
                String bodyWeightTag = "close";
                String heightTag = "close";
                String bmiTag = "close";
                String muacTag = "close";
                String muacPediatricsTag = "close";
                String muacPregnantTag = "close";
                String supplementaryFoodTag = "close";
                String nutritionalStatusReferredTag = "close";
                String gbv1Tag = "close";
                String gbv1ReferredTag = "close";
                String gbv2Tag = "close";
                String gbv2ReferredTag = "close";
                String hypertensiveTag = "close";
                String firstHypertensiveTag = "close";
                String bpAboveTag = "close";
                String bpReferredTag = "close";
                String diabeticTag = "close";
                String firstDiabeticTag = "close";
                String dmReferredTag = "close";
                String dmValuesTag = "close";
                String phdp1Tag = "close";
                String phdp1ServicesProvidedTag = "close";
                String phdp2Tag = "close";
                String phdp2ServicesProvidedTag = "close";
                String phdp3Tag = "close";
                String phdp3ServicesProvidedTag = "close";
                String phdp4Tag = "close";
                String phdp4ServicesProvidedTag = "close";
                String phdp5Tag = "close";
                String phdp5ServicesProvidedTag = "close";
                String phdp6Tag = "close";
                String phdp6ServicesProvidedTag = "close";
                String phdp7Tag = "close";
                String phdp7ServicesProvidedTag = "close";
                String phdp8Tag = "close";
                String phdp8ServicesProvidedTag = "close";
                String phdp9ServicesProvidedTag = "close";
                String reproductiveIntentions1Tag = "close";
                String reproductiveIntentions1ReferredTag = "close";
                String reproductiveIntentions2Tag = "close";
                String reproductiveIntentions2ReferredTag = "close";
                String reproductiveIntentions3Tag = "close";
                String reproductiveIntentions3ReferredTag = "close";
                String malariaPrevention1Tag = "close";
                String malariaPrevention1ReferredTag = "close";
                String malariaPrevention2Tag = "close";
                String malariaPrevention2ReferredTag = "close";
                String userIdTag = "close";
                String timeStampTag = "close";
                String idUUIDTag = "close";

                //this method is called every time the parser gets an open tag '<'
                //identifies which tag is being open at the time by assigning an open flag
                @Override
                public void startElement(String uri, String localName, String element, Attributes attributes) throws SAXException {
                    if (element.equalsIgnoreCase("chroniccare")) {
                        chroniccareTag = "open";
                        chroniccare = new Chroniccare();
                    }
                    if (element.equalsIgnoreCase("chroniccare_id")) {
                        chroniccareIdTag = "open";
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
                    if (element.equalsIgnoreCase("client_type")) {
                        clientTypeTag = "open";
                    }
                    if (element.equalsIgnoreCase("current_status")) {
                        currentStatusTag = "open";
                    }
                    if (element.equalsIgnoreCase("pregnancy_status")) {
                        pregnancyStatusTag = "open";
                    }
                    if (element.equalsIgnoreCase("clinic_stage")) {
                        clinicStageTag = "open";
                    }
                    if (element.equalsIgnoreCase("last_cd4")) {
                        lastCd4Tag = "open";
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
                    if (element.equalsIgnoreCase("eligible_cd4")) {
                        eligibleCd4Tag = "open";
                    }
                    if (element.equalsIgnoreCase("eligible_viral_load")) {
                        eligibleViralLoadTag = "open";
                    }
                    if (element.equalsIgnoreCase("cotrim_eligibility1")) {
                        cotrimEligibility1Tag = "open";
                    }
                    if (element.equalsIgnoreCase("cotrim_eligibility2")) {
                        cotrimEligibility2Tag = "open";
                    }
                    if (element.equalsIgnoreCase("cotrim_eligibility3")) {
                        cotrimEligibility3Tag = "open";
                    }
                    if (element.equalsIgnoreCase("cotrim_eligibility4")) {
                        cotrimEligibility4Tag = "open";
                    }
                    if (element.equalsIgnoreCase("cotrim_eligibility5")) {
                        cotrimEligibility5Tag = "open";
                    }
                    if (element.equalsIgnoreCase("ipt")) {
                        iptTag = "open";
                    }
                    if (element.equalsIgnoreCase("inh")) {
                        inhTag = "open";
                    }
                    if (element.equalsIgnoreCase("tb_treatment")) {
                        tbTreatmentTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_started_tb_treatment")) {
                        dateStartedTbTreatmentTag = "open";
                    }
                    if (element.equalsIgnoreCase("tb_referred")) {
                        tbReferredTag = "open";
                    }
                    if (element.equalsIgnoreCase("eligible_ipt")) {
                        eligibleIptTag = "open";
                    }
                    if (element.equalsIgnoreCase("tb_values")) {
                        tbValuesTag = "open";
                    }
                    if (element.equalsIgnoreCase("body_weight")) {
                        bodyWeightTag = "open";
                    }
                    if (element.equalsIgnoreCase("height")) {
                        heightTag = "open";
                    }
                    if (element.equalsIgnoreCase("bmi")) {
                        bmiTag = "open";
                    }
                    if (element.equalsIgnoreCase("muac")) {
                        muacTag = "open";
                    }
                    if (element.equalsIgnoreCase("muacPediatrics")) {
                        muacPediatricsTag = "open";
                    }
                    if (element.equalsIgnoreCase("muac_pregnant")) {
                        muacPregnantTag = "open";
                    }
                    if (element.equalsIgnoreCase("supplementary_food")) {
                        supplementaryFoodTag = "open";
                    }
                    if (element.equalsIgnoreCase("nutritional_status_referred")) {
                        nutritionalStatusReferredTag = "open";
                    }
                    if (element.equalsIgnoreCase("gbv1")) {
                        gbv1Tag = "open";
                    }
                    if (element.equalsIgnoreCase("gbv1_referred")) {
                        gbv1ReferredTag = "open";
                    }
                    if (element.equalsIgnoreCase("gbv2")) {
                        gbv2Tag = "open";
                    }
                    if (element.equalsIgnoreCase("gbv2_referred")) {
                        gbv2ReferredTag = "open";
                    }
                    if (element.equalsIgnoreCase("hypertensive")) {
                        hypertensiveTag = "open";
                    }
                    if (element.equalsIgnoreCase("first_hypertensive")) {
                        firstHypertensiveTag = "open";
                    }
                    if (element.equalsIgnoreCase("bp_above")) {
                        bpAboveTag = "open";
                    }
                    if (element.equalsIgnoreCase("bp_referred")) {
                        bpReferredTag = "open";
                    }
                    if (element.equalsIgnoreCase("diabetic")) {
                        diabeticTag = "open";
                    }
                    if (element.equalsIgnoreCase("bp_referred")) {
                        bpReferredTag = "open";
                    }
                    if (element.equalsIgnoreCase("first_diabetic")) {
                        firstDiabeticTag = "open";
                    }
                    if (element.equalsIgnoreCase("dm_referred")) {
                        dmReferredTag = "open";
                    }
                    if (element.equalsIgnoreCase("dm_values")) {
                        dmValuesTag = "open";
                    }
                    if (element.equalsIgnoreCase("phdp1")) {
                        phdp1Tag = "open";
                    }
                    if (element.equalsIgnoreCase("phdp1_services_provided")) {
                        phdp1ServicesProvidedTag = "open";
                    }
                    if (element.equalsIgnoreCase("phdp2")) {
                        phdp2Tag = "open";
                    }
                    if (element.equalsIgnoreCase("phdp2_services_provided")) {
                        phdp2ServicesProvidedTag = "open";
                    }
                    if (element.equalsIgnoreCase("phdp3")) {
                        phdp3Tag = "open";
                    }
                    if (element.equalsIgnoreCase("phdp3_services_provided")) {
                        phdp3ServicesProvidedTag = "open";
                    }
                    if (element.equalsIgnoreCase("phdp4")) {
                        phdp4Tag = "open";
                    }
                    if (element.equalsIgnoreCase("phdp5_services_provided")) {
                        phdp5ServicesProvidedTag = "open";
                    }
                    if (element.equalsIgnoreCase("phdp6")) {
                        phdp6Tag = "open";
                    }
                    if (element.equalsIgnoreCase("phdp6_services_provided")) {
                        phdp6ServicesProvidedTag = "open";
                    }
                    if (element.equalsIgnoreCase("phdp7")) {
                        phdp7Tag = "open";
                    }
                    if (element.equalsIgnoreCase("phdp7_services_provided")) {
                        phdp7ServicesProvidedTag = "open";
                    }
                    if (element.equalsIgnoreCase("phdp8")) {
                        phdp8Tag = "open";
                    }
                    if (element.equalsIgnoreCase("phdp8_services_provided")) {
                        phdp8ServicesProvidedTag = "open";
                    }
                    if (element.equalsIgnoreCase("phdp9_services_provided")) {
                        phdp9ServicesProvidedTag = "open";
                    }

                    if (element.equalsIgnoreCase("reproductive_intentions1")) {
                        reproductiveIntentions1Tag = "open";
                    }
                    if (element.equalsIgnoreCase("reproductive_intentions1_referred")) {
                        reproductiveIntentions1ReferredTag = "open";
                    }

                    if (element.equalsIgnoreCase("reproductive_intentions2")) {
                        reproductiveIntentions2Tag = "open";
                    }
                    if (element.equalsIgnoreCase("reproductive_intentions2_referred")) {
                        reproductiveIntentions2ReferredTag = "open";
                    }
                    if (element.equalsIgnoreCase("reproductive_intentions3")) {
                        reproductiveIntentions3Tag = "open";
                    }
                    if (element.equalsIgnoreCase("reproductive_intentions3_referred")) {
                        reproductiveIntentions3ReferredTag = "open";
                    }
                    if (element.equalsIgnoreCase(" malaria_prevention1")) {
                        malariaPrevention1Tag = "open";
                    }
                    if (element.equalsIgnoreCase("malaria_prevention1_referred")) {
                        malariaPrevention1ReferredTag = "open";
                    }
                    if (element.equalsIgnoreCase(" malaria_prevention2")) {
                        malariaPrevention2Tag = "open";
                    }
                    if (element.equalsIgnoreCase("malaria_prevention2_referred")) {
                        malariaPrevention2ReferredTag = "open";
                    }
                    if (element.equalsIgnoreCase("time_stamp")) {
                        timeStampTag = "open";
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
                    if (facilityIdTag.equals("open")) {
                        facilityId = Long.parseLong(new String(chars, start, length));
                        chroniccare.setFacilityId(facilityId);
                    }
                    if (hospitalNumTag.equals("open")) {
                        hospitalNum = new String(chars, start, length);
                    }
                    if (dateVisitTag.equals("open")) {
                        dateVisit = new String(chars, start, length);
                        if (!dateVisit.trim().isEmpty()) {
                            chroniccare.setDateVisit(DateUtil.parseStringToDate(dateVisit, "yyyy-MM-dd"));
                        } else {
                            skipRecord = true;
                        }

                    }
                    if (clientTypeTag.equals("open")) {
                        chroniccare.setClientType(new String(chars, start, length));
                    }
                    if (currentStatusTag.equals("open")) {
                        chroniccare.setCurrentStatus(new String(chars, start, length));
                    }
                    if (pregnancyStatusTag.equals("open")) {
                        chroniccare.setPregnancyStatus(new String(chars, start, length));
                    }
                    if (clinicStageTag.equals("open")) {
                        chroniccare.setClinicStage(new String(chars, start, length));
                    }
                    if (lastCd4Tag.equals("open")) {
                        String lastCd4 = new String(chars, start, length);
                        if (!lastCd4.trim().isEmpty()) {
                            chroniccare.setLastCd4(Double.parseDouble(lastCd4));
                        }
                    }
                    if (dateLastCd4Tag.equals("open")) {
                        String dateLastCd4 = new String(chars, start, length);
                        if (!dateLastCd4.trim().isEmpty()) {
                            chroniccare.setDateLastCd4(DateUtil.parseStringToDate(dateLastCd4, "yyyy-MM-dd"));
                        }
                    }
                    if (lastViralLoadTag.equals("open")) {
                        String lastViralLoad = new String(chars, start, length);
                        if (!lastViralLoad.trim().isEmpty()) {
                            chroniccare.setLastViralLoad(Double.parseDouble(lastViralLoad));
                        }
                    }
                    if (dateLastViralLoadTag.equals("open")) {
                        String dateLastViralLoad = new String(chars, start, length);
                        if (!dateLastViralLoad.trim().isEmpty()) {
                            chroniccare.setDateLastViralLoad(DateUtil.parseStringToDate(dateLastViralLoad, "yyyy-MM-dd"));
                        }
                    }
                    if (eligibleCd4Tag.equals("open")) {
                        chroniccare.setEligibleCd4(new String(chars, start, length));
                    }
                    if (eligibleViralLoadTag.equals("open")) {
                        chroniccare.setEligibleViralLoad(new String(chars, start, length));
                    }
                    if (cotrimEligibility1Tag.equals("open")) {
                        String cotrimEligibility1 = new String(chars, start, length);
                        if (!cotrimEligibility1.trim().isEmpty()) {
                            chroniccare.setCotrimEligibility1(Integer.parseInt(cotrimEligibility1));
                        }
                    }
                    if (cotrimEligibility2Tag.equals("open")) {
                        String cotrimEligibility2 = new String(chars, start, length);
                        if (!cotrimEligibility2.trim().isEmpty()) {
                            chroniccare.setCotrimEligibility2(Integer.parseInt(cotrimEligibility2));
                        }
                    }
                    if (cotrimEligibility3Tag.equals("open")) {
                        String cotrimEligibility3 = new String(chars, start, length);
                        if (!cotrimEligibility3.trim().isEmpty()) {
                            chroniccare.setCotrimEligibility3(Integer.parseInt(cotrimEligibility3));
                        }
                    }
                    if (cotrimEligibility4Tag.equals("open")) {
                        String cotrimEligibility4 = new String(chars, start, length);
                        if (!cotrimEligibility4.trim().isEmpty()) {
                            chroniccare.setCotrimEligibility4(Integer.parseInt(cotrimEligibility4));
                        }
                    }
                    if (cotrimEligibility5Tag.equals("open")) {
                        String cotrimEligibility5 = new String(chars, start, length);
                        if (!cotrimEligibility5.trim().isEmpty()) {
                            chroniccare.setCotrimEligibility5(Integer.parseInt(cotrimEligibility5));
                        }
                    }
                    if (iptTag.equals("open")) {
                        chroniccare.setIpt(new String(chars, start, length));
                    }
                    if (inhTag.equals("open")) {
                        chroniccare.setInh(new String(chars, start, length));
                    }
                    if (tbTreatmentTag.equals("open")) {
                        chroniccare.setTbTreatment(new String(chars, start, length));
                    }
                    if (tbReferredTag.equals("open")) {
                        chroniccare.setTbReferred(new String(chars, start, length));
                    }
                    if (dateStartedTbTreatmentTag.equals("open")) {
                        String dateStartedTbTreatment = new String(chars, start, length);
                        if (!dateStartedTbTreatment.trim().isEmpty()) {
                            chroniccare.setDateStartedTbTreatment(DateUtil.parseStringToDate(dateStartedTbTreatment, "yyyy-MM-dd"));
                        }
                    }
                    if (eligibleIptTag.equals("open")) {
                        chroniccare.setEligibleIpt(new String(chars, start, length));
                    }
                    if (tbValuesTag.equals("open")) {
                        chroniccare.setTbValues(new String(chars, start, length));
                    }
                    if (bodyWeightTag.equals("open")) {
                        String bodyWeight = new String(chars, start, length);
                        if (!bodyWeight.trim().isEmpty()) {
                            chroniccare.setBodyWeight(Double.parseDouble(bodyWeight));
                        }
                    }
                    if (heightTag.equals("open")) {
                        String height = new String(chars, start, length);
                        if (!height.trim().isEmpty()) {
                            chroniccare.setHeight(Double.parseDouble(height));
                        }
                    }
                    if (bmiTag.equals("open")) {
                        chroniccare.setBmi(new String(chars, start, length));
                    }
                    if (muacTag.equals("open")) {
                        String muac = new String(chars, start, length);
                        if (!muac.trim().isEmpty()) {
                            chroniccare.setHeight(Double.parseDouble(muac));
                        }
                    }
                    if (muacPediatricsTag.equals("open")) {
                        chroniccare.setMuacPediatrics(new String(chars, start, length));
                    }
                    if (muacPregnantTag.equals("open")) {
                        chroniccare.setMuacPregnant(new String(chars, start, length));
                    }
                    if (phdp1Tag.equals("open")) {
                        chroniccare.setPhdp1(new String(chars, start, length));
                    }
                    if (phdp1ServicesProvidedTag.equals("open")) {
                        chroniccare.setPhdp1ServicesProvided(new String(chars, start, length));
                    }
                    if (phdp2Tag.equals("open")) {
                        chroniccare.setPhdp2(new String(chars, start, length));
                    }
                    if (phdp2ServicesProvidedTag.equals("open")) {
                        chroniccare.setPhdp2ServicesProvided(new String(chars, start, length));
                    }
                    if (phdp3Tag.equals("open")) {
                        chroniccare.setPhdp3(new String(chars, start, length));
                    }
                    if (phdp3ServicesProvidedTag.equals("open")) {
                        chroniccare.setPhdp3ServicesProvided(new String(chars, start, length));
                    }
                    if (phdp4Tag.equals("open")) {
                        chroniccare.setPhdp4(new String(chars, start, length));
                    }
                    if (phdp4ServicesProvidedTag.equals("open")) {
                        chroniccare.setPhdp4ServicesProvided(new String(chars, start, length));
                    }
                    if (phdp5Tag.equals("open")) {
                        chroniccare.setPhdp5(new String(chars, start, length));
                    }
                    if (phdp5ServicesProvidedTag.equals("open")) {
                        chroniccare.setPhdp5ServicesProvided(new String(chars, start, length));
                    }
                    if (phdp6Tag.equals("open")) {
                        String phdp6 = new String(chars, start, length);
                        if (!phdp6.trim().isEmpty()) {
                            chroniccare.setPhdp6(Integer.parseInt(phdp6));
                        }
                    }
                    if (phdp6ServicesProvidedTag.equals("open")) {
                        chroniccare.setPhdp6ServicesProvided(new String(chars, start, length));
                    }
                    if (phdp7Tag.equals("open")) {
                        String phdp7 = new String(chars, start, length);
                        if (!phdp7.trim().isEmpty()) {
                            chroniccare.setPhdp7(Integer.parseInt(phdp7));
                        }
                    }
                    if (phdp7ServicesProvidedTag.equals("open")) {
                        chroniccare.setPhdp7ServicesProvided(new String(chars, start, length));
                    }
                    if (phdp8Tag.equals("open")) {
                        chroniccare.setPhdp8(new String(chars, start, length));
                    }
                    if (phdp8ServicesProvidedTag.equals("open")) {
                        chroniccare.setPhdp8ServicesProvided(new String(chars, start, length));
                    }
                    if (phdp9ServicesProvidedTag.equals("open")) {
                        chroniccare.setPhdp9ServicesProvided(new String(chars, start, length));
                    }
                    if (reproductiveIntentions1Tag.equals("open")) {
                        chroniccare.setReproductiveIntentions1(new String(chars, start, length));
                    }
                    if (reproductiveIntentions1ReferredTag.equals("open")) {
                        chroniccare.setReproductiveIntentions1Referred(new String(chars, start, length));
                    }
                    if (reproductiveIntentions2Tag.equals("open")) {
                        chroniccare.setReproductiveIntentions2(new String(chars, start, length));
                    }
                    if (reproductiveIntentions2ReferredTag.equals("open")) {
                        chroniccare.setReproductiveIntentions2Referred(new String(chars, start, length));
                    }
                    if (reproductiveIntentions3Tag.equals("open")) {
                        chroniccare.setReproductiveIntentions3(new String(chars, start, length));
                    }
                    if (reproductiveIntentions3ReferredTag.equals("open")) {
                        chroniccare.setReproductiveIntentions3Referred(new String(chars, start, length));
                    }
                    if (malariaPrevention1Tag.equals("open")) {
                        chroniccare.setMalariaPrevention1(new String(chars, start, length));
                    }
                    if (malariaPrevention1ReferredTag.equals("open")) {
                        chroniccare.setMalariaPrevention1Referred(new String(chars, start, length));
                    }
                    if (malariaPrevention2Tag.equals("open")) {
                        chroniccare.setMalariaPrevention2(new String(chars, start, length));
                    }
                    if (malariaPrevention2ReferredTag.equals("open")) {
                        chroniccare.setMalariaPrevention2Referred(new String(chars, start, length));
                    }
                    if (timeStampTag.equals("open")) {
                        chroniccare.setTimeStamp(new java.sql.Date(new java.util.Date().getTime()));
                    }
                    if (userIdTag.equals("open")) {
                        String userId = new String(chars, start, length);
                        if (!userId.trim().trim().isEmpty()) {
                            chroniccare.setUserId(Long.parseLong(userId));
                        }
                    }
                    if (idUUIDTag.equals("open")) {
                        chroniccare.setUuid(new String(chars, start, length));
                    }
                }

                @Override
                public void endElement(String uri, String localName, String element) throws SAXException {
                    if (element.equalsIgnoreCase("chroniccare_id")) {
                        chroniccareIdTag = "close";
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
                    if (element.equalsIgnoreCase("client_type")) {
                        clientTypeTag = "close";
                    }
                    if (element.equalsIgnoreCase("current_status")) {
                        currentStatusTag = "close";
                    }
                    if (element.equalsIgnoreCase("pregnancy_status")) {
                        pregnancyStatusTag = "close";
                    }
                    if (element.equalsIgnoreCase("clinic_stage")) {
                        clinicStageTag = "close";
                    }
                    if (element.equalsIgnoreCase("last_cd4")) {
                        lastCd4Tag = "close";
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
                    if (element.equalsIgnoreCase("eligible_cd4")) {
                        eligibleCd4Tag = "close";
                    }
                    if (element.equalsIgnoreCase("eligible_viral_load")) {
                        eligibleViralLoadTag = "close";
                    }
                    if (element.equalsIgnoreCase("cotrim_eligibility1")) {
                        cotrimEligibility1Tag = "close";
                    }
                    if (element.equalsIgnoreCase("cotrim_eligibility2")) {
                        cotrimEligibility2Tag = "close";
                    }
                    if (element.equalsIgnoreCase("cotrim_eligibility3")) {
                        cotrimEligibility3Tag = "close";
                    }
                    if (element.equalsIgnoreCase("cotrim_eligibility4")) {
                        cotrimEligibility4Tag = "close";
                    }
                    if (element.equalsIgnoreCase("cotrim_eligibility5")) {
                        cotrimEligibility5Tag = "close";
                    }
                    if (element.equalsIgnoreCase("ipt")) {
                        iptTag = "close";
                    }
                    if (element.equalsIgnoreCase("inh")) {
                        inhTag = "close";
                    }
                    if (element.equalsIgnoreCase("tb_treatment")) {
                        tbTreatmentTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_started_tb_treatment")) {
                        dateStartedTbTreatmentTag = "close";
                    }
                    if (element.equalsIgnoreCase("tb_referred")) {
                        tbReferredTag = "close";
                    }
                    if (element.equalsIgnoreCase("eligible_ipt")) {
                        eligibleIptTag = "close";
                    }
                    if (element.equalsIgnoreCase("tb_values")) {
                        tbValuesTag = "close";
                    }
                    if (element.equalsIgnoreCase("body_weight")) {
                        bodyWeightTag = "close";
                    }
                    if (element.equalsIgnoreCase("height")) {
                        heightTag = "close";
                    }
                    if (element.equalsIgnoreCase("bmi")) {
                        bmiTag = "close";
                    }
                    if (element.equalsIgnoreCase("muac")) {
                        muacTag = "close";
                    }
                    if (element.equalsIgnoreCase("muacPediatrics")) {
                        muacPediatricsTag = "close";
                    }
                    if (element.equalsIgnoreCase("muac_pregnant")) {
                        muacPregnantTag = "close";
                    }
                    if (element.equalsIgnoreCase("supplementary_food")) {
                        supplementaryFoodTag = "close";
                    }
                    if (element.equalsIgnoreCase("nutritional_status_referred")) {
                        nutritionalStatusReferredTag = "close";
                    }
                    if (element.equalsIgnoreCase("gbv1")) {
                        gbv1Tag = "close";
                    }
                    if (element.equalsIgnoreCase("gbv1_referred")) {
                        gbv1ReferredTag = "close";
                    }
                    if (element.equalsIgnoreCase("gbv2")) {
                        gbv2Tag = "close";
                    }
                    if (element.equalsIgnoreCase("gbv2_referred")) {
                        gbv2ReferredTag = "close";
                    }
                    if (element.equalsIgnoreCase("hypertensive")) {
                        hypertensiveTag = "close";
                    }
                    if (element.equalsIgnoreCase("first_hypertensive")) {
                        firstHypertensiveTag = "close";
                    }
                    if (element.equalsIgnoreCase("bp_above")) {
                        bpAboveTag = "close";
                    }
                    if (element.equalsIgnoreCase("bp_referred")) {
                        bpReferredTag = "close";
                    }
                    if (element.equalsIgnoreCase("diabetic")) {
                        diabeticTag = "close";
                    }
                    if (element.equalsIgnoreCase("bp_referred")) {
                        bpReferredTag = "close";
                    }
                    if (element.equalsIgnoreCase("first_diabetic")) {
                        firstDiabeticTag = "close";
                    }
                    if (element.equalsIgnoreCase("dm_referred")) {
                        dmReferredTag = "close";
                    }
                    if (element.equalsIgnoreCase("dm_values")) {
                        dmValuesTag = "close";
                    }
                    if (element.equalsIgnoreCase("phdp1")) {
                        phdp1Tag = "close";
                    }
                    if (element.equalsIgnoreCase("phdp1_services_provided")) {
                        phdp1ServicesProvidedTag = "close";
                    }
                    if (element.equalsIgnoreCase("phdp2")) {
                        phdp2Tag = "close";
                    }
                    if (element.equalsIgnoreCase("phdp2_services_provided")) {
                        phdp2ServicesProvidedTag = "close";
                    }
                    if (element.equalsIgnoreCase("phdp3")) {
                        phdp3Tag = "close";
                    }
                    if (element.equalsIgnoreCase("phdp3_services_provided")) {
                        phdp3ServicesProvidedTag = "close";
                    }
                    if (element.equalsIgnoreCase("phdp4")) {
                        phdp4Tag = "close";
                    }
                    if (element.equalsIgnoreCase("phdp5_services_provided")) {
                        phdp5ServicesProvidedTag = "close";
                    }
                    if (element.equalsIgnoreCase("phdp6")) {
                        phdp6Tag = "close";
                    }
                    if (element.equalsIgnoreCase("phdp6_services_provided")) {
                        phdp6ServicesProvidedTag = "close";
                    }
                    if (element.equalsIgnoreCase("phdp7")) {
                        phdp7Tag = "close";
                    }
                    if (element.equalsIgnoreCase("phdp7_services_provided")) {
                        phdp7ServicesProvidedTag = "close";
                    }
                    if (element.equalsIgnoreCase("phdp8")) {
                        phdp8Tag = "close";
                    }
                    if (element.equalsIgnoreCase("phdp8_services_provided")) {
                        phdp8ServicesProvidedTag = "close";
                    }
                    if (element.equalsIgnoreCase("phdp9_services_provided")) {
                        phdp9ServicesProvidedTag = "close";
                    }

                    if (element.equalsIgnoreCase("reproductive_intentions1")) {
                        reproductiveIntentions1Tag = "close";
                    }
                    if (element.equalsIgnoreCase("reproductive_intentions1_referred")) {
                        reproductiveIntentions1ReferredTag = "close";
                    }

                    if (element.equalsIgnoreCase("reproductive_intentions2")) {
                        reproductiveIntentions2Tag = "close";
                    }
                    if (element.equalsIgnoreCase("reproductive_intentions2_referred")) {
                        reproductiveIntentions2ReferredTag = "close";
                    }
                    if (element.equalsIgnoreCase("reproductive_intentions3")) {
                        reproductiveIntentions3Tag = "close";
                    }
                    if (element.equalsIgnoreCase("reproductive_intentions3_referred")) {
                        reproductiveIntentions3ReferredTag = "close";
                    }
                    if (element.equalsIgnoreCase(" malaria_prevention1")) {
                        malariaPrevention1Tag = "close";
                    }
                    if (element.equalsIgnoreCase("malaria_prevention1_referred")) {
                        malariaPrevention1ReferredTag = "close";
                    }
                    if (element.equalsIgnoreCase(" malaria_prevention2")) {
                        malariaPrevention2Tag = "close";
                    }
                    if (element.equalsIgnoreCase("malaria_prevention2_referred")) {
                        malariaPrevention2ReferredTag = "close";
                    }
                    if (element.equalsIgnoreCase("time_stamp")) {
                        timeStampTag = "close";
                    }
                    if (element.equalsIgnoreCase("user_id")) {
                        userIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("id_uuid") || element.equalsIgnoreCase("uuid")) {
                        idUUIDTag = "close";
                    }

                    //if this is the closing tag of a chroniccare element save the record
                    if (element.equalsIgnoreCase("chroniccare")) {
                        chroniccareTag = "close";
                        if (!skipRecord) {
                            Long patientId = ServerIDProvider.getPatientServerId(hospitalNum, facilityId);
                            if (patientId != null) {
                                Patient patient = new Patient();
                                patient.setPatientId(patientId);
                                chroniccare.setPatient(patient);
                                Long id = ServerIDProvider
                                        .getPatientDependantId("chroniccare", hospitalNum,
                                                chroniccare.getDateVisit(), facilityId);
                                if (id != null) {
                                    chroniccare.setChroniccareId(id);
                                    ChroniccareDAO.update(chroniccare);
                                } else {
                                    try {
                                        ChroniccareDAO.save(chroniccare);
                                    } catch (Exception ignored) {

                                    }
                                }
                            }
                        }
                    }
                }
            };

            //parse the XML specified in the given path and uses supplied handler to parse the document
            //this calls startElement(), endElement(), and character() methods accordingly
            saxParser.parse(xmlFileName, defaultHandler);
            //new CleanupService().cleanNullFields("chroniccare", facilityId);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }

}
