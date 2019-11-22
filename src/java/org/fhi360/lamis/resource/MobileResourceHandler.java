package org.fhi360.lamis.resource;

import com.google.gson.Gson;
import java.util.Date;
import java.util.List;
import org.fhi360.lamis.dao.hibernate.AssessmentDao;
import org.fhi360.lamis.dao.hibernate.ClinicDAO;
import org.fhi360.lamis.dao.hibernate.HtsDAO;
import org.fhi360.lamis.dao.hibernate.IndexcontactDAO;
import org.fhi360.lamis.dao.hibernate.PatientDAO;
import org.fhi360.lamis.dao.jdbc.AssessmentJDBC;
import org.fhi360.lamis.dao.jdbc.ClinicJDBC;
import org.fhi360.lamis.dao.jdbc.HtsJDBC;
import org.fhi360.lamis.dao.jdbc.IndexcontactJDBC;
import org.fhi360.lamis.dao.jdbc.PatientJDBC;
import org.fhi360.lamis.model.Assessment;
import org.fhi360.lamis.model.Clinic;
import org.fhi360.lamis.model.Facility;
import org.fhi360.lamis.model.Hts;
import org.fhi360.lamis.model.Indexcontact;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.model.dto.mobile.AssessmentDTO;
import org.fhi360.lamis.model.dto.mobile.ClinicDTO;
import org.fhi360.lamis.model.dto.mobile.HtsDTO;
import org.fhi360.lamis.model.dto.mobile.IndexContactDTO;
import org.fhi360.lamis.model.dto.mobile.PatientDTO;
import org.fhi360.lamis.utility.DateUtil;
import org.springframework.http.HttpStatus;

public class MobileResourceHandler {

