/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.service.parser.xml;

import org.fhi360.lamis.model.Patient;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.fhi360.lamis.dao.hibernate.DevolveDAO;
import org.fhi360.lamis.model.Devolve;
import org.fhi360.lamis.service.ServerIDProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DevolveXmlParser extends DefaultHandler {

    private long facilityId;
    private long idOnServer;
    private String hospitalNum;
    private String dateDevolved;
    private Boolean populated;
    private Boolean skipRecord;
    private Devolve devolve;
    private Patient patient = new Patient();

    public DevolveXmlParser() {
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

                String devolveTag = "close";
                String devolveIdTag = "close";
                String patientIdTag = "close";
                String facilityIdTag = "close";
                String hospitalNumTag = "close";
                String communitypharmIdTag = "close";
                String dateDevolvedTag = "close";
                String typeDmocTag = "close";
                String viralLoadAssessedTag = "close";
                String lastViralLoadTag = "close";
                String dateLastViralLoadTag = "close";
                String cd4AssessedTag = "close";
                String lastCd4Tag = "close";
                String dateLastCd4Tag = "close";
                String lastClinicStageTag = "close";
                String dateLastClinicStageTag = "close";
                String regimentypeTag = "close";
                String regimenTag = "close";
                String arvDispensedTag = "close";
                String dateNextClinicTag = "close";
                String dateNextRefillTag = "close";
                String dateLastClinicTag = "close";
                String dateLastRefillTag = "close";
                String notesTag = "close";
                String dateDiscontinuedTag = "close";
                String reasonDiscontinuedTag = "close";
                String timeStampTag = "close";
                String userIdTag = "close";
                String idUUIDTag = "close";

                //this method is called every time the parser gets an open tag '<'
                //identifies which tag is being open at the time by assigning an open flag
                @Override
                public void startElement(String uri, String localName, String element, Attributes attributes) throws SAXException {
                    if (element.equalsIgnoreCase("devolve")) {
                        devolveTag = "open";
                        devolve = new Devolve();
                    }
                    if (element.equalsIgnoreCase("devolve_id")) {
                        devolveIdTag = "open";
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

                    if (element.equalsIgnoreCase("communitypharm_id")) {
                        communitypharmIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_devolved")) {
                        dateDevolvedTag = "open";
                    }
                    if (element.equalsIgnoreCase("type_dmoc")) {
                        typeDmocTag = "open";
                    }
                    if (element.equalsIgnoreCase("viral_load_assessed")) {
                        viralLoadAssessedTag = "open";
                    }
                    if (element.equalsIgnoreCase("last_viral_load")) {
                        lastViralLoadTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_last_viral_load")) {
                        dateLastViralLoadTag = "open";
                    }
                    if (element.equalsIgnoreCase("cd4_assessed")) {
                        cd4AssessedTag = "open";
                    }
                    if (element.equalsIgnoreCase("last_cd4")) {
                        lastCd4Tag = "open";
                    }
                    if (element.equalsIgnoreCase("date_last_cd4")) {
                        dateLastCd4Tag = "open";
                    }
                    if (element.equalsIgnoreCase("last_clinic_stage")) {
                        lastClinicStageTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_last_clinic_stage")) {
                        dateLastClinicStageTag = "open";
                    }
                    if (element.equalsIgnoreCase("regimentype")) {
                        regimentypeTag = "open";
                    }
                    if (element.equalsIgnoreCase("regimen")) {
                        regimenTag = "open";
                    }
                    if (element.equalsIgnoreCase("arv_dispensed")) {
                        arvDispensedTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_next_clinic")) {
                        dateNextClinicTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_next_refill")) {
                        dateNextRefillTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_last_clinic")) {
                        dateLastClinicTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_last_refill")) {
                        dateLastRefillTag = "open";
                    }
                    if (element.equalsIgnoreCase("notes")) {
                        notesTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_discontinued")) {
                        dateDiscontinuedTag = "open";
                    }
                    if (element.equalsIgnoreCase("reason_discontinued")) {
                        reasonDiscontinuedTag = "open";
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
                        devolve.setFacilityId(facilityId);
                    }
                    if (hospitalNumTag.equals("open")) {
                        hospitalNum = new String(chars, start, length);
                    }
                    if (communitypharmIdTag.equals("open")) {
                        long communitypharmId = Long.parseLong(new String(chars, start, length));
                        devolve.setCommunitypharmId(communitypharmId);
                    }
                    if (dateDevolvedTag.equals("open")) {
                        dateDevolved = new String(chars, start, length);
                        if (!dateDevolved.trim().isEmpty()) {
                            devolve.setDateDevolved(DateUtil.parseStringToDate(dateDevolved, "yyyy-MM-dd"));
                        } else {
                            skipRecord = true;
                        }
                    }
                    if (typeDmocTag.equals("open")) {
                        devolve.setTypeDmoc(new String(chars, start, length));
                    }
                    if (viralLoadAssessedTag.equals("open")) {
                        devolve.setViralLoadAssessed(new String(chars, start, length));
                    }
                    if (lastViralLoadTag.equals("open")) {
                        String lastViralLoad = new String(chars, start, length);
                        if (!lastViralLoad.trim().isEmpty()) {
                            devolve.setLastViralLoad(Double.parseDouble(lastViralLoad));
                        }
                    }
                    if (dateLastViralLoadTag.equals("open")) {
                        String dateLastViralLoad = new String(chars, start, length);
                        if (!dateLastViralLoad.trim().isEmpty()) {
                            devolve.setDateLastViralLoad(DateUtil.parseStringToDate(dateLastViralLoad, "yyyy-MM-dd"));
                        }
                    }
                    if (cd4AssessedTag.equals("open")) {
                        devolve.setCd4Assessed(new String(chars, start, length));
                    }
                    if (lastCd4Tag.equals("open")) {
                        String lastCd4 = new String(chars, start, length);
                        if (!lastCd4.trim().isEmpty()) {
                            devolve.setLastCd4(Double.parseDouble(lastCd4));
                        }
                    }
                    if (dateLastCd4Tag.equals("open")) {
                        String dateLastCd4 = new String(chars, start, length);
                        if (!dateLastCd4.trim().isEmpty()) {
                            devolve.setDateLastCd4(DateUtil.parseStringToDate(dateLastCd4, "yyyy-MM-dd"));
                        }
                    }
                    if (lastClinicStageTag.equals("open")) {
                        devolve.setLastClinicStage(new String(chars, start, length));
                    }
                    if (dateLastClinicStageTag.equals("open")) {
                        String dateLastClinicStage = new String(chars, start, length);
                        if (!dateLastClinicStage.trim().isEmpty()) {
                            devolve.setDateLastClinicStage(DateUtil.parseStringToDate(dateLastClinicStage, "yyyy-MM-dd"));
                        }
                    }
                    if (regimentypeTag.equals("open")) {
                        devolve.setRegimentype(new String(chars, start, length));
                    }
                    if (regimenTag.equals("open")) {
                        devolve.setRegimen(new String(chars, start, length));
                    }
                    if (arvDispensedTag.equals("open")) {
                        devolve.setArvDispensed(new String(chars, start, length));
                    }
                    if (dateNextClinicTag.equals("open")) {
                        String dateNextClinic = new String(chars, start, length);
                        if (!dateNextClinic.trim().isEmpty()) {
                            devolve.setDateNextClinic(DateUtil.parseStringToDate(dateNextClinic, "yyyy-MM-dd"));
                        }
                    }
                    if (dateNextRefillTag.equals("open")) {
                        String dateNextRefill = new String(chars, start, length);
                        if (!dateNextRefill.trim().isEmpty()) {
                            devolve.setDateNextRefill(DateUtil.parseStringToDate(dateNextRefill, "yyyy-MM-dd"));
                        }
                    }
                    if (dateLastClinicTag.equals("open")) {
                        String dateLastClinic = new String(chars, start, length);
                        if (!dateLastClinic.trim().isEmpty()) {
                            devolve.setDateLastClinic(DateUtil.parseStringToDate(dateLastClinic, "yyyy-MM-dd"));
                        }
                    }
                    if (dateNextRefillTag.equals("open")) {
                        String dateLastRefill = new String(chars, start, length);
                        if (!dateLastRefill.trim().isEmpty()) {
                            devolve.setDateLastRefill(DateUtil.parseStringToDate(dateLastRefill, "yyyy-MM-dd"));
                        }
                    }
                    if (notesTag.equals("open")) {
                        devolve.setNotes(new String(chars, start, length));
                    }
                    if (dateDiscontinuedTag.equals("open")) {
                        String dateDiscontinued = new String(chars, start, length);
                        if (!dateDiscontinued.trim().isEmpty()) {
                            devolve.setDateDiscontinued(DateUtil.parseStringToDate(dateDiscontinued, "yyyy-MM-dd"));
                        }
                    }
                    if (reasonDiscontinuedTag.equals("open")) {
                        devolve.setReasonDiscontinued(new String(chars, start, length));
                    }
                    if (timeStampTag.equals("open")) {
                        devolve.setTimeStamp(new java.sql.Date(new java.util.Date().getTime()));
                    }
                    if (userIdTag.equals("open")) {
                        String userId = new String(chars, start, length);
                        if (!userId.trim().trim().isEmpty()) {
                            devolve.setUserId(Long.parseLong(userId));
                        }
                    }
                    if (idUUIDTag.equals("open")) {
                        devolve.setUuid(new String(chars, start, length));
                    }
                }

                @Override
                public void endElement(String uri, String localName, String element) throws SAXException {
                    if (element.equalsIgnoreCase("devolve")) {
                        devolveTag = "close";
                    }
                    if (element.equalsIgnoreCase("devolve_id")) {
                        devolveIdTag = "close";
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
                    if (element.equalsIgnoreCase("communitypharm_id")) {
                        communitypharmIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_devolved")) {
                        dateDevolvedTag = "close";
                    }
                    if (element.equalsIgnoreCase("type_dmoc")) {
                        typeDmocTag = "close";
                    }
                    if (element.equalsIgnoreCase("viral_load_assessed")) {
                        viralLoadAssessedTag = "close";
                    }
                    if (element.equalsIgnoreCase("last_viral_load")) {
                        lastViralLoadTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_last_viral_load")) {
                        dateLastViralLoadTag = "close";
                    }
                    if (element.equalsIgnoreCase("cd4_assessed")) {
                        cd4AssessedTag = "close";
                    }
                    if (element.equalsIgnoreCase("last_cd4")) {
                        lastCd4Tag = "close";
                    }
                    if (element.equalsIgnoreCase("date_last_cd4")) {
                        dateLastCd4Tag = "close";
                    }
                    if (element.equalsIgnoreCase("last_clinic_stage")) {
                        lastClinicStageTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_last_clinic_stage")) {
                        dateLastClinicStageTag = "close";
                    }
                    if (element.equalsIgnoreCase("regimentype")) {
                        regimentypeTag = "close";
                    }
                    if (element.equalsIgnoreCase("regimen")) {
                        regimenTag = "close";
                    }
                    if (element.equalsIgnoreCase("arv_dispensed")) {
                        arvDispensedTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_next_clinic")) {
                        dateNextClinicTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_next_refill")) {
                        dateNextRefillTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_last_clinic")) {
                        dateLastClinicTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_last_refill")) {
                        dateLastRefillTag = "close";
                    }
                    if (element.equalsIgnoreCase("notes")) {
                        notesTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_discontinued")) {
                        dateDiscontinuedTag = "close";
                    }
                    if (element.equalsIgnoreCase("reason_discontinued")) {
                        reasonDiscontinuedTag = "close";
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

                    //if this is the closing tag of a clinic element save the record
                    if (element.equalsIgnoreCase("devolve")) {
                        devolveTag = "close";
                        if (!skipRecord) {
                            Long patientId = ServerIDProvider.getPatientServerId(hospitalNum, facilityId);
                            if (patientId != null) {
                                Patient patient = new Patient();
                                patient.setPatientId(patientId);
                                devolve.setPatient(patient);
                                Long id = ServerIDProvider
                                        .getPatientDependantId("devolve", hospitalNum,
                                                devolve.getDateDevolved(), facilityId);
                                if (id != null) {
                                    devolve.setDevolveId(id);
                                    DevolveDAO.update(devolve);
                                } else {
                                    try {
                                        DevolveDAO.save(devolve);
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
            //new CleanupService().cleanNullFields("devolve", facilityId);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }

}
