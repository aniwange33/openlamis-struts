/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.service.parser.xml;

import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.utility.PatientNumberNormalizer;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.fhi360.lamis.dao.hibernate.NigqualDAO;
import org.fhi360.lamis.model.Nigqual;
import org.fhi360.lamis.service.ServerIDProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class NigqualXmlParser extends DefaultHandler {

    private long facilityId;
    private String hospitalNum;
    private int reviewPeriodId;
    private long idOnServer;
    private boolean populated;
    private Nigqual nigqual;
    private Patient patient = new Patient();

    public NigqualXmlParser() {
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
                String nigqualTag = "close";
                String nigqualIdTag = "close";
                String patientIdTag = "close";
                String facilityIdTag = "close";
                String portalIdTag = "close";
                String hospitalNumTag = "close";
                String reportingDateBeginTag = "close";
                String reportingDateEndTag = "close";
                String reviewPeriodIdTag = "close";
                String thermaticAreaTag = "close";
                String populationTag = "close";
                String sampleSizeTag = "close";
                String timeStampTag = "close";
                String idUUIDTag = "close";

                //this method is called every time the parser gets an open tag '<'
                //identifies which tag is being open at the time by assigning an open flag
                @Override
                public void startElement(String uri, String localName, String element, Attributes attributes) throws SAXException {
                    if (element.equalsIgnoreCase("nigqual")) {
                        nigqualTag = "open";
                        nigqual = new Nigqual();
                    }
                    if (element.equalsIgnoreCase("nigqual_id")) {
                        nigqualIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("patient_id")) {
                        patientIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("portal_id")) {
                        portalIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("hospital_num")) {
                        hospitalNumTag = "open";
                    }
                    if (element.equalsIgnoreCase("reporting_date_begin")) {
                        reportingDateBeginTag = "open";
                    }
                    if (element.equalsIgnoreCase("reporting_date_end")) {
                        reportingDateEndTag = "open";
                    }
                    if (element.equalsIgnoreCase("review_period_id")) {
                        reviewPeriodIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("thermatic_area")) {
                        thermaticAreaTag = "open";
                    }
                    if (element.equalsIgnoreCase("population")) {
                        populationTag = "open";
                    }
                    if (element.equalsIgnoreCase("sample_size")) {
                        sampleSizeTag = "open";
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
                        nigqual.setFacilityId(facilityId);
                    }
                    if (hospitalNumTag.equals("open")) {
                        hospitalNum = new String(chars, start, length);
                    }
                    if (reportingDateBeginTag.equals("open")) {
                        String reportingDateBegin = new String(chars, start, length);
                        if (!reportingDateBegin.trim().isEmpty()) {
                            nigqual.setReportingDateBegin(DateUtil.parseStringToDate(reportingDateBegin, "yyyy-MM-dd"));
                        }
                    }
                    if (reportingDateEndTag.equals("open")) {
                        String reportingDateEnd = new String(chars, start, length);
                        if (!reportingDateEnd.trim().isEmpty()) {
                            nigqual.setReportingDateEnd(DateUtil.parseStringToDate(reportingDateEnd, "yyyy-MM-dd"));
                        }
                    }
                    if (reviewPeriodIdTag.equals("open")) {
                        String id = new String(chars, start, length);
                        if (!id.trim().isEmpty()) {
                            reviewPeriodId = Integer.parseInt(id);
                            nigqual.setReviewPeriodId(reviewPeriodId);
                        }
                    }
                    if (thermaticAreaTag.equals("open")) {
                        nigqual.setThermaticArea(new String(chars, start, length));
                    }
                    if (populationTag.equals("open")) {
                        String population = new String(chars, start, length);
                        if (!population.trim().isEmpty()) {
                            nigqual.setPopulation(Integer.parseInt(population));
                        }
                    }
                    if (sampleSizeTag.equals("open")) {
                        String sampleSize = new String(chars, start, length);
                        if (!sampleSize.trim().isEmpty()) {
                            nigqual.setSampleSize(Integer.parseInt(sampleSize));
                        }
                    }
                    if (timeStampTag.equals("open")) {
                        nigqual.setTimeStamp(new java.sql.Date(new java.util.Date().getTime()));
                    }
                    if (idUUIDTag.equals("open")) {
                        nigqual.setUuid(new String(chars, start, length));
                    }
                }

                @Override
                public void endElement(String uri, String localName, String element) throws SAXException {
                    if (element.equalsIgnoreCase("nigqual_id")) {
                        nigqualIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("patient_id")) {
                        patientIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("portal_id")) {
                        portalIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("hospital_num")) {
                        hospitalNumTag = "close";
                    }
                    if (element.equalsIgnoreCase("reporting_date_begin")) {
                        reportingDateBeginTag = "close";
                    }
                    if (element.equalsIgnoreCase("reporting_date_end")) {
                        reportingDateEndTag = "close";
                    }
                    if (element.equalsIgnoreCase("review_period_id")) {
                        reviewPeriodIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("thermatic_area")) {
                        thermaticAreaTag = "close";
                    }
                    if (element.equalsIgnoreCase("population")) {
                        populationTag = "close";
                    }
                    if (element.equalsIgnoreCase("sample_size")) {
                        sampleSizeTag = "close";
                    }
                    if (element.equalsIgnoreCase("time_stamp")) {
                        timeStampTag = "close";
                    }
                    if (element.equalsIgnoreCase("id_uuid") || element.equalsIgnoreCase("uuid")) {
                        idUUIDTag = "close";
                    }

                    //if this is the closing tag of a nigqual element save the record
                    if (element.equalsIgnoreCase("nigqual")) {
                        nigqualTag = "close";
                        Long patientId = ServerIDProvider.getPatientServerId(hospitalNum, facilityId);
                            if (patientId != null) {
                                Patient patient = new Patient();
                                patient.setPatientId(patientId);
                                nigqual.setPatient(patient);
                                Long id = ServerIDProvider
                                        .getPatientDependantId("nigqual", hospitalNum,
                                                nigqual.getReviewPeriodId(), facilityId);
                                if (id != null) {
                                    nigqual.setNigqualId(id);
                                    NigqualDAO.update(nigqual);
                                } else {
                                    NigqualDAO.save(nigqual);
                                }
                            }
                    }
                }
            };

            //parse the XML specified in the given path and uses supplied handler to parse the document
            //this calls startElement(), endElement(), and character() methods accordingly
            saxParser.parse(xmlFileName, defaultHandler);
            //new CleanupService().cleanNullFields("nigqual", facilityId);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }

}
