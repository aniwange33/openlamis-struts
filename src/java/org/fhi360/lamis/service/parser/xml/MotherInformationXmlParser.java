/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.parser.xml;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.fhi360.lamis.dao.hibernate.MotherInformationDAO;
import org.fhi360.lamis.model.Facility;
import org.fhi360.lamis.model.Motherinformation;
import org.fhi360.lamis.service.ServerIDProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.PatientNumberNormalizer;
import org.fhi360.lamis.utility.Scrambler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author user10
 */
public class MotherInformationXmlParser extends DefaultHandler {

    private long facilityId;
    private long idOnServer;
    private String hospitalNum;
    private String referenceNum;
    private String hospitalNumMother;
    private Boolean populated;
    private Motherinformation motherInformation;
    private Facility facility = new Facility();
    private Scrambler scrambler = new Scrambler();

    public MotherInformationXmlParser() {
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
                String motherinformationTag;
                String motherinformationIdTag;
                String patientIdTag;
                String facilityIdTag;
                String hospitalNumTag;
                String uniqueIdTag;
                String surnameTag;
                String otherNamesTag;
                String dateConfirmedHivTag;
                String timeHivDiagnosisTag;
                String dateEnrolledPmtctTag;
                String dateStartedTag;
                String regimenTag;
                String addressTag;
                String phoneTag;
                String artStatusTag;
                String inFacilityTag;
                String timeStampTag;
                String userIdTag;
                String idUUIDTag = "close";

                //this method is called every time the parser gets an open tag '<'
                //identifies which tag is being open at the time by assigning an open flag
                @Override
                public void startElement(String uri, String localName, String element, Attributes attributes) throws SAXException {
                    if (element.equalsIgnoreCase("motherinformation")) {
                        motherinformationTag = "open";
                        motherInformation = new Motherinformation();
                    }
                    if (element.equalsIgnoreCase("motherinformation_id")) {
                        motherinformationIdTag = "open";
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
                    if (element.equalsIgnoreCase("unique_id")) {
                        uniqueIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("hospital_num")) {
                        hospitalNumTag = "open";
                    }
                    if (element.equalsIgnoreCase("surname")) {
                        surnameTag = "open";
                    }
                    if (element.equalsIgnoreCase("surname")) {
                        surnameTag = "open";
                    }
                    if (element.equalsIgnoreCase("other_names")) {
                        otherNamesTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_confirmed_hiv")) {
                        dateConfirmedHivTag = "open";
                    }
                    if (element.equalsIgnoreCase("time_hiv_diagnosis")) {
                        timeHivDiagnosisTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_enrolled_pmtct")) {
                        dateEnrolledPmtctTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_started")) {
                        dateStartedTag = "open";
                    }
                    if (element.equalsIgnoreCase("phone")) {
                        phoneTag = "open";
                    }
                    if (element.equalsIgnoreCase("in_facility")) {
                        inFacilityTag = "open";
                    }
                    if (element.equalsIgnoreCase("atr_status")) {
                        artStatusTag = "open";
                    }
                    if (element.equalsIgnoreCase("address")) {
                        addressTag = "open";
                    }
                    if (element.equalsIgnoreCase("regimen")) {
                        regimenTag = "open";
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
                        facility.setFacilityId(facilityId);
                        motherInformation.setFacility(facility);
                    }
                    if (motherinformationIdTag.equals("open")) {
                        String motherinformationId = new String(chars, start, length);
                        if (!motherinformationId.trim().isEmpty()) {
                            motherInformation.setMotherinformationId(Long.parseLong(motherinformationId));
                        }
                    }
                    if (patientIdTag.equals("open")) {
                        String patientId = new String(chars, start, length);
                        motherInformation.setPatientId(Long.parseLong(patientId));
                    }
                    if (hospitalNumTag.equals("open")) {
                        hospitalNum = new String(chars, start, length);
                        motherInformation.setHospitalNum(hospitalNum);
                    }
                    if (uniqueIdTag.equals("open")) {
                        motherInformation.setUniqueId(new String(chars, start, length));
                    }
                    if (surnameTag.equals("open")) {
                        motherInformation.setSurname(scrambler.scrambleCharacters(new String(chars, start, length)));
                    }
                    if (otherNamesTag.equals("open")) {
                        motherInformation.setOtherNames(scrambler.scrambleCharacters(new String(chars, start, length)));
                    }
                    if (timeHivDiagnosisTag.equals("open")) {
                        motherInformation.setTimeHivDiagnosis(new String(chars, start, length));
                    }

                    if (dateConfirmedHivTag.equals("open")) {
                        String dateConfirmedHiv = new String(chars, start, length);
                        if (!dateConfirmedHiv.trim().isEmpty()) {
                            motherInformation.setDateConfirmedHiv(DateUtil.parseStringToDate(dateConfirmedHiv, "yyyy-MM-dd"));
                        }
                    }

                    if (dateEnrolledPmtctTag.equals("open")) {
                        String dateEnrolledPmtct = new String(chars, start, length);
                        if (!dateEnrolledPmtct.trim().isEmpty()) {
                            motherInformation.setDateEnrolledPmtct(DateUtil.parseStringToDate(dateEnrolledPmtct, "yyyy-MM-dd"));
                        }
                    }

                    if (addressTag.equals("open")) {
                        motherInformation.setAddress(scrambler.scrambleCharacters(new String(chars, start, length)));
                    }

                    if (phoneTag.equals("open")) {
                        motherInformation.setPhone(scrambler.scrambleCharacters(new String(chars, start, length)));
                    }

                    if (dateStartedTag.equals("open")) {
                        String dateStarted = new String(chars, start, length);
                        if (!dateStarted.trim().isEmpty()) {
                            motherInformation.setDateStarted(DateUtil.parseStringToDate(dateStarted, "yyyy-MM-dd"));
                        }
                    }

                    if (regimenTag.equals("open")) {
                        motherInformation.setRegimen(new String(chars, start, length));
                    }
                    if (artStatusTag.equals("open")) {
                        motherInformation.setArtStatus(new String(chars, start, length));
                    }
                    if (inFacilityTag.equals("open")) {
                        motherInformation.setInFacility(new String(chars, start, length));
                    }
                    if (timeStampTag.equals("open")) {
                        motherInformation.setTimeStamp(new java.sql.Date(new java.util.Date().getTime()));
                    }
                    if (userIdTag.equals("open")) {
                        String userId = new String(chars, start, length);
                        if (!userId.trim().trim().isEmpty()) {
                            motherInformation.setUserId(Long.parseLong(userId));
                        }
                    }
                    if (idUUIDTag.equals("open")) {
                        motherInformation.setUuid(new String(chars, start, length));
                    }
                }

                @Override
                public void endElement(String uri, String localName, String element) throws SAXException {
                    if (element.equalsIgnoreCase("motherinformation_id")) {
                        motherinformationIdTag = "close";
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
                    if (element.equalsIgnoreCase("unique_id")) {
                        uniqueIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("hospital_num")) {
                        hospitalNumTag = "close";
                    }
                    if (element.equalsIgnoreCase("surname")) {
                        surnameTag = "close";
                    }
                    if (element.equalsIgnoreCase("surname")) {
                        surnameTag = "close";
                    }
                    if (element.equalsIgnoreCase("other_names")) {
                        otherNamesTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_confirmed_hiv")) {
                        dateConfirmedHivTag = "close";
                    }
                    if (element.equalsIgnoreCase("time_hiv_diagnosis")) {
                        timeHivDiagnosisTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_enrolled_pmtct")) {
                        dateEnrolledPmtctTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_started")) {
                        dateStartedTag = "close";
                    }
                    if (element.equalsIgnoreCase("phone")) {
                        phoneTag = "close";
                    }
                    if (element.equalsIgnoreCase("in_facility")) {
                        inFacilityTag = "close";
                    }
                    if (element.equalsIgnoreCase("atr_status")) {
                        artStatusTag = "close";
                    }
                    if (element.equalsIgnoreCase("address")) {
                        addressTag = "close";
                    }
                    if (element.equalsIgnoreCase("regimen")) {
                        regimenTag = "close";
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

                    //if this is the closing tag of a child element save the record
                    if (element.equalsIgnoreCase("motherinformation")) {
                        motherinformationTag = "close";
                        //Get mothers ID from the patient table
                         Long patientId = ServerIDProvider.getPatientServerId(hospitalNum, facilityId);
                            if (patientId != null) {
                                motherInformation.setPatientId(patientId);
                            }
                            Long id = ServerIDProvider.getMotherInformationId(hospitalNum, facilityId);
                            if (id != null) {
                                motherInformation.setMotherinformationId(id);
                                MotherInformationDAO.update(motherInformation);
                            } else {
                                MotherInformationDAO.save(motherInformation);
                            }
                    }
                }
            };

            //parse the XML specified in the given path and uses supplied handler to parse the document
            //this calls startElement(), endElement(), and character() methods accordingly
            saxParser.parse(xmlFileName, defaultHandler);
            //new CleanupService().cleanNullFields("motherinformation", facilityId);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }
}
