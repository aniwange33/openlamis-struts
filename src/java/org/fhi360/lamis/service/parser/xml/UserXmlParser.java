/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.parser.xml;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.fhi360.lamis.dao.hibernate.UserDAO;
import org.fhi360.lamis.model.User;
import org.fhi360.lamis.service.EntityIdentifier;
import org.fhi360.lamis.utility.DateUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author user10
 */
public class UserXmlParser extends DefaultHandler {

    private long facilityId, userId;
    private long idOnServer;
    private Boolean populated;
    private User user;
    private EntityIdentifier entityIdentifier = new EntityIdentifier();

    public UserXmlParser() {
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
                String userTag = "close";
                String userIdTag = "close";
                String usernameTag = "close";
                String passwordTag = "close";
                String fullnameTag = "close";
                String facilityIdTag = "close";
                String timeLoginTag = "close";
                String viewIdentifierTag = "close";
                String userGroupTag = "close";
                String timeStampTag = "close";
                String idUUIDTag = "close";

                //this method is called every time the parser gets an open tag '<'
                //identifies which tag is being open at the time by assigning an open flag
                @Override
                public void startElement(String uri, String localName, String element, Attributes attributes) throws SAXException {
                    if (element.equalsIgnoreCase("user")) {
                        userTag = "open";
                        user = new User();
                    }
                    if (element.equalsIgnoreCase("user_id")) {
                        userIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("username")) {
                        usernameTag = "open";
                    }
                    if (element.equalsIgnoreCase("password")) {
                        passwordTag = "open";
                    }
                    if (element.equalsIgnoreCase("fullname")) {
                        fullnameTag = "open";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("time_login")) {
                        timeLoginTag = "open";
                    }
                    if (element.equalsIgnoreCase("view_identifier")) {
                        viewIdentifierTag = "open";
                    }
                    if (element.equalsIgnoreCase("user_group")) {
                        userGroupTag = "open";
                    }
                    if (element.equalsIgnoreCase("time_stamp")) {
                        timeStampTag = "open";
                    }
                    if (element.equalsIgnoreCase("id_uuid")) {
                        idUUIDTag = "close";
                    }
                }

                //store data store in between '<' and '>' tags                     
                @Override
                public void characters(char[] chars, int start, int length) throws SAXException {
                    if (facilityIdTag.equals("open")) {
                        facilityId = Long.parseLong(new String(chars, start, length));
                        user.setFacilityId(facilityId);
                    }
                    if (userIdTag.equals("open")) {
                        userId = Long.parseLong(new String(chars, start, length));
                    }
                    if (fullnameTag.equals("open")) {
                        user.setFullname(new String(chars, start, length));
                    }
                    if (usernameTag.equals("open")) {
                        user.setUsername(new String(chars, start, length));
                    }
                    if (passwordTag.equals("open")) {
                        user.setPassword(new String(chars, start, length));
                    }
                    if (timeLoginTag.equals("open")) {
                        user.setTimeLogin(DateUtil.getSqlTimeStamp(new String(chars, start, length)));
                    }
                    if (viewIdentifierTag.equals("open")) {
                        user.setViewIdentifier(Integer.parseInt(new String(chars, start, length)));
                    }
                    if (userGroupTag.equals("open")) {
                        user.setUserGroup(new String(chars, start, length));
                    }
                    if (timeStampTag.equals("open")) {
                        user.setTimeStamp(new java.sql.Date(new java.util.Date().getTime()));
                    }
                    if (idUUIDTag.equals("open")) {
                        user.setUuid(new String(chars, start, length));
                    }
                }

                @Override
                public void endElement(String uri, String localName, String element) throws SAXException {
                    if (element.equalsIgnoreCase("user")) {
                        userTag = "close";
                    }
                    if (element.equalsIgnoreCase("user_id")) {
                        userIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("username")) {
                        usernameTag = "close";
                    }
                    if (element.equalsIgnoreCase("password")) {
                        passwordTag = "close";
                    }
                    if (element.equalsIgnoreCase("fullname")) {
                        fullnameTag = "close";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("time_login")) {
                        timeLoginTag = "close";
                    }
                    if (element.equalsIgnoreCase("view_identifier")) {
                        viewIdentifierTag = "close";
                    }
                    if (element.equalsIgnoreCase("user_group")) {
                        userGroupTag = "close";
                    }
                    if (element.equalsIgnoreCase("time_stamp")) {
                        timeStampTag = "close";
                    }
                    if (element.equalsIgnoreCase("id_uuid")) {
                        idUUIDTag = "close";
                    }

                    //if this is the closing tag of a clinic element save the record
                    if (element.equalsIgnoreCase("user")) {
                        userTag = "close";
                        user.setLocalId(userId);

                        //Retrieve the clinic ID from the server if this clinic exists
                        String query = "SELECT user_id AS id_on_server FROM user WHERE local_id = " + userId + " AND facility_id = " + facilityId;
                        idOnServer = entityIdentifier.getIdOnServer(query); //check if this record exist on the server
                        if (idOnServer == 0) {
                            idOnServer = UserDAO.save(user);
                            entityIdentifier.executeUpdate(query);
                        } else {
                            user.setUserId(idOnServer);
                            UserDAO.update(user);
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
