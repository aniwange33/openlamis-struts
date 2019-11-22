/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.parser;

import org.fhi360.lamis.dao.hibernate.EncounterDAO;
import org.fhi360.lamis.model.Encounter;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.utility.DateUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * @author user10
 */
public class EncounterXmlParser extends DefaultHandler {

    public EncounterXmlParser() {
    }
    
    public void parseXml(String xmlFileName) {
        final Encounter[] encounter = new Encounter[]{null};
        try {
            //obtain and configure a SAX based parser
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

            //obtain object for SAX hadler class
            SAXParser saxParser = saxParserFactory.newSAXParser();
            DefaultHandler defaultHandler = new DefaultHandler() {
                private String encounterTag = "close";
                private String encounterIdTag = "close";
                private String patientIdTag = "close";
                private String facilityIdTag = "close";
                private String communitypharmIdTag = "close";
                private String dateVisitTag = "close";
                private String question1Tag = "close";
                private String question2Tag = "close";
                private String question3Tag = "close";
                private String question4Tag = "close";
                private String question5Tag = "close";
                private String question6Tag = "close";
                private String question7Tag = "close";
                private String question8Tag = "close";
                private String question9Tag = "close";
                private String regimen1Tag = "close";
                private String regimen2Tag = "close";
                private String regimen3Tag = "close";
                private String regimen4Tag = "close";
                private String duration1Tag = "close";
                private String duration2Tag = "close";
                private String duration3Tag = "close";
                private String duration4Tag = "close";
                private String prescribed1Tag = "close";
                private String prescribed2Tag = "close";
                private String prescribed3Tag = "close";
                private String prescribed4Tag = "close";
                private String dispensed1Tag = "close";
                private String dispensed2Tag = "close";
                private String dispensed3Tag = "close";
                private String dispensed4Tag = "close";
                private String notesTag = "close";
                private String nextRefillTag = "close";
                private String regimentypeTag = "close";
                private String timeStampTag = "close";
                private String uploadedTag = "close";
                private String timeUploadedTag = "close";
                private String idUUIDTag = "close";
                String deviceconfigIdTag = "close";


                @Override
                public void startElement(String uri, String localName, String element, Attributes attributes) throws SAXException {
                    if (element.equalsIgnoreCase("encounter")) {
                        encounterTag = "open";
                        encounter[0] = new Encounter();
                    }
                    if (element.equalsIgnoreCase("encounter_id")) {
                        encounterIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "open";
                    }

                    if (element.equalsIgnoreCase("communitypharm_id")) {
                        communitypharmIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("patient_id")) {
                        patientIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_visit")) {
                        dateVisitTag = "open";
                    }
                    if (element.equalsIgnoreCase("question1")) {
                        question1Tag = "open";
                    }
                    if (element.equalsIgnoreCase("question2")) {
                        question2Tag = "open";
                    }
                    if (element.equalsIgnoreCase("question3")) {
                        question3Tag = "open";
                    }
                    if (element.equalsIgnoreCase("question4")) {
                        question4Tag = "open";
                    }
                    if (element.equalsIgnoreCase("question5")) {
                        question5Tag = "open";
                    }
                    if (element.equalsIgnoreCase("question6")) {
                        question6Tag = "open";
                    }
                    if (element.equalsIgnoreCase("question7")) {
                        question7Tag = "open";
                    }
                    if (element.equalsIgnoreCase("question8")) {
                        question8Tag = "open";
                    }
                    if (element.equalsIgnoreCase("question9")) {
                        question9Tag = "open";
                    }

                    if (element.equalsIgnoreCase("regimen1")) {
                        regimen1Tag = "open";
                    }
                    if (element.equalsIgnoreCase("regimen2")) {
                        regimen2Tag = "open";
                    }
                    if (element.equalsIgnoreCase("regimen3")) {
                        regimen3Tag = "open";
                    }
                    if (element.equalsIgnoreCase("regimen4")) {
                        regimen4Tag = "open";
                    }
                    if (element.equalsIgnoreCase("duration1")) {
                        duration1Tag = "open";
                    }
                    if (element.equalsIgnoreCase("duration2")) {
                        duration2Tag = "open";
                    }
                    if (element.equalsIgnoreCase("duration3")) {
                        duration3Tag = "open";
                    }
                    if (element.equalsIgnoreCase("duration4")) {
                        duration4Tag = "open";
                    }

                    if (element.equalsIgnoreCase("prescribed1")) {
                        prescribed1Tag = "open";
                    }

                    if (element.equalsIgnoreCase(" prescribed2")) {
                        prescribed2Tag = "open";
                    }

                    if (element.equalsIgnoreCase("prescribed3")) {
                        prescribed3Tag = "open";
                    }

                    if (element.equalsIgnoreCase("prescribed4")) {
                        prescribed4Tag = "open";
                    }

                    if (element.equalsIgnoreCase("duration4")) {
                        dispensed1Tag = "open";
                    }

                    if (element.equalsIgnoreCase("dispensed2")) {
                        dispensed2Tag = "open";
                    }

                    if (element.equalsIgnoreCase("dispensed3")) {
                        dispensed3Tag = "open";
                    }

                    if (element.equalsIgnoreCase("dispensed4")) {
                        dispensed4Tag = "open";
                    }

                    if (element.equalsIgnoreCase("notes")) {
                        notesTag = "open";
                    }

                    if (element.equalsIgnoreCase("next_refill")) {
                        nextRefillTag = "open";
                    }

                    if (element.equalsIgnoreCase("regimentype")) {
                        regimentypeTag = "open";
                    }
                    if (element.equalsIgnoreCase("time_stamp")) {
                        timeStampTag = "open";
                    }


                    if (element.equalsIgnoreCase("uploaded")) {
                        uploadedTag = "open";
                    }

                    if (element.equalsIgnoreCase("time_uploaded")) {
                        timeUploadedTag = "open";
                    }
                    if (element.equalsIgnoreCase("id_uuid")) {
                        idUUIDTag = "open";
                    }

                    if (element.equalsIgnoreCase("deviceconfig_id")) {
                        deviceconfigIdTag = "open";
                    }


                }

                @Override
                public void characters(char[] chars, int start, int length) throws SAXException {

                    if (encounterIdTag.equals("open")) {
                        String id = new String(chars, start, length);
                        encounter[0].setEncounterId(Long.parseLong(id));
                    }

                    if (facilityIdTag.equals("open")) {
                        encounter[0].setFacilityId(Long.parseLong(new String(chars, start, length)));
                    }

                    if (communitypharmIdTag.equals("open")) {
                        encounter[0].setCommunitypharmId(Long.parseLong(new String(chars, start, length)));
                    }
                    if (dateVisitTag.equals("open")) {
                        dateVisitTag = "open";
                        String date = new String(chars, start, length);
                        encounter[0].setDateVisit(DateUtil.parseStringToDate(date, "yyyy-MM-dd"));
                    }
                    if (question1Tag.equals("open")) {
                        encounter[0].setQuestion1(new String(chars, start, length));
                    }
                    if (question2Tag.equals("open")) {
                        encounter[0].setQuestion2(new String(chars, start, length));
                    }
                    if (question3Tag.equals("open")) {
                        encounter[0].setQuestion3(new String(chars, start, length));
                    }
                    if (question4Tag.equals("open")) {
                        encounter[0].setQuestion4(new String(chars, start, length));
                    }
                    if (question5Tag.equals("open")) {
                        encounter[0].setQuestion5(new String(chars, start, length));
                    }
                    if (question6Tag.equals("open")) {
                        encounter[0].setQuestion6(new String(chars, start, length));
                    }
                    if (question7Tag.equals("open")) {
                        encounter[0].setQuestion7(new String(chars, start, length));
                    }
                    if (question8Tag.equals("open")) {
                        encounter[0].setQuestion8(new String(chars, start, length));
                    }
                    if (question9Tag.equals("open")) {
                        encounter[0].setQuestion9(new String(chars, start, length));
                    }
                    if (regimen1Tag.equals("open")) {
                        encounter[0].setRegimen1(new String(chars, start, length));
                    }
                    if (regimen2Tag.equals("open")) {
                        encounter[0].setRegimen2(new String(chars, start, length));
                    }
                    if (regimen3Tag.equals("open")) {
                        encounter[0].setRegimen3(new String(chars, start, length));
                    }
                    if (regimen4Tag.equals("open")) {
                        encounter[0].setRegimen4(new String(chars, start, length));
                    }
                    if (duration1Tag.equals("open")) {
                        encounter[0].setDuration1(Integer.parseInt(new String(chars, start, length)));
                    }
                    if (duration2Tag.equals("open")) {
                        encounter[0].setDuration2(Integer.parseInt(new String(chars, start, length)));
                    }
                    if (duration3Tag.equals("open")) {
                        encounter[0].setDuration3(Integer.parseInt(new String(chars, start, length)));
                    }
                    if (duration4Tag.equals("open")) {
                        encounter[0].setDuration4(Integer.parseInt(new String(chars, start, length)));
                    }
                    if (prescribed1Tag.equals("open")) {
                        encounter[0].setPrescribed1(Integer.parseInt(new String(chars, start, length)));
                    }
                    if (prescribed2Tag.equals("open")) {
                        encounter[0].setPrescribed2(Integer.parseInt(new String(chars, start, length)));
                    }
                    if (prescribed3Tag.equals("open")) {
                        encounter[0].setPrescribed3(Integer.parseInt(new String(chars, start, length)));
                    }
                    if (prescribed4Tag.equals("open")) {
                        encounter[0].setPrescribed4(Integer.parseInt(new String(chars, start, length)));
                    }
                    if (dispensed1Tag.equals("open")) {
                        encounter[0].setDispensed1(Integer.parseInt(new String(chars, start, length)));
                    }
                    if (dispensed2Tag.equals("open")) {
                        encounter[0].setDispensed2(Integer.parseInt(new String(chars, start, length)));
                    }
                    if (dispensed3Tag.equals("open")) {
                        encounter[0].setDispensed3(Integer.parseInt(new String(chars, start, length)));
                    }
                    if (dispensed4Tag.equals("open")) {
                        encounter[0].setDispensed4(Integer.parseInt(new String(chars, start, length)));
                    }
                    if (notesTag.equals("open")) {
                        encounter[0].setNotes(new String(chars, start, length));
                    }
                    if (nextRefillTag.equals("open")) {
                        encounter[0].setNextRefill(DateUtil.parseStringToDate(new String(chars, start, length), "yyyy-MM-dd"));
                    }
                    if (regimentypeTag.equals("open")) {
                        encounter[0].setRegimentype(new String(chars, start, length));
                    }
                    if (timeStampTag.equals("open")) {
                        encounter[0].setTimeStamp(new java.sql.Date(new java.util.Date().getTime()));
                    }
                    if (uploadedTag.equals("open")) {
                        encounter[0].setUploaded(Integer.parseInt(new String(chars, start, length)));
                    }
                    if (timeUploadedTag.equals("open")) {
                        encounter[0].setTimeUploaded(new java.sql.Date(new java.util.Date().getTime()));
                    }
                    if (idUUIDTag.equalsIgnoreCase("open")) {
                        encounter[0].setUuid(new String(chars, start, length));
                    }
                    if (encounterTag.equals("open")) {
                        String id = new String(chars, start, length);
                        encounter[0].setEncounterId(Long.parseLong(id));
                    }
                    if (patientIdTag.equals("open")) {
                        String id = new String(chars, start, length);
                        Patient patient = new Patient();
                        patient.setPatientId(Long.parseLong(id));
                        encounter[0].setPatient(patient);
                    }
                    if (facilityIdTag.equals("open")) {

                        encounter[0].setFacilityId(Long.parseLong(new String(chars, start, length)));
                    }
                    if (dateVisitTag.equals("open")) {
                        String dateNextVisit = new String(chars, start, length);
                        if (!dateNextVisit.trim().isEmpty()) {
                            encounter[0].setDateVisit(DateUtil.parseStringToDate(dateNextVisit, "yyyy-MM-dd"));
                        }
                    }
                    if (regimentypeTag.equals("open")) {
                        encounter[0].setRegimentype(new String(chars, start, length));
                    }
                    if (regimen1Tag.equals("open")) {

                        encounter[0].setRegimen1(new String(chars, start, length));
                    }
                    if (regimen2Tag.equals("open")) {

                        encounter[0].setRegimen2(new String(chars, start, length));
                    }
                    if (regimen3Tag.equals("open")) {
                        encounter[0].setRegimen3(new String(chars, start, length));
                    }
                    if (regimen4Tag.equals("open")) {

                        encounter[0].setRegimen4(new String(chars, start, length));
                    }
                    if (duration1Tag.equals("open")) {

                        encounter[0].setDuration1(Integer.parseInt(new String(chars, start, length)));
                    }
                    if (duration2Tag.equals("open")) {
                        encounter[0].setDuration2(Integer.parseInt(new String(chars, start, length)));
                    }
                    if (duration3Tag.equals("open")) {

                        encounter[0].setDuration3(Integer.parseInt(new String(chars, start, length)));
                    }
                    if (duration4Tag.equals("open")) {

                        encounter[0].setDuration4(Integer.parseInt(new String(chars, start, length)));
                    }
                    if (deviceconfigIdTag.equals("open")) {
                        encounter[0].setDeviceconfigId(Long.parseLong(new String(chars, start, length)));
                    }

                }

                @Override
                public void endElement(String uri, String localName, String element) throws SAXException {


                    if (element.equalsIgnoreCase("encounter_id")) {
                        encounterIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "close";
                    }

                    if (element.equalsIgnoreCase("communitypharm_id")) {
                        communitypharmIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("patient_id")) {
                        patientIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_visit")) {
                        dateVisitTag = "close";
                    }
                    if (element.equalsIgnoreCase("question1")) {
                        question1Tag = "close";
                    }
                    if (element.equalsIgnoreCase("question2")) {
                        question2Tag = "close";
                    }
                    if (element.equalsIgnoreCase("question3")) {
                        question3Tag = "close";
                    }
                    if (element.equalsIgnoreCase("question4")) {
                        question4Tag = "close";
                    }
                    if (element.equalsIgnoreCase("question5")) {
                        question5Tag = "close";
                    }
                    if (element.equalsIgnoreCase("question6")) {
                        question6Tag = "close";
                    }
                    if (element.equalsIgnoreCase("question7")) {
                        question7Tag = "close";
                    }
                    if (element.equalsIgnoreCase("question8")) {
                        question8Tag = "close";
                    }
                    if (element.equalsIgnoreCase("question9")) {
                        question9Tag = "close";
                    }

                    if (element.equalsIgnoreCase("regimen1")) {
                        regimen1Tag = "close";
                    }
                    if (element.equalsIgnoreCase("regimen2")) {
                        regimen2Tag = "close";
                    }
                    if (element.equalsIgnoreCase("regimen3")) {
                        regimen3Tag = "close";
                    }
                    if (element.equalsIgnoreCase("regimen4")) {
                        regimen4Tag = "close";
                    }
                    if (element.equalsIgnoreCase("duration1")) {
                        duration1Tag = "close";
                    }
                    if (element.equalsIgnoreCase("duration2")) {
                        duration2Tag = "close";
                    }
                    if (element.equalsIgnoreCase("duration3")) {
                        duration3Tag = "close";
                    }
                    if (element.equalsIgnoreCase("duration4")) {
                        duration4Tag = "close";
                    }

                    if (element.equalsIgnoreCase("prescribed1")) {
                        prescribed1Tag = "close";
                    }

                    if (element.equalsIgnoreCase(" prescribed2")) {
                        prescribed2Tag = "close";
                    }

                    if (element.equalsIgnoreCase("prescribed3")) {
                        prescribed3Tag = "close";
                    }

                    if (element.equalsIgnoreCase("prescribed4")) {
                        prescribed4Tag = "close";
                    }

                    if (element.equalsIgnoreCase("duration4")) {
                        dispensed1Tag = "close";
                    }

                    if (element.equalsIgnoreCase("dispensed2")) {
                        dispensed2Tag = "close";
                    }

                    if (element.equalsIgnoreCase("dispensed3")) {
                        dispensed3Tag = "close";
                    }

                    if (element.equalsIgnoreCase("dispensed4")) {
                        dispensed4Tag = "close";
                    }

                    if (element.equalsIgnoreCase("notes")) {
                        notesTag = "close";
                    }

                    if (element.equalsIgnoreCase("next_refill")) {
                        nextRefillTag = "close";
                    }

                    if (element.equalsIgnoreCase("regimentype")) {
                        regimentypeTag = "close";
                    }
                    if (element.equalsIgnoreCase("time_stamp")) {
                        timeStampTag = "close";
                    }


                    if (element.equalsIgnoreCase("uploaded")) {
                        uploadedTag = "close";
                    }

                    if (element.equalsIgnoreCase("time_uploaded")) {
                        timeUploadedTag = "close";
                    }
                    if (element.equalsIgnoreCase("id_uuid")) {
                        idUUIDTag = "close";
                    }
                    if (element.equalsIgnoreCase("deviceconfig_id")) {
                        deviceconfigIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("encounter")) {
                        encounterTag = "close";
                        EncounterDAO.save(encounter[0]);
                    }

                }
            };
            saxParser.parse(xmlFileName, defaultHandler);
        } catch (Exception e) {
        }

    }
}
