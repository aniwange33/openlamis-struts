/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.service.parser.xml;

import org.fhi360.lamis.dao.hibernate.PharmacyDAO;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.model.Pharmacy;
import org.fhi360.lamis.utility.PatientNumberNormalizer;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.fhi360.lamis.service.ServerIDProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PharmacyXmlParser extends DefaultHandler {

    private long facilityId;
    private String hospitalNum;
    private String dateVisit;
    private String regimendrugId;
    private long idOnServer;
    private Boolean populated;
    private Boolean skipRecord;
    private Pharmacy pharmacy;
    private Patient patient = new Patient();

    public PharmacyXmlParser() {
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
                String pharmacyTag = "close";
                String pharmacyIdTag = "close";
                String patientIdTag = "close";
                String facilityIdTag = "close";
                String hospitalNumTag = "close";
                String dateVisitTag = "close";
                String durationTag = "close";
                String morningTag = "close";
                String afternoonTag = "close";
                String eveningTag = "close";
                String adrScreenedTag = "close";
                String adrIdsTag = "close";
                String prescripErrorTag = "close";
                String adherenceTag = "close";
                String nextAppointmentTag = "close";
                String regimentypeIdTag = "close";
                String regimenIdTag = "close";
                String regimendrugIdTag = "close";
                String timeStampTag = "close";
                String userIdTag = "close";
                String idUUIDTag = "close";

                //this method is called every time the parser gets an open tag '<'
                //identifies which tag is being open at the time by assigning an open flag
                @Override
                public void startElement(String uri, String localName, String element, Attributes attributes) throws SAXException {
                    if (element.equalsIgnoreCase("pharmacy")) {
                        pharmacyTag = "open";
                        pharmacy = new Pharmacy();
                    }
                    if (element.equalsIgnoreCase("pharmacy_id")) {
                        pharmacyIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("patient_id")) {
                        patientIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("hospital_num")) {
                        hospitalNumTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_visit")) {
                        dateVisitTag = "open";
                    }
                    if (element.equalsIgnoreCase("duration")) {
                        durationTag = "open";
                    }
                    if (element.equalsIgnoreCase("morning")) {
                        morningTag = "open";
                    }
                    if (element.equalsIgnoreCase("afternoon")) {
                        afternoonTag = "open";
                    }
                    if (element.equalsIgnoreCase("evening")) {
                        eveningTag = "open";
                    }
                    if (element.equalsIgnoreCase("adr_screened")) {
                        adrScreenedTag = "open";
                    }
                    if (element.equalsIgnoreCase("adr_ids")) {
                        adrIdsTag = "open";
                    }
                    if (element.equalsIgnoreCase("prescrip_error")) {
                        prescripErrorTag = "open";
                    }
                    if (element.equalsIgnoreCase("adherence")) {
                        adherenceTag = "open";
                    }
                    if (element.equalsIgnoreCase("regimentype_id")) {
                        regimentypeIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("regimen_id")) {
                        regimenIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("regimendrug_id")) {
                        regimendrugIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("next_appointment")) {
                        nextAppointmentTag = "open";
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
                        pharmacy.setFacilityId(facilityId);
                    }
                    if (hospitalNumTag.equals("open")) {
                        hospitalNum = new String(chars, start, length);
                    }
                    if (dateVisitTag.equals("open")) {
                        dateVisit = new String(chars, start, length);
                        if (!dateVisit.trim().isEmpty()) {
                            pharmacy.setDateVisit(DateUtil.parseStringToDate(dateVisit, "yyyy-MM-dd"));
                        } else {
                            skipRecord = true;
                        }
                    }
                    if (durationTag.equals("open")) {
                        String duration = new String(chars, start, length);
                        if (!duration.trim().isEmpty()) {
                            pharmacy.setDuration(Integer.parseInt(duration));
                        }
                    }
                    if (morningTag.equals("open")) {
                        String morning = new String(chars, start, length);
                        if (!morning.trim().isEmpty()) {
                            pharmacy.setMorning(Double.parseDouble(morning));
                        }
                    }
                    if (afternoonTag.equals("open")) {
                        String afternoon = new String(chars, start, length);
                        if (!afternoon.trim().isEmpty()) {
                            pharmacy.setAfternoon(Double.parseDouble(afternoon));
                        }
                    }
                    if (eveningTag.equals("open")) {
                        String evening = new String(chars, start, length);
                        if (!evening.trim().isEmpty()) {
                            pharmacy.setEvening(Double.parseDouble(evening));
                        }
                    }
                    if (adrScreenedTag.equals("open")) {
                        pharmacy.setAdrScreened(new String(chars, start, length));
                    }
                    if (adrIdsTag.equals("open")) {
                        pharmacy.setAdrIds(new String(chars, start, length));
                    }
                    if (prescripErrorTag.equals("open")) {
                        String prescripError = new String(chars, start, length);
                        if (!prescripError.trim().isEmpty()) {
                            pharmacy.setPrescripError(Integer.parseInt(prescripError));
                        }
                    }
                    if (adherenceTag.equals("open")) {
                        String adherence = new String(chars, start, length);
                        if (!adherence.trim().equals("")) {
                            pharmacy.setAdherence(Integer.parseInt(adherence));
                        }
                    }
                    if (regimentypeIdTag.equals("open")) {
                        String regimentypeId = new String(chars, start, length);
                        if (!regimentypeId.trim().isEmpty()) {
                            pharmacy.setRegimentypeId(Long.parseLong(regimentypeId));
                        }
                    }
                    if (regimenIdTag.equals("open")) {
                        String regimenId = new String(chars, start, length);
                        if (!regimenId.trim().isEmpty()) {
                            pharmacy.setRegimenId(Long.parseLong(regimenId));
                        }
                    }
                    if (regimendrugIdTag.equals("open")) {
                        regimendrugId = new String(chars, start, length);
                        if (!regimendrugId.trim().isEmpty()) {
                            pharmacy.setRegimendrugId(Long.parseLong(regimendrugId));
                        }
                    }
                    if (nextAppointmentTag.equals("open")) {
                        String nextAppointment = new String(chars, start, length);
                        if (!nextAppointment.trim().isEmpty()) {
                            pharmacy.setNextAppointment(DateUtil.parseStringToDate(nextAppointment, "yyyy-MM-dd"));
                        }
                    }
                    if (timeStampTag.equals("open")) {
                        pharmacy.setTimeStamp(new java.sql.Date(new java.util.Date().getTime()));
                    }
                    if (userIdTag.equals("open")) {
                        String userId = new String(chars, start, length);
                        if (!userId.trim().isEmpty()) {
                            pharmacy.setUserId(Long.parseLong(userId));
                        }
                    }
                    if (idUUIDTag.equals("open")) {
                        pharmacy.setUuid(new String(chars, start, length));
                    }
                }

                @Override
                public void endElement(String uri, String localName, String element) throws SAXException {
                    if (element.equalsIgnoreCase("pharmacy_id")) {
                        pharmacyIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("patient_id")) {
                        patientIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("hospital_num")) {
                        hospitalNumTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_visit")) {
                        dateVisitTag = "close";
                    }
                    if (element.equalsIgnoreCase("duration")) {
                        durationTag = "close";
                    }
                    if (element.equalsIgnoreCase("morning")) {
                        morningTag = "close";
                    }
                    if (element.equalsIgnoreCase("afternoon")) {
                        afternoonTag = "close";
                    }
                    if (element.equalsIgnoreCase("evening")) {
                        eveningTag = "close";
                    }
                    if (element.equalsIgnoreCase("adr_screened")) {
                        adrScreenedTag = "close";
                    }
                    if (element.equalsIgnoreCase("adr_ids")) {
                        adrIdsTag = "close";
                    }
                    if (element.equalsIgnoreCase("prescrip_error")) {
                        prescripErrorTag = "close";
                    }
                    if (element.equalsIgnoreCase("adherence")) {
                        adherenceTag = "close";
                    }
                    if (element.equalsIgnoreCase("regimentype_id")) {
                        regimentypeIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("regimen_id")) {
                        regimenIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("regimendrug_id")) {
                        regimendrugIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("next_appointment")) {
                        nextAppointmentTag = "close";
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

                    //if this is the closing tag of a pharmacy element save the record
                    if (element.equalsIgnoreCase("pharmacy")) {
                        pharmacyTag = "close";
                        if (!skipRecord) { final Long patientId = ServerIDProvider.getPatientServerId(hospitalNum, facilityId);
                            if (patientId != null) {
                                Patient patient = new Patient();
                                patient.setPatientId(patientId);
                                pharmacy.setPatient(patient);
                                Long id = ServerIDProvider
                                        .getPharmacyId(hospitalNum, pharmacy.getDateVisit(),
                                                pharmacy.getRegimendrugId(), facilityId);
                                if (id != null) {
                                    pharmacy.setPharmacyId(id);
                                    PharmacyDAO.update(pharmacy);
                                } else {
                                    try {
                                        PharmacyDAO.save(pharmacy);
                                    } catch (Exception ignored) {
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
            // new CleanupService().cleanNullFields("pharmacy", facilityId);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }

}
