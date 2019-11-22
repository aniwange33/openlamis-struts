/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.parser.xml;

/**
 *
 * @author user10
 */
import org.fhi360.lamis.service.EntityIdentifier;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.fhi360.lamis.dao.hibernate.ValidatedDAO;
import org.fhi360.lamis.model.Validated;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.PatientNumberNormalizer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ValidatedXmlParser extends DefaultHandler{
    private long facilityId;
    private String hospitalNum;
    private String dateValidated;
    private long idOnServer;
    private Boolean populated;
    private Validated validated;
    private EntityIdentifier entityIdentifier = new EntityIdentifier(); ;

    public ValidatedXmlParser() {
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
                String validatedTag = "close";
                String validatedIdTag = "close";
                String facilityIdTag = "close";
                String patientIdTag = "close";
                String tableValidatedTag = "close";
                String recordIdTag = "close";
                String validatedByTag = "close";
                String dateValidatedTag = "close";
                String timeStampTag = "close";
                String userIdTag = "close";
                String createdByTag = "close";
                               
                //this method is called every time the parser gets an open tag '<'
                //identifies which tag is being open at the time by assigning an open flag
                @Override
                public void startElement(String uri, String localName, String element, Attributes attributes) throws SAXException {
                    if (element.equalsIgnoreCase("validated")) {
                        validatedTag = "open";
                        validated = new Validated();
                    }
                    if (element.equalsIgnoreCase("validated_id")) {
                        validatedIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("patient_id")) {
                        patientIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("table_validated")) {
                        tableValidatedTag = "open";
                    }
                    if (element.equalsIgnoreCase("record_id")) {
                        recordIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("validated_by")) {
                        validatedByTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_validated")) {
                        dateValidatedTag = "open";
                    }
                    if (element.equalsIgnoreCase("time_stamp")) {
                        timeStampTag = "open";
                    }
                    if (element.equalsIgnoreCase("user_id")) {
                        userIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("created_by")) {
                        createdByTag = "open";
                    }
                }
                
                //store data store in between '<' and '>' tags                     
                @Override
                public void characters(char[] chars, int start, int length) throws SAXException {
                    if (facilityIdTag.equals("open")) {
                        facilityId = Long.parseLong(new String(chars, start, length));
                        validated.setFacilityId(facilityId);
                        if(!populated) {
                            entityIdentifier.createEntities(facilityId, "validated", RandomStringUtils.randomAlphanumeric(6));
                            populated = true;
                        }            
                    }                                        
                    if (tableValidatedTag.equals("open")) {
                        validated.setTableValidated(new String(chars, start, length));
                    }                    
                    if (recordIdTag.equals("open")) {
                        validated.setRecordId(new String(chars, start, length));
                    }                    
                    if (validatedByTag.equals("open")) {
                        validated.setValidatedBy(new String(chars, start, length));
                    }                    
                    if (dateValidatedTag.equals("open")) {
                        dateValidated = new String(chars, start, length);
                        if(!dateValidated.trim().isEmpty()) validated.setDateValidated(DateUtil.parseStringToDate(dateValidated, "yyyy-MM-dd"));
                    }                    
                    if (timeStampTag.equals("open")) {
                        validated.setTimeStamp(new java.sql.Date(new java.util.Date().getTime()));
                    }                    
                    if (userIdTag.equals("open")) {
                       validated.setUserId(Integer.parseInt(new String(chars, start, length)));
                    }                    
                    if (createdByTag.equals("open")) {
                        validated.setCreatedBy(new String(chars, start, length));
                    }                    
                                        
                }
                
                @Override
                public void endElement(String uri, String localName, String element) throws SAXException {
                    if (element.equalsIgnoreCase("validated")) {
                        validatedTag = "close";
                    }
                    if (element.equalsIgnoreCase("validated_id")) {
                        validatedIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("patient_id")) {
                        patientIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("table_validated")) {
                        tableValidatedTag = "close";
                    }
                    if (element.equalsIgnoreCase("record_id")) {
                        recordIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("validated_by")) {
                        validatedByTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_validated")) {
                        dateValidatedTag = "close";
                    }
                    if (element.equalsIgnoreCase("time_stamp")) {
                        timeStampTag = "close";
                    }
                    if (element.equalsIgnoreCase("user_id")) {
                        userIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("created_by")) {
                        createdByTag = "close";
                    }
                    
                    //TODO: if this is the closing tag of a validation element save the record
                    if (element.equalsIgnoreCase("validated")) {
                        validatedTag = "close";
                        
                        String query = "SELECT id_on_server FROM entity WHERE TRIM(LEADING '0' FROM hospital_num) = '" + PatientNumberNormalizer.unpadNumber(hospitalNum) + "'"; 
                        idOnServer = entityIdentifier.getIdOnServer(query); //check if patient exist on the server
                        if(idOnServer != 0) {
                            validated.setPatientId(idOnServer);

                            //Retrieve the validated ID from the server if this record exists
                            query = "SELECT id_on_server FROM dependant WHERE TRIM(LEADING '0' FROM hospital_num) = '" + PatientNumberNormalizer.unpadNumber(hospitalNum) + "' AND date_validated = '" + dateValidated + "'"; 
                            idOnServer = entityIdentifier.getIdOnServer(query); //check if this record exist on the server
                            if(idOnServer == 0) {
                                idOnServer = ValidatedDAO.save(validated);
                            } 
                            else {
                                validated.setValidatedId(idOnServer);        
                                ValidatedDAO.update(validated);                
                            }                                                        
                        }
                    }                   
                }
            }; 
        
            //parse the XML specified in the given path and uses supplied handler to parse the document
            //this calls startElement(), endElement(), and character() methods accordingly
            saxParser.parse(xmlFileName, defaultHandler);
            //new CleanupService().cleanNullFields("validated", facilityId);
        }
        catch(Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }
    
}

