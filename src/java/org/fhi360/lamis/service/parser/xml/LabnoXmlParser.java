/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.service.parser.xml;

import org.fhi360.lamis.utility.LabNumberNormalizer;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class LabnoXmlParser extends DefaultHandler {

    private long facilityId;
    private int year;
    private int lastno;

    public LabnoXmlParser() {
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
                String labnoTag = "close";
                String facilityIdTag = "close";
                String yearTag = "close";
                String lastnoTag = "close";

                //this method is called every time the parser gets an open tag '<'
                //identifies which tag is being open at the time by assigning an open flag
                @Override
                public void startElement(String uri, String localName, String element, Attributes attributes) throws SAXException {
                    if (element.equalsIgnoreCase("labno")) {
                        labnoTag = "open";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("year")) {
                        yearTag = "open";
                    }
                    if (element.equalsIgnoreCase("lastno")) {
                        lastnoTag = "open";
                    }
                }

                //store data store in between '<' and '>' tags                     
                @Override
                public void characters(char[] chars, int start, int length) throws SAXException {
                    if (facilityIdTag.equals("open")) {
                        facilityId = Long.parseLong(new String(chars, start, length));
                    }
                    if (yearTag.equals("open")) {
                        year = Integer.parseInt(new String(chars, start, length));
                    }
                    if (lastnoTag.equals("open")) {
                        lastno = Integer.parseInt(new String(chars, start, length));
                    }
                }

                @Override
                public void endElement(String uri, String localName, String element) throws SAXException {
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("year")) {
                        yearTag = "close";
                    }
                    if (element.equalsIgnoreCase("lastno")) {
                        lastnoTag = "close";
                    }

                    //if this is the closing tag of a labno element save the record
                    if (element.equalsIgnoreCase("labno")) {
                        labnoTag = "close";
                        LabNumberNormalizer.updateLabno(facilityId, year, lastno);
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
