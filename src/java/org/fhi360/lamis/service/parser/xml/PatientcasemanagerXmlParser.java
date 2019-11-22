/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.parser.xml;

import java.io.Serializable;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.fhi360.lamis.dao.hibernate.PatientCaseManagerDAO;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.model.Patientcasemanager;
import org.fhi360.lamis.service.ServerIDProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.PatientNumberNormalizer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author user10
 */
public class PatientcasemanagerXmlParser implements Serializable {

    private long facilityId;
    private Long idOnServer;
    private String hospitalNum;
    private String dateAssigned;
    private Long casemanagerId;
    private Boolean populated;
    private Boolean skipRecord;
    private Patientcasemanager patientCaseManager;
    private Patient patient = new Patient();

    public PatientcasemanagerXmlParser() {
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
                String patientCaseManagerTag = "close";
                String patientCaseManagerIdTag = "close";
                String caseManagerIdTag = "close";
                String facilityIdTag = "close";
                String hospitalNumTag = "close";
                String dateAssignedTag = "close";
                String actionTag = "close";
                String timeStampTag = "close";
                String userIdTag = "close";
                String idUUIDTag = "close";

                //this method is called every time the parser gets an open tag '<'
                //identifies which tag is being open at the time by assigning an open flag
                @Override
                public void startElement(String uri, String localName, String element, Attributes attributes) throws SAXException {
                    if (element.equalsIgnoreCase("patientcasemanager")) {
                        patientCaseManagerTag = "open";
                        patientCaseManager = new Patientcasemanager();
                    }
                    if (element.equalsIgnoreCase("patientcasemanager_id")) {
                        patientCaseManagerIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("casemanager_id")) {
                        caseManagerIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("hospital_num")) {
                        hospitalNumTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_assigned")) {
                        dateAssignedTag = "open";
                    }
                    if (element.equalsIgnoreCase("action")) {
                        actionTag = "open";
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
                        patientCaseManager.setFacilityId(facilityId);
                    }
                    if (hospitalNumTag.equals("open")) {
                        hospitalNum = new String(chars, start, length);
                    }
                    if (caseManagerIdTag.equals("open")) {
                        casemanagerId = Long.parseLong(new String(chars, start, length));
                    }
                    if (dateAssignedTag.equals("open")) {
                        dateAssigned = new String(chars, start, length);
                        if (!dateAssigned.trim().isEmpty()) {
                            patientCaseManager.setDateAssigned(DateUtil.parseStringToDate(dateAssigned, "yyyy-MM-dd"));
                        } else {
                            skipRecord = true;
                        }
                    }
                    if (actionTag.equals("open")) {
                        patientCaseManager.setAction(new String(chars, start, length));
                    }
                    if (timeStampTag.equals("open")) {
                        patientCaseManager.setTimeStamp(new java.sql.Date(new java.util.Date().getTime()));
                    }
                    if (userIdTag.equals("open")) {
                        String userId = new String(chars, start, length);
                        if (!userId.trim().trim().isEmpty()) {
                            patientCaseManager.setUserId(Integer.parseInt(userId));
                        }
                    }
                    if (idUUIDTag.equals("open")) {
                        patientCaseManager.setUuid(new String(chars, start, length));
                    }
                }

                @Override
                public void endElement(String uri, String localName, String element) throws SAXException {
                    if (element.equalsIgnoreCase("patientcasemanager")) {
                        patientCaseManagerTag = "close";
                    }
                    if (element.equalsIgnoreCase("patientcasemanager_id")) {
                        patientCaseManagerIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("casemanager_id")) {
                        caseManagerIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("hospital_num")) {
                        hospitalNumTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_assigned")) {
                        dateAssignedTag = "close";
                    }
                    if (element.equalsIgnoreCase("action")) {
                        actionTag = "close";
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

                    //if this is the closing tag of a clinic element save the record
                    if (element.equalsIgnoreCase("patientcasemanager")) {
                        patientCaseManagerIdTag = "close";
                        if (skipRecord) {
                            System.out.println("....record has a null value: " + hospitalNum);
                        } else {
                            //Using the hospital number of this patient that was waived into the clinic xml, get the ID of this patient on the server
                            //If the hospital number does not exist then the patient does not exist. If the patient exist retrieve the ID and use it to form the patient object
                            //Then set the patient object on the clinic object

                            Long patientId = ServerIDProvider.getPatientServerId(hospitalNum, facilityId);
                            if (patientId != null) {
                                Patient patient = new Patient();
                                patient.setPatientId(patientId);
                                patientCaseManager.setPatient(patient);
                                try {
                                    Long caseManagerId = ServerIDProvider.getCaseManagerId(
                                            Long.parseLong(facilityId + "" + patientCaseManager.getCasemanagerId()),
                                            facilityId
                                    );
                                    if (caseManagerId != null) {
                                        patientCaseManager.setCasemanagerId(caseManagerId);
                                    }
                                }catch (Exception ignored){}
                                Long id = ServerIDProvider
                                        .getPatientDependantId("patientcasemanager", hospitalNum,
                                                patientCaseManager.getDateAssigned(), facilityId);
                                if (id != null) {
                                    patientCaseManager.setPatientcasemanagerId(id);
                                    PatientCaseManagerDAO.update(patientCaseManager);
                                } else {
                                    PatientCaseManagerDAO.save(patientCaseManager);
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
