/**
 * @author aalozie
 */
package org.fhi360.lamis.service.parser.xml;

import org.fhi360.lamis.dao.hibernate.ChildDAO;
import org.fhi360.lamis.model.Child;
import org.fhi360.lamis.model.Motherinformation;
import org.fhi360.lamis.service.ServerIDProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.Scrambler;
import org.fhi360.lamis.utility.StringUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class ChildXmlParser extends DefaultHandler {

    private Long facilityId;
    private Boolean skipRecord;
    private Child child;
    private Scrambler scrambler = new Scrambler();

    public ChildXmlParser() {
    }

    public void parseXml(String xmlFileName) {
        skipRecord = false;
        try {
            //obtain and configure a SAX based parser
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

            //obtain object for SAX hadler class
            SAXParser saxParser = saxParserFactory.newSAXParser();

            //default handler for SAX handler class
            // all three methods are written in handler's body
            DefaultHandler defaultHandler = new DefaultHandler() {
                String childTag = "close";
                String childIdTag = "close";
                String patientIdTag = "close";
                String hospitalNumMotherTag = "close";  //weaved into the xml
                String facilityIdTag = "close";
                String deliveryIdTag = "close";
                String motherIdTag = "close";
                String referenceNumTag = "close";
                String hospitalNumTag = "close";
                String surnameTag = "close";
                String otherNamesTag = "close";
                String dateBirthTag = "close";
                String genderTag = "close";
                String bodyWeightTag = "close";
                String apgarScoreTag = "close";
                String statusTag = "close";
                String timeStampTag = "close";
                String userIdTag = "close";
                String uuidTag = "close";
                String id;
                String uuid;
                String hospitalNum;
                String hospitalNumMother;
                String referenceNum;

                //this method is called every time the parser gets an open tag '<'
                //identifies which tag is being open at the time by assigning an open flag
                @Override
                public void startElement(String uri, String localName, String element, Attributes attributes) throws SAXException {
                    if (element.equalsIgnoreCase("child")) {
                        childTag = "open";
                        child = new Child();
                        skipRecord = false;
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("child_id")) {
                        childIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("patient_id")) {
                        patientIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("delivery_id")) {
                        deliveryIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("mother_id")) {
                        motherIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("reference_num")) {
                        referenceNumTag = "open";
                    }
                    if (element.equalsIgnoreCase("hospital_num")) {
                        hospitalNumTag = "open";
                    }
                    if (element.equalsIgnoreCase("hospital_num_mother")) {
                        hospitalNumMotherTag = "open";
                    }
                    if (element.equalsIgnoreCase("surname")) {
                        surnameTag = "open";
                    }
                    if (element.equalsIgnoreCase("other_names")) {
                        otherNamesTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_birth")) {
                        dateBirthTag = "open";
                    }
                    if (element.equalsIgnoreCase("gender")) {
                        genderTag = "open";
                    }
                    if (element.equalsIgnoreCase("body_weight")) {
                        bodyWeightTag = "open";
                    }
                    if (element.equalsIgnoreCase("apgar_score")) {
                        apgarScoreTag = "open";
                    }
                    if (element.equalsIgnoreCase("status")) {
                        statusTag = "open";
                    }
                    if (element.equalsIgnoreCase("time_stamp")) {
                        timeStampTag = "open";
                    }
                    if (element.equalsIgnoreCase("user_id")) {
                        userIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("id_uuid") || element.equalsIgnoreCase("uuid")) {
                        uuidTag = "open";
                    }
                }

                //store data store in between '<' and '>' tags                     
                @Override
                public void characters(char[] chars, int start, int length) throws SAXException {
                    if (facilityIdTag.equals("open")) {
                        facilityId = Long.parseLong(new String(chars, start, length));
                        child.setFacilityId(facilityId);
                    }
                    if (deliveryIdTag.equals("open")) {
                        String deliveryId = new String(chars, start, length);
                        if (!deliveryId.trim().trim().isEmpty()) {
                            if (StringUtil.isLong(deliveryId))
                                child.setDeliveryId(Long.parseLong(deliveryId));
                            else skipRecord = true;
                        }
                    }
                    if (referenceNumTag.equals("open")) {
                        referenceNum = new String(chars, start, length);
                        child.setReferenceNum(referenceNum);
                    }
                    if (hospitalNumTag.equals("open")) {
                        hospitalNum = new String(chars, start, length);
                        child.setHospitalNumber(hospitalNum);
                    }
                    if (hospitalNumMotherTag.equals("open")) {
                        hospitalNumMother = new String(chars, start, length);
                    }
                    if (surnameTag.equals("open")) {
                        String surname = new String(chars, start, length);
                        if (!surname.isEmpty()) {
                            child.setSurname(scrambler.scrambleCharacters(surname));
                        }
                    }
                    if (otherNamesTag.equals("open")) {
                        String otherNames = new String(chars, start, length);
                        if (!otherNames.isEmpty()) {
                            child.setOtherNames(scrambler.scrambleCharacters(otherNames));
                        }
                    }
                    if (genderTag.equals("open")) {
                        child.setGender(new String(chars, start, length));
                    }
                    if (dateBirthTag.equals("open")) {
                        String dateBirth = new String(chars, start, length);
                        if (!dateBirth.trim().isEmpty()) {
                            child.setDateBirth(DateUtil.parseStringToDate(dateBirth, "yyyy-MM-dd"));
                        } else {
                            skipRecord = true;
                        }
                    }
                    if (bodyWeightTag.equals("open")) {
                        String bodyWeight = new String(chars, start, length);
                        if (!bodyWeight.trim().isEmpty()) {
                            if (StringUtil.isDouble(bodyWeight))
                                child.setBodyWeight(Double.parseDouble(bodyWeight));
                            else skipRecord = true;
                        }
                    }
                    if (apgarScoreTag.equals("open")) {
                        String apgarScore = new String(chars, start, length);
                        if (!apgarScore.trim().trim().isEmpty()) {
                            if (StringUtil.isInt(apgarScore))
                                child.setApgarScore(Integer.parseInt(apgarScore));
                            else skipRecord = true;
                        }
                    }
                    if (statusTag.equals("open")) {
                        child.setStatus(new String(chars, start, length));
                    }
                    if (timeStampTag.equals("open")) {
                        child.setTimeStamp(new java.sql.Date(new java.util.Date().getTime()));
                    }
                    if (uuidTag.equals("open")) {
                        uuid = new String(chars, start, length);
                        child.setUuid(uuid);
                    }
                }

                @Override
                public void endElement(String uri, String localName, String element) throws SAXException {
                    if (element.equalsIgnoreCase("child_id")) {
                        childIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("patient_id")) {
                        patientIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("delivery_id")) {
                        deliveryIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("reference_num")) {
                        referenceNumTag = "close";
                    }
                    if (element.equalsIgnoreCase("hospital_num")) {
                        hospitalNumTag = "close";
                    }
                    if (element.equalsIgnoreCase("hospital_num_mother")) {
                        hospitalNumMotherTag = "close";
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
                    if (element.equalsIgnoreCase("mother_id")) {
                        motherIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_birth")) {
                        dateBirthTag = "close";
                    }
                    if (element.equalsIgnoreCase("body_weight")) {
                        bodyWeightTag = "close";
                    }
                    if (element.equalsIgnoreCase("apgar_score")) {
                        apgarScoreTag = "close";
                    }
                    if (element.equalsIgnoreCase("status")) {
                        statusTag = "close";
                    }
                    if (element.equalsIgnoreCase("time_stamp")) {
                        timeStampTag = "close";
                    }
                    if (element.equalsIgnoreCase("user_id")) {
                        userIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("uuid")) {
                        uuidTag = "close";
                    }

                    //if this is the closing tag of a child element save the record
                    if (element.equalsIgnoreCase("child")) {
                        childTag = "close";
                        if (skipRecord) {
                            System.out.println("....record has a null value: " + hospitalNum);
                        } else {
                            Long motherId = ServerIDProvider
                                    .getChildMotherInformationId(hospitalNum, facilityId);
                            if (motherId != null) {
                                Motherinformation motherinformation = new Motherinformation();
                                motherinformation.setMotherinformationId(motherId);
                                //child.setMotherinformation(motherinformation);
                                Long id = ServerIDProvider.getChildId(referenceNum, facilityId);
                                if (id != null) {
                                    child.setChildId(id);
                                    ChildDAO.update(child);
                                } else {
                                    try {
                                        ChildDAO.save(child);
                                    }catch (Exception ignored){

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
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
