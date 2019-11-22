/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.service.parser.xml;

import org.fhi360.lamis.dao.hibernate.OihistoryDAO;
import org.fhi360.lamis.model.Oihistory;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.utility.PatientNumberNormalizer;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.fhi360.lamis.service.ServerIDProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class OiXmlParser extends DefaultHandler {

    private long facilityId;
    private String hospitalNum;
    private String dateVisit;
    private String oi;
    private long idOnServer;
    private Boolean populated;
    private Boolean skipRecord;
    private Oihistory oihistory;
    private Patient patient = new Patient();

    public OiXmlParser() {
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
                String oihistoryTag = "close";
                String historyIdTag = "close";
                String patientIdTag = "close";
                String facilityIdTag = "close";
                String hospitalNumTag = "close";
                String dateVisitTag = "close";
                String oiTag = "close";
                String timeStampTag = "close";
                String idUUIDTag = "close";

                //this method is called every time the parser gets an open tag '<'
                //identifies which tag is being open at the time by assigning an open flag
                @Override
                public void startElement(String uri, String localName, String element, Attributes attributes) throws SAXException {
                    if (element.equalsIgnoreCase("oihistory")) {
                        oihistoryTag = "open";
                        oihistory = new Oihistory();
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
                    if (element.equalsIgnoreCase("oi")) {
                        oiTag = "open";
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
                        oihistory.setFacilityId(facilityId);
                    }
                    if (hospitalNumTag.equals("open")) {
                        hospitalNum = new String(chars, start, length);
                    }
                    if (dateVisitTag.equals("open")) {
                        dateVisit = new String(chars, start, length);
                        if (!dateVisit.trim().isEmpty()) {
                            oihistory.setDateVisit(DateUtil.parseStringToDate(dateVisit, "yyyy-MM-dd"));
                        } else {
                            skipRecord = true;
                        }
                    }
                    if (oiTag.equals("open")) {
                        oi = new String(chars, start, length);
                        oihistory.setOi(new String(chars, start, length));
                    }
                    if (timeStampTag.equals("open")) {
                        oihistory.setTimeStamp(new java.sql.Date(new java.util.Date().getTime()));
                    }
                    if (idUUIDTag.equals("open")) {
                        oihistory.setUuid(new String(chars, start, length));
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
                    if (element.equalsIgnoreCase("oi")) {
                        oiTag = "close";
                    }
                    if (element.equalsIgnoreCase("time_stamp")) {
                        timeStampTag = "close";
                    }
                    if (element.equalsIgnoreCase("id_uuid") || element.equalsIgnoreCase("uuid")) {
                        idUUIDTag = "close";
                    }
                    //if this is the closing tag of a oihistory element save the record
                    if (element.equalsIgnoreCase("oihistory")) {
                        oihistoryTag = "close";
                        if (skipRecord) {
                            System.out.println("....record has a null value: " + hospitalNum);
                        } else {
                            Long patientId = ServerIDProvider.getPatientServerId(hospitalNum, facilityId);
                            if (patientId != null) {
                                Patient patient = new Patient();
                                patient.setPatientId(patientId);
                                oihistory.setPatient(patient);
                                Long id = ServerIDProvider.getOIHistoryId(
                                        hospitalNum, oihistory.getDateVisit(), oihistory.getOi(), facilityId);
                                if (id != null) {
                                    oihistory.setHistoryId(id);
                                    OihistoryDAO.update(oihistory);
                                } else {
                                    OihistoryDAO.save(oihistory);
                                }
                            }
                        }

                    }
                }
            };

            //parse the XML specified in the given path and uses supplied handler to parse the document
            //this calls startElement(), endElement(), and character() methods accordingly
            saxParser.parse(xmlFileName, defaultHandler);
            // new CleanupService().cleanNullFields("oihistory", facilityId);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }

}
