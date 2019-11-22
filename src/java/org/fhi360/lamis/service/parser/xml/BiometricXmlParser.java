/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.parser.xml;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.fhi360.lamis.dao.hibernate.BiometricDAO;
import org.fhi360.lamis.model.Biometric;
import org.fhi360.lamis.utility.DateUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import java.util.Base64;

/**
 *
 * @author User10
 */
public class BiometricXmlParser extends DefaultHandler {

    public BiometricXmlParser() {
    }
    
    public void parseXml(String xmlFileName) {
        final Biometric[] biometric = new Biometric[]{null};
        try {
            //obtain and configure a SAX based parser
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

            //obtain object for SAX hadler class
            SAXParser saxParser = saxParserFactory.newSAXParser();
            DefaultHandler defaultHandler = new DefaultHandler() {
                String biometricTag = "close";
                String idTag = "close";
                String templateTag = "close";
                String templateTypeTag = "close";
                String biometricTypeTag = "close";
                String patientIDTag = "close";
                String facilityIDTag = "close";
                String hospitalNumTag = "close";
                String patientNameTag = "close";
                String patientPhoneTag = "close";
                String patientGenderTag = "close";
                String patientAddressTag = "close";
                String enrollmentDateTag = "close";

                @Override
                public void startElement(String uri, String localName, String element, Attributes attributes) throws SAXException {
                    if (element.equalsIgnoreCase("biometric")) {
                        biometricTag = "open";
                        biometric[0] = new Biometric();
                    }
                    if (element.equalsIgnoreCase("biometric_id")) {
                        idTag = "open";
                    }
                    if (element.equalsIgnoreCase("template")) {
                        templateTag = "open";
                    }
                    if (element.equalsIgnoreCase("biometric_type")) {
                        biometricTypeTag = "open";
                    }
                    if (element.equalsIgnoreCase("template_type")) {
                        templateTypeTag = "open";
                    }
                    if (element.equalsIgnoreCase("patient_id")) {
                        patientIDTag = "open";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIDTag = "open";
                    }
                    if (element.equalsIgnoreCase("patient_name")) {
                        patientNameTag = "open";
                    }
                    if (element.equalsIgnoreCase("patient_phone")) {
                        patientPhoneTag = "open";
                    }
                    if (element.equalsIgnoreCase("patient_gender")) {
                        patientGenderTag = "open";
                    }
                    if (element.equalsIgnoreCase("patient_address")) {
                        patientAddressTag = "open";
                    }
                    if (element.equalsIgnoreCase("hospital_num")) {
                        hospitalNumTag = "open";
                    }
                    if (element.equalsIgnoreCase("enrollment_date")) {
                        enrollmentDateTag = "open";
                    }
                }

                @Override
                public void characters(char[] chars, int start, int length) throws SAXException {
                    if (idTag.equals("open")) {
                        String id = new String(chars, start, length);
                        biometric[0].setId(id);
                    }
                    if (patientIDTag.equals("open")) {
                        String id = new String(chars, start, length);
                        biometric[0].setPatientId(id);
                    }
                    if (patientNameTag.equals("open")) {
                        String name = new String(chars, start, length);
                        biometric[0].setPatientName(name);
                    }
                    if (patientAddressTag.equals("open")) {
                        String address = new String(chars, start, length);
                        biometric[0].setPatientAddress(address);
                    }
                    if (patientPhoneTag.equals("open")) {
                        String phone = new String(chars, start, length);
                        biometric[0].setPatientPhone(phone);
                    }
                    if (patientGenderTag.equals("open")) {
                        String gender = new String(chars, start, length);
                        biometric[0].setPatientGender(gender);
                    }
                    if (biometricTypeTag.equals("open")) {
                        String type = new String(chars, start, length);
                        biometric[0].setBiometricType(type);
                    }
                    if (templateTypeTag.equals("open")) {
                        String type = new String(chars, start, length);
                        biometric[0].setTemplateType(type);
                    }
                    if (templateTag.equals("open")) {
                        String template = new String(chars, start, length);
                        biometric[0].setTemplate(Base64.getDecoder().decode(template));
                    }
                    if (facilityIDTag.equals("open")) {
                        String id = new String(chars, start, length);
                        biometric[0].setFacilityId(Long.parseLong(id));
                    }
                    if (enrollmentDateTag.equals("open")) {
                        String date = new String(chars, start, length);
                        biometric[0].setEnrollmentDate(DateUtil.parseStringToDate(date, "yyyy-MM-dd"));
                    }
                }

                @Override
                public void endElement(String uri, String localName, String element) throws SAXException {
                    if (element.equalsIgnoreCase("biometric_id")) {
                        idTag = "close";
                    }
                    if (element.equalsIgnoreCase("patient_id")) {
                        patientIDTag = "close";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIDTag = "close";
                    }
                    if (element.equalsIgnoreCase("hospital_num")) {
                        hospitalNumTag = "close";
                    }
                    if (element.equalsIgnoreCase("biometric_type")) {
                        biometricTypeTag = "close";
                    }
                    if (element.equalsIgnoreCase("template_type")) {
                        templateTypeTag = "close";
                    }
                    if (element.equalsIgnoreCase("template")) {
                        templateTag = "close";
                    }
                    if (element.equalsIgnoreCase("patient_address")) {
                        patientAddressTag = "close";
                    }
                    if (element.equalsIgnoreCase("patient_name")) {
                        patientNameTag = "close";
                    }
                    if (element.equalsIgnoreCase("patient_phone")) {
                        patientPhoneTag = "close";
                    }
                    if (element.equalsIgnoreCase("patient_gender")) {
                        patientGenderTag = "close";
                    }
                    if (element.equalsIgnoreCase("enrollment_date")) {
                        enrollmentDateTag = "close";
                    }
                    if (element.equalsIgnoreCase("biometric")) {
                        biometricTag = "close";
                        BiometricDAO.save(biometric[0]);
                    }
                }
            };
            saxParser.parse(xmlFileName, defaultHandler);
        } catch (Exception ignored) {
        }

    }
}
