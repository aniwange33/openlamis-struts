/**
 * @author aalozie
 */
package org.fhi360.lamis.service.parser.xml;

import org.fhi360.lamis.dao.hibernate.ChildfollowupDAO;
import org.fhi360.lamis.model.Child;
import org.fhi360.lamis.model.Childfollowup;
import org.fhi360.lamis.service.ServerIDProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.StringUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class ChildfollowupXmlParser extends DefaultHandler {

    private Long facilityId;
    private Boolean skipRecord;
    private Childfollowup childfollowup;

    public ChildfollowupXmlParser() {
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
                String childfollowupTag = "close";
                String childfollowupIdTag = "close";
                String facilityIdTag = "close";
                String referenceNumTag = "close";  //waivered into the xml
                String dateVisitTag = "close";
                String ageVisitTag = "close";
                String dateNvpInitiatedTag = "close";
                String ageNvpInitiatedTag = "close";
                String dateCotrimInitiatedTag = "close";
                String ageCotrimInitiatedTag = "close";
                String bodyWeightTag = "close";
                String heightTag = "close";
                String feedingTag = "close";
                String arvTag = "close";
                String cotrimTag = "close";
                String dateSampleCollectedTag = "close";
                String reasonPcrTag = "close";
                String dateSampleSentTag = "close";
                String datePcrResultTag = "close";
                String pcrResultTag = "close";
                String rapidTestTag = "close";
                String rapidTestResultTag = "close";
                String caregiverGivenResultTag = "close";
                String childOutcomeTag = "close";
                String referredTag = "close";
                String dateNextVisitTag = "close";
                String timeStampTag = "close";
                String userIdTag = "close";
                String childIdTag = "close";
                String id;
                String uuid;
                String dateVisit;
                String referenceNum;
                String uuidTag = "close";

                //this method is called every time the parser gets an open tag '<'
                //identifies which tag is being open at the time by assigning an open flag
                @Override
                public void startElement(String uri, String localName, String element, Attributes attributes) throws SAXException {
                    if (element.equalsIgnoreCase("childfollowup")) {
                        childfollowupTag = "open";
                        childfollowup = new Childfollowup();
                        skipRecord = false;
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("child_id")) {
                        childIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("childfollowup_id")) {
                        childfollowupIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("reference_num")) {
                        referenceNumTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_visit")) {
                        dateVisitTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_nvp_initiated")) {
                        dateNvpInitiatedTag = "open";
                    }
                    if (element.equalsIgnoreCase("age_nvp_initiated")) {
                        ageNvpInitiatedTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_cotrim_initiated")) {
                        dateCotrimInitiatedTag = "open";
                    }
                    if (element.equalsIgnoreCase("age_cotrim_initiated")) {
                        ageCotrimInitiatedTag = "open";
                    }
                    if (element.equalsIgnoreCase("body_weight")) {
                        bodyWeightTag = "open";
                    }
                    if (element.equalsIgnoreCase("height")) {
                        heightTag = "open";
                    }
                    if (element.equalsIgnoreCase("feeding")) {
                        feedingTag = "open";
                    }
                    if (element.equalsIgnoreCase("arv")) {
                        arvTag = "open";
                    }
                    if (element.equalsIgnoreCase("cotrim")) {
                        cotrimTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_sample_collected")) {
                        dateSampleCollectedTag = "open";
                    }
                    if (element.equalsIgnoreCase("reason_pcr")) {
                        reasonPcrTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_sample_sent")) {
                        dateSampleSentTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_pcr_result")) {
                        datePcrResultTag = "open";
                    }
                    if (element.equalsIgnoreCase("pcr_result")) {
                        pcrResultTag = "open";
                    }
                    if (element.equalsIgnoreCase("rapid_test")) {
                        rapidTestTag = "open";
                    }
                    if (element.equalsIgnoreCase("rapid_test_result")) {
                        rapidTestResultTag = "open";
                    }
                    if (element.equalsIgnoreCase("caregiver_given_result")) {
                        caregiverGivenResultTag = "open";
                    }
                    if (element.equalsIgnoreCase("child_outcome")) {
                        childOutcomeTag = "open";
                    }
                    if (element.equalsIgnoreCase("referred")) {
                        referredTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_next_visit")) {
                        dateNextVisitTag = "open";
                    }
                    if (element.equalsIgnoreCase("time_stamp")) {
                        timeStampTag = "open";
                    }
                    if (element.equalsIgnoreCase("user_id")) {
                        userIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("uuid")) {
                        uuidTag = "open";
                    }
                }

                //store data store in between '<' and '>' tags                     
                @Override
                public void characters(char[] chars, int start, int length) throws SAXException {
                   if (referenceNumTag.equals("open")) {
                        referenceNum = new String(chars, start, length);
                    }
                    if (dateVisitTag.equals("open")) {
                        dateVisit = new String(chars, start, length);
                        if (!dateVisit.trim().isEmpty()) {
                            childfollowup.setDateVisit(DateUtil.parseStringToDate(dateVisit, "yyyy-MM-dd"));
                        } else {
                            skipRecord = true;
                        }
                    }
                    if (dateNvpInitiatedTag.equals("open")) {
                        String dateNvpInitiated = new String(chars, start, length);
                        if (!dateNvpInitiated.trim().isEmpty()) {
                            childfollowup.setDateNvpInitiated(DateUtil.parseStringToDate(dateNvpInitiated, "yyyy-MM-dd"));
                        }
                    }
                    if (ageNvpInitiatedTag.equals("open")) {
                        String ageNvpInitiated = new String(chars, start, length);
                        if (!ageNvpInitiated.trim().trim().isEmpty()) {
                            if (StringUtil.isInt(ageNvpInitiated))
                                childfollowup.setAgeNvpInitiated(Integer.parseInt(ageNvpInitiated));
                            else skipRecord = true;
                        }
                    }
                    if (dateCotrimInitiatedTag.equals("open")) {
                        String dateCotrimInitiated = new String(chars, start, length);
                        if (!dateCotrimInitiated.trim().isEmpty()) {
                            childfollowup.setDateCotrimInitiated(DateUtil.parseStringToDate(dateCotrimInitiated, "yyyy-MM-dd"));
                        }
                    }
                    if (ageCotrimInitiatedTag.equals("open")) {
                        String ageCotrimInitiated = new String(chars, start, length);
                        if (!ageCotrimInitiated.trim().trim().isEmpty()) {
                            if (StringUtil.isInt(ageCotrimInitiated))
                                childfollowup.setAgeCotrimInitiated(Integer.parseInt(ageCotrimInitiated));
                            else skipRecord = true;
                        }
                    }
                    if (bodyWeightTag.equals("open")) {
                        String bodyWeight = new String(chars, start, length);
                        if (!bodyWeight.trim().isEmpty()) {
                            if (StringUtil.isDouble(bodyWeight))
                                childfollowup.setBodyWeight(Double.parseDouble(bodyWeight));
                            else skipRecord = true;
                        }
                    }
                    if (heightTag.equals("open")) {
                        String height = new String(chars, start, length);
                        if (!height.trim().isEmpty()) {
                            if (StringUtil.isDouble(height))
                                childfollowup.setHeight(Double.parseDouble(height));
                            else skipRecord = true;
                        }
                    }
                    if (feedingTag.equals("open")) {
                        childfollowup.setFeeding(new String(chars, start, length));
                    }
                    if (arvTag.equals("open")) {
                        childfollowup.setArv(new String(chars, start, length));
                    }
                    if (cotrimTag.equals("open")) {
                        childfollowup.setCotrim(new String(chars, start, length));
                    }
                    if (dateSampleCollectedTag.equals("open")) {
                        String dateSampleCollected = new String(chars, start, length);
                        if (!dateSampleCollected.trim().isEmpty()) {
                            childfollowup.setDateSampleCollected(DateUtil.parseStringToDate(dateSampleCollected, "yyyy-MM-dd"));
                        }
                    }
                    if (reasonPcrTag.equals("open")) {
                        childfollowup.setReasonPcr(new String(chars, start, length));
                    }
                    if (dateSampleSentTag.equals("open")) {
                        String dateSampleSent = new String(chars, start, length);
                        if (!dateSampleSent.trim().isEmpty()) {
                            childfollowup.setDateSampleSent(DateUtil.parseStringToDate(dateSampleSent, "yyyy-MM-dd"));
                        }
                    }
                    if (datePcrResultTag.equals("open")) {
                        String datePcrResult = new String(chars, start, length);
                        if (!datePcrResult.trim().isEmpty()) {
                            childfollowup.setDatePcrResult(DateUtil.parseStringToDate(datePcrResult, "yyyy-MM-dd"));
                        }
                    }
                    if (pcrResultTag.equals("open")) {
                        childfollowup.setPcrResult(new String(chars, start, length));
                    }
                    if (rapidTestTag.equals("open")) {
                        childfollowup.setRapidTest(new String(chars, start, length));
                    }
                    if (rapidTestResultTag.equals("open")) {
                        childfollowup.setRapidTestResult(new String(chars, start, length));
                    }
                    if (caregiverGivenResultTag.equals("open")) {
                        childfollowup.setCaregiverGivenResult(new String(chars, start, length));
                    }
                    if (childOutcomeTag.equals("open")) {
                        childfollowup.setChildOutcome(new String(chars, start, length));
                    }
                    if (referredTag.equals("open")) {
                        childfollowup.setReferred(new String(chars, start, length));
                    }
                    if (dateNextVisitTag.equals("open")) {
                        String dateNextVisit = new String(chars, start, length);
                        if (!dateNextVisit.trim().isEmpty()) {
                            childfollowup.setDateNextVisit(DateUtil.parseStringToDate(dateNextVisit, "yyyy-MM-dd"));
                        }
                    }
                    if (timeStampTag.equals("open")) {
                        childfollowup.setTimeStamp(new java.sql.Date(new java.util.Date().getTime()));
                    }
                    if (uuidTag.equals("open")) {
                        uuid = new String(chars, start, length);
                        childfollowup.setUuid(uuid);
                    }
                    if (facilityIdTag.equals("open")) {
                        facilityId = Long.parseLong(new String(chars, start, length));
                        childfollowup.setFacilityId(facilityId);
                    }
                }

                @Override
                public void endElement(String uri, String localName, String element) throws SAXException {
                    if (element.equalsIgnoreCase("childfollowup")) {
                        childfollowupTag = "close";
                    }
                    if (element.equalsIgnoreCase("child_id")) {
                        childIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("childfollowup_id")) {
                        childfollowupIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("reference_num")) {
                        referenceNumTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_visit")) {
                        dateVisitTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_nvp_initiated")) {
                        dateNvpInitiatedTag = "close";
                    }
                    if (element.equalsIgnoreCase("age_nvp_initiated")) {
                        ageNvpInitiatedTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_cotrim_initiated")) {
                        dateCotrimInitiatedTag = "close";
                    }
                    if (element.equalsIgnoreCase("age_cotrim_initiated")) {
                        ageCotrimInitiatedTag = "close";
                    }
                    if (element.equalsIgnoreCase("body_weight")) {
                        bodyWeightTag = "close";
                    }
                    if (element.equalsIgnoreCase("height")) {
                        heightTag = "close";
                    }
                    if (element.equalsIgnoreCase("feeding")) {
                        feedingTag = "close";
                    }
                    if (element.equalsIgnoreCase("arv")) {
                        arvTag = "close";
                    }
                    if (element.equalsIgnoreCase("cotrim")) {
                        cotrimTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_sample_collected")) {
                        dateSampleCollectedTag = "close";
                    }
                    if (element.equalsIgnoreCase("reason_pcr")) {
                        reasonPcrTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_sample_sent")) {
                        dateSampleSentTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_pcr_result")) {
                        datePcrResultTag = "close";
                    }
                    if (element.equalsIgnoreCase("pcr_result")) {
                        pcrResultTag = "close";
                    }
                    if (element.equalsIgnoreCase("rapid_test")) {
                        rapidTestTag = "close";
                    }
                    if (element.equalsIgnoreCase("rapid_test_result")) {
                        rapidTestResultTag = "close";
                    }
                    if (element.equalsIgnoreCase("caregiver_given_result")) {
                        caregiverGivenResultTag = "close";
                    }
                    if (element.equalsIgnoreCase("child_outcome")) {
                        childOutcomeTag = "close";
                    }
                    if (element.equalsIgnoreCase("referred")) {
                        referredTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_next_visit")) {
                        dateNextVisitTag = "close";
                    }
                    if (element.equalsIgnoreCase("time_stamp")) {
                        timeStampTag = "close";
                    }
                    if (element.equalsIgnoreCase("user_id")) {
                        userIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("id_uuid") || element.equalsIgnoreCase("uuid")) {
                        uuidTag = "open";
                    }

                    //if this is the closing tag of a child element save the record...
                    if (element.equalsIgnoreCase("childfollowup")) {
                        childfollowupTag = "close";
                        if (skipRecord) {
                            System.out.println("....record has a null value: ");
                        } else {
                            Long childId = ServerIDProvider.getChildFollowupChildId(referenceNum, facilityId);
                            if (childId != null) {
                                Child child = new Child();
                                child.setChildId(childId);
                                childfollowup.setChild(child);
                                Long id = ServerIDProvider.getChildFollowupId(referenceNum,
                                        childfollowup.getDateVisit(), facilityId);
                                if (id != null) {
                                    childfollowup.setChildfollowupId(id);
                                    ChildfollowupDAO.save(childfollowup);
                                } else {
                                    try {
                                        ChildfollowupDAO.save(childfollowup);
                                    }catch (Exception ignored){

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
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
