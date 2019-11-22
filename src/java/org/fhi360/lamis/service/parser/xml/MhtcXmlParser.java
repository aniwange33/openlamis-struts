/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.parser;

import com.itextpdf.text.pdf.codec.Base64;
import java.util.Date;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.fhi360.lamis.dao.hibernate.BiometricDAO;
import org.fhi360.lamis.dao.hibernate.MhtcDAO;
import org.fhi360.lamis.model.Biometric;
import org.fhi360.lamis.model.Mhtc;
import org.fhi360.lamis.utility.DateUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author User10
 */
public class MhtcXmlParser extends DefaultHandler {

    public MhtcXmlParser() {
    }

    
    public void parseXml(String xmlFileName) {
        final Mhtc[] mhtc = new Mhtc[]{null};
        try {
            //obtain and configure a SAX based parser
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            //obtain object for SAX hadler class
            SAXParser saxParser = saxParserFactory.newSAXParser();
            DefaultHandler defaultHandler = new DefaultHandler() {
                String mhtcTag = "close";
                String mhtcIdTag = "close";
                String facilityIdTag = "close";
                String communitypharmIdTag = "close";
                String monthTag = "close";
                String yearTag = "close";
                String numTestedTag = "close";
                String numPositiveTag = "close";
                String numReferredTag = "close";
                String numOnsiteVisitTag = "close";
                String timeStampTag = "close";
                String uploadedTag = "close";
                String timeUploadedTag = "close";
                String deviceconfigIdTag = "close";

                @Override
                public void startElement(String uri, String localName, String element, Attributes attributes) throws SAXException {
                    if (element.equalsIgnoreCase("mhtc")) {
                        mhtcTag = "open";
                        mhtc[0] = new Mhtc();
                    }
                    if (element.equalsIgnoreCase("mhtc_id")) {
                        mhtcIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("communitypharm_id")) {
                        communitypharmIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("month")) {
                        monthTag = "open";
                    }
                    if (element.equalsIgnoreCase("year")) {
                        yearTag = "open";
                    }
                    if (element.equalsIgnoreCase("num_tested")) {
                        numTestedTag = "open";
                    }
                    if (element.equalsIgnoreCase("num_positive")) {
                        numPositiveTag = "open";
                    }
                    if (element.equalsIgnoreCase("num_referred")) {
                        numReferredTag = "open";
                    }
                    if (element.equalsIgnoreCase("num_onsite_visit")) {
                        numOnsiteVisitTag = "open";
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
                    if (mhtcIdTag.equals("open")) {
                        String id = new String(chars, start, length);
                        mhtc[0].setMhtcId(Long.parseLong(id));
                    }
                        if (facilityIdTag.equals("open")) {
                        //System.out.println("facilityIdTag "+new String(chars, start, length));
                        long facilityId = Long.parseLong(new String(chars, start, length));
                        mhtc[0].setFacilityId(facilityId);

                    }
                    if (communitypharmIdTag.equals("open")) {
                        String id = new String(chars, start, length);
                        mhtc[0].setCommunitypharmId(Long.parseLong(id));
                    }
                    if (monthTag.equals("open")) {
                        String month = new String(chars, start, length);
                        mhtc[0].setMonth(Integer.parseInt(month));
                    }
                    if (yearTag.equals("open")) {
                        String year = new String(chars, start, length);
                        mhtc[0].setYear(Integer.parseInt(year));
                    }
                    if (numTestedTag.equals("open")) {
                        String numberTested = new String(chars, start, length);
                        mhtc[0].setNumTested(Integer.parseInt(numberTested));
                    }
                    if (numPositiveTag.equals("open")) {
                        String numPositive = new String(chars, start, length);
                        mhtc[0].setNumPositive(Integer.parseInt(numPositive));
                    }
                    if (numReferredTag.equals("open")) {
                        String numReferred = new String(chars, start, length);
                        mhtc[0].setNumReferred(Integer.parseInt(numReferred));
                    }
                    if (numOnsiteVisitTag.equals("open")) {
                        String numOnsiteVisit = new String(chars, start, length);
                        mhtc[0].setNumOnsiteVisit(Integer.parseInt(numOnsiteVisit));
                    }
                    if (uploadedTag.equals("open")) {
                        mhtc[0].setUploaded(Integer.parseInt(new String(chars, start, length)));
                    }
                    if (timeStampTag.equals("open")) {
                        mhtc[0].setTimeStamp(new java.sql.Date(new Date().getTime()));
                    }
                    if (timeUploadedTag.equals("open")) {
                        mhtc[0].setTimeUploaded(new java.sql.Date(new Date().getTime()));
                    }
                    if (deviceconfigIdTag.equals("open")) {
                        mhtc[0].setDeviceconfigId(Long.parseLong(new String(chars, start, length)));
                    }
                }

                @Override
                public void endElement(String uri, String localName, String element) throws SAXException {
                    if (element.equalsIgnoreCase("mhtc_id")) {
                        mhtcIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("communitypharm_id")) {
                        communitypharmIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("month")) {
                        monthTag = "close";
                    }
                    if (element.equalsIgnoreCase("year")) {
                        yearTag = "close";
                    }
                    if (element.equalsIgnoreCase("num_tested")) {
                        numTestedTag = "close";
                    }
                    if (element.equalsIgnoreCase("num_positive")) {
                        numPositiveTag = "close";
                    }
                    if (element.equalsIgnoreCase("num_referred")) {
                        numReferredTag = "close";
                    }
                    if (element.equalsIgnoreCase("num_onsite_visit")) {
                        numOnsiteVisitTag = "close";
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
                    if (element.equalsIgnoreCase("mhtc")) {
                        mhtcTag = "close";
                        MhtcDAO.save(mhtc[0]);
                    }
                }
            };
            saxParser.parse(xmlFileName, defaultHandler);
        } catch (Exception e) {

        }

    }
}
