/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.service.parser.xml;

import org.fhi360.lamis.dao.hibernate.EidDAO;
import org.fhi360.lamis.model.Eid;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.fhi360.lamis.service.ServerIDProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.Scrambler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class EidXmlParser extends DefaultHandler {

    private long facilityId;
    private long idOnServer;
    private Boolean populated;
    private Eid eid;
    private String labno;
    private Scrambler scrambler = new Scrambler();

    public EidXmlParser() {
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
                String eidTag = "close";
                String eidIdTag = "close";
                String facilityIdTag = "close";
                String labnoTag = "close";
                String motherNameTag = "close";
                String motherAddressTag = "close";
                String motherPhoneTag = "close";
                String senderNameTag = "close";
                String senderAddressTag = "close";
                String senderDesignationTag = "close";
                String senderPhoneTag = "close";
                String reasonPcrTag = "close";
                String rapidTestDoneTag = "close";
                String dateRapidTestTag = "close";
                String rapidTestResultTag = "close";
                String motherArtReceivedTag = "close";
                String motherProphylaxReceivedTag = "close";
                String childProphylaxReceivedTag = "close";
                String breastfedEverTag = "close";
                String feedingMethodTag = "close";
                String breastfedNowTag = "close";
                String feedingCessationAgeTag = "close";
                String cotrimTag = "close";
                String nextAppointmentTag = "close";
                String timeStampTag = "close";
                String idUUIDTag = "close";

                //this method is called every time the parser gets an open tag '<'
                //identifies which tag is being open at the time by assigning an open flag
                @Override
                public void startElement(String uri, String localName, String element, Attributes attributes) throws SAXException {
                    if (element.equalsIgnoreCase("eid")) {
                        eidTag = "open";
                        eid = new Eid();
                    }
                    if (element.equalsIgnoreCase("eid_id")) {
                        eidIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("labno")) {
                        labnoTag = "open";
                    }
                    if (element.equalsIgnoreCase("mother_name")) {
                        motherNameTag = "open";
                    }
                    if (element.equalsIgnoreCase("mother_address")) {
                        motherAddressTag = "open";
                    }
                    if (element.equalsIgnoreCase("mother_phone")) {
                        motherPhoneTag = "open";
                    }
                    if (element.equalsIgnoreCase("sender_name")) {
                        senderNameTag = "open";
                    }
                    if (element.equalsIgnoreCase("sender_address")) {
                        senderAddressTag = "open";
                    }
                    if (element.equalsIgnoreCase("sender_designation")) {
                        senderDesignationTag = "open";
                    }
                    if (element.equalsIgnoreCase("sender_phone")) {
                        senderPhoneTag = "open";
                    }
                    if (element.equalsIgnoreCase("reason_pcr")) {
                        reasonPcrTag = "open";
                    }
                    if (element.equalsIgnoreCase("rapid_test_done")) {
                        rapidTestDoneTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_rapid_test")) {
                        dateRapidTestTag = "open";
                    }
                    if (element.equalsIgnoreCase("rapid_test_result")) {
                        rapidTestResultTag = "open";
                    }
                    if (element.equalsIgnoreCase("mother_art_received")) {
                        motherArtReceivedTag = "open";
                    }
                    if (element.equalsIgnoreCase("mother_prophylax_received")) {
                        motherProphylaxReceivedTag = "open";
                    }
                    if (element.equalsIgnoreCase("child_prophylax_received")) {
                        childProphylaxReceivedTag = "open";
                    }
                    if (element.equalsIgnoreCase("breastfed_ever")) {
                        breastfedEverTag = "open";
                    }
                    if (element.equalsIgnoreCase("feeding_method")) {
                        feedingMethodTag = "open";
                    }
                    if (element.equalsIgnoreCase("breastfed_now")) {
                        breastfedNowTag = "open";
                    }
                    if (element.equalsIgnoreCase("feeding_cessation_age")) {
                        feedingCessationAgeTag = "open";
                    }
                    if (element.equalsIgnoreCase("cotrim")) {
                        cotrimTag = "open";
                    }
                    if (element.equalsIgnoreCase("next_appointment")) {
                        nextAppointmentTag = "open";
                    }
                    if (element.equalsIgnoreCase("time_stamp")) {
                        timeStampTag = "open";
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
                        eid.setFacilityId(facilityId);
                    }
                    if (labnoTag.equals("open")) {
                        labno = (new String(chars, start, length));
                        eid.setLabno(labno);
                    }
                    if (motherNameTag.equals("open")) {
                        eid.setMotherName(scrambler.scrambleCharacters(new String(chars, start, length)));
                    }
                    if (motherAddressTag.equals("open")) {
                        eid.setMotherAddress(scrambler.scrambleCharacters(new String(chars, start, length)));
                    }
                    if (motherPhoneTag.equals("open")) {
                        eid.setMotherPhone(scrambler.scrambleNumbers(new String(chars, start, length)));
                    }
                    if (senderNameTag.equals("open")) {
                        eid.setSenderName(new String(chars, start, length));
                    }
                    if (senderAddressTag.equals("open")) {
                        eid.setSenderAddress(new String(chars, start, length));
                    }
                    if (senderDesignationTag.equals("open")) {
                        eid.setSenderDesignation(new String(chars, start, length));
                    }
                    if (senderPhoneTag.equals("open")) {
                        eid.setSenderPhone(new String(chars, start, length));
                    }
                    if (reasonPcrTag.equals("open")) {
                        eid.setReasonPcr(new String(chars, start, length));
                    }
                    if (rapidTestDoneTag.equals("open")) {
                        eid.setRapidTestDone(new String(chars, start, length));
                    }
                    if (dateRapidTestTag.equals("open")) {
                        String dateRapidTest = new String(chars, start, length);
                        if (!dateRapidTest.trim().isEmpty()) {
                            eid.setDateRapidTest(DateUtil.parseStringToDate(dateRapidTest, "yyyy-MM-dd"));
                        }
                    }
                    if (rapidTestResultTag.equals("open")) {
                        eid.setRapidTestResult(new String(chars, start, length));
                    }
                    if (motherArtReceivedTag.equals("open")) {
                        eid.setMotherArtReceived(new String(chars, start, length));
                    }
                    if (motherProphylaxReceivedTag.equals("open")) {
                        eid.setMotherProphylaxReceived(new String(chars, start, length));
                    }
                    if (childProphylaxReceivedTag.equals("open")) {
                        eid.setChildProphylaxReceived(new String(chars, start, length));
                    }
                    if (feedingMethodTag.equals("open")) {
                        eid.setFeedingMethod(new String(chars, start, length));
                    }
                    if (breastfedEverTag.equals("open")) {
                        eid.setBreastfedEver(new String(chars, start, length));
                    }
                    if (feedingCessationAgeTag.equals("open")) {
                        String feedingCessationAge = new String(chars, start, length);
                        if (!feedingCessationAge.trim().isEmpty()) {
                            eid.setFeedingCessationAge(Integer.parseInt(feedingCessationAge));
                        }
                    }
                    if (cotrimTag.equals("open")) {
                        eid.setCotrim(new String(chars, start, length));
                    }
                    if (nextAppointmentTag.equals("open")) {
                        String nextAppointment = new String(chars, start, length);
                        if (!nextAppointment.trim().isEmpty()) {
                            eid.setNextAppointment(DateUtil.parseStringToDate(nextAppointment, "yyyy-MM-dd"));
                        }
                    }
                    if (timeStampTag.equals("open")) {
                        eid.setTimeStamp(new java.sql.Date(new java.util.Date().getTime()));
                    }
                    if (idUUIDTag.equals("open")) {
                        eid.setUuid(new String(chars, start, length));
                    }
                }

                @Override
                public void endElement(String uri, String localName, String element) throws SAXException {
                    if (element.equalsIgnoreCase("eid_id")) {
                        eidIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("labno")) {
                        labnoTag = "close";
                    }
                    if (element.equalsIgnoreCase("mother_name")) {
                        motherNameTag = "close";
                    }
                    if (element.equalsIgnoreCase("mother_address")) {
                        motherAddressTag = "close";
                    }
                    if (element.equalsIgnoreCase("mother_phone")) {
                        motherPhoneTag = "close";
                    }
                    if (element.equalsIgnoreCase("sender_name")) {
                        senderNameTag = "close";
                    }
                    if (element.equalsIgnoreCase("sender_address")) {
                        senderAddressTag = "close";
                    }
                    if (element.equalsIgnoreCase("sender_designation")) {
                        senderDesignationTag = "close";
                    }
                    if (element.equalsIgnoreCase("sender_phone")) {
                        senderPhoneTag = "close";
                    }
                    if (element.equalsIgnoreCase("reason_pcr")) {
                        reasonPcrTag = "close";
                    }
                    if (element.equalsIgnoreCase("rapid_test_done")) {
                        rapidTestDoneTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_rapid_test")) {
                        dateRapidTestTag = "close";
                    }
                    if (element.equalsIgnoreCase("rapid_test_result")) {
                        rapidTestResultTag = "close";
                    }
                    if (element.equalsIgnoreCase("mother_art_received")) {
                        motherArtReceivedTag = "close";
                    }
                    if (element.equalsIgnoreCase("mother_prophylax_received")) {
                        motherProphylaxReceivedTag = "close";
                    }
                    if (element.equalsIgnoreCase("child_prophylax_received")) {
                        childProphylaxReceivedTag = "close";
                    }
                    if (element.equalsIgnoreCase("breastfed_ever")) {
                        breastfedEverTag = "close";
                    }
                    if (element.equalsIgnoreCase("feeding_method")) {
                        feedingMethodTag = "close";
                    }
                    if (element.equalsIgnoreCase("breastfed_now")) {
                        breastfedNowTag = "close";
                    }

                    if (element.equalsIgnoreCase("feeding_cessation_age")) {
                        feedingCessationAgeTag = "close";
                    }
                    if (element.equalsIgnoreCase("cotrim")) {
                        cotrimTag = "close";
                    }
                    if (element.equalsIgnoreCase("next_appointment")) {
                        nextAppointmentTag = "close";
                    }
                    if (element.equalsIgnoreCase("time_stamp")) {
                        timeStampTag = "close";
                    }
                    if (element.equalsIgnoreCase("id_uuid") || element.equalsIgnoreCase("uuid")) {
                        idUUIDTag = "close";
                    }

                    //if this is the closing tag of a eid element save the record
                    if (element.equalsIgnoreCase("eid")) {
                        eidTag = "close";
                        Long eidId = ServerIDProvider.getEidId(labno, facilityId);
                        if (eidId != null) {
                            eid.setEidId(eidId);
                            EidDAO.update(eid);
                        } else {
                            EidDAO.save(eid);
                        }
                    }
                }
            };

            //parse the XML specified in the given path and uses supplied handler to parse the document
            //this calls startElement(), endElement(), and character() methods accordingly
            saxParser.parse(xmlFileName, defaultHandler);
            //new CleanupService().cleanNullFields("eid", facilityId);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }

}
