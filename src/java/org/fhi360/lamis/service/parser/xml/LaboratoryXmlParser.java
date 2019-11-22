/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.service.parser.xml;

import org.fhi360.lamis.dao.hibernate.LaboratoryDAO;
import org.fhi360.lamis.model.Laboratory;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.utility.PatientNumberNormalizer;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.fhi360.lamis.service.ServerIDProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class LaboratoryXmlParser extends DefaultHandler {

    private long facilityId;
    private String hospitalNum;
    private String dateReported;
    private String labtestId;
    private long idOnServer;
    private Boolean populated;
    private Boolean skipRecord;
    private Laboratory laboratory;
    private Patient patient = new Patient();

    public LaboratoryXmlParser() {
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
                String laboratoryTag = "close";
                String laboratoryIdTag = "close";
                String patientIdTag = "close";
                String facilityIdTag = "close";
                String hospitalNumTag = "close";
                String dateReportedTag = "close";
                String dateCollectedTag = "close";
                String labnoTag = "close";
                String resultabTag = "close";
                String resultpcTag = "close";
                String commentTag = "close";
                String labtestIdTag = "close";
                String timeStampTag = "close";
                String userIdTag = "close";
                String idUUIDTag = "close";

                //this method is called every time the parser gets an open tag '<'
                //identifies which tag is being open at the time by assigning an open flag
                @Override
                public void startElement(String uri, String localName, String element, Attributes attributes) throws SAXException {
                    if (element.equalsIgnoreCase("laboratory")) {
                        laboratoryTag = "open";
                        laboratory = new Laboratory();
                    }
                    if (element.equalsIgnoreCase("laboratory_id")) {
                        laboratoryIdTag = "open";
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
                    if (element.equalsIgnoreCase("date_reported")) {
                        dateReportedTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_collected")) {
                        dateCollectedTag = "open";
                    }
                    if (element.equalsIgnoreCase("labno")) {
                        labnoTag = "open";
                    }
                    if (element.equalsIgnoreCase("resultab")) {
                        resultabTag = "open";
                    }
                    if (element.equalsIgnoreCase("resultpc")) {
                        resultpcTag = "open";
                    }
                    if (element.equalsIgnoreCase("comment")) {
                        commentTag = "open";
                    }
                    if (element.equalsIgnoreCase("labtest_id")) {
                        labtestIdTag = "open";
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
                        laboratory.setFacilityId(facilityId);
                    }
                    if (hospitalNumTag.equals("open")) {
                        hospitalNum = new String(chars, start, length);
                    }
                    if (dateReportedTag.equals("open")) {
                        dateReported = new String(chars, start, length);
                        if (!dateReported.trim().isEmpty()) {
                            laboratory.setDateReported(DateUtil.parseStringToDate(dateReported, "yyyy-MM-dd"));
                        } else {
                            skipRecord = true;
                        }
                    }
                    if (dateCollectedTag.equals("open")) {
                        String dateCollected = new String(chars, start, length);
                        if (!dateCollected.trim().isEmpty()) {
                            laboratory.setDateCollected(DateUtil.parseStringToDate(dateCollected, "yyyy-MM-dd"));
                        }
                    }
                    if (labnoTag.equals("open")) {
                        laboratory.setLabno(new String(chars, start, length));
                    }
                    if (resultabTag.equals("open")) {
                        laboratory.setResultab(new String(chars, start, length));
                    }
                    if (resultpcTag.equals("open")) {
                        laboratory.setResultpc(new String(chars, start, length));
                    }
                    if (commentTag.equals("open")) {
                        laboratory.setComment(new String(chars, start, length));
                    }
                    if (labtestIdTag.equals("open")) {
                        labtestId = new String(chars, start, length);
                        if (!labtestId.trim().isEmpty()) {
                            laboratory.setLabtestId(Integer.parseInt(labtestId));
                        }
                    }
                    if (timeStampTag.equals("open")) {
                        laboratory.setTimeStamp(new java.sql.Date(new java.util.Date().getTime()));
                    }
                    if (userIdTag.equals("open")) {
                        String userId = new String(chars, start, length);
                        if (!userId.trim().isEmpty()) {
                            laboratory.setUserId(Long.parseLong(userId));
                        }
                    }
                    if (idUUIDTag.equals("open")) {
                        laboratory.setUuid(new String(chars, start, length));
                    }
                }

                @Override
                public void endElement(String uri, String localName, String element) throws SAXException {
                    if (element.equalsIgnoreCase("laboratory_id")) {
                        laboratoryIdTag = "close";
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
                    if (element.equalsIgnoreCase("date_reported")) {
                        dateReportedTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_collected")) {
                        dateCollectedTag = "close";
                    }
                    if (element.equalsIgnoreCase("labno")) {
                        labnoTag = "close";
                    }
                    if (element.equalsIgnoreCase("resultab")) {
                        resultabTag = "close";
                    }
                    if (element.equalsIgnoreCase("resultpc")) {
                        resultpcTag = "close";
                    }
                    if (element.equalsIgnoreCase("comment")) {
                        commentTag = "close";
                    }
                    if (element.equalsIgnoreCase("labtest_id")) {
                        labtestIdTag = "close";
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

                    //if this is the closing tag of a patient element save the record
                    if (element.equalsIgnoreCase("laboratory")) {
                        laboratoryTag = "close";
                        if (!skipRecord) {Long patientId = ServerIDProvider.getPatientServerId(hospitalNum, facilityId);
                            if (patientId != null) {
                                Patient patient = new Patient();
                                patient.setPatientId(patientId);
                                laboratory.setPatient(patient);
                                Long id = ServerIDProvider
                                        .getLaboratoryId(hospitalNum, laboratory.getDateReported(),
                                                Long.parseLong(labtestId), facilityId);
                                if (id != null) {
                                    laboratory.setLaboratoryId(id);
                                    LaboratoryDAO.update(laboratory);
                                } else {
                                    try {
                                        LaboratoryDAO.save(laboratory);
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
            //new CleanupService().cleanNullFields("laboratory", facilityId);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }

}
