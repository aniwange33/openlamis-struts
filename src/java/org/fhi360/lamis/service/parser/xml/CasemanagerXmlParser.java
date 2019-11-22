/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.parser.xml;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.fhi360.lamis.dao.hibernate.CaseManagerDAO;
import org.fhi360.lamis.model.Casemanager;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.service.ServerIDProvider;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author user10
 */
public class CasemanagerXmlParser  extends DefaultHandler{
    
    private long facilityId, caseManagerId;
    private Casemanager caseManager;

    public CasemanagerXmlParser() {
    }

    
    public void parseXml(String xmlFileName) {
        try {
            //obtain and configure a SAX based parser
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

            //obtain object for SAX hadler class
            SAXParser saxParser = saxParserFactory.newSAXParser();

            //default handler for SAX handler class
            // all three methods are written in handler's body
            DefaultHandler defaultHandler = new DefaultHandler() {
                String caseManagerTag = "close";
                String caseManagerIdTag = "close";
                String facilityIdTag = "close";
                String fullNameTag = "close";
                String sexTag = "close";
                String ageTag = "close";
                String phoneNumberTag = "close";
                String religionTag = "close";
                String addressTag = "close";
                String uploadedTag = "close";
                String timeUploadedTag = "close";
                String timeStampTag = "close";
                String userIdTag = "close";
                String idUUIDTag = "close";
                               
                //this method is called every time the parser gets an open tag '<'
                //identifies which tag is being open at the time by assigning an open flag
                @Override
                public void startElement(String uri, String localName, String element, Attributes attributes) throws SAXException {
                    if (element.equalsIgnoreCase("casemanager")) {
                        caseManagerTag = "open";
                        caseManager = new Casemanager();
                    }
                    if (element.equalsIgnoreCase("casemanager_id")) {
                        caseManagerIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("fullname")) {
                        fullNameTag = "open";
                    }
                    if (element.equalsIgnoreCase("sex")) {
                        sexTag = "open";
                    }
                    if (element.equalsIgnoreCase("age")) {
                        ageTag = "open";
                    }
                    if (element.equalsIgnoreCase("phone_number")) {
                        phoneNumberTag = "open";
                    }
                    if (element.equalsIgnoreCase("religion")) {
                        religionTag = "open";
                    }
                    if (element.equalsIgnoreCase("address")) {
                        addressTag = "open";
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
                    if (caseManagerIdTag.equals("open")) {
                        caseManagerId = Long.parseLong(new String(chars, start, length));
                    }
                    if (facilityIdTag.equals("open")) {
                        facilityId = Long.parseLong(new String(chars, start, length));
                        caseManager.setFacilityId(facilityId); 
                    }   
                    if (fullNameTag.equals("open")) {
                        caseManager.setFullname(new String(chars, start, length));
                    }                    
                    if (sexTag.equals("open")) {
                        caseManager.setSex(new String(chars, start, length));
                    }                    
                    if (ageTag.equals("open")) {
                        caseManager.setAge(new String(chars, start, length));
                    }                    
                    if (phoneNumberTag.equals("open")) {
                        caseManager.setPhoneNumber(new String(chars, start, length));
                    }                    
                    if (religionTag.equals("open")) {
                        caseManager.setReligion(new String(chars, start, length));
                    }                    
                    if (addressTag.equals("open")) {
                        caseManager.setAddress(new String(chars, start, length));
                    }                    
                    if (timeStampTag.equals("open")) {
                        caseManager.setTimeStamp(new java.sql.Date(new java.util.Date().getTime()));
                    } 
                    if (idUUIDTag.equals("open")) {
                        caseManager.setUuid(new String(chars, start, length));
                    }
                    if (userIdTag.equals("open")) {
                        String userId = new String(chars, start, length);
                        if(!userId.trim().trim().isEmpty()) caseManager.setUserId(Integer.parseInt(userId));
                    }                    
                }
                
                @Override
                public void endElement(String uri, String localName, String element) throws SAXException {
                    if (element.equalsIgnoreCase("casemanager")) {
                        caseManagerTag = "close";
                    }
                    if (element.equalsIgnoreCase("casemanager_id")) {
                        caseManagerIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("fullname")) {
                        fullNameTag = "close";
                    }
                    if (element.equalsIgnoreCase("sex")) {
                        sexTag = "close";
                    }
                    if (element.equalsIgnoreCase("age")) {
                        ageTag = "close";
                    }
                    if (element.equalsIgnoreCase("phone_number")) {
                        phoneNumberTag = "close";
                    }
                    if (element.equalsIgnoreCase("religion")) {
                        religionTag = "close";
                    }
                    if (element.equalsIgnoreCase("address")) {
                        addressTag = "close";
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
                    if (element.equalsIgnoreCase("casemanager")) {
                        caseManagerTag = "close";
                       
                        caseManager.setLocalId(caseManagerId); 
                        Long caseManagerId = ServerIDProvider.getCaseManagerId(
                                caseManager.getLocalId(), facilityId
                        );
                        if (caseManagerId != null) {
                            caseManager.setCasemanagerId(caseManagerId);
                            CaseManagerDAO.update(caseManager);
                        } else {
                            CaseManagerDAO.save(caseManager);
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
