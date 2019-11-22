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
public class Indexcontact implements java.io.Serializable {

    private long indexcontactId;
    private Hts hts;
    private long htsId;
    private String clientCode;
    private String indexContactCode;
    private long facilityId;
    private String contactType;
    private String surname;
    private String otherNames;
    private Integer age;
    private String gender;
    private String address;
    private String phone;
    private String relationship;
    private String gbv;
    private Integer durationPartner;
    private String phoneTracking;
    private String homeTracking;
    private String outcome;
    private Date dateHivTest;
    private String hivStatus;
    private String linkCare;
    private String partnerNotification;
    private String modeNotification;
    private String serviceProvided;
    private Date timeStamp;
    private Long userId;
    private Integer uploaded;
    private Date timeUploaded;
    private Long deviceconfigId;

    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Indexcontact() {
    }

    public Indexcontact(long indexcontactId, Hts hts, long facilityId) {
        this.indexcontactId = indexcontactId;
        this.hts = hts;
        this.facilityId = facilityId;
    }

    public Indexcontact(long indexcontactId, Hts hts, long htsId, String clientCode, String indexContactCode, long facilityId, String contactType, String surname, String otherNames, Integer age, String gender, String address, String phone, String relationship, String gbv, Integer durationPartner, String phoneTracking, String homeTracking, String outcome, Date dateHivTest, String hivStatus, String linkCare, String partnerNotification, String modeNotification, String serviceProvided, Date timeStamp, Long userId, Integer uploaded, Date timeUploaded, Long deviceconfigId, String uuid) {
        this.indexcontactId = indexcontactId;
        this.hts = hts;
        this.htsId = htsId;
        this.clientCode = clientCode;
        this.indexContactCode = indexContactCode;
        this.facilityId = facilityId;
        this.contactType = contactType;
        this.surname = surname;
        this.otherNames = otherNames;
        this.age = age;
        this.gender = gender;
        this.address = address;
        this.phone = phone;
        this.relationship = relationship;
        this.gbv = gbv;
        this.durationPartner = durationPartner;
        this.phoneTracking = phoneTracking;
        this.homeTracking = homeTracking;
        this.outcome = outcome;
        this.dateHivTest = dateHivTest;
        this.hivStatus = hivStatus;
        this.linkCare = linkCare;
        this.partnerNotification = partnerNotification;
        this.modeNotification = modeNotification;
        this.serviceProvided = serviceProvided;
        this.timeStamp = timeStamp;
        this.userId = userId;
        this.uploaded = uploaded;
        this.timeUploaded = timeUploaded;
        this.deviceconfigId = deviceconfigId;
        this.uuid = uuid;
    }


    public long getIndexcontactId() {
        return indexcontactId;
    }

    public void setIndexcontactId(long indexcontactId) {
        this.indexcontactId = indexcontactId;
    }

    public long getHtsId() {
        return htsId;
    }

    public void setHtsId(long htsId) {
        this.htsId = htsId;
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public String getIndexContactCode() {
        return indexContactCode;
    }

    public void setIndexContactCode(String indexContactCode) {
        this.indexContactCode = indexContactCode;
    }

    public long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(long facilityId) {
        this.facilityId = facilityId;
    }

    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getGbv() {
        return gbv;
    }

    public void setGbv(String gbv) {
        this.gbv = gbv;
    }

    public Integer getDurationPartner() {
        return durationPartner;
    }

    public void setDurationPartner(Integer durationPartner) {
        this.durationPartner = durationPartner;
    }

    public String getPhoneTracking() {
        return phoneTracking;
    }

    public void setPhoneTracking(String phoneTracking) {
        this.phoneTracking = phoneTracking;
    }

    public String getHomeTracking() {
        return homeTracking;
    }

    public void setHomeTracking(String homeTracking) {
        this.homeTracking = homeTracking;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public Date getDateHivTest() {
        return dateHivTest;
    }

    public void setDateHivTest(Date dateHivTest) {
        this.dateHivTest = dateHivTest;
    }

    public String getHivStatus() {
        return hivStatus;
    }

    public void setHivStatus(String hivStatus) {
        this.hivStatus = hivStatus;
    }

    public String getLinkCare() {
        return linkCare;
    }

    public void setLinkCare(String linkCare) {
        this.linkCare = linkCare;
    }

    public String getPartnerNotification() {
        return partnerNotification;
    }

    public void setPartnerNotification(String partnerNotification) {
        this.partnerNotification = partnerNotification;
    }

    public String getModeNotification() {
        return modeNotification;
    }

    public void setModeNotification(String modeNotification) {
        this.modeNotification = modeNotification;
    }

    public String getServiceProvided() {
        return serviceProvided;
    }

    public void setServiceProvided(String serviceProvided) {
        this.serviceProvided = serviceProvided;
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

    public Long getDeviceconfigId() {
        return deviceconfigId;
    }

    public void setDeviceconfigId(Long deviceconfigId) {
        this.deviceconfigId = deviceconfigId;
    }

    public Hts getHts() {
        return hts;
    }

    public void setHts(Hts hts) {
        this.hts = hts;
    }

}
