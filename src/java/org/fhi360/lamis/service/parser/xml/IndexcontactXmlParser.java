/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.parser.xml;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.fhi360.lamis.dao.hibernate.IndexcontactDAO;
import org.fhi360.lamis.dao.jdbc.HtsJDBC;
import org.fhi360.lamis.dao.jdbc.IndexcontactJDBC;
import org.fhi360.lamis.model.Hts;
import org.fhi360.lamis.model.Indexcontact;
import org.fhi360.lamis.utility.DateUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author User10
 */
public class IndexcontactXmlParser extends DefaultHandler {

    private long facilityId;
    private boolean skipRecord;
    private Indexcontact indexcontact;

    public IndexcontactXmlParser() {
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
                String indexcontactTag = "close";
                String facilityIdTag = "close";
                String timeStampTag = "close";
                String idUUIDTag = "close";
                String clientCodeTag = "close";
                String contactTypeTag = "close";
                String surnameTag = "close";
                String otherNamesTag = "close";
                String ageTag = "close";
                String genderTag = "close";
                String phoneTag = "close";
                String addressTag = "close";
                String relationshipTag = "close";
                String gbvTag = "close";
                String durationPartnerTag = "close";
                String phoneTrackingTag = "close";
                String homeTrackingTag = "close";
                String outcomeTag = "close";
                String dateHivTestTag = "close";
                String hivStatusTag = "close";
                String linkCareTag = "close";
                String partnerNotificationTag = "close";
                String modeNotificationTag = "close";
                String serviceProvidedTag = "close";
                String indexContactCodeTag = "close";

                //this method is called every time the parser gets an open tag '<'
                //identifies which tag is being open at the time by assigning an open flag
                @Override
                public void startElement(String uri, String localName, String element, Attributes attributes) throws SAXException {
                    if (element.equalsIgnoreCase("indexcontext")) {
                        indexcontactTag = "open";
                        indexcontact = new Indexcontact();
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("time_stamp")) {
                        timeStampTag = "open";
                    }
                    if (element.equalsIgnoreCase("id_uuid") || element.equalsIgnoreCase("uuid")) {
                        idUUIDTag = "open";
                    }
                    if (element.equalsIgnoreCase("client_code")) {
                        clientCodeTag = "open";
                    }
                    if (element.equalsIgnoreCase("contact_type")) {
                        contactTypeTag = "open";
                    }
                    if (element.equalsIgnoreCase("surname")) {
                        surnameTag = "open";
                    }
                    if (element.equalsIgnoreCase("other_names")) {
                        otherNamesTag = "open";
                    }
                    if (element.equalsIgnoreCase("age")) {
                        ageTag = "open";
                    }
                    if (element.equalsIgnoreCase("gender")) {
                        genderTag = "open";
                    }
                    if (element.equalsIgnoreCase("phone")) {
                        phoneTag = "open";
                    }
                    if (element.equalsIgnoreCase("addres")) {
                        addressTag = "open";
                    }
                    if (element.equalsIgnoreCase("relationship")) {
                        relationshipTag = "open";
                    }
                    if (element.equalsIgnoreCase("gbv")) {
                        gbvTag = "open";
                    }
                    if (element.equalsIgnoreCase("duration_partner")) {
                        durationPartnerTag = "open";
                    }
                    if (element.equalsIgnoreCase("phone_tracking")) {
                        phoneTrackingTag = "open";
                    }
                    if (element.equalsIgnoreCase("home_tracking")) {
                        homeTrackingTag = "open";
                    }
                    if (element.equalsIgnoreCase("outcome")) {
                        outcomeTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_hiv_test")) {
                        dateHivTestTag = "open";
                    }
                    if (element.equalsIgnoreCase("hiv_status")) {
                        hivStatusTag = "open";
                    }
                    if (element.equalsIgnoreCase("link_care")) {
                        linkCareTag = "open";
                    }
                    if (element.equalsIgnoreCase("partner_notification")) {
                        partnerNotificationTag = "open";
                    }
                    if (element.equalsIgnoreCase("mode_notification")) {
                        modeNotificationTag = "open";
                    }
                    if (element.equalsIgnoreCase("service_provided")) {
                        serviceProvidedTag = "open";
                    }
                    if (element.equalsIgnoreCase("index_contact_code")) {
                        indexContactCodeTag = "open";
                    }
                }

                //store data store in between '<' and '>' tags                     
                @Override
                public void characters(char[] chars, int start, int length) throws SAXException {
                    String content = new String(chars, start, length);
                    if (facilityIdTag.equals("open")) {
                        facilityId = Long.parseLong(content);
                        indexcontact.setFacilityId(facilityId);
                    }
                    if (idUUIDTag.equals("open")) {
                        indexcontact.setUuid(content);
                    }
                    if (timeStampTag.equals("open")) {
                        indexcontact.setTimeStamp(new java.sql.Date(new java.util.Date().getTime()));
                    }
                    if (clientCodeTag.equals("open")) {
                        indexcontact.setClientCode(content);
                    }
                    if (contactTypeTag.equals("open")) {
                        indexcontact.setContactType(content);
                    }
                    if (surnameTag.equals("open")) {
                        indexcontact.setSurname(content);
                    }
                    if (otherNamesTag.equals("open")) {
                        indexcontact.setOtherNames(content);
                    }
                    if (ageTag.equals("open")) {
                        try {
                            indexcontact.setAge(Integer.parseInt(content));
                        } catch (Exception e) {
                        }
                    }
                    if (genderTag.equals("open")) {
                        indexcontact.setGender(content);
                    }
                    if (phoneTag.equals("open")) {
                        indexcontact.setPhone(content);
                    }
                    if (addressTag.equals("open")) {
                        indexcontact.setAddress(content);
                    }
                    if (relationshipTag.equals("open")) {
                        indexcontact.setRelationship(content);
                    }
                    if (gbvTag.equals("open")) {
                        indexcontact.setGbv(content);
                    }
                    if (durationPartnerTag.equals("open")) {
                        try {
                            indexcontact.setDurationPartner(Integer.parseInt(content));
                        } catch (Exception e) {
                        }
                    }
                    if (phoneTrackingTag.equals("open")) {
                        indexcontact.setPhoneTracking(content);
                    }
                    if (homeTrackingTag.equals("open")) {
                        indexcontact.setHomeTracking(content);
                    }
                    if (outcomeTag.equals("open")) {
                        indexcontact.setOutcome(content);
                    }
                    if (hivStatusTag.equals("open")) {
                        indexcontact.setHivStatus(content);
                    }
                    if (linkCareTag.equals("open")) {
                        indexcontact.setLinkCare(content);
                    }
                    if (partnerNotificationTag.equals("open")) {
                        indexcontact.setPartnerNotification(content);
                    }
                    if (modeNotificationTag.equals("open")) {
                        indexcontact.setModeNotification(content);
                    }
                    if (serviceProvidedTag.equals("open")) {
                        indexcontact.setServiceProvided(content);
                    }
                    if (indexContactCodeTag.equals("open")) {
                        indexcontact.setIndexContactCode(content);
                    }
                    if (dateHivTestTag.equals("open")) {
                        if (!content.trim().isEmpty()) {
                            indexcontact.setDateHivTest(DateUtil.parseStringToDate(content, "yyyy-MM-dd"));
                        }
                    }
                }

                @Override
                public void endElement(String uri, String localName, String element) throws SAXException {
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("time_stamp")) {
                        timeStampTag = "close";
                    }
                    if (element.equalsIgnoreCase("id_uuid") || element.equalsIgnoreCase("uuid")) {
                        idUUIDTag = "close";
                    }
                    if (element.equalsIgnoreCase("client_code")) {
                        clientCodeTag = "close";
                    }
                    if (element.equalsIgnoreCase("contact_type")) {
                        contactTypeTag = "close";
                    }
                    if (element.equalsIgnoreCase("surname")) {
                        surnameTag = "close";
                    }
                    if (element.equalsIgnoreCase("other_names")) {
                        otherNamesTag = "close";
                    }
                    if (element.equalsIgnoreCase("age")) {
                        ageTag = "close";
                    }
                    if (element.equalsIgnoreCase("gender")) {
                        genderTag = "close";
                    }
                    if (element.equalsIgnoreCase("phone")) {
                        phoneTag = "close";
                    }
                    if (element.equalsIgnoreCase("addres")) {
                        addressTag = "close";
                    }
                    if (element.equalsIgnoreCase("relationship")) {
                        relationshipTag = "close";
                    }
                    if (element.equalsIgnoreCase("gbv")) {
                        gbvTag = "close";
                    }
                    if (element.equalsIgnoreCase("duration_partner")) {
                        durationPartnerTag = "close";
                    }
                    if (element.equalsIgnoreCase("phone_tracking")) {
                        phoneTrackingTag = "close";
                    }
                    if (element.equalsIgnoreCase("home_tracking")) {
                        homeTrackingTag = "close";
                    }
                    if (element.equalsIgnoreCase("outcome")) {
                        outcomeTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_hiv_test")) {
                        dateHivTestTag = "close";
                    }
                    if (element.equalsIgnoreCase("hiv_status")) {
                        hivStatusTag = "close";
                    }
                    if (element.equalsIgnoreCase("link_care")) {
                        linkCareTag = "close";
                    }
                    if (element.equalsIgnoreCase("partner_notification")) {
                        partnerNotificationTag = "close";
                    }
                    if (element.equalsIgnoreCase("mode_notification")) {
                        modeNotificationTag = "close";
                    }
                    if (element.equalsIgnoreCase("service_provided")) {
                        serviceProvidedTag = "close";
                    }
                    if (element.equalsIgnoreCase("index_contact_code")) {
                        indexContactCodeTag = "close";
                    }

                    //if this is the closing tag of a adherehistory element save the record
                    if (element.equalsIgnoreCase("adherehistory")) {
                        indexcontactTag = "close";
                        if (!skipRecord) {
                            long htsId = new HtsJDBC().getHtsId(indexcontact.getIndexContactCode(), indexcontact.getFacilityId());
                            Hts hts = new Hts();
                            hts.setHtsId(htsId);
                            indexcontact.setHts(hts);
                            long indexContactId = new IndexcontactJDBC().getIndexcontactId(indexcontact.getIndexContactCode(), indexcontact.getFacilityId());
                            if (indexContactId == 0L) {
                                IndexcontactDAO.save(indexcontact);
                            } else {
                                indexcontact.setIndexcontactId(indexContactId);
                                IndexcontactDAO.update(indexcontact);
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
