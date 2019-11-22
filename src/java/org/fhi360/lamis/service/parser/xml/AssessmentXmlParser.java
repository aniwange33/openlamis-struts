/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.parser.xml;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.fhi360.lamis.dao.hibernate.AssessmentDao;
import org.fhi360.lamis.dao.jdbc.AssessmentJDBC;
import org.fhi360.lamis.model.Assessment;
import org.fhi360.lamis.utility.DateUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author User10
 */
public class AssessmentXmlParser extends DefaultHandler {

    private boolean skipRecord;
    private Assessment assessment;
    private long facilityId;

    public AssessmentXmlParser() {
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
                String facilityIdTag = "close";
                String dateVisitTag = "close";
                String timeStampTag = "close";
                String idUUIDTag = "close";
                String assessmentIdTag = "close";
                String assessmentTag = "close";
                String question1Tag = "close";
                String question2Tag = "close";
                String question3Tag = "close";
                String question4Tag = "close";
                String question5Tag = "close";
                String question6Tag = "close";
                String question7Tag = "close";
                String question8Tag = "close";
                String question9Tag = "close";
                String question10Tag = "close";
                String question11Tag = "close";
                String question12Tag = "close";
                String sti1Tag = "close";
                String sti2Tag = "close";
                String sti3Tag = "close";
                String sti4Tag = "close";
                String sti5Tag = "close";
                String sti6Tag = "close";
                String sti7Tag = "close";
                String sti8Tag = "close";
                String clientCodeTag = "close";
                String clientAssessmentCodeTag = "close";
                String deviceConfigIdTag = "close";

                //this method is called every time the parser gets an open tag '<'
                //identifies which tag is being open at the time by assigning an open flag
                @Override
                public void startElement(String uri, String localName, String element, Attributes attributes) throws SAXException {
                    if (element.equalsIgnoreCase("assessment")) {
                        assessmentTag = "open";
                        assessment = new Assessment();
                        skipRecord = false;
                    }
                    if (element.equalsIgnoreCase("assessment_id")) {
                        assessmentIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_visit")) {
                        dateVisitTag = "open";
                    }
                    if (element.equalsIgnoreCase("time_stamp")) {
                        timeStampTag = "open";
                    }
                    if (element.equalsIgnoreCase("id_uuid") || element.equalsIgnoreCase("uuid")) {
                        idUUIDTag = "open";
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
                    if (element.equalsIgnoreCase("question10")) {
                        question10Tag = "open";
                    }
                    if (element.equalsIgnoreCase("question11")) {
                        question11Tag = "open";
                    }
                    if (element.equalsIgnoreCase("question12")) {
                        question12Tag = "open";
                    }
                    if (element.equalsIgnoreCase("sti1")) {
                        sti1Tag = "open";
                    }
                    if (element.equalsIgnoreCase("sti2")) {
                        sti2Tag = "open";
                    }
                    if (element.equalsIgnoreCase("sti3")) {
                        sti3Tag = "open";
                    }
                    if (element.equalsIgnoreCase("sti4")) {
                        sti4Tag = "open";
                    }
                    if (element.equalsIgnoreCase("sti5")) {
                        sti5Tag = "open";
                    }
                    if (element.equalsIgnoreCase("sti6")) {
                        sti6Tag = "open";
                    }
                    if (element.equalsIgnoreCase("sti7")) {
                        sti7Tag = "open";
                    }
                    if (element.equalsIgnoreCase("sti8")) {
                        sti8Tag = "open";
                    }
                    if (element.equalsIgnoreCase("client_code")) {
                        clientCodeTag = "open";
                    }
                    if (element.equalsIgnoreCase("client_assessment_code")) {
                        clientAssessmentCodeTag = "open";
                    }
                    if (element.equalsIgnoreCase("deviceconfig_id")) {
                        deviceConfigIdTag = "open";
                    }
                }

                //store data store in between '<' and '>' tags                     
                @Override
                public void characters(char[] chars, int start, int length) throws SAXException {
                    String content = new String(chars, start, length);
                    Integer intContent = 0;
                    try {
                        intContent = Integer.parseInt(content);
                    } catch (Exception e) {
                    }
                    if (facilityIdTag.equals("open")) {
                        facilityId = Long.parseLong(content);
                        assessment.setFacilityId(facilityId);
                    }
                    if (dateVisitTag.equals("open")) {
                        if (!content.trim().isEmpty()) {
                            assessment.setDateVisit(DateUtil.parseStringToDate(content, "yyyy-MM-dd"));
                        } else {
                            skipRecord = true;
                        }
                    }
                    if (idUUIDTag.equals("open")) {
                        assessment.setUuid(content);
                    }
                    if (timeStampTag.equals("open")) {
                        assessment.setTimeStamp(new java.sql.Date(new java.util.Date().getTime()));
                    }
                    if (question1Tag.equals("open")) {
                        assessment.setQuestion1(content);
                    }
                    if (question2Tag.equals("open")) {
                        assessment.setQuestion2(content);
                    }
                    if (question3Tag.equals("open")) {
                        assessment.setQuestion3(intContent);
                    }
                    if (question4Tag.equals("open")) {
                        assessment.setQuestion4(intContent);
                    }
                    if (question5Tag.equals("open")) {
                        assessment.setQuestion5(intContent);
                    }
                    if (question6Tag.equals("open")) {
                        assessment.setQuestion6(intContent);
                    }
                    if (question7Tag.equals("open")) {
                        assessment.setQuestion7(intContent);
                    }
                    if (question8Tag.equals("open")) {
                        assessment.setQuestion8(intContent);
                    }
                    if (question9Tag.equals("open")) {
                        assessment.setQuestion9(intContent);
                    }
                    if (question10Tag.equals("open")) {
                        assessment.setQuestion10(intContent);
                    }
                    if (question11Tag.equals("open")) {
                        assessment.setQuestion11(intContent);
                    }
                    if (question12Tag.equals("open")) {
                        assessment.setQuestion12(intContent);
                    }
                    if (sti1Tag.equals("open")) {
                        assessment.setSti1(intContent);
                    }
                    if (sti2Tag.equals("open")) {
                        assessment.setSti2(intContent);
                    }
                    if (sti3Tag.equals("open")) {
                        assessment.setSti3(intContent);
                    }
                    if (sti4Tag.equals("open")) {
                        assessment.setSti4(intContent);
                    }
                    if (sti5Tag.equals("open")) {
                        assessment.setSti5(intContent);
                    }
                    if (sti6Tag.equals("open")) {
                        assessment.setSti6(intContent);
                    }
                    if (sti7Tag.equals("open")) {
                        assessment.setSti7(intContent);
                    }
                    if (sti8Tag.equals("open")) {
                        assessment.setSti8(intContent);
                    }
                    if (clientCodeTag.equals("open")) {
                        assessment.setClientCode(content);
                    }
                    if (deviceConfigIdTag.equals("open")) {
                        assessment.setDeviceconfigId(intContent.longValue());
                    }
                }

                @Override
                public void endElement(String uri, String localName, String element) throws SAXException {

                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_visit")) {
                        dateVisitTag = "close";
                    }
                    if (element.equalsIgnoreCase("time_stamp")) {
                        timeStampTag = "close";
                    }
                    if (element.equalsIgnoreCase("id_uuid") || element.equalsIgnoreCase("uuid")) {
                        idUUIDTag = "close";
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
                    if (element.equalsIgnoreCase("question10")) {
                        question10Tag = "close";
                    }
                    if (element.equalsIgnoreCase("question11")) {
                        question11Tag = "close";
                    }
                    if (element.equalsIgnoreCase("question12")) {
                        question12Tag = "close";
                    }
                    if (element.equalsIgnoreCase("sti1")) {
                        sti1Tag = "close";
                    }
                    if (element.equalsIgnoreCase("sti2")) {
                        sti2Tag = "close";
                    }
                    if (element.equalsIgnoreCase("sti3")) {
                        sti3Tag = "close";
                    }
                    if (element.equalsIgnoreCase("sti4")) {
                        sti4Tag = "close";
                    }
                    if (element.equalsIgnoreCase("sti5")) {
                        sti5Tag = "close";
                    }
                    if (element.equalsIgnoreCase("sti6")) {
                        sti6Tag = "close";
                    }
                    if (element.equalsIgnoreCase("sti7")) {
                        sti7Tag = "close";
                    }
                    if (element.equalsIgnoreCase("sti8")) {
                        sti8Tag = "close";
                    }
                    if (element.equalsIgnoreCase("client_code")) {
                        clientCodeTag = "close";
                    }
                    if (element.equalsIgnoreCase("client_assessment_code")) {
                        clientAssessmentCodeTag = "close";
                    }
                    if (element.equalsIgnoreCase("deviceconfig_id")) {
                        deviceConfigIdTag = "close";
                    }

                    //if this is the closing tag of a adherehistory element save the record
                    if (element.equalsIgnoreCase("assessment")) {
                        assessmentTag = "close";
                        if (!skipRecord) {
                            long assessmentId = new AssessmentJDBC().getHtsAssessment(assessment.getUuid(), assessment.getFacilityId());
                            if (assessmentId == 0L) {
                                AssessmentDao.save(assessment);
                            } else {
                                assessment.setAssessmentId(assessmentId);
                                AssessmentDao.update(assessment);
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
