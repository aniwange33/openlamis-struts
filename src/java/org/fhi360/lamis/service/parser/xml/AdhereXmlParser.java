/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.service.parser.xml;

import org.fhi360.lamis.dao.hibernate.AdherehistoryDAO;
import org.fhi360.lamis.model.Adherehistory;
import org.fhi360.lamis.model.Patient;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.fhi360.lamis.service.ServerIDProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AdhereXmlParser extends DefaultHandler {

    private long facilityId;
    private String hospitalNum;
    private String dateVisit;
    private boolean skipRecord;
    private Adherehistory adherehistory;

    public AdhereXmlParser() {
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
                String adherehistoryTag = "close";
                String historyIdTag = "close";
                String patientIdTag = "close";
                String facilityIdTag = "close";
                String hospitalNumTag = "close";
                String dateVisitTag = "close";
                String reasonTag = "close";
                String timeStampTag = "close";
                String idUUIDTag = "close";

                //this method is called every time the parser gets an open tag '<'
                //identifies which tag is being open at the time by assigning an open flag
                @Override
                public void startElement(String uri, String localName, String element, Attributes attributes) throws SAXException {
                    if (element.equalsIgnoreCase("adherehistory")) {
                        adherehistoryTag = "open";
                        adherehistory = new Adherehistory();
                    }
                    if (element.equalsIgnoreCase("history_id")) {
                        historyIdTag = "open";
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
                    if (element.equalsIgnoreCase("date_visit")) {
                        dateVisitTag = "open";
                    }
                    if (element.equalsIgnoreCase("reason")) {
                        reasonTag = "open";
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
                        adherehistory.setFacilityId(facilityId);

                    }
                    if (hospitalNumTag.equals("open")) {
                        hospitalNum = new String(chars, start, length);
                    }
                    if (dateVisitTag.equals("open")) {
                        dateVisit = new String(chars, start, length);
                        if (!dateVisit.trim().isEmpty()) {
                            adherehistory.setDateVisit(DateUtil.parseStringToDate(dateVisit, "yyyy-MM-dd"));
                        } else {
                            skipRecord = true;
                        }
                    }
                    if (reasonTag.equals("open")) {
                        adherehistory.setReason(new String(chars, start, length));
                    }
                    if (idUUIDTag.equals("open")) {
                        adherehistory.setUuid(new String(chars, start, length));
                    }
                    if (timeStampTag.equals("open")) {
                        adherehistory.setTimeStamp(new java.sql.Date(new java.util.Date().getTime()));
                    }
                }

                @Override
                public void endElement(String uri, String localName, String element) throws SAXException {
                    if (element.equalsIgnoreCase("history_id")) {
                        historyIdTag = "close";
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
                    if (element.equalsIgnoreCase("date_visit")) {
                        dateVisitTag = "close";
                    }
                    if (element.equalsIgnoreCase("reason")) {
                        reasonTag = "close";
                    }
                    if (element.equalsIgnoreCase("time_stamp")) {
                        timeStampTag = "close";
                    }
                    if (element.equalsIgnoreCase("id_uuid") || element.equalsIgnoreCase("uuid")) {
                        idUUIDTag = "close";
                    }

                    //if this is the closing tag of a adherehistory element save the record
                    if (element.equalsIgnoreCase("adherehistory")) {
                        adherehistoryTag = "close";
                        if (!skipRecord) {
                            Long patientId = ServerIDProvider.getPatientServerId(hospitalNum, facilityId);
                            if (patientId != null) {
                                Patient patient = new Patient();
                                patient.setPatientId(patientId);
                                adherehistory.setPatient(patient);
                                Long id = ServerIDProvider.getPatientDependantId("adherehistory", hospitalNum, adherehistory.getDateVisit(), facilityId);
                                if (id != null) {
                                    adherehistory.setHistoryId(id);
                                    AdherehistoryDAO.update(adherehistory);
                                } else {
                                    try {
                                        AdherehistoryDAO.save(adherehistory);
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
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }

}
