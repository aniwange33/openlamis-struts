/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.service.parser.xml;

import org.fhi360.lamis.dao.hibernate.AdrhistoryDAO;
import org.fhi360.lamis.model.Adrhistory;
import org.fhi360.lamis.model.Patient;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.fhi360.lamis.service.ServerIDProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AdrXmlParser extends DefaultHandler{
    private long facilityId;
    private String hospitalNum;
    private String dateVisit;
    private String adr;
    private String screener;
    private Boolean skipRecord;    
    private Adrhistory adrhistory;

    public AdrXmlParser() {
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
                String adrhistoryTag = "close";
                String historyIdTag = "close";
                String patientIdTag = "close";
                String facilityIdTag = "close";
                String hospitalNumTag = "close";
                String dateVisitTag = "close";
                String adrTag = "close";
                String severityTag = "close";
                String screenerTag = "close";
                String timeStampTag = "close";
                String idUUIDTag = "close";
                               
                //this method is called every time the parser gets an open tag '<'
                //identifies which tag is being open at the time by assigning an open flag
                @Override
                public void startElement(String uri, String localName, String element, Attributes attributes) throws SAXException {
                    if (element.equalsIgnoreCase("adrhistory")) {
                        adrhistoryTag = "open";
                        adrhistory = new Adrhistory();
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
                    if (element.equalsIgnoreCase("adr")) {
                        adrTag = "open";
                    }
                    if (element.equalsIgnoreCase("severity")) {
                        severityTag = "open";
                    }
                    if (element.equalsIgnoreCase("screener")) {
                        screenerTag = "open";
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
                        adrhistory.setFacilityId(facilityId);           
                    }                    
                    if (hospitalNumTag.equals("open")) {
                        hospitalNum = new String(chars, start, length);
                    }                    
                    if (dateVisitTag.equals("open")) {
                        dateVisit = new String(chars, start, length);
                        if(!dateVisit.trim().isEmpty()) {
                            adrhistory.setDateVisit(DateUtil.parseStringToDate(dateVisit, "yyyy-MM-dd"));
                        }
                        else {
                            skipRecord = true;
                        }
                    }
                    if (adrTag.equals("open")) {
                        adr = new String(chars, start, length);
                        adrhistory.setAdr(adr);
                    }                    
                    if (severityTag.equals("open")) {
                        String severity = new String(chars, start, length);
                        if(!severity.trim().isEmpty()) adrhistory.setSeverity(Integer.parseInt(severity));
                    }                    
                    if (screenerTag.equals("open")) {
                        screener = new String(chars, start, length);
                        if(!screener.trim().isEmpty()) adrhistory.setScreener(Integer.parseInt(screener));
                    }                    
                    if (timeStampTag.equals("open")) {
                        adrhistory.setTimeStamp(new java.sql.Date(new java.util.Date().getTime()));
                    }  
                    if (idUUIDTag.equals("open")) {
                        adrhistory.setUuid(new String(chars, start, length));
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
                    if (element.equalsIgnoreCase("adr")) {
                        adrTag = "close";
                    }
                    if (element.equalsIgnoreCase("severity")) {
                        severityTag = "close";
                    }
                    if (element.equalsIgnoreCase("screener")) {
                        screenerTag = "close";
                    }
                    if (element.equalsIgnoreCase("time_stamp")) {
                        timeStampTag = "close";
                    }
                    if (element.equalsIgnoreCase("id_uuid") || element.equalsIgnoreCase("uuid")) {
                        idUUIDTag = "close";
                    }
                    
                    //if this is the closing tag of a patient element save the record
                    if (element.equalsIgnoreCase("adrhistory")) {
                        adrhistoryTag = "close";
                        if(!skipRecord) { Long patientId = ServerIDProvider.getPatientServerId(hospitalNum, facilityId);
                            if (patientId != null) {
                                Patient patient = new Patient();
                                patient.setPatientId(patientId);
                                adrhistory.setPatient(patient);
                                Long id = ServerIDProvider
                                        .getPatientDependantId("adrhistory", hospitalNum,
                                                adrhistory.getDateVisit(), facilityId);
                                if (id != null) {
                                    adrhistory.setHistoryId(id);
                                    AdrhistoryDAO.update(adrhistory);
                                } else {
                                    AdrhistoryDAO.save(adrhistory);
                                }
                            }                    
                        }
                        
                    }                   
                }
            }; 
        
            //parse the XML specified in the given path and uses supplied handler to parse the document
            //this calls startElement(), endElement(), and character() methods accordingly
            saxParser.parse(xmlFileName, defaultHandler);
        }
        catch(Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }
    
}

