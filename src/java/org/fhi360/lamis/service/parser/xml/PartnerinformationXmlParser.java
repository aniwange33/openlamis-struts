/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.service.parser.xml;

import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.utility.PatientNumberNormalizer;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.fhi360.lamis.dao.hibernate.PartnerinformationDAO;
import org.fhi360.lamis.model.Partnerinformation;
import org.fhi360.lamis.service.ServerIDProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PartnerinformationXmlParser extends DefaultHandler {

    private long facilityId;
    private String hospitalNum;
    private String dateVisit;
    private long idOnServer;
    private boolean populated;
    private Boolean skipRecord;
    private Partnerinformation partnerinformation;
    private Patient patient = new Patient();

    public PartnerinformationXmlParser() {
    }

    public void parseXml(String xmlFileName) {
        populated = false;
        skipRecord = false;
        try {
            //obtain and configure a SAX based parser
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

            //obtain object for SAX hadler class
            SAXParser saxParser = saxParserFactory.newSAXParser();

            //default handler for SAX handler class
            // all three methods are written in handler's body
            DefaultHandler defaultHandler = new DefaultHandler() {
                String partnerinformationTag = "close";
                String partnerinformationIdTag = "close";
                String patientIdTag = "close";
                String facilityIdTag = "close";
                String hospitalNumTag = "close";
                String dateVisitTag = "close";
                String partnerNotificationTag = "close";
                String partnerHivStatusTag = "close";
                String partnerReferredTag = "close";
                String timeStampTag = "close";
                String userIdTag = "close";
                String idUUIDTag = "close";

                //this method is called every time the parser gets an open tag '<'
                //identifies which tag is being open at the time by assigning an open flag
                @Override
                public void startElement(String uri, String localName, String element, Attributes attributes) throws SAXException {
                    if (element.equalsIgnoreCase("partnerinformation")) {
                        partnerinformationTag = "open";
                        partnerinformation = new Partnerinformation();
                    }
                    if (element.equalsIgnoreCase("partnerinformation_id")) {
                        partnerinformationIdTag = "open";
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
                    if (element.equalsIgnoreCase("partner_notification")) {
                        partnerNotificationTag = "open";
                    }
                    if (element.equalsIgnoreCase("partner_hiv_status")) {
                        partnerHivStatusTag = "open";
                    }
                    if (element.equalsIgnoreCase("partner_referred")) {
                        partnerReferredTag = "open";
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
                        partnerinformation.setFacilityId(facilityId);
                    }
                    if (hospitalNumTag.equals("open")) {
                        hospitalNum = new String(chars, start, length);
                    }
                    if (dateVisitTag.equals("open")) {
                        dateVisit = new String(chars, start, length);
                        if (!dateVisit.trim().isEmpty()) {
                            partnerinformation.setDateVisit(DateUtil.parseStringToDate(dateVisit, "yyyy-MM-dd"));
                        } else {
                            skipRecord = true;
                        }
                    }
                    if (partnerNotificationTag.equals("open")) {
                        partnerinformation.setPartnerNotification(new String(chars, start, length));
                    }
                    if (partnerHivStatusTag.equals("open")) {
                        partnerinformation.setPartnerHivStatus(new String(chars, start, length));
                    }
                    if (partnerReferredTag.equals("open")) {
                        partnerinformation.setPartnerReferred(new String(chars, start, length));
                    }
                    if (timeStampTag.equals("open")) {
                        partnerinformation.setTimeStamp(new java.sql.Date(new java.util.Date().getTime()));
                    }
                    if (userIdTag.equals("open")) {
                        String userId = new String(chars, start, length);
                        if (!userId.trim().isEmpty()) {
                            partnerinformation.setUserId(Long.parseLong(userId));
                        }
                    }
                    if (idUUIDTag.equals("open")) {
                        partnerinformation.setUuid(new String(chars, start, length));
                    }
                }

                @Override
                public void endElement(String uri, String localName, String element) throws SAXException {
                    if (element.equalsIgnoreCase("partnerinformation_id")) {
                        partnerinformationIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("patient_id")) {
                        patientIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("hospital_num")) {
                        hospitalNumTag = "close";
                    }
                    if (element.equalsIgnoreCase("partner_notification")) {
                        partnerNotificationTag = "open";
                    }
                    if (element.equalsIgnoreCase("partner_hiv_status")) {
                        partnerHivStatusTag = "close";
                    }
                    if (element.equalsIgnoreCase("partner_referred")) {
                        partnerReferredTag = "close";
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

                    //if this is the closing tag of a adherehistory element save the record
                    if (element.equalsIgnoreCase("partnerinformation")) {
                        partnerinformationTag = "close";
                        if (!skipRecord) { Long patientId = ServerIDProvider.getPatientServerId(hospitalNum, facilityId);
                            if (patientId != null) {
                                Patient patient = new Patient();
                                patient.setPatientId(patientId);
                                partnerinformation.setPatient(patient);
                                Long id = ServerIDProvider
                                        .getPartnerInformationId(hospitalNum, facilityId);
                                if (id != null) {
                                    partnerinformation.setPartnerinformationId(id);
                                    PartnerinformationDAO.update(partnerinformation);
                                } else {
                                    PartnerinformationDAO.save(partnerinformation);
                                }
                            }
                        }

                    }
                }
            };

            //parse the XML specified in the given path and uses supplied handler to parse the document
            //this calls startElement(), endElement(), and character() methods accordingly
            saxParser.parse(xmlFileName, defaultHandler);
            //new CleanupService().cleanNullFields("partnerinformation", facilityId);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }

}
