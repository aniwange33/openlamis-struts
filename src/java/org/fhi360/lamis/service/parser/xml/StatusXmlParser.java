/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.service.parser.xml;

import org.fhi360.lamis.dao.hibernate.StatushistoryDAO;
import org.fhi360.lamis.model.Statushistory;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.utility.PatientNumberNormalizer;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.fhi360.lamis.service.ServerIDProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class StatusXmlParser extends DefaultHandler {

    private long facilityId;
    private String hospitalNum;
    private String dateCurrentStatus;
    private String currentStatus;
    private long idOnServer;
    private Boolean populated;
    private Boolean skipRecord;
    private Statushistory statushistory;
    private Patient patient = new Patient();

    public StatusXmlParser() {
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
                String statushistoryTag = "close";
                String historyIdTag = "close";
                String patientIdTag = "close";
                String facilityIdTag = "close";
                String hospitalNumTag = "close";
                String currentStatusTag = "close";
                String dateCurrentStatusTag = "close";
                String dateTrackedTag = "close";
                String agreedDateTag = "close";
                String outcomeTag = "close";
                String reasonInterruptTag = "close";
                String causeDeathTag = "close";
                String timeStampTag = "close";
                String idUUIDTag = "close";

                //this method is called every time the parser gets an open tag '<'
                //identifies which tag is being open at the time by assigning an open flag
                @Override
                public void startElement(String uri, String localName, String element, Attributes attributes) throws SAXException {
                    if (element.equalsIgnoreCase("statushistory")) {
                        statushistoryTag = "open";
                        statushistory = new Statushistory();
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
                    if (element.equalsIgnoreCase("current_status")) {
                        currentStatusTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_current_status")) {
                        dateCurrentStatusTag = "open";
                    }
                    if (element.equalsIgnoreCase("reason_interrupt")) {
                        reasonInterruptTag = "open";
                    }
                    if (element.equalsIgnoreCase("cause_death")) {
                        causeDeathTag = "open";
                    }
                    if (element.equalsIgnoreCase("agreed_date")) {
                        agreedDateTag = "open";
                    }
                    if (element.equalsIgnoreCase("outcome")) {
                        outcomeTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_tracked")) {
                        dateTrackedTag = "open";
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
                        statushistory.setFacilityId(facilityId);
                    }
                    if (hospitalNumTag.equals("open")) {
                        hospitalNum = new String(chars, start, length);
                    }
                    if (currentStatusTag.equals("open")) {
                        currentStatus = new String(chars, start, length);
                        statushistory.setCurrentStatus(new String(chars, start, length));
                    }
                    if (dateCurrentStatusTag.equals("open")) {
                        dateCurrentStatus = new String(chars, start, length);
                        if (!dateCurrentStatus.trim().isEmpty()) {
                            statushistory.setDateCurrentStatus(DateUtil.parseStringToDate(dateCurrentStatus, "yyyy-MM-dd"));
                        } else {
                            skipRecord = true;
                        }
                    }
                    if (reasonInterruptTag.equals("open")) {
                        statushistory.setReasonInterrupt(new String(chars, start, length));
                    }
                    if (causeDeathTag.equals("open")) {
                        statushistory.setCauseDeath(new String(chars, start, length));
                    }
                    if (dateTrackedTag.equals("open")) {
                        String dateTracked = new String(chars, start, length);
                        if (!dateTracked.trim().isEmpty()) {
                            statushistory.setDateTracked(DateUtil.parseStringToDate(dateTracked, "yyyy-MM-dd"));
                        }
                    }
                    if (agreedDateTag.equals("open")) {
                        String agreedDate = new String(chars, start, length);
                        if (!agreedDate.trim().isEmpty()) {
                            statushistory.setAgreedDate(DateUtil.parseStringToDate(agreedDate, "yyyy-MM-dd"));
                        }
                    }
                    if (outcomeTag.equals("open")) {
                        statushistory.setOutcome(new String(chars, start, length));
                    }
                    if (timeStampTag.equals("open")) {
                        statushistory.setTimeStamp(new java.sql.Date(new java.util.Date().getTime()));
                    }
                    if (idUUIDTag.equals("open")) {
                        statushistory.setUuid(new String(chars, start, length));
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
                    if (element.equalsIgnoreCase("current_status")) {
                        currentStatusTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_current_status")) {
                        dateCurrentStatusTag = "close";
                    }
                    if (element.equalsIgnoreCase("reason_interrupt")) {
                        reasonInterruptTag = "close";
                    }
                    if (element.equalsIgnoreCase("cause_death")) {
                        causeDeathTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_tracked")) {
                        dateTrackedTag = "close";
                    }
                    if (element.equalsIgnoreCase("agreed_date")) {
                        agreedDateTag = "close";
                    }
                    if (element.equalsIgnoreCase("outcome")) {
                        outcomeTag = "close";
                    }
                    if (element.equalsIgnoreCase("time_stamp")) {
                        timeStampTag = "close";
                    }
                    if (element.equalsIgnoreCase("id_uuid") || element.equalsIgnoreCase("uuid")) {
                        idUUIDTag = "close";
                    }

                    //if this is the closing tag of a statushistory element save the record
                    if (element.equalsIgnoreCase("statushistory")) {
                        statushistoryTag = "close";
                        if (!skipRecord) {Long patientId = ServerIDProvider.getPatientServerId(hospitalNum, facilityId);
                            if (patientId != null) {
                                Patient patient = new Patient();
                                patient.setPatientId(patientId);
                                statushistory.setPatient(patient);
                                Long id = ServerIDProvider
                                        .getStatusHistoryId(hospitalNum, statushistory.getDateCurrentStatus(),
                                                currentStatus, facilityId);
                                if (id != null) {
                                    statushistory.setHistoryId(id);
                                    StatushistoryDAO.update(statushistory);
                                } else {
                                    try {
                                        StatushistoryDAO.save(statushistory);
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
            //new CleanupService().cleanNullFields("statushistory", facilityId);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }

}