    public static synchronized void saveAssessment(List<AssessmentDTO> assessmentList) {
        try {
            for (AssessmentDTO assessmentDto : assessmentList) {
                Assessment assessment = new Assessment();
                assessment.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
                long assessmentId = new AssessmentJDBC().getHtsAssessment(assessmentDto.getClientCode(), assessmentDto.getFacilityId());
                if (assessmentId == 0L) {
                    assessment.setFacilityId(assessmentDto.getFacilityId());
                    assessment.setDateVisit(DateUtil.parseStringToDate(assessmentDto.getDateVisit(), "yyyy-MM-dd"));
                    assessment.setClientCode(assessmentDto.getClientCode());
                    assessment.setQuestion1(assessmentDto.getQuestion1());
                    assessment.setQuestion2(String.valueOf(assessmentDto.getQuestion2()));
                    assessment.setQuestion3(assessmentDto.getQuestion3());
                    assessment.setQuestion4(assessmentDto.getQuestion4());
                    assessment.setQuestion5(assessmentDto.getQuestion5());
                    assessment.setQuestion6(assessmentDto.getQuestion6());
                    assessment.setQuestion7(assessmentDto.getQuestion7());
                    assessment.setQuestion8(assessmentDto.getQuestion8());
                    assessment.setQuestion9(assessmentDto.getQuestion9());
                    assessment.setQuestion10(assessmentDto.getQuestion10());
                    assessment.setQuestion11(assessmentDto.getQuestion11());
                    assessment.setQuestion12(assessmentDto.getQuestion12());
                    assessment.setSti1(assessmentDto.getSti1());
                    assessment.setSti2(assessmentDto.getSti2());
                    assessment.setSti3(assessmentDto.getSti3());
                    assessment.setSti4(assessmentDto.getSti4());
                    assessment.setSti5(assessmentDto.getSti5());
                    assessment.setSti6(assessmentDto.getSti6());
                    assessment.setSti7(assessmentDto.getSti7());
                    assessment.setSti8(assessmentDto.getSti8());
                    assessment.setDeviceconfigId(assessmentDto.getDeviceconfigId());
                    AssessmentDao.save(assessment);
                } else {

                    assessment.setAssessmentId(assessmentId);
                    assessment.setFacilityId(assessmentDto.getFacilityId());
                    assessment.setDateVisit(DateUtil.parseStringToDate(assessmentDto.getDateVisit(), "yyyy-MM-dd"));
                    assessment.setClientCode(assessmentDto.getClientCode());
                    assessment.setQuestion1(assessmentDto.getQuestion1());
                    assessment.setQuestion2(String.valueOf(assessmentDto.getQuestion2()));
                    assessment.setQuestion3(assessmentDto.getQuestion3());
                    assessment.setQuestion4(assessmentDto.getQuestion4());
                    assessment.setQuestion5(assessmentDto.getQuestion5());
                    assessment.setQuestion6(assessmentDto.getQuestion6());
                    assessment.setQuestion7(assessmentDto.getQuestion7());
                    assessment.setQuestion8(assessmentDto.getQuestion8());
                    assessment.setQuestion9(assessmentDto.getQuestion9());
                    assessment.setQuestion10(assessmentDto.getQuestion10());
                    assessment.setQuestion11(assessmentDto.getQuestion11());
                    assessment.setQuestion12(assessmentDto.getQuestion12());
                    assessment.setSti1(assessmentDto.getSti1());
                    assessment.setSti2(assessmentDto.getSti2());
                    assessment.setSti3(assessmentDto.getSti3());
                    assessment.setSti4(assessmentDto.getSti4());
                    assessment.setSti5(assessmentDto.getSti5());
                    assessment.setSti6(assessmentDto.getSti6());
                    assessment.setSti7(assessmentDto.getSti7());
                    assessment.setSti8(assessmentDto.getSti8());
                    assessment.setDeviceconfigId(assessmentDto.getDeviceconfigId());
                    AssessmentDao.update(assessment);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        System.out.println("CODE IS HERE  " + HttpStatus.OK);
        //return Response.ok().build();
    }

    public static synchronized void saveHts(List<HtsDTO> htsList) {
        try {
            for (HtsDTO htsDto : htsList) {
                Hts hts = new Hts();
                hts.setTimeStamp(new java.sql.Timestamp(new Date().getTime()));
                long htsId = new HtsJDBC().getHtsId(htsDto.getClientCode(), htsDto.getFacilityId());
                if (htsId == 0L) {
                    hts.setStateId(htsDto.getStateId());
                    hts.setLgaId(htsDto.getLgaId());
                    hts.setFacilityId(htsDto.getFacilityId());
                    hts.setFacilityName(htsDto.getFacilityName());
                    hts.setDateVisit(DateUtil.parseStringToDate(htsDto.getDateVisit(), "yyyy-MM-dd"));
                    hts.setLongitude((double) htsDto.getLongitude());
                    hts.setLatitude((double) htsDto.getLatitude());
                    hts.setClientCode(htsDto.getClientCode());
                    hts.setReferredFrom(htsDto.getReferredFrom());
                    hts.setTestingSetting(htsDto.getTestingSetting());
                    hts.setSurname(htsDto.getSurname());
                    long assessmentId = new AssessmentJDBC().getHtsAssessment(htsDto.getClientCode(), htsDto.getFacilityId());
                    if (assessmentId != 0L) {
                        hts.setAssessmentId(assessmentId);
                    }
                    hts.setOtherNames(htsDto.getOtherNames());
                    hts.setDateBirth(DateUtil.parseStringToDate(htsDto.getDateBirth(), "yyyy-MM-dd"));
                    hts.setAge(htsDto.getAge());
                    hts.setAgeUnit(htsDto.getAgeUnit());
                    hts.setPhone(htsDto.getPhone());
                    hts.setAddress(htsDto.getAddress());
                    hts.setGender(htsDto.getGender());
                    hts.setFirstTimeVisit(htsDto.getFirstTimeVisit());
                    hts.setState(htsDto.getState());
                    hts.setLga(htsDto.getLga());
                    hts.setMaritalStatus(htsDto.getMaritalStatus());
                    hts.setNumChildren(htsDto.getNumChildren());
                    hts.setNumWives(htsDto.getNumWives());
                    hts.setTypeCounseling(htsDto.getTypeCounseling());
                    hts.setIndexClient(htsDto.getIndexClient());
                    hts.setTypeIndex(htsDto.getTypeIndex());
                    hts.setIndexClientCode(htsDto.getIndexClientCode());
                    hts.setDateRegistration(DateUtil.parseStringToDate(htsDto.getDateRegistration(), "yyyy-MM-dd"));
                    hts.setDateStarted(DateUtil.parseStringToDate(htsDto.getDateStarted(), "yyyy-MM-dd"));
                    hts.setKnowledgeAssessment1(htsDto.getKnowledgeAssessment1());
                    hts.setKnowledgeAssessment2(htsDto.getKnowledgeAssessment2());
                    hts.setKnowledgeAssessment3(htsDto.getKnowledgeAssessment3());
                    hts.setKnowledgeAssessment4(htsDto.getKnowledgeAssessment4());
                    hts.setKnowledgeAssessment5(htsDto.getKnowledgeAssessment5());
                    hts.setKnowledgeAssessment6(htsDto.getKnowledgeAssessment6());
                    hts.setKnowledgeAssessment7(htsDto.getKnowledgeAssessment7());

                    hts.setRiskAssessment1(htsDto.getRiskAssessment1());
                    hts.setRiskAssessment2(htsDto.getRiskAssessment2());
                    hts.setRiskAssessment3(htsDto.getRiskAssessment3());
                    hts.setRiskAssessment4(htsDto.getRiskAssessment4());
                    hts.setRiskAssessment5(htsDto.getRiskAssessment5());
                    hts.setRiskAssessment6(htsDto.getRiskAssessment6());

                    hts.setTbScreening1(htsDto.getTbScreening1());
                    hts.setTbScreening2(htsDto.getTbScreening2());
                    hts.setTbScreening3(htsDto.getTbScreening3());
                    hts.setTbScreening4(htsDto.getTbScreening4());

                    hts.setStiScreening1(htsDto.getStiScreening1());
                    hts.setStiScreening2(htsDto.getStiScreening2());
                    hts.setStiScreening3(htsDto.getStiScreening3());
                    hts.setStiScreening4(htsDto.getStiScreening4());
                    hts.setStiScreening5(htsDto.getStiScreening5());

                    hts.setHivTestResult(htsDto.getHivTestResult());

                    hts.setTestedHiv(htsDto.getTestedHiv());

                    hts.setPostTest1(htsDto.getPostTest1());
                    hts.setPostTest2(htsDto.getPostTest2());
                    hts.setPostTest3(htsDto.getPostTest3());
                    hts.setPostTest4(htsDto.getPostTest4());
                    hts.setPostTest5(htsDto.getPostTest5());
                    hts.setPostTest6(htsDto.getPostTest6());
                    hts.setPostTest7(htsDto.getPostTest7());
                    hts.setPostTest8(htsDto.getPostTest8());
                    hts.setPostTest9(htsDto.getPostTest9());
                    hts.setPostTest10(htsDto.getPostTest10());
                    hts.setPostTest11(htsDto.getPostTest11());
                    hts.setPostTest12(htsDto.getPostTest12());
                    hts.setPostTest13(htsDto.getPostTest13());
                    hts.setPostTest14(htsDto.getPostTest14());
                    hts.setPartnerNotification(htsDto.getPartnerNotification());
                    hts.setNotificationCounseling(htsDto.getNotificationCounseling());
                    hts.setNumberPartner(htsDto.getNumberPartner());
                    hts.setSyphilisTestResult(htsDto.getSyphilisTestResult());
                    hts.setDeviceconfigId(htsDto.getDeviceconfigId());
                    hts.setHepatitisbTestResult(htsDto.getHepatitisbTestResult());
                    hts.setHepatitiscTestResult(htsDto.getHepatitiscTestResult());
                    hts.setNote(htsDto.getNote());
                    hts.setStiReferred(htsDto.getStiReferred());
                    hts.setArtReferred(htsDto.getArtReferred());
                    HtsDAO.save(hts);
                } else {
                    hts.setHtsId(htsId);
                    hts.setStateId(htsDto.getStateId());
                    hts.setLgaId(htsDto.getLgaId());
                    hts.setFacilityId(htsDto.getFacilityId());
                    hts.setFacilityName(htsDto.getFacilityName());
                    hts.setDateVisit(DateUtil.parseStringToDate(htsDto.getDateVisit(), "yyyy-MM-dd"));
                    hts.setLongitude((double) htsDto.getLongitude());
                    hts.setLatitude((double) htsDto.getLatitude());
                    hts.setClientCode(htsDto.getClientCode());
                    hts.setReferredFrom(htsDto.getReferredFrom());
                    hts.setTestingSetting(htsDto.getTestingSetting());
                    hts.setSurname(htsDto.getSurname());
                    long assessmentId = new AssessmentJDBC().getHtsAssessment(htsDto.getClientCode(), htsDto.getFacilityId());
                    if (assessmentId != 0L) {
                        hts.setAssessmentId(assessmentId);
                    }

                    hts.setOtherNames(htsDto.getOtherNames());
                    hts.setDateBirth(DateUtil.parseStringToDate(htsDto.getDateBirth(), "yyyy-MM-dd"));
                    hts.setAge(htsDto.getAge());
                    hts.setAgeUnit(htsDto.getAgeUnit());
                    hts.setPhone(htsDto.getPhone());
                    hts.setAddress(htsDto.getAddress());
                    hts.setGender(htsDto.getGender());
                    hts.setFirstTimeVisit(htsDto.getFirstTimeVisit());
                    hts.setState(htsDto.getState());
                    hts.setLga(htsDto.getLga());
                    hts.setMaritalStatus(htsDto.getMaritalStatus());
                    hts.setNumChildren(htsDto.getNumChildren());
                    hts.setNumWives(htsDto.getNumWives());
                    hts.setTypeCounseling(htsDto.getTypeCounseling());
                    hts.setIndexClient(htsDto.getIndexClient());
                    hts.setTypeIndex(htsDto.getTypeIndex());
                    hts.setIndexClientCode(htsDto.getIndexClientCode());
                    hts.setDateRegistration(DateUtil.parseStringToDate(htsDto.getDateRegistration(), "yyyy-MM-dd"));
                    hts.setDateStarted(DateUtil.parseStringToDate(htsDto.getDateStarted(), "yyyy-MM-dd"));

                    hts.setKnowledgeAssessment1(htsDto.getKnowledgeAssessment1());
                    hts.setKnowledgeAssessment2(htsDto.getKnowledgeAssessment2());
                    hts.setKnowledgeAssessment3(htsDto.getKnowledgeAssessment3());
                    hts.setKnowledgeAssessment4(htsDto.getKnowledgeAssessment4());
                    hts.setKnowledgeAssessment5(htsDto.getKnowledgeAssessment5());
                    hts.setKnowledgeAssessment6(htsDto.getKnowledgeAssessment6());
                    hts.setKnowledgeAssessment7(htsDto.getKnowledgeAssessment7());

                    hts.setRiskAssessment1(htsDto.getRiskAssessment1());
                    hts.setRiskAssessment2(htsDto.getRiskAssessment2());
                    hts.setRiskAssessment3(htsDto.getRiskAssessment3());
                    hts.setRiskAssessment4(htsDto.getRiskAssessment4());
                    hts.setRiskAssessment5(htsDto.getRiskAssessment5());
                    hts.setRiskAssessment6(htsDto.getRiskAssessment6());

                    hts.setTbScreening1(htsDto.getTbScreening1());
                    hts.setTbScreening2(htsDto.getTbScreening2());
                    hts.setTbScreening3(htsDto.getTbScreening3());
                    hts.setTbScreening4(htsDto.getTbScreening4());

                    hts.setStiScreening1(htsDto.getStiScreening1());
                    hts.setStiScreening2(htsDto.getStiScreening2());
                    hts.setStiScreening3(htsDto.getStiScreening3());
                    hts.setStiScreening4(htsDto.getStiScreening4());
                    hts.setStiScreening5(htsDto.getStiScreening5());

                    hts.setHivTestResult(htsDto.getHivTestResult());

                    hts.setTestedHiv(htsDto.getTestedHiv());

                    hts.setPostTest1(htsDto.getPostTest1());
                    hts.setPostTest2(htsDto.getPostTest2());
                    hts.setPostTest3(htsDto.getPostTest3());
                    hts.setPostTest4(htsDto.getPostTest4());
                    hts.setPostTest5(htsDto.getPostTest5());
                    hts.setPostTest6(htsDto.getPostTest6());
                    hts.setPostTest7(htsDto.getPostTest7());
                    hts.setPostTest8(htsDto.getPostTest8());
                    hts.setPostTest9(htsDto.getPostTest9());
                    hts.setPostTest10(htsDto.getPostTest10());
                    hts.setPostTest11(htsDto.getPostTest11());
                    hts.setPostTest12(htsDto.getPostTest12());
                    hts.setPostTest13(htsDto.getPostTest13());
                    hts.setPostTest14(htsDto.getPostTest14());
                    hts.setPartnerNotification(htsDto.getPartnerNotification());
                    hts.setNotificationCounseling(htsDto.getNotificationCounseling());
                    hts.setNumberPartner(htsDto.getNumberPartner());
                    hts.setSyphilisTestResult(htsDto.getSyphilisTestResult());
                    hts.setDeviceconfigId(htsDto.getDeviceconfigId());
                    hts.setHepatitisbTestResult(htsDto.getHepatitisbTestResult());
                    hts.setHepatitiscTestResult(htsDto.getHepatitiscTestResult());
                    hts.setNote(htsDto.getNote());
                    hts.setStiReferred(htsDto.getStiReferred());
                    hts.setArtReferred(htsDto.getArtReferred());
                    HtsDAO.update(hts);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);

        }

    }

    public static synchronized void saveIndexContact(List<IndexContactDTO> indexcontactList) {
        try {
            for (IndexContactDTO indexcontactDto : indexcontactList) {
                Indexcontact indexContact = new Indexcontact();
                indexContact.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
                long htsId = new HtsJDBC().getHtsId(indexcontactDto.getClientCode(), indexcontactDto.getFacilityId());
                Hts hts = new Hts();
                hts.setHtsId(htsId);
                indexContact.setHts(hts);
                long indexContactId = new IndexcontactJDBC().getIndexcontactId(indexcontactDto.getIndexContactCode(), indexcontactDto.getFacilityId());

                if (indexContactId == 0L) {
                    indexContact.setFacilityId(indexcontactDto.getFacilityId());
                    indexContact.setContactType(indexcontactDto.getContactType());
                    indexContact.setDeviceconfigId(indexcontactDto.getDeviceconfigId());
                    indexContact.setIndexContactCode(indexcontactDto.getIndexContactCode());
                    indexContact.setClientCode(indexcontactDto.getClientCode());
                    indexContact.setSurname(indexcontactDto.getSurname());
                    indexContact.setOtherNames(indexcontactDto.getOtherNames());
                    indexContact.setAge(indexcontactDto.getAge());
                    indexContact.setGender(indexcontactDto.getGender());
                    indexContact.setAddress(indexcontactDto.getAddress());
                    indexContact.setPhone(indexcontactDto.getPhone());
                    indexContact.setRelationship(indexcontactDto.getRelationship());
                    indexContact.setGbv(indexcontactDto.getGbv());
                    indexContact.setDurationPartner(indexcontactDto.getDurationPartner());
                    indexContact.setPhoneTracking(indexcontactDto.getPhoneTracking());
                    indexContact.setHomeTracking(indexcontactDto.getHomeTracking());
                    indexContact.setOutcome(indexcontactDto.getOutcome());
                    indexContact.setHivStatus(indexcontactDto.getHivStatus());
                    indexContact.setDateHivTest(DateUtil.parseStringToDate(indexcontactDto.getDateHivTest(), "yyyy-MM-dd"));
                    indexContact.setLinkCare(indexcontactDto.getLinkCare());
                    indexContact.setPartnerNotification(indexcontactDto.getPartnerNotification());
                    indexContact.setModeNotification(indexcontactDto.getModeNotification());
                    indexContact.setServiceProvided(indexcontactDto.getServiceProvided());
                    IndexcontactDAO.save(indexContact);
                } else {
                    indexContact.setIndexcontactId(indexContactId);
                    indexContact.setFacilityId(indexcontactDto.getFacilityId());
                    indexContact.setContactType(indexcontactDto.getContactType());
                    indexContact.setDeviceconfigId(indexcontactDto.getDeviceconfigId());
                    indexContact.setIndexContactCode(indexcontactDto.getIndexContactCode());
                    indexContact.setClientCode(indexcontactDto.getClientCode());
                    indexContact.setSurname(indexcontactDto.getSurname());
                    indexContact.setOtherNames(indexcontactDto.getOtherNames());
                    indexContact.setAge(indexcontactDto.getAge());
                    indexContact.setGender(indexcontactDto.getGender());
                    indexContact.setAddress(indexcontactDto.getAddress());
                    indexContact.setPhone(indexcontactDto.getPhone());
                    indexContact.setRelationship(indexcontactDto.getRelationship());
                    indexContact.setGbv(indexcontactDto.getGbv());
                    indexContact.setDurationPartner(indexcontactDto.getDurationPartner());
                    indexContact.setPhoneTracking(indexcontactDto.getPhoneTracking());
                    indexContact.setHomeTracking(indexcontactDto.getHomeTracking());
                    indexContact.setOutcome(indexcontactDto.getOutcome());
                    indexContact.setHivStatus(indexcontactDto.getHivStatus());
                    indexContact.setDateHivTest(DateUtil.parseStringToDate(indexcontactDto.getDateHivTest(), "yyyy-MM-dd"));
                    indexContact.setLinkCare(indexcontactDto.getLinkCare());
                    indexContact.setPartnerNotification(indexcontactDto.getPartnerNotification());
                    indexContact.setModeNotification(indexcontactDto.getModeNotification());
                    indexContact.setServiceProvided(indexcontactDto.getServiceProvided());
                    IndexcontactDAO.update(indexContact);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            //return Response.serverError().build();
            throw new RuntimeException(e);
        }
        // return Response.ok().build();
    }

//    public static synchronized void savePatients(List<PatientDTO> patientList) {
//        System.out.println("SERVER THRE");
//        try {
//            System.out.println("PATIENT LIST "+patientList.toString());
//           System.out.println("SERVER FOUR"+ patientList.size());
//            for (PatientDTO patientDto : patientList) {
//                System.out.println("SERVER FIVE");
//                Patient patient = new Patient();
//                System.out.println("name: "+patientDto.getSurname());
//                System.out.println("HOSPITAL NUMBER "+patientDto.getHospitalNum());
//                 System.out.println("FACILITY ID "+patientDto.getFacility().getFacilityId());
//                
//                // patient.setTimeStamp(new java.sql.Date(new java.util.Date().getTime()));
//              
//                long patientId = new PatientJDBC().getPatientId(patientDto.getFacility().getFacilityId(), patientDto.getHospitalNum());
//                System.out.println("patientId " + patientId);
//                if (patientId == 0L) {
//                    Facility facility = new Facility();
//                    facility.setFacilityId(patientDto.getFacility().getFacilityId());
//                    patient.setFacility(facility);
//                    patient.setHtsId(patientDto.getHtsId());
//                    patient.setDeviceconfigId(patientDto.getDeviceconfigId());
//                    patient.setHospitalNum(patientDto.getHospitalNum());
//                    patient.setUniqueId(patientDto.getUniqueId());
//                    patient.setSurname(patientDto.getSurname());
//                    patient.setOtherNames(patientDto.getOtherNames());
//                    patient.setDateBirth(DateUtil.parseStringToDate(patientDto.getDateBirth(), "yyyy-MM-dd"));
//                    patient.setAge(patientDto.getAge());
//                    patient.setGender(patientDto.getGender());
//                    patient.setAgeUnit(patientDto.getAgeUnit());
//                    patient.setMaritalStatus(patientDto.getMaritalStatus());
//                    patient.setOccupation(patientDto.getOccupation());
//                    patient.setEducation(patientDto.getEducation());
//                    patient.setAddress(patientDto.getAddress());
//                    patient.setPhone(patientDto.getPhone());
//                    patient.setState(patientDto.getState());
//                    patient.setLga(patientDto.getLga());
//                    patient.setNextKin(patientDto.getNextKin());
//                    patient.setAddressKin(patientDto.getAddress());
//                    patient.setPhoneKin(patientDto.getPhoneKin());
//                    patient.setRelationKin(patientDto.getRelationKin());
//                    patient.setEntryPoint(patientDto.getEntryPoint());
//                    patient.setTargetGroup(patientDto.getTargetGroup());
//                    patient.setDateConfirmedHiv(DateUtil.parseStringToDate(patientDto.getDateConfirmedHiv(), "yyyy-MM-dd"));
//                    patient.setTbStatus(patientDto.getTbStatus());
//                    patient.setPregnant(patientDto.getPregnant());
//                    patient.setBreastfeeding(patientDto.getBreastfeeding());
//
//                    patient.setDateRegistration(DateUtil.parseStringToDate(patientDto.getDateRegistration(), "yyyy-MM-dd"));
//                    patient.setStatusRegistration(patientDto.getStatusRegistration());
//                    PatientDAO.save(patient);
//                    System.out.println("saved ");
//                } else {
//                    patient.setPatientId(patientId);
//                    Facility facility = new Facility();
//                    facility.setFacilityId(patientDto.getFacility().getFacilityId());
//                    patient.setFacility(facility);
//                    patient.setHtsId(patientDto.getHtsId());
//                    patient.setDeviceconfigId(patientDto.getDeviceconfigId());
//                    patient.setHospitalNum(patientDto.getHospitalNum());
//                    patient.setUniqueId(patientDto.getUniqueId());
//                    patient.setSurname(patientDto.getSurname());
//                    patient.setOtherNames(patientDto.getOtherNames());
//                    patient.setDateBirth(DateUtil.parseStringToDate(patientDto.getDateBirth(), "yyyy-MM-dd"));
//                    patient.setAge(patientDto.getAge());
//                    patient.setGender(patientDto.getGender());
//                    patient.setAgeUnit(patientDto.getAgeUnit());
//                    patient.setMaritalStatus(patientDto.getMaritalStatus());
//                    patient.setOccupation(patientDto.getOccupation());
//                    patient.setEducation(patientDto.getEducation());
//                    patient.setAddress(patientDto.getAddress());
//                    patient.setPhone(patientDto.getPhone());
//                    patient.setState(patientDto.getState());
//                    patient.setLga(patientDto.getLga());
//                    patient.setNextKin(patientDto.getNextKin());
//                    patient.setAddressKin(patientDto.getAddress());
//                    patient.setPhoneKin(patientDto.getPhoneKin());
//                    patient.setRelationKin(patientDto.getRelationKin());
//                    patient.setEntryPoint(patientDto.getEntryPoint());
//                    patient.setTargetGroup(patientDto.getTargetGroup());
//                    patient.setDateConfirmedHiv(DateUtil.parseStringToDate(patientDto.getDateConfirmedHiv(), "yyyy-MM-dd"));
//                    patient.setTbStatus(patientDto.getTbStatus());
//                    patient.setPregnant(patientDto.getPregnant());
//                    patient.setBreastfeeding(patientDto.getBreastfeeding());
//
//                    patient.setDateRegistration(DateUtil.parseStringToDate(patientDto.getDateRegistration(), "yyyy-MM-dd"));
//                    patient.setStatusRegistration(patientDto.getStatusRegistration());
//                    System.out.println("saved 2 ");
//                    PatientDAO.update(patient);
//                }
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("saved  " + e.getMessage());
//            throw new RuntimeException(e);
//        }
//
//    }
//
//    public static synchronized void saveClinic(List<ClinicDTO> clinicList) {
//        try {
//            for (ClinicDTO clinicDto : clinicList) {
//                Clinic clinic = new Clinic();
//                clinic.setTimeStamp(new java.sql.Date(new java.util.Date().getTime()));
//                long patientId = new PatientJDBC().getPatientId(clinicDto.getFacilityId(), clinicDto.getPatient().getHospitalNum());
//                Patient patient = new Patient();
//                patient.setPatientId(patientId);
//                clinic.setPatient(patient);
//                long clinicId = new ClinicJDBC().getClinicId(patientId, DateUtil.parseStringToDate(clinicDto.getDateVisit(), "yyyy-MM-dd"));
//                if (clinicId == 0L) {
//                    clinic.setDeviceconfigId(clinicDto.getDeviceconfigId());
//                    clinic.setBp(clinicDto.getBp());
//                    clinic.setDateVisit(DateUtil.parseStringToDate(clinicDto.getDateVisit(), "yyyy-MM-dd"));
//                    clinic.setHeight(clinicDto.getHeight());
//                    clinic.setBodyWeight(clinicDto.getBodyWeight());
//                    clinic.setTbStatus(clinicDto.getTbStatus());
//                    clinic.setFuncStatus(clinicDto.getFuncStatus());
//                    clinic.setRegimen(clinicDto.getRegimen());
//                    clinic.setFacilityId(clinicDto.getFacilityId());
//                    clinic.setWaist(clinicDto.getWaist());
//                    clinic.setRegimentype(clinicDto.getRegimentype());
//                    clinic.setBreastfeeding(clinicDto.getBreastfeeding());
//                    clinic.setNextAppointment(DateUtil.parseStringToDate(clinicDto.getNextAppointment(), "yyyy-MM-dd"));
//                    ClinicDAO.save(clinic);
//                } else {
//                    clinic.setClinicId(clinicId);
//                    clinic.setDeviceconfigId(clinicDto.getDeviceconfigId());
//                    clinic.setBp(clinicDto.getBp());
//                    clinic.setDateVisit(DateUtil.parseStringToDate(clinicDto.getDateVisit(), "yyyy-MM-dd"));
//                    clinic.setHeight(clinicDto.getHeight());
//                    clinic.setBodyWeight(clinicDto.getBodyWeight());
//                    clinic.setTbStatus(clinicDto.getTbStatus());
//                    clinic.setFuncStatus(clinicDto.getFuncStatus());
//                    clinic.setRegimen(clinicDto.getRegimen());
//                    clinic.setFacilityId(clinicDto.getFacilityId());
//                    clinic.setWaist(clinicDto.getWaist());
//                    clinic.setRegimentype(clinicDto.getRegimentype());
//                    clinic.setBreastfeeding(clinicDto.getBreastfeeding());
//                    clinic.setNextAppointment(DateUtil.parseStringToDate(clinicDto.getNextAppointment(), "yyyy-MM-dd"));
//                    ClinicDAO.update(clinic);
//                }
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
//
//    }

}
