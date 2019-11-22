/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.service.parser.xml;

import org.fhi360.lamis.dao.hibernate.ClinicDAO;
import org.fhi360.lamis.model.Clinic;
import org.fhi360.lamis.model.Patient;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.fhi360.lamis.service.ServerIDProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ClinicXmlParser extends DefaultHandler {

    private long facilityId;
    private long idOnServer;
    private String hospitalNum;
    private String dateVisit;
    private Boolean populated;
    private Boolean skipRecord;
    private Clinic clinic;
    private Patient patient = new Patient();

    public ClinicXmlParser() {
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
                String clinicTag = "close";
                String clinicIdTag = "close";
                String patientIdTag = "close";
                String facilityIdTag = "close";
                String hospitalNumTag = "close";
                String dateVisitTag = "close";
                String clinicStageTag = "close";
                String funcStatusTag = "close";
                String tbStatusTag = "close";
                String viralLoadTag = "close";
                String cd4Tag = "close";
                String cd4pTag = "close";
                String regimentypeTag = "close";
                String regimenTag = "close";
                String bodyWeightTag = "close";
                String heightTag = "close";
                String waistTag = "close";
                String bpTag = "close";
                String pregnantTag = "close";
                String breastfeedingTag = "close";
                String lmpTag = "close";
                String oiScreenedTag = "close";
                String oiIdsTag = "close";
                String stiTreatedTag = "close";
                String stiIdsTag = "close";
                String adrScreenedTag = "close";
                String adrIdsTag = "close";
                String adherenceLevelTag = "close";
                String adhereIdsTag = "close";
                String commenceTag = "close";
                String nextAppointmentTag = "close";
                String timeStampTag = "close";
                String userIdTag = "close";
                String idUUIDTag = "close";

                //this method is called every time the parser gets an open tag '<'
                //identifies which tag is being open at the time by assigning an open flag
                @Override
                public void startElement(String uri, String localName, String element, Attributes attributes) throws SAXException {
                    if (element.equalsIgnoreCase("clinic")) {
                        clinicTag = "open";
                        clinic = new Clinic();
                    }
                    if (element.equalsIgnoreCase("clinic_id")) {
                        clinicIdTag = "open";
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
                    if (element.equalsIgnoreCase("clinic_stage")) {
                        clinicStageTag = "open";
                    }
                    if (element.equalsIgnoreCase("func_status")) {
                        funcStatusTag = "open";
                    }
                    if (element.equalsIgnoreCase("tb_status")) {
                        tbStatusTag = "open";
                    }
                    if (element.equalsIgnoreCase("viral_load")) {
                        viralLoadTag = "open";
                    }
                    if (element.equalsIgnoreCase("cd4")) {
                        cd4Tag = "open";
                    }
                    if (element.equalsIgnoreCase("cd4p")) {
                        cd4pTag = "open";
                    }
                    if (element.equalsIgnoreCase("regimentype")) {
                        regimentypeTag = "open";
                    }
                    if (element.equalsIgnoreCase("regimen")) {
                        regimenTag = "open";
                    }
                    if (element.equalsIgnoreCase("body_weight")) {
                        bodyWeightTag = "open";
                    }
                    if (element.equalsIgnoreCase("height")) {
                        heightTag = "open";
                    }
                    if (element.equalsIgnoreCase("waist")) {
                        waistTag = "open";
                    }
                    if (element.equalsIgnoreCase("bp")) {
                        bpTag = "open";
                    }
                    if (element.equalsIgnoreCase("pregnant")) {
                        pregnantTag = "open";
                    }
                    if (element.equalsIgnoreCase("breastfeeding")) {
                        breastfeedingTag = "open";
                    }
                    if (element.equalsIgnoreCase("lmp")) {
                        lmpTag = "open";
                    }
                    if (element.equalsIgnoreCase("oi_screened")) {
                        oiScreenedTag = "open";
                    }
                    if (element.equalsIgnoreCase("oi_ids")) {
                        oiIdsTag = "open";
                    }
                    if (element.equalsIgnoreCase("sti_treated")) {
                        stiTreatedTag = "open";
                    }
                    if (element.equalsIgnoreCase("sti_ids")) {
                        stiIdsTag = "open";
                    }
                    if (element.equalsIgnoreCase("adr_screened")) {
                        adrScreenedTag = "open";
                    }
                    if (element.equalsIgnoreCase("adr_ids")) {
                        adrIdsTag = "open";
                    }
                    if (element.equalsIgnoreCase("adherence_level")) {
                        adherenceLevelTag = "open";
                    }
                    if (element.equalsIgnoreCase("adhere_ids")) {
                        adhereIdsTag = "open";
                    }
                    if (element.equalsIgnoreCase("commence")) {
                        commenceTag = "open";
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
                        clinic.setFacilityId(facilityId);
                    }
                    if (hospitalNumTag.equals("open")) {
                        hospitalNum = new String(chars, start, length);
                    }
                    if (dateVisitTag.equals("open")) {
                        dateVisit = new String(chars, start, length);
                        if (!dateVisit.trim().isEmpty()) {
                            clinic.setDateVisit(DateUtil.parseStringToDate(dateVisit, "yyyy-MM-dd"));
                        } else {
                            skipRecord = true;
                        }
                    }
                    if (clinicStageTag.equals("open")) {
                        clinic.setClinicStage(new String(chars, start, length));
                    }
                    if (funcStatusTag.equals("open")) {
                        clinic.setFuncStatus(new String(chars, start, length));
                    }
                    if (tbStatusTag.equals("open")) {
                        clinic.setTbStatus(new String(chars, start, length));
                    }
                    if (viralLoadTag.equals("open")) {
                        String viralLoad = new String(chars, start, length);
                        if (!viralLoad.trim().isEmpty()) {
                            clinic.setViralLoad(Double.parseDouble(viralLoad));
                        }
                    }
                    if (cd4Tag.equals("open")) {
                        String cd4 = new String(chars, start, length);
                        if (!cd4.trim().isEmpty()) {
                            clinic.setCd4(Double.parseDouble(cd4));
                        }
                    }
                    if (cd4pTag.equals("open")) {
                        String cd4p = new String(chars, start, length);
                        if (!cd4p.trim().isEmpty()) {
                            clinic.setCd4p(Double.parseDouble(cd4p));
                        }
                    }
                    if (regimentypeTag.equals("open")) {
                        clinic.setRegimentype(new String(chars, start, length));
                    }
                    if (regimenTag.equals("open")) {
                        clinic.setRegimen(new String(chars, start, length));
                    }

                    if (bodyWeightTag.equals("open")) {
                        String bodyWeight = new String(chars, start, length);
                        if (!bodyWeight.trim().isEmpty()) {
                            clinic.setBodyWeight(Double.parseDouble(bodyWeight));
                        }
                    }
                    if (heightTag.equals("open")) {
                        String height = new String(chars, start, length);
                        if (!height.trim().isEmpty()) {
                            clinic.setHeight(Double.parseDouble(height));
                        }
                    }
                    if (waistTag.equals("open")) {
                        String waist = new String(chars, start, length);
                        if (!waist.trim().equals("")) {
                            clinic.setWaist(Double.parseDouble(waist));
                        }
                    }
                    if (bpTag.equals("open")) {
                        clinic.setBp(new String(chars, start, length));
                    }
                    if (pregnantTag.equals("open")) {
                        String pregnant = new String(chars, start, length);
                        if (!pregnant.trim().isEmpty()) {
                            clinic.setPregnant(Integer.parseInt(pregnant));
                        }
                    }
                    if (breastfeedingTag.equals("open")) {
                        String breastfeeding = new String(chars, start, length);
                        if (!breastfeeding.trim().isEmpty()) {
                            clinic.setBreastfeeding(Integer.parseInt(breastfeeding));
                        }
                    }
                    if (lmpTag.equals("open")) {
                        String lmp = new String(chars, start, length);
                        if (!lmp.trim().isEmpty()) {
                            clinic.setLmp(DateUtil.parseStringToDate(lmp, "yyyy-MM-dd"));
                        }
                    }
                    if (oiScreenedTag.equals("open")) {
                        clinic.setOiScreened(new String(chars, start, length));
                    }
                    if (oiIdsTag.equals("open")) {
                        clinic.setOiIds(new String(chars, start, length));
                    }

                    if (stiTreatedTag.equals("open")) {
                        clinic.setStiTreated(new String(chars, start, length));
                    }
                    if (stiIdsTag.equals("open")) {
                        clinic.setStiIds(new String(chars, start, length));
                    }

                    if (adrScreenedTag.equals("open")) {
                        clinic.setAdrScreened(new String(chars, start, length));
                    }
                    if (adrIdsTag.equals("open")) {
                        clinic.setAdrIds(new String(chars, start, length));
                    }
                    if (adherenceLevelTag.equals("open")) {
                        clinic.setAdherenceLevel(new String(chars, start, length));
                    }
                    if (adhereIdsTag.equals("open")) {
                        clinic.setAdhereIds(new String(chars, start, length));
                    }
                    if (commenceTag.equals("open")) {
                        String commence = new String(chars, start, length);
                        if (!commence.trim().isEmpty()) {
                            clinic.setCommence(Integer.parseInt(commence));
                        }
                    }
                    if (nextAppointmentTag.equals("open")) {
                        String nextAppointment = new String(chars, start, length);
                        if (!nextAppointment.trim().isEmpty()) {
                            clinic.setNextAppointment(DateUtil.parseStringToDate(nextAppointment, "yyyy-MM-dd"));
                        }
                    }
                    if (timeStampTag.equals("open")) {
                        clinic.setTimeStamp(new java.sql.Date(new java.util.Date().getTime()));
                    }
                    if (userIdTag.equals("open")) {
                        String userId = new String(chars, start, length);
                        if (!userId.trim().trim().isEmpty()) {
                            clinic.setUserId(Long.parseLong(userId));
                        }
                    }
                    if (idUUIDTag.equals("open")) {
                        clinic.setUuid(new String(chars, start, length));
                    }
                }

                @Override
                public void endElement(String uri, String localName, String element) throws SAXException {
                    if (element.equalsIgnoreCase("clinic")) {
                        clinicTag = "close";
                    }
                    if (element.equalsIgnoreCase("clinic_id")) {
                        clinicIdTag = "close";
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
                    if (element.equalsIgnoreCase("clinic_stage")) {
                        clinicStageTag = "close";
                    }
                    if (element.equalsIgnoreCase("func_status")) {
                        funcStatusTag = "close";
                    }
                    if (element.equalsIgnoreCase("tb_status")) {
                        tbStatusTag = "close";
                    }
                    if (element.equalsIgnoreCase("viral_load")) {
                        viralLoadTag = "close";
                    }
                    if (element.equalsIgnoreCase("cd4")) {
                        cd4Tag = "close";
                    }
                    if (element.equalsIgnoreCase("cd4p")) {
                        cd4pTag = "close";
                    }
                    if (element.equalsIgnoreCase("regimentype")) {
                        regimentypeTag = "close";
                    }
                    if (element.equalsIgnoreCase("regimen")) {
                        regimenTag = "close";
                    }
                    if (element.equalsIgnoreCase("body_weight")) {
                        bodyWeightTag = "close";
                    }
                    if (element.equalsIgnoreCase("height")) {
                        heightTag = "close";
                    }
                    if (element.equalsIgnoreCase("waist")) {
                        waistTag = "close";
                    }
                    if (element.equalsIgnoreCase("bp")) {
                        bpTag = "close";
                    }
                    if (element.equalsIgnoreCase("pregnant")) {
                        pregnantTag = "close";
                    }
                    if (element.equalsIgnoreCase("breastfeeding")) {
                        breastfeedingTag = "close";
                    }
                    if (element.equalsIgnoreCase("lmp")) {
                        lmpTag = "close";
                    }
                    if (element.equalsIgnoreCase("oi_screened")) {
                        oiScreenedTag = "close";
                    }
                    if (element.equalsIgnoreCase("oi_ids")) {
                        oiIdsTag = "close";
                    }

                    if (element.equalsIgnoreCase("sti_treated")) {
                        stiTreatedTag = "close";
                    }
                    if (element.equalsIgnoreCase("sti_ids")) {
                        stiIdsTag = "close";
                    }

                    if (element.equalsIgnoreCase("adr_screened")) {
                        adrScreenedTag = "close";
                    }
                    if (element.equalsIgnoreCase("adr_ids")) {
                        adrIdsTag = "close";
                    }
                    if (element.equalsIgnoreCase("adherence_level")) {
                        adherenceLevelTag = "close";
                    }
                    if (element.equalsIgnoreCase("adhere_ids")) {
                        adhereIdsTag = "close";
                    }
                    if (element.equalsIgnoreCase("commence")) {
                        commenceTag = "close";
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

                    //if this is the closing tag of a clinic element save the record
                    if (element.equalsIgnoreCase("clinic")) {
                        clinicTag = "close";
                        if (skipRecord) {
                            System.out.println("....record has a null value: " + hospitalNum);
                        } else {
                            //Using the hospital number of this patient that was waived into the clinic xml, get the ID of this patient on the server
                            //If the hospital number does not exist then the patient does not exist. If the patient exist retrieve the ID and use it to form the patient object
                            //Then set the patient object on the clinic object
                            Long patientId = ServerIDProvider.getPatientServerId(hospitalNum, facilityId);
                            if (patientId != null) {
                                Patient patient = new Patient();
                                patient.setPatientId(patientId);
                                clinic.setPatient(patient);
                                Long id = ServerIDProvider
                                        .getPatientDependantId("clinic", hospitalNum,
                                                clinic.getDateVisit(), facilityId);
                                if (id != null) {
                                    clinic.setClinicId(id);
                                    ClinicDAO.update(clinic);
                                } else {
                                    try {
                                        ClinicDAO.save(clinic);
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
            //new CleanupService().cleanNullFields("clinic", facilityId);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }

}
