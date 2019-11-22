/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.parser.xml;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.fhi360.lamis.dao.hibernate.EacDAO;
import org.fhi360.lamis.model.Eac;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.service.ServerIDProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author user10
 */
public class EacXmlParser extends DefaultHandler {

    private long facilityId;
    private String hospitalNum;
    private String dateEac1;
    private long idOnServer;
    private Boolean populated;
    private Boolean skipRecord;
    private Eac eac;
    private Patient patient = new Patient();

    public EacXmlParser() {
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
                String eacTag = "close";
                String eacIdTag = "close";
                String patientIdTag = "close";
                String facilityIdTag = "close";
                String hospitalNumTag = "close";
                String dateEac1Tag = "close";
                String dateEac2Tag = "close";
                String dateEac3Tag = "close";
                String dateSampleCollectedTag = "close";
                String lastViralLoadTag = "close";
                String dateLastViralLoadTag = "close";
                String notesTag = "close";
                String timeStampTag = "close";
                String idUUIDTag = "close";

                //this method is called every time the parser gets an open tag '<'
                //identifies which tag is being open at the time by assigning an open flag
                @Override
                public void startElement(String uri, String localName, String element, Attributes attributes) throws SAXException {
                    if (element.equalsIgnoreCase("eac")) {
                        eacTag = "open";
                        eac = new Eac();
                    }
                    if (element.equalsIgnoreCase("eac_id")) {
                        eacIdTag = "open";
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
                    if (element.equalsIgnoreCase("date_eac1")) {
                        dateEac1Tag = "open";
                    }
                    if (element.equalsIgnoreCase("date_eac2")) {
                        dateEac2Tag = "open";
                    }
                    if (element.equalsIgnoreCase("date_eac3")) {
                        dateEac3Tag = "open";
                    }
                    if (element.equalsIgnoreCase("date_sample_collected")) {
                        dateSampleCollectedTag = "open";
                    }
                    if (element.equalsIgnoreCase("last_viral_load")) {
                        lastViralLoadTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_last_viral_load")) {
                        dateLastViralLoadTag = "open";
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
                    }
                    if (hospitalNumTag.equals("open")) {
                        hospitalNum = new String(chars, start, length);
                    }
                    if (dateEac1Tag.equals("open")) {
                        dateEac1 = new String(chars, start, length);
                        if (!dateEac1.trim().isEmpty()) {
                            eac.setDateEac1(DateUtil.parseStringToDate(dateEac1, "yyyy-MM-dd"));
                        } else {
                            skipRecord = true;
                        }
                    }
                    if (dateEac2Tag.equals("open")) {
                        String dateEac2 = new String(chars, start, length);
                        if (!dateEac2.trim().isEmpty()) {
                            eac.setDateEac2(DateUtil.parseStringToDate(dateEac2, "yyyy-MM-dd"));
                        }
                    }
                    if (dateEac3Tag.equals("open")) {
                        String dateEac3 = new String(chars, start, length);
                        if (!dateEac3.trim().isEmpty()) {
                            eac.setDateEac3(DateUtil.parseStringToDate(dateEac3, "yyyy-MM-dd"));
                        }
                    }
                    if (dateSampleCollectedTag.equals("open")) {
                        String dateSampleCollected = new String(chars, start, length);
                        if (!dateSampleCollected.trim().isEmpty()) {
                            eac.setDateSampleCollected(DateUtil.parseStringToDate(dateSampleCollected, "yyyy-MM-dd"));
                        }
                    }
                    if (lastViralLoadTag.equals("open")) {
                        String lastViralLoad = new String(chars, start, length);
                        if (!lastViralLoad.trim().isEmpty()) {
                            eac.setLastViralLoad(Double.parseDouble(lastViralLoad));
                        }
                    }
                    if (dateLastViralLoadTag.equals("open")) {
                        String dateLastViralLoad = new String(chars, start, length);
                        if (!dateLastViralLoad.trim().isEmpty()) {
                            eac.setDateLastViralLoad(DateUtil.parseStringToDate(dateLastViralLoad, "yyyy-MM-dd"));
                        }
                    }
                    if (timeStampTag.equals("open")) {
                        eac.setTimeStamp(new java.sql.Date(new java.util.Date().getTime()));
                    }
                    if (idUUIDTag.equals("open")) {
                        eac.setUuid(new String(chars, start, length));
                    }
                }

                @Override
                public void endElement(String uri, String localName, String element) throws SAXException {
                    if (element.equalsIgnoreCase("eac_id")) {
                        eacIdTag = "close";
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
                    if (element.equalsIgnoreCase("date_eac1")) {
                        dateEac1Tag = "close";
                    }
                    if (element.equalsIgnoreCase("date_eac2")) {
                        dateEac2Tag = "close";
                    }
                    if (element.equalsIgnoreCase("date_eac3")) {
                        dateEac3Tag = "close";
                    }
                    if (element.equalsIgnoreCase("date_sample_collectd")) {
                        dateSampleCollectedTag = "close";
                    }
                    if (element.equalsIgnoreCase("last_viral_load")) {
                        lastViralLoadTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_last_viral_load")) {
                        dateLastViralLoadTag = "close";
                    }
                    if (element.equalsIgnoreCase("time_stamp")) {
                        timeStampTag = "close";
                    }
                    if (element.equalsIgnoreCase("id_uuid") || element.equalsIgnoreCase("uuid")) {
                        idUUIDTag = "close";
                    }

                    //if this is the closing tag of a statushistory element save the record
                    if (element.equalsIgnoreCase("eac")) {
                        eacTag = "close";
                        if (!skipRecord) {Long patientId = ServerIDProvider.getPatientServerId(hospitalNum, facilityId);
                            if (patientId != null) {
                                Patient patient = new Patient();
                                patient.setPatientId(patientId);
                                eac.setPatient(patient);
                                Long id = ServerIDProvider
                                        .getPatientDependantId("eac", hospitalNum,
                                                eac.getDateEac1(), facilityId);
                                if (id != null) {
                                    eac.setEacId(id);
                                    EacDAO.update(eac);
                                } else {
                                    try {
                                        EacDAO.save(eac);
                                    }catch (Exception ignored){

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
            //new CleanupService().cleanNullFields("eac", facilityId);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }

}
