/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.service.parser.xml;

import org.fhi360.lamis.model.Patient;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.lang3.StringUtils;
import org.fhi360.lamis.dao.hibernate.DeliveryDAO;
import org.fhi360.lamis.model.Delivery;
import org.fhi360.lamis.service.ServerIDProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DeliveryXmlParser extends DefaultHandler {

    private long facilityId;
    private long idOnServer;
    private String hospitalNum;
    private String ancNum;
    private String dateDelivery;
    private Boolean populated;
    private boolean skipRecord;
    private Delivery delivery;
    private Patient patient = new Patient();

    public DeliveryXmlParser() {
    }

    public void parseXml(String xmlFileName) {
        populated = false;
        skipRecord = false;
        try {
            //obtain and configure a SAX based parser
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

            //obtain object for SAX hadler class
            SAXParser saxParser = saxParserFactory.newSAXParser();

            //default handler for SAX handler class
            // all three methods are written in handler's body
            DefaultHandler defaultHandler = new DefaultHandler() {
                String deliveryTag = "close";
                String deliveryIdTag = "close";
                String facilityIdTag = "close";
                String hospitalNumTag = "close";
                String ancNumTag = "close";
                String ancIdTag = "close";
                String bookingStatusTag = "close";
                String dateDeliveryTag = "close";
                String romDeliveryIntervalTag = "close";
                String modeDeliveryTag = "close";
                String episiotomyTag = "close";
                String vaginalTearTag = "close";
                String maternalOutcomeTag = "close";
                String timeHivDiagnosisTag = "close";
                String screenPostPartumTag = "close";
                String arvRegimenPastTag = "close";
                String arvRegimenCurrentTag = "close";
                String dateArvRegimenCurrentTag = "close";
                String dateConfirmedHivTag = "close";
                String clinicStageTag = "close";
                String cd4OrderedTag = "close";
                String cd4Tag = "close";
                String timeStampTag = "close";
                String userIdTag = "close";
                String idUUIDTag = "close";

                //this method is called every time the parser gets an open tag '<'
                //identifies which tag is being open at the time by assigning an open flag
                @Override
                public void startElement(String uri, String localName, String element, Attributes attributes) throws SAXException {
                    if (element.equalsIgnoreCase("delivery")) {
                        deliveryTag = "open";
                        delivery = new Delivery();
                    }
                    if (element.equalsIgnoreCase("delivery_id")) {
                        deliveryIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_delivery")) {
                        dateDeliveryTag = "open";
                    }
                    if (element.equalsIgnoreCase("hospital_num")) {
                        hospitalNumTag = "open";
                    }
                    if (element.equalsIgnoreCase("anc_num")) {
                        ancNumTag = "open";
                    }
                    if (element.equalsIgnoreCase("anc_id")) {
                        ancIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("booking_status")) {
                        bookingStatusTag = "open";
                    }
                    if (element.equalsIgnoreCase("rom_delivery_interval")) {
                        romDeliveryIntervalTag = "open";
                    }
                    if (element.equalsIgnoreCase("mode_delivery")) {
                        modeDeliveryTag = "open";
                    }
                    if (element.equalsIgnoreCase("episiotomy")) {
                        episiotomyTag = "open";
                    }
                    if (element.equalsIgnoreCase("vaginal_tear")) {
                        vaginalTearTag = "open";
                    }
                    if (element.equalsIgnoreCase("maternal_outcome")) {
                        maternalOutcomeTag = "open";
                    }
                    if (element.equalsIgnoreCase("time_hiv_diagnosis")) {
                        timeHivDiagnosisTag = "open";
                    }
                    if (element.equalsIgnoreCase("screen_post_partum")) {
                        screenPostPartumTag = "open";
                    }
                    if (element.equalsIgnoreCase("arv_regimen_past")) {
                        arvRegimenPastTag = "open";
                    }
                    if (element.equalsIgnoreCase("arv_regimen_current")) {
                        arvRegimenCurrentTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_arv_regimen_current")) {
                        dateArvRegimenCurrentTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_confirmed_hiv")) {
                        dateConfirmedHivTag = "open";
                    }
                    if (element.equalsIgnoreCase("clinic_stage")) {
                        clinicStageTag = "open";
                    }
                    if (element.equalsIgnoreCase("cd4_ordered")) {
                        cd4OrderedTag = "open";
                    }
                    if (element.equalsIgnoreCase("cd4")) {
                        cd4Tag = "open";
                    }
                    if (element.equalsIgnoreCase("time_stamp")) {
                        timeStampTag = "open";
                    }
                    if (element.equalsIgnoreCase("user_id")) {
                        userIdTag = "open";
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
                        delivery.setFacilityId(facilityId);
                    }
                    if (hospitalNumTag.equals("open")) {
                        hospitalNum = new String(chars, start, length);
                    }
                    if (ancNumTag.equals("open")) {
                        ancNum = new String(chars, start, length);
                    }
                    if (bookingStatusTag.equals("open")) {
                        String bookingStatus = new String(chars, start, length);
                        if (!bookingStatus.trim().isEmpty()) {
                            delivery.setBookingStatus(Integer.parseInt(bookingStatus));
                        }
                    }
                    if (dateDeliveryTag.equals("open")) {
                        dateDelivery = new String(chars, start, length);
                        if (!dateDelivery.trim().isEmpty()) {
                            delivery.setDateDelivery(DateUtil.parseStringToDate(dateDelivery, "yyyy-MM-dd"));
                        } else {
                            skipRecord = true;
                        }
                    }
                    if (romDeliveryIntervalTag.equals("open")) {
                        delivery.setRomDeliveryInterval(new String(chars, start, length));
                    }
                    if (modeDeliveryTag.equals("open")) {
                        delivery.setModeDelivery(new String(chars, start, length));
                    }
                    if (episiotomyTag.equals("open")) {
                        delivery.setEpisiotomy(new String(chars, start, length));
                    }
                    if (vaginalTearTag.equals("open")) {
                        delivery.setVaginalTear(new String(chars, start, length));
                    }
                    if (maternalOutcomeTag.equals("open")) {
                        delivery.setMaternalOutcome(new String(chars, start, length));
                    }
                    if (timeHivDiagnosisTag.equals("open")) {
                        delivery.setTimeHivDiagnosis(new String(chars, start, length));
                    }
                    if (screenPostPartumTag.equals("open")) {
                        String screenPostPartum = new String(chars, start, length);
                        if (!screenPostPartum.trim().isEmpty()) {
                            delivery.setScreenPostPartum(Integer.parseInt(screenPostPartum));
                        }
                    }
                    if (dateArvRegimenCurrentTag.equals("open")) {
                        String dateArvRegimenCurrent = new String(chars, start, length);
                        if (!dateArvRegimenCurrent.trim().isEmpty()) {
                            delivery.setDateArvRegimenCurrent(DateUtil.parseStringToDate(dateArvRegimenCurrent, "yyyy-MM-dd"));
                        }
                    }
                    if (arvRegimenCurrentTag.equals("open")) {
                        delivery.setArvRegimenCurrent(new String(chars, start, length));
                    }
                    if (dateConfirmedHivTag.equals("open")) {
                        String dateConfirmedHiv = new String(chars, start, length);
                        if (!dateConfirmedHiv.trim().isEmpty()) {
                            delivery.setDateConfirmedHiv(DateUtil.parseStringToDate(dateConfirmedHiv, "yyyy-MM-dd"));
                        }
                    }
                    if (cd4OrderedTag.equals("open")) {
                        delivery.setCd4Ordered(new String(chars, start, length));
                    }
                    if (cd4Tag.equals("open")) {
                        String cd4 = new String(chars, start, length);
                        if (!cd4.trim().isEmpty()) {
                            delivery.setCd4(Double.parseDouble(cd4));
                        }
                    }
                    if (timeStampTag.equals("open")) {
                        delivery.setTimeStamp(new java.sql.Date(new java.util.Date().getTime()));
                    }
                    if (userIdTag.equals("open")) {
                        String userId = new String(chars, start, length);
                        if (!userId.trim().trim().isEmpty()) {
                            delivery.setUserId(Long.parseLong(userId));
                        }
                    }
                    if (idUUIDTag.equals("open")) {
                        delivery.setUuid(new String(chars, start, length));
                    }
                }

                @Override
                public void endElement(String uri, String localName, String element) throws SAXException {
                    if (element.equalsIgnoreCase("delivery")) {
                        deliveryTag = "close";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("hospital_num")) {
                        hospitalNumTag = "close";
                    }
                    if (element.equalsIgnoreCase("anc_num")) {
                        ancNumTag = "close";
                    }
                    if (element.equalsIgnoreCase("delivery_id")) {
                        deliveryIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_delivery")) {
                        dateDeliveryTag = "close";
                    }
                    if (element.equalsIgnoreCase("booking_status")) {
                        bookingStatusTag = "close";
                    }
                    if (element.equalsIgnoreCase("rom_delivery_interval")) {
                        romDeliveryIntervalTag = "close";
                    }
                    if (element.equalsIgnoreCase("mode_delivery")) {
                        modeDeliveryTag = "close";
                    }
                    if (element.equalsIgnoreCase("episiotomy")) {
                        episiotomyTag = "close";
                    }
                    if (element.equalsIgnoreCase("vaginal_tear")) {
                        vaginalTearTag = "close";
                    }
                    if (element.equalsIgnoreCase("maternal_outcome")) {
                        maternalOutcomeTag = "close";
                    }
                    if (element.equalsIgnoreCase("time_hiv_diagnosis")) {
                        timeHivDiagnosisTag = "close";
                    }
                    if (element.equalsIgnoreCase("screen_post_partum")) {
                        screenPostPartumTag = "close";
                    }
                    if (element.equalsIgnoreCase("arv_regimen_past")) {
                        arvRegimenPastTag = "close";
                    }
                    if (element.equalsIgnoreCase("arv_regimen_current")) {
                        arvRegimenCurrentTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_arv_regimen_current")) {
                        dateArvRegimenCurrentTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_confirmed_hiv")) {
                        dateConfirmedHivTag = "close";
                    }
                    if (element.equalsIgnoreCase("clinic_stage")) {
                        clinicStageTag = "close";
                    }
                    if (element.equalsIgnoreCase("cd4_ordered")) {
                        cd4OrderedTag = "close";
                    }
                    if (element.equalsIgnoreCase("cd4")) {
                        cd4Tag = "close";
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

                    //if this is the closing tag of a delivery element save the record
                    if (element.equalsIgnoreCase("delivery")) {
                        deliveryTag = "close";
                        if (!skipRecord) {Long patientId = ServerIDProvider.getPatientServerId(hospitalNum, facilityId);
                            if (patientId != null) {
                                Patient patient = new Patient();
                                patient.setPatientId(patientId);
                                delivery.setPatient(patient);
                                if (!StringUtils.isBlank(ancNum)) {
                                    Long ancId = ServerIDProvider.getAncId(ancNum, facilityId);
                                    if (ancId != null) {
                                        delivery.setAncId(ancId);
                                    }
                                }
                                Long id = ServerIDProvider
                                        .getPatientDependantId("delivery", hospitalNum,
                                                delivery.getDateDelivery(), facilityId);
                                if (id != null) {
                                    delivery.setDeliveryId(id);
                                    DeliveryDAO.update(delivery);
                                } else {
                                    try {
                                        DeliveryDAO.save(delivery);
                                    }catch (Exception ignored){}
                                }
                            }
                        }
                    }
                }
            };

            //parse the XML specified in the given path and uses supplied handler to parse the document
            //this calls startElement(), endElement(), and character() methods accordingly
            saxParser.parse(xmlFileName, defaultHandler);
            //new CleanupService().cleanNullFields("delivery", facilityId);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }

}
