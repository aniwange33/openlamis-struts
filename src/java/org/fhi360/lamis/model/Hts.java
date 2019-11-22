/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.model;

import java.util.Date;

/**
 *
 * @author user10
 */
public class Hts implements java.io.Serializable {

    private Long htsId;
    private Long assessmentId;
    private Long stateId;
    private Long lgaId;
    private Long facilityId;
    private String facilityName;
    private Date dateVisit;
    private String clientCode;
    private String hospitalNum;
    private String referredFrom;
    private String testingSetting;
    private String surname;
    private String otherNames;
    private Date dateBirth;
    private Integer age;
    private String ageUnit;
    private String phone;
    private String address;
    private String gender;
    private String firstTimeVisit;
    private String state;
    private String lga;
    private String maritalStatus;
    private Integer numChildren;
    private Integer numWives;
    private String typeCounseling;
    private String indexClient;
    private String typeIndex;
    private String indexClientCode;
    private Integer knowledgeAssessment1;
    private Integer knowledgeAssessment2;
    private Integer knowledgeAssessment3;
    private Integer knowledgeAssessment4;
    private Integer knowledgeAssessment5;
    private Integer knowledgeAssessment6;
    private Integer knowledgeAssessment7;
    private Integer riskAssessment1;
    private Integer riskAssessment2;
    private Integer riskAssessment3;
    private Integer riskAssessment4;
    private Integer riskAssessment5;
    private Integer riskAssessment6;
    private Integer tbScreening1;
    private Integer tbScreening2;
    private Integer tbScreening3;
    private Integer tbScreening4;
    private Integer stiScreening1;
    private Integer stiScreening2;
    private Integer stiScreening3;
    private Integer stiScreening4;
    private Integer stiScreening5;
    private String hivTestResult;
    private String testedHiv;
    private Integer postTest1;
    private Integer postTest2;
    private Integer postTest3;
    private Integer postTest4;
    private Integer postTest5;
    private Integer postTest6;
    private Integer postTest7;
    private Integer postTest8;
    private Integer postTest9;
    private Integer postTest10;
    private Integer postTest11;
    private Integer postTest12;
    private Integer postTest13;
    private Integer postTest14;
    private String syphilisTestResult;
    private String hepatitisbTestResult;
    private String hepatitiscTestResult;
    private String note;
    private String artReferred;
    private String partnerNotification;
    private String tbReferred;
    private String stiReferred;
    private Double latitude;
    private Double longitude;
    private Date dateStarted;
    private Date dateRegistration;
    private Date timeStamp;
    private Long userId;
    private Integer uploaded;
    private Date timeUploaded;
    private Long deviceconfigId;
    private String notificationCounseling;
    private int numberPartner;
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Hts() {
    }

    public Hts(Long htsId, Long stateId, Long lgaId, Long facilityId, Date dateVisit, String clientCode) {
        this.htsId = htsId;
        this.stateId = stateId;
        this.lgaId = lgaId;
        this.facilityId = facilityId;
        this.dateVisit = dateVisit;
        this.clientCode = clientCode;
    }

