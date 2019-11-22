/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.service.parser.xml;

import org.fhi360.lamis.dao.hibernate.SpecimenDAO;
import org.fhi360.lamis.model.Specimen;
import org.fhi360.lamis.utility.Scrambler;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.fhi360.lamis.service.ServerIDProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SpecimenXmlParser extends DefaultHandler {

    private long facilityId;
    private long idOnServer;
    private Boolean populated;
    private Specimen specimen;
    private String labno;
    private Scrambler scrambler = new Scrambler();

    public SpecimenXmlParser() {
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
                String specimenTag = "close";
                String specimenIdTag = "close";
                String facilityIdTag = "close";
                String stateIdTag = "close";
                String lgaIdTag = "close";
                String treatmentUnitIdTag = "close";
                String specimenTypeTag = "close";
                String labnoTag = "close";
                String barcodeTag = "close";
                String dateReceivedTag = "close";
                String dateCollectedTag = "close";
                String dateAssayTag = "close";
                String dateReportedTag = "close";
                String dateDispatchedTag = "close";
                String qualityCntrlTag = "close";
                String resultTag = "close";
                String reasonNoTestTag = "close";
                String hospitalNumTag = "close";
                String surnameTag = "close";
                String otherNamesTag = "close";
                String genderTag = "close";
                String dateBirthTag = "close";
                String ageTag = "close";
                String ageUnitTag = "close";
                String addressTag = "close";
                String phoneTag = "close";
                String userIdTag = "close";
                String timeStampTag = "close";
                String idUUIDTag = "close";

                //this method is called every time the parser gets an open tag '<'
                //identifies which tag is being open at the time by assigning an open flag
                @Override
                public void startElement(String uri, String localName, String element, Attributes attributes) throws SAXException {
                    if (element.equalsIgnoreCase("specimen")) {
                        specimenTag = "open";
                        specimen = new Specimen();
                    }
                    if (element.equalsIgnoreCase("specimen_id")) {
                        specimenIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("state_id")) {
                        stateIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("lga_id")) {
                        lgaIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("treatment_unit_id")) {
                        treatmentUnitIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("specimen_type")) {
                        specimenTypeTag = "open";
                    }
                    if (element.equalsIgnoreCase("labno")) {
                        labnoTag = "open";
                    }
                    if (element.equalsIgnoreCase("barcode")) {
                        barcodeTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_received")) {
                        dateReceivedTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_collected")) {
                        dateCollectedTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_assay")) {
                        dateAssayTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_reported")) {
                        dateReportedTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_dispatched")) {
                        dateDispatchedTag = "open";
                    }
                    if (element.equalsIgnoreCase("quality_cntrl")) {
                        qualityCntrlTag = "open";
                    }
                    if (element.equalsIgnoreCase("result")) {
                        resultTag = "open";
                    }
                    if (element.equalsIgnoreCase("reason_no_test")) {
                        reasonNoTestTag = "open";
                    }
                    if (element.equalsIgnoreCase("hospital_num")) {
                        hospitalNumTag = "open";
                    }
                    if (element.equalsIgnoreCase("surname")) {
                        surnameTag = "open";
                    }
                    if (element.equalsIgnoreCase("other_names")) {
                        otherNamesTag = "open";
                    }
                    if (element.equalsIgnoreCase("gender")) {
                        genderTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_birth")) {
                        dateBirthTag = "open";
                    }
                    if (element.equalsIgnoreCase("age")) {
                        ageTag = "open";
                    }
                    if (element.equalsIgnoreCase("age_unit")) {
                        ageUnitTag = "open";
                    }
                    if (element.equalsIgnoreCase("address")) {
                        addressTag = "open";
                    }
                    if (element.equalsIgnoreCase("phone")) {
                        phoneTag = "open";
                    }
                    if (element.equalsIgnoreCase("user_id")) {
                        userIdTag = "open";
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
                        specimen.setFacilityId(facilityId);
                    }
                    if (specimenTypeTag.equals("open")) {
                        specimen.setSpecimenType(new String(chars, start, length));
                    }
                    if (stateIdTag.equals("open")) {
                        specimen.setStateId(Long.parseLong(new String(chars, start, length)));
                    }
                    if (lgaIdTag.equals("open")) {
                        specimen.setLgaId(Long.parseLong(new String(chars, start, length)));
                    }
                    if (treatmentUnitIdTag.equals("open")) {
                        specimen.setTreatmentUnitId(Long.parseLong(new String(chars, start, length)));
                    }
                    if (labnoTag.equals("open")) {
                        labno = (new String(chars, start, length));
                        specimen.setLabno(labno);
                    }
                    if (barcodeTag.equals("open")) {
                        specimen.setBarcode(new String(chars, start, length));
                    }
                    if (dateReceivedTag.equals("open")) {
                        String dateReceived = new String(chars, start, length);
                        if (!dateReceived.trim().isEmpty()) {
                            specimen.setDateReceived(DateUtil.parseStringToDate(dateReceived, "yyyy-MM-dd"));
                        }
                    }
                    if (dateCollectedTag.equals("open")) {
                        String dateCollected = new String(chars, start, length);
                        if (!dateCollected.trim().isEmpty()) {
                            specimen.setDateCollected(DateUtil.parseStringToDate(dateCollected, "yyyy-MM-dd"));
                        }
                    }
                    if (dateAssayTag.equals("open")) {
                        String dateAssay = new String(chars, start, length);
                        if (!dateAssay.trim().isEmpty()) {
                            specimen.setDateAssay(DateUtil.parseStringToDate(dateAssay, "yyyy-MM-dd"));
                        }
                    }
                    if (dateReportedTag.equals("open")) {
                        String dateReported = new String(chars, start, length);
                        if (!dateReported.trim().isEmpty()) {
                            specimen.setDateReported(DateUtil.parseStringToDate(dateReported, "yyyy-MM-dd"));
                        }
                    }
                    if (dateDispatchedTag.equals("open")) {
                        String dateDispatched = new String(chars, start, length);
                        if (!dateDispatched.trim().isEmpty()) {
                            specimen.setDateDispatched(DateUtil.parseStringToDate(dateDispatched, "yyyy-MM-dd"));
                        }
                    }
                    if (qualityCntrlTag.equals("open")) {
                        String qualityCntrl = new String(chars, start, length);
                        if (!qualityCntrl.trim().isEmpty()) {
                            specimen.setQualityCntrl(Integer.parseInt(qualityCntrl));
                        }
                    }
                    if (resultTag.equals("open")) {
                        specimen.setResult(new String(chars, start, length));
                    }
                    if (reasonNoTestTag.equals("open")) {
                        specimen.setReasonNoTest(new String(chars, start, length));
                    }
                    if (hospitalNumTag.equals("open")) {
                        specimen.setHospitalNum(new String(chars, start, length));
                    }
                    if (surnameTag.equals("open")) {
                        specimen.setSurname(scrambler.scrambleCharacters(new String(chars, start, length)));
                    }
                    if (otherNamesTag.equals("open")) {
                        specimen.setOtherNames(scrambler.scrambleCharacters(new String(chars, start, length)));
                    }
                    if (genderTag.equals("open")) {
                        specimen.setGender(new String(chars, start, length));
                    }
                    if (dateBirthTag.equals("open")) {
                        String dateBirth = new String(chars, start, length);
                        if (!dateBirth.trim().isEmpty()) {
                            specimen.setDateBirth(DateUtil.parseStringToDate(dateBirth, "yyyy-MM-dd"));
                        }
                    }
                    if (ageTag.equals("open")) {
                        String age = new String(chars, start, length);
                        if (!age.trim().isEmpty()) {
                            specimen.setAge(Integer.parseInt(age));
                        }
                    }
                    if (ageUnitTag.equals("open")) {
                        specimen.setAgeUnit(new String(chars, start, length));
                    }
                    if (addressTag.equals("open")) {
                        specimen.setAddress(scrambler.scrambleCharacters(new String(chars, start, length)));
                    }
                    if (phoneTag.equals("open")) {
                        specimen.setPhone(scrambler.scrambleNumbers(new String(chars, start, length)));
                    }
                    if (timeStampTag.equals("open")) {
                        specimen.setTimeStamp(new java.sql.Date(new java.util.Date().getTime()));
                    }
                    if (userIdTag.equals("open")) {
                        String userId = new String(chars, start, length);
                        if (!userId.trim().trim().isEmpty()) {
                            specimen.setUserId(Long.parseLong(userId));
                        }
                    }
                    if (idUUIDTag.equals("open")) {
                        specimen.setUuid(new String(chars, start, length));
                    }
                }

                @Override
                public void endElement(String uri, String localName, String element) throws SAXException {
                    if (element.equalsIgnoreCase("specimen_id")) {
                        specimenIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("specimen_type")) {
                        specimenTypeTag = "close";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("labno")) {
                        labnoTag = "close";
                    }
                    if (element.equalsIgnoreCase("barcode")) {
                        barcodeTag = "close";
                    }
                    if (element.equalsIgnoreCase("state_id")) {
                        stateIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("lga_id")) {
                        lgaIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("treatment_unit_id")) {
                        treatmentUnitIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_received")) {
                        dateReceivedTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_collected")) {
                        dateCollectedTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_assay")) {
                        dateAssayTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_reported")) {
                        dateReportedTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_dispatched")) {
                        dateDispatchedTag = "close";
                    }
                    if (element.equalsIgnoreCase("quality_cntrl")) {
                        qualityCntrlTag = "close";
                    }
                    if (element.equalsIgnoreCase("result")) {
                        resultTag = "close";
                    }
                    if (element.equalsIgnoreCase("reason_no_test")) {
                        reasonNoTestTag = "close";
                    }
                    if (element.equalsIgnoreCase("hospital_num")) {
                        hospitalNumTag = "close";
                    }
                    if (element.equalsIgnoreCase("surname")) {
                        surnameTag = "close";
                    }
                    if (element.equalsIgnoreCase("other_names")) {
                        otherNamesTag = "close";
                    }
                    if (element.equalsIgnoreCase("gender")) {
                        genderTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_birth")) {
                        dateBirthTag = "close";
                    }
                    if (element.equalsIgnoreCase("age")) {
                        ageTag = "close";
                    }
                    if (element.equalsIgnoreCase("age_unit")) {
                        ageUnitTag = "close";
                    }
                    if (element.equalsIgnoreCase("address")) {
                        addressTag = "close";
                    }
                    if (element.equalsIgnoreCase("phone")) {
                        phoneTag = "close";
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

                    //if this is the closing tag of a specimen element save the record
                    if (element.equalsIgnoreCase("specimen")) {
                        specimenTag = "close";
                        Long id = ServerIDProvider
                                .getSpecimenId(specimen.getLabno(), facilityId);
                        if (id != null) {
                            specimen.setSpecimenId(id);
                            SpecimenDAO.update(specimen);
                        } else {
                            SpecimenDAO.save(specimen);
                        }
                    }
                }
            };

            //parse the XML specified in the given path and uses supplied handler to parse the document
            //this calls startElement(), endElement(), and character() methods accordingly
            saxParser.parse(xmlFileName, defaultHandler);
            //new CleanupService().cleanNullFields("specimen", facilityId);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }

}
