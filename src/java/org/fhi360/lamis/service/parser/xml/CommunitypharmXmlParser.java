/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.parser.xml;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.fhi360.lamis.dao.hibernate.CommunitypharmDAO;
import org.fhi360.lamis.model.Communitypharm;
import org.fhi360.lamis.service.EntityIdentifier;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author user10
 */
public class CommunitypharmXmlParser extends DefaultHandler {

    private long facilityId, communityPharmId;
    private String pin;
    private long idOnServer;
    private Boolean populated;
    private Communitypharm communityPharm;
    private EntityIdentifier entityIdentifier = new EntityIdentifier();

    public CommunitypharmXmlParser() {
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
                String communitypharmTag = "close";
                String communitypharmIdTag = "close";
                String stateIdTag = "close";
                String lgaIdTag = "close";
                String pharmacyTag = "close";
                String addressTag = "close";
                String phoneTag = "close";
                String phone1Tag = "close";
                String emailTag = "close";
                String pinTag = "close";
                String timeStampTag = "close";
                String idUUIDTag = "close";

                //this method is called every time the parser gets an open tag '<'
                //identifies which tag is being open at the time by assigning an open flag
                @Override
                public void startElement(String uri, String localName, String element, Attributes attributes) throws SAXException {
                    if (element.equalsIgnoreCase("communitypharm")) {
                        communitypharmTag = "open";
                        communityPharm = new Communitypharm();
                    }
                    if (element.equalsIgnoreCase("communitypharm_id")) {
                        communitypharmIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("state_id")) {
                        stateIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("lga_id")) {
                        lgaIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("pharmacy")) {
                        pharmacyTag = "open";
                    }
                    if (element.equalsIgnoreCase("address")) {
                        addressTag = "open";
                    }
                    if (element.equalsIgnoreCase("phone")) {
                        phoneTag = "open";
                    }
                    if (element.equalsIgnoreCase("phone1")) {
                        phone1Tag = "open";
                    }
                    if (element.equalsIgnoreCase("email")) {
                        emailTag = "open";
                    }
                    if (element.equalsIgnoreCase("pin")) {
                        pinTag = "open";
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
                    if (communitypharmIdTag.equals("open")) {
                        communityPharmId = Long.parseLong(new String(chars, start, length));
                    }
                    if (stateIdTag.equals("open")) {
                        communityPharm.setStateId(Long.parseLong(new String(chars, start, length)));
                    }
                    if (lgaIdTag.equals("open")) {
                        communityPharm.setLgaId(Long.parseLong(new String(chars, start, length)));
                    }
                    if (pharmacyTag.equals("open")) {
                        communityPharm.setPharmacy(new String(chars, start, length));
                    }
                    if (addressTag.equals("open")) {
                        communityPharm.setAddress(new String(chars, start, length));
                    }
                    if (phoneTag.equals("open")) {
                        communityPharm.setPhone(new String(chars, start, length));
                    }
                    if (phone1Tag.equals("open")) {
                        communityPharm.setPhone1(new String(chars, start, length));
                    }
                    if (emailTag.equals("open")) {
                        communityPharm.setEmail(new String(chars, start, length));
                    }
                    if (pinTag.equals("open")) {
                        pin = new String(chars, start, length);
                        communityPharm.setPin(pin);
                    }
                    if (timeStampTag.equals("open")) {
                        communityPharm.setTimeStamp(new java.sql.Date(new java.util.Date().getTime()));
                    }
                    if (idUUIDTag.equals("open")) {
                        communityPharm.setUuid(new String(chars, start, length));
                    }
                }

                @Override
                public void endElement(String uri, String localName, String element) throws SAXException {
                    if (element.equalsIgnoreCase("communitypharm")) {
                        communitypharmTag = "close";
                    }
                    if (element.equalsIgnoreCase("communitypharm_id")) {
                        communitypharmIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("state_id")) {
                        stateIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("lga_id")) {
                        lgaIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("pharmacy")) {
                        pharmacyTag = "close";
                    }
                    if (element.equalsIgnoreCase("address")) {
                        addressTag = "close";
                    }
                    if (element.equalsIgnoreCase("phone")) {
                        phoneTag = "close";
                    }
                    if (element.equalsIgnoreCase("phone1")) {
                        phone1Tag = "close";
                    }
                    if (element.equalsIgnoreCase("email")) {
                        emailTag = "close";
                    }
                    if (element.equalsIgnoreCase("pin")) {
                        pinTag = "close";
                    }
                    if (element.equalsIgnoreCase("time_stamp")) {
                        timeStampTag = "close";
                    }
                    if (element.equalsIgnoreCase("id_uuid") || element.equalsIgnoreCase("uuid")) {
                        idUUIDTag = "close";
                    }

                    //if this is the closing tag of a clinic element save the record
                    if (element.equalsIgnoreCase("communitypharm")) {
                        communitypharmTag = "close";

                        //Retrieve the clinic ID from the server if this clinic exists
                        String query = "SELECT communitypharm_id AS id_on_server FROM communitypharm WHERE pin = '" + pin + "'";
                        idOnServer = entityIdentifier.getIdOnServer(query); //check if this record exist on the server
                        if (idOnServer == 0) {
                            idOnServer = CommunitypharmDAO.save(communityPharm);
                            entityIdentifier.executeUpdate(query);
                        } else {
                            communityPharm.setCommunitypharmId(idOnServer);
                            CommunitypharmDAO.update(communityPharm);
                        }
                    }
                }
            };
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }

}