    public Hts(Long htsId, Long assessmentId, Long stateId, Long lgaId, Long facilityId, String facilityName, Date dateVisit, String clientCode, String hospitalNum, String referredFrom, String testingSetting, String surname, String otherNames, Date dateBirth, Integer age, String ageUnit, String phone, String address, String gender, String firstTimeVisit, String state, String lga, String maritalStatus, Integer numChildren, Integer numWives, String typeCounseling, String indexClient, String typeIndex, String indexClientCode, Integer knowledgeAssessment1, Integer knowledgeAssessment2, Integer knowledgeAssessment3, Integer knowledgeAssessment4, Integer knowledgeAssessment5, Integer knowledgeAssessment6, Integer knowledgeAssessment7, Integer riskAssessment1, Integer riskAssessment2, Integer riskAssessment3, Integer riskAssessment4, Integer riskAssessment5, Integer riskAssessment6, Integer tbScreening1, Integer tbScreening2, Integer tbScreening3, Integer tbScreening4, Integer stiScreening1, Integer stiScreening2, Integer stiScreening3, Integer stiScreening4, Integer stiScreening5, String hivTestResult, String testedHiv, Integer postTest1, Integer postTest2, Integer postTest3, Integer postTest4, Integer postTest5, Integer postTest6, Integer postTest7, Integer postTest8, Integer postTest9, Integer postTest10, Integer postTest11, Integer postTest12, Integer postTest13, Integer postTest14, String syphilisTestResult, String hepatitisbTestResult, String hepatitiscTestResult, String note, String artReferred, String partnerNotification, String tbReferred, String stiReferred, Double latitude, Double longitude, Date dateStarted, Date dateRegistration, Date timeStamp, Long userId, Integer uploaded, Date timeUploaded, Long deviceconfigId, String notificationCounseling, int numberPartner, String uuid) {
        this.htsId = htsId;
        this.assessmentId = assessmentId;
        this.stateId = stateId;
        this.lgaId = lgaId;
        this.facilityId = facilityId;
        this.facilityName = facilityName;
        this.dateVisit = dateVisit;
        this.clientCode = clientCode;
        this.hospitalNum = hospitalNum;
        this.referredFrom = referredFrom;
        this.testingSetting = testingSetting;
        this.surname = surname;
        this.otherNames = otherNames;
        this.dateBirth = dateBirth;
        this.age = age;
        this.ageUnit = ageUnit;
        this.phone = phone;
        this.address = address;
        this.gender = gender;
        this.firstTimeVisit = firstTimeVisit;
        this.state = state;
        this.lga = lga;
        this.maritalStatus = maritalStatus;
        this.numChildren = numChildren;
        this.numWives = numWives;
        this.typeCounseling = typeCounseling;
        this.indexClient = indexClient;
        this.typeIndex = typeIndex;
        this.indexClientCode = indexClientCode;
        this.knowledgeAssessment1 = knowledgeAssessment1;
        this.knowledgeAssessment2 = knowledgeAssessment2;
        this.knowledgeAssessment3 = knowledgeAssessment3;
        this.knowledgeAssessment4 = knowledgeAssessment4;
        this.knowledgeAssessment5 = knowledgeAssessment5;
        this.knowledgeAssessment6 = knowledgeAssessment6;
        this.knowledgeAssessment7 = knowledgeAssessment7;
        this.riskAssessment1 = riskAssessment1;
        this.riskAssessment2 = riskAssessment2;
        this.riskAssessment3 = riskAssessment3;
        this.riskAssessment4 = riskAssessment4;
        this.riskAssessment5 = riskAssessment5;
        this.riskAssessment6 = riskAssessment6;
        this.tbScreening1 = tbScreening1;
        this.tbScreening2 = tbScreening2;
        this.tbScreening3 = tbScreening3;
        this.tbScreening4 = tbScreening4;
        this.stiScreening1 = stiScreening1;
        this.stiScreening2 = stiScreening2;
        this.stiScreening3 = stiScreening3;
        this.stiScreening4 = stiScreening4;
        this.stiScreening5 = stiScreening5;
        this.hivTestResult = hivTestResult;
        this.testedHiv = testedHiv;
        this.postTest1 = postTest1;
        this.postTest2 = postTest2;
        this.postTest3 = postTest3;
        this.postTest4 = postTest4;
        this.postTest5 = postTest5;
        this.postTest6 = postTest6;
        this.postTest7 = postTest7;
        this.postTest8 = postTest8;
        this.postTest9 = postTest9;
        this.postTest10 = postTest10;
        this.postTest11 = postTest11;
        this.postTest12 = postTest12;
        this.postTest13 = postTest13;
        this.postTest14 = postTest14;
        this.syphilisTestResult = syphilisTestResult;
        this.hepatitisbTestResult = hepatitisbTestResult;
        this.hepatitiscTestResult = hepatitiscTestResult;
        this.note = note;
        this.artReferred = artReferred;
        this.partnerNotification = partnerNotification;
        this.tbReferred = tbReferred;
        this.stiReferred = stiReferred;
        this.latitude = latitude;
        this.longitude = longitude;
        this.dateStarted = dateStarted;
        this.dateRegistration = dateRegistration;
        this.timeStamp = timeStamp;
        this.userId = userId;
        this.uploaded = uploaded;
        this.timeUploaded = timeUploaded;
        this.deviceconfigId = deviceconfigId;
        this.notificationCounseling = notificationCounseling;
        this.numberPartner = numberPartner;
        this.uuid = uuid;
    }

