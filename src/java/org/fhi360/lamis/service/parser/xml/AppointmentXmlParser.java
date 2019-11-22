/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.parser;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.fhi360.lamis.dao.hibernate.AppointmentDAO;
import org.fhi360.lamis.model.Appointment;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.service.EntityIdentifier;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.PatientNumberNormalizer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author user10
 */
public class AppointmentXmlParser extends DefaultHandler {

    private EntityIdentifier entityIdentifier = new EntityIdentifier();
    private Long idOnServer;
    private String hospitalNum;
    private Boolean skipRecord;

    public AppointmentXmlParser() {
    }

    public void parseXml(String xmlFileName) {
        final Appointment[] appointment = new Appointment[]{null};
        try {
            //obtain and configure a SAX based parser
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

            //obtain object for SAX hadler class
            SAXParser saxParser = saxParserFactory.newSAXParser();
            DefaultHandler defaultHandler = new DefaultHandler() {
                String appointmentTag = "close";
                String appointmentIdTag = "close";
                String hospitalNumTag = "close";
                String patientIdTag = "close";
                String dateVisitTag = "close";
                String facilityIdTag = "close";
                String communitypharmIdTag = "close";
                String dateLastVisitTag = "close";
                String dateNextVisitTag = "close";
                String dateTrackedTag = "close";
                String typeTrackingTag = "close";
                String trackingOutcomeTag = "close";
                String dateAgreedTag = "close";
                String timeStampTag = "close";
                String uploadedTag = "close";
                String timeUploadedTag = "close";
                String deviceconfigIdTag = "close";

                @Override
                public void startElement(String uri, String localName, String element, Attributes attributes) throws SAXException {
                    if (element.equalsIgnoreCase("appointment")) {
                        appointmentTag = "open";
                        appointment[0] = new Appointment();
                    }
                    if (element.equalsIgnoreCase("appointment_id")) {
                        appointmentIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("hospital_num")) {
                        hospitalNumTag = "open";
                    }
                    if (element.equalsIgnoreCase("patient_id")) {
                        patientIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("communitypharm_id")) {
                        communitypharmIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("dateLast_visit")) {
                        dateLastVisitTag = "open";
                    }
                    if (element.equalsIgnoreCase("dateNext_visit")) {
                        dateNextVisitTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_tracked")) {
                        dateTrackedTag = "open";
                    }
                    if (element.equalsIgnoreCase("type_tracking")) {
                        typeTrackingTag = "open";
                    }
                    if (element.equalsIgnoreCase("tracking_outcome")) {
                        trackingOutcomeTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_agreed")) {
                        dateAgreedTag = "open";
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
                    if (element.equalsIgnoreCase("deviceconfig_id")) {
                        deviceconfigIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_visit")) {
                        dateVisitTag = "open";
                    }

                }

                @Override
                public void characters(char[] chars, int start, int length) throws SAXException {
                    if (appointmentIdTag.equals("open")) {
                        String id = new String(chars, start, length);
                        appointment[0].setAppointmentId(Long.parseLong(id));
                    }
                    if (patientIdTag.equals("open")) {

                        appointment[0].setPatientId(Long.parseLong(new String(chars, start, length)));

                    }
                    if (hospitalNumTag.equals("open")) {
                        hospitalNum = new String(chars, start, length);
                    }
                    if (facilityIdTag.equals("open")) {

                        appointment[0].setFacilityId(Long.parseLong(new String(chars, start, length)));
                    }
                    if (communitypharmIdTag.equals("open")) {

                        appointment[0].setCommunitypharmId(Long.parseLong(new String(chars, start, length)));
                    }
                    if (dateLastVisitTag.equals("open")) {
                        String dateLastVisit = new String(chars, start, length);
                        if (!dateLastVisit.trim().isEmpty()) {
                            appointment[0].setDateLastVisit(DateUtil.parseStringToDate(dateLastVisit, "yyyy-MM-dd"));

                        } else {
                            skipRecord = true;
                        }
                    }
                    if (dateNextVisitTag.equals("open")) {
                        String dateNextVisit = new String(chars, start, length);
                        if (!dateNextVisit.trim().isEmpty()) {
                            appointment[0].setDateNextVisit(DateUtil.parseStringToDate(dateNextVisit, "yyyy-MM-dd"));

                        } else {
                            skipRecord = true;
                        }

                    }
                    if (typeTrackingTag.equals("open")) {

                        appointment[0].setTypeTracking(new String(chars, start, length));
                    }
                    if (trackingOutcomeTag.equals("open")) {

                        appointment[0].setTrackingOutcome(new String(chars, start, length));
                    }
                    if (dateAgreedTag.equals("open")) {

                        String dateNextVisit = new String(chars, start, length);
                        if (!dateNextVisit.trim().isEmpty()) {
                            appointment[0].setDateAgreed(DateUtil.parseStringToDate(dateNextVisit, "yyyy-MM-dd"));

                        }
                    }
                    if (timeStampTag.equals("open")) {

                        appointment[0].setTimeStamp(new java.sql.Date(new java.util.Date().getTime()));
                    }
                    if (uploadedTag.equals("open")) {
                        appointment[0].setUploaded(Integer.parseInt(new String(chars, start, length)));
                    }
     
                }

                @Override
                public void endElement(String uri, String localName, String element) throws SAXException {

                    if (element.equalsIgnoreCase("appointment_id")) {
                        appointmentIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("patient_id")) {
                        patientIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("communitypharm_id")) {
                        communitypharmIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("dateLast_visit")) {
                        dateLastVisitTag = "close";
                    }
                    if (element.equalsIgnoreCase("dateNext_visit")) {
                        dateNextVisitTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_tracked")) {
                        dateTrackedTag = "close";
                    }
                    if (element.equalsIgnoreCase("type_tracking")) {
                        typeTrackingTag = "close";
                    }
                    if (element.equalsIgnoreCase("tracking_outcome")) {
                        trackingOutcomeTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_agreed")) {
                        dateAgreedTag = "close";
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
                    if (element.equalsIgnoreCase("deviceconfig_id")) {
                        deviceconfigIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("appointment")) {
                        appointmentTag = "close";
                        if (skipRecord) {
                            System.out.println("....record has a null value: " + hospitalNum);
                        } else {
                            //Using the hospital number of this patient that was waived into the clinic xml, get the ID of this patient on the server
                            //If the hospital number does not exist then the patient does not exist. If the patient exist retrieve the ID and use it to form the patient object
                            //Then set the patient object on the clinic object
                            String query = "SELECT id_on_server FROM entity WHERE TRIM(LEADING '0' FROM hospital_num) = '" + PatientNumberNormalizer.unpadNumber(hospitalNum) + "'";
                            idOnServer = entityIdentifier.getIdOnServer(query); //check if patient exist on the server
                            if (idOnServer != 0) {
                                Patient patient = new Patient();
                                patient.setPatientId(idOnServer);
                                appointment[0].setPatientId(patient.getPatientId());

                                //Retrieve the clinic ID from the server if this clinic exists
                                query = "SELECT id_on_server FROM dependant WHERE TRIM(LEADING '0' FROM hospital_num) = '" + PatientNumberNormalizer.unpadNumber(hospitalNum) + "' AND date_visit = '" + dateNextVisitTag + "'";
                                idOnServer = entityIdentifier.getIdOnServer(query); //check if this record exist on the server
                                if (idOnServer == 0) {
                                    idOnServer = AppointmentDAO.save(appointment[0]);
                                } else {
                                    appointment[0].setAppointmentId(idOnServer);
                                    AppointmentDAO.update(appointment[0]);
                                }
                            }

                        }

                    }

                }
            };
            saxParser.parse(xmlFileName, defaultHandler);
        } catch (Exception e) {
        }

    }
}
