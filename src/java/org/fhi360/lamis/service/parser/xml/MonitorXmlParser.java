/**
 *
 * @author aalozie
 */

package org.fhi360.lamis.service.parser.xml;

import java.util.List;
import org.fhi360.lamis.service.DeleteService;
import org.fhi360.lamis.utility.PatientNumberNormalizer;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MonitorXmlParser {
    private String[] entityId;
    private long facilityId;
    private long idOnServer;
    private String tableName;
    private int operationId;
    private boolean populated;
    boolean skipRecord = false;
    private DeleteService deleteService = new DeleteService();

    public MonitorXmlParser() {
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
                String monitorTag = "close";
                String facilityIdTag = "close";
                String entityIdTag = "close";
                String tableNameTag = "close";
                String operationIdTag = "close";
                
                //this method is called every time the parser gets an open tag '<'
                //identifies which element is being open at the time by assigning an open flag
                @Override
                public void startElement(String uri, String localName, String element, Attributes attributes) throws SAXException {
                    if (element.equalsIgnoreCase("monitor")) {
                        monitorTag = "open";
                        skipRecord = false;
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("entity_id")) {
                        entityIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("table_name")) {
                        tableNameTag = "open";
                    }
                    if (element.equalsIgnoreCase("operation_id")) {
                        operationIdTag = "open";
                    }
                }
                
                //store data store in between '<' and '>' tags                     
                @Override
                public void characters(char[] chars, int start, int length) throws SAXException {
                    if (facilityIdTag.equals("open")) {
                        facilityId = Long.parseLong(new String(chars, start, length));           
                    }                    
                    if (entityIdTag.equals("open")) {
                        entityId = (new String(chars, start, length)).split("#");   //see Monitor interceptor for format of entity id
                        if (entityId.length < 3) {
                            skipRecord = true;
                        }
                    }
                    if (tableNameTag.equals("open")) {
                        tableName = new String(chars, start, length);
                    }                    
                    if (operationIdTag.equals("open")) {
                        operationId = Integer.parseInt(new String(chars, start, length));
                    }                    
                }
                
                @Override
                public void endElement(String uri, String localName, String element) throws SAXException {
                    if (element.equalsIgnoreCase("monitor")) {
                        monitorTag = "close";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("entity_id")) {
                        entityIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("table_name")) {
                        tableNameTag = "close";
                    }
                    if (element.equalsIgnoreCase("operation_id")) {
                        operationIdTag = "close";
                    }
                    
                    //if this is the closing tag of a monitor element save the record
                    if (element.equalsIgnoreCase("monitor")) {
                        monitorTag = "close";
                        if (entityId.length > 0 && !skipRecord) {
                            JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
                            String query = "SELECT patient_id FROM PATIENT WHERE TRIM(LEADING '0' FROM hospital_num) = ?" +
                                    " and FACILITY_ID = ?";
                            List<Long> patientIds = jdbcTemplate.queryForList(query, Long.class,
                                    PatientNumberNormalizer.unpadNumber(entityId[0]), facilityId);
                            if (patientIds.size() > 0) {
                                if (operationId == 3) {
                                    if (tableName.equals("patient")) {
                                        deleteService.deletePatient(facilityId, patientIds.get(0));
                                    }
                                    if (tableName.equals("clinic") && entityId[1] != null) {
                                        deleteService.deleteClinic(facilityId, patientIds.get(0),
                                                DateUtil.parseStringToDate(entityId[1], "MM/dd/yyyy"));
                                    }
                                    if (tableName.equals("pharmacy") && entityId[1] != null) {
                                        deleteService.deletePharmacy(facilityId, patientIds.get(0),
                                                DateUtil.parseStringToDate(entityId[1], "MM/dd/yyyy"));
                                    }
                                    if (tableName.equals("laboratory") && entityId[1] != null) {
                                        deleteService.deleteLaboratory(facilityId, patientIds.get(0),
                                                DateUtil.parseStringToDate(entityId[1], "MM/dd/yyyy"));
                                    }

                                    if (tableName.equals("statushistory") && entityId[1] != null && entityId[2] != null) {
                                        deleteService.deleteStatus(facilityId, patientIds.get(0),
                                                entityId[1], DateUtil.parseStringToDate(entityId[2], "MM/dd/yyyy"));
                                    }
                                    if (tableName.equals("chroniccare") && entityId[1] != null) {
                                        deleteService.deleteChroniccare(facilityId, patientIds.get(0),
                                                DateUtil.parseStringToDate(entityId[1], "MM/dd/yyyy"));
                                    }
                                    if (tableName.equals("anc")) {
                                        deleteService.deleteAnc(facilityId, patientIds.get(0));
                                    }
                                    if (tableName.equals("delivery")) {
                                        deleteService.deleteDelivery(facilityId, patientIds.get(0));
                                    }
                                    if (tableName.equals("child")) {
                                        deleteService.deleteChild(facilityId, patientIds.get(0));
                                    }
                                    if (tableName.equals("childfollowup")) {
                                        deleteService.deleteChildfollowup(facilityId, patientIds.get(0));
                                    }
                                    if (tableName.equals("maternalfollowup")) {
                                        deleteService.deleteMaternalfollowup(facilityId, patientIds.get(0));
                                    }
                                    if (tableName.equals("specimen") && entityId[0] != null) {
                                        deleteService.deleteSpecimen(facilityId, entityId[0]);
                                    }
                                    if (tableName.equals("nigqual") && entityId[0] != null) {
                                        deleteService.deleteNigqual(facilityId, Integer.parseInt(entityId[0]));
                                    }
                                }
                                if (tableName.equals("patient") && operationId == 4) {
                                    if (entityId[0] != null && entityId[1] != null) {
                                        //new PatientHospitalNumberUpdater().changeHospitalNum(entityId[0], entityId[1], facilityId);
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
        }
        catch(Exception exception) {
            exception.printStackTrace();            
        }
    }   
  
}