    public Long getHtsId() {
        return htsId;
    }

    public void setHtsId(Long htsId) {
        this.htsId = htsId;
    }

    public Long getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(Long assessmentId) {
        this.assessmentId = assessmentId;
    }

    public Long getStateId() {
        return stateId;
    }

    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

    public Long getLgaId() {
        return lgaId;
    }

    public void setLgaId(Long lgaId) {
        this.lgaId = lgaId;
    }

    public Long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public Date getDateVisit() {
        return dateVisit;
    }

    public void setDateVisit(Date dateVisit) {
        this.dateVisit = dateVisit;
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public String getHospitalNum() {
        return hospitalNum;
    }

    public void setHospitalNum(String hospitalNum) {
        this.hospitalNum = hospitalNum;
    }

    public String getReferredFrom() {
        return referredFrom;
    }

    public void setReferredFrom(String referredFrom) {
        this.referredFrom = referredFrom;
    }

    public String getTestingSetting() {
        return testingSetting;
    }

    public void setTestingSetting(String testingSetting) {
        this.testingSetting = testingSetting;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getOtherNames() {
        return otherNames;
    }

    public void setOtherNames(String otherNames) {
        this.otherNames = otherNames;
    }

    public Date getDateBirth() {
        return dateBirth;
    }

    public void setDateBirth(Date dateBirth) {
        this.dateBirth = dateBirth;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getAgeUnit() {
        return ageUnit;
    }

    public void setAgeUnit(String ageUnit) {
        this.ageUnit = ageUnit;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFirstTimeVisit() {
        return firstTimeVisit;
    }

    public void setFirstTimeVisit(String firstTimeVisit) {
        this.firstTimeVisit = firstTimeVisit;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLga() {
        return lga;
    }

    public void setLga(String lga) {
        this.lga = lga;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public Integer getNumChildren() {
        return numChildren;
    }

    public void setNumChildren(Integer numChildren) {
        this.numChildren = numChildren;
    }

    public Integer getNumWives() {
        return numWives;
    }

    public void setNumWives(Integer numWives) {
        this.numWives = numWives;
    }

    public String getTypeCounseling() {
        return typeCounseling;
    }

    public void setTypeCounseling(String typeCounseling) {
        this.typeCounseling = typeCounseling;
    }

    public String getIndexClient() {
        return indexClient;
    }

    public void setIndexClient(String indexClient) {
        this.indexClient = indexClient;
    }

    public String getTypeIndex() {
        return typeIndex;
    }

    public void setTypeIndex(String typeIndex) {
        this.typeIndex = typeIndex;
    }

    public String getIndexClientCode() {
        return indexClientCode;
    }

    public void setIndexClientCode(String indexClientCode) {
        this.indexClientCode = indexClientCode;
    }

    public Integer getKnowledgeAssessment1() {
        return knowledgeAssessment1;
    }

    public void setKnowledgeAssessment1(Integer knowledgeAssessment1) {
        this.knowledgeAssessment1 = knowledgeAssessment1;
    }

    public Integer getKnowledgeAssessment2() {
        return knowledgeAssessment2;
    }

    public void setKnowledgeAssessment2(Integer knowledgeAssessment2) {
        this.knowledgeAssessment2 = knowledgeAssessment2;
    }

    public Integer getKnowledgeAssessment3() {
        return knowledgeAssessment3;
    }

    public void setKnowledgeAssessment3(Integer knowledgeAssessment3) {
        this.knowledgeAssessment3 = knowledgeAssessment3;
    }

    public Integer getKnowledgeAssessment4() {
        return knowledgeAssessment4;
    }

    public void setKnowledgeAssessment4(Integer knowledgeAssessment4) {
        this.knowledgeAssessment4 = knowledgeAssessment4;
    }

    public Integer getKnowledgeAssessment5() {
        return knowledgeAssessment5;
    }

    public void setKnowledgeAssessment5(Integer knowledgeAssessment5) {
        this.knowledgeAssessment5 = knowledgeAssessment5;
    }

    public Integer getKnowledgeAssessment6() {
        return knowledgeAssessment6;
    }

    public void setKnowledgeAssessment6(Integer knowledgeAssessment6) {
        this.knowledgeAssessment6 = knowledgeAssessment6;
    }

    public Integer getKnowledgeAssessment7() {
        return knowledgeAssessment7;
    }

    public void setKnowledgeAssessment7(Integer knowledgeAssessment7) {
        this.knowledgeAssessment7 = knowledgeAssessment7;
    }

    public Integer getRiskAssessment1() {
        return riskAssessment1;
    }

    public void setRiskAssessment1(Integer riskAssessment1) {
        this.riskAssessment1 = riskAssessment1;
    }

    public Integer getRiskAssessment2() {
        return riskAssessment2;
    }

    public void setRiskAssessment2(Integer riskAssessment2) {
        this.riskAssessment2 = riskAssessment2;
    }

    public Integer getRiskAssessment3() {
        return riskAssessment3;
    }

    public void setRiskAssessment3(Integer riskAssessment3) {
        this.riskAssessment3 = riskAssessment3;
    }

    public Integer getRiskAssessment4() {
        return riskAssessment4;
    }

    public void setRiskAssessment4(Integer riskAssessment4) {
        this.riskAssessment4 = riskAssessment4;
    }

    public Integer getRiskAssessment5() {
        return riskAssessment5;
    }

    public void setRiskAssessment5(Integer riskAssessment5) {
        this.riskAssessment5 = riskAssessment5;
    }

    public Integer getRiskAssessment6() {
        return riskAssessment6;
    }

    public void setRiskAssessment6(Integer riskAssessment6) {
        this.riskAssessment6 = riskAssessment6;
    }

    public Integer getTbScreening1() {
        return tbScreening1;
    }

    public void setTbScreening1(Integer tbScreening1) {
        this.tbScreening1 = tbScreening1;
    }

    public Integer getTbScreening2() {
        return tbScreening2;
    }

    public void setTbScreening2(Integer tbScreening2) {
        this.tbScreening2 = tbScreening2;
    }

    public Integer getTbScreening3() {
        return tbScreening3;
    }

    public void setTbScreening3(Integer tbScreening3) {
        this.tbScreening3 = tbScreening3;
    }

    public Integer getTbScreening4() {
        return tbScreening4;
    }

    public void setTbScreening4(Integer tbScreening4) {
        this.tbScreening4 = tbScreening4;
    }

    public Integer getStiScreening1() {
        return stiScreening1;
    }

    public void setStiScreening1(Integer stiScreening1) {
        this.stiScreening1 = stiScreening1;
    }

    public Integer getStiScreening2() {
        return stiScreening2;
    }

    public void setStiScreening2(Integer stiScreening2) {
        this.stiScreening2 = stiScreening2;
    }

    public Integer getStiScreening3() {
        return stiScreening3;
    }

    public void setStiScreening3(Integer stiScreening3) {
        this.stiScreening3 = stiScreening3;
    }

    public Integer getStiScreening4() {
        return stiScreening4;
    }

    public void setStiScreening4(Integer stiScreening4) {
        this.stiScreening4 = stiScreening4;
    }

    public Integer getStiScreening5() {
        return stiScreening5;
    }

    public void setStiScreening5(Integer stiScreening5) {
        this.stiScreening5 = stiScreening5;
    }

    public String getHivTestResult() {
        return hivTestResult;
    }

    public void setHivTestResult(String hivTestResult) {
        this.hivTestResult = hivTestResult;
    }

    public String getTestedHiv() {
        return testedHiv;
    }

    public void setTestedHiv(String testedHiv) {
        this.testedHiv = testedHiv;
    }

    public Integer getPostTest1() {
        return postTest1;
    }

    public void setPostTest1(Integer postTest1) {
        this.postTest1 = postTest1;
    }

    public Integer getPostTest2() {
        return postTest2;
    }

    public void setPostTest2(Integer postTest2) {
        this.postTest2 = postTest2;
    }

    public Integer getPostTest3() {
        return postTest3;
    }

    public void setPostTest3(Integer postTest3) {
        this.postTest3 = postTest3;
    }

    public Integer getPostTest4() {
        return postTest4;
    }

    public void setPostTest4(Integer postTest4) {
        this.postTest4 = postTest4;
    }

    public Integer getPostTest5() {
        return postTest5;
    }

    public void setPostTest5(Integer postTest5) {
        this.postTest5 = postTest5;
    }

    public Integer getPostTest6() {
        return postTest6;
    }

    public void setPostTest6(Integer postTest6) {
        this.postTest6 = postTest6;
    }

    public Integer getPostTest7() {
        return postTest7;
    }

    public void setPostTest7(Integer postTest7) {
        this.postTest7 = postTest7;
    }

    public Integer getPostTest8() {
        return postTest8;
    }

    public void setPostTest8(Integer postTest8) {
        this.postTest8 = postTest8;
    }

    public Integer getPostTest9() {
        return postTest9;
    }

    public void setPostTest9(Integer postTest9) {
        this.postTest9 = postTest9;
    }

    public Integer getPostTest10() {
        return postTest10;
    }

    public void setPostTest10(Integer postTest10) {
        this.postTest10 = postTest10;
    }

    public Integer getPostTest11() {
        return postTest11;
    }

    public void setPostTest11(Integer postTest11) {
        this.postTest11 = postTest11;
    }

    public Integer getPostTest12() {
        return postTest12;
    }

    public void setPostTest12(Integer postTest12) {
        this.postTest12 = postTest12;
    }

    public Integer getPostTest13() {
        return postTest13;
    }

    public void setPostTest13(Integer postTest13) {
        this.postTest13 = postTest13;
    }

    public Integer getPostTest14() {
        return postTest14;
    }

    public void setPostTest14(Integer postTest14) {
        this.postTest14 = postTest14;
    }

    public String getSyphilisTestResult() {
        return syphilisTestResult;
    }

    public void setSyphilisTestResult(String syphilisTestResult) {
        this.syphilisTestResult = syphilisTestResult;
    }

    public String getHepatitisbTestResult() {
        return hepatitisbTestResult;
    }

    public void setHepatitisbTestResult(String hepatitisbTestResult) {
        this.hepatitisbTestResult = hepatitisbTestResult;
    }

    public String getHepatitiscTestResult() {
        return hepatitiscTestResult;
    }

    public void setHepatitiscTestResult(String hepatitiscTestResult) {
        this.hepatitiscTestResult = hepatitiscTestResult;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getArtReferred() {
        return artReferred;
    }

    public void setArtReferred(String artReferred) {
        this.artReferred = artReferred;
    }

    public String getPartnerNotification() {
        return partnerNotification;
    }

    public void setPartnerNotification(String partnerNotification) {
        this.partnerNotification = partnerNotification;
    }

    public String getTbReferred() {
        return tbReferred;
    }

    public void setTbReferred(String tbReferred) {
        this.tbReferred = tbReferred;
    }

    public String getStiReferred() {
        return stiReferred;
    }

    public void setStiReferred(String stiReferred) {
        this.stiReferred = stiReferred;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Date getDateStarted() {
        return dateStarted;
    }

    public void setDateStarted(Date dateStarted) {
        this.dateStarted = dateStarted;
    }

    public Date getDateRegistration() {
        return dateRegistration;
    }

    public void setDateRegistration(Date dateRegistration) {
        this.dateRegistration = dateRegistration;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getUploaded() {
        return uploaded;
    }

    public void setUploaded(Integer uploaded) {
        this.uploaded = uploaded;
    }

    public Date getTimeUploaded() {
        return timeUploaded;
    }

    public void setTimeUploaded(Date timeUploaded) {
        this.timeUploaded = timeUploaded;
    }

    /**
     * @return the deviceconfigId
     */
    public Long getDeviceconfigId() {
        return deviceconfigId;
    }

    /**
     * @param deviceconfigId the deviceconfigId to set
     */
    public void setDeviceconfigId(Long deviceconfigId) {
        this.deviceconfigId = deviceconfigId;
    }

    /**
     * @return the notificationCounseling
     */
    public String getNotificationCounseling() {
        return notificationCounseling;
    }

    /**
     * @param notificationCounseling the notificationCounseling to set
     */
    public void setNotificationCounseling(String notificationCounseling) {
        this.notificationCounseling = notificationCounseling;
    }

    /**
     * @return the numberPartner
     */
    public int getNumberPartner() {
        return numberPartner;
    }

    /**
     * @param numberPartner the numberPartner to set
     */
    public void setNumberPartner(int numberPartner) {
        this.numberPartner = numberPartner;
    }

    @Override
    public String toString() {
        return "Hts{" + "htsId=" + htsId + ", assessmentId=" + assessmentId + ", stateId=" + stateId + ", lgaId=" + lgaId + ", facilityId=" + facilityId + ", facilityName=" + facilityName + ", dateVisit=" + dateVisit + ", clientCode=" + clientCode + ", hospitalNum=" + hospitalNum + ", referredFrom=" + referredFrom + ", testingSetting=" + testingSetting + ", surname=" + surname + ", otherNames=" + otherNames + ", dateBirth=" + dateBirth + ", age=" + age + ", ageUnit=" + ageUnit + ", phone=" + phone + ", address=" + address + ", gender=" + gender + ", firstTimeVisit=" + firstTimeVisit + ", state=" + state + ", lga=" + lga + ", maritalStatus=" + maritalStatus + ", numChildren=" + numChildren + ", numWives=" + numWives + ", typeCounseling=" + typeCounseling + ", indexClient=" + indexClient + ", typeIndex=" + typeIndex + ", indexClientCode=" + indexClientCode + ", knowledgeAssessment1=" + knowledgeAssessment1 + ", knowledgeAssessment2=" + knowledgeAssessment2 + ", knowledgeAssessment3=" + knowledgeAssessment3 + ", knowledgeAssessment4=" + knowledgeAssessment4 + ", knowledgeAssessment5=" + knowledgeAssessment5 + ", knowledgeAssessment6=" + knowledgeAssessment6 + ", knowledgeAssessment7=" + knowledgeAssessment7 + ", riskAssessment1=" + riskAssessment1 + ", riskAssessment2=" + riskAssessment2 + ", riskAssessment3=" + riskAssessment3 + ", riskAssessment4=" + riskAssessment4 + ", riskAssessment5=" + riskAssessment5 + ", riskAssessment6=" + riskAssessment6 + ", tbScreening1=" + tbScreening1 + ", tbScreening2=" + tbScreening2 + ", tbScreening3=" + tbScreening3 + ", tbScreening4=" + tbScreening4 + ", stiScreening1=" + stiScreening1 + ", stiScreening2=" + stiScreening2 + ", stiScreening3=" + stiScreening3 + ", stiScreening4=" + stiScreening4 + ", stiScreening5=" + stiScreening5 + ", hivTestResult=" + hivTestResult + ", testedHiv=" + testedHiv + ", postTest1=" + postTest1 + ", postTest2=" + postTest2 + ", postTest3=" + postTest3 + ", postTest4=" + postTest4 + ", postTest5=" + postTest5 + ", postTest6=" + postTest6 + ", postTest7=" + postTest7 + ", postTest8=" + postTest8 + ", postTest9=" + postTest9 + ", postTest10=" + postTest10 + ", postTest11=" + postTest11 + ", postTest12=" + postTest12 + ", postTest13=" + postTest13 + ", postTest14=" + postTest14 + ", syphilisTestResult=" + syphilisTestResult + ", hepatitisbTestResult=" + hepatitisbTestResult + ", hepatitiscTestResult=" + hepatitiscTestResult + ", note=" + note + ", artReferred=" + artReferred + ", partnerNotification=" + partnerNotification + ", tbReferred=" + tbReferred + ", stiReferred=" + stiReferred + ", latitude=" + latitude + ", longitude=" + longitude + ", dateStarted=" + dateStarted + ", dateRegistration=" + dateRegistration + ", timeStamp=" + timeStamp + ", userId=" + userId + ", uploaded=" + uploaded + ", timeUploaded=" + timeUploaded + ", deviceconfigId=" + deviceconfigId + ", notificationCounseling=" + notificationCounseling + ", numberPartner=" + numberPartner + ", uuid=" + uuid + '}';
    }

}
