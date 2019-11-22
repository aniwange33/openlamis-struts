/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.service.parser.xml;

import org.fhi360.lamis.dao.hibernate.RegimenhistoryDAO;
import org.fhi360.lamis.model.Regimenhistory;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.utility.PatientNumberNormalizer;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.fhi360.lamis.service.ServerIDProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RegimenXmlParser extends DefaultHandler {

    private long facilityId;
    private String hospitalNum;
    private String dateVisit;
    private String regimentype;
    private String regimen;
    private String reasonSwitchedSubs;
    private long idOnServer;
    private Boolean populated;
    private Boolean skipRecord;
    private Regimenhistory regimenhistory;
    private Patient patient = new Patient();

    public RegimenXmlParser() {
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
                String regimenhistoryTag = "close";
                String historyIdTag = "close";
                String patientIdTag = "close";
                String facilityIdTag = "close";
                String hospitalNumTag = "close";
                String dateVisitTag = "close";
                String regimentypeTag = "close";
                String regimenTag = "close";
                String reasonSwitchedSubsTag = "close";
                String timeStampTag = "close";
                String idUUIDTag = "close";

                //this method is called every time the parser gets an open tag '<'
                //identifies which tag is being open at the time by assigning an open flag
                @Override
                public void startElement(String uri, String localName, String element, Attributes attributes) throws SAXException {
                    if (element.equalsIgnoreCase("regimenhistory")) {
                        regimenhistoryTag = "open";
                        regimenhistory = new Regimenhistory();
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
                    if (element.equalsIgnoreCase("regimentype")) {
                        regimentypeTag = "open";
                    }
                    if (element.equalsIgnoreCase("regimen")) {
                        regimenTag = "open";
                    }
                    if (element.equalsIgnoreCase("reasonSwitchedSubs")) {
                        reasonSwitchedSubsTag = "open";
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
                        regimenhistory.setFacilityId(facilityId);
                    }
                    if (hospitalNumTag.equals("open")) {
                        hospitalNum = new String(chars, start, length);
                    }
                    if (dateVisitTag.equals("open")) {
                        dateVisit = new String(chars, start, length);
                        if (!dateVisit.trim().isEmpty()) {
                            regimenhistory.setDateVisit(DateUtil.parseStringToDate(dateVisit, "yyyy-MM-dd"));
                        } else {
                            skipRecord = true;
                        }
                    }
                    if (regimentypeTag.equals("open")) {
                        regimentype = new String(chars, start, length);
                        regimenhistory.setRegimentype(regimentype);
                    }
                    if (regimenTag.equals("open")) {
                        regimen = new String(chars, start, length);
                        regimenhistory.setRegimen(regimen);
                    }
                    if (reasonSwitchedSubsTag.equals("open")) {
                        reasonSwitchedSubs = new String(chars, start, length);
                        regimenhistory.setReasonSwitchedSubs(reasonSwitchedSubs);
                    }
                    if (timeStampTag.equals("open")) {
                        regimenhistory.setTimeStamp(new java.sql.Date(new java.util.Date().getTime()));
                    }
                    if (idUUIDTag.equals("open")) {
                        regimenhistory.setUuid(new String(chars, start, length));
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
                    if (element.equalsIgnoreCase("regimentype")) {
                        regimentypeTag = "close";
                    }
                    if (element.equalsIgnoreCase("regimen")) {
                        regimenTag = "close";
                    }
                    if (element.equalsIgnoreCase("reasonSwitchedSubs")) {
                        reasonSwitchedSubsTag = "close";
                    }
                    if (element.equalsIgnoreCase("time_stamp")) {
                        timeStampTag = "close";
                    }
                    if (element.equalsIgnoreCase("id_uuid") || element.equalsIgnoreCase("uuid")) {
                        idUUIDTag = "close";
                    }

                    //if this is the closing tag of a regimenhistory element save the record
                    if (element.equalsIgnoreCase("regimenhistory")) {
                        regimenhistoryTag = "close";
                        if (!skipRecord) {Long patientId = ServerIDProvider.getPatientServerId(hospitalNum, facilityId);
                            if (patientId != null) {
                                Patient patient = new Patient();
                                patient.setPatientId(patientId);
                                regimenhistory.setPatient(patient);
                                Long id = ServerIDProvider.getRegimenId(
                                        hospitalNum, regimenhistory.getDateVisit(), regimentype,
                                        regimen, facilityId
                                );
                                if (id != null) {
                                    regimenhistory.setHistoryId(id);
                                    RegimenhistoryDAO.update(regimenhistory);
                                } else {
                                    try {
                                        RegimenhistoryDAO.save(regimenhistory);
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
            //new CleanupService().cleanNullFields("regimenhistory", facilityId);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }

}
