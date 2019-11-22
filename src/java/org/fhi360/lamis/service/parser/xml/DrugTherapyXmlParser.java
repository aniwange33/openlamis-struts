/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.parser;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.fhi360.lamis.dao.hibernate.DrugtherapyDAO;
import org.fhi360.lamis.model.Drugtherapy;
import org.fhi360.lamis.utility.DateUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author user10
 */
public class DrugTherapyXmlParser extends DefaultHandler {

    public DrugTherapyXmlParser() {
    }

    
    public void parseXml(String xmlFileName) {
        final Drugtherapy[] drugtherapy = new Drugtherapy[]{null};
        try {
            //obtain and configure a SAX based parser
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

            //obtain object for SAX hadler class
            SAXParser saxParser = saxParserFactory.newSAXParser();
            DefaultHandler defaultHandler = new DefaultHandler() {
                String drugtherapyIdTag = "close";
                String drugtherapyTag = "close";
                String patientIdTag = "close";
                String facilityIdTag = "close";
                String communitypharmIdTag = "close";
                String dateVisitTag = "close";
                String oisTag = "close";
                String therapyProblemScreenedTag = "close";
                String adherenceIssuesTag = "close";
                String medicationErrorTag = "close";
                String adrsTag = "close";
                String severityTag = "close";
                String icsrFormTag = "close";
                String adrReferredTag = "close";
                String timeStampTag = "close";
                String uploadedTag = "close";
                String timeUploadedTag = "close";
                   String deviceconfigIdTag = "close";
                
                      

                @Override
                public void startElement(String uri, String localName, String element, Attributes attributes) throws SAXException {
                    if (element.equalsIgnoreCase("drugtherapy")) {
                        drugtherapyTag = "open";
                        drugtherapy[0] = new Drugtherapy();
                    }
                    if (element.equalsIgnoreCase("drugtherapy_id")) {
                        drugtherapyIdTag = "open";
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
                    if (element.equalsIgnoreCase("date_visit")) {
                        dateVisitTag = "open";
                    }
                    if (element.equalsIgnoreCase("ois")) {
                        oisTag = "open";
                    }
                    if (element.equalsIgnoreCase("therapy_problem_screened")) {
                        therapyProblemScreenedTag = "open";
                    }
                    if (element.equalsIgnoreCase("adherence_issues")) {
                        adherenceIssuesTag = "open";
                    }
                    if (element.equalsIgnoreCase("medication_error")) {
                        medicationErrorTag = "open";
                    }
                    if (element.equalsIgnoreCase("adrs")) {
                        adrsTag = "open";
                    }
                    if (element.equalsIgnoreCase("severity")) {
                        severityTag = "open";
                    }
                    if (element.equalsIgnoreCase("icsr_form")) {
                        icsrFormTag = "open";
                    }
                    if (element.equalsIgnoreCase("adr_referred")) {
                        adrReferredTag = "open";
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

                }

                @Override
                public void characters(char[] chars, int start, int length) throws SAXException {
                 
                    if (drugtherapyIdTag.equals("open")) {
                        String id = new String(chars, start, length);
                        drugtherapy[0].setDrugtherapyId(Long.parseLong(id));
                    }
                    if (patientIdTag.equals("open")) {
                       
                        drugtherapy[0].setPatientId(Long.parseLong(new String(chars, start, length)));
                    }
                    if (facilityIdTag.equals("open")) {

                        drugtherapy[0].setFacilityId(Long.parseLong(new String(chars, start, length)));
                    }
                    if (communitypharmIdTag.equals("open")) {
                        drugtherapy[0].setCommunitypharmId(Long.parseLong(new String(chars, start, length)));
                    }
                    if (dateVisitTag.equals("open")) {
                        String dateVisit = new String(chars, start, length);
                        drugtherapy[0].setDateVisit(DateUtil.parseStringToDate(dateVisit, "yyyy-MM-dd"));
                    }
                    if (oisTag.equals("open")) {
                        drugtherapy[0].setOis(new String(chars, start, length));
                    }
                    if (therapyProblemScreenedTag.equals("open")) {
                       
                        drugtherapy[0]. setTherapyProblemScreened(new String(chars, start, length));
                    }
                    if (adherenceIssuesTag.equals("open")) {
                       
                         drugtherapy[0].setAdherenceIssues(new String(chars, start, length));
                    }
                    if (medicationErrorTag.equals("open")) {
                        
                        drugtherapy[0].setMedicationError(new String(chars, start, length));
                    }
                    if (adrsTag.equals("open")) {
                       
                        drugtherapy[0].setAdrs(new String(chars, start, length));
                    }
                    if (severityTag.equals("open")) {
                       
                        drugtherapy[0].setSeverity(new String(chars, start, length));
                    }
                    if (icsrFormTag.equals("open")) {
                       
                        drugtherapy[0].setIcsrForm(new String(chars, start, length));
                    }
                    if (adrReferredTag.equals("open")) {
                        
                        drugtherapy[0].setAdrReferred(new String(chars, start, length));
                    }

                    if (timeStampTag.equals("open")) {
                       
                          drugtherapy[0].setTimeStamp(new java.sql.Date(new java.util.Date().getTime()));
                    }

                    if (uploadedTag.equals("open")) {
                        
                         drugtherapy[0].setUploaded(Integer.parseInt(new String(chars, start, length)));
                    }
                    if(timeUploadedTag.equals("open")){
                       String timeUploaded=new String(chars, start, length);
                        drugtherapy[0].setTimeUploaded(DateUtil.parseStringToDate(timeUploaded, "yyyy-MM-dd"));
                    }
                        
                    if (deviceconfigIdTag.equals("open")) {
                       drugtherapy[0].setDeviceconfigId(Long.parseLong(new String(chars, start, length)));
                    }

                }

                @Override
                public void endElement(String uri, String localName, String element) throws SAXException {
 
                    if (element.equalsIgnoreCase("drugtherapy_id")) {
                        drugtherapyIdTag = "close";
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
                    if (element.equalsIgnoreCase("date_visit")) {
                        dateVisitTag = "close";
                    }
                    if (element.equalsIgnoreCase("ois")) {
                        oisTag = "open";
                    }
                    if (element.equalsIgnoreCase("therapy_problem_screened")) {
                        therapyProblemScreenedTag = "close";
                    }
                    if (element.equalsIgnoreCase("adherence_issues")) {
                        adherenceIssuesTag = "close";
                    }
                    if (element.equalsIgnoreCase("medication_error")) {
                        medicationErrorTag = "close";
                    }
                    if (element.equalsIgnoreCase("adrs")) {
                        adrsTag = "close";
                    }
                    if (element.equalsIgnoreCase("severity")) {
                        severityTag = "close";
                    }
                    if (element.equalsIgnoreCase("icsr_form")) {
                        icsrFormTag = "close";
                    }
                    if (element.equalsIgnoreCase("adr_referred")) {
                        adrReferredTag = "close";
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
                   
                    if (element.equalsIgnoreCase("drugtherapy")) {
                        drugtherapyTag = "close";
                        DrugtherapyDAO.save(drugtherapy[0]);
                    }
                }
            };
            saxParser.parse(xmlFileName, defaultHandler);
        } catch (Exception e) {
        }

    }
}
