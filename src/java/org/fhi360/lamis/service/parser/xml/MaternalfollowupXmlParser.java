/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.service.parser.xml;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.lang3.StringUtils;
import org.fhi360.lamis.dao.hibernate.MaternalfollowupDAO;
import org.fhi360.lamis.model.Maternalfollowup;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.service.ServerIDProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.PatientNumberNormalizer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MaternalfollowupXmlParser extends DefaultHandler {

    private long facilityId;
    private long idOnServer;
    private String hospitalNum;
    private String ancNum;
    private String dateVisit;
    private Boolean populated;
    private Maternalfollowup maternalfollowup;

    public MaternalfollowupXmlParser() {
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
                String maternalfollowupTag = "close";
                String maternalfollowupIdTag = "close";
                String facilityIdTag = "close";
                String hospitalNumTag = "close";
                String ancNumTag = "close";
                String ancIdTag = "close";
                String dateVisitTag = "close";
                String bodyWeightTag = "close";
                String bpTag = "close";
                String fundalHeightTag = "close";
                String fetalPresentationTag = "close";
                String dateConfirmedHivTag = "close";
                String timeHivDiagnosisTag = "close";
                String arvRegimenPastTag = "close";
                String arvRegimenCurrentTag = "close";
                String dateArvRegimenCurrentTag = "close";
                String screenPostPartumTag = "close";
                String cd4OrderedTag = "close";
                String cd4Tag = "close";
                String counselNutritionTag = "close";
                String counselFeedingTag = "close";
                String counselFamilyPlanningTag = "close";
                String familyPlanningMethodTag = "close";
                String referredTag = "close";
                String dateNextVisitTag = "close";
                String timeStampTag = "close";
                String userIdTag = "close";
                String idUUIDTag = "close";

                //this method is called every time the parser gets an open tag '<'
                //identifies which tag is being open at the time by assigning an open flag
                @Override
                public void startElement(String uri, String localName, String element, Attributes attributes) throws SAXException {
                    if (element.equalsIgnoreCase("maternalfollowup")) {
                        maternalfollowupTag = "open";
                        maternalfollowup = new Maternalfollowup();
                    }
                    if (element.equalsIgnoreCase("maternalfollowup_id")) {
                        maternalfollowupIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_visit")) {
                        dateVisitTag = "open";
                    }
                    if (element.equalsIgnoreCase("hospital_num")) {
                        hospitalNumTag = "open";
                    }
                    if (element.equalsIgnoreCase("anc_num")) {
                        ancNumTag = "open";
                    }
                    if (element.equalsIgnoreCase("body_weight")) {
                        bodyWeightTag = "open";
                    }
                    if (element.equalsIgnoreCase("bp")) {
                        bpTag = "open";
                    }
                    if (element.equalsIgnoreCase("fundal_height")) {
                        fundalHeightTag = "open";
                    }
                    if (element.equalsIgnoreCase("fetal_presentation")) {
                        fetalPresentationTag = "open";
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
                    if (element.equalsIgnoreCase("screen_post_partum")) {
                        screenPostPartumTag = "open";
                    }
                    if (element.equalsIgnoreCase("cd4_ordered")) {
                        cd4OrderedTag = "open";
                    }
                    if (element.equalsIgnoreCase("cd4")) {
                        cd4Tag = "open";
                    }
                    if (element.equalsIgnoreCase("counsel_nutrition")) {
                        counselNutritionTag = "open";
                    }
                    if (element.equalsIgnoreCase("counsel_feeding")) {
                        counselFeedingTag = "open";
                    }
                    if (element.equalsIgnoreCase("counsel_family_planning")) {
                        counselFamilyPlanningTag = "open";
                    }
                    if (element.equalsIgnoreCase("family_planning_method")) {
                        familyPlanningMethodTag = "open";
                    }
                    if (element.equalsIgnoreCase("referred")) {
                        referredTag = "open";
                    }
                    if (element.equalsIgnoreCase("date_next_visit")) {
                        dateNextVisitTag = "open";
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
                        maternalfollowup.setFacilityId(facilityId);
                    }
                    if (hospitalNumTag.equals("open")) {
                        hospitalNum = new String(chars, start, length);
                    }
                    if (ancNumTag.equals("open")) {
                        ancNum = new String(chars, start, length);
                    }
                    if (dateVisitTag.equals("open")) {
                        dateVisit = new String(chars, start, length);
                        if (!dateVisit.trim().isEmpty()) {
                            maternalfollowup.setDateVisit(DateUtil.parseStringToDate(dateVisit, "yyyy-MM-dd"));
                        }
                    }
                    if (bodyWeightTag.equals("open")) {
                        String bodyWeight = new String(chars, start, length);
                        if (!bodyWeight.trim().isEmpty()) {
                            maternalfollowup.setBodyWeight(Double.parseDouble(bodyWeight));
                        }
                    }
                    if (bpTag.equals("open")) {
                        maternalfollowup.setBp(new String(chars, start, length));
                    }
                    if (fundalHeightTag.equals("open")) {
                        String fundalHeight = new String(chars, start, length);
                        if (!fundalHeight.trim().isEmpty()) {
                            maternalfollowup.setFundalHeight(Double.parseDouble(fundalHeight));
                        }
                    }
                    if (fetalPresentationTag.equals("open")) {
                        maternalfollowup.setFetalPresentation(new String(chars, start, length));
                    }
                    if (dateConfirmedHivTag.equals("open")) {
                        String dateConfirmedHiv = new String(chars, start, length);
                        if (!dateConfirmedHiv.trim().isEmpty()) {
                            maternalfollowup.setDateConfirmedHiv(DateUtil.parseStringToDate(dateConfirmedHiv, "yyyy-MM-dd"));
                        }
                    }
                    if (timeHivDiagnosisTag.equals("open")) {
                        maternalfollowup.setTimeHivDiagnosis(new String(chars, start, length));
                    }
                    if (arvRegimenPastTag.equals("open")) {
                        maternalfollowup.setArvRegimenPast(new String(chars, start, length));
                    }
                    if (arvRegimenCurrentTag.equals("open")) {
                        maternalfollowup.setArvRegimenCurrent(new String(chars, start, length));
                    }
                    if (dateArvRegimenCurrentTag.equals("open")) {
                        String dateArvRegimenCurrent = new String(chars, start, length);
                        if (!dateArvRegimenCurrent.trim().isEmpty()) {
                            maternalfollowup.setDateArvRegimenCurrent(DateUtil.parseStringToDate(dateArvRegimenCurrent, "yyyy-MM-dd"));
                        }
                    }
                    if (screenPostPartumTag.equals("open")) {
                        String screenPostPartum = new String(chars, start, length);
                        if (!screenPostPartum.trim().isEmpty()) {
                            maternalfollowup.setScreenPostPartum(Integer.parseInt(screenPostPartum));
                        }
                    }
                    if (cd4OrderedTag.equals("open")) {
                        maternalfollowup.setCd4Ordered(new String(chars, start, length));
                    }
                    if (cd4Tag.equals("open")) {
                        String cd4 = new String(chars, start, length);
                        if (!cd4.trim().isEmpty()) {
                            maternalfollowup.setCd4(Double.parseDouble(cd4));
                        }
                    }
                    if (counselNutritionTag.equals("open")) {
                        String counselNutrition = new String(chars, start, length);
                        if (!counselNutrition.trim().isEmpty()) {
                            maternalfollowup.setCounselNutrition(Integer.parseInt(counselNutrition));
                        }
                    }
                    if (counselFeedingTag.equals("open")) {
                        String counselFeeding = new String(chars, start, length);
                        if (!counselFeeding.trim().isEmpty()) {
                            maternalfollowup.setScreenPostPartum(Integer.parseInt(counselFeeding));
                        }
                    }
                    if (familyPlanningMethodTag.equals("open")) {
                        maternalfollowup.setFamilyPlanningMethod(new String(chars, start, length));
                    }
                    if (referredTag.equals("open")) {
                        maternalfollowup.setReferred(new String(chars, start, length));
                    }
                    if (dateNextVisitTag.equals("open")) {
                        String dateNextVisit = new String(chars, start, length);
                        if (!dateNextVisit.trim().isEmpty()) {
                            maternalfollowup.setDateNextVisit(DateUtil.parseStringToDate(dateNextVisit, "yyyy-MM-dd"));
                        }
                    }
                    if (timeStampTag.equals("open")) {
                        maternalfollowup.setTimeStamp(new java.sql.Date(new java.util.Date().getTime()));
                    }
                    if (userIdTag.equals("open")) {
                        String userId = new String(chars, start, length);
                        if (!userId.trim().trim().isEmpty()) {
                            maternalfollowup.setUserId(Long.parseLong(userId));
                        }
                    }
                    if (idUUIDTag.equals("open")) {
                        maternalfollowup.setUuid(new String(chars, start, length));
                    }
                }

                @Override
                public void endElement(String uri, String localName, String element) throws SAXException {
                    if (element.equalsIgnoreCase("maternalfollowup")) {
                        maternalfollowupTag = "close";
                        maternalfollowup = new Maternalfollowup();
                    }
                    if (element.equalsIgnoreCase("maternalfollowup_id")) {
                        maternalfollowupIdTag = "closen";
                    }
                    if (element.equalsIgnoreCase("facility_id")) {
                        facilityIdTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_visit")) {
                        dateVisitTag = "close";
                    }
                    if (element.equalsIgnoreCase("hospital_num")) {
                        hospitalNumTag = "close";
                    }
                    if (element.equalsIgnoreCase("anc_num")) {
                        ancNumTag = "close";
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
                    if (element.equalsIgnoreCase("cd4_ordered")) {
                        cd4OrderedTag = "close";
                    }
                    if (element.equalsIgnoreCase("cd4")) {
                        cd4Tag = "close";
                    }
                    if (element.equalsIgnoreCase("referred")) {
                        referredTag = "close";
                    }
                    if (element.equalsIgnoreCase("date_next_visit")) {
                        dateNextVisitTag = "close";
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

                    //if this is the closing tag of a child element save the record
                    if (element.equalsIgnoreCase("maternalfollowup")) {
                        maternalfollowupTag = "close";
                        Long patientId = ServerIDProvider.getPatientServerId(hospitalNum, facilityId);
                        if (patientId != null) {
                            Patient patient = new Patient();
                            patient.setPatientId(patientId);
                            maternalfollowup.setPatient(patient);
                            if (!StringUtils.isBlank(ancNum)) {
                                Long ancId = ServerIDProvider.getAncId(ancNum, facilityId);
                                if (ancId != null) {
                                    maternalfollowup.setAncId(ancId);
                                }
                            }
                            Long id = ServerIDProvider
                                    .getPatientDependantId("maternalfollowup", hospitalNum,
                                            maternalfollowup.getDateVisit(), facilityId);
                            if (id != null) {
                                maternalfollowup.setMaternalfollowupId(id);
                                MaternalfollowupDAO.update(maternalfollowup);
                            } else {
                                try {
                                    MaternalfollowupDAO.save(maternalfollowup);
                                } catch (Exception ignored) {

                                }

                            }
                        }
                    }
                }
            };

            //parse the XML specified in the given path and uses supplied handler to parse the document
            //this calls startElement(), endElement(), and character() methods accordingly
            saxParser.parse(xmlFileName, defaultHandler);
            //new CleanupService().cleanNullFields("maternalfollowup", facilityId);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }

}
